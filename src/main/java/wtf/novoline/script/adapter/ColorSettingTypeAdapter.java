package wtf.novoline.script.adapter;

import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.setting.ColorSetting;

public final class ColorSettingTypeAdapter extends AbstractTypeAdapter<ColorSetting> {
   public ColorSettingTypeAdapter(TypeRegistry typeRegistry) {
      super("ColorSetting", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, ColorSetting colorSetting) {
      scriptValue.putConsumer("setColor", colorSetting::setColor);
      scriptValue.putSupplier("getColor", colorSetting::getColor);
      return scriptValue;
   }
}
