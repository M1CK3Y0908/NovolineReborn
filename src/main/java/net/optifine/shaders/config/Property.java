package net.optifine.shaders.config;

import java.util.Properties;
import net.minecraft.src.Config;
import org.apache.commons.lang3.ArrayUtils;

public class Property {
   private int defaultValue = 0;
   private String propertyName = null;
   private String[] propertyValues = null;
   private String userName = null;
   private String[] userValues = null;
   private int value = 0;

   public boolean loadFrom(Properties props) {
      this.resetValue();
      if (props == null) {
         return false;
      }

      String propertyValue = props.getProperty(this.propertyName);
      return propertyValue == null ? false : this.setPropertyValue(propertyValue);
   }

   public Property(String text, String[] texts, String text2, String[] texts2) {
      this.propertyName = text;
      this.propertyValues = texts;
      this.userName = text2;
      this.userValues = texts2;
      this.defaultValue = 0;
      if (texts.length != texts2.length) {
         throw new IllegalArgumentException("Property and user values have different lengths: " + texts.length + " != " + texts2.length);
      }

      if (0 < texts.length) {
         this.value = 0;
      } else {
         throw new IllegalArgumentException("Invalid default value: " + 0);
      }
   }

   public String getPropertyValue() {
      return this.propertyValues[this.value];
   }

   public void nextValue(boolean forward) {
      int i = this.propertyValues.length - 1;
      this.value = Config.limit(this.value, 0, i);
      if (forward) {
         this.value++;
         if (this.value > i) {
            this.value = 0;
         }
      } else {
         this.value--;
         if (this.value < 0) {
            this.value = i;
         }
      }
   }

   public String getUserName() {
      return this.userName;
   }

   public boolean setPropertyValue(String propVal) {
      if (propVal == null) {
         this.value = this.defaultValue;
         return false;
      }

      this.value = ArrayUtils.indexOf(this.propertyValues, propVal);
      if (this.value >= 0 && this.value < this.propertyValues.length) {
         return true;
      }

      this.value = this.defaultValue;
      return false;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public int getValue() {
      return this.value;
   }

   public void resetValue() {
      this.value = this.defaultValue;
   }

   public String getUserValue() {
      return this.userValues[this.value];
   }

   @Override
   public String toString() {
      return this.propertyName + "=" + this.getPropertyValue() + " [" + Config.arrayToString(this.propertyValues) + "], value: " + this.value;
   }
}
