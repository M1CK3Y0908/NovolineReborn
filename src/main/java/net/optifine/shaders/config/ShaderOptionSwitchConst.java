package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.util.StrUtils;

public class ShaderOptionSwitchConst extends ShaderOptionSwitch {
   private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*bool\\s*([A-Za-z0-9_]+)\\s*=\\s*(true|false)\\s*;\\s*(//.*)?$");

   @Override
   public boolean checkUsed() {
      return false;
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher = PATTERN_CONST.matcher(line);
      return !matcher.matches() ? false : matcher.group(1).matches(this.getName());
   }

   public static ShaderOption parseOption(String line, String path) {
      Matcher matcher = PATTERN_CONST.matcher(line);
      if (!matcher.matches()) {
         return null;
      } else {
         String text = matcher.group(1);
         String text2 = matcher.group(2);
         String text3 = matcher.group(3);
         if (text != null && text.length() > 0) {
            path = StrUtils.removePrefix(path, "/shaders/");
            ShaderOptionSwitchConst shaderOptionSwitchConst = new ShaderOptionSwitchConst(text, text3, text2, path);
            shaderOptionSwitchConst.setVisible(false);
            return shaderOptionSwitchConst;
         } else {
            return null;
         }
      }
   }

   public ShaderOptionSwitchConst(String name, String description, String value, String path) {
      super(name, description, value, path);
   }

   @Override
   public String getSourceLine() {
      return "const bool " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }
}
