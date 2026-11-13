package net.optifine.shaders.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.optifine.expr.ExpressionFloatArrayCached;
import net.optifine.expr.ExpressionFloatCached;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.IExpressionFloatArray;
import net.optifine.expr.ParseException;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Program;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.uniform.CustomUniform;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderExpressionResolver;
import net.optifine.shaders.uniform.UniformType;
import net.optifine.util.StrUtils;

public class ShaderPackParser {
   private static final Pattern PATTERN_VERSION = Pattern.compile("^\\s*#version\\s+.*$");
   private static final Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
   private static final Set<String> setConstNames = makeSetConstNames();
   private static final Map<String, Integer> mapAlphaFuncs = makeMapAlphaFuncs();
   private static final Map<String, Integer> mapBlendFactors = makeMapBlendFactors();

   public static void parseBuffersFlip(Properties props) {
      for (String text : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String[] stringArray = Config.tokenize(text, ".");
         if (stringArray.length == 3) {
            String text3 = stringArray[0];
            String text4 = stringArray[1];
            String text5 = stringArray[2];
            if (text3.equals("flip")) {
               Program program = Shaders.getProgram(text4);
               if (program == null) {
                  SMCLog.severe("Invalid program name: " + text4);
               } else {
                  Boolean[] booleanArray = program.getBuffersFlip();
                  int bufferIndexFromString = Shaders.getBufferIndexFromString(text5);
                  if (bufferIndexFromString >= 0 && bufferIndexFromString < booleanArray.length) {
                     String propertyValue = props.getProperty(text).trim();
                     Boolean obool = Config.parseBoolean(propertyValue, (Boolean)null);
                     if (obool == null) {
                        SMCLog.severe("Invalid boolean value: " + propertyValue);
                     } else {
                        booleanArray[bufferIndexFromString] = obool;
                     }
                  } else {
                     SMCLog.severe("Invalid buffer name: " + text5);
                  }
               }
            }
         }
      }
   }

   private static Set<String> makeSetConstNames() {
      HashSet hashSet = new HashSet();
      hashSet.add("shadowMapResolution");
      hashSet.add("shadowMapFov");
      hashSet.add("shadowDistance");
      hashSet.add("shadowDistanceRenderMul");
      hashSet.add("shadowIntervalSize");
      hashSet.add("generateShadowMipmap");
      hashSet.add("generateShadowColorMipmap");
      hashSet.add("shadowHardwareFiltering");
      hashSet.add("shadowHardwareFiltering0");
      hashSet.add("shadowHardwareFiltering1");
      hashSet.add("shadowtex0Mipmap");
      hashSet.add("shadowtexMipmap");
      hashSet.add("shadowtex1Mipmap");
      hashSet.add("shadowcolor0Mipmap");
      hashSet.add("shadowColor0Mipmap");
      hashSet.add("shadowcolor1Mipmap");
      hashSet.add("shadowColor1Mipmap");
      hashSet.add("shadowtex0Nearest");
      hashSet.add("shadowtexNearest");
      hashSet.add("shadow0MinMagNearest");
      hashSet.add("shadowtex1Nearest");
      hashSet.add("shadow1MinMagNearest");
      hashSet.add("shadowcolor0Nearest");
      hashSet.add("shadowColor0Nearest");
      hashSet.add("shadowColor0MinMagNearest");
      hashSet.add("shadowcolor1Nearest");
      hashSet.add("shadowColor1Nearest");
      hashSet.add("shadowColor1MinMagNearest");
      hashSet.add("wetnessHalflife");
      hashSet.add("drynessHalflife");
      hashSet.add("eyeBrightnessHalflife");
      hashSet.add("centerDepthHalflife");
      hashSet.add("sunPathRotation");
      hashSet.add("ambientOcclusionLevel");
      hashSet.add("superSamplingLevel");
      hashSet.add("noiseTextureResolution");
      return hashSet;
   }

