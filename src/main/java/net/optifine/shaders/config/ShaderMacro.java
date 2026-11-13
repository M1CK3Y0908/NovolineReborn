package net.optifine.shaders.config;

public class ShaderMacro {
   private String name;
   private String value;

   public String getSourceLine() {
      return "#define " + this.name + " " + this.value;
   }

   public ShaderMacro(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String toString() {
      return this.getSourceLine();
   }

   public String getValue() {
      return this.value;
   }
}
