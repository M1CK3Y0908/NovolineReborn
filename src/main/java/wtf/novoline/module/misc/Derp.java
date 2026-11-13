package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.concurrent.ThreadLocalRandom;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.time.Stopwatch;

public class Derp extends Module {
   private final NumberSetting derp = settingManager.register(new NumberSetting(this, "Delay (ms)", 50, 0, 500));
   private final Stopwatch rotateTimer = new Stopwatch();
   @EventLink(0)
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.rotateTimer.hasElapsedFloat(this.derp.getCurrentValue())) {
         preUpdateEvent.rotationYaw = ThreadLocalRandom.current().nextInt(-90, 90);
         preUpdateEvent.rotationPitch = ThreadLocalRandom.current().nextInt(-90, 90);
         minecraft.thePlayer.rotationYawHead = preUpdateEvent.rotationYaw;
         minecraft.thePlayer.rotationPitchHead = preUpdateEvent.rotationPitch;
         this.rotateTimer.reset();
      }
   };

   public Derp() {
      super(Category.MISC, "Derp", "Spins your head rapidly");
   }
}
