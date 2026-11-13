package wtf.novoline.setting;

import java.util.Locale;
import wtf.novoline.module.Module;
import wtf.novoline.setting.Setting.FeatureType;

public abstract class ModuleSetting extends Setting {
   private final Module module;
   private final ModuleSetting.CheckboxType checkboxType;

   public void interactCallback() {
   }

   public ModuleSetting(Module module2, String text, String text2, ModuleSetting.CheckboxType checkboxType2) {
      super(
         FeatureType.FEATURE,
         text,
         "feature." + module2.getName().replaceAll(" ", "").toLowerCase(Locale.ROOT) + "." + text2 + "." + text.replaceAll(" ", "").toLowerCase(Locale.ROOT)
      );
      this.module = module2;
      this.checkboxType = checkboxType2;
   }

   public ModuleSetting.CheckboxType getCheckboxType() {
      return this.checkboxType;
   }

   public boolean shouldShow() {
      return true;
   }

   public Module getModule() {
      return this.module;
   }

   public enum CheckboxType {
      CHECKBOX,
      COLOR,
      COMBO,
      MULTI_COMBO,
      SLIDER;
   }
}
