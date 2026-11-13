package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;

public class Watermark extends Module {
   public final ComboSetting watermark = settingManager.register(new ComboSetting(this, "Type", 0, "OneTap", "NeverLose"));
   public final ColorSetting main = settingManager.register(new ColorSetting(this, "Main", -5917444));
   public final CheckboxSetting showPlayTime = settingManager.register(new CheckboxSetting(this, "Show play time", true));

   public Watermark() {
      super(Category.HUD, "Watermark", "");
      this.setEnabled(true);
   }
}
