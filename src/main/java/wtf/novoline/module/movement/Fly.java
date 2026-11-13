package wtf.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.player.PlayerUtils;

public class Fly extends Module {
   private final ComboSetting fly = settingManager.register(new ComboSetting(this, "Mode", 0, "Creative", "Motion", "DamageBoost", "Hypixel"));
   private final NumberSetting speed = settingManager.register(new NumberSetting(this, "Speed", 2.0F, 0.0F, 10.0F) {
      @Override
      public boolean shouldShow() {
         return Fly.this.fly.getCurrentElement() == 1;
      }
   });
   private final NumberSetting boostValue = settingManager.register(new NumberSetting(this, "Boost value", 0.5F, 0.0F, 1.0F) {
      @Override
      public boolean shouldShow() {
         return Fly.this.fly.getCurrentElement() == 2;
      }
   });
   private boolean jumped;
   private double boostSpeed = 0.0;
   private boolean hovering;
   private int ticksSinceDamage = 0;
   private int intValue2 = 0;
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.fly.getCurrentElementAsString());
      int currentElement = this.fly.getCurrentElement();
      if (currentElement == 0) {
         minecraft.thePlayer.capabilities.isFlying = true;
      } else if (currentElement == 1) {
         minecraft.thePlayer.motionY = 0.0;
         if (PlayerUtils.isMoving()) {
            PlayerUtils.setMotionInMoveDirection(this.speed.getCurrentValue());
         } else {
            PlayerUtils.setMotionInMoveDirection(0.0);
         }

         if (minecraft.gameSettings.keyBindJump.isKeyDown()) {
            minecraft.thePlayer.motionY += 0.6F;
         } else if (minecraft.gameSettings.keyBindSneak.isKeyDown()) {
            minecraft.thePlayer.motionY -= 0.6F;
         }
      } else if (currentElement == 3) {
         if (!this.jumped && minecraft.thePlayer.onGround) {
            minecraft.thePlayer.motionY = 0.42F;
            this.jumped = true;
            return;
         }

         if (minecraft.thePlayer.onGround && !this.hovering) {
            this.sendPacketUnregistered(new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 0.42F, minecraft.thePlayer.posZ, true));
            this.hovering = true;
         }

         if (this.hovering) {
            minecraft.thePlayer.motionY = 0.0;
         }
      }
   };
   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      int currentElement = this.fly.getCurrentElement();
      if (currentElement == 2) {
         minecraft.thePlayer.setSprinting(true);
         this.ticksSinceDamage++;
         if (minecraft.thePlayer.hurtTime > 0 && minecraft.thePlayer.hurtTime < 7) {
            if (minecraft.thePlayer.hurtTime > 4) {
               this.ticksSinceDamage = 0;
            }

            if (minecraft.thePlayer.onGround) {
               this.boostSpeed = (int)this.boostValue.getCurrentValue();
               this.boostSpeed = this.boostSpeed * (minecraft.thePlayer.isPotionActive(Potion.jump) ? 2.0 : 2.15);
               playerMoveEvent.y = minecraft.thePlayer.motionY = 4.0;
            }
         }

         if (this.ticksSinceDamage > 12 && this.boostSpeed > 0.0) {
            this.boostSpeed *= 0.93;
         }

         playerMoveEvent.setSpeed(Math.max(this.boostSpeed, PlayerUtils.getDefaultMoveSpeed()));
      } else if (currentElement == 3) {
         if (!this.hovering) {
            playerMoveEvent.x = playerMoveEvent.z = 0.0;
         } else {
            playerMoveEvent.setSpeed(PlayerUtils.getDefaultMoveSpeed());
         }
      }
   };

   @Override
   protected void onDisable() {
      if (this.fly.getCurrentElement() == 0) {
         minecraft.thePlayer.capabilities.isFlying = false;
      }
   }

   @Override
   protected void onInitialized() {
      this.addAutoDisableTargets(ModuleManager.speed, ModuleManager.scaffold, ModuleManager.antiVoid);
   }

   public Fly() {
      super(Category.MOVEMENT, "Fly", "");
   }

   @Override
   protected void onEnable() {
      this.boostSpeed = this.ticksSinceDamage = 0;
      this.hovering = this.jumped = false;
   }
}
