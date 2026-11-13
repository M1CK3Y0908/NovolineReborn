package wtf.novoline.module.visual;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.NumberSetting;

public class CameraClip extends Module {
   public final CheckboxSetting cameraClip = settingManager.register(new CheckboxSetting(this, "Override distance", false));
   public final NumberSetting distance = settingManager.register(new NumberSetting(this, "Distance", 10, 1, 100));

   public CameraClip() {
      super(Category.VISUAL, "CameraClip", "");
   }
}
