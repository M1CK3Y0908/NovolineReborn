package cc.novoline.modules.move;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.MotionUpdateEvent;
import cc.novoline.events.events.Render2DEvent;
import cc.novoline.events.events.Render3DEvent;
import cc.novoline.gui.screen.setting.Manager;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.gui.screen.setting.SettingType;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.*;
import cc.novoline.utils.BlockUtils;
import cc.novoline.utils.MoveUtils;
import cc.novoline.utils.PlayerUtils;
import cc.novoline.utils.RenderUtils;
import cc.novoline.utils.RotationUtils;
import cc.novoline.utils.ScaleUtils;
import cc.novoline.utils.fonts.impl.Fonts;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.*;

import static cc.novoline.modules.configurations.property.object.PropertyFactory.*;

public final class Scaffold extends AbstractModule {

    public enum RotationMode {
        None, Strict, Smooth, Simple, Lazy, Offset;

        public float side;
        public float sideJitter;
        public float groundSide;

        public void onDisable() {
            this.groundSide = 0.0f;
            this.sideJitter = 0.0f;
            this.side = 0.0f;
        }
    }

    public enum FastScaffoldMode {
        Disabled(false, false, false),
        Vanilla(true, false, false),
        Constant_A(true, false, false),
        Constant_B(false, false, false),
        Float_A(true, false, false),
        Float_B(true, false, false),
        Jump_A(false, true, false),
        Jump_B(false, true, false),
        Jump_C(true, true, false),
        Jump_D(true, true, false),
        Keep_Y_A(false, false, true),
        Keep_Y_B(false, false, true),
        Side(true, false, false);

        public final boolean sprint;
        public final boolean jump;
        public final boolean keepY;
        public int state;
        public int ticks;
        public double height;

        FastScaffoldMode(boolean sprint, boolean jump, boolean keepY) {
            this.sprint = sprint;
            this.jump = jump;
            this.keepY = keepY;
        }

        public boolean canFastScaffold(Scaffold scaffold) {
            if (!scaffold.isEnabled() || this == Disabled) {
                return false;
            }
            if (MoveUtils.jumpKeyDown()) {
                return false;
            }
            if (this != Float_A) {
                if (!MoveUtils.isMoving() || scaffold.mc.player.isSneaking()) {
                    return false;
                }
                if (this.sprint && !this.jump && !MoveUtils.direction().forward) {
                    return false;
                }
            }
            if (this == Side) {
                return !MoveUtils.isDiagonal();
            }
            return true;
        }

        public void onEnable(Scaffold scaffold) {
            scaffold.enableTick = scaffold.mc.player.ticksExisted;
            this.state = 0;
            if (this == Float_A) {
                if (scaffold.mc.player.onGround) {
                    this.ticks = 1;
                    scaffold.delay = 1;
                    if (scaffold.mc.player.motionY == -0.0784000015258789) {
                        scaffold.mc.player.jump();
                    }
                    scaffold.keptY = (int) Math.floor(scaffold.mc.player.posY) - 1;
                }
            } else if (this == Jump_B || this == Jump_C) {
                this.height = 2.2;
            } else if (this == Jump_D) {
                this.height = 2.5;
            } else if (this == Side) {
                this.ticks = 4;
            }
        }

        public void onDisable(Scaffold scaffold, boolean disabling) {
            if (this.jump || this.keepY) {
                scaffold.keptY = -1;
            }
            if (this == Float_A) {
                if (scaffold.disablingModule) {
                    if (scaffold.mc.player.onGround && MoveUtils.isAirBelow()) {
                        scaffold.mc.player.jump();
                    }
                    if (scaffold.getRotationMode() != RotationMode.Offset) {
                        MoveUtils.multiplyMotion(0.58);
                    }
                }
            } else if (this == Float_B && scaffold.disablingModule) {
                MoveUtils.multiplyMotion(0.85);
            }
            this.ticks = 0;
        }
    }

    public enum MultiPlaceMode {
        Disabled, _1_extra, _2_extra
    }

    /* properties @off */
    @Property("motion")
    private final DoubleProperty motion = createDouble(1.0D).minimum(0.5D).maximum(1.2D);
    @Property("rotation")
    private final StringProperty rotationSetting = createString("Strict").acceptableValues("Strict", "Smooth", "Simple", "Lazy", "Offset", "None");
    @Property("fast-scaffold")
    private final StringProperty fastScaffoldSetting = createString("Disabled").acceptableValues(
            "Disabled", "Constant_A", "Constant_B", "Jump_A", "Jump_B", "Jump_C", "Jump_D", "Keep_Y_A", "Keep_Y_B", "Float_A", "Float_B", "Side", "Vanilla"
    );
    @Property("fast-scaffold-rmb")
    private final StringProperty fastScaffoldRmbSetting = createString("Disabled").acceptableValues(
            "Disabled", "Constant_A", "Constant_B", "Jump_A", "Jump_B", "Jump_C", "Jump_D", "Keep_Y_A", "Keep_Y_B", "Float_A", "Float_B", "Side", "Vanilla"
    );
    @Property("fast-scaffold-motion")
    private final DoubleProperty fastScaffoldMotion = createDouble(1.0D).minimum(0.5D).maximum(1.2D);
    @Property("multi-place")
    private final StringProperty multiPlaceSetting = createString("Disabled").acceptableValues("Disabled", "1_extra", "2_extra");
    @Property("precision")
    private final StringProperty precisionSetting = createString("High").acceptableValues("Low", "Medium", "High", "Very_high");
    @Property("sneak-every")
    private final IntProperty sneakEvery = createInt(0).minimum(0).maximum(10);
    @Property("speed-boost")
    private final DoubleProperty speedBoost = createDouble(0.0D).minimum(0.0D).maximum(10.0D);

