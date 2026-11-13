package net.optifine.shaders.config;

import net.optifine.Lang;

public class PropertyDefaultTrueFalse extends Property {
   public static final String[] PROPERTY_VALUES = new String[]{"Default", "ON", "OFF"};
   public static final String[] USER_VALUES = new String[]{"default", "true", "false"};

   public PropertyDefaultTrueFalse(String text, String text2) {
      super(text, USER_VALUES, text2, PROPERTY_VALUES);
   }

   public boolean isDefault() {
      return this.getValue() == 0;
   }

   public boolean isTrue() {
      return this.getValue() == 1;
   }

   public boolean isFalse() {
      return this.getValue() == 2;
   }

   @Override
   public String getUserValue() {
      return this.isDefault() ? Lang.getDefault() : (this.isTrue() ? Lang.getOn() : (this.isFalse() ? Lang.getOff() : super.getUserValue()));
   }
}
