package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.util.StrUtils;

public class ShaderOptionSwitch extends ShaderOption {
   private static final Pattern PATTERN_DEFINE = Pattern.compile("^\\s*(//)?\\s*#define\\s+([A-Za-z0-9_]+)\\s*(//.*)?$");
   private static final Pattern PATTERN_IFDEF = Pattern.compile("^\\s*#if(n)?def\\s+([A-Za-z0-9_]+)(\\s*)?$");

   public static ShaderOption parseOption(String line, String path) {
      Matcher matcher2 = PATTERN_DEFINE.matcher(line);
      if (!matcher2.matches()) {
         return null;
      } else {
         String text2 = matcher2.group(1);
         String text3 = matcher2.group(2);
         String text4 = matcher2.group(3);
         if (text3 != null && text3.length() > 0) {
            boolean flag = !Config.equals(text2, "//");
            path = StrUtils.removePrefix(path, "/shaders/");
            return new ShaderOptionSwitch(text3, text4, String.valueOf(flag), path);
         } else {
            return null;
         }
      }
   }

   @Override
   public String getSourceLine() {
      return isTrue(this.getValue()) ? "#define " + this.getName() + " // Shader option ON" : "//#define " + this.getName() + " // Shader option OFF";
   }

   @Override
   public String getValueColor(String val) {
      return isTrue(val) ? "§a" : "§c";
   }

   public static boolean isTrue(String val) {
      return Boolean.valueOf(val);
   }

   @Override
   public boolean isUsedInLine(String line) {
      Matcher matcher = PATTERN_IFDEF.matcher(line);
      return matcher.matches() && matcher.group(2).equals(this.getName());
   }

   public ShaderOptionSwitch(String name, String description, String value, String path) {
      super(name, description, value, new String[]{"false", "true"}, value, path);
   }

   @Override
   public String getValueText(String val) {
      String text = super.getValueText(val);
      return text != val ? text : (isTrue(val) ? Lang.getOn() : Lang.getOff());
   }

   @Override
   public boolean checkUsed() {
      return true;
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher = PATTERN_DEFINE.matcher(line);
      return !matcher.matches() ? false : matcher.group(2).matches(this.getName());
   }
}