    @Property("auto-swap")
    private final BooleanProperty autoSwap = booleanTrue();
    @Property("cancel-knockback")
    private final BooleanProperty cancelKnockback = booleanFalse();
    @Property("highlight-blocks")
    private final BooleanProperty highlightBlocks = booleanTrue();
    @Property("jump-facing-forward")
    private final BooleanProperty jumpFacingForward = booleanFalse();
    @Property("legit-motion")
    private final BooleanProperty legitMotion = booleanFalse();
    @Property("safe-walk")
    private final BooleanProperty safeWalk = booleanTrue();
    @Property("show-block-count")
    private final BooleanProperty showBlockCount = booleanTrue();
    @Property("silent-swing")
    private final BooleanProperty silentSwing = booleanFalse();
    @Property("slow-on-enable")
    private final BooleanProperty slowOnEnable = booleanFalse();
    @Property("render-rotations")
    private final BooleanProperty renderRotations = booleanTrue();

    // compatibility
    private final ListProperty<String> addons = createList("Safe Walk", "Tower");
    private final List<Block> blacklistedBlocks = Arrays.asList(
            Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.skull, Blocks.stained_glass_pane,
            Blocks.iron_bars, Blocks.snow_layer, Blocks.chest, Blocks.trapped_chest, Blocks.ender_chest,
            Blocks.torch, Blocks.anvil, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
            Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
            Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook,
            Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.yellow_flower, Blocks.red_mushroom, Blocks.brown_mushroom,
            Blocks.vine, Blocks.trapdoor, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.gravel, Blocks.cactus,
            Blocks.dispenser, Blocks.dropper, Blocks.crafting_table, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall,
            Blocks.oak_fence, Blocks.web, Blocks.standing_banner, Blocks.wall_banner, Blocks.deadbush
    );
    /* properties @on */

    /* fields */
    public int keptY = -1;
    public int enableTick;
    public int startTick;
    public int delay;
    public int legitTicks;
    public int jumpState;
    public boolean disablingModule;

    private int searchMode;
    private int jumpTick;
    private int airBelowTicks;
    private int placedCount;
    private boolean sneakQueued;
    private boolean smoothSnap;
    private boolean fastEnabled;
    private boolean rightHeld;
    private BlockPos lastPlaced;
    private float aimYaw;
    private float aimPitch = 100.0f;
    private float smoothLimit;
    private float legitYaw;
    private float restPitch;
    private double yawOffset;
    private long placedAt;
    private long startedAt;
    private long stoppedAt;
    private final Map<BlockPos, Long> placed = new HashMap<>();
    private String countText;
    private int oldSlot = -1;

    private static final EnumFacing[] SIDES = {EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST};
    private static final BlockPos.MutableBlockPos SCRATCH = new BlockPos.MutableBlockPos();

    public Scaffold(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Scaffold", "Scaffold", Keyboard.KEY_NONE, EnumModuleType.MOVEMENT, "Places blocks beneath player with RavenXD mechanics");
        Manager.put(new Setting("SF_MOTION", "Motion", SettingType.SLIDER, this, motion, 0.05D));
        Manager.put(new Setting("SF_ROTATION", "Rotation", SettingType.COMBOBOX, this, rotationSetting));
        Manager.put(new Setting("SF_FAST", "Fast Scaffold", SettingType.COMBOBOX, this, fastScaffoldSetting));
        Manager.put(new Setting("SF_FAST_RMB", "Fast Scaffold RMB", SettingType.COMBOBOX, this, fastScaffoldRmbSetting));
        Manager.put(new Setting("SF_FAST_MOT", "Fast Scaffold Motion", SettingType.SLIDER, this, fastScaffoldMotion, 0.05D));
        Manager.put(new Setting("SF_MULTI", "Multi Place", SettingType.COMBOBOX, this, multiPlaceSetting));
        Manager.put(new Setting("SF_PRECISION", "Precision", SettingType.COMBOBOX, this, precisionSetting));
        Manager.put(new Setting("SF_SNEAK", "Sneak Every", SettingType.SLIDER, this, sneakEvery, 1));
        Manager.put(new Setting("SF_SPEED_BOOST", "Speed Boost", SettingType.SLIDER, this, speedBoost, 0.5D));

        Manager.put(new Setting("SF_AUTO_SWAP", "Auto Swap", SettingType.CHECKBOX, this, autoSwap));
        Manager.put(new Setting("SF_CANCEL_KB", "Cancel Knockback", SettingType.CHECKBOX, this, cancelKnockback));
        Manager.put(new Setting("SF_HIGHLIGHT", "Highlight Blocks", SettingType.CHECKBOX, this, highlightBlocks));
        Manager.put(new Setting("SF_JUMP_FWD", "Jump Facing Forward", SettingType.CHECKBOX, this, jumpFacingForward));
        Manager.put(new Setting("SF_LEGIT_MOT", "Legit Motion", SettingType.CHECKBOX, this, legitMotion));
        Manager.put(new Setting("SF_SAFEWALK", "Safe Walk", SettingType.CHECKBOX, this, safeWalk));
        Manager.put(new Setting("SF_SHOW_COUNT", "Show Block Count", SettingType.CHECKBOX, this, showBlockCount));
        Manager.put(new Setting("SF_SILENT_SWING", "Silent Swing", SettingType.CHECKBOX, this, silentSwing));
        Manager.put(new Setting("SF_SLOW_ENABLE", "Slow On Enable", SettingType.CHECKBOX, this, slowOnEnable));
        Manager.put(new Setting("SF_RENDER_ROTS", "Render Rotations", SettingType.CHECKBOX, this, renderRotations));
    }

