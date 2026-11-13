package net.optifine.shaders.uniform;

import java.util.HashMap;
import java.util.Map;
import net.optifine.util.CounterInt;
import net.optifine.util.SmoothFloat;

public class Smoother {
   private static Map<Integer, SmoothFloat> mapSmoothValues = new HashMap<>();
   private static CounterInt counterIds = new CounterInt(1);

   public static float getSmoothValue(int id, float value, float timeFadeUpSec, float timeFadeDownSec) {
      synchronized (mapSmoothValues) {
         Integer id2 = id;
         SmoothFloat smoothFloat = mapSmoothValues.get(id2);
         if (smoothFloat == null) {
            smoothFloat = new SmoothFloat(value, timeFadeUpSec, timeFadeDownSec);
            mapSmoothValues.put(id2, smoothFloat);
         }

         return smoothFloat.getSmoothValue(value, timeFadeUpSec, timeFadeDownSec);
      }
   }

   public static int getNextId() {
      synchronized (counterIds) {
         return counterIds.nextValue();
      }
   }

   public static void resetValues() {
      synchronized (mapSmoothValues) {
         mapSmoothValues.clear();
      }
   }
}
