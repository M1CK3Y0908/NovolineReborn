package cc.novoline.modules.combat;

import cc.novoline.Novoline;
import cc.novoline.events.EventTarget;
import cc.novoline.events.events.MotionUpdateEvent;
import cc.novoline.events.events.PacketEvent;
import cc.novoline.events.events.Render2DEvent;
import cc.novoline.events.events.Render3DEvent;
import cc.novoline.events.events.TickUpdateEvent;
import cc.novoline.gui.screen.setting.Manager;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.gui.screen.setting.SettingType;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.PlayerManager;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.*;
import cc.novoline.modules.move.Scaffold;
import cc.novoline.utils.BlockUtils;
import cc.novoline.utils.MoveUtils;
import cc.novoline.utils.PlayerUtils;
import cc.novoline.utils.RenderUtils;
import cc.novoline.utils.RotationUtils;
import cc.novoline.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static cc.novoline.gui.screen.setting.SettingType.COLOR_PICKER;
import static cc.novoline.modules.PlayerManager.EnumPlayerType.FRIEND;
import static cc.novoline.modules.PlayerManager.EnumPlayerType.TARGET;
import static cc.novoline.modules.configurations.property.object.PropertyFactory.*;

public final class KillAura extends AbstractModule {

    public enum AutoBlockMode {
        Off(false, false, false),
        Manual(false, false, false),
        Vanilla(false, false, false),
        Damage(false, false, false),
        Fake(false, false, false),
        Interact_A(true, true, true),
        Interact_B(true, true, true),
        Interact_C(true, true, true),
        Interact_D(true, true, true),
        Swap(false, true, true),
        Dual(true, true, false);

        public final boolean interacts;
        public final boolean packet;
        public final boolean lags;

        AutoBlockMode(boolean interacts, boolean packet, boolean lags) {
            this.interacts = interacts;
            this.packet = packet;
            this.lags = lags;
        }
    }

    /* fields */
    private Entity target;
    private Entity previousTarget;
    private final List<Entity> targetList = new CopyOnWriteArrayList<>();
    private final Timer attackTimer = new Timer();
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private float iyaw, ipitch, prevIYaw, prevIPitch;
    private float lastClientYaw, lastClientPitch;
    private float[] rotations;
    private float[] interactRotations;
    private Vec3 aimPoint;
    private final Map<Integer, Vec3> aimPoints = new HashMap<>();
    private final Map<UUID, Long> switchTimes = new HashMap<>();
    private final List<Packet<?>> heldPackets = Collections.synchronizedList(new ArrayList<>());

    private long attackDelay;
    private double currentAps;
    private boolean attackQueued;
    private int forcedAttacks;
    private int blockState = -1;
    private boolean fakeBlocking;
    private boolean serverBlocking;
    private boolean inSwingRange;
    private boolean attackedThisTick;
    private boolean attacking;
    private boolean interacting;
    private boolean releasing;
    private boolean returning;
    private boolean lagging;
    private int lagAccumulator;
    private double dualAccumulator;
    private int dualSwaps;
    private long damageBlockAt;
    private boolean passedRightClick;

    /* properties @off */
    @Property("mode")
    private final StringProperty mode = createString("Switch").acceptableValues("Switch", "Single", "Multi");
    @Property("sort")
    private final StringProperty sort = createString("Distance").acceptableValues("Distance", "Health", "Hurt_time", "Yaw");
    @Property("rotation")
    private final StringProperty rotationMode = createString("Silent").acceptableValues("Silent", "Lock_view", "None");
    @Property("autoblock")
    private final StringProperty autoBlockSetting = createString("Interact_A").acceptableValues(
            "Off", "Manual", "Vanilla", "Damage", "Fake", "Interact_A", "Interact_B", "Interact_C", "Interact_D", "Swap", "Dual"
    );
    @Property("aps")
    private final DoubleProperty aps = createDouble(12.0D).minimum(1.0D).maximum(20.0D);
    @Property("autoblock-aps")
    private final DoubleProperty autoBlockAps = createDouble(10.0D).minimum(1.0D).maximum(20.0D);
    @Property("autoblock-lag")
    private final IntProperty autoBlockLag = createInt(0).minimum(0).maximum(100);
    @Property("range")
    private final DoubleProperty range = createDouble(3.1D).minimum(1.0D).maximum(6.0D);
    @Property("swing-range")
    private final DoubleProperty swingRange = createDouble(3.3D).minimum(1.0D).maximum(8.0D);
    @Property("block-range")
    private final DoubleProperty blockRange = createDouble(3.5D).minimum(1.0D).maximum(8.0D);
    @Property("wall-range")
    private final DoubleProperty wallRange = createDouble(3.0D).minimum(1.0D).maximum(6.0D);
    @Property("fov")
    private final IntProperty fov = createInt(360).minimum(30).maximum(360);
    @Property("rotations-smoothness")
    private final DoubleProperty smoothness = createDouble(0.0D).minimum(0.0D).maximum(10.0D);
    @Property("switch-delay")
    private final FloatProperty switchDelay = createFloat(300.0F).minimum(0.0F).maximum(1000.0F);
    @Property("targets-limit")
    private final IntProperty targetsLimit = createInt(1).minimum(1).maximum(10);
    @Property("attack-on-damage")
    private final IntProperty attackOnDamage = createInt(0).minimum(0).maximum(5);