    public RotationMode getRotationMode() {
        try {
            return RotationMode.valueOf(rotationSetting.get());
        } catch (Exception e) {
            return RotationMode.Strict;
        }
    }

    public FastScaffoldMode getFastScaffoldMode() {
        try {
            String val = rightHeld ? fastScaffoldRmbSetting.get() : fastScaffoldSetting.get();
            return FastScaffoldMode.valueOf(val);
        } catch (Exception e) {
            return FastScaffoldMode.Disabled;
        }
    }

    public MultiPlaceMode getMultiPlaceMode() {
        try {
            return MultiPlaceMode.valueOf(multiPlaceSetting.get());
        } catch (Exception e) {
            return MultiPlaceMode.Disabled;
        }
    }

    public RotationUtils.Precision getPrecision() {
        try {
            return RotationUtils.Precision.valueOf(precisionSetting.get());
        } catch (Exception e) {
            return RotationUtils.Precision.High;
        }
    }

    @Override
    public void onEnable() {
        setSuffix(fastScaffoldSetting.get().equalsIgnoreCase("Disabled") ? rotationSetting.get() : fastScaffoldSetting.get());
        restPitch = 84.5f + (float) (Math.random() * 3.0);
        yawOffset = RotationUtils.randomOffset();
        oldSlot = mc.player.inventory.currentItem;
        startTick = mc.player.ticksExisted;
        startedAt = System.currentTimeMillis();

        if (slowOnEnable.get()) {
            MoveUtils.multiplyMotion(0.2);
        }

        if (!mc.player.onGround) {
            delay = 3;
        } else if (PlayerUtils.holdingSword() && MoveUtils.nearEdge(0.2) && BlockUtils.countBlocks() > 0) {
            jumpState = 2;
            jumpTick = mc.player.ticksExisted;
            delay = 4;
        } else {
            delay = 1;
        }
    }

    @Override
    public void onDisable() {
        disablingModule = true;
        stoppedAt = System.currentTimeMillis();
        getRotationMode().onDisable();
        if (fastEnabled) {
            getFastScaffoldMode().onDisable(this, true);
            fastEnabled = false;
        }
        sneakQueued = false;
        smoothSnap = false;
        rightHeld = false;
        jumpState = 1;
        keptY = -1;
        lastPlaced = null;
        aimPitch = 100.0f;
        legitTicks = 0;
        delay = 0;
        airBelowTicks = 0;
        placedCount = 0;
        placedAt = 0L;

        if (oldSlot != -1 && autoSwap.get()) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        double d = MoveUtils.speed();
        if (d > 0.34) {
            MoveUtils.multiplyMotion((0.29 + 0.02 * Math.random()) / d);
        }
        disablingModule = false;
    }

    @EventTarget
    public void onMotion(MotionUpdateEvent event) {
        if (event.getState() == MotionUpdateEvent.State.PRE) {
            updatePreMotion(event);
        } else if (event.getState() == MotionUpdateEvent.State.POST) {
            updatePostPlayerInput();
        }
    }

    private void updatePreMotion(MotionUpdateEvent event) {
        rightHeld = Mouse.isButtonDown(1) && mc.currentScreen == null;
        FastScaffoldMode fast = getFastScaffoldMode();

        // float preMotion logic
        boolean floatWasSet = FastScaffoldMode.Float_A.state == 1 || FastScaffoldMode.Float_B.state == 1;
        FastScaffoldMode.Float_A.state = 0;
        FastScaffoldMode.Float_B.state = 0;

        if (fast.canFastScaffold(this)) {
            if (fast == FastScaffoldMode.Float_A) {
                if (event.getY() % 1.0 == 0.0 && mc.player.motionY == -0.0784000015258789) {
                    event.setY(event.getY() + 1.0E-13);
                    FastScaffoldMode.Float_A.state = 1;
                    if (MoveUtils.isMoving() && !mc.player.isSneaking()) {
                        double d = fast.sprint ? 0.2 + MoveUtils.perSpeedLevel(MoveUtils.isDiagonal() ? 0.035 : 0.043)
                                : 0.23 + MoveUtils.perSpeedLevel(MoveUtils.isDiagonal() ? 0.042 : 0.05);
                        if (MoveUtils.speedLevel() == 0 && MoveUtils.isDiagonal()) d *= 0.87;
                        MoveUtils.strafe(d);
                        if (!fast.sprint) mc.player.setSprinting(true);
                    }
                }
            } else if (fast == FastScaffoldMode.Float_B) {
                if (event.getY() % 1.0 == 0.0 && mc.player.motionY == -0.0784000015258789 && MoveUtils.isMoving() && !mc.player.isSneaking()) {
                    double d;
                    if (!floatWasSet && (MoveUtils.isDiagonal() || !MoveUtils.isAirBelow())) {
                        event.setY(event.getY() + 0.001);
                        event.setOnGround(false);
                        FastScaffoldMode.Float_B.state = 1;
                        d = 0.153;
                    } else {
                        d = 0.245 + MoveUtils.perSpeedLevel(0.025);
                    }
                    MoveUtils.strafe(d);
                }
            } else if ((fast.jump || fast.keepY) && mc.player.onGround && (jumpFacingForward.get() || floatWasSet)) {
                if (fast.state == 1) {
                    fast.state = 2;
                    double d = fast.sprint && MoveUtils.direction().forward ? 0.25 : (fast.keepY ? 0.54 : 0.4);
                    jumpWith(d);
                }
                delay = legitTicks;
            }
        }

        // update placement
        updatePlacement();

        // calculate rotations
        float[] rot = calculateRotations(event.getYaw(), event.getPitch());
        if (rot != null) {
            event.setYaw(rot[0]);
            event.setPitch(rot[1]);
            aimYaw = rot[0];
            aimPitch = rot[1];
        }
    }

