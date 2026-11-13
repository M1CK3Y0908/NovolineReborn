package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;

public class TargetHUD extends Module {
   public final ComboSetting targetHUD = settingManager.register(new ComboSetting(this, "Mode", 0, "Type 1", "Type 2", "Type 3"));
   public final MultiComboSetting addons = settingManager.register(new MultiComboSetting(this, "Addons").add("Blur", true).add("Shadow", true));
   public final NumberSetting blurRadius = settingManager.register(new NumberSetting(this, "Blur radius", 18, 1, 40) {
      @Override
      public boolean shouldShow() {
         return TargetHUD.this.addons.isSelected(0);
      }
   });
   public final NumberSetting blurSigma = settingManager.register(new NumberSetting(this, "Blur sigma", 0.1F, 0.01F, 10.0F) {
      @Override
      public boolean shouldShow() {
         return TargetHUD.this.addons.isSelected(0);
      }
   });

   public TargetHUD() {
      super(Category.HUD, "TargetHUD", "");
      this.setEnabled(true);
   }
}
