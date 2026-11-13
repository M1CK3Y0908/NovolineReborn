package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.optifine.util.StrUtils;
import org.lwjgl.util.vector.Vector4f;

public class ShaderLine {
   public static final int TYPE_EXTENSION = 7;
   public static final int TYPE_ATTRIBUTE = 2;
   private int type;
   public static final int TYPE_CONST_BOOL = 5;
   public static final int TYPE_CONST_VEC4 = 8;
   public static final int TYPE_UNIFORM = 1;
   public static final int TYPE_CONST_FLOAT = 4;
   public static final int TYPE_PROPERTY = 6;
   public static final int TYPE_CONST_INT = 3;
   private String name;
   private String value;
   private String line;

   public boolean isExtension() {
      return this.type == 7;
   }

   public boolean isConstFloat() {
      return this.type == 4;
   }

   public boolean getValueBool() {
      String text = this.value.toLowerCase();
      if (!text.equals("true") && !text.equals("false")) {
         throw new RuntimeException("Invalid boolean: " + this.value + ", line: " + this.line);
      } else {
         return Boolean.valueOf(this.value);
      }
   }

   public boolean isUniform() {
      return this.type == 1;
   }

   public boolean isConstBool(String name1, String name2) {
      return this.isConstBool(name1) || this.isConstBool(name2);
   }

   public String getValue() {
      return this.value;
   }

   public boolean isConstBool(String name) {
      return this.isConstBool() && name.equals(this.name);
   }

   public int getType() {
      return this.type;
   }

   public Vector4f getValueVec4() {
      if (this.value == null) {
         return null;
      }

      String[] texts = Config.tokenize(StrUtils.trim(StrUtils.removePrefix(this.value.trim(), "vec4"), " ()"), ", ");
      if (texts.length != 4) {
         return null;
      }

      float[] fArray = new float[4];

      for (int i = 0; i < texts.length; i++) {
         float f2 = Config.parseFloat(StrUtils.removeSuffix(texts[i], new String[]{"F", "f"}), Float.MAX_VALUE);
         if (f2 == Float.MAX_VALUE) {
            return null;
         }

         fArray[i] = f2;
      }

      return new Vector4f(fArray[0], fArray[1], fArray[2], fArray[3]);
   }

   public boolean isAttribute() {
      return this.type == 2;
   }

   public boolean isConstBoolSuffix(String suffix) {
      return this.isConstBool() && this.name.endsWith(suffix);
   }

   public boolean checkString2(String text2, String text3) {
      return this.isConstBool(text2, text3) && this.getValueBool();
   }

   public boolean isConstVec4() {
      return this.type == 8;
   }

   public boolean isConstVec4Suffix(String suffix) {
      return this.isConstVec4() && this.name.endsWith(suffix);
   }

   public boolean isAttribute(String name) {
      return this.isAttribute() && name.equals(this.name);
   }

   public boolean isConstInt(String name) {
      return this.isConstInt() && name.equals(this.name);
   }

   public boolean isUniform(String name) {
      return this.isConstBool(name) && this.getValueBool();
   }

   public boolean isExtension(String name) {
      return this.isExtension() && name.equals(this.name);
   }

   public boolean isConstInt() {
      return this.type == 3;
   }

   public boolean isProperty(String name, String value) {
      return this.isProperty(name) && value.equals(this.value);
   }

   public boolean isConstBool() {
      return this.type == 5;
   }

   public boolean isProperty() {
      return this.type == 6;
   }

   public boolean checkString(String text2, String text3, String text4) {
      return this.isConstBool(text2, text3, text4) && this.getValueBool();
   }

   public boolean isConstIntSuffix(String suffix) {
      return this.isConstInt() && this.name.endsWith(suffix);
   }

   public boolean isConstBoolSuffix(String suffix, boolean val) {
      return this.isConstBoolSuffix(suffix) && this.getValueBool() == val;
   }

   public boolean isConstFloat(String name) {
      return this.isConstFloat() && name.equals(this.name);
   }

   public ShaderLine(int type, String name, String value, String line) {
      this.type = type;
      this.name = name;
      this.value = value;
      this.line = line;
   }

   public int getValueInt() {
      try {
         return Integer.parseInt(this.value);
      } catch (NumberFormatException numberFormatException) {
         throw new NumberFormatException("Invalid integer: " + this.value + ", line: " + this.line);
      }
   }

   public String getName() {
      return this.name;
   }

   public boolean isProperty(String name) {
      return this.isProperty() && name.equals(this.name);
   }

   public float getValueFloat() {
      try {
         return Float.parseFloat(this.value);
      } catch (NumberFormatException numberFormatException) {
         throw new NumberFormatException("Invalid float: " + this.value + ", line: " + this.line);
      }
   }

   public boolean isConstBool(String name1, String name2, String name3) {
      return this.isConstBool(name1) || this.isConstBool(name2) || this.isConstBool(name3);
   }
}