   public static CustomUniforms parseCustomUniforms(Properties props) {
      String text3 = "uniform.";
      text3 = "variable.";
      HashMap hashMap = new HashMap();
      ArrayList arrayList = new ArrayList();

      for (String text4 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String[] texts = Config.tokenize(text4, ".");
         if (texts.length == 3) {
            String text5 = texts[0];
            String text6 = texts[1];
            String text7 = texts[2];
            String text8 = props.getProperty(text4).trim();
            if (hashMap.containsKey(text7)) {
               SMCLog.warning("Expression already defined: " + text7);
            } else if (text5.equals("uniform") || text5.equals("variable")) {
               SMCLog.info("Custom " + text5 + ": " + text7);
               CustomUniform customUniform = parseCustomUniform(text5, text7, text6, text8, hashMap);
               if (customUniform != null) {
                  hashMap.put(text7, customUniform.getExpression());
                  if (!text5.equals("variable")) {
                     arrayList.add(customUniform);
                  }
               }
            }
         }
      }

      if (arrayList.size() <= 0) {
         return null;
      }

      CustomUniform[] customUniforms = (CustomUniform[]) arrayList.toArray(new CustomUniform[arrayList.size()]);
      return new CustomUniforms(customUniforms, hashMap);
   }

   private static ShaderProfile parseProfile(String name, Properties props, Set<String> parsedProfiles, ShaderOption[] shaderOptions) {
      String text3 = "profile." + name;
      if (parsedProfiles.contains(text3)) {
         Config.warn("[Shaders] Profile already parsed: " + name);
         return null;
      }

      parsedProfiles.add(name);
      ShaderProfile shaderProfile = new ShaderProfile(name);
      String[] texts = Config.tokenize(props.getProperty(text3), " ");

      for (int i = 0; i < texts.length; i++) {
         String text4 = texts[i];
         if (text4.startsWith("profile.")) {
            String text5 = text4.substring("profile.".length());
            ShaderProfile shaderProfile2 = parseProfile(text5, props, parsedProfiles, shaderOptions);
            shaderProfile.addOptionValues(shaderProfile2);
            shaderProfile.addDisabledPrograms(shaderProfile2.getDisabledPrograms());
         } else {
            String[] texts2 = Config.tokenize(text4, ":=");
            if (texts2.length == 1) {
               String text6 = texts2[0];
               boolean flag = true;
               if (text6.startsWith("!")) {
                  flag = false;
                  text6 = text6.substring(1);
               }

               if (text6.startsWith("program.")) {
                  String text7 = text6.substring("program.".length());
                  if (!Shaders.isProgramPath(text7)) {
                     Config.warn("Invalid program: " + text7 + " in profile: " + shaderProfile.getName());
                  } else if (flag) {
                     shaderProfile.removeDisabledProgram(text7);
                  } else {
                     shaderProfile.addDisabledProgram(text7);
                  }
               } else {
                  ShaderOption shaderOption3 = ShaderUtils.getShaderOption(text6, shaderOptions);
                  if (!(shaderOption3 instanceof ShaderOptionSwitch)) {
                     Config.warn("[Shaders] Invalid option: " + text6);
                  } else {
                     shaderProfile.addOptionValue(text6, String.valueOf(flag));
                     shaderOption3.setVisible(true);
                  }
               }
            } else if (texts2.length != 2) {
               Config.warn("[Shaders] Invalid option value: " + text4);
            } else {
               String text8 = texts2[0];
               String text9 = texts2[1];
               ShaderOption shaderOption4 = ShaderUtils.getShaderOption(text8, shaderOptions);
               if (shaderOption4 == null) {
                  Config.warn("[Shaders] Invalid option: " + text4);
               } else if (!shaderOption4.isValidValue(text9)) {
                  Config.warn("[Shaders] Invalid value: " + text4);
               } else {
                  shaderOption4.setVisible(true);
                  shaderProfile.addOptionValue(text8, text9);
               }
            }
         }
      }

      return shaderProfile;
   }

   public static ShaderProfile[] parseProfiles(Properties props, ShaderOption[] shaderOptions) {
      ArrayList arrayList = new ArrayList();

      for (String text3 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         if (text3.startsWith("profile.")) {
            String text4 = text3.substring("profile.".length());
            props.getProperty(text3);
            HashSet hashSet = new HashSet();
            ShaderProfile shaderProfile = parseProfile(text4, props, hashSet, shaderOptions);
            if (shaderProfile != null) {
               arrayList.add(shaderProfile);
            }
         }
      }

      return arrayList.size() <= 0 ? null : (ShaderProfile[]) arrayList.toArray(new ShaderProfile[arrayList.size()]);
   }

