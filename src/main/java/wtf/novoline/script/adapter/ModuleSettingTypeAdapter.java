package wtf.novoline.script.adapter;

import wtf.novoline.module.Module;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;

public final class ModuleSettingTypeAdapter extends AbstractTypeAdapter<ModuleSetting> {
   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(CheckboxSetting.class, new CheckboxSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(ComboSetting.class, new ComboSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(NumberSetting.class, new NumberSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(MultiComboSetting.class, new MultiComboSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(ColorSetting.class, new ColorSettingTypeAdapter(this.typeRegistry));
   }

   public ModuleSettingTypeAdapter(TypeRegistry typeRegistry2) {
      super("FeatureSetting", typeRegistry2);
      this.addSubTypes(
         ScriptType.createScriptType("CheckboxSetting", CheckboxSetting.class),
         ScriptType.createScriptType("ComboSetting", ComboSetting.class),
         ScriptType.createScriptType("SliderSetting", NumberSetting.class),
         ScriptType.createScriptType("MultiComboSetting", MultiComboSetting.class),
         ScriptType.createScriptType("ColorSetting", ColorSetting.class)
      );
   }

   public ScriptValue put(ScriptValue scriptValue, ModuleSetting moduleSetting) {
      scriptValue.putEnum("featureSettingType", moduleSetting.getCheckboxType());
      scriptValue.putSupplier("getReference", MappingUtils.mappingSupplier(Module.class, moduleSetting::getModule));
      scriptValue.putSupplier("shouldShow", moduleSetting::shouldShow);
      scriptValue.putRunnable("interactCallback", moduleSetting::interactCallback);
      return scriptValue;
   }
}
