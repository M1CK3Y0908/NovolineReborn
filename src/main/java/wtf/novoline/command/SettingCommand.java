package wtf.novoline.command;

import com.google.common.primitives.Floats;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import wtf.novoline.Novoline;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.Setting;
import wtf.novoline.setting.SettingManager;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.render.RenderUtils;

public class SettingCommand extends Command {
   private final SettingManager settingManager = Novoline.getNovoline().getSettingManager();

   @Override
   public void execute(String[] texts) {
      if (texts.length >= 3) {
         Setting bySignature = this.settingManager.getBySignature(texts[1]);
         if (bySignature == null) {
            this.sendLocalized("command.setting.invalid_signature", texts[1]);
            return;
         }

         if (bySignature instanceof CheckboxSetting checkboxSetting) {
            if (texts[2].equalsIgnoreCase("toggle")) {
               checkboxSetting.setEnabled(!checkboxSetting.isEnabled());
            } else {
               checkboxSetting.setEnabled(Boolean.parseBoolean(texts[2]));
            }
         } else if (bySignature instanceof NumberSetting numberSetting) {
            try {
               numberSetting.setCurrentValueIgnoreNormalize(Float.parseFloat(texts[2]));
            } catch (NumberFormatException numberFormatException) {
               this.sendLocalized("command.setting.parse_failed", Language.translate("command.setting.type_decimal"), texts[2]);
               return;
            }
         } else if (bySignature instanceof ComboSetting comboSetting) {
            try {
               int i = Integer.parseInt(texts[2]);
               int length2 = comboSetting.getElements().length;
               this.checkIndex(i, length2);
               comboSetting.setCurrentElement(i);
            } catch (NumberFormatException numberFormatException2) {
               this.sendLocalized("command.setting.parse_failed", Language.translate("command.setting.type_number"), texts[2]);
               return;
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
               this.sendLocalized("command.setting.invalid_value", indexOutOfBoundsException);
               return;
            }
         } else {
            Setting setting2 = bySignature;
            if (setting2 instanceof MultiComboSetting) {
               List elements = ((MultiComboSetting)setting2).getElements();

               try {
                  int i2 = Integer.parseInt(texts[2]);
                  this.checkIndex(i2, elements.size());
                  Option option = (Option)elements.get(i2);
                  String text = texts[3];
                  if (text.equals("toggle")) {
                     option.setSelected(!option.isSelected());
                  } else {
                     option.setSelected(Boolean.parseBoolean(text));
                  }

                  if (!texts[texts.length - 1].equals("-s")) {
                     this.sendLocalized("command.setting.mcb.updated_value", option.getName(), option.isSelected());
                  }

                  return;
               } catch (NumberFormatException numberFormatException3) {
                  this.sendLocalized("command.setting.parse_failed", Language.translate("command.setting.type_number"), texts[2]);
                  return;
               } catch (IndexOutOfBoundsException indexOutOfBoundsException2) {
                  this.sendLocalized("command.setting.invalid_value", indexOutOfBoundsException2);
                  return;
               }
            }

            if (!(bySignature instanceof ColorSetting colorSetting)) {
               this.sendLocalized("command.setting.invalid_type", bySignature);
               return;
            }

            try {
               if (texts[2].startsWith("#")) {
                  colorSetting.setColor(Color.decode(texts[2]).getRGB());
               } else if (texts[2].contains(",")) {
                  List list = Arrays.stream(texts[2].split(",")).map(Float::parseFloat).limit(4L).collect(Collectors.toList());
                  int size2 = list.size();
                  if (size2 < 3) {
                     this.sendLocalized("command.setting.invalid_value", Language.translate("command.setting.color.invalid_rgb"));
                     return;
                  }

                  if (size2 == 3) {
                     list.add(255.0F);
                  }

                  colorSetting.setColor(RenderUtils.packColor(Floats.toArray(list)));
               }
            } catch (IllegalArgumentException illegalArgumentException) {
               this.sendLocalized("command.setting.invalid_value", illegalArgumentException.getMessage());
               return;
            }
         }

         this.reportUnlessSilent(texts, bySignature);
      } else {
         this.sendLocalized("command.wrong_arguments");
         this.sendMessage("§c.set feature.module.checkbox.name <true/false/toggle>");
         this.sendMessage("§c.set feature.module.color.name <#HEX_CODE/255,255,255>");
         this.sendMessage("§c.set feature.module.combo.name <element index>");
         this.sendMessage("§c.set feature.module.multicombo.name <element index> <true/false/toggle>");
         this.sendMessage("§c.set feature.module.slider.name <number>");
         this.sendLocalized("command.setting.help_append_silent");
      }
   }

   private void reportUnlessSilent(String[] texts, Setting setting2) {
      if (!texts[texts.length - 1].equals("-s")) {
         this.reportValue(setting2);
      }
   }

   private void checkIndex(int i, int i2) {
      if (i < 0 || i >= i2) {
         throw new IndexOutOfBoundsException(i + " must be within [" + 0 + ", " + i2 + ")");
      }
   }

   private void reportValue(Setting setting2) {
      String text = null;
      Setting setting3 = setting2;
      if (setting3 instanceof CheckboxSetting) {
         text = String.valueOf(((CheckboxSetting)setting3).isEnabled());
      } else {
         setting3 = setting2;
         if (setting3 instanceof ComboSetting) {
            text = ((ComboSetting)setting3).getCurrentElementAsString();
         } else {
            setting3 = setting2;
            if (setting3 instanceof ColorSetting) {
               float[] fArray = RenderUtils.unpackColor255(((ColorSetting)setting3).getColor());
               text = String.format("rgba(%.0f,%.0f,%.0f,%.0f)", fArray[0], fArray[1], fArray[2], fArray[3]);
            } else {
               setting3 = setting2;
               if (setting3 instanceof NumberSetting) {
                  text = String.valueOf(((NumberSetting)setting3).getCurrentValue());
               }
            }
         }
      }

      if (text != null) {
         this.sendLocalized("command.setting.updated_value", setting2.getName(), text);
      } else {
         this.sendLocalized("command.setting.updated_value_generic", setting2.getName());
      }
   }

   public SettingCommand() {
      super("sets value for a setting", "setting", "set");
   }
}