   public static void parseBlendStates(Properties props) {
      for (String text3 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String[] texts = Config.tokenize(text3, ".");
         if (texts.length == 2) {
            String text4 = texts[0];
            String text5 = texts[1];
            if (text4.equals("blend")) {
               Program program2 = Shaders.getProgram(text5);
               if (program2 == null) {
                  SMCLog.severe("Invalid program name: " + text5);
               } else {
                  GlBlendState glBlendState = parseBlendState(props.getProperty(text3).trim());
                  if (glBlendState != null) {
                     program2.setBlendState(glBlendState);
                  }
               }
            }
         }
      }
   }

   private static void collectShaderOptions(IShaderPack shaderPack, String dir, String[] programNames, Map<String, ShaderOption> mapOptions) {
      for (int index = 0; index < programNames.length; index++) {
         String text3 = programNames[index];
         if (!text3.equals("")) {
            String text4 = dir + "/" + text3 + ".vsh";
            String text5 = dir + "/" + text3 + ".fsh";
            collectShaderOptions(shaderPack, text4, mapOptions);
            collectShaderOptions(shaderPack, text5, mapOptions);
         }
      }
   }

   private static IExpression makeExpressionCached(IExpression expr) {
      return expr instanceof IExpressionFloat
         ? new ExpressionFloatCached((IExpressionFloat)expr)
         : (expr instanceof IExpressionFloatArray ? new ExpressionFloatArrayCached((IExpressionFloatArray)expr) : expr);
   }

   private static Map<String, Integer> makeMapAlphaFuncs() {
      HashMap hashMap = new HashMap();
      hashMap.put("NEVER", new Integer(512));
      hashMap.put("LESS", new Integer(513));
      hashMap.put("EQUAL", new Integer(514));
      hashMap.put("LEQUAL", new Integer(515));
      hashMap.put("GREATER", new Integer(516));
      hashMap.put("NOTEQUAL", new Integer(517));
      hashMap.put("GEQUAL", new Integer(518));
      hashMap.put("ALWAYS", new Integer(519));
      return Collections.unmodifiableMap(hashMap);
   }

   public static ShaderOption[] parseShaderPackOptions(IShaderPack shaderPack, String[] programNames, List<Integer> listDimensions) {
      if (shaderPack == null) {
         return new ShaderOption[0];
      }

      HashMap hashMap = new HashMap();
      collectShaderOptions(shaderPack, "/shaders", programNames, hashMap);

      for (int i : listDimensions) {
         String text3 = "/shaders/world" + i;
         collectShaderOptions(shaderPack, text3, programNames, hashMap);
      }

      Collection<ShaderOption> collection = hashMap.values();
      ShaderOption[] shaderOptions2 = collection.toArray(new ShaderOption[collection.size()]);
      Comparator comparator = new Comparator<ShaderOption>() {
         public int compare(ShaderOption shaderOption, ShaderOption shaderOption2) {
            return shaderOption.getName().compareToIgnoreCase(shaderOption2.getName());
         }
      };
      Arrays.sort(shaderOptions2, comparator);
      return shaderOptions2;
   }

   private static GlBlendState parseBlendState(String str) {
      String[] stringArray = Config.tokenize(str, " ");
      if (stringArray.length == 1) {
         String text3 = stringArray[0];
         if (text3.equals("off") || text3.equals("false")) {
            return new GlBlendState(false);
         }
      } else if (stringArray.length == 2 || stringArray.length == 4) {
         String text4 = stringArray[0];
         String text5 = stringArray[1];
         String text6 = text4;
         String text7 = text5;
         if (stringArray.length == 4) {
            text6 = stringArray[2];
            text7 = stringArray[3];
         }

         Integer integer = mapBlendFactors.get(text4);
         Integer integer4 = mapBlendFactors.get(text5);
         Integer integer5 = mapBlendFactors.get(text6);
         Integer integer6 = mapBlendFactors.get(text7);
         if (integer != null && integer4 != null && integer5 != null && integer6 != null) {
            return new GlBlendState(true, integer, integer4, integer5, integer6);
         }
      }

      SMCLog.severe("Invalid blend mode: " + str);
      return null;
   }

