package cc.novoline.modules.combat;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.PacketEvent;
import cc.novoline.events.events.PlayerUpdateEvent;
import cc.novoline.events.events.TickUpdateEvent;
import cc.novoline.gui.screen.setting.Manager;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.gui.screen.setting.SettingType;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.BooleanProperty;
import cc.novoline.modules.configurations.property.object.IntProperty;
import cc.novoline.utils.MoveUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;

import static cc.novoline.modules.configurations.property.object.PropertyFactory.booleanFalse;
import static cc.novoline.modules.configurations.property.object.PropertyFactory.createInt;

public final class JumpReset extends AbstractModule {

    /* properties @off */
    @Property("chance")
    private final IntProperty chance = createInt(100).minimum(0).maximum(100);
    @Property("minimize-vertical")
    private final BooleanProperty minimizeVertical = booleanFalse();
    @Property("only-while-targeting")
    private final BooleanProperty onlyWhileTargeting = booleanFalse();

    /* fields */
    private boolean pending;
    private boolean explosion;
    private double kbX;
    private double kbZ;
    private int kbY;

    /* constructor */
    public JumpReset(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "JumpReset", "Jump Reset", Keyboard.KEY_NONE, EnumModuleType.COMBAT, "Reduces knockback by resetting it with jump");
        Manager.put(new Setting("JR_CHANCE", "Chance", SettingType.SLIDER, this, chance, 5));
        Manager.put(new Setting("JR_MIN_VER", "Minimize Vertical", SettingType.CHECKBOX, this, minimizeVertical));
        Manager.put(new Setting("JR_TARGETING", "Only While Targeting", SettingType.CHECKBOX, this, onlyWhileTargeting));
    }

    @Override
    public void onDisable() {
        explosion = false;
        pending = false;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getState() == PacketEvent.State.INCOMING) {
            if (event.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion p = (S27PacketExplosion) event.getPacket();
                if (mc.player.getDistanceSq(p.getX(), p.getY(), p.getZ()) <= 36.0) {
                    explosion = true;
                }
            } else if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity p = (S12PacketEntityVelocity) event.getPacket();
                if (p.getEntityID() == mc.player.getEntityID()) {
                    if (explosion) {
                        return;
                    }
                    if (minimizeVertical.get() && (long) p.getMotionY() < 3000L) {
                        return;
                    }
                    kbX = (double) p.getMotionX() / 8000.0;
                    kbY = p.getMotionY();
                    kbZ = (double) p.getMotionZ() / 8000.0;
                    pending = true;
                }
            }
        }
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (pending) {
            pending = false;
            if (!mc.player.onGround || !mc.player.isSprinting()) {
                return;
            }
            if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isRiding() || mc.player.isPotionActive(Potion.jump)) {
                return;
            }
            if (onlyWhileTargeting.get() && !isTargeting()) {
                return;
            }
            if (chance.get() != 100 && !(Math.random() < chance.get() / 100.0)) {
                return;
            }
            if (!helps()) {
                return;
            }
            mc.player.setJumping(true);
            mc.player.jump();
        }
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        explosion = false;
    }

    private boolean isTargeting() {
        KillAura killAura = getModule(KillAura.class);
        if (killAura != null && killAura.isEnabled() && killAura.getTarget() != null) {
            return true;
        }
        return mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase;
    }

    private boolean helps() {
        if (kbX == 0.0 && kbZ == 0.0) {
            return false;
        }
        double kb = Math.sqrt(kbX * kbX + kbZ * kbZ);
        float yaw = MathHelper.wrapAngleTo180_float(mc.player.rotationYaw);
        double after = withJump(kbX, kbZ, yaw);
        double less = (kb - after) / kb;
        return less >= 0.03;
    }

    private static double withJump(double x, double z, float yaw) {
        double r = Math.toRadians(yaw);
        double nx = x + -Math.sin(r) * 0.2;
        double nz = z + Math.cos(r) * 0.2;
        return Math.sqrt(nx * nx + nz * nz);
    }
}
