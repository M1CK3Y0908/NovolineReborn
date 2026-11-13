package wtf.novoline.module.visual;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;

public class Weather extends Module {
   public final ComboSetting weather = settingManager.register(new ComboSetting(this, "Type", 0, "Snow", "Rain"));
   public final CheckboxSetting customColor = settingManager.register(new CheckboxSetting(this, "Custom color", true));
   public final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -12986881) {
      @Override
      public boolean shouldShow() {
         return Weather.this.customColor.isEnabled();
      }
   });

   public Weather() {
      super(Category.VISUAL, "Weather", "");
   }
}
