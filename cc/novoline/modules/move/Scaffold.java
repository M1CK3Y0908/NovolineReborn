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
import cc.novoline.utils.RenderUtils;
import cc.novoline.utils.RotationUtils;
import cc.novoline.utils.ScaleUtils;
import cc.novoline.utils.WorldUtils;
import cc.novoline.utils.fonts.impl.Fonts;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
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

        public boolean canSprint(Scaffold scaffold) {
            if (!this.canFastScaffold(scaffold)) {
                if (scaffold.mc.player.ticksExisted == scaffold.startTick && scaffold.jumpState == 2) {
                    return false;
                }
                return false;
            }
            if (scaffold.legitMotion.get() && (this.jump || this.keepY)) {
                return scaffold.legitTicks > 0;
            }
            if (!this.sprint) {
                return false;
            }
            return this != Side || this.ticks != -1;
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

        public boolean canFastScaffold(Scaffold scaffold) {
            if (!scaffold.active || this == Disabled) {
                return false;
            }
            if (scaffold.towering || MoveUtils.jumpKeyDown()) {
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

        public void onDisable(Scaffold scaffold, boolean disabling) {
            if (this.jump || this.keepY) {
                scaffold.keptY = -1;
            }
            if (this.jump && !disabling && !scaffold.towering) {
                MoveUtils.setSpeed(0.05);
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
    @Property("slow-speed-potion")
    private final BooleanProperty slowSpeedPotion = booleanFalse();
    @Property("tower")
    private final BooleanProperty tower = booleanTrue();
    @Property("tower-mode")
    private final StringProperty towerModeSetting = createString("Vanilla").acceptableValues("Vanilla", "Normal", "Fly", "2_tick", "4_tick");
    @Property("render-rotations")
    private final BooleanProperty renderRotations = booleanTrue();
    /* properties @on */

    /* fields */
    public boolean active;
    public boolean disablingModule;
    private boolean fastEnabled;
    private boolean rightHeld;
    public int keptY = -1;
    private int searchMode;
    public int legitTicks;
    public int delay;
    public int enableTick;
    public int jumpState;
    public int startTick;
    private int jumpTick;
    private int airBelowTicks;
    private int placedCount;
    public int pausedTicks;
    private boolean sneakQueued;
    private boolean smoothSnap;
    private BlockPos lastPlaced;
    private float aimYaw;
    private float aimPitch = 100.0f;
    private float smoothLimit;
    private float legitYaw;
    private float restPitch;
    private double yawOffset;
    private long placedAt;
    public long startedAt;
    public float startPitch;
    private long stoppedAt;
    private FastScaffoldMode lastFastScaffold = FastScaffoldMode.Disabled;
    private final Map<BlockPos, Long> placed = new HashMap<>();
    private int oldSlot = -1;

    public long groundTicks;
    public long airTicks;
    public float lastReportedYaw;
    public float lastReportedPitch;

    // Tower fields
    public boolean towering;
    private int towerTicks;
    private int towerStage;
    private int slowedLeft;

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
        Manager.put(new Setting("SF_SLOW_POT", "Slow Speed Potion", SettingType.CHECKBOX, this, slowSpeedPotion));
        Manager.put(new Setting("SF_TOWER", "Tower", SettingType.CHECKBOX, this, tower));
        Manager.put(new Setting("SF_TOWER_MODE", "Tower Mode", SettingType.COMBOBOX, this, towerModeSetting));
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
            String val = multiPlaceSetting.get();
            if (val.equalsIgnoreCase("1_extra")) return MultiPlaceMode._1_extra;
            if (val.equalsIgnoreCase("2_extra")) return MultiPlaceMode._2_extra;
            return MultiPlaceMode.valueOf(val);
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
        if (autoSwap.get()) {
            oldSlot = mc.player.inventory.currentItem;
        }
        restPitch = randomFloat(84.5f, 87.5f);
        yawOffset = RotationUtils.randomOffset();
        startTick = mc.player.ticksExisted;
        startedAt = System.currentTimeMillis();
        aimYaw = mc.player.rotationYaw;
        aimPitch = 100.0f;

        if (!mc.player.onGround) {
            delay = 3;
        } else if (MoveUtils.nearEdge(0.2) && getBlockCount() > 0) {
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
        stop();
        legitTicks = 0;
        delay = 0;
        airBelowTicks = 0;
        pausedTicks = 0;
        placedCount = 0;
        placedAt = 0L;
        double d = MoveUtils.speed();
        if (d > 0.34) {
            MoveUtils.multiplyMotion((0.29 + 0.02 * Math.random()) / d);
        }
        disablingModule = false;
    }

    private void stop() {
        if (active) {
            stoppedAt = System.currentTimeMillis();
            getRotationMode().onDisable();
            if (oldSlot != -1 && autoSwap.get()) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            if (fastEnabled) {
                getFastScaffoldMode().onDisable(this, true);
                fastEnabled = false;
            }
            towering = false;
            sneakQueued = false;
            smoothSnap = false;
            rightHeld = false;
            active = false;
            jumpState = 1;
            if (mc.player.onGround && getRotationMode() != RotationMode.Offset) {
                MoveUtils.multiplyMotion(0.75);
            }
        }
        keptY = -1;
        lastPlaced = null;
        aimPitch = 100.0f;
    }

    public void resetKeepY() {
        keptY = -1;
    }

    @EventTarget
    public void onMotion(MotionUpdateEvent event) {
        if (event.getState() == MotionUpdateEvent.State.PRE) {
            if (mc.player.onGround) {
                ++groundTicks;
                airTicks = 0L;
            } else {
                ++airTicks;
                groundTicks = 0L;
            }
            lastReportedYaw = event.getYaw();
            lastReportedPitch = event.getPitch();

            updatePreMotion(event);
            updateTowerPreMotion(event);
            updatePlacement();

            float[] rot = calculateRotations(event.getYaw(), event.getPitch());
            if (rot != null) {
                event.setYaw(rot[0]);
                event.setPitch(rot[1]);
                aimYaw = rot[0];
                aimPitch = rot[1];
            }
        } else if (event.getState() == MotionUpdateEvent.State.POST) {
            updatePostPlayerInput();
        }
    }

    private void updatePreMotion(MotionUpdateEvent e) {
        boolean floatWasSet = FastScaffoldMode.Float_A.state == 1 || FastScaffoldMode.Float_B.state == 1;
        FastScaffoldMode.Float_A.state = 0;
        FastScaffoldMode.Float_B.state = 0;
        FastScaffoldMode fastScaffold = getFastScaffoldMode();
        if (!fastScaffold.canFastScaffold(this)) {
            return;
        }
        if (fastScaffold == FastScaffoldMode.Float_A) {
            if (e.getY() % 1.0 == 0.0 && mc.player.motionY == -0.0784000015258789) {
                e.setY(e.getY() + 1.0E-13);
                fastScaffold.state = 1;
                if (MoveUtils.isMoving() && !mc.player.isSneaking()) {
                    double d = fastScaffold.sprint ? 0.2 + MoveUtils.perSpeedLevel(MoveUtils.isDiagonal() ? 0.035 : 0.043)
                            : 0.23 + MoveUtils.perSpeedLevel(MoveUtils.isDiagonal() ? 0.042 : 0.05);
                    if (MoveUtils.speedLevel() == 0 && MoveUtils.isDiagonal()) {
                        d *= 0.87;
                    }
                    MoveUtils.strafe(d);
                    if (!fastScaffold.sprint) {
                        mc.player.setSprinting(true);
                    }
                }
            }
        } else if (fastScaffold == FastScaffoldMode.Float_B) {
            if (e.getY() % 1.0 == 0.0 && mc.player.motionY == -0.0784000015258789 && MoveUtils.isMoving() && !mc.player.isSneaking()) {
                double d;
                if (!floatWasSet && (MoveUtils.isDiagonal() || !MoveUtils.isAirBelow())) {
                    e.setY(e.getY() + 0.001);
                    e.setOnGround(false);
                    fastScaffold.state = 1;
                    d = 0.153;
                } else {
                    d = 0.245 + MoveUtils.perSpeedLevel(0.025);
                }
                MoveUtils.strafe(d);
            }
        } else if ((fastScaffold.jump || fastScaffold.keepY) && mc.player.onGround && (jumpFacingForward.get() || floatWasSet)) {
            if (fastScaffold.state == 1) {
                fastScaffold.state = 2;
                double d = fastScaffold.sprint && MoveUtils.direction().forward ? 0.25 : (fastScaffold.keepY ? 0.54 : 0.4);
                jumpWith(d);
            }
            delay = legitTicks;
        }
    }

    private void updateTowerPreMotion(MotionUpdateEvent e) {
        if (!towering) {
            towerTicks = 0;
            return;
        }
        ++towerTicks;
        boolean diagonal = MoveUtils.isDiagonal();
        String mode = towerModeSetting.get();
        if (mode.equalsIgnoreCase("Vanilla")) {
            if (jumpFacingForward.get()) {
                if (towerStage == 0) {
                    if (mc.player.onGround) {
                        if (legitTicks == 0) {
                            towerStage = 1;
                            legitTicks = legitMotion.get() ? 5 : 1;
                        } else {
                            towerStage = 2;
                        }
                    }
                } else if (towerStage == 1) {
                    delay = legitTicks;
                    towerStage = 2;
                }
            }
            return;
        }
        boolean faceBack = false;
        mc.player.setSprinting(false);
        if (mode.equalsIgnoreCase("Normal")) {
            if (mc.player.onGround) {
                double d = (diagonal ? 0.28 : 0.42) / 10.0 + (speedBoost.get() / 10.0);
                MoveUtils.jump(d);
                if (d > 0.17) faceBack = true;
            }
        } else if (mode.equalsIgnoreCase("Fly")) {
            if (mc.player.ticksExisted % 2 == 0) {
                mc.player.motionY = 0.42;
                double d = Math.min(0.24, 0.27) + 1.0E-4 * Math.random();
                MoveUtils.strafe(d);
            }
        } else if (mode.equalsIgnoreCase("2_tick") || mode.equalsIgnoreCase("4_tick")) {
            if (mc.player.onGround) {
                MoveUtils.jump((diagonal ? 0.28 : 0.42) / 10.0);
            } else {
                int decimals = MoveUtils.posYDecimals();
                if (decimals == 4200) {
                    mc.player.motionY = 0.33f;
                    MoveUtils.strafe(0.28);
                } else if (decimals == 7500) {
                    mc.player.motionY = 1.0 - (mc.player.posY % 1.0);
                }
            }
        }
        if (faceBack && jumpFacingForward.get()) {
            e.setYaw(MoveUtils.movementAngle() - 180.0f + RotationUtils.randomOffset());
            delay = 2;
        }
    }

    private void updatePlacement() {
        if (pausedTicks > 0 && --pausedTicks != 0) {
            stop();
            return;
        }
        if (mc.currentScreen != null) {
            stop();
            return;
        }
        autoSwapTick();
        ItemStack itemStack = mc.player.getHeldItem();
        if (itemStack == null || !isValid(itemStack.getItem())) {
            if (!autoSwap.get() || getBlockCount() == 0) {
                stop();
            }
            return;
        }
        long l = System.currentTimeMillis();
        if (!active) {
            startTick = mc.player.ticksExisted;
            startedAt = l;
            startPitch = mc.player.rotationPitch;
            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }
            if (slowOnEnable.get()) {
                MoveUtils.multiplyMotion(0.2);
            }
        }
        active = true;
        fastScaffoldTick();
        if (delay > 0) {
            --delay;
            return;
        }
        BlockPos below = BlockUtils.below(1.0);
        airBelowTicks = BlockUtils.isReplaceable(below) ? ++airBelowTicks : 0;
        BlockPos pos = place(null, itemStack);
        if (pos != null) {
            ++placedCount;
            if (sneakEvery.get() > 0 && placedCount % sneakEvery.get() == 0) {
                sneakQueued = true;
            }
            if (itemStack.stackSize > 0) {
                afterPlace(pos, itemStack);
            }
        }
    }

    private void autoSwapTick() {
        if (!autoSwap.get()) return;
        ItemStack held = mc.player.getHeldItem();
        if (held != null && isValid(held.getItem()) && held.stackSize > 0) {
            return;
        }
        int bestSlot = -1;
        int maxCount = 0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != null && isValid(stack.getItem()) && stack.stackSize > maxCount) {
                maxCount = stack.stackSize;
                bestSlot = i;
            }
        }
        if (bestSlot != -1 && mc.player.inventory.currentItem != bestSlot) {
            mc.player.inventory.currentItem = bestSlot;
        }
    }

    private void fastScaffoldTick() {
        rightHeld = Mouse.isButtonDown(1) && mc.currentScreen == null;
        FastScaffoldMode fastScaffold = getFastScaffoldMode();
        boolean moving = MoveUtils.isMoving();
        boolean groundMoving = mc.player.onGround && moving;
        boolean can = fastScaffold.canFastScaffold(this);
        boolean sprintForward = fastScaffold.sprint && MoveUtils.direction().forward;
        boolean applied = false;
        if (fastScaffold == lastFastScaffold) {
            if (!can) {
                if (fastEnabled) {
                    fastEnabled = false;
                    fastScaffold.onDisable(this, false);
                }
            } else if (!fastEnabled) {
                fastEnabled = true;
                fastScaffold.onEnable(this);
            }
        } else {
            if (fastEnabled && lastFastScaffold != null) {
                lastFastScaffold.onDisable(this, false);
            }
            if (can) {
                fastScaffold.onEnable(this);
            }
            fastEnabled = can;
            lastFastScaffold = fastScaffold;
        }
        double d;
        if (!can) {
            if (!towering && legitTicks > 0 && mc.player.onGround) {
                delay = 0;
                legitTicks = 0;
            }
        } else {
            boolean newY;
            double speed;
            switch (fastScaffold) {
                case Constant_A:
                    if (groundMoving) {
                        d = 0.153 + MoveUtils.perSpeedLevel(0.034);
                        if (MoveUtils.speedLevel() == 0 && MoveUtils.isDiagonal()) {
                            d *= 0.87;
                        }
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
                            d = Math.floor(mc.player.posY);
                            if (d - 2.0 == (double) keptY && mc.player.posY + mc.player.motionY < d) {
                                legitTicks = !legitMotion.get() ? 1 : 5;
                            }
                        }
                        break;
                    }
                    if (fastScaffold.state != 2) {
                        newY = false;
                        if (mc.player.ticksExisted - fastScaffold.ticks > 10) {
                            keptY = (int) Math.floor(mc.player.posY) - 1;
                            newY = true;
                        }
                        if ((jumpFacingForward.get() || FastScaffoldMode.Float_A.state == 1 || FastScaffoldMode.Float_B.state == 1) && newY) {
                            if (jumpFacingForward.get()) {
                                legitTicks = !legitMotion.get() ? 1 : 5;
                            }
                            fastScaffold.state = 1;
                        }
                        if (fastScaffold.state == 0) {
                            if (fastScaffold != FastScaffoldMode.Jump_A) {
                                if (!sprintForward) {
                                    speed = !newY ? 0.545 : 0.4;
                                } else {
                                    speed = !newY ? 0.3 : 0.25;
                                }
                            } else {
                                speed = !MoveUtils.isDiagonal() ? 0.53 : 0.37;
                            }
                            jumpWith(speed);
                        }
                    } else {
                        fastScaffold.state = 0;
                    }
                    MoveUtils.multiplyMotion(0.95);
                    break;
                case Keep_Y_A:
                case Keep_Y_B:
                    if (!groundMoving) {
                        if (moving && jumpFacingForward.get() && mc.player.motionY < 0.0) {
                            d = Math.floor(mc.player.posY);
                            if (d - 1.0 == (double) keptY && mc.player.posY + mc.player.motionY < d) {
                                legitTicks = !legitMotion.get() ? 1 : 5;
                            }
                        }
                    } else {
                        if (fastScaffold.state != 2) {
                            newY = false;
                            if (mc.player.ticksExisted - fastScaffold.ticks > 10) {
                                keptY = (int) Math.floor(mc.player.posY) - 1;
                                newY = true;
                            }
                            if ((jumpFacingForward.get() || FastScaffoldMode.Float_A.state == 1 || FastScaffoldMode.Float_B.state == 1) && newY) {
                                if (jumpFacingForward.get() && legitTicks == 0) {
                                    legitTicks = !legitMotion.get() ? 1 : 5;
                                }
                                fastScaffold.state = 1;
                                MoveUtils.multiplyMotion(MoveUtils.speedLevel() <= 0 ? 0.8 : 0.4);
                            }
                            if (fastScaffold.state == 0) {
                                if (!sprintForward) {
                                    speed = !newY ? 0.545 : 0.54;
                                } else {
                                    speed = !newY ? 0.3 : 0.25;
                                }
                                jumpWith(speed);
                            }
                        } else {
                            fastScaffold.state = 0;
                        }
                        MoveUtils.multiplyMotion(0.95);
                    }
                    break;
                default:
                    break;
            }
            if (!mc.player.onGround && (fastScaffold == FastScaffoldMode.Jump_D || fastScaffold == FastScaffoldMode.Keep_Y_B) && MoveUtils.motionYDecimals() == 30) {
                mc.player.motionY -= 0.14;
            }
            if (groundMoving) {
                d = fastScaffoldMotion.get();
                if (!legitMotion.get() && MoveUtils.direction().diagonal) {
                    d -= 0.02;
                }
                MoveUtils.multiplyMotion(d);
                applied = true;
            }
        }
        if (!applied && groundMoving) {
            d = motion.get();
            if (!legitMotion.get()) {
                if (slowSpeedPotion.get()) {
                    d -= MoveUtils.perSpeedLevel(0.17);
                }
                if (MoveUtils.direction().diagonal) {
                    d -= 0.02;
                }
                if (mc.player.ticksExisted - startTick <= 2) {
                    d -= 0.05;
                }
            }
            MoveUtils.multiplyMotion(d);
        }
    }

    private void jumpWith(double d) {
        if (legitMotion.get()) {
            return;
        }
        d += (speedBoost.get() / 10.0) + (1.0E-4 * Math.random());
        MoveUtils.jump(d);
    }

    private void updatePostPlayerInput() {
        if (sneakQueued && sincePlace() > 90L) {
            sneakQueued = false;
            mc.player.movementInput().setSneak(true);
            mc.player.movementInput().setMoveForward(mc.player.movementInput().getMoveForward() * 0.3f);
            mc.player.movementInput().setMoveStrafe(mc.player.movementInput().getMoveStrafe() * 0.3f);
        }
        if (jumpState == 1) {
            if (MoveUtils.jumpKeyDown()) {
                mc.player.movementInput().setJump(false);
                jumpState = 2;
            } else {
                jumpState = 0;
            }
        } else if (jumpState == 2) {
            mc.player.movementInput().setJump(true);
            jumpState = 0;
        }
        FastScaffoldMode fastScaffold = getFastScaffoldMode();
        if (fastScaffold == FastScaffoldMode.Float_A || fastScaffold == FastScaffoldMode.Float_B) {
            if (!fastScaffold.canFastScaffold(this) && fastScaffold.state == 1) {
                fastScaffold.state = 0;
            }
        } else if ((fastScaffold.jump || fastScaffold.keepY) && legitMotion.get() && fastScaffold.canFastScaffold(this) && mc.player.onGround && fastScaffold.state == 0) {
            mc.player.movementInput().setJump(true);
        }

        // Tower postPlayerInput
        if (canTower()) {
            towering = true;
            slowedLeft = 0;
            if (towerModeSetting.get().equalsIgnoreCase("Vanilla")) {
                if (jumpFacingForward.get()) {
                    mc.player.movementInput().setJump(true);
                    if (towerStage == 2) {
                        towerStage = 0;
                        mc.player.movementInput().setJump(true);
                    } else {
                        mc.player.movementInput().setJump(false);
                    }
                    if (legitMotion.get() && MoveUtils.isDiagonal() && airTicks == 1L) {
                        mc.player.movementInput().setMoveForward(0.0f);
                        mc.player.movementInput().setMoveStrafe(0.0f);
                    }
                } else {
                    mc.player.movementInput().setJump(true);
                }
            } else {
                mc.player.movementInput().setJump(false);
            }
        } else if (towering) {
            towering = false;
            if (slowedLeft == 0) {
                slowedLeft = 2;
            }
            towerStage = 0;
            resetKeepY();
        }
        if (slowedLeft > 0) {
            --slowedLeft;
            if (MoveUtils.isMoving()) {
                MoveUtils.setSpeed(0.15);
            }
        }
    }

    public boolean towerAllowed() {
        long l = sincePlace();
        return tower.get() && active && (l < 400L || l > 700L);
    }

    private boolean canTower() {
        if (mc.player.abilities.isFlying() || mc.currentScreen != null) return false;
        if (!active || !towerAllowed()) return false;
        ItemStack held = mc.player.getHeldItem();
        if (held == null || !isValid(held.getItem())) return false;
        if (!MoveUtils.isMoving() && !MoveUtils.jumpKeyDown()) return false;
        if (mc.player.isSneaking()) return false;
        if (!(MoveUtils.jumpKeyDown() || (towerStage != 0 && legitTicks != 0))) return false;
        return true;
    }

    private BlockPos place(BlockPos last, ItemStack itemStack) {
        BlockPos target = search(last);
        if (target == null) {
            return null;
        }
        EnumFacing facing = faceFor(target, null);
        if (facing == null) {
            return null;
        }
        BlockPos against = target.offset(facing.getOpposite());
        if (airBelowTicks > 0) {
            float yaw = aimYaw;
            float pitch = aimPitch;
            MovingObjectPosition hit = aim(against, facing, -170.0f, aimYaw);
            if (hit == null) {
                return last == null ? null : place(null, itemStack);
            }
            RotationMode rotation = getRotationMode();
            switch (rotation) {
                case Simple: {
                    float f3 = MoveUtils.movementAngle();
                    if (!(Math.abs(f3 - aimYaw) < 100.0f)) break;
                    float f4 = aimYaw - f3;
                    f4 = (f4 + 540.0f) % 360.0f - 180.0f;
                    float f6 = 0.0f;
                    if (Math.abs(f4) >= 10.0f) {
                        f6 = Math.max(-10.0f, Math.min(f4, 10.0f));
                    }
                    aimYaw = f3 + f6;
                    if (!mc.player.onGround) break;
                    aimPitch = 82.5f;
                    break;
                }
                case Lazy: {
                    if (pitch == 100.0f || rayTraceBlock(against, null, mc.playerController.getBlockReachDistance(), yaw, pitch) == null) break;
                    aimYaw = yaw;
                    aimPitch = pitch;
                    break;
                }
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
                FastScaffoldMode fastScaffold = getFastScaffoldMode();
                if (searchMode == 1 || (searchMode == 2 && fastScaffold.keepY)) {
                    fastScaffold.ticks = mc.player.ticksExisted;
                }
                lastPlaced = target;
                placedAt = System.currentTimeMillis();
                placed.put(target, placedAt);
                airBelowTicks = 0;
                return target;
            }
        } else if (MoveUtils.isMoving() && Math.random() > 0.8) {
            sendPacketNoEvent(new C08PacketPlayerBlockPlacement(itemStack));
        }
        return null;
    }

    private BlockPos search(BlockPos last) {
        List<Vec3> list = new ArrayList<>();
        searchMode = 0;
        FastScaffoldMode fastScaffold = getFastScaffoldMode();
        if (keptY != -1 && fastScaffold.canFastScaffold(this)) {
            int decimals = MoveUtils.posYDecimals();
            if (fastScaffold == FastScaffoldMode.Jump_A) {
                if (decimals == 1661 || (decimals == 2492 && mc.player.motionY > 0.0)) {
                    searchMode = 1;
                } else {
                    searchMode = 2;
                }
            } else if (fastScaffold == FastScaffoldMode.Jump_B || fastScaffold == FastScaffoldMode.Jump_C || fastScaffold == FastScaffoldMode.Jump_D) {
                if (mc.player.ticksExisted - fastScaffold.ticks > 4 && mc.player.posY - (double) keptY < fastScaffold.height && mc.player.motionY < -0.1) {
                    searchMode = 1;
                    if (fastScaffold.ticks == 0) {
                        MoveUtils.multiplyMotion(0.4);
                    }
                } else {
                    searchMode = 2;
                }
            } else if (fastScaffold.keepY) {
                searchMode = 2;
            } else if (fastScaffold == FastScaffoldMode.Float_A) {
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
        int n3;
        double d;
        for (int i = -5; i <= 5; ++i) {
            for (int j = -5; j <= 5; ++j) {
                for (int k = -3; k <= -1; ++k) {
                    int n4 = MathHelper.floor_double(mc.player.posY) + k;
                    if (mode > 0 && n4 != y) continue;
                    double d2 = mc.player.posX + (double) i;
                    d = mc.player.posZ + (double) j;
                    SCRATCH.set(MathHelper.floor_double(d2), n4, MathHelper.floor_double(d));
                    if (BlockUtils.isReplaceable(SCRATCH)) continue;
                    for (n3 = -1; n3 <= 1; n3 += 2) {
                        switch (mode) {
                            case 0:
                                list.add(new Vec3(d2 + (double) n3, (double) n4, d));
                                list.add(new Vec3(d2, (double) n4, d + (double) n3));
                            case 1:
                                list.add(new Vec3(d2, (double) (n4 + n3), d));
                                continue;
                            case 2:
                                list.add(new Vec3(d2 + (double) n3, (double) n4, d));
                                list.add(new Vec3(d2, (double) n4, d + (double) n3));
                        }
                    }
                }
            }
        }
        if (list.isEmpty()) {
            return false;
        }
        Set<Long> seen = new HashSet<>(list.size() * 2);
        List<Vec3> unique = new ArrayList<>(list.size());
        for (Vec3 vec3 : list) {
            long l = Double.doubleToLongBits(vec3.xCoord) ^ Double.doubleToLongBits(vec3.yCoord) * 31L ^ Double.doubleToLongBits(vec3.zCoord) * 31L * 31L;
            if (!seen.add(l)) continue;
            unique.add(vec3);
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
        Comparator<Vec3> comparator = Comparator.comparingDouble(p -> WorldUtils.distanceSq(x, fy, z, p));
        if (mc.player.ticksExisted - startTick <= 3) {
            double yaw = mc.player.rotationYaw * ((float) Math.PI / 180.0f);
            final double dirX = -Math.sin(yaw);
            final double dirZ = Math.cos(yaw);
            comparator = comparator.thenComparingDouble(p -> WorldUtils.behind(x, z, dirX, dirZ, p));
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

    public EnumFacing faceFor(BlockPos pos, BlockPos only) {
        BlockPos down = pos.down();
        if (canPlaceAgainst(down) && (only == null || down.equals(only))) {
            return EnumFacing.UP;
        }
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

    private boolean canPlaceAgainst(BlockPos pos) {
        if (!placed.isEmpty() && !placed.containsKey(pos) && sincePlace() < 250L) {
            boolean near = false;
            for (Map.Entry<BlockPos, Long> entry : placed.entrySet()) {
                if (!(mc.player.getDistanceSq(entry.getKey()) < 12.0)) continue;
                near = true;
                break;
            }
            if (near) {
                return false;
            }
        }
        return !BlockUtils.isReplaceable(pos) && !BlockUtils.isInteractable(BlockUtils.getBlock(pos));
    }

    private MovingObjectPosition aim(BlockPos pos, EnumFacing facing, float offset, float lastYaw) {
        float[] r = RotationUtils.searchBlockRotation(pos, facing, offset, getPrecision(), lastYaw, false);
        if (r == null) {
            return null;
        }
        aimYaw = r[0];
        aimPitch = r[1];
        if (getRotationMode() == RotationMode.Smooth && lastPlaced != null && groundTicks <= 3L) {
            smoothLimit = 35.0f + (float) (1.0 * Math.random());
            float f3 = MathHelper.wrapAngleTo180_float(aimYaw - lastYaw);
            if (Math.abs(f3) > smoothLimit) {
                return null;
            }
        }
        return rayTraceBlock(pos, null, mc.playerController.getBlockReachDistance(), aimYaw, aimPitch);
    }

    private float[] calculateRotations(float f, float f2) {
        if (!active || getRotationMode() == RotationMode.None) {
            return null;
        }
        RotationMode rotation = getRotationMode();
        long l = sincePlace();
        float f7;
        float f8;
        float f9;
        float f10;
        if (rotation == RotationMode.Offset) {
            float f6 = (float) MoveUtils.inputAngleSigned();
            f7 = mc.player.rotationYaw + f6;
            f8 = (f7 % 360.0f + 360.0f) % 360.0f;
            f9 = f8 % 90.0f;
            if (!(f9 >= 80.0f) && !(f9 < 10.0f)) {
                f10 = f9 >= 10.0f && f9 < 45.0f ? 45.0f : -45.0f;
            } else {
                f10 = -45.0f;
            }
            if (!mc.player.onGround && rotation.groundSide != 0.0f) {
                f10 = rotation.groundSide;
            } else {
                rotation.groundSide = f10;
            }
            if (f10 != rotation.side) {
                rotation.side = f10;
                rotation.sideJitter = (float) ((double) Math.signum(f10) * (0.02 + Math.random() * 0.06));
            }
            aimYaw = MoveUtils.movementAngle() + (f10 + rotation.sideJitter);
        }
        boolean jumpRotation = false;
        FastScaffoldMode fastScaffold = getFastScaffoldMode();
        if (legitTicks <= 0) {
            if (fastScaffold == FastScaffoldMode.Float_A && fastScaffold.ticks == 1 && mc.player.ticksExisted == enableTick) {
                jumpRotation = true;
            }
        } else {
            --legitTicks;
            jumpRotation = true;
        }
        if (jumpRotation) {
            f7 = -180.0f;
            f8 = aimPitch == 100.0f ? mc.player.rotationPitch : aimPitch;
            if (legitMotion.get()) {
                if (legitTicks == 4) {
                    if ((double) (mc.player.rotationYaw - lastReportedYaw) < -0.7) {
                        f7 = -f7;
                    }
                    legitYaw = f7;
                    --delay;
                    jumpRotation = false;
                } else if (legitTicks == 3) {
                    if (legitYaw > 0.0f) {
                        f7 = -f7;
                    }
                    if (rotation == RotationMode.Smooth) {
                        legitTicks = Math.min(legitTicks, 1);
                        if (delay > legitTicks) {
                            delay = legitTicks;
                        }
                        smoothSnap = true;
                    }
                    f8 = randomFloat(20.0f, 25.0f);
                } else if (legitTicks == 2) {
                    legitYaw += randomFloat(105.0f, 120.0f) * -Math.signum(legitYaw);
                    f7 = legitYaw;
                    f8 = randomFloat(40.0f, 45.0f);
                } else if (legitTicks == 1) {
                    legitYaw += randomFloat(33.0f, 35.0f) * -Math.signum(legitYaw);
                    f7 = legitYaw;
                    if (aimPitch == 100.0f || rotation == RotationMode.Simple) {
                        f8 = randomFloat(50.0f, 55.0f);
                    }
                } else if (legitTicks == 0) {
                    legitYaw += randomFloat(29.0f, 33.0f) * -Math.signum(legitYaw);
                    f7 = legitYaw;
                    if (aimPitch == 100.0f || rotation == RotationMode.Simple) {
                        f8 = randomFloat(65.0f, 70.0f);
                    }
                }
            }
            if (rotation == RotationMode.Offset && rotation.side > 0.0f) {
                f7 = -f7;
            }
            if (jumpRotation) {
                return RotationUtils.fixSensitivity(MoveUtils.movementAngle() + f7 + RotationUtils.randomOffset(), f8, f, f2);
            }
        }
        float yaw;
        float pitch;
        if (rotation == RotationMode.Smooth && lastPlaced != null && groundTicks <= 3L) {
            yaw = aimYaw;
            pitch = aimPitch;
            f7 = smoothLimit;
            if (smoothSnap) {
                smoothSnap = false;
                f7 = 81.0f + randomFloat(-2.0f, 2.0f);
            }
            f8 = MathHelper.wrapAngleTo180_float(yaw - f);
            if (Math.abs(f8) > f7) {
                yaw = f + (!(f8 > 0.0f) ? -f7 : f7);
            }
            return RotationUtils.fixSensitivity(yaw, pitch, f, f2);
        }
        if (aimPitch != 100.0f && l < holdTime()) {
            yaw = aimYaw;
            pitch = aimPitch;
            if (l > 5L && MoveUtils.isMoving() && Math.random() > 0.3) {
                yaw = (float) ((double) yaw + (Math.random() * 0.45 + 0.05) * (Math.random() < 0.5 ? -1 : 1));
                pitch += RotationUtils.randomOffset();
            }
            float[] r = RotationUtils.fixSensitivity(yaw, pitch, f, f2);
            yaw = r[0];
            pitch = r[1];
        } else {
            if (aimPitch != 100.0f) {
                aimPitch = 100.0f;
            }
            int n = mc.player.ticksExisted - startTick;
            f8 = MoveUtils.movementAngle();
            f9 = rotation != RotationMode.Simple ? restPitch : 82.5f;
            f8 += (float) yawOffset;
            f9 += RotationUtils.randomOffset();
            if (n <= 2 && !mc.player.onGround && mc.player.ticksExisted - jumpTick > 3) {
                BlockPos target = search(null);
                if (target != null) {
                    EnumFacing facing = faceFor(target, null);
                    if (facing != null && facing != EnumFacing.UP) {
                        BlockPos against = target.offset(facing.getOpposite());
                        float[] r = RotationUtils.searchBlockRotation(against, facing, -170.0f, getPrecision(), f, false);
                        if (r != null) {
                            f8 = r[0];
                            f9 = r[1];
                        }
                    }
                }
            } else if (rotation == RotationMode.Offset) {
                f8 = aimYaw;
            }
            yaw = f8;
            pitch = f9;
            if (n <= 2) {
                f10 = n != 0 ? (n != 2 ? 0.8f : 0.55f) : 0.3f;
                f10 = (float) ((double) f10 + (Math.random() * 0.04 - 0.02));
                yaw = f + f10 * (f8 - f);
                pitch = f2 + f10 * (f9 - f2);
                if (Math.abs(yaw - f8) < 5.0f) {
                    delay = 0;
                }
            }
            float[] r = RotationUtils.fixSensitivity(yaw, pitch, f, f2);
            yaw = r[0];
            if (yaw != f) {
                pitch = r[1];
                yawOffset = RotationUtils.randomOffset();
            } else {
                pitch = f2;
            }
        }
        return new float[]{yaw, pitch};
    }

    private void afterPlace(BlockPos pos, ItemStack itemStack) {
        FastScaffoldMode fastScaffold = getFastScaffoldMode();
        if (fastScaffold == FastScaffoldMode.Side && fastScaffold.canFastScaffold(this) && (fastScaffold.ticks == -1 || ++fastScaffold.ticks == 2 || fastScaffold.ticks >= 5)) {
            if (fastScaffold.ticks >= 5) {
                fastScaffold.ticks = 0;
            }
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
                BlockPos placedPos = BlockUtils.placedPos(hit);
                if (placedPos != null && BlockUtils.isReplaceable(placedPos)) {
                    placePacket(hit, itemStack);
                    if (silentSwing.get()) {
                        sendPacketNoEvent(new C0APacketAnimation());
                    } else {
                        mc.player.swingItem();
                    }
                    placedAt = System.currentTimeMillis();
                    placed.put(pos, placedAt);
                    if (fastScaffold.ticks == -1) {
                        fastScaffold.ticks = 0;
                    }
                    return;
                }
            }
            fastScaffold.ticks = -1;
        }
        MultiPlaceMode multiPlace = getMultiPlaceMode();
        if (multiPlace != MultiPlaceMode.Disabled && itemStack.stackSize > 0 && (towering || fastScaffold.canFastScaffold(this)) && placeExtra(itemStack)
                && multiPlace == MultiPlaceMode._2_extra && itemStack.stackSize > 0) {
            placeExtra(itemStack);
        }
    }

    private boolean placeExtra(ItemStack itemStack) {
        BlockPos target = search(lastPlaced);
        if (target == null) {
            return false;
        }
        EnumFacing facing = faceFor(target, lastPlaced);
        if (facing == null) {
            return false;
        }
        BlockPos against = target.offset(facing.getOpposite());
        float[] r = RotationUtils.searchBlockRotation(against, facing, -170.0f, getPrecision(), aimYaw, false);
        if (r == null) {
            return false;
        }
        MovingObjectPosition hit = rayTraceBlock(against, null, mc.playerController.getBlockReachDistance(), r[0], r[1]);
        if (hit == null) {
            return false;
        }
        placePacket(hit, itemStack);
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

    private void placePacket(MovingObjectPosition hit, ItemStack itemStack) {
        float fx = (float) (hit.hitVec.xCoord - (double) hit.getBlockPos().getX());
        float fy = (float) (hit.hitVec.yCoord - (double) hit.getBlockPos().getY());
        float fz = (float) (hit.hitVec.zCoord - (double) hit.getBlockPos().getZ());
        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(hit.getBlockPos(), hit.facing.getIndex(), itemStack, fx, fy, fz));
        if (!mc.player.abilities.isCreative()) {
            itemStack.onItemUse(mc.player, mc.world, hit.getBlockPos(), hit.facing, fx, fy, fz);
        }
    }

    public MovingObjectPosition rayTraceBlock(BlockPos pos, EnumFacing facing, double range, float yaw, float pitch) {
        Vec3 from = new Vec3(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
        Vec3 dir = RotationUtils.getVectorForRotation(yaw, pitch);
        Vec3 to = from.addVector(dir.xCoord * range, dir.yCoord * range, dir.zCoord * range);
        MovingObjectPosition hit = mc.world.rayTraceBlocks(from, to, false, false, false);
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return null;
        }
        if (!hit.getBlockPos().equals(pos) || (facing != null && hit.facing != facing)) {
            return null;
        }
        return hit;
    }

    private long holdTime() {
        if (mc.player.isSprinting()) return 710L;
        if (!MoveUtils.isMoving()) return 460L;
        return 110L;
    }

    private long sincePlace() {
        return Math.abs(System.currentTimeMillis() - placedAt);
    }

    private float randomFloat(float a, float b) {
        return (float) ((double) a + (double) (b - a) * Math.random());
    }

    public boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        ItemBlock itemBlock = (ItemBlock) item;
        Block block = itemBlock.getBlock();
        return BlockUtils.isPlaceable(block);
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
            if (!(item instanceof ItemBlock) || !BlockUtils.isPlaceable(((ItemBlock) item).getBlock())) {
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
            long age = now - Math.abs(entry.getValue());
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
        if (!isEnabled() || !active) return false;
        if (safeWalk.get()) {
            FastScaffoldMode fast = getFastScaffoldMode();
            if (fast.jump || fast.keepY) {
                return !fast.canFastScaffold(this);
            }
            return true;
        }
        FastScaffoldMode fast = getFastScaffoldMode();
        return (fast == FastScaffoldMode.Float_A || fast == FastScaffoldMode.Float_B) && fast.canFastScaffold(this);
    }
    //endregion
}
