package wtf.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.player.PlayerUtils;

public class LongJump extends Module {
   private int jumpPhase;
   private int groundTicks;
   private static final float[] floatArray = new float[]{
      0.4F, 0.4F, 0.38F, 0.365F, 0.33F, 0.26F, 0.25F, 0.23F, 0.21F, 0.12F, 0.1F, 0.08F, 0.06F, 0.04F, -0.01F, -0.05F, -0.09F, -0.12F, -0.15F, -0.18F
   };
   private final ComboSetting longJump = settingManager.register(new ComboSetting(this, "Mode", 0, "Hypixel", "HypixelDamage", "Motion", "HypixelBow"));
   private boolean bowShotDone;
   private final NumberSetting motionSpeed = settingManager.register(new NumberSetting(this, "Motion speed", 1.0F, 0.7F, 2.0F) {
      @Override
      public boolean shouldShow() {
         return LongJump.this.longJump.getCurrentElement() == 2;
      }
   });
   private int savedSlot;
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.longJump.getCurrentElementAsString());
      int currentElement = this.longJump.getCurrentElement();
      if (currentElement == 0) {
         if (minecraft.thePlayer.motionY < 0.0 && minecraft.thePlayer.fallDistance < 1.0F) {
            minecraft.thePlayer.motionY *= 0.65;
         }

         if (minecraft.thePlayer.onGround && ++this.groundTicks >= 1) {
            this.setEnabled(false);
         }
      } else if (currentElement == 2) {
         switch (this.jumpPhase) {
            case 0:
               if (PlayerUtils.isMoving()) {
                  minecraft.thePlayer.setSprinting(true);
                  minecraft.thePlayer.jump();
                  this.jumpPhase++;
               }
               break;
            case 1:
               PlayerUtils.setSpeed(this.motionSpeed.getCurrentValue());
               if (minecraft.thePlayer.onGround) {
                  this.setEnabled(false);
               }
         }
      } else if (currentElement == 3 && this.hasBow()) {
         if (!this.bowShotDone) {
            int slotOfClass = InventoryUtils.findSlotOfClass(ItemBow.class, false);
            if (slotOfClass == -1) {
               return;
            }

            preUpdateEvent.rotationPitch = -89.2F;
            this.step++;
            if (minecraft.thePlayer.inventory.currentItem != slotOfClass) {
               this.savedSlot = minecraft.thePlayer.inventory.currentItem;
               minecraft.thePlayer.inventory.currentItem = slotOfClass;
            }

            if (!minecraft.gameSettings.keyBindUseItem.isKeyDown()) {
               minecraft.gameSettings.keyBindUseItem.setKeyDown(true);
            }

            if (this.step > 4) {
               if (minecraft.gameSettings.keyBindUseItem.isKeyDown()) {
                  minecraft.gameSettings.keyBindUseItem.setKeyDown(false);
               }

               this.bowShotDone = true;
               this.step = 0;
            }
         }

         this.resetYInterpolation();
      }
   };
   private boolean noDamageTaken;
   @EventLink
   private final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = onUpdateWalkingPlayerEvent -> {
      if (this.longJump.getCurrentElement() == 3 && this.hasBow()) {
         if (this.step == 0 && this.bowShotDone && !minecraft.gameSettings.keyBindUseItem.isKeyDown()) {
            minecraft.thePlayer.inventory.currentItem = this.savedSlot;
            this.step = 1;
         }

         this.resetYInterpolation();
      }
   };
   private double currentSpeed;
   @EventLink
   private final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      int currentElement = this.longJump.getCurrentElement();
      if (currentElement == 0) {
         double defaultMoveSpeed = PlayerUtils.getDefaultMoveSpeed();
         switch (this.jumpPhase) {
            case 0:
               if (minecraft.thePlayer.onGround) {
                  this.currentSpeed = defaultMoveSpeed;
                  minecraft.thePlayer.jump();
                  playerMoveEvent.y = minecraft.thePlayer.motionY;
               }
               break;
            case 1:
               this.currentSpeed = defaultMoveSpeed * 2.0;
               break;
            case 2:
               double d = 0.66 * (this.currentSpeed - defaultMoveSpeed);
               this.currentSpeed -= d;
               break;
            default:
               this.currentSpeed *= 0.99;
         }

         PlayerUtils.setSpeed(Math.max(this.currentSpeed, defaultMoveSpeed));
         this.jumpPhase++;
      } else if (currentElement == 1) {
         double defaultMoveSpeed2 = PlayerUtils.getDefaultMoveSpeed();
         switch (this.jumpPhase) {
            case 0:
               this.jump();
               if (minecraft.thePlayer.hurtTime <= 0) {
                  this.noDamageTaken = true;
               }

               this.currentSpeed = defaultMoveSpeed2 * (minecraft.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.4F : 1.6F);
               playerMoveEvent.setSpeed(this.currentSpeed);
               this.jumpPhase++;
               break;
            case 1:
               playerMoveEvent.y = minecraft.thePlayer.motionY = 0.42F;
               playerMoveEvent.setSpeed(this.currentSpeed);
               this.jumpPhase++;
               break;
            case 2:
               if (this.noDamageTaken && this.step < floatArray.length) {
                  playerMoveEvent.y = minecraft.thePlayer.motionY = floatArray[this.step++];
               }

               if (minecraft.thePlayer.onGround) {
                  this.setEnabled(false);
               }

               if (PlayerUtils.isMoving()) {
                  playerMoveEvent.setSpeed(Math.max(defaultMoveSpeed2, this.currentSpeed *= 0.93));
               } else {
                  playerMoveEvent.setSpeed(this.currentSpeed = 0.0);
               }
         }
      } else if (currentElement == 3 && this.hasBow()) {
         if (!this.noDamageTaken) {
            playerMoveEvent.x = playerMoveEvent.y = playerMoveEvent.z = 0.0;
            minecraft.thePlayer.motionX = minecraft.thePlayer.motionY = minecraft.thePlayer.motionZ = 0.0;
         }

         double defaultMoveSpeed3 = PlayerUtils.getDefaultMoveSpeed();
         switch (this.jumpPhase) {
            case 0:
               if (minecraft.thePlayer.hurtTime <= 0) {
                  return;
               }

               this.noDamageTaken = true;
               this.jumpPhase++;
               break;
            case 1:
               if (this.noDamageTaken) {
                  this.currentSpeed = defaultMoveSpeed3 * 1.85F;
               }

               playerMoveEvent.y = minecraft.thePlayer.motionY = 0.42F;
               this.jumpPhase++;
               break;
            case 2:
               if (this.noDamageTaken && this.step < floatArray2.length) {
                  playerMoveEvent.y = minecraft.thePlayer.motionY = floatArray2[this.step++];
               }

               if (minecraft.thePlayer.onGround) {
                  this.setEnabled(false);
               }

               if (PlayerUtils.isMoving()) {
                  playerMoveEvent.setSpeed(Math.max(this.currentSpeed *= 0.98, defaultMoveSpeed3));
               } else {
                  playerMoveEvent.setSpeed(this.currentSpeed = 0.0);
               }
         }
      }
   };
   private int step;
   private static final float[] floatArray2 = new float[]{
      0.4F, 0.4F, 0.38F, 0.365F, 0.33F, 0.26F, 0.25F, 0.23F, 0.21F, 0.12F, 0.1F, -0.01F, -0.05F, -0.09F, -0.12F, -0.15F, -0.18F, -0.21F, -0.24F, -0.27F, -0.3F
   };

   private boolean hasBow() {
      return InventoryUtils.findSlotOfClass(ItemBow.class, false) != -1;
   }

   @Override
   protected void onInitialized() {
      this.addAutoDisableTargets(ModuleManager.speed, ModuleManager.scaffold, ModuleManager.fly);
   }

   @Override
   protected void onDisable() {
      this.noDamageTaken = false;
   }

   private double randomBetween(double d, double d2) {
      return Math.random() * (d - d2) + d2;
   }

   private void resetYInterpolation() {
      minecraft.thePlayer.posY = minecraft.thePlayer.posY - (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY);
      minecraft.thePlayer.lastTickPosY = minecraft.thePlayer.lastTickPosY - (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY);
   }

   public LongJump() {
      super(Category.MOVEMENT, "LongJump", "");
   }

   private void jump() {
      double posX2 = minecraft.thePlayer.posX;
      double posY2 = minecraft.thePlayer.posY;
      double posZ2 = minecraft.thePlayer.posZ;
      float f = 3.1F;
      if (minecraft.thePlayer.isPotionActive(Potion.jump)) {
         f += minecraft.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0F;
      }

      for (int i = 0;
         i < (int)(f / (this.randomBetween(0.089, 0.0849) - 0.001 - Math.random() * 2.0E-4F - Math.random() * 2.0E-4F) + 18.0);
         i++
      ) {
         this.sendPacketUnregistered(
            new C04PacketPlayerPosition(
               posX2, posY2 + this.randomBetween(0.0655, 0.0625) - this.randomBetween(0.001, 0.01) - Math.random() * 2.0E-4F, posZ2, false
            )
         );
         this.sendPacketUnregistered(new C04PacketPlayerPosition(posX2, posY2 + Math.random() * 2.0E-4F, posZ2, false));
      }

      this.sendPacketUnregistered(new C03PacketPlayer(true));
   }

   @Override
   protected void onEnable() {
      this.step = this.jumpPhase = 0;
      this.bowShotDone = false;
      this.noDamageTaken = false;
      if (this.longJump.getCurrentElement() == 3 && !this.hasBow()) {
         this.showNotification(NotificationType.ERROR, "LongJump", Language.translate("feature.longjump.notification.hypixel_bow.bow_required"), 3500L);
         this.disable();
      }
   }
}
