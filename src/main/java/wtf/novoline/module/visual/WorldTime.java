package wtf.novoline.module.visual;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.NumberSetting;

public class WorldTime extends Module {
   public final NumberSetting worldTime = settingManager.register(new NumberSetting(this, "Time", 12, 0, 24));

   public WorldTime() {
      super(Category.VISUAL, "WorldTime", "");
   }
}
