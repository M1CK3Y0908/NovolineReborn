package net.optifine.shaders.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;

public class MacroProcessor {
   public static String process(String strIn) throws IOException {
      StringReader stringReader = new StringReader(strIn);
      BufferedReader bufferedReader = new BufferedReader(stringReader);
      MacroState macroState = new MacroState();
      StringBuilder stringBuilder = new StringBuilder();

      while (true) {
         String line = bufferedReader.readLine();
         if (line == null) {
            return stringBuilder.toString();
         }

         if (macroState.processLine(line) && !MacroState.isMacroLine(line)) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
         }
      }
   }

   public static InputStream process(InputStream in, String path) throws IOException {
      String text = Config.readInputStream(in, "ASCII");
      String text2 = getMacroHeader(text);
      if (!text2.isEmpty()) {
         text = text2 + text;
         if (Shaders.saveFinalShaders) {
            String text3 = path.replace(':', '/') + ".pre";
            Shaders.saveShader(text3, text);
         }

         text = process(text);
      }

      if (Shaders.saveFinalShaders) {
         String text4 = path.replace(':', '/');
         Shaders.saveShader(text4, text);
      }

      byte[] bytes = text.getBytes("ASCII");
      return new ByteArrayInputStream(bytes);
   }

   private static String getMacroHeader(String str) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      ArrayList arrayList = null;
      StringReader stringReader = new StringReader(str);
      BufferedReader bufferedReader = new BufferedReader(stringReader);

      while (true) {
         String line = bufferedReader.readLine();
         if (line == null) {
            return stringBuilder.toString();
         }

         if (MacroState.isMacroLine(line)) {
            if (stringBuilder.length() == 0) {
               stringBuilder.append(ShaderMacros.getFixedMacroLines());
            }

            if (arrayList == null) {
               arrayList = new ArrayList<>(Arrays.asList(ShaderMacros.getExtensions()));
            }

            Iterator iterator2 = arrayList.iterator();

            while (iterator2.hasNext()) {
               ShaderMacro shaderMacro = (ShaderMacro)iterator2.next();
               if (line.contains(shaderMacro.getName())) {
                  stringBuilder.append(shaderMacro.getSourceLine());
                  stringBuilder.append("\n");
                  iterator2.remove();
               }
            }
         }
      }
   }
}
