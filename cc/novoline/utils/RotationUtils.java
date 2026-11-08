package cc.novoline.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.List;

public final class RotationUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    public static float lastYawStep;
    public static float lastPitchStep;

    private RotationUtils() {
    }

    public enum Precision {
        Low(40.0f),
        Medium(20.0f),
        High(5.0f),
        Very_high(1.5f);

        public final float offsetYaw;

        Precision(float offsetYaw) {
            this.offsetYaw = offsetYaw;
        }

        public float getOffsetYaw() {
            return this.offsetYaw;
        }

        public float getOffsetPitch(float pitch) {
            if (pitch > 75.0f) {
                return 1.0f;
            }
            if (pitch > 60.0f) {
                return 2.0f;
            }
            return 5.0f;
        }
    }

    public static float clampPitch(float pitch) {
        return MathHelper.clamp_float(pitch, -90.0f, 90.0f);
    }

    public static float[] fixSensitivity(float yaw, float pitch, float lastYaw, float lastPitch, boolean jitter) {
        float f5 = yaw - lastYaw;
        float f6 = Math.abs(f5);
        float f7 = pitch - lastPitch;
        float f8 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        double d = (double) (f8 * f8 * f8) * 1.2;
        float f9 = lastYawStep = (float) ((double) Math.round((double) f5 / d) * d);
        float f10 = lastPitchStep = (float) ((double) Math.round((double) f7 / d) * d);
        yaw = lastYaw + f9;
        pitch = lastPitch + f10;
        return new float[]{yaw, clampPitch(pitch)};
    }

    public static float[] fixSensitivity(float yaw, float pitch, float lastYaw, float lastPitch) {
        return fixSensitivity(yaw, pitch, lastYaw, lastPitch, false);
    }

    public static float randomOffset() {
        return (float) (Math.random() - 0.5);
    }

    public static float yawBetween(double fromX, double fromZ, double toX, double toZ) {
        double d7 = Math.atan2(toX - fromX, toZ - fromZ) * (double) 57.29578f;
        return (float) (-d7);
    }

    public static float yawTo(double x, double z) {
        return yawBetween(mc.player.posX, mc.player.posZ, x, z);
    }

    public static double yawDifference(Entity entity, float currentYaw) {
        float f2 = yawTo(entity.posX, entity.posZ);
        return MathHelper.wrapAngleTo180_double((double) (f2 - currentYaw));
    }

    public static double absYawDifference(Entity entity, float currentYaw) {
        return Math.abs(yawDifference(entity, currentYaw));
    }

    public static Vec3 getVectorForRotation(float yaw, float pitch) {
        float f3 = MathHelper.cos((yaw *= -((float) Math.PI / 180)) - (float) Math.PI);
        float f4 = MathHelper.sin(yaw - (float) Math.PI);
        float f5 = -MathHelper.cos(pitch *= -((float) Math.PI / 180));
        float f6 = MathHelper.sin(pitch);
        return new Vec3(f4 * f5, f6, f3 * f5);
    }

    public static float[] fromDelta(double dx, double dy, double dz) {
        double d4 = Math.sqrt(dx * dx + dz * dz);
        float f = (float) (Math.atan2(dz, dx) * (double) 57.29578f) - 90.0f;
        float f2 = (float) (-(Math.atan2(dy, d4) * (double) 57.29578f));
        float curYaw = mc.player.rotationYaw;
        float curPitch = mc.player.rotationPitch;
        f = curYaw + MathHelper.wrapAngleTo180_float(f - curYaw);
        f2 = clampPitch(curPitch + MathHelper.wrapAngleTo180_float(f2 - curPitch));
        return new float[]{f, f2};
    }

    public static float[] toVec(Vec3 vec3) {
        double x = mc.player.posX;
        double y = mc.player.posY + (double) mc.player.getEyeHeight();
        double z = mc.player.posZ;
        if (vec3.xCoord == x && vec3.yCoord == y && vec3.zCoord == z) {
            return new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        }
        return fromDelta(vec3.xCoord - x, vec3.yCoord - y, vec3.zCoord - z);
    }

    public static float[] toEntityBox(Entity entity) {
        if (entity == null) {
            return null;
        }
        return toBox(entity, entity.getEntityBoundingBox());
    }

    public static float[] toBox(Entity entity, AxisAlignedBB box) {
        if (entity == null || box == null) {
            return null;
        }
        Vec3 eyes = mc.player.getPositionEyes(1.0f);
        float f = entity.getCollisionBorderSize();
        box = box.expand(f, f, f);
        if (box.isVecInside(eyes)) {
            return new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        }
        Vec3 p = aimPoint(eyes, box);
        return fromDelta(p.xCoord - eyes.xCoord, p.yCoord - eyes.yCoord, p.zCoord - eyes.zCoord);
    }

    public static Vec3 aimPoint(Vec3 eyes, AxisAlignedBB box) {
        double cx = (box.minX + box.maxX) / 2.0;
        double cy = box.minY + (box.maxY - box.minY) * 0.825;
        double cz = (box.minZ + box.maxZ) / 2.0;

        double d5 = 0.03;
        double px = MathHelper.clamp_double(eyes.xCoord, box.minX + d5, box.maxX - d5);
        double py = MathHelper.clamp_double(eyes.yCoord, box.minY + d5, box.maxY - d5);
        double pz = MathHelper.clamp_double(eyes.zCoord, box.minZ + d5, box.maxZ - d5);

        return new Vec3(px, py, pz);
    }

    public static boolean clearLine(Vec3 from, Vec3 to) {
        MovingObjectPosition hit = mc.world.rayTraceBlocks(from, to, false, true, false);
        return hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK;
    }

    public static Vec3 visiblePoint(Entity entity) {
        Vec3 eyes = mc.player.getPositionEyes(1.0f);
        float f = entity.getCollisionBorderSize();
        AxisAlignedBB box = entity.getEntityBoundingBox().expand(f, f, f);
        if (box.isVecInside(eyes)) {
            return eyes;
        }
        Vec3 p = aimPoint(eyes, box);
        if (clearLine(eyes, p)) {
            return p;
        }
        return fallbackPoint(entity, eyes);
    }

    public static Vec3 fallbackPoint(Entity entity, Vec3 eyes) {
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;
        double eye = entity.getEyeHeight();
        double h1 = eye - 0.2;
        double h2 = eye - 0.9;
        double h3 = eye - 1.5;
        double o = 0.28;
        Vec3[] points = {
                new Vec3(x, y + (eye + 0.2), z),
                new Vec3(x, y + (eye - 0.6), z),
                new Vec3(x, y + h2, z),
                new Vec3(x, y + (eye - 1.2), z),
                new Vec3(x, y + h3, z),
                new Vec3(x + o, y + h1, z + o),
                new Vec3(x + o, y + h1, z - o),
                new Vec3(x - o, y + h1, z + o),
                new Vec3(x - o, y + h1, z - o),
                new Vec3(x + o, y + h2, z + o),
                new Vec3(x + o, y + h2, z - o),
                new Vec3(x - o, y + h2, z + o),
                new Vec3(x - o, y + h2, z - o),
                new Vec3(x + o, y + h3, z + o),
                new Vec3(x + o, y + h3, z - o),
                new Vec3(x - o, y + h3, z + o),
                new Vec3(x - o, y + h3, z - o),
        };
        for (Vec3 p : points) {
            if (clearLine(eyes, p)) {
                return p;
            }
        }
        return null;
    }

    public static boolean canSee(Entity entity) {
        Vec3 eyes = mc.player.getPositionEyes(1.0f);
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;
        double h = (double) entity.getEyeHeight() - 0.2;
        if (clearLine(eyes, new Vec3(x + 0.3, y + h, z))) {
            return true;
        }
        if (clearLine(eyes, new Vec3(x - 0.3, y + h, z))) {
            return true;
        }
        if (clearLine(eyes, new Vec3(x, y + h, z + 0.3))) {
            return true;
        }
        if (clearLine(eyes, new Vec3(x, y + h, z - 0.3))) {
            return true;
        }
        for (double d = (double) entity.getEyeHeight() + 0.2; d > 0.0; d -= 0.2) {
            if (clearLine(eyes, new Vec3(x, y + d, z))) {
                return true;
            }
        }
        return false;
    }

    public static boolean rayHits(Entity entity, AxisAlignedBB box, float yaw, float pitch, double range) {
        if (entity == null || !entity.canBeCollidedWith()) {
            return false;
        }
        Vec3 from = mc.player.getPositionEyes(1.0f);
        Vec3 dir = getVectorForRotation(yaw, pitch);
        Vec3 to = from.addVector(dir.xCoord * range, dir.yCoord * range, dir.zCoord * range);
        float f = entity.getCollisionBorderSize();
        box = box.expand(f, f, f);
        return box.calculateIntercept(from, to) != null;
    }

    public static boolean rayHits(Entity entity, float yaw, float pitch, double range) {
        if (entity == null) {
            return false;
        }
        return rayHits(entity, entity.getEntityBoundingBox(), yaw, pitch, range);
    }

    public static boolean rayHits(Entity entity, double range) {
        float[] r = toEntityBox(entity);
        if (r == null) return false;
        return rayHits(entity, r[0], r[1], range);
    }

    public static MovingObjectPosition rayTraceEntity(Entity entity, double reach, float yaw, float pitch) {
        if (entity == null) return null;
        Vec3 eyes = mc.player.getPositionEyes(1.0f);
        Vec3 dir = getVectorForRotation(yaw, pitch);
        Vec3 to = eyes.addVector(dir.xCoord * reach, dir.yCoord * reach, dir.zCoord * reach);
        float f = entity.getCollisionBorderSize();
        AxisAlignedBB box = entity.getEntityBoundingBox().expand(f, f, f);
        MovingObjectPosition intercept = box.calculateIntercept(eyes, to);
        return intercept;
    }

    public static float[] searchBlockRotation(BlockPos pos, EnumFacing facing, float offset, Precision precision, float airYaw, boolean playerYaw) {
        float bestYaw = 0.0f;
        float bestPitch = 0.0f;
        float base = playerYaw ? mc.player.rotationYaw : (mc.player.onGround ? MoveUtils.movementAngle() : airYaw);
        float from = base + offset;
        float to = base + 170.0f;
        float reach = mc.playerController.getBlockReachDistance();
        float best = 360.0f;
        for (float yaw = from; yaw <= to; yaw += precision.getOffsetYaw()) {
            float y = (float) ((double) yaw + (Math.random() - 0.5));
            for (float pitch = 90.0f; pitch > 30.0f; pitch -= precision.getOffsetPitch(pitch)) {
                float p = MathHelper.clamp_float((float) ((double) pitch + (Math.random() - 0.5)), -90.0f, 90.0f);
                Vec3 eyes = mc.player.getPositionEyes(1.0f);
                Vec3 dir = getVectorForRotation(y, p);
                Vec3 end = eyes.addVector(dir.xCoord * reach, dir.yCoord * reach, dir.zCoord * reach);
                MovingObjectPosition hit = mc.world.rayTraceBlocks(eyes, end, false, false, false);
                if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) continue;
                if (facing != null && hit.facing != facing) continue;
                BlockPos hitPos = hit.getBlockPos();
                if (!hitPos.equals(pos)) continue;
                if (!awayFromEdge((float) (hit.hitVec.yCoord - (double) hitPos.getY()))) continue;
                if (!awayFromEdge((float) (hit.hitVec.xCoord - (double) hitPos.getX()))) continue;
                if (!awayFromEdge((float) (hit.hitVec.zCoord - (double) hitPos.getZ()))) continue;
                float diff = Math.abs(base - y);
                if (diff < best) {
                    best = diff;
                    bestYaw = y;
                    bestPitch = p;
                }
            }
        }
        if (best == 360.0f) {
            return null;
        }
        if (playerYaw && best > 70.0f) {
            return null;
        }
        return new float[]{bestYaw, bestPitch};
    }

    public static boolean awayFromEdge(float f) {
        return f == 0.0f || f == 1.0f || ((double) f > 0.06 && (double) f < 0.94);
    }
}
