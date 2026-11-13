package wtf.novoline.script.adapter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import wtf.novoline.Novoline;
import wtf.novoline.module.Module;
import wtf.novoline.module.combat.Antibot;
import wtf.novoline.module.combat.Killaura;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeAdapter;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.setting.ModuleSetting;

public class ModuleTypeAdapter extends AbstractTypeAdapter<Module> {
   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(Killaura.class, new ModuleTypeAdapter.AbstractTypeAdapterChild(this.typeRegistry));
      this.typeRegistry.put(Antibot.class, new ModuleTypeAdapter.AbstractTypeAdapterChild2(this.typeRegistry));
   }

   public ModuleTypeAdapter(TypeRegistry typeRegistry2) {
      super("AbstractModule", typeRegistry2);
      this.addSubTypes(ScriptType.createScriptType("KillauraFeature", Killaura.class), ScriptType.createScriptType("AntibotFeature", Antibot.class));
   }

   public ScriptValue put(ScriptValue scriptValue, Module module2) {
      scriptValue.put("name", module2.getName());
      scriptValue.put("description", module2.getDescription());
      scriptValue.putEnum("category", module2.getCategory());
      scriptValue.putRunnable("toggle", module2::toggle);
      scriptValue.putConsumer("setEnabled", module2::setEnabled);
      scriptValue.putSupplier("isEnabled", module2::isEnabled);
      scriptValue.putSupplier("getSuffix", module2::getSuffix);
      scriptValue.putConsumer("setSuffix", module2::setSuffix);
      scriptValue.putSupplier("getKeyBind", module2::getKeyBind);
      scriptValue.putConsumer("setKeyBind", module2::setKeyBind);
      scriptValue.putFunction(
         "getSetting",
         string -> {
            ModuleSetting moduleSetting2 = Novoline.getNovoline()
               .getSettingManager()
               .getSettings()
               .stream()
               .filter(ModuleSetting.class::isInstance)
               .map(ModuleSetting.class::cast)
               .filter(moduleSetting -> moduleSetting.getModule().equals(module2))
               .filter(moduleSetting -> moduleSetting.getName().equals(string))
               .findFirst()
               .orElse(null);
            return moduleSetting2 == null ? null : ((TypeAdapter<ModuleSetting>)this.typeRegistry.getTypeAdapter(moduleSetting2.getClass())).pack(moduleSetting2);
         }
      );
      return scriptValue;
   }

   public static final class AbstractTypeAdapterChild extends AbstractTypeAdapter<Killaura> {
      public ScriptValue put(ScriptValue scriptValue, Killaura killaura) {
         scriptValue.putSupplier("getTarget", MappingUtils.mappingSupplier(Entity.class, () -> killaura.entityLivingBase));
         scriptValue.putSupplier("isAutoBlocked", killaura::isAutoBlocked);
         scriptValue.putSupplier("getRotYaw", killaura::getRotYaw);
         scriptValue.putSupplier("getRotPitch", killaura::getRotPitch);
         return scriptValue;
      }

      public AbstractTypeAdapterChild(TypeRegistry typeRegistry2) {
         super("KillauraFeature", typeRegistry2);
      }
   }

   public static final class AbstractTypeAdapterChild2 extends AbstractTypeAdapter<Antibot> {
      public ScriptValue put(ScriptValue scriptValue, Antibot antibot) {
         scriptValue.putFunction("isPlayerBot", MappingUtils.m2oMappingFunction(EntityPlayer.class, antibot::isPlayerBot));
         return scriptValue;
      }

      public AbstractTypeAdapterChild2(TypeRegistry typeRegistry2) {
         super("AntibotFeature", typeRegistry2);
      }
   }
}
