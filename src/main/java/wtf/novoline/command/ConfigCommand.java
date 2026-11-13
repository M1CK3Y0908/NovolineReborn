package wtf.novoline.command;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.hud.Notifications;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.Setting;
import wtf.novoline.setting.MultiComboSetting.Option;

public class ConfigCommand extends Command {
   public ConfigCommand() {
      super("config management", "config", "cfg");
   }

   @Override
   public void execute(String[] texts) {
      if (texts.length == 2 && texts[1].equalsIgnoreCase("list")) {
         List configs2 = this.getConfigs();
         if (configs2.isEmpty()) {
            this.sendLocalized("command.config.listempty");
         } else {
            String text = String.join("§7,§3 ", configs2);
            this.sendLocalized("command.config.cfglist", configs2.size(), text + "§7.");
         }
      } else if (texts.length >= 2 && texts[1].equalsIgnoreCase("reset")) {
         HashSet hashSet = new HashSet();
         if (texts.length > 2) {
            Arrays.stream(Arrays.copyOfRange(texts, 2, texts.length)).map(Category::byName).forEach(hashSet::add);
         }

         for (Setting setting2 : this.novoline.getSettingManager().getSettings()) {
            if (!(setting2 instanceof ModuleSetting moduleSetting) || !hashSet.contains(moduleSetting.getModule().getCategory())) {
               if (setting2 instanceof NumberSetting numberSetting) {
                  numberSetting.setCurrentValue(numberSetting.getDefaultState());
               } else if (setting2 instanceof CheckboxSetting checkboxSetting) {
                  checkboxSetting.setEnabled(checkboxSetting.isDefaultState());
               } else if (setting2 instanceof ComboSetting comboSetting) {
                  comboSetting.setCurrentElement(comboSetting.getDefaultState());
               } else {
                  Setting setting3 = setting2;
                  if (setting3 instanceof MultiComboSetting) {
                     for (Option option : ((MultiComboSetting)setting3).getElements()) {
                        option.setSelected(option.isDefaultState());
                     }
                  } else if (setting2 instanceof ColorSetting colorSetting) {
                     colorSetting.setColor(colorSetting.getDefaultColor());
                  }
               }
            }
         }

         Notifications notifications2 = ModuleManager.notifications;
         boolean flag = notifications2.wasEnabledAtInit();
         notifications2.setEnabled(false);

         for (Module module2 : ModuleManager.modules) {
            if (!(module2 instanceof Notifications) && !hashSet.contains(module2.getCategory())) {
               module2.setEnabled(module2.wasEnabledAtInit());
            }
         }

         notifications2.setEnabled(flag);
         this.sendLocalized("command.config.reset");
      } else if ((texts.length == 3 || texts.length == 4) && texts[1].equalsIgnoreCase("save")) {
         List configs3 = this.getConfigs();
         if (configs3.contains(texts[2]) && texts.length != 4) {
            this.sendLocalized("command.config.cfg_already_exists", texts[2]);
            this.sendLocalized("command.config.cfg_already_exists2");
         } else {
            this.getConfigManager().saveConfig(texts[2]);
            this.sendLocalized("command.config.cfg_saved", texts[2]);
         }
      } else if (texts.length != 3) {
         this.sendLocalized("command.wrong_arguments");
         this.sendMessage("§c.cfg list");
         this.sendMessage("§c.cfg reset [exclusions...]");
         this.sendMessage("§c.cfg load <name>");
         this.sendMessage("§c.cfg save <name>");
         this.sendMessage("§c.cfg save <name> force");
         this.sendMessage("§c.cfg download <link>");
      } else {
         if (texts[1].equalsIgnoreCase("load")) {
            List configs4 = this.getConfigs();
            if (configs4.contains(texts[2])) {
               this.novoline.getMacroManager().getMacros().clear();

               for (Module module3 : ModuleManager.modules) {
                  module3.setKeyBind(0);
               }

               this.getConfigManager().loadConfig(texts[2]);
               this.sendLocalized("command.config.cfg_loaded", texts[2]);
            } else {
               this.sendLocalized("command.config.cfg_not_found", texts[2]);
            }
         } else if (texts[1].equalsIgnoreCase("download")) {
            try {
               URI uri = URI.create(texts[2]);
               String path2 = uri.getPath();
               if (path2.contains("/")) {
                  path2 = path2.substring(path2.lastIndexOf("/") + 1);
               } else {
                  path2 = "unnamed_config_" + ThreadLocalRandom.current().nextInt();
               }

               this.sendMessage("Downloading config...");
               String text2 = path2;
               new Thread(
                     () -> {
                        try {
                           Files.writeString(
                              Directories.CONFIGS_DIR.resolve(text2.endsWith(".novo") ? text2 : text2 + ".novo"),
                              IOUtils.toString(uri, StandardCharsets.UTF_8)
                           );
                           this.sendMessage("Config saved as " + text2);
                        } catch (Exception exception) {
                           Novoline.taggedLogger.error(exception, "Couldn't download config at {}", texts[2]);
                           this.sendMessage(EnumChatFormatting.RED + "Couldn't download config: " + exception.getMessage());
                        }
                     },
                     "Config-Download-Thread"
                  )
                  .start();
            } catch (Exception exception2) {
               Novoline.taggedLogger.error(exception2, "Couldn't parse config URL: {}", texts[2]);
               this.sendMessage(EnumChatFormatting.RED + "Couldn't parse config URL: " + exception2.getMessage());
            }
         }
      }
   }

   private List getConfigs() {
      try {
         return Files.list(Directories.CONFIGS_DIR)
            .filter(path -> Files.isRegularFile(path))
            .filter(path -> path.getFileName().toString().endsWith(".novo"))
            .map(path -> FilenameUtils.removeExtension(path.getFileName().toString()))
            .collect(Collectors.toList());
      } catch (Exception exception) {
         Novoline.taggedLogger.error(exception, "Couldn't load configs");
         throw new RuntimeException(exception);
      }
   }
}
