package net.optifine.shaders;

import net.minecraft.src.Config;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderProfile;

public class ShaderUtils {
   public static ShaderOption getShaderOption(String name, ShaderOption[] opts) {
      if (opts == null) {
         return null;
      }

      for (int index = 0; index < opts.length; index++) {
         ShaderOption shaderOption2 = opts[index];
         if (shaderOption2.getName().equals(name)) {
            return shaderOption2;
         }
      }

      return null;
   }

   public static boolean matchProfile(ShaderProfile prof, ShaderOption[] opts, boolean def) {
      if (prof == null) {
         return false;
      }

      if (opts == null) {
         return false;
      }

      String[] stringArray = prof.getOptions();

      for (int index = 0; index < stringArray.length; index++) {
         String text = stringArray[index];
         ShaderOption shaderOption = getShaderOption(text, opts);
         if (shaderOption != null) {
            String text2 = def ? shaderOption.getValueDefault() : shaderOption.getValue();
            String text3 = prof.getValue(text);
            if (!Config.equals(text2, text3)) {
               return false;
            }
         }
      }

      return true;
   }

   public static ShaderProfile detectProfile(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
      if (profs == null) {
         return null;
      }

      for (int index = 0; index < profs.length; index++) {
         ShaderProfile shaderProfile = profs[index];
         if (matchProfile(shaderProfile, opts, def)) {
            return shaderProfile;
         }
      }

      return null;
   }
}