    @Property("targets")
    private final ListProperty<String> targets = createList("Players", "Fireballs").acceptableValues("Players", "Mobs", "Animals", "Invisibles", "Fireballs");
    @Property("filters")
    private final ListProperty<String> filters = createList("Teams").acceptableValues("Teams", "Armor");
    @Property("disable-in-inventory")
    private final BooleanProperty disableInInventory = booleanTrue();
    @Property("disable-while-blocking")
    private final BooleanProperty disableWhileBlocking = booleanFalse();
    @Property("disable-while-mining")
    private final BooleanProperty disableWhileMining = booleanFalse();
    @Property("disable-sprint-midair")
    private final BooleanProperty disableSprintMidair = booleanFalse();
    @Property("hit-through-blocks")
    private final BooleanProperty hitThroughBlocks = booleanFalse();
    @Property("ignore-teammates")
    private final BooleanProperty ignoreTeammates = booleanTrue();
    @Property("manual-block")
    private final BooleanProperty manualBlock = booleanFalse();
    @Property("prioritize-enemies")
    private final BooleanProperty prioritizeEnemies = booleanFalse();
    @Property("require-mouse-down")
    private final BooleanProperty requireMouseDown = booleanFalse();
    @Property("silent-swing")
    private final BooleanProperty silentSwing = booleanFalse();
    @Property("weapon-only")
    private final BooleanProperty weaponOnly = booleanFalse();

    @Property("target-hud")
    private final BooleanProperty targetHud = booleanTrue();
    @Property("visualize-range")
    private final BooleanProperty visRange = booleanFalse();
    @Property("color")
    private final ColorProperty color = createColor(0xFFFFFFFF);
    @Property("th-x")
    private final IntProperty thx = createInt((int) screenSize.getWidth() / 4).minimum(1).maximum((int) screenSize.getWidth() / 2);
    @Property("th-y")
    private final IntProperty thy = createInt((int) screenSize.getHeight() / 4).minimum(1).maximum((int) screenSize.getHeight() / 2);
    @Property("autodisable")
    private final ListProperty<String> autoDisable = createList("Death").acceptableValues("World Change", "Game End", "Death");
    /* properties @on */