    private boolean isSupported() {
        if (!mc.player.onGround) return false;
        BlockPos below = BlockUtils.below(1.0);
        return !BlockUtils.isReplaceable(below) && !MoveUtils.nearEdge(0.15);
    }

    private void updatePlacement() {
        if (mc.currentScreen != null) {
            return;
        }

        if (autoSwap.get()) {
            swapToBlock();
        }

        ItemStack held = mc.player.getHeldItem();
        if (held == null || !isValid(held.getItem())) {
            return;
        }

        FastScaffoldMode fast = getFastScaffoldMode();
        fastScaffoldTick(fast);

        if (delay > 0) {
            --delay;
            return;
        }

        BlockPos below = BlockUtils.below(1.0);
        airBelowTicks = BlockUtils.isReplaceable(below) ? ++airBelowTicks : 0;

        boolean towering = MoveUtils.jumpKeyDown();
        boolean jumpOrKeepY = fast.jump || fast.keepY;
        if (isSupported() && !towering && !jumpOrKeepY) {
            return;
        }

        BlockPos pos = place(null, held);
        if (pos != null) {
            ++placedCount;
            if (sneakEvery.get() > 0 && placedCount % sneakEvery.get() == 0) {
                sneakQueued = true;
            }
            if (held.stackSize > 0) {
                afterPlace(pos, held);
            }
        }
    }

    private void swapToBlock() {
        ItemStack held = mc.player.getHeldItem();
        if (held != null && isValid(held.getItem())) return;
        int slot = neededSlot();
        if (slot >= 0 && slot < 9) {
            mc.player.inventory.currentItem = slot;
        }
    }

    private void fastScaffoldTick(FastScaffoldMode fast) {
        boolean moving = MoveUtils.isMoving();
        boolean groundMoving = mc.player.onGround && moving;
        boolean can = fast.canFastScaffold(this);
        boolean sprintForward = fast.sprint && MoveUtils.direction().forward;
        boolean applied = false;

        if (can && !fastEnabled) {
            fastEnabled = true;
            fast.onEnable(this);
        } else if (!can && fastEnabled) {
            fastEnabled = false;
            fast.onDisable(this, false);
        }

        if (can) {
            boolean newY;
            double speed;
            switch (fast) {
                case Constant_A:
                    if (groundMoving) {
                        double d = 0.153 + MoveUtils.perSpeedLevel(0.034);
                        if (MoveUtils.speedLevel() == 0 && MoveUtils.isDiagonal()) d *= 0.87;
                        MoveUtils.strafe(d);
                    }
                    break;
                case Constant_B:
                    if (groundMoving) {
                        MoveUtils.strafe(0.181);
                    }
                    break;
                case Jump_A:
                case Jump_B:
                case Jump_C:
                case Jump_D:
                    if (!groundMoving) {
                        if (moving && jumpFacingForward.get() && mc.player.motionY < 0.0) {
                            double d = Math.floor(mc.player.posY);
                            if (d - 2.0 == (double) keptY && mc.player.posY + mc.player.motionY < d) {
                                legitTicks = !legitMotion.get() ? 1 : 5;
                            }
                        }
                    } else {
                        if (fast.state != 2) {
                            newY = false;
                            if (mc.player.ticksExisted - fast.ticks > 10) {
                                keptY = (int) Math.floor(mc.player.posY) - 1;
                                newY = true;
                            }
                            if (jumpFacingForward.get() && newY) {
                                legitTicks = !legitMotion.get() ? 1 : 5;
                                fast.state = 1;
                            }
                            if (fast.state == 0) {
                                if (fast != FastScaffoldMode.Jump_A) {
                                    speed = !sprintForward ? (!newY ? 0.545 : 0.4) : (!newY ? 0.3 : 0.25);
                                } else {
                                    speed = !MoveUtils.isDiagonal() ? 0.53 : 0.37;
                                }
                                jumpWith(speed);
                            }
                        } else {
                            fast.state = 0;
                        }
                        MoveUtils.multiplyMotion(0.95);
                    }
                    break;
                case Keep_Y_A:
                case Keep_Y_B:
                    if (!groundMoving) {
                        if (moving && jumpFacingForward.get() && mc.player.motionY < 0.0) {
                            double d = Math.floor(mc.player.posY);
                            if (d - 1.0 == (double) keptY && mc.player.posY + mc.player.motionY < d) {
                                legitTicks = !legitMotion.get() ? 1 : 5;
                            }
                        }
                    } else {
                        if (fast.state != 2) {
                            newY = false;
                            if (mc.player.ticksExisted - fast.ticks > 10) {
                                keptY = (int) Math.floor(mc.player.posY) - 1;
                                newY = true;
                            }
                            if (jumpFacingForward.get() && newY) {
                                if (legitTicks == 0) legitTicks = !legitMotion.get() ? 1 : 5;
                                fast.state = 1;
                                MoveUtils.multiplyMotion(MoveUtils.speedLevel() <= 0 ? 0.8 : 0.4);
                            }
                            if (fast.state == 0) {
                                speed = !sprintForward ? (!newY ? 0.545 : 0.54) : (!newY ? 0.3 : 0.25);
                                jumpWith(speed);
                            }
                        } else {
                            fast.state = 0;
                        }
                        MoveUtils.multiplyMotion(0.95);
                    }
                    break;
                default:
                    break;
            }

            if (!mc.player.onGround && (fast == FastScaffoldMode.Jump_D || fast == FastScaffoldMode.Keep_Y_B) && MoveUtils.motionYDecimals() == 30) {
                mc.player.motionY -= 0.14;
            }

            if (groundMoving) {
                double d = fastScaffoldMotion.get();
                if (!legitMotion.get() && MoveUtils.direction().diagonal) d -= 0.02;
                MoveUtils.multiplyMotion(d);
                applied = true;
            }
        }

        if (!applied && groundMoving) {
            double d = motion.get();
            if (!legitMotion.get()) {
                if (MoveUtils.direction().diagonal) d -= 0.02;
                if (mc.player.ticksExisted - startTick <= 2) d -= 0.05;
            }
            MoveUtils.multiplyMotion(d);
        }
    }

