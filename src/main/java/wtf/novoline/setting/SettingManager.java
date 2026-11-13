package wtf.novoline.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import wtf.novoline.module.Module;
import wtf.novoline.setting.Setting.FeatureType;

public class SettingManager {
   private final List<Setting> settings = new ArrayList<>();
   private final Map<String, Setting> settingsBySignature = new HashMap<>();

   public List<Setting> getSettings() {
      return this.settings;
   }

   public <T extends Setting> T register(T value) {
      this.settings.add(value);
      this.settingsBySignature.put(value.getSignature(), value);
      return (T)value;
   }

   public List<Setting> getListByFeature(Module module2) {
      return this.settings
         .stream()
         .filter(setting2 -> setting2.getSettingType().equals(FeatureType.FEATURE) && ((ModuleSetting)setting2).getModule().getClass().equals(module2.getClass()))
         .collect(Collectors.toList());
   }

   public void unregister(Setting setting3) {
      this.settings.remove(setting3);
      this.settingsBySignature.remove(setting3.getSignature());
   }

   @Nullable
   public Setting getBySignature(String text) {
      return this.settingsBySignature.get(text);
   }
}
