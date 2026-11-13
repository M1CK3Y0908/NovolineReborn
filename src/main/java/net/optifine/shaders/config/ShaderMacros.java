package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.minecraft.util.Util;
import net.optifine.shaders.Shaders;

public class ShaderMacros {
   public static final String PREFIX_MACRO = "MC_VERSION";
   public static final String MC_OLD_LIGHTING = "MC_OLD_LIGHTING";
   public static final String MC_GLSL_VERSION = "MC_GLSL_VERSION";
   public static final String MC_HAND_DEPTH = "MC_HAND_DEPTH";
   public static final String MC_SPECULAR_MAP = "MC_SPECULAR_MAP";
   public static final String MC_GL_RENDERER_QUADRO = "MC_GL_RENDERER_QUADRO";
   public static final String MC_GL_VENDOR_INTEL = "MC_GL_VENDOR_INTEL";
   public static final String MC_RENDER_QUALITY = "MC_RENDER_QUALITY";
   public static final String MC_OS_MAC = "MC_OS_MAC";
   public static final String MC_GL_RENDERER_INTEL = "MC_GL_RENDERER_INTEL";
   public static final String MC_GL_VERSION = "MC_GL_VERSION";
   public static final String MC_OS_LINUX = "MC_OS_LINUX";
   public static final String MC_OS_WINDOWS = "MC_OS_WINDOWS";
   public static final String MC_GL_RENDERER_RADEON = "MC_GL_RENDERER_RADEON";
   public static final String MC_GL_RENDERER_MESA = "MC_GL_RENDERER_MESA";
   public static final String MC_FXAA_LEVEL = "MC_FXAA_LEVEL";
   public static final String MC_OS_OTHER = "MC_OS_OTHER";
   public static final String MC_SHADOW_QUALITY = "MC_SHADOW_QUALITY";
   public static final String MC_GL_VENDOR_XORG = "MC_GL_VENDOR_XORG";
   public static final String MC_NORMAL_MAP = "MC_NORMAL_MAP";
   public static final String MC_GL_RENDERER_GALLIUM = "MC_GL_RENDERER_GALLIUM";
   public static final String MC_GL_VENDOR_OTHER = "MC_GL_VENDOR_OTHER";
   public static final String MC_GL_VENDOR_NVIDIA = "MC_GL_VENDOR_NVIDIA";
   public static final String MC_OLD_HAND_LIGHT = "MC_OLD_HAND_LIGHT";
   private static ShaderMacro[] extensionMacros;
   public static final String MC_GL_RENDERER_GEFORCE = "MC_GL_RENDERER_GEFORCE";
   public static final String MC_GL_RENDERER_OTHER = "MC_GL_RENDERER_OTHER";
   public static final String MC_GL_VENDOR_ATI = "MC_GL_VENDOR_ATI";
   private static String text = "MC_";