    private void jumpWith(double d) {
        if (legitMotion.get()) return;
        d += (speedBoost.get() / 10.0) + (1.0E-4 * Math.random());
        MoveUtils.setSpeed(d);
        mc.player.motionY = 0.41999998688698;
    }

    private void updatePostPlayerInput() {
        if (sneakQueued && sincePlace() > 90L) {
            sneakQueued = false;
            mc.player.setSneaking(true);
        }
        if (jumpState == 1) {
            if (MoveUtils.jumpKeyDown()) {
                jumpState = 2;
            } else {
                jumpState = 0;
            }
        } else if (jumpState == 2) {
            mc.player.jump();
            jumpState = 0;
        }
    }

    private BlockPos place(BlockPos last, ItemStack itemStack) {
        FastScaffoldMode fast = getFastScaffoldMode();
        boolean towering = MoveUtils.jumpKeyDown();
        boolean jumpOrKeepY = fast.jump || fast.keepY;
        if (isSupported() && !towering && !jumpOrKeepY) {
            return null;
        }

        BlockPos target = search(last);
        if (target == null) return null;
        EnumFacing facing = faceFor(target, null);
        if (facing == null) return null;
        BlockPos against = target.offset(facing.getOpposite());

        if (airBelowTicks > 0 || MoveUtils.nearEdge(0.15) || towering || jumpOrKeepY) {
            MovingObjectPosition hit = aim(against, facing, -170.0f, aimYaw);
            if (hit == null) {
                return last == null ? null : place(null, itemStack);
            }
            RotationMode rotMode = getRotationMode();
            switch (rotMode) {
                case Simple:
                    float f3 = MoveUtils.movementAngle();
                    if (Math.abs(f3 - aimYaw) < 100.0f) {
                        float f4 = (aimYaw - f3 + 540.0f) % 360.0f - 180.0f;
                        float f6 = 0.0f;
                        if (Math.abs(f4) >= 10.0f) {
                            f6 = Math.max(-10.0f, Math.min(f4, 10.0f));
                        }
                        aimYaw = f3 + f6;
                        if (mc.player.onGround) aimPitch = 82.5f;
                    }
                    break;
                case Lazy:
                    break;
                default:
                    break;
            }

            Vec3 vec3 = hit.hitVec;
            if (mc.playerController.onPlayerRightClick(mc.player, mc.world, itemStack, against, facing, vec3)) {
                if (silentSwing.get()) {
                    sendPacketNoEvent(new C0APacketAnimation());
                } else {
                    mc.player.swingItem();
                }
                if (searchMode == 1 || (searchMode == 2 && fast.keepY)) {
                    fast.ticks = mc.player.ticksExisted;
                }
                lastPlaced = target;
                placedAt = System.currentTimeMillis();
                placed.put(target, placedAt);
                airBelowTicks = 0;
                return target;
            }
        }
        return null;
    }

