package wtf.novoline.script.adapter;

import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.setting.CheckboxSetting;

public final class CheckboxSettingTypeAdapter extends AbstractTypeAdapter<CheckboxSetting> {
   public ScriptValue put(ScriptValue scriptValue, CheckboxSetting checkboxSetting) {
      scriptValue.putRunnable("toggle", checkboxSetting::toggle);
      scriptValue.putSupplier("isEnabled", checkboxSetting::isEnabled);
      scriptValue.putConsumer("setEnabled", checkboxSetting::setEnabled);
      scriptValue.putSupplier("isDefaultState", checkboxSetting::isDefaultState);
      return scriptValue;
   }

   public CheckboxSettingTypeAdapter(TypeRegistry typeRegistry) {
      super("CheckboxSetting", typeRegistry);
   }
}
