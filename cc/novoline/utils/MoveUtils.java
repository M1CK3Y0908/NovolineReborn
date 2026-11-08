package cc.novoline.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class MoveUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    private MoveUtils() {
    }

    public enum MoveDirection {
        NONE(false, false, false),
        FORWARD(true, false, false),
        FORWARD_LEFT(true, true, false),
        FORWARD_RIGHT(true, true, false),
        BACK(false, false, true),
        BACK_LEFT(false, true, true),
        BACK_RIGHT(false, true, true),
        LEFT(false, false, false),
        RIGHT(false, false, false);

        public final boolean forward;
        public final boolean diagonal;
        public final boolean backward;

        MoveDirection(boolean forward, boolean diagonal, boolean backward) {
            this.forward = forward;
            this.diagonal = diagonal;
            this.backward = backward;
        }
    }

    public static float forwardInput() {
        float f = 0.0f;
        if (mc.gameSettings.keyBindForward.isKeyDown()) f += 1.0f;
        if (mc.gameSettings.keyBindBack.isKeyDown()) f -= 1.0f;
        return f;
    }

    public static float strafeInput() {
        float f = 0.0f;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) f += 1.0f;
        if (mc.gameSettings.keyBindRight.isKeyDown()) f -= 1.0f;
        return f;
    }

    public static boolean isMoving() {
        return mc.player.movementInput().getMoveForward() != 0.0f || mc.player.movementInput().getMoveStrafe() != 0.0f;
    }

    public static boolean jumpKeyDown() {
        return mc.gameSettings.keyBindJump.isKeyDown()
                || (mc.gameSettings.keyBindJump.getKeyCode() < 0 ? Mouse.isButtonDown(mc.gameSettings.keyBindJump.getKeyCode() + 100) : Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
    }

    public static double inputAngle() {
        double d = Math.toDegrees(Math.atan2(strafeInput(), -forwardInput()));
        return (d + 360.0) % 360.0;
    }

    public static double inputAngleSigned() {
        double d = Math.toDegrees(Math.atan2(-strafeInput(), forwardInput()));
        return d == 0.0 ? 0.0 : d;
    }

    public static float movementAngle() {
        return mc.player.rotationYaw + (float) inputAngle();
    }

    public static MoveDirection direction(double forward, double strafe) {
        if (forward == 0.0) {
            if (strafe == 0.0) return MoveDirection.NONE;
            if (strafe > 0.0) return MoveDirection.LEFT;
            return MoveDirection.RIGHT;
        } else if (forward > 0.0) {
            if (strafe == 0.0) return MoveDirection.FORWARD;
            if (strafe > 0.0) return MoveDirection.FORWARD_LEFT;
            return MoveDirection.FORWARD_RIGHT;
        } else {
            if (strafe == 0.0) return MoveDirection.BACK;
            if (strafe > 0.0) return MoveDirection.BACK_LEFT;
            return MoveDirection.BACK_RIGHT;
        }
    }

    public static MoveDirection direction() {
        return direction(forwardInput(), strafeInput());
    }

    public static boolean diagonalYaw() {
        float f = Math.abs(mc.player.rotationYaw % 90.0f);
        return f > 10.0f && f < 80.0f;
    }

    public static boolean isDiagonal() {
        return diagonalYaw() || direction().diagonal;
    }

    public static float moveYaw(float yaw, float forward, float strafe) {
        float f4 = 1.0f;
        if (forward < 0.0f) {
            yaw += 180.0f;
            f4 = -0.5f;
        } else if (forward > 0.0f) {
            f4 = 0.5f;
        }
        if (strafe > 0.0f) {
            yaw -= 90.0f * f4;
        } else if (strafe < 0.0f) {
            yaw += 90.0f * f4;
        }
        return yaw;
    }

    public static float moveYaw(float yaw) {
        return moveYaw(yaw, mc.player.movementInput().getMoveForward(), mc.player.movementInput().getMoveStrafe());
    }

    public static double[] movementVector() {
        if (!isMoving()) return new double[]{0.0, 0.0};
        float yaw = moveYaw(mc.player.rotationYaw);
        double dx = -Math.sin(Math.toRadians(yaw));
        double dz = Math.cos(Math.toRadians(yaw));
        return new double[]{dx, dz};
    }

    public static void setSpeed(double speed, float yaw) {
        if (speed == 0.0) {
            mc.player.motionZ = 0.0;
            mc.player.motionX = 0.0;
            return;
        }
        mc.player.motionX = -Math.sin(Math.toRadians(yaw)) * speed;
        mc.player.motionZ = Math.cos(Math.toRadians(yaw)) * speed;
    }

    public static void setSpeed(double speed) {
        float yaw = mc.player.rotationYaw;
        if (mc.player.movementInput().getMoveForward() < 0.0f) {
            yaw += 180.0f;
        }
        setSpeed(speed, yaw);
    }

    public static void strafe(double speed) {
        setSpeed(speed, moveYaw(mc.player.rotationYaw));
    }

    public static void strafe(double speed, float yaw) {
        setSpeed(speed, moveYaw(yaw));
    }

    public static double speed() {
        return Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static int speedLevel() {
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            return mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }

    public static double perSpeedLevel(double amount) {
        return (double) speedLevel() * amount;
    }

    public static int posYDecimals() {
        return (int) Math.round(mc.player.posY % 1.0 * 10000.0);
    }

    public static int motionYDecimals() {
        return (int) Math.round(mc.player.motionY % 10.0 * 10000.0);
    }

    public static boolean nearEdge(double margin) {
        int x = MathHelper.floor_double(mc.player.posX);
        int y = MathHelper.floor_double(mc.player.posY - 0.01);
        int z = MathHelper.floor_double(mc.player.posZ);
        BlockPos pos = new BlockPos(x, y, z);
        if (mc.world.isAirBlock(pos)) {
            return true;
        }
        double fx = mc.player.posX - (double) x;
        double fz = mc.player.posZ - (double) z;
        return (fx < margin && mc.world.isAirBlock(new BlockPos(x - 1, y, z)))
                || (fx > 1.0 - margin && mc.world.isAirBlock(new BlockPos(x + 1, y, z)))
                || (fz < margin && mc.world.isAirBlock(new BlockPos(x, y, z - 1)))
                || (fz > 1.0 - margin && mc.world.isAirBlock(new BlockPos(x, y, z + 1)));
    }

    public static boolean isAirBelow(double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y - (double) (y % 1.0 == 0.0 ? 1 : 0), z);
        return mc.world.isAirBlock(pos);
    }

    public static boolean isAirBelow() {
        return isAirBelow(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    public static void jump(double speed) {
        strafe(speed);
        mc.player.jump();
    }

    public static void multiplyMotion(double mult) {
        mc.player.motionX *= mult;
        mc.player.motionZ *= mult;
    }
}
