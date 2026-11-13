package wtf.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.combat.TargetStrafe;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.PlayerUtils;

public class Speed extends Module {
   private boolean explosionBoostPending;
   private final ComboSetting speed = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Hypixel", "Hypixel2", "Motion", "Matrix", "Matrix2", "NCP", "ZoneCraft")
   );
   private double lastTickDistance;
   private final MultiComboSetting moveAddons = settingManager.register(
      new MultiComboSetting(this, "Move addons")
         .add("Timer boost", false)
         .add("Timer boost attack", false)
         .add("Reduce Y", false)
         .add("Damage boost", false)
         .add("Lagback check", false)
         .add("Rotation strafe", true)
   );
   private double explosionSpeed;
   private boolean wasOnGroundHypixel2;
   private float lastMoveYaw;
   private double currentSpeed;
   private boolean wasOnGroundHypixel;
   private final CheckboxSetting movementSlowdown = settingManager.register(new CheckboxSetting(this, "Movement slowdown", false) {
      @Override
      public boolean shouldShow() {
         return Speed.this.moveAddons.isSelected(5) && Speed.this.speed.getCurrentElement() == 0;
      }
   });
   private final NumberSetting slowdown = settingManager.register(new NumberSetting(this, "Slowdown", 0.5F, 0.1F, 1.0F) {
      @Override
      public boolean shouldShow() {
         return Speed.this.movementSlowdown.shouldShow() && Speed.this.movementSlowdown.isEnabled();
      }
   });
   private final NumberSetting speed2 = settingManager.register(new NumberSetting(this, "Speed", 0.3F, 0.0F, 1.0F) {
      @Override
      public boolean shouldShow() {
         return Speed.this.speed.getCurrentElement() == 2;
      }
   });
   private final NumberSetting timerBoost = settingManager.register(new NumberSetting(this, "Timer boost", 1.0F, 1.0F, 2.0F) {
      @Override
      public boolean shouldShow() {
         return Speed.this.moveAddons.isSelected(0);
      }
   });
   private double minSpeedHypixel2;
   private final NumberSetting timerBoostAttack = settingManager.register(new NumberSetting(this, "Timer boost attack", 1.0F, 1.0F, 2.0F) {
      @Override
      public boolean shouldShow() {
         return Speed.this.moveAddons.isSelected(1);
      }
   });
   @EventLink
   public final Listener<UpdateEvent> onUpdate = updateEvent -> {
      KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindJump.getKeyCode(), false);
      this.lastTickDistance = PlayerUtils.getLastTickDistance();
      this.launchSpeed = PlayerUtils.getDefaultMoveSpeed() * 0.81;
      this.minSpeedHypixel = PlayerUtils.getBaseMoveSpeed(0.19 * (PlayerUtils.isInsideLiquid() ? 0.5 : (minecraft.thePlayer.movementInput.sneak ? 0.8 : 1.0)));
      this.minSpeedHypixel2 = PlayerUtils.getBaseMoveSpeed(0.16) * (PlayerUtils.isInsideLiquid() ? 0.5 : (minecraft.thePlayer.movementInput.sneak ? 0.8 : 1.0));
      if (PlayerUtils.isMoving()) {
         minecraft.thePlayer.setSprinting(true);
      }
   };
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.speed.getCurrentElementAsString());
      int currentElement = this.speed.getCurrentElement();
      if (currentElement != 1) {
         minecraft.timer.timerSpeed = this.getTimerSpeed();
      }

      if (this.moveAddons.isSelected(5) && this.airTicks > 0.01 && PlayerUtils.isMoving()) {
         if (currentElement != 0) {
            preUpdateEvent.rotationYaw = PlayerUtils.getMoveDirectionDegrees();
         } else {
            preUpdateEvent.rotationYaw = this.lastMoveYaw;
         }
      }

      if (currentElement == 0 && HypixelState.isServerType(ServerType.HYPIXEL)) {
         minecraft.timer.timerSpeed = this.getTimerSpeed();
         if (PlayerUtils.isMoving()) {
            minecraft.thePlayer.setSprinting(true);
            if (minecraft.thePlayer.onGround && minecraft.thePlayer.jumpTicks == 0) {
               minecraft.thePlayer.jump();
               minecraft.thePlayer.jumpTicks = 10;
               this.currentSpeed = this.launchSpeed * 2.0;
            }
         }
      } else if (currentElement == 2) {
         if (PlayerUtils.isMoving()) {
            minecraft.thePlayer.setSprinting(true);
            if (minecraft.thePlayer.onGround) {
               minecraft.thePlayer.jump();
            }

            PlayerUtils.setSpeed(this.speed2.getCurrentValue());
         }
      } else if (currentElement == 3) {
         if (PlayerUtils.isMoving()) {
            minecraft.thePlayer.setSprinting(true);
            if (minecraft.thePlayer.onGround) {
               if (!minecraft.gameSettings.keyBindJump.isKeyDown()) {
                  minecraft.thePlayer.jump();
               }

               this.airTicks = 0;
            } else {
               this.airTicks++;
               if (this.airTicks == 2) {
                  minecraft.thePlayer.motionX *= 1.7F;
                  minecraft.thePlayer.motionZ *= 1.7F;
               }
            }
         }
      } else if (currentElement == 4 && PlayerUtils.isMoving()) {
         minecraft.thePlayer.setSprinting(true);
         if (minecraft.thePlayer.onGround) {
            if (!minecraft.gameSettings.keyBindJump.isKeyDown()) {
               minecraft.thePlayer.jump();
            }

            this.airTicks = 0;
         } else {
            this.airTicks++;
            if (this.airTicks == 4) {
               minecraft.thePlayer.jumpMovementFactor = 0.025F;
            } else if (this.airTicks == 5) {
               minecraft.thePlayer.jumpMovementFactor = 0.025F;
            } else if (this.airTicks == 2) {
               minecraft.thePlayer.jumpMovementFactor = 0.35F;
            } else if (this.airTicks == 3) {
               minecraft.thePlayer.jumpMovementFactor = 0.025F;
            } else {
               minecraft.thePlayer.jumpMovementFactor = 0.02F;
            }
         }
      }
   };
   private double minSpeedHypixel;
   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      int currentElement = this.speed.getCurrentElement();
      if (currentElement == 0 && HypixelState.isServerType(ServerType.HYPIXEL)) {
         if (!(minecraft.thePlayer.fallDistance <= 5.0F) || minecraft.thePlayer.isSpectator() || PlayerUtils.isStandingOnLiquid()) {
            return;
         }

         if (PlayerUtils.isMoving()) {
            if (minecraft.thePlayer.onGround && !PlayerUtils.isStandingOnLiquid()) {
               this.currentSpeed = this.minSpeedHypixel * 2.139999980926514;
               playerMoveEvent.y = minecraft.thePlayer.motionY = PlayerUtils.addJumpBoost(this.moveAddons.isSelected(2) ? 0.399999986886978 : 0.419999986886978);
            }

            if (this.wasOnGroundHypixel) {
               this.currentSpeed = this.lastTickDistance - 0.81999 * (this.lastTickDistance - this.minSpeedHypixel);
            }

            this.currentSpeed = this.currentSpeed - this.lastTickDistance / 149.0;
            this.currentSpeed = this.addExplosionBoost(this.currentSpeed);
            this.currentSpeed = Math.max(this.currentSpeed, this.minSpeedHypixel);
            this.applyMoveSpeed(playerMoveEvent, Math.max(this.currentSpeed, this.minSpeedHypixel));
            this.wasOnGroundHypixel = minecraft.thePlayer.onGround;
         } else {
            this.applyMoveSpeed(playerMoveEvent, 0.0);
         }
      } else if (currentElement == 1 && PlayerUtils.isMoving() && HypixelState.isServerType(ServerType.HYPIXEL)) {
         float timerSpeed2 = this.getTimerSpeed();
         if (minecraft.thePlayer.onGround) {
            minecraft.timer.timerSpeed = 1.0F;
            this.currentSpeed = this.minSpeedHypixel2 * 2.145F;
            playerMoveEvent.y = minecraft.thePlayer.motionY = PlayerUtils.addJumpBoost(ModuleManager.scaffold.isEnabled() ? 0.41999998688698 : 0.39999998688698);
            playerMoveEvent.setMotionFromInput(this.currentSpeed, 1.0F, minecraft.thePlayer.rotationYaw);
         } else if (this.wasOnGroundHypixel2) {
            minecraft.timer.timerSpeed = !HypixelState.isGameType(HypixelGameType.SKY_WARS) && !HypixelState.isGameType(HypixelGameType.LOBBY) ? 0.9F : 1.0F;
            this.currentSpeed = this.lastTickDistance - 0.819999 * (this.lastTickDistance - this.minSpeedHypixel2);
            playerMoveEvent.setMotionFromInput(this.currentSpeed, 1.0F, minecraft.thePlayer.rotationYaw);
         } else {
            minecraft.timer.timerSpeed = !HypixelState.isGameType(HypixelGameType.SKY_WARS) && !HypixelState.isGameType(HypixelGameType.LOBBY)
               ? timerSpeed2 - 0.1F
               : timerSpeed2;
            this.currentSpeed = this.lastTickDistance - this.lastTickDistance / 159.9;
         }

         this.currentSpeed = this.addExplosionBoost(this.currentSpeed);
         this.currentSpeed = Math.max(this.currentSpeed, this.minSpeedHypixel2);
         this.applyTargetStrafeSpeed(playerMoveEvent, this.currentSpeed);
         this.wasOnGroundHypixel2 = minecraft.thePlayer.onGround;
      } else if (currentElement == 5) {
         if (PlayerUtils.isMoving()) {
            if (minecraft.thePlayer.onGround) {
               this.airTicks = 0;
            }

            double defaultMoveSpeed = PlayerUtils.getDefaultMoveSpeed();
            if (this.airTicks == 0) {
               if (minecraft.thePlayer.onGround) {
                  double d = this.moveAddons.isSelected(3) ? 0.3994F : 0.42;
                  playerMoveEvent.y = minecraft.thePlayer.motionY = d;
                  this.currentSpeed *= 2.1499999;
               }
            } else if (this.airTicks == 1) {
               this.currentSpeed = this.lastTickDistance - 0.84 * (this.lastTickDistance - defaultMoveSpeed);
            }

            if (this.airTicks > 0) {
               this.currentSpeed = this.currentSpeed - this.lastTickDistance / 159.0;
            }

            this.currentSpeed = this.addExplosionBoost(this.currentSpeed);
            this.currentSpeed = Math.max(this.currentSpeed, defaultMoveSpeed);
            this.applyTargetStrafeSpeed(playerMoveEvent, this.currentSpeed);
            this.airTicks++;
         }
      } else if (currentElement == 6 && minecraft.thePlayer.onGround && PlayerUtils.isMoving()) {
         this.sendPacketUnregistered(
            new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getPosition().down(), 0, new ItemStack(Blocks.stone, 1), 0.54345F, 0.523234F, 0.5435345F)
         );
         playerMoveEvent.setSpeed(0.5 + Math.random() * 0.003);
      }
   };
   private int airTicks;
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (this.speed.getCurrentElement() == 6 && packetReceiveEvent.packet instanceof S08PacketPlayerPosLook s08PacketPlayerPosLook && minecraft.thePlayer.ticksExisted > 30) {
         this.sendPacketUnregistered(new C04PacketPlayerPosition(s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(), false));
         packetReceiveEvent.cancel();
      } else {
         if (this.moveAddons.isSelected(3) && packetReceiveEvent.packet instanceof S27PacketExplosion s27PacketExplosion && s27PacketExplosion.getAffectedBlockPositions().isEmpty()) {
            this.explosionSpeed = Math.hypot(
               minecraft.thePlayer.motionX + s27PacketExplosion.getMotionX() / 8500.0F, minecraft.thePlayer.motionZ + s27PacketExplosion.getMotionZ() / 8500.0F
            );
            this.explosionBoostPending = true;
         }

         if (this.moveAddons.isSelected(4) && packetReceiveEvent.packet instanceof S08PacketPlayerPosLook) {
            this.showNotification(NotificationType.WARNING, "Speed", Language.translate("feature.speed.notification.lag_back"), 3500L);
            this.disable();
         }
      }
   };
   private double launchSpeed;

   private double addExplosionBoost(double d) {
      if (this.moveAddons.isSelected(3) && this.explosionBoostPending && !minecraft.thePlayer.isPotionActive(Potion.poison) && !minecraft.thePlayer.isBurning()
         )
       {
         d += this.explosionSpeed;
         this.explosionBoostPending = false;
      }

      return d;
   }

   private float getTimerSpeed() {
      EntityLivingBase entityLivingBase2 = ModuleManager.killaura.entityLivingBase;
      if (this.moveAddons.isSelected(1) && entityLivingBase2 != null) {
         return this.timerBoostAttack.getCurrentValue();
      } else {
         return this.moveAddons.isSelected(0) ? this.timerBoost.getCurrentValue() : 1.0F;
      }
   }

   @Override
   protected void onToggled() {
      this.launchSpeed = this.lastTickDistance = 0.0;
      minecraft.timer.timerSpeed = 1.0F;
   }

   @Override
   protected void onInitialized() {
      this.addAutoDisableTargets(ModuleManager.scaffold, ModuleManager.longJump, ModuleManager.fly, ModuleManager.antiVoid);
      super.onInitialized();
   }

   private void applyMoveSpeed(PlayerMoveEvent playerMoveEvent2, double d) {
      MovementInput movementInput2 = minecraft.thePlayer.movementInput;
      double moveForward2 = movementInput2.moveForward;
      double moveStrafe2 = movementInput2.moveStrafe;
      double d2 = moveForward2 == 0.0 ? 90.0 : (moveForward2 < 0.0 ? -45.0 : 45.0);
      float rotationYaw2 = minecraft.thePlayer.rotationYaw;
      float f = rotationYaw2 + (moveForward2 < 0.0 ? 180.0F : 0.0F);
      if (moveStrafe2 < 0.0) {
         f = (float)(f + d2);
      }

      if (moveStrafe2 > 0.0) {
         f = (float)(f - d2);
      }

      float f2 = MathHelper.wrapAngleTo180_float(f);
      if (this.movementSlowdown.isEnabled() && Math.abs(f2 - this.lastMoveYaw) >= 45.0) {
         d = 0.25 - 0.0015 * (this.slowdown.getCurrentValue() * 100.0F);
      }

      this.lastMoveYaw = f2;
      if (moveForward2 == 0.0 && moveStrafe2 == 0.0) {
         playerMoveEvent2.x = playerMoveEvent2.z = 0.0;
      } else {
         playerMoveEvent2.x = -(MathHelper.createFloat2(Math.toRadians(f2)) * d);
         playerMoveEvent2.z = MathHelper.createFloat(Math.toRadians(f2)) * d;
      }
   }

   private void applyTargetStrafeSpeed(PlayerMoveEvent playerMoveEvent2, double d) {
      TargetStrafe targetStrafe2 = ModuleManager.targetStrafe;
      if (!targetStrafe2.isEnabled() || !targetStrafe2.overrideSpeed.isSelected(0) || !targetStrafe2.applyStrafe(playerMoveEvent2, d)) {
         playerMoveEvent2.setSpeed(d);
      }
   }

   @Override
   protected void onEnable() {
      this.lastMoveYaw = PlayerUtils.getMoveDirectionDegrees();
   }

   public Speed() {
      super(Category.MOVEMENT, "Speed", "");
   }
}