    private void afterPlace(BlockPos pos, ItemStack itemStack) {
        FastScaffoldMode fast = getFastScaffoldMode();
        if (fast == FastScaffoldMode.Side && fast.canFastScaffold(this) && (fast.ticks == -1 || ++fast.ticks == 2 || fast.ticks >= 5)) {
            if (fast.ticks >= 5) fast.ticks = 0;
            double cx = (double) pos.getX() + 0.5;
            double cz = (double) pos.getZ() + 0.5;
            EnumFacing facing = null;
            switch (mc.player.getHorizontalFacing()) {
                case NORTH:
                case SOUTH:
                    facing = mc.player.posX > cx ? EnumFacing.EAST : EnumFacing.WEST;
                    break;
                case WEST:
                case EAST:
                    facing = mc.player.posZ > cz ? EnumFacing.SOUTH : EnumFacing.NORTH;
                    break;
                default:
                    break;
            }
            if (facing != null) {
                Vec3 vec3 = BlockUtils.hitVec(pos, facing);
                MovingObjectPosition hit = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.BLOCK, vec3, facing, pos);
                BlockPos placedP = BlockUtils.placedPos(hit);
                if (placedP != null && BlockUtils.isReplaceable(placedP)) {
                    mc.playerController.onPlayerRightClick(mc.player, mc.world, itemStack, pos, facing, vec3);
                    if (silentSwing.get()) {
                        sendPacketNoEvent(new C0APacketAnimation());
                    } else {
                        mc.player.swingItem();
                    }
                    placedAt = System.currentTimeMillis();
                    placed.put(pos, placedAt);
                }
            }
        }

