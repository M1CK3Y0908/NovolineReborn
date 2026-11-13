package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.novoline.event.MoveEntityEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;

public class SafeWalk extends Module {
   private final CheckboxSetting safeWalk = settingManager.register(new CheckboxSetting(this, "Only on ground", true));
   @EventLink
   private final Listener<MoveEntityEvent> onMoveEntity = moveEntityEvent -> {
      if (!this.safeWalk.isEnabled() || minecraft.thePlayer.onGround) {
         moveEntityEvent.cancel();
      }
   };

   public SafeWalk() {
      super(Category.WORLD, "SafeWalk", "Prevents you from falling from block edges");
   }
}