   private static Map<String, Integer> makeMapBlendFactors() {
      HashMap hashMap = new HashMap();
      hashMap.put("ZERO", new Integer(0));
      hashMap.put("ONE", new Integer(1));
      hashMap.put("SRC_COLOR", new Integer(768));
      hashMap.put("ONE_MINUS_SRC_COLOR", new Integer(769));
      hashMap.put("DST_COLOR", new Integer(774));
      hashMap.put("ONE_MINUS_DST_COLOR", new Integer(775));
      hashMap.put("SRC_ALPHA", new Integer(770));
      hashMap.put("ONE_MINUS_SRC_ALPHA", new Integer(771));
      hashMap.put("DST_ALPHA", new Integer(772));
      hashMap.put("ONE_MINUS_DST_ALPHA", new Integer(773));
      hashMap.put("CONSTANT_COLOR", new Integer(32769));
      hashMap.put("ONE_MINUS_CONSTANT_COLOR", new Integer(32770));
      hashMap.put("CONSTANT_ALPHA", new Integer(32771));
      hashMap.put("ONE_MINUS_CONSTANT_ALPHA", new Integer(32772));
      hashMap.put("SRC_ALPHA_SATURATE", new Integer(776));
      return Collections.unmodifiableMap(hashMap);
   }

   public static void parseRenderScales(Properties props) {
      for (String text3 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String[] texts = Config.tokenize(text3, ".");
         if (texts.length == 2) {
            String text4 = texts[0];
            String text5 = texts[1];
            if (text4.equals("scale")) {
               Program program2 = Shaders.getProgram(text5);
               if (program2 == null) {
                  SMCLog.severe("Invalid program name: " + text5);
               } else {
                  RenderScale renderScale = parseRenderScale(props.getProperty(text3).trim());
                  if (renderScale != null) {
                     program2.setRenderScale(renderScale);
                  }
               }
            }
         }
      }
   }

   private static RenderScale parseRenderScale(String str) {
      String[] stringArray = Config.tokenize(str, " ");
      float f = Config.parseFloat(stringArray[0], -1.0F);
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (stringArray.length > 1) {
         if (stringArray.length != 3) {
            SMCLog.severe("Invalid render scale: " + str);
            return null;
         }

         f2 = Config.parseFloat(stringArray[1], -1.0F);
         f3 = Config.parseFloat(stringArray[2], -1.0F);
      }

      if (Config.between(f, 0.0F, 1.0F) && Config.between(f2, 0.0F, 1.0F) && Config.between(f3, 0.0F, 1.0F)) {
         return new RenderScale(f, f2, f3);
      }

      SMCLog.severe("Invalid render scale: " + str);
      return null;
   }

   private static void collectShaderOptions(IShaderPack sp, String path, Map<String, ShaderOption> mapOptions) {
      String[] lines2 = getLines(sp, path);

      for (int i = 0; i < lines2.length; i++) {
         ShaderOption shaderOption3 = getShaderOption(lines2[i], path);
         if (shaderOption3 != null && !shaderOption3.getName().startsWith(ShaderMacros.getPrefixMacro()) && (!shaderOption3.checkUsed() || isOptionUsed(shaderOption3, lines2))) {
            String name2 = shaderOption3.getName();
            ShaderOption shaderOption4 = mapOptions.get(name2);
            if (shaderOption4 != null) {
               if (!Config.equals(shaderOption4.getValueDefault(), shaderOption3.getValueDefault())) {
                  Config.warn("Ambiguous shader option: " + shaderOption3.getName());
                  Config.warn(" - in " + Config.arrayToString(shaderOption4.getPaths()) + ": " + shaderOption4.getValueDefault());
                  Config.warn(" - in " + Config.arrayToString(shaderOption3.getPaths()) + ": " + shaderOption3.getValueDefault());
                  shaderOption4.update();
               }

               if (shaderOption4.getDescription() == null || shaderOption4.getDescription().length() <= 0) {
                  shaderOption4.setDescription(shaderOption3.getDescription());
               }

               shaderOption4.addPaths(shaderOption3.getPaths());
            } else {
               mapOptions.put(name2, shaderOption3);
            }
         }
      }
   }

   private static ShaderMacro[] findMacros(String line, ShaderMacro[] macros) {
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < macros.length; index++) {
         ShaderMacro shaderMacro = macros[index];
         if (line.contains(shaderMacro.getName())) {
            arrayList.add(shaderMacro);
         }
      }

