package net.optifine.shaders.config;

import java.util.ArrayList;
import net.optifine.Lang;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;

public class ShaderOptionProfile extends ShaderOption {
   private ShaderProfile[] profiles = null;
   private static final String text4 = "<custom>";
   private static final String text22 = "<profile>";
   private ShaderOption[] options = null;

   @Override
   public String getValueColor(String val) {
      return val.equals("<custom>") ? "§c" : "§a";
   }

   @Override
   public String getDescriptionText() {
      String text = Shaders.translate("profile.comment", (String)null);
      if (text != null) {
         return text;
      }

      StringBuffer stringBuffer = new StringBuffer();

      for (int index = 0; index < this.profiles.length; index++) {
         String name = this.profiles[index].getName();
         if (name != null) {
            String text2 = Shaders.translate("profile." + name + ".comment", (String)null);
            if (text2 != null) {
               stringBuffer.append(text2);
               if (!text2.endsWith(". ")) {
                  stringBuffer.append(". ");
               }
            }
         }
      }

      return stringBuffer.toString();
   }

   public ShaderOptionProfile(ShaderProfile[] profiles, ShaderOption[] options) {
      super("<profile>", "", detectProfileName(profiles, options), getProfileNames(profiles), detectProfileName(profiles, options, true), (String)null);
      this.profiles = profiles;
      this.options = options;
   }

   @Override
   public String getNameText() {
      return Lang.get("of.shaders.profile");
   }

   private ShaderProfile getProfile(String name) {
      for (int index = 0; index < this.profiles.length; index++) {
         ShaderProfile shaderProfile2 = this.profiles[index];
         if (shaderProfile2.getName().equals(name)) {
            return shaderProfile2;
         }
      }

      return null;
   }

   private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts) {
      return detectProfileName(profs, opts, false);
   }

   private static String[] getProfileNames(ShaderProfile[] profs) {
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < profs.length; index++) {
         ShaderProfile shaderProfile2 = profs[index];
         arrayList.add(shaderProfile2.getName());
      }

      arrayList.add("<custom>");
      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   public void updateProfile() {
      ShaderProfile shaderProfile = this.getProfile(this.getValue());
      if (shaderProfile == null || !ShaderUtils.matchProfile(shaderProfile, this.options, false)) {
         String text = detectProfileName(this.profiles, this.options);
         this.setValue(text);
      }
   }

   @Override
   public void nextValue() {
      super.nextValue();
      if (this.getValue().equals("<custom>")) {
         super.nextValue();
      }

      this.applyProfileOptions();
   }

   @Override
   public String getValueText(String val) {
      return val.equals("<custom>") ? Lang.get("of.general.custom", "<custom>") : Shaders.translate("profile." + val, val);
   }

   private void applyProfileOptions() {
      ShaderProfile shaderProfile = this.getProfile(this.getValue());
      if (shaderProfile != null) {
         String[] stringArray = shaderProfile.getOptions();

         for (int index = 0; index < stringArray.length; index++) {
            String text3 = stringArray[index];
            ShaderOption shaderOption = this.getOption(text3);
            if (shaderOption != null) {
               String text2 = shaderProfile.getValue(text3);
               shaderOption.setValue(text2);
            }
         }
      }
   }

   private ShaderOption getOption(String name) {
      for (int index = 0; index < this.options.length; index++) {
         ShaderOption shaderOption2 = this.options[index];
         if (shaderOption2.getName().equals(name)) {
            return shaderOption2;
         }
      }

      return null;
   }

   private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
      ShaderProfile shaderProfile = ShaderUtils.detectProfile(profs, opts, def);
      return shaderProfile == null ? "<custom>" : shaderProfile.getName();
   }
}
