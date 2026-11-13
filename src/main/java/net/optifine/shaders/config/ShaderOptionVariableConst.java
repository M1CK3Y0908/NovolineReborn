package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.util.StrUtils;

public class ShaderOptionVariableConst extends ShaderOptionVariable {
   private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*(float|int)\\s*([A-Za-z0-9_]+)\\s*=\\s*(-?[0-9\\.]+f?F?)\\s*;\\s*(//.*)?$");
   private String type = null;

   public static ShaderOption parseOption(String line, String path) {
      Matcher matcher2 = PATTERN_CONST.matcher(line);
      if (!matcher2.matches()) {
         return null;
      }

      String text = matcher2.group(1);
      String text2 = matcher2.group(2);
      String text3 = matcher2.group(3);
      String text4 = matcher2.group(4);
      String segment = StrUtils.getSegment(text4, "[", "]");
      if (segment != null && segment.length() > 0) {
         text4 = text4.replace(segment, "").trim();
      }

      String[] texts = parseValues(text3, segment);
      if (text2 != null && text2.length() > 0) {
         path = StrUtils.removePrefix(path, "/shaders/");
         return new ShaderOptionVariableConst(text2, text, text4, text3, texts, path);
      } else {
         return null;
      }
   }

   @Override
   public String getSourceLine() {
      return "const " + this.type + " " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
   }

   @Override
   public boolean matchesLine(String line) {
      Matcher matcher = PATTERN_CONST.matcher(line);
      return !matcher.matches() ? false : matcher.group(2).matches(this.getName());
   }

   public ShaderOptionVariableConst(String name, String type, String description, String value, String[] values, String path) {
      super(name, description, value, values, path);
      this.type = type;
   }
}
