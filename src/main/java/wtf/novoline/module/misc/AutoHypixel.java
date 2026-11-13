package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.GamemodeUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.ScheduledTask;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;

public class AutoHypixel extends Module {
   private final MultiComboSetting autoHypixel = settingManager.register(
      new MultiComboSetting(this, "Features").add("AutoPlay", true).add("AutoGG", false).add("Auto phase", false)
   );
   private final NumberSetting autoPlayDelayMs = settingManager.register(new NumberSetting(this, "AutoPlay Delay (ms)", 1000, 100, 5000) {
      @Override
      public boolean shouldShow() {
         return AutoHypixel.this.autoHypixel.isSelected(0);
      }
   });
   private final NumberSetting autoGGDelayMs = settingManager.register(new NumberSetting(this, "AutoGG Delay (ms)", 1000, 0, 5000) {
      @Override
      public boolean shouldShow() {
         return AutoHypixel.this.autoHypixel.isSelected(1);
      }
   });
   @EventLink
   private final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
      if (this.autoHypixel.isSelected(0)) {
         int i = (int)this.autoPlayDelayMs.getCurrentValue();
         this.showNotification(NotificationType.INFO, "AutoPlay", Language.translate("feature.autohypixel.auto_play.notification.sending", i), i);
         this.runLater(i, () -> minecraft.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("/play " + gameEndEvent.gameType)));
      }

      if (this.autoHypixel.isSelected(1)) {
         int i2 = (int)this.autoGGDelayMs.getCurrentValue();
         this.runLater(i2, () -> minecraft.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("gg")));
      }
   };
   @EventLink
   private final Listener<GamemodeUpdateEvent> onGamemodeUpdate = gamemodeUpdateEvent -> {
      if (gamemodeUpdateEvent.hypixelGameType == HypixelGameType.PRE_GAME && this.autoHypixel.isSelected(2)) {
         this.runLater(2300, () -> {
            minecraft.thePlayer.setPositionAndUpdate(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 5.0, minecraft.thePlayer.posZ);
            this.showNotification(NotificationType.INFO, "AutoHypixel", Language.translate("feature.autohypixel.auto_phase.notification.phased"), 3000L);
         });
      }
   };

   private void runLater(int i, final Runnable runnable) {
      novoline.getTaskScheduler().schedule(new ScheduledTask(i) {
         @Override
         public void run() {
            if (AutoHypixel.access$000().thePlayer != null) {
               runnable.run();
            }
         }
      });
   }

   static Minecraft access$000() {
      return minecraft;
   }

   public AutoHypixel() {
      super(Category.MISC, "AutoHypixel", "Automating routine Hypixel-specific tasks");
   }
}
