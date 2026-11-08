package net.shadersmod.uniform;

import java.util.HashMap;
import java.util.Map;
import net.optifine.SmoothFloat;

public class Smoother {
    private static Map<Integer, SmoothFloat> mapSmoothValues = new HashMap<>();

    public static float getSmoothValue(int id, float value, float timeFadeUpSec, float timeFadeDownSec) {
        synchronized (mapSmoothValues) {
            Integer integer = id;
            SmoothFloat smoothfloat = mapSmoothValues.get(integer);
            if (smoothfloat == null) {
                smoothfloat = new SmoothFloat(value, timeFadeUpSec, timeFadeDownSec);
                mapSmoothValues.put(integer, smoothfloat);
            }

            return smoothfloat.getSmoothValue(value);
        }
    }

    public static void reset() {
        synchronized (mapSmoothValues) {
            mapSmoothValues.clear();
        }
    }
}