    public KillAura(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Killaura", "Kill Aura", Keyboard.KEY_R, EnumModuleType.COMBAT, "Automatically attacks entities with RavenXD mechanics");
        Manager.put(new Setting("AURA_MODE", "Mode", SettingType.COMBOBOX, this, mode));
        Manager.put(new Setting("AURA_SORT", "Sort by", SettingType.COMBOBOX, this, sort));
        Manager.put(new Setting("AURA_ROTATION", "Rotation", SettingType.COMBOBOX, this, rotationMode));
        Manager.put(new Setting("AUTOBLOCK", "AutoBlock", SettingType.COMBOBOX, this, autoBlockSetting));
        Manager.put(new Setting("APS", "APS", SettingType.SLIDER, this, aps, 0.5D));
        Manager.put(new Setting("AB_APS", "AutoBlock APS", SettingType.SLIDER, this, autoBlockAps, 0.5D));
        Manager.put(new Setting("AB_LAG", "AutoBlock Lag", SettingType.SLIDER, this, autoBlockLag, 5));
        Manager.put(new Setting("RANGE", "Attack Range", SettingType.SLIDER, this, range, 0.1D));
        Manager.put(new Setting("SWING_RANGE", "Swing Range", SettingType.SLIDER, this, swingRange, 0.1D));
        Manager.put(new Setting("BLOCK_RANGE", "Block Range", SettingType.SLIDER, this, blockRange, 0.1D));
        Manager.put(new Setting("WALL_RANGE", "Wall Range", SettingType.SLIDER, this, wallRange, 0.1D));
        Manager.put(new Setting("AURA_FOV", "FOV", SettingType.SLIDER, this, fov, 5));
        Manager.put(new Setting("AURA_SMOOTH", "Smoothing", SettingType.SLIDER, this, smoothness, 0.5D));
        Manager.put(new Setting("SWITCH_DELAY", "Switch Delay", SettingType.SLIDER, this, switchDelay, 25.0F));
        Manager.put(new Setting("TARGETS_LIMIT", "Targets Limit", SettingType.SLIDER, this, targetsLimit, 1));
        Manager.put(new Setting("ATTACK_ON_DMG", "Attack On Damage", SettingType.SLIDER, this, attackOnDamage, 1));

        Manager.put(new Setting("TARGETS", "Targets", SettingType.SELECTBOX, this, targets));
        Manager.put(new Setting("KA_FILTER", "Filters", SettingType.SELECTBOX, this, filters));
        Manager.put(new Setting("DIS_IN_INV", "Disable In Inventory", SettingType.CHECKBOX, this, disableInInventory));
        Manager.put(new Setting("DIS_WHILE_BLOCKING", "Disable While Blocking", SettingType.CHECKBOX, this, disableWhileBlocking));
        Manager.put(new Setting("DIS_WHILE_MINING", "Disable While Mining", SettingType.CHECKBOX, this, disableWhileMining));
        Manager.put(new Setting("DIS_SPRINT_MIDAIR", "Disable Sprint Midair", SettingType.CHECKBOX, this, disableSprintMidair));
        Manager.put(new Setting("HIT_THROUGH_BLOCKS", "Hit Through Blocks", SettingType.CHECKBOX, this, hitThroughBlocks));
        Manager.put(new Setting("IGNORE_TEAMMATES", "Ignore Teammates", SettingType.CHECKBOX, this, ignoreTeammates));
        Manager.put(new Setting("MANUAL_BLOCK", "Manual Block", SettingType.CHECKBOX, this, manualBlock));
        Manager.put(new Setting("PRIORITIZE_ENEMIES", "Prioritize Enemies", SettingType.CHECKBOX, this, prioritizeEnemies));
        Manager.put(new Setting("REQ_MOUSE_DOWN", "Require Mouse Down", SettingType.CHECKBOX, this, requireMouseDown));
        Manager.put(new Setting("SILENT_SWING", "Silent Swing", SettingType.CHECKBOX, this, silentSwing));
        Manager.put(new Setting("WEAPON_ONLY", "Weapon Only", SettingType.CHECKBOX, this, weaponOnly));

        Manager.put(new Setting("TAR_HUD", "Target HUD", SettingType.CHECKBOX, this, targetHud));
        Manager.put(new Setting("KA_VIS_RANGE", "Visualize Range", SettingType.CHECKBOX, this, visRange));
        Manager.put(new Setting("KA_RANGE_COLOR", "Range Color", COLOR_PICKER, this, color, null, visRange::get));
        Manager.put(new Setting("KADISABLE", "Disable On", SettingType.SELECTBOX, this, autoDisable));
    }

    public AutoBlockMode getAutoBlockMode() {
        try {
            return AutoBlockMode.valueOf(autoBlockSetting.get());
        } catch (Exception e) {
            return AutoBlockMode.Off;
        }
    }

    @Override
    public void onEnable() {
        setSuffix(mode.get());
        iyaw = mc.player.rotationYaw;
        ipitch = mc.player.rotationPitch;
        prevIYaw = iyaw;
        prevIPitch = ipitch;
        lastClientYaw = iyaw;
        lastClientPitch = ipitch;
        target = null;
        previousTarget = null;
        targetList.clear();
        attackTimer.reset();
        setDelay(true);
        blockState = -1;
        fakeBlocking = false;
        serverBlocking = false;
        returning = false;
        rotations = null;
        interactRotations = null;
        heldPackets.clear();
    }

    @Override
    public void onDisable() {
        if (getAutoBlockMode() != AutoBlockMode.Manual && !isRightDown() && mc.player.isUsingItem()) {
            setBlocking(false, false);
        }
        if (blockState != -1) {
            releaseFlow(blockState);
            blockState = -1;
        }
        fakeBlocking = false;
        serverBlocking = false;
        returning = false;
        attackedThisTick = false;
        inSwingRange = false;
        attackQueued = false;
        target = null;
        previousTarget = null;
        targetList.clear();
        interactRotations = null;
        rotations = null;
        aimPoint = null;
        aimPoints.clear();
        switchTimes.clear();
        dualSwaps = 0;
        forcedAttacks = 0;
        releaseHeldPackets(true);
    }

