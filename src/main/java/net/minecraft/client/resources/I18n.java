package net.minecraft.client.resources;

import java.util.Map;
import net.minecraft.entity.Entity;

public class I18n {
   private static Entity[] entityArray;
   private static Locale i18nLocale;

   public static String format(String translateKey, Object... parameters) {
      return i18nLocale.formatMessage(translateKey, parameters);
   }

   public static void handleArray(Entity[] entitys) {
      entityArray = entitys;
   }

   static void setLocale(Locale i18nLocaleIn) {
      i18nLocale = i18nLocaleIn;
   }

   public static Entity[] getEntityArray() {
      return entityArray;
   }

   public static Map getLocaleProperties() {
      return i18nLocale.properties;
   }
}
