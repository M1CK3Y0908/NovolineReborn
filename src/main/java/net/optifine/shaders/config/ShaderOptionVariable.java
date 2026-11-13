package net.optifine.shaders.config;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;
import net.optifine.util.StrUtils;

public class ShaderOptionVariable extends ShaderOption {
   private static final Pattern PATTERN_VARIABLE = Pattern.compile("^\\s*#define\\s+(\\w+)\\s+(-?[0-9\\.Ff]+|\\w+)\\s*(//.*)?$");

   @Override
   public String getSourceLine() {
      return "#define " + this.getName() + " " + this.getValue() + " // Shader option " + this.getValue();
   }

   @Override
   public String getValueText(String val) {
      String text2 = Shaders.translate("prefix." + this.getName(), "");
      String valueText = super.getValueText(val);
      String text3 = Shaders.translate("suffix." + this.getName(), "");
      return text2 + valueText + text3;
   }

   @Override
   public String getValueColor(String val) {
      String text = val.toLowerCase();
      return !text.equals("false") && !text.equals("off") ? "§a" : "§c";
   }

   public static String[] parseValues(String value, String valuesStr) {
      String[] texts = new String[]{value};
      if (valuesStr == null) {
         return texts;
      }

      valuesStr = valuesStr.trim();
      valuesStr = StrUtils.removePrefix(valuesStr, "[");
      valuesStr = StrUtils.removeSuffix(valuesStr, "]");
      valuesStr = valuesStr.trim();
      if (valuesStr.length() <= 0) {
         return texts;
      }

      String[] stringArray1 = Config.tokenize(valuesStr, " ");
      if (stringArray1.length <= 0) {
         return texts;
      }

      if (!Arrays.asList(stringArray1).contains(value)) {
         stringArray1 = (String[])Config.removeObjectFromArray(stringArray1, value);
      }

      return stringArray1;
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher = PATTERN_VARIABLE.matcher(line);
      return !matcher.matches() ? false : matcher.group(1).matches(this.getName());
   }

   public static ShaderOption parseOption(String line, String path) {
      Matcher matcher2 = PATTERN_VARIABLE.matcher(line);
      if (!matcher2.matches()) {
         return null;
      }

      String text2 = matcher2.group(1);
      String text3 = matcher2.group(2);
      String text4 = matcher2.group(3);
      String segment = StrUtils.getSegment(text4, "[", "]");
      if (segment != null && segment.length() > 0) {
         text4 = text4.replace(segment, "").trim();
      }

      String[] texts = parseValues(text3, segment);
      if (text2 != null && text2.length() > 0) {
         path = StrUtils.removePrefix(path, "/shaders/");
         return new ShaderOptionVariable(text2, text4, text3, texts, path);
      } else {
         return null;
      }
   }

   public ShaderOptionVariable(String name, String description, String value, String[] values, String path) {
      super(name, description, value, values, value, path);
      this.setVisible(this.getValues().length > 1);
   }
}