    @EventTarget
    public void onMotion(MotionUpdateEvent event) {
        if (event.getState() == MotionUpdateEvent.State.PRE) {
            updatePreMotion(event);
        } else if (event.getState() == MotionUpdateEvent.State.POST) {
            updatePostMotion();
        }
    }

    private void updatePreMotion(MotionUpdateEvent event) {
        if (interactRotations != null) {
            applyRotations(interactRotations, event);
            interactRotations = null;
            return;
        }

        findTarget();
        updateAps();

        boolean canAttack = exists(target);
        if (disableInInventory.get() && mc.currentScreen != null) {
            canAttack = false;
        }

        if (target != null) {
            if (inSwingRange) {
                updateRotations();
                applyRotations(rotations, event);
                if (smoothness.get() > 0.0) {
                    lastClientYaw = mc.player.rotationYaw;
                    lastClientPitch = mc.player.rotationPitch;
                    returning = true;
                }
            }

            AutoBlockMode ab = getAutoBlockMode();
            if (ab.packet) {
                if (!blockAllowed() || !PlayerUtils.holdingSword()) {
                    if (blockState != -1) {
                        releaseFlow(blockState);
                        blockState = -1;
                        fakeBlocking = false;
                        serverBlocking = false;
                        canAttack = false;
                    }
                } else {
                    handlePacketAutoBlockPre(ab);
                }
            }
        } else if (returning) {
            returning = smoothReturn(event);
        }

        if (forcedAttacks > 0) {
            --forcedAttacks;
            attackQueued = true;
        }

        if (attackTimer.delay(attackDelay)) {
            attackTimer.reset();
            setDelay(false);
            if (target != null) {
                if (!(disableSprintMidair.get() && !mc.player.onGround && mc.player.isSprinting())) {
                    attackQueued = true;
                }
            }
        }

        if (attackQueued && canAttack) {
            attackQueued = false;
            executeAttack();
        }
    }

    private void updatePostMotion() {
        if (target != null) {
            if (inSwingRange && rotationMode.get().equalsIgnoreCase("Lock_view") && rotations != null) {
                mc.player.rotationYaw = rotations[0];
                mc.player.rotationPitch = rotations[1];
            }
            if (blockAllowed() && !getAutoBlockMode().packet) {
                AutoBlockMode ab = getAutoBlockMode();
                switch (ab) {
                    case Vanilla:
                        setBlocking(true, false);
                        break;
                    case Damage:
                        if (System.currentTimeMillis() - damageBlockAt < 800L) {
                            setBlocking(true, false);
                        } else if (!isRightDown()) {
                            setBlocking(false, false);
                        }
                        break;
                    case Fake:
                        fakeBlocking = true;
                        break;
                    default:
                        break;
                }
            }
        }
        attackedThisTick = false;
    }

