package wtf.novoline.module.combat;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.NumberSetting;

public class Reach extends Module {
   private final NumberSetting reach = settingManager.register(new NumberSetting(this, "Attack reach", 3.5F, 3.0F, 6.0F));
   private final NumberSetting blockReach = settingManager.register(new NumberSetting(this, "Block reach", 3.5F, 3.0F, 6.0F));

   public Reach() {
      super(Category.COMBAT, "Reach", "Increases reach");
   }

   public NumberSetting getReach() {
      return this.reach;
   }

   public NumberSetting getBlockReach() {
      return this.blockReach;
   }
}