   public static String getRenderer() {
      String openGlRenderer2 = Config.openGlRenderer;
      if (openGlRenderer2 == null) {
         return "MC_GL_RENDERER_OTHER";
      }

      openGlRenderer2 = openGlRenderer2.toLowerCase();
      return openGlRenderer2.startsWith("amd")
         ? "MC_GL_RENDERER_RADEON"
         : (
            openGlRenderer2.startsWith("ati")
               ? "MC_GL_RENDERER_RADEON"
               : (
                  openGlRenderer2.startsWith("radeon")
                     ? "MC_GL_RENDERER_RADEON"
                     : (
                        openGlRenderer2.startsWith("gallium")
                           ? "MC_GL_RENDERER_GALLIUM"
                           : (
                              openGlRenderer2.startsWith("intel")
                                 ? "MC_GL_RENDERER_INTEL"
                                 : (
                                    openGlRenderer2.startsWith("geforce")
                                       ? "MC_GL_RENDERER_GEFORCE"
                                       : (
                                          openGlRenderer2.startsWith("nvidia")
                                             ? "MC_GL_RENDERER_GEFORCE"
                                             : (
                                                openGlRenderer2.startsWith("quadro")
                                                   ? "MC_GL_RENDERER_QUADRO"
                                                   : (
                                                      openGlRenderer2.startsWith("nvs")
                                                         ? "MC_GL_RENDERER_QUADRO"
                                                         : (openGlRenderer2.startsWith("mesa") ? "MC_GL_RENDERER_MESA" : "MC_GL_RENDERER_OTHER")
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   private static void addMacroLine(StringBuilder sb, String name) {
      sb.append("#define ");
      sb.append(name);
      sb.append("\n");
   }

   public static String getVendor() {
      String openGlVendor2 = Config.openGlVendor;
      if (openGlVendor2 == null) {
         return "MC_GL_VENDOR_OTHER";
      }

      openGlVendor2 = openGlVendor2.toLowerCase();
      return openGlVendor2.startsWith("ati")
         ? "MC_GL_VENDOR_ATI"
         : (
            openGlVendor2.startsWith("intel")
               ? "MC_GL_VENDOR_INTEL"
               : (openGlVendor2.startsWith("nvidia") ? "MC_GL_VENDOR_NVIDIA" : (openGlVendor2.startsWith("x.org") ? "MC_GL_VENDOR_XORG" : "MC_GL_VENDOR_OTHER"))
         );
   }

   public static String getFixedMacroLines() {
      StringBuilder stringBuilder = new StringBuilder();
      addMacroLine(stringBuilder, "MC_VERSION", Config.getMinecraftVersionInt());
      addMacroLine(stringBuilder, "MC_GL_VERSION " + Config.getGlVersion().toInt());
      addMacroLine(stringBuilder, "MC_GLSL_VERSION " + Config.getGlslVersion().toInt());
      addMacroLine(stringBuilder, getOs());
      addMacroLine(stringBuilder, getVendor());
      addMacroLine(stringBuilder, getRenderer());
      return stringBuilder.toString();
   }

   public static ShaderMacro[] getExtensions() {
      if (extensionMacros == null) {
         String[] stringArray = Config.getOpenGlExtensions();
         ShaderMacro[] ashadermacro = new ShaderMacro[stringArray.length];

         for (int index = 0; index < stringArray.length; index++) {
            ashadermacro[index] = new ShaderMacro(text + stringArray[index], "");
         }

         extensionMacros = ashadermacro;
      }

      return extensionMacros;
   }

   private static void addMacroLine(StringBuilder sb, String name, float value) {
      sb.append("#define ");
      sb.append(name);
      sb.append(" ");
      sb.append(value);
      sb.append("\n");
   }

   public static String getPrefixMacro() {
      return text;
   }

   private static void addMacroLine(StringBuilder sb, String name, int value) {
      sb.append("#define ");
      sb.append(name);
      sb.append(" ");
      sb.append(value);
      sb.append("\n");
   }

   public static String getOs() {
      net.minecraft.util.Util.EnumOS util$enumos = Util.getOSType();
      switch (util$enumos) {
         case WINDOWS:
            return "MC_OS_WINDOWS";
         case OSX:
            return "MC_OS_MAC";
         case LINUX:
            return "MC_OS_LINUX";
         default:
            return "MC_OS_OTHER";
      }
   }

   public static String getOptionMacroLines() {
      StringBuilder stringBuilder = new StringBuilder();
      if (Shaders.configAntialiasingLevel > 0) {
         addMacroLine(stringBuilder, "MC_FXAA_LEVEL", Shaders.configAntialiasingLevel);
      }

      if (Shaders.configNormalMap) {
         addMacroLine(stringBuilder, "MC_NORMAL_MAP");
      }

      if (Shaders.configSpecularMap) {
         addMacroLine(stringBuilder, "MC_SPECULAR_MAP");
      }

      addMacroLine(stringBuilder, "MC_RENDER_QUALITY", Shaders.configRenderResMul);
      addMacroLine(stringBuilder, "MC_SHADOW_QUALITY", Shaders.configShadowResMul);
      addMacroLine(stringBuilder, "MC_HAND_DEPTH", Shaders.configHandDepthMul);
      if (Shaders.isOldHandLight()) {
         addMacroLine(stringBuilder, "MC_OLD_HAND_LIGHT");
      }

      if (Shaders.isOldLighting()) {
         addMacroLine(stringBuilder, "MC_OLD_LIGHTING");
      }

      return stringBuilder.toString();
   }
}
