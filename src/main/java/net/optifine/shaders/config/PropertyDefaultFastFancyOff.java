package net.optifine.shaders.config;

import net.minecraft.src.Config;

public class PropertyDefaultFastFancyOff extends Property {
   public static final String[] PROPERTY_VALUES = new String[]{"default", "fast", "fancy", "off"};
   public static final String[] USER_VALUES = new String[]{"Default", "Fast", "Fancy", "OFF"};

   public boolean isOff() {
      return this.getValue() == 3;
   }

   @Override
   public boolean setPropertyValue(String propVal) {
      if (Config.equals(propVal, "none")) {
         propVal = "off";
      }

      return super.setPropertyValue(propVal);
   }

   public PropertyDefaultFastFancyOff(String text, String text2) {
      super(text, PROPERTY_VALUES, text2, USER_VALUES);
   }

   public boolean isFancy() {
      return this.getValue() == 2;
   }

   public boolean isDefault() {
      return this.getValue() == 0;
   }
}
