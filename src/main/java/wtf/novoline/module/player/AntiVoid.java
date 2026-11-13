package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.time.Stopwatch;

public class AntiVoid extends Module {
   private final ComboSetting antiVoid = settingManager.register(new ComboSetting(this, "Mode", 0, "Simple", "Advanced"));
   private final NumberSetting minFallDistance = settingManager.register(new NumberSetting(this, "Min fall distance", 3.0F, 1.0F, 10.0F) {
      @Override
      public boolean shouldShow() {
         return AntiVoid.this.antiVoid.getCurrentElement() == 1;
      }
   });
   private final NumberSetting maxFallDistance = settingManager.register(new NumberSetting(this, "Max fall distance", 3.0F, 1.0F, 10.0F) {
      @Override
      public boolean shouldShow() {
         return AntiVoid.this.antiVoid.getCurrentElement() == 1;
      }
   });
   private final NumberSetting cooldownMs = settingManager.register(new NumberSetting(this, "Cooldown (ms)", 150, 0, 500) {
      @Override
      public boolean shouldShow() {
         return AntiVoid.this.antiVoid.getCurrentElement() == 1;
      }
   });
   private final CheckboxSetting disableOnFail = settingManager.register(new CheckboxSetting(this, "Disable on fail", false) {
      @Override
      public boolean shouldShow() {
         return AntiVoid.this.antiVoid.getCurrentElement() == 1;
      }
   });
   private final CheckboxSetting enableScaffold = settingManager.register(new CheckboxSetting(this, "Enable Scaffold", false) {
      @Override
      public boolean shouldShow() {
         return AntiVoid.this.disableOnFail.isEnabled() && AntiVoid.this.antiVoid.getCurrentElement() == 1;
      }
   });
   private final NumberSetting setbackAttempts = settingManager.register(new NumberSetting(this, "Setback attempts", 5, 1, 20) {
      @Override
      public boolean shouldShow() {
         return AntiVoid.this.disableOnFail.isEnabled() && AntiVoid.this.antiVoid.getCurrentElement() == 1;
      }
   });
   private final NumberSetting resetDelayS = settingManager.register(new NumberSetting(this, "Reset delay (s)", 30, 5, 60) {
      @Override
      public boolean shouldShow() {
         return AntiVoid.this.disableOnFail.isEnabled() && AntiVoid.this.antiVoid.getCurrentElement() == 1;
      }
   });
   private final Stopwatch cooldownTimer = new Stopwatch();
   private final Stopwatch resetTimer = new Stopwatch();
   private int setbackCount = 0;
   private boolean voidDetected = false;
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.antiVoid.getCurrentElementAsString());
      boolean flag = !PlayerUtils.hasBlockBelow();
      if (this.antiVoid.getCurrentElement() == 0) {
         if (minecraft.thePlayer.fallDistance > 3.0F && flag) {
            preUpdateEvent.posX = -1922.0;
            preUpdateEvent.posY = -1922.0;
            preUpdateEvent.posZ = -1922.0;
            if (!this.voidDetected) {
               this.sendPacketUnregistered(
                  new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ + 0.2, false)
               );
            }

            this.voidDetected = true;
         } else {
            this.voidDetected = false;
         }
      } else {
         if (flag
            && !minecraft.thePlayer.capabilities.isFlying
            && minecraft.thePlayer.fallDistance > this.minFallDistance.getCurrentValue()
            && minecraft.thePlayer.fallDistance < this.maxFallDistance.getCurrentValue()
            && this.cooldownTimer.hasElapsedFloat(this.cooldownMs.getCurrentValue())) {
            if (this.disableOnFail.isEnabled() && !this.resetTimer.hasElapsedFloat(this.resetDelayS.getCurrentValue() * 1000.0F)) {
               return;
            }

            preUpdateEvent.posY = preUpdateEvent.posY + minecraft.thePlayer.fallDistance + 1.0 + Math.random() / 100.0;
            preUpdateEvent.onGround = false;
            if (this.disableOnFail.isEnabled() && ++this.setbackCount >= this.setbackAttempts.getCurrentValue()) {
               this.resetTimer.reset();
               if (this.enableScaffold.isEnabled()) {
                  this.showNotification(NotificationType.INFO, "AntiVoid", Language.translate("feature.antivoid.notification.enabled_scaffold"), 3000L);
                  ModuleManager.scaffold.setEnabled(true);
               }
            }

            this.cooldownTimer.reset();
         } else if (!flag) {
            this.setbackCount = 0;
         }
      }
   };

   public AntiVoid() {
      super(Category.PLAYER, "AntiVoid", "");
   }
}