      return (ShaderMacro[]) arrayList.toArray(new ShaderMacro[arrayList.size()]);
   }

   private static String loadFile(String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel) throws IOException {
      if (includeLevel >= 10) {
         throw new IOException("#include depth exceeded: " + includeLevel + ", file: " + filePath);
      }

      includeLevel++;
      InputStream resourceAsStream = shaderPack.getResourceAsStream(filePath);
      if (resourceAsStream == null) {
         return null;
      }

      InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "ASCII");
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      bufferedReader = resolveIncludes(bufferedReader, filePath, shaderPack, fileIndex, listFiles, includeLevel);
      CharArrayWriter charArrayWriter = new CharArrayWriter();

      while (true) {
         String line2 = bufferedReader.readLine();
         if (line2 == null) {
            return charArrayWriter.toString();
         }

         charArrayWriter.write(line2);
         charArrayWriter.write("\n");
      }
   }

   private static String[] getLines(IShaderPack sp, String path) {
      try {
         ArrayList arrayList = new ArrayList();
         String text3 = loadFile(path, sp, 0, arrayList, 0);
         return text3 == null ? new String[0] : Config.readLines(new ByteArrayInputStream(text3.getBytes()));
      } catch (IOException ioException) {
         Config.dbg(ioException.getClass().getName() + ": " + ioException.getMessage());
         return new String[0];
      }
   }

   public static Map<String, IExpressionBool> parseProgramConditions(Properties props, ShaderOption[] shaderOptions) {
      Pattern pattern = Pattern.compile("program\\.([^.]+)\\.enabled");
      HashMap hashMap = new HashMap();

      for (String text3 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         Matcher matcher2 = pattern.matcher(text3);
         if (matcher2.matches()) {
            String text4 = matcher2.group(1);
            IExpressionBool iExpressionBool = parseOptionExpression(props.getProperty(text3).trim(), shaderOptions);
            if (iExpressionBool == null) {
               SMCLog.severe("Error parsing program condition: " + text3);
            } else {
               hashMap.put(text4, iExpressionBool);
            }
         }
      }

      return hashMap;
   }

   public static void parseAlphaStates(Properties props) {
      for (String text3 : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String[] texts = Config.tokenize(text3, ".");
         if (texts.length == 2) {
            String text4 = texts[0];
            String text5 = texts[1];
            if (text4.equals("alphaTest")) {
               Program program2 = Shaders.getProgram(text5);
               if (program2 == null) {
                  SMCLog.severe("Invalid program name: " + text5);
               } else {
                  GlAlphaState glAlphaState = parseAlphaState(props.getProperty(text3).trim());
                  if (glAlphaState != null) {
                     program2.setAlphaState(glAlphaState);
                  }
               }
            }
         }
      }
   }

   private static ShaderOption getShaderOption(String line, String path) {
      ShaderOption shaderOption = ShaderOptionSwitch.parseOption(line, path);
      if (shaderOption == null) {
         shaderOption = ShaderOptionVariable.parseOption(line, path);
      }

      if (shaderOption != null) {
         return shaderOption;
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionSwitchConst.parseOption(line, path);
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionVariableConst.parseOption(line, path);
      }

      return shaderOption != null && setConstNames.contains(shaderOption.getName()) ? shaderOption : null;
   }

   private static IExpressionBool parseOptionExpression(String val, ShaderOption[] shaderOptions) {
      try {
         ShaderOptionResolver shaderOptionResolver = new ShaderOptionResolver(shaderOptions);
         return new ExpressionParser(shaderOptionResolver).parseBool(val);
      } catch (ParseException parseException) {
         SMCLog.warning(parseException.getClass().getName() + ": " + parseException.getMessage());
         return null;
      }
   }

   private static GlAlphaState parseAlphaState(String str) {
      String[] stringArray = Config.tokenize(str, " ");
      if (stringArray.length == 1) {
         String text3 = stringArray[0];
         if (text3.equals("off") || text3.equals("false")) {
            return new GlAlphaState(false);
         }
      } else if (stringArray.length == 2) {
         String text4 = stringArray[0];
         String text5 = stringArray[1];
         Integer integer = mapAlphaFuncs.get(text4);
         float f = Config.parseFloat(text5, -1.0F);
         if (integer != null && f >= 0.0F) {
            return new GlAlphaState(true, integer, f);
         }
      }

      SMCLog.severe("Invalid alpha test: " + str);
      return null;
   }

   private static boolean parseGuiScreen(
      String key, Properties props, Map<String, ScreenShaderOptions> map, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions
   ) {
      String propertyValue = props.getProperty(key);
      if (propertyValue == null) {
         return false;
      }

      ArrayList arrayList = new ArrayList();
      HashSet hashSet = new HashSet();
      String[] stringArray = Config.tokenize(propertyValue, " ");

      for (int index = 0; index < stringArray.length; index++) {
         String propertyValue2 = stringArray[index];
         if (propertyValue2.equals("<empty>")) {
            arrayList.add(null);
         } else if (hashSet.contains(propertyValue2)) {
            Config.warn("[Shaders] Duplicate option: " + propertyValue2 + ", key: " + key);
         } else {
            hashSet.add(propertyValue2);
            if (propertyValue2.equals("<profile>")) {
               if (shaderProfiles == null) {
                  Config.warn("[Shaders] Option profile can not be used, no profiles defined: " + propertyValue2 + ", key: " + key);
               } else {
                  ShaderOptionProfile shaderOptionProfile = new ShaderOptionProfile(shaderProfiles, shaderOptions);
                  arrayList.add(shaderOptionProfile);
               }
            } else if (propertyValue2.equals("*")) {
               ShaderOptionRest shaderOptionRest = new ShaderOptionRest("<rest>");
               arrayList.add(shaderOptionRest);
            } else if (propertyValue2.startsWith("[") && propertyValue2.endsWith("]")) {
               String text2 = StrUtils.removePrefixSuffix(propertyValue2, "[", "]");
               if (!text2.matches("^[a-zA-Z0-9_]+$")) {
                  Config.warn("[Shaders] Invalid screen: " + propertyValue2 + ", key: " + key);
               } else if (!parseGuiScreen("screen." + text2, props, map, shaderProfiles, shaderOptions)) {
                  Config.warn("[Shaders] Invalid screen: " + propertyValue2 + ", key: " + key);
               } else {
                  ShaderOptionScreen shaderOptionScreen = new ShaderOptionScreen(text2);
                  arrayList.add(shaderOptionScreen);
               }
            } else {
               ShaderOption shaderOption2 = ShaderUtils.getShaderOption(propertyValue2, shaderOptions);
               if (shaderOption2 == null) {
                  Config.warn("[Shaders] Invalid option: " + propertyValue2 + ", key: " + key);
                  arrayList.add(null);
               } else {
                  shaderOption2.setVisible(true);
                  arrayList.add(shaderOption2);
               }
            }
         }
      }

      ShaderOption[] shaderOptions2 = (ShaderOption[]) arrayList.toArray(new ShaderOption[arrayList.size()]);
      String propertyValue2 = props.getProperty(key + ".columns");
      int i = Config.parseInt(propertyValue2, 2);
      ScreenShaderOptions screenShaderOptions = new ScreenShaderOptions(key, shaderOptions2, i);
      map.put(key, screenShaderOptions);
      return true;
   }

   public static Set<String> parseOptionSliders(Properties props, ShaderOption[] shaderOptions) {
      HashSet hashSet = new HashSet();
      String property = props.getProperty("sliders");
      if (property == null) {
         return hashSet;
      }

      String[] texts = Config.tokenize(property, " ");

      for (int i = 0; i < texts.length; i++) {
         String text3 = texts[i];
         if (ShaderUtils.getShaderOption(text3, shaderOptions) == null) {
            Config.warn("Invalid shader option: " + text3);
         } else {
            hashSet.add(text3);
         }
      }

      return hashSet;
   }

   private static CustomUniform parseCustomUniform(String kind, String name, String type, String src, Map<String, IExpression> mapExpressions) {
      try {
         UniformType uniformType = UniformType.parse(type);
         if (uniformType == null) {
            SMCLog.warning("Unknown " + kind + " type: " + uniformType);
            return null;
         } else {
            ShaderExpressionResolver shaderExpressionResolver = new ShaderExpressionResolver(mapExpressions);
            IExpression iExpression = new ExpressionParser(shaderExpressionResolver).parse(src);
            ExpressionType expressionType = iExpression.getExpressionType();
            if (!uniformType.matchesExpressionType(expressionType)) {
               SMCLog.warning("Expression type does not match " + kind + " type, expression: " + expressionType + ", " + kind + ": " + uniformType + " " + name);
               return null;
            } else {
               iExpression = makeExpressionCached(iExpression);
               return new CustomUniform(name, uniformType, iExpression);
            }
         }
      } catch (ParseException parseException) {
         SMCLog.warning(parseException.getClass().getName() + ": " + parseException.getMessage());
         return null;
      }
   }

   public static BufferedReader resolveIncludes(
      BufferedReader reader, String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel
   ) throws IOException {
      String text3 = "/";
      int i = filePath.lastIndexOf("/");
      if (i >= 0) {
         text3 = filePath.substring(0, i);
      }

      CharArrayWriter charArrayWriter = new CharArrayWriter();
      int i2 = -1;
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      int i3 = 1;

      while (true) {
         String line2 = reader.readLine();
         if (line2 == null) {
            char[] cArray = charArrayWriter.toCharArray();
            if (i2 >= 0 && linkedHashSet.size() > 0) {
               StringBuilder stringBuilder = new StringBuilder();

               for (ShaderMacro shaderMacro : (Iterable<ShaderMacro>) linkedHashSet) {
                  stringBuilder.append("#define ");
                  stringBuilder.append(shaderMacro.getName());
                  stringBuilder.append(" ");
                  stringBuilder.append(shaderMacro.getValue());
                  stringBuilder.append("\n");
               }

               String text4 = stringBuilder.toString();
               StringBuilder stringBuilder2 = new StringBuilder(new String(cArray));
               stringBuilder2.insert(i2, text4);
               String text5 = stringBuilder2.toString();
               cArray = text5.toCharArray();
            }

            CharArrayReader charArrayReader = new CharArrayReader(cArray);
            return new BufferedReader(charArrayReader);
         }

         if (i2 < 0) {
            Matcher matcher2 = PATTERN_VERSION.matcher(line2);
            if (matcher2.matches()) {
               String text6 = ShaderMacros.getFixedMacroLines() + ShaderMacros.getOptionMacroLines();
               String text7 = line2 + "\n" + text6;
               String text8 = "#line " + (i3 + 1) + " " + fileIndex;
               line2 = text7 + text8;
               i2 = charArrayWriter.size() + text7.length();
            }
         }

         Matcher matcher3 = PATTERN_INCLUDE.matcher(line2);
         if (matcher3.matches()) {
            String text9 = matcher3.group(1);
            boolean flag = text9.startsWith("/");
            String text10 = flag ? "/shaders" + text9 : text3 + "/" + text9;
            if (!listFiles.contains(text10)) {
               listFiles.add(text10);
            }

            int i4 = listFiles.indexOf(text10) + 1;
            line2 = loadFile(text10, shaderPack, i4, listFiles, includeLevel);
            if (line2 == null) {
               throw new IOException("Included file not found: " + filePath);
            }

            if (line2.endsWith("\n")) {
               line2 = line2.substring(0, line2.length() - 1);
            }

            String text11 = "#line 1 " + i4 + "\n";
            if (line2.startsWith("#version ")) {
               text11 = "";
            }

            line2 = text11 + line2 + "\n#line " + (i3 + 1) + " " + fileIndex;
         }

         if (i2 >= 0 && line2.contains(ShaderMacros.getPrefixMacro())) {
            ShaderMacro[] macros2 = findMacros(line2, ShaderMacros.getExtensions());

            for (int i5 = 0; i5 < macros2.length; i5++) {
               ShaderMacro shaderMacro2 = macros2[i5];
               linkedHashSet.add(shaderMacro2);
            }
         }

         charArrayWriter.write(line2);
         charArrayWriter.write("\n");
         i3++;
      }
   }

   private static boolean isOptionUsed(ShaderOption so, String[] lines) {
      for (int index = 0; index < lines.length; index++) {
         String text3 = lines[index];
         if (so.isUsedInLine(text3)) {
            return true;
         }
      }

      return false;
   }

   public static Map<String, ScreenShaderOptions> parseGuiScreens(Properties props, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
      HashMap hashMap = new HashMap();
      parseGuiScreen("screen", props, hashMap, shaderProfiles, shaderOptions);
      return hashMap.isEmpty() ? null : hashMap;
   }
}
