package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.player.PlayerUtils;

public class TargetStrafe extends Module {
   private final MultiComboSetting targetStrafe = settingManager.register(
      new MultiComboSetting(this, "Addons").add("Keep distance", true).add("Save target", true).add("Auto jump", true)
   );
   private EntityLivingBase entityLivingBase;
   public final MultiComboSetting overrideSpeed = settingManager.register(new MultiComboSetting(this, "Override speed").add("Speed", true));
   private int strafeSide;
   private final NumberSetting distance = settingManager.register(new NumberSetting(this, "Distance", 2.0F, 0.0F, 8.0F));
   private int groundSpoofCounter;
   private final NumberSetting speed = settingManager.register(new NumberSetting(this, "Speed", 0.23F, 0.0F, 1.0F));
   private final CheckboxSetting onlyOnJumpKey = settingManager.register(new CheckboxSetting(this, "Only on jump key", false));
   @EventLink
   private final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      if (!ModuleManager.speed.isEnabled() || !this.overrideSpeed.isSelected(0)) {
         this.applyStrafe(playerMoveEvent, this.speed.getCurrentValue());
      }
   };
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (ModuleManager.killaura.isEnabled()) {
         if (!this.onlyOnJumpKey.isEnabled() || Keyboard.isKeyDown(57)) {
            if (!PlayerUtils.hasBlockBelowAt(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ)) {
               this.strafeSide = -this.strafeSide;
            } else {
               if (minecraft.gameSettings.keyBindLeft.isKeyDown()) {
                  this.strafeSide = 1;
               } else if (minecraft.gameSettings.keyBindRight.isKeyDown()) {
                  this.strafeSide = -1;
               }

               if (minecraft.thePlayer.isCollidedHorizontally) {
                  this.strafeSide = -this.strafeSide;
               }
            }

            if (HypixelState.isServerType(ServerType.HYPIXEL) && !ModuleManager.speed.isEnabled()) {
               this.groundSpoofCounter++;
               if (this.groundSpoofCounter > 1) {
                  this.groundSpoofCounter = 0;
                  if (minecraft.thePlayer.fallDistance <= 5.0F) {
                     preUpdateEvent.onGround = true;
                  }
               }
            }
         }
      }
   };

   public boolean applyStrafe(PlayerMoveEvent playerMoveEvent2, double d) {
      if (!ModuleManager.killaura.isEnabled()) {
         return false;
      }

      if (this.onlyOnJumpKey.isEnabled() && !Keyboard.isKeyDown(57)) {
         return false;
      }

      EntityLivingBase entityLivingBase2 = ModuleManager.killaura.entityLivingBase;
      if (entityLivingBase2 != null) {
         this.entityLivingBase = entityLivingBase2;
         if (this.targetStrafe.isSelected(2) && minecraft.thePlayer.onGround && !ModuleManager.speed.isEnabled() && !Keyboard.isKeyDown(57)) {
            minecraft.thePlayer.jump();
            playerMoveEvent2.y = minecraft.thePlayer.motionY;
         }

         float f = (float)minecraft.thePlayer.getDistance(entityLivingBase2.posX, entityLivingBase2.posY, entityLivingBase2.posZ);
         float currentValue = this.distance.getCurrentValue();
         float f2;
         if (this.targetStrafe.isSelected(0)) {
            if (f < currentValue - 0.25F) {
               f2 = -1.0F;
            } else if (f > currentValue + 0.25F) {
               f2 = 1.0F;
            } else {
               f2 = 0.0F;
            }
         } else {
            f2 = f > currentValue ? 1.0F : 0.0F;
         }

         float f3 = RotationUtils.getRotationsToPoint(entityLivingBase2.posX, entityLivingBase2.posY, entityLivingBase2.posZ)[0];
         playerMoveEvent2.setSpeedCustom(d, f3, this.strafeSide, f2, 45);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void onEnable() {
      this.entityLivingBase = null;
   }

   public TargetStrafe() {
      super(Category.COMBAT, "TargetStrafe", "");
   }
}
