package net.optifine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.src.Config;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomLoadingScreens {
   private static CustomLoadingScreen[] screens = null;
   private static int screensMinDimensionId = 0;

   public static void warn(String str) {
      Config.warn("CustomLoadingScreen: " + str);
   }

   private static Pair<CustomLoadingScreen[], Integer> parseScreens() {
      String[] texts = ResUtils.collectFiles("optifine/gui/loading/background", ".png");
      HashMap hashMap = new HashMap();

      for (int i = 0; i < texts.length; i++) {
         String text = texts[i];
         String text2 = StrUtils.removePrefixSuffix(text, "optifine/gui/loading/background", ".png");
         int i2 = Config.parseInt(text2, Integer.MIN_VALUE);
         if (i2 == Integer.MIN_VALUE) {
            warn("Invalid dimension ID: " + text2 + ", path: " + text);
         } else {
            hashMap.put(i2, text);
         }
      }

      Set set = hashMap.keySet();
      Integer[] integers = (Integer[]) set.toArray(new Integer[set.size()]);
      Arrays.sort(integers);
      if (integers.length <= 0) {
         return new ImmutablePair<>(null, 0);
      }

      Properties properties2 = ResUtils.readProperties("optifine/gui/loading/loading.properties", "CustomLoadingScreens");
      int i3 = integers[0];
      CustomLoadingScreen[] customLoadingScreens = new CustomLoadingScreen[integers[integers.length - 1] - i3 + 1];

      for (int i4 = 0; i4 < integers.length; i4++) {
         Integer integer = integers[i4];
         String text3 = (String)hashMap.get(integer);
         customLoadingScreens[integer - i3] = CustomLoadingScreen.parseScreen(text3, integer, properties2);
      }

      return new ImmutablePair<>(customLoadingScreens, i3);
   }

   public static void update() {
      screens = null;
      screensMinDimensionId = 0;
      Pair<CustomLoadingScreen[], Integer> pair = parseScreens();
      screens = pair.getLeft();
      screensMinDimensionId = pair.getRight();
   }

   public static CustomLoadingScreen getCustomLoadingScreen() {
      if (screens == null) {
         return null;
      }

      int i = PacketThreadUtil.lastDimensionId - screensMinDimensionId;
      CustomLoadingScreen customLoadingScreen = null;
      if (i >= 0 && i < screens.length) {
         customLoadingScreen = screens[i];
      }

      return customLoadingScreen;
   }
}
