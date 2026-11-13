package wtf.novoline.script.adapter;

import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.Setting;
import wtf.novoline.setting.MultiComboSetting.Option;

public class SettingTypeAdapter extends AbstractTypeAdapter<Setting> {
   public ScriptValue put(ScriptValue scriptValue, Setting setting2) {
      scriptValue.putSupplier("getName", setting2::getName);
      scriptValue.putSupplier("getSignature", setting2::getSignature);
      scriptValue.putEnum("type", setting2.getSettingType());
      return scriptValue;
   }

   public SettingTypeAdapter(TypeRegistry typeRegistry2) {
      super("Setting", typeRegistry2);
      this.addSubTypes(ScriptType.createScriptType("FeatureSetting", ModuleSetting.class));
   }

   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(ModuleSetting.class, new ModuleSettingTypeAdapter(this.typeRegistry));
   }

   public static final class AbstractTypeAdapterChild extends AbstractTypeAdapter<Option> {
      public AbstractTypeAdapterChild(TypeRegistry typeRegistry2) {
         super("MultiComboSetting.Element", typeRegistry2);
      }

      public ScriptValue put(ScriptValue scriptValue, Option option) {
         scriptValue.putSupplier("isSelected", option::isSelected);
         scriptValue.putSupplier("isDefaultState", option::isDefaultState);
         scriptValue.putConsumer("setSelected", option::setSelected);
         scriptValue.putSupplier("getName", option::getName);
         scriptValue.putRunnable("toggleSelection", option::toggleSelection);
         return scriptValue;
      }
   }
}
