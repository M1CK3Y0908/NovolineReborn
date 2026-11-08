package net.optifine;

import net.minecraft.util.MathHelper;

public class MathUtils {
    public static int getAverage(int[] vals) {
        if (vals.length <= 0) {
            return 0;
        }

        int i = getSum(vals);
        return i / vals.length;
    }

    public static int getSum(int[] vals) {
        if (vals.length <= 0) {
            return 0;
        }

        int i = 0;

        for (int j = 0; j < vals.length; j++) {
            int k = vals[j];
            i += k;
        }

        return i;
    }

    public static int roundDownToPowerOfTwo(int val) {
        int i = MathHelper.roundUpToPowerOfTwo(val);
        return val == i ? i : i / 2;
    }

    public static boolean equalsDelta(float f1, float f2, float delta) {
        return Math.abs(f1 - f2) <= delta;
    }

    public static float toDeg(float angle) {
        return angle * 180.0F / (float)Math.PI;
    }

    public static float toRad(float angle) {
        return angle / 180.0F * (float)Math.PI;
    }
}
