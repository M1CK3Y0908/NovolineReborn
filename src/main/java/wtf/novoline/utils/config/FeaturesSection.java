package wtf.novoline.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import wtf.novoline.Novoline;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.Module.PressType;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.Setting;
import wtf.novoline.setting.SettingManager;
import wtf.novoline.setting.MultiComboSetting.Option;

public class FeaturesSection extends ConfigSection {
   private final SettingManager settingManager = Novoline.getNovoline().getSettingManager();
   private final boolean includeHotkeys;

   public FeaturesSection(boolean flag) {
      super("features");
      this.includeHotkeys = flag;
   }

   @Override
   public void deserialize(JsonObject jsonObject) {
      for (Module module2 : ModuleManager.modules) {
         JsonObject asJsonObject = jsonObject.getAsJsonObject(module2.getName());
         if (asJsonObject != null) {
            module2.setEnabled(asJsonObject.get("enabled").getAsBoolean());
            if (this.includeHotkeys) {
               module2.setKeyBind(asJsonObject.get("hotkey").getAsInt());
               if (asJsonObject.has("hotkey_press_type")) {
                  String asString = asJsonObject.get("hotkey_press_type").getAsString();
                  module2.setPressType(PressType.valueOf(asString));
               }
            }

            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
            if (asJsonObject2 != null) {
               for (Setting setting2 : this.settingManager.getListByFeature(module2)) {
                  JsonElement jsonElement = asJsonObject2.get(setting2.getSignature());
                  if (jsonElement instanceof JsonPrimitive) {
                     if (setting2 instanceof CheckboxSetting checkboxSetting) {
                        checkboxSetting.setEnabled(jsonElement.getAsBoolean());
                     } else {
                        Setting setting3 = setting2;
                        if (setting3 instanceof NumberSetting) {
                           ((NumberSetting)setting3).setCurrentValueIgnoreNormalize(jsonElement.getAsFloat());
                        } else if (setting2 instanceof ComboSetting comboSetting) {
                           comboSetting.setCurrentElementByName(jsonElement.getAsString());
                        } else if (setting2 instanceof ColorSetting colorSetting) {
                           colorSetting.setColor(jsonElement.getAsInt());
                        }
                     }
                  } else if (jsonElement instanceof JsonArray) {
                     Setting setting4 = setting2;
                     if (setting4 instanceof MultiComboSetting) {
                        List elements = ((MultiComboSetting)setting4).getElements();
                        JsonArray jsonArray = (JsonArray)jsonElement;

                        for (Option option : (Iterable<MultiComboSetting.Option>) elements) {
                           option.setSelected(false);

                           for (JsonElement jsonElement2 : jsonArray) {
                              if (option.getName().equals(jsonElement2.getAsString())) {
                                 option.setSelected(true);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public JsonObject serialize() {
      JsonObject jsonObject = new JsonObject();

      for (Module module2 : ModuleManager.modules) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("enabled", module2.isEnabled());
         jsonObject2.addProperty("hotkey", module2.getKeyBind());
         jsonObject2.addProperty("hotkey_press_type", module2.getPressType().name());
         JsonObject jsonObject3 = new JsonObject();

         for (Setting setting2 : this.settingManager.getListByFeature(module2)) {
            String signature = setting2.getSignature();
            if (setting2 instanceof CheckboxSetting checkboxSetting) {
               jsonObject3.addProperty(signature, checkboxSetting.isEnabled());
            } else if (setting2 instanceof NumberSetting numberSetting) {
               jsonObject3.addProperty(signature, numberSetting.getCurrentValue());
            } else if (setting2 instanceof ComboSetting comboSetting) {
               jsonObject3.addProperty(signature, comboSetting.getCurrentElementAsString());
            } else {
               Setting setting3 = setting2;
               if (!(setting3 instanceof MultiComboSetting)) {
                  if (setting2 instanceof ColorSetting colorSetting) {
                     jsonObject3.addProperty(signature, colorSetting.getColor());
                  }
               } else {
                  List elements = ((MultiComboSetting)setting3).getElements();
                  JsonArray jsonArray = new JsonArray(elements.size());

                  for (Option option : (Iterable<MultiComboSetting.Option>) elements) {
                     if (option.isSelected()) {
                        jsonArray.add(option.getName());
                     }
                  }

                  jsonObject3.add(signature, jsonArray);
               }
            }
         }

         if (jsonObject3.entrySet().size() > 0) {
            jsonObject2.add("settings", jsonObject3);
         }

         jsonObject.add(module2.getName(), jsonObject2);
      }

      return jsonObject;
   }
}
