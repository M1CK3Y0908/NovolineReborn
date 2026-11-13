package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderParser {
   public static Pattern PATTERN_UNIFORM = Pattern.compile("\\s*uniform\\s+\\w+\\s+(\\w+).*");
   public static Pattern PATTERN_ATTRIBUTE = Pattern.compile("\\s*attribute\\s+\\w+\\s+(\\w+).*");
   public static Pattern PATTERN_CONST_INT = Pattern.compile("\\s*const\\s+int\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
   public static Pattern PATTERN_CONST_FLOAT = Pattern.compile("\\s*const\\s+float\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
   public static Pattern PATTERN_CONST_VEC4 = Pattern.compile("\\s*const\\s+vec4\\s+(\\w+)\\s*=\\s*(.+)\\s*;.*");
   public static Pattern PATTERN_CONST_BOOL = Pattern.compile("\\s*const\\s+bool\\s+(\\w+)\\s*=\\s*(\\w+)\\s*;.*");
   public static Pattern PATTERN_PROPERTY = Pattern.compile("\\s*(/\\*|//)?\\s*([A-Z]+):\\s*(\\w+)\\s*(\\*/.*|\\s*)");
   public static Pattern PATTERN_EXTENSION = Pattern.compile("\\s*#\\s*extension\\s+(\\w+)\\s*:\\s*(\\w+).*");
   public static Pattern PATTERN_DEFERRED_FSH = Pattern.compile(".*deferred[0-9]*\\.fsh");
   public static Pattern PATTERN_COMPOSITE_FSH = Pattern.compile(".*composite[0-9]*\\.fsh");
   public static Pattern PATTERN_FINAL_FSH = Pattern.compile(".*final\\.fsh");
   public static Pattern PATTERN_DRAW_BUFFERS = Pattern.compile("[0-7N]*");

   public static int getColorIndex(String uniform) {
      int index = getIndex(uniform, "gaux", 1, 4);
      return index > 0 ? index + 3 : getIndex(uniform, "colortex", 4, 7);
   }

   public static int getIndex(String uniform, String prefix, int minIndex, int maxIndex) {
      if (uniform.length() != prefix.length() + 1) {
         return -1;
      }

      if (!uniform.startsWith(prefix)) {
         return -1;
      }

      int count = uniform.charAt(prefix.length()) - '0';
      return count >= minIndex && count <= maxIndex ? count : -1;
   }

   public static int getShadowDepthIndex(String uniform) {
      return uniform.equals("shadow") ? 0 : (uniform.equals("watershadow") ? 1 : getIndex(uniform, "shadowtex", 0, 1));
   }

   public static boolean isValidDrawBuffers(String str) {
      return PATTERN_DRAW_BUFFERS.matcher(str).matches();
   }

   public static boolean isComposite(String filename) {
      return PATTERN_COMPOSITE_FSH.matcher(filename).matches();
   }

   public static boolean isFinal(String filename) {
      return PATTERN_FINAL_FSH.matcher(filename).matches();
   }

   public static int getShadowColorIndex(String uniform) {
      return uniform.equals("shadowcolor") ? 0 : getIndex(uniform, "shadowcolor", 0, 1);
   }

   public static ShaderLine parseLine(String line) {
      Matcher matcher = PATTERN_UNIFORM.matcher(line);
      if (matcher.matches()) {
         return new ShaderLine(1, matcher.group(1), "", line);
      }

      Matcher matcher8 = PATTERN_ATTRIBUTE.matcher(line);
      if (matcher8.matches()) {
         return new ShaderLine(2, matcher8.group(1), "", line);
      }

      Matcher matcher9 = PATTERN_PROPERTY.matcher(line);
      if (matcher9.matches()) {
         return new ShaderLine(6, matcher9.group(2), matcher9.group(3), line);
      }

      Matcher matcher10 = PATTERN_CONST_INT.matcher(line);
      if (matcher10.matches()) {
         return new ShaderLine(3, matcher10.group(1), matcher10.group(2), line);
      }

      Matcher matcher11 = PATTERN_CONST_FLOAT.matcher(line);
      if (matcher11.matches()) {
         return new ShaderLine(4, matcher11.group(1), matcher11.group(2), line);
      }

      Matcher matcher12 = PATTERN_CONST_BOOL.matcher(line);
      if (matcher12.matches()) {
         return new ShaderLine(5, matcher12.group(1), matcher12.group(2), line);
      }

      Matcher matcher13 = PATTERN_EXTENSION.matcher(line);
      if (matcher13.matches()) {
         return new ShaderLine(7, matcher13.group(1), matcher13.group(2), line);
      }

      Matcher matcher14 = PATTERN_CONST_VEC4.matcher(line);
      return matcher14.matches() ? new ShaderLine(8, matcher14.group(1), matcher14.group(2), line) : null;
   }

   public static boolean isDeferred(String filename) {
      return PATTERN_DEFERRED_FSH.matcher(filename).matches();
   }

   public static int getDepthIndex(String uniform) {
      return getIndex(uniform, "depthtex", 0, 2);
   }
}
