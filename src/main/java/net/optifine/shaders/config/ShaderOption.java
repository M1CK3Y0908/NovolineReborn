package net.optifine.shaders.config;

import java.util.Arrays;
import java.util.List;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;
import net.optifine.util.StrUtils;

public abstract class ShaderOption {
   private String name = null;
   private String description = null;
   public static final String text = "§a";
   private String value = null;
   public static final String text2 = "§c";
   private String[] values = null;
   private String valueDefault = null;
   private String[] paths = null;
   private boolean enabled = true;
   private boolean visible = true;
   public static final String text3 = "§9";

   public String getValueColor(String val) {
      return "";
   }

   public String getDescriptionText() {
      String text4 = Config.normalize(this.description);
      text4 = StrUtils.removePrefix(text4, "//");
      return Shaders.translate("option." + this.getName() + ".comment", text4);
   }

   public void nextValue() {
      int index2 = getIndex(this.value, this.values);
      if (index2 >= 0) {
         index2 = (index2 + 1) % this.values.length;
         this.value = this.values[index2];
      }
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public String[] getValues() {
      return (String[])this.values.clone();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getSourceLine() {
      return null;
   }

   public void setIndexNormalized(float f) {
      if (this.values.length > 1) {
         f = Config.limit(f, 0.0F, 1.0F);
         int count = Math.round(f * (this.values.length - 1));
         this.value = this.values[count];
      }
   }

   public boolean matchesLine(String line) {
      return false;
   }

   public boolean isValidValue(String val) {
      return getIndex(val, this.values) >= 0;
   }

   public String getValueDefault() {
      return this.valueDefault;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean setValue(String value) {
      if (getIndex(value, this.values) < 0) {
         return false;
      }

      this.value = value;
      return true;
   }

   public boolean checkUsed() {
      return false;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public String getValueText(String val) {
      return Shaders.translate("value." + this.name + "." + val, val);
   }

   public float getIndexNormalized() {
      if (this.values.length <= 1) {
         return 0.0F;
      }

      int index2 = getIndex(this.value, this.values);
      return index2 < 0 ? 0.0F : 1.0F * index2 / (this.values.length - 1.0F);
   }

   public String getValue() {
      return this.value;
   }

   public void prevValue() {
      int index2 = getIndex(this.value, this.values);
      if (index2 >= 0) {
         index2 = (index2 - 1 + this.values.length) % this.values.length;
         this.value = this.values[index2];
      }
   }

   public String getNameText() {
      return Shaders.translate("option." + this.name, this.name);
   }

   @Override
   public String toString() {
      return this.name + ", value: " + this.value + ", valueDefault: " + this.valueDefault + ", paths: " + Config.arrayToString(this.paths);
   }

   public boolean isUsedInLine(String line) {
      return false;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void addPaths(String[] newPaths) {
      List<String> list = Arrays.asList(this.paths);

      for (int index = 0; index < newPaths.length; index++) {
         String text4 = newPaths[index];
         if (!list.contains(text4)) {
            this.paths = (String[])Config.addObjectToArray(this.paths, text4);
         }
      }
   }

   public boolean isChanged() {
      return !Config.equals(this.value, this.valueDefault);
   }

   public void resetValue() {
      this.value = this.valueDefault;
   }

   public ShaderOption(String name, String description, String value, String[] values, String valueDefault, String path) {
      this.name = name;
      this.description = description;
      this.value = value;
      this.values = values;
      this.valueDefault = valueDefault;
      if (path != null) {
         this.paths = new String[]{path};
      }
   }

   private static int getIndex(String str, String[] strs) {
      for (int i = 0; i < strs.length; i++) {
         if (strs[i].equals(str)) {
            return i;
         }
      }

      return -1;
   }

   public String[] getPaths() {
      return this.paths;
   }

   public void update() {
      this.enabled = false;
   }
}
