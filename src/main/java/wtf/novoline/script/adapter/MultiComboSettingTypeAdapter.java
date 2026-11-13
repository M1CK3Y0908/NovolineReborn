package wtf.novoline.script.adapter;

import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.SettingTypeAdapter.AbstractTypeAdapterChild;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.MultiComboSetting.Option;

public final class MultiComboSettingTypeAdapter extends AbstractTypeAdapter<MultiComboSetting> {
   public ScriptValue put(ScriptValue scriptValue, MultiComboSetting multiComboSetting) {
      scriptValue.putBiConsumer("add", multiComboSetting::add);
      scriptValue.putFunction("isSelected", multiComboSetting::isSelected);
      scriptValue.putFunction("byName", MappingUtils.o2mMappingFunction(Option.class, multiComboSetting::byName));
      scriptValue.putSupplier("getElements", MappingUtils.mappingListSupplier(Option.class, multiComboSetting::getElements));
      return scriptValue;
   }

   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(Option.class, new AbstractTypeAdapterChild(this.typeRegistry));
   }

   public MultiComboSettingTypeAdapter(TypeRegistry typeRegistry2) {
      super("MultiComboSetting", typeRegistry2);
   }
}