        MultiPlaceMode mp = getMultiPlaceMode();
        if (mp != MultiPlaceMode.Disabled && itemStack.stackSize > 0 && (MoveUtils.jumpKeyDown() || fast.canFastScaffold(this))) {
            placeExtra(itemStack);
            if (mp == MultiPlaceMode._2_extra && itemStack.stackSize > 0) {
                placeExtra(itemStack);
            }
        }
    }

    private boolean placeExtra(ItemStack itemStack) {
        BlockPos target = search(lastPlaced);
        if (target == null) return false;
        EnumFacing facing = faceFor(target, lastPlaced);
        if (facing == null) return false;
        BlockPos against = target.offset(facing.getOpposite());
        float[] r = RotationUtils.searchBlockRotation(against, facing, -170.0f, getPrecision(), aimYaw, false);
        if (r == null) return false;

        Vec3 eyes = mc.player.getPositionEyes(1.0f);
        Vec3 dir = RotationUtils.getVectorForRotation(r[0], r[1]);
        Vec3 end = eyes.addVector(dir.xCoord * mc.playerController.getBlockReachDistance(), dir.yCoord * mc.playerController.getBlockReachDistance(), dir.zCoord * mc.playerController.getBlockReachDistance());
        MovingObjectPosition hit = mc.world.rayTraceBlocks(eyes, end, false, false, true);
        if (hit == null) return false;

        if (mc.playerController.onPlayerRightClick(mc.player, mc.world, itemStack, against, facing, hit.hitVec)) {
            if (silentSwing.get()) {
                sendPacketNoEvent(new C0APacketAnimation());
            } else {
                mc.player.swingItem();
            }
            lastPlaced = target;
            placedAt = System.currentTimeMillis();
            placed.put(target, placedAt);
            return true;
        }
        return false;
    }

    private BlockPos search(BlockPos last) {
        List<Vec3> list = new ArrayList<>();
        searchMode = 0;
        FastScaffoldMode fast = getFastScaffoldMode();

        if (keptY != -1 && fast.canFastScaffold(this)) {
            int decimals = MoveUtils.posYDecimals();
            if (fast == FastScaffoldMode.Jump_A) {
                searchMode = (decimals == 1661 || decimals == 2492 && mc.player.motionY > 0.0) ? 1 : 2;
            } else if (fast == FastScaffoldMode.Jump_B || fast == FastScaffoldMode.Jump_C || fast == FastScaffoldMode.Jump_D) {
                if (mc.player.ticksExisted - fast.ticks > 4 && mc.player.posY - (double) keptY < fast.height && mc.player.motionY < -0.1) {
                    searchMode = 1;
                    if (fast.ticks == 0) MoveUtils.multiplyMotion(0.4);
                } else {
                    searchMode = 2;
                }
            } else if (fast.keepY) {
                searchMode = 2;
            } else if (fast == FastScaffoldMode.Float_A) {
                if (mc.player.onGround && mc.player.ticksExisted - enableTick > 0) {
                    keptY = -1;
                } else {
                    searchMode = 2;
                }
            }
        }

        return candidates(list, searchMode, keptY) ? firstPlaceable(list, last) : null;
    }

    private boolean candidates(List<Vec3> list, int mode, int y) {
        for (int i = -5; i <= 5; ++i) {
            for (int j = -5; j <= 5; ++j) {
                for (int k = -3; k <= -1; ++k) {
                    int n4 = MathHelper.floor_double(mc.player.posY) + k;
                    if (mode > 0 && n4 != y) continue;
                    double d2 = mc.player.posX + (double) i;
                    double d = mc.player.posZ + (double) j;
                    SCRATCH.set(MathHelper.floor_double(d2), n4, MathHelper.floor_double(d));
                    if (BlockUtils.isReplaceable(SCRATCH)) continue;

                    for (int n3 = -1; n3 <= 1; n3 += 2) {
                        switch (mode) {
                            case 0:
                                list.add(new Vec3(d2 + (double) n3, (double) n4, d));
                                list.add(new Vec3(d2, (double) n4, d + (double) n3));
                                break;
                            case 1:
                                list.add(new Vec3(d2, (double) (n4 + n3), d));
                                continue;
                            case 2:
                                list.add(new Vec3(d2 + (double) n3, (double) n4, d));
                                list.add(new Vec3(d2, (double) n4, d + (double) n3));
                                break;
                        }
                    }
                }
            }
        }
        if (list.isEmpty()) return false;

        Set<Long> seen = new HashSet<>(list.size() * 2);
        List<Vec3> unique = new ArrayList<>(list.size());
        for (Vec3 vec3 : list) {
            long l = Double.doubleToLongBits(vec3.xCoord) ^ Double.doubleToLongBits(vec3.yCoord) * 31L ^ Double.doubleToLongBits(vec3.zCoord) * 31L * 31L;
            if (seen.add(l)) unique.add(vec3);
        }
        list.clear();
        list.addAll(unique);

        final double x = mc.player.posX;
        double y2 = mc.player.posY - 1.0;
        final double z = mc.player.posZ;
        if (!mc.player.onGround && !MoveUtils.jumpKeyDown() && !getFastScaffoldMode().canFastScaffold(this)) {
            y2 -= 1.0;
        }
        final double fy = y2;
        Comparator<Vec3> comparator = Comparator.comparingDouble(p -> {
            double dx = x - p.xCoord, dy = fy - p.yCoord, dz = z - p.zCoord;
            return dx * dx + dy * dy + dz * dz;
        });

        if (mc.player.ticksExisted - startTick <= 3) {
            double yaw = Math.toRadians(mc.player.rotationYaw);
            final double dirX = -Math.sin(yaw);
            final double dirZ = Math.cos(yaw);
            comparator = comparator.thenComparingDouble(p -> -((p.xCoord - x) * dirX + (p.zCoord - z) * dirZ));
        }
        list.sort(comparator);
        return true;
    }

    private BlockPos firstPlaceable(List<Vec3> list, BlockPos last) {
        for (Vec3 vec3 : list) {
            SCRATCH.set(MathHelper.floor_double(vec3.xCoord), MathHelper.floor_double(vec3.yCoord), MathHelper.floor_double(vec3.zCoord));
            if (!BlockUtils.isReplaceable(SCRATCH) || faceFor(SCRATCH, last) == null) continue;
            return new BlockPos(SCRATCH);
        }
        return null;
    }

    private MovingObjectPosition aim(BlockPos pos, EnumFacing facing, float offset, float lastYaw) {
        float[] r = RotationUtils.searchBlockRotation(pos, facing, offset, getPrecision(), lastYaw, false);
        if (r == null) return null;
        aimYaw = r[0];
        aimPitch = r[1];
        if (getRotationMode() == RotationMode.Smooth && lastPlaced != null) {
            smoothLimit = 35.0f + (float) Math.random();
            float f3 = MathHelper.wrapAngleTo180_float(aimYaw - lastYaw);
            if (Math.abs(f3) > smoothLimit) return null;
        }
        Vec3 eyes = mc.player.getPositionEyes(1.0f);
        Vec3 dir = RotationUtils.getVectorForRotation(aimYaw, aimPitch);
        Vec3 end = eyes.addVector(dir.xCoord * mc.playerController.getBlockReachDistance(), dir.yCoord * mc.playerController.getBlockReachDistance(), dir.zCoord * mc.playerController.getBlockReachDistance());
        return mc.world.rayTraceBlocks(eyes, end, false, false, true);
    }

    private float[] calculateRotations(float curYaw, float curPitch) {
        RotationMode rotMode = getRotationMode();
        if (rotMode == RotationMode.None) return null;

        long l = sincePlace();
        float yaw;
        float pitch;

        if (rotMode == RotationMode.Offset) {
            float f6 = (float) MoveUtils.inputAngleSigned();
            float f7 = mc.player.rotationYaw + f6;
            float f8 = (f7 % 360.0f + 360.0f) % 360.0f;
            float f9 = f8 % 90.0f;
            float f10 = (f9 >= 10.0f && f9 < 45.0f) ? 45.0f : -45.0f;
            if (!mc.player.onGround && rotMode.groundSide != 0.0f) {
                f10 = rotMode.groundSide;
            } else {
                rotMode.groundSide = f10;
            }
            if (f10 != rotMode.side) {
                rotMode.side = f10;
                rotMode.sideJitter = (float) (Math.signum(f10) * (0.02 + Math.random() * 0.06));
            }
            aimYaw = MoveUtils.movementAngle() + (f10 + rotMode.sideJitter);
        }

        if (rotMode == RotationMode.Smooth && lastPlaced != null) {
            yaw = aimYaw;
            pitch = aimPitch;
            float f7 = smoothLimit;
            if (smoothSnap) {
                smoothSnap = false;
                f7 = 81.0f + (float) ((Math.random() - 0.5) * 4.0);
            }
            float f8 = MathHelper.wrapAngleTo180_float(yaw - curYaw);
            if (Math.abs(f8) > f7) {
                yaw = curYaw + (f8 > 0.0f ? f7 : -f7);
            }
            return RotationUtils.fixSensitivity(yaw, pitch, curYaw, curPitch);
        }

        if (aimPitch != 100.0f && l < holdTime()) {
            yaw = aimYaw;
            pitch = aimPitch;
            if (l > 5L && MoveUtils.isMoving() && Math.random() > 0.3) {
                yaw += (float) ((Math.random() - 0.5) * 0.5);
                pitch += RotationUtils.randomOffset();
            }
            return RotationUtils.fixSensitivity(yaw, pitch, curYaw, curPitch);
        } else {
            if (aimPitch != 100.0f) aimPitch = 100.0f;
            int n = mc.player.ticksExisted - startTick;
            float f8 = MoveUtils.movementAngle() + (float) yawOffset;
            float f9 = rotMode != RotationMode.Simple ? restPitch : 82.5f;
            f9 += RotationUtils.randomOffset();

            yaw = f8;
            pitch = f9;
            if (n <= 2) {
                float f10 = n != 0 ? (n != 2 ? 0.8f : 0.55f) : 0.3f;
                yaw = curYaw + f10 * (f8 - curYaw);
                pitch = curPitch + f10 * (f9 - curPitch);
            }
            return RotationUtils.fixSensitivity(yaw, pitch, curYaw, curPitch);
        }
    }

    public static EnumFacing faceFor(BlockPos pos, BlockPos only) {
        BlockPos down = pos.down();
        if (canPlaceAgainst(down) && (only == null || down.equals(only))) {
            return EnumFacing.UP;
        }
        Minecraft mc = Minecraft.getInstance();
        double d = mc.player.posX - ((double) pos.getX() + 0.5);
        double d2 = mc.player.posZ - ((double) pos.getZ() + 0.5);
        EnumFacing facing = Math.abs(d) > Math.abs(d2) ? (d > 0.0 ? EnumFacing.EAST : EnumFacing.WEST) : (d2 > 0.0 ? EnumFacing.SOUTH : EnumFacing.NORTH);
        BlockPos toward = pos.offset(facing);
        if (canPlaceAgainst(toward) && (only == null || toward.equals(only))) {
            return facing.getOpposite();
        }
        for (EnumFacing side : SIDES) {
            if (side == facing) continue;
            BlockPos other = pos.offset(side);
            if (!canPlaceAgainst(other)) continue;
            if (only != null && !other.equals(only)) continue;
            return side.getOpposite();
        }
        return null;
    }

    private static boolean canPlaceAgainst(BlockPos pos) {
        return !BlockUtils.isReplaceable(pos) && !BlockUtils.isInteractable(BlockUtils.getBlock(pos));
    }

    private long holdTime() {
        if (mc.player.isSprinting()) return 710L;
        if (!MoveUtils.isMoving()) return 460L;
        return 110L;
    }

    private long sincePlace() {
        return Math.abs(System.currentTimeMillis() - placedAt);
    }

    public boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        ItemBlock itemBlock = (ItemBlock) item;
        Block block = itemBlock.getBlock();
        return !getBlacklistedBlocks().contains(block);
    }

    public int neededSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != null && stack.stackSize != 0) {
                Item item = stack.getItem();
                if (isValid(item)) {
                    return i;
                }
            }
        }
        return mc.player.inventory.currentItem;
    }

    public int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.player.inventoryContainer.getSlot(i).getHasStack()) continue;
            final ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
            if (is == null) continue;
            final Item item = is.getItem();
            if (!(item instanceof ItemBlock) || getBlacklistedBlocks().contains(((ItemBlock) item).getBlock())) {
                continue;
            }
            blockCount += is.stackSize;
        }
        return blockCount;
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (!showBlockCount.get()) return;

        GL11.glPushMatrix();
        ScaleUtils.scale(mc);
        ItemStack stack = mc.player.inventory.getStackInSlot(neededSlot());

        if (stack != null && stack.getItem() instanceof ItemBlock && isValid(stack.getItem())) {
            ScaledResolution sr = event.getResolution();
            float width = Fonts.SF.SF_18.SF_18.stringWidth("/" + getBlockCount());
            float x = sr.getScaledWidthStatic(mc) / 2.0F - width / 2.0F;
            float y = sr.getScaledHeightStatic(mc) / 2.0F;

            RenderUtils.drawStack(Fonts.SF.SF_18.SF_18, false, stack, x - 5.0F, y + 11.0F);
            Fonts.SF.SF_18.SF_18.drawString(Integer.toString(getBlockCount()), x + 11.0F, y + 16.0F, 0xFFFFFF, true);
        }

        GL11.glPopMatrix();
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (!highlightBlocks.get() || placed.isEmpty()) return;
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<BlockPos, Long>> it = placed.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Long> entry = it.next();
            long age = now - entry.getValue();
            float f = (float) age / 500.0f;
            if (f >= 1.0f) {
                it.remove();
                continue;
            }
            float alpha = 1.0f - f;
            drawBlockBox(entry.getKey(), 1.0f, 0.4f, 0.8f, alpha * 0.4f);
        }
    }

    private void drawBlockBox(BlockPos pos, float r, float g, float b, float a) {
        RenderManager rm = mc.getRenderManager();
        double x = (double) pos.getX() - rm.viewerPosX;
        double y = (double) pos.getY() - rm.viewerPosY;
        double z = (double) pos.getZ() - rm.viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        RenderUtils.drawSolidBlockESP(pos, new Color(r, g, b, a).getRGB());
    }

    //region Compatibility Getters
    public boolean renderRotations() {
        return renderRotations.get();
    }

    public float getYaw() {
        return aimYaw;
    }

    public float getPitch() {
        return aimPitch;
    }

    public boolean safeWalk() {
        if (!isEnabled()) return false;
        if (safeWalk.get()) return true;
        FastScaffoldMode f = getFastScaffoldMode();
        return (f == FastScaffoldMode.Float_A || f == FastScaffoldMode.Float_B) && f.canFastScaffold(this);
    }

    public List<Block> getBlacklistedBlocks() {
        return blacklistedBlocks;
    }

    public ListProperty<String> getAddons() {
        return addons;
    }
    //endregion
}