    private void executeAttack() {
        Entity entity = target;
        boolean inReach = false;
        float[] currentRot = rotations != null ? rotations : new float[]{mc.player.rotationYaw, mc.player.rotationPitch};

        if (inSwingRange) {
            boolean sameRange = range.get().equals(swingRange.get());
            inReach = (sameRange && smoothness.get() == 0.0) || RotationUtils.rayHits(entity, currentRot[0], currentRot[1], range.get());
        }

        if (inSwingRange) {
            boolean attack = inReach;
            boolean swing = true;
            boolean silent = silentSwing.get();

            if (disableWhileBlocking.get() && PlayerUtils.holdingSword() && mc.player.isBlocking()) {
                swing = false;
                attack = false;
            }

            if (attack) {
                attackedThisTick = true;
                attacking = true;
                if (!silent) {
                    mc.player.swingItem();
                } else {
                    sendPacketNoEvent(new C0APacketAnimation());
                }
                mc.playerController.attackEntity(mc.player, entity);
                attacking = false;
            } else if (swing) {
                if (!silent) {
                    mc.player.swingItem();
                } else {
                    sendPacketNoEvent(new C0APacketAnimation());
                }
            }

            AutoBlockMode ab = getAutoBlockMode();
            if (blockAllowed() && blockState == 1 && ab.interacts && inReach) {
                MovingObjectPosition hit = RotationUtils.rayTraceEntity(entity, range.get() + 0.3, currentRot[0], currentRot[1]);
                if (hit != null && hit.hitVec != null) {
                    Vec3 vec3 = new Vec3(hit.hitVec.xCoord - entity.posX, hit.hitVec.yCoord - entity.posY, hit.hitVec.zCoord - entity.posZ);
                    sendPacketNoEvent(new C02PacketUseEntity(entity, vec3));
                    sendPacketNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.INTERACT));
                }
                if (PlayerUtils.holdingSword()) {
                    setBlocking(true, true);
                }
            }
        }
    }

    private void handlePacketAutoBlockPre(AutoBlockMode ab) {
        if (blockState == 0) {
            setBlocking(false, false);
        }
        switch (ab) {
            case Swap:
                switch (blockState) {
                    case 0:
                        setBlocking(true, true);
                        ++blockState;
                        break;
                    case -1:
                    case 1:
                        if (blockState == -1 || currentAps >= 10.0) {
                            attackQueued = true;
                        }
                        rollLag();
                        blockState = 0;
                        swapAway();
                        serverBlocking = false;
                        if (!lagging) fakeBlocking = false;
                        break;
                }
                break;
            case Dual:
                handleDualAutoBlock();
                break;
            case Interact_A:
            case Interact_B:
            case Interact_C:
            case Interact_D:
                switch (blockState) {
                    case 0:
                        if (currentAps >= 10.0) attackQueued = true;
                        if (attackQueued) {
                            ++blockState;
                            if ((ab == AutoBlockMode.Interact_B || ab == AutoBlockMode.Interact_C)) {
                                sendPacketNoEvent(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
                            }
                        }
                        break;
                    case -1:
                    case 1:
                        rollLag();
                        if (blockState == 1) {
                            if (ab == AutoBlockMode.Interact_B || ab == AutoBlockMode.Interact_C) {
                                sendPacketNoEvent(new C09PacketHeldItemChange(freeSlot()));
                                if (ab == AutoBlockMode.Interact_C) {
                                    setBlocking(false, true);
                                } else {
                                    serverBlocking = false;
                                }
                            } else {
                                if (ab == AutoBlockMode.Interact_D) swapAway();
                                setBlocking(false, true);
                            }
                        }
                        blockState = 0;
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void handleDualAutoBlock() {
        int best = -1;
        int second = -1;
        double bestDamage = -1.0;
        double secondDamage = -1.0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemSword) {
                double dmg = 4.0;
                if (dmg > bestDamage) {
                    secondDamage = bestDamage;
                    second = best;
                    bestDamage = dmg;
                    best = i;
                } else if (dmg > secondDamage) {
                    secondDamage = dmg;
                    second = i;
                }
            }
        }
        if (second == -1) {
            blockState = -1;
            return;
        }
        if (attackQueued) {
            blockState = 1;
            int slot = (dualSwaps++ & 1) == 0 ? best : second;
            sendPacketNoEvent(new C09PacketHeldItemChange(slot));
        } else {
            blockState = 0;
        }
    }

    private void swapAway() {
        sendPacketNoEvent(new C09PacketHeldItemChange(freeSlot()));
        sendPacketNoEvent(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
    }

    private int freeSlot() {
        int current = mc.player.inventory.currentItem;
        for (int i = 0; i < 9; ++i) {
            if (i != current && mc.player.inventory.getStackInSlot(i) == null) {
                return i;
            }
        }
        for (int i = 0; i < 9; ++i) {
            if (i != current) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack != null && !(stack.getItem() instanceof ItemSword)) {
                    return i;
                }
            }
        }
        return (current + 1) % 9;
    }

    private void rollLag() {
        if ((lagAccumulator += autoBlockLag.get()) >= 100) {
            lagAccumulator -= 100;
            lagging = true;
        } else {
            lagging = false;
        }
    }

    public void setBlocking(boolean block, boolean packet) {
        if (packet) {
            if (block) {
                sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.player.getHeldItem()));
            } else {
                releasing = true;
                sendPacketNoEvent(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                releasing = false;
            }
            serverBlocking = fakeBlocking = block;
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), block);
            fakeBlocking = block;
        }
    }

    private void releaseFlow(int state) {
        AutoBlockMode ab = getAutoBlockMode();
        if (ab.packet) {
            setBlocking(false, true);
        }
        fakeBlocking = false;
        serverBlocking = false;
    }

    private void releaseHeldPackets(boolean all) {
        if (heldPackets.isEmpty()) return;
        synchronized (heldPackets) {
            for (Packet<?> p : heldPackets) {
                sendPacketNoEvent(p);
            }
            heldPackets.clear();
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getState() == PacketEvent.State.INCOMING) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity p = (S12PacketEntityVelocity) event.getPacket();
                if (p.getEntityID() == mc.player.getEntityID()) {
                    damageBlockAt = System.currentTimeMillis();
                    if (attackOnDamage.get() > 0) {
                        forcedAttacks = attackOnDamage.get();
                    }
                }
            }
        } else if (event.getState() == PacketEvent.State.OUTGOING) {
            Packet<?> p = event.getPacket();
            AutoBlockMode ab = getAutoBlockMode();
            if (ab.lags && lagging && !heldPackets.isEmpty()) {
                if (!(p instanceof C0APacketAnimation)) {
                    heldPackets.add(p);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        if (lagging && !heldPackets.isEmpty()) {
            releaseHeldPackets(true);
            lagging = false;
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (targetHud.get()) {
            if (shouldAttack() || (target != null && target.isEntityAlive())) {
                if (target instanceof EntityPlayer && !(mc.currentScreen instanceof GuiChat)) {
                    RenderUtils.renderTHUD(this, (EntityPlayer) target);
                }
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (visRange.get()) {
            RenderUtils.pre3D();
            GL11.glLineWidth(6);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glColor4f(0, 0, 0, 0.4F);
            for (double d = 0; d < Math.PI * 2; d += Math.PI * 2 / 51) {
                double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() + Math.sin(d) * range.get() - mc.getRenderManager().renderPosX;
                double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY;
                double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() + Math.cos(d) * range.get() - mc.getRenderManager().renderPosZ;
                GL11.glVertex3d(x, y, z);
            }
            GL11.glEnd();
            GL11.glLineWidth(3);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glColor4f(color.getAwtColor().getRed() / 255F, color.getAwtColor().getGreen() / 255F, color.getAwtColor().getBlue() / 255F, 1);
            for (double d = 0; d < Math.PI * 2; d += Math.PI * 2 / 51) {
                double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() + Math.sin(d) * range.get() - mc.getRenderManager().renderPosX;
                double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY;
                double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() + Math.cos(d) * range.get() - mc.getRenderManager().renderPosZ;
                GL11.glVertex3d(x, y, z);
            }
            GL11.glEnd();
            RenderUtils.post3D();
        }
    }

    private void findTarget() {
        if (disableInInventory.get() && mc.currentScreen != null) {
            setTarget(null);
            return;
        }
        if (weaponOnly.get() && !PlayerUtils.holdingWeapon()) {
            setTarget(null);
            return;
        }
        if (requireMouseDown.get() && (!Mouse.isButtonDown(0) || mc.currentScreen instanceof GuiChat)) {
            setTarget(null);
            return;
        }
        if (disableWhileMining.get() && Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            setTarget(null);
            return;
        }

        double maxReach = Math.max(Math.max(range.get(), swingRange.get()), blockRange.get());
        double scanRangeSq = (maxReach + 1.2) * (maxReach + 1.2);

        List<EntityLivingBase> candidates = new ArrayList<>();
        Entity fireball = null;
        double nearestFireball = -1.0;

        for (Entity e : mc.world.getLoadedEntityList()) {
            if (e == mc.player) continue;

            if (targets.contains("Fireballs") && e instanceof EntityLargeFireball) {
                double d = mc.player.getDistanceSqToEntity(e);
                if (d <= scanRangeSq && PlayerUtils.inFov((float) fov.get(), e) && RotationUtils.rayHits(e, swingRange.get())) {
                    if (nearestFireball == -1.0 || d < nearestFireball) {
                        nearestFireball = d;
                        fireball = e;
                    }
                }
                continue;
            }

            if (!(e instanceof EntityLivingBase)) continue;
            EntityLivingBase living = (EntityLivingBase) e;

            if (!isValidEntity(living)) continue;

            double d = mc.player.getDistanceSqToEntity(living);
            if (d > scanRangeSq) continue;

            if (fov.get() < 360 && !PlayerUtils.inFov((float) fov.get(), living)) continue;

            if (!hitThroughBlocks.get()) {
                Vec3 point = RotationUtils.visiblePoint(living);
                if (point == null) continue;
                aimPoints.put(living.getEntityId(), point);
            }

            candidates.add(living);
        }

        if (candidates.isEmpty()) {
            if (fireball != null) {
                inSwingRange = true;
                setTarget(fireball);
            } else {
                setTarget(null);
            }
            targetList.clear();
            return;
        }

        Comparator<EntityLivingBase> comparator = null;
        switch (sort.get()) {
            case "Health":
                comparator = Comparator.comparingDouble(EntityLivingBase::getHealth);
                break;
            case "Hurt_time":
                comparator = Comparator.comparingDouble(ent -> ent.hurtTime);
                break;
            case "Yaw":
                comparator = Comparator.comparingDouble(ent -> RotationUtils.absYawDifference(ent, mc.player.rotationYaw));
                break;
            case "Distance":
            default:
                comparator = Comparator.comparingDouble(ent -> mc.player.getDistanceSqToEntity(ent));
                break;
        }
        candidates.sort(comparator);

        targetList.clear();
        targetList.addAll(candidates);

        long delay = switchDelay.get() < 100.0F ? 100L : switchDelay.get().longValue();
        long now = System.currentTimeMillis();
        long cycle = (long) Math.min((double) candidates.size(), targetsLimit.get()) * delay;
        Entity chosen = null;

        for (EntityLivingBase e : candidates) {
            UUID uuid = e.getUniqueID();
            Long l = switchTimes.get(uuid);
            if (l == null) continue;
            if (l > 0L) {
                if (Math.abs(now - l) < delay) {
                    chosen = e;
                    break;
                }
                if (cycle != delay) {
                    switchTimes.put(uuid, -l);
                    continue;
                }
                switchTimes.remove(uuid);
                continue;
            }
            if (Math.abs(now - -l) < cycle) continue;
            switchTimes.remove(uuid);
        }

        if (chosen == null) {
            for (EntityLivingBase e : candidates) {
                if (switchTimes.get(e.getUniqueID()) != null) continue;
                switchTimes.put(e.getUniqueID(), now);
                chosen = e;
                break;
            }
            if (chosen == null && exists(previousTarget)) {
                chosen = previousTarget;
            }
        }

        if (chosen == null && !candidates.isEmpty()) {
            chosen = candidates.get(0);
        }

        inSwingRange = true;
        setTarget(chosen);
    }

    private void updateRotations() {
        float lastYaw = iyaw;
        float lastPitch = ipitch;
        rotations = aim();
        if (smoothness.get() > 0.0) {
            smooth(rotations, lastYaw, lastPitch);
        }
        rotations = RotationUtils.fixSensitivity(rotations[0], rotations[1], lastYaw, lastPitch);
    }

    private void applyRotations(float[] rot, MotionUpdateEvent event) {
        if (rot == null) return;
        prevIYaw = iyaw;
        prevIPitch = ipitch;
        iyaw = rot[0];
        ipitch = rot[1];

        if (rotationMode.get().equalsIgnoreCase("Silent")) {
            event.setYaw(rot[0]);
            event.setPitch(rot[1]);
        } else if (rotationMode.get().equalsIgnoreCase("Lock_view")) {
            mc.player.rotationYaw = rot[0];
            mc.player.rotationPitch = rot[1];
            event.setYaw(rot[0]);
            event.setPitch(rot[1]);
        }
    }

    private boolean smoothReturn(MotionUpdateEvent event) {
        if (smoothness.get() == 0.0) return false;
        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;
        float dYaw = Math.abs(MathHelper.wrapAngleTo180_float(yaw - lastClientYaw));
        float dPitch = Math.abs(pitch - lastClientPitch);
        lastClientYaw = yaw;
        lastClientPitch = pitch;
        if (dYaw > 20.0f || dPitch > 8.0f) return false;

        float[] rot = new float[]{yaw, pitch};
        smooth(rot, iyaw, ipitch);
        rot = RotationUtils.fixSensitivity(rot[0], rot[1], iyaw, ipitch);
        applyRotations(rot, event);

        return Math.abs(MathHelper.wrapAngleTo180_float(rot[0] - mc.player.rotationYaw)) > 3.0f
                || Math.abs(rot[1] - mc.player.rotationPitch) > 3.0f;
    }

    private void smooth(float[] rot, float lastYaw, float lastPitch) {
        float maxYaw = 7.0f;
        float maxPitch = 3.0f;
        float keep = (float) (1.0 - Math.min(9.9, smoothness.get()) / 10.0);
        float dYaw = MathHelper.wrapAngleTo180_float(rot[0] - lastYaw);
        float aYaw = Math.abs(dYaw);
        float dPitch = rot[1] - lastPitch;
        float aPitch = Math.abs(dPitch);
        if (aYaw > maxYaw) {
            dYaw = Math.signum(dYaw) * ((aYaw - maxYaw) * keep + maxYaw);
        }
        if (aPitch > maxPitch) {
            dPitch = Math.signum(dPitch) * ((aPitch - maxPitch) * keep + maxPitch);
        }
        rot[0] = lastYaw + dYaw;
        rot[1] = lastPitch + dPitch;
    }

    private float[] aim() {
        if (aimPoint != null) {
            return RotationUtils.toVec(aimPoint);
        }
        return RotationUtils.toEntityBox(target);
    }

    private void updateAps() {
        double d = blockAllowed() ? autoBlockAps.get() : aps.get();
        if (target != null && getAutoBlockMode() == AutoBlockMode.Dual) {
            if (blockState == -1) {
                dualAccumulator = 20.0 - d;
            }
            if ((dualAccumulator += d) >= 20.0) {
                dualAccumulator -= 20.0;
                attackQueued = true;
            }
        }
        if (currentAps != d) {
            setDelay(true);
            currentAps = d;
        }
    }

    private void setDelay(boolean now) {
        if (now) {
            attackDelay = 0L;
        } else {
            double a = currentAps > 0.0 ? currentAps : aps.get();
            attackDelay = (long) (1000.0 / a + (double) (int) ((Math.random() - 0.5) * 8.0));
        }
    }

    public boolean blockAllowed() {
        return !manualBlock.get() || isRightDown();
    }

    private boolean isRightDown() {
        return Mouse.isButtonDown(1);
    }

    private boolean exists(Entity t) {
        return t != null && t.isEntityAlive() && mc.world.getEntityByID(t.getEntityID()) != null;
    }

    public boolean isValidEntity(@NonNull Entity entity) {
        if (entity == mc.player || !entity.isEntityAlive() || (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).deathTime != 0)) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (!targets.contains("Players")) {
                return false;
            }
            EntityPlayer player = (EntityPlayer) entity;
            if (novoline.getPlayerManager().hasType(player.getName(), FRIEND)) {
                return false;
            }
            if (ignoreTeammates.get() && filters.contains("Teams") && PlayerUtils.inTeamWithMinecraftPlayer(player)) {
                return false;
            }
            if (filters.contains("Armor") && !hasArmor(player)) {
                return false;
            }
            if (entity.isInvisible() && !targets.contains("Invisibles") && PlayerUtils.isInvisibleUnarmoured((EntityLivingBase) entity)) {
                return false;
            }
            return true;
        }
        if (entity instanceof EntityMob || entity instanceof EntitySlime) {
            return targets.contains("Mobs");
        }
        if (entity instanceof EntityAnimal || entity instanceof EntityVillager) {
            return targets.contains("Animals");
        }
        return false;
    }

    private boolean hasArmor(@NonNull EntityPlayer e) {
        for (int i = 0; i <= 3; ++i) {
            if (e.inventory.armorInventory[i] != null) return true;
        }
        return false;
    }

    //region Compatibility Getters
    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity entity) {
        if (entity == null) {
            switchTimes.clear();
            if (target != null && getAutoBlockMode().packet) {
                releaseFlow(blockState);
            }
            fakeBlocking = false;
            serverBlocking = false;
            inSwingRange = false;
            blockState = -1;
            aimPoint = null;
            aimPoints.clear();
        } else if (!hitThroughBlocks.get()) {
            aimPoint = aimPoints.get(entity.getEntityID());
        }
        previousTarget = target;
        target = entity;
    }

    public boolean shouldAttack() {
        return target != null && target.isEntityAlive() && !isEnabled(Scaffold.class);
    }

    public boolean shouldBlock() {
        return (getAutoBlockMode() != AutoBlockMode.Off) && target != null && PlayerUtils.holdingSword() && isEnabled() && !isEnabled(Scaffold.class);
    }

    public boolean isFakeBlocking() {
        return fakeBlocking;
    }

    public boolean isServerBlocking() {
        return serverBlocking;
    }

    public DoubleProperty getRange() {
        return range;
    }

    public DoubleProperty getWallRange() {
        return wallRange;
    }

    public DoubleProperty getBlockRange() {
        return blockRange;
    }

    public DoubleProperty getAps() {
        return aps;
    }

    public FloatProperty getSwitchDelay() {
        return switchDelay;
    }

    public StringProperty getMode() {
        return mode;
    }

    public String mode() {
        return mode.get();
    }

    public StringProperty getSort() {
        return sort;
    }

    public IntProperty getFovCheck() {
        return fov;
    }

    public ListProperty<String> getTargets() {
        return targets;
    }

    public ListProperty<String> getFilters() {
        return filters;
    }

    public BooleanProperty getAutoBlock() {
        return PropertyFactory.createBoolean(getAutoBlockMode() != AutoBlockMode.Off);
    }

    public BooleanProperty getTargetHud() {
        return targetHud;
    }

    public ListProperty<String> getAutoDisable() {
        return autoDisable;
    }

    public IntProperty getThx() {
        return thx;
    }

    public IntProperty getThy() {
        return thy;
    }

    public float getYaw() {
        return iyaw;
    }

    public float getPitch() {
        return ipitch;
    }

    public float getIYaw() {
        return iyaw;
    }

    public float getIPitch() {
        return ipitch;
    }

    public float getPrevIYaw() {
        return prevIYaw;
    }

    public float getPrevIPitch() {
        return prevIPitch;
    }

    public List<Entity> getTargetList() {
        return targetList;
    }
    //endregion
}
