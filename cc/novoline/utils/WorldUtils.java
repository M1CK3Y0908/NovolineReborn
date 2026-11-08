package cc.novoline.utils;

import net.minecraft.util.Vec3;

public final class WorldUtils {

    private WorldUtils() {}

    public static double distanceSq(double x, double y, double z, Vec3 p) {
        double d4 = x - p.xCoord;
        double d5 = y - p.yCoord;
        double d6 = z - p.zCoord;
        return d4 * d4 + d5 * d5 + d6 * d6;
    }

    public static double behind(double x, double z, double dirX, double dirZ, Vec3 p) {
        double d5 = p.xCoord - x;
        double d6 = p.zCoord - z;
        return -(d5 * dirX + d6 * dirZ);
    }
}
