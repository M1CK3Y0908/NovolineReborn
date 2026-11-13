package net.minecraft.util;

public class StatCollector {
   private static StringTranslate localizedName = StringTranslate.getInstance();
   private static StringTranslate fallbackTranslator = new StringTranslate();

   public static long getLastTranslationUpdateTimeInMilliseconds() {
      return localizedName.getLastUpdateTimeInMilliseconds();
   }

   public static String translateToLocalFormatted(String key, Object... format) {
      return localizedName.translateKeyFormat(key, format);
   }

   public static boolean canTranslate(String key) {
      return localizedName.isKeyTranslated(key);
   }

   public static String translateToFallback(String key) {
      return fallbackTranslator.translateKey(key);
   }

   public static String translateToLocal(String key) {
      return localizedName.translateKey(key);
   }
}
