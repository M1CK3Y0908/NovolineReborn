package wtf.novoline.script.adapter;

import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.setting.NumberSetting;

public final class NumberSettingTypeAdapter extends AbstractTypeAdapter<NumberSetting> {
   public ScriptValue put(ScriptValue scriptValue, NumberSetting numberSetting) {
      scriptValue.putSupplier("getMinValue", numberSetting::getMinValue);
      scriptValue.putSupplier("getMaxValue", numberSetting::getMaxValue);
      scriptValue.putSupplier("getCurrentValue", numberSetting::getCurrentValue);
      scriptValue.putSupplier("getDefaultState", numberSetting::getDefaultState);
      scriptValue.putConsumer("setCurrentValue", numberSetting::setCurrentValue);
      scriptValue.putConsumer("setCurrentValueIgnoreNormalize", numberSetting::setCurrentValueIgnoreNormalize);
      scriptValue.putEnum("type", numberSetting.getType());
      return scriptValue;
   }

   public NumberSettingTypeAdapter(TypeRegistry typeRegistry) {
      super("SliderSetting", typeRegistry);
   }
}
