package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import wtf.novoline.Novoline;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.movement.Speed;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.ScheduledTask;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.time.Stopwatch;

public class NoFall extends Module {
   private boolean matrixFallDetected;
   private final ComboSetting noFall = settingManager.register(
      new ComboSetting(
         this, "Mode", 0, "Vanilla", "VanillaSpoof", "Always OnGround", "Never OnGround", "HypixelSpoof", "Advanced", "Matrix", "Matrix2", "Matrix3", "Matrix4"
      )
   );
   private boolean landingTrickArmed = false;
   private boolean awaitingSetback = false;
   private boolean sampleFallStart = false;
   private int spoofCount;
   private boolean speedDisabledByNoFall = false;
   private double groundDistance = 0.0;
   private final Stopwatch groundTimer = new Stopwatch();
   public boolean fallSpoofActive;
   private boolean setbackTimerStarted;
   @EventLink
   public final Listener<RunTickEvent> onRunTick = runTickEvent -> {
      if (this.noFall.getCurrentElement() == 5) {
         if (this.awaitingSetback && !this.setbackTimerStarted) {
            this.groundTimer.reset();
            this.setbackTimerStarted = true;
         }

         if (this.awaitingSetback && this.groundTimer.hasElapsed(200L)) {
            minecraft.timer.timerSpeed = 1.0F;
            this.groundTimer.reset();
            this.setbackTimerStarted = false;
            this.awaitingSetback = false;
         } else if (!this.awaitingSetback) {
            this.setbackTimerStarted = false;
         }
      }
   };
   @EventLink
   public final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (this.noFall.getCurrentElement() == 5 && this.landingTrickArmed && packetSendEvent.packet instanceof C03PacketPlayer && minecraft.thePlayer != null) {
         packetSendEvent.cancel();
         this.sendPacketUnregistered(new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
      }
   };
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (this.noFall.getCurrentElement() == 5 && this.awaitingSetback && packetReceiveEvent.packet instanceof S08PacketPlayerPosLook s08PacketPlayerPosLook) {
         packetReceiveEvent.cancel();
         this.sendPacketUnregistered(new C04PacketPlayerPosition(s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(), false));
         if (this.speedDisabledByNoFall) {
            Novoline.getNovoline().getTaskScheduler().schedule(new ScheduledTask(100) {
               @Override
               public void run() {
                  ModuleManager.speed.setEnabled(true);
                  NoFall.this.speedDisabledByNoFall = false;
               }
            });
         }

         minecraft.timer.timerSpeed = 1.0F;
         this.awaitingSetback = false;
      }
   };
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.noFall.getCurrentElementAsString());
      int currentElement = this.noFall.getCurrentElement();
      if (currentElement == 0 && minecraft.thePlayer.fallDistance > 2.0F) {
         minecraft.thePlayer.fallDistance = 0.0F;
         this.sendPacketUnregistered(new C03PacketPlayer(true));
      }

      if (currentElement == 1) {
         if (minecraft.thePlayer.fallDistance > 2.0F) {
            minecraft.thePlayer.fallDistance = 0.0F;
            preUpdateEvent.onGround = true;
         }
      } else if (currentElement == 2) {
         preUpdateEvent.onGround = true;
      } else if (currentElement == 3) {
         preUpdateEvent.onGround = false;
      } else if (currentElement == 4) {
         if (minecraft.thePlayer.fallDistance > 3.0F) {
            minecraft.thePlayer.fallDistance = 0.0F;
            double d = (float)Math.round(preUpdateEvent.posY / 0.015625) * 0.015625F;
            if (Math.abs(preUpdateEvent.posY - d) <= 0.005) {
               preUpdateEvent.posY = d;
            }

            preUpdateEvent.onGround = true;
         }
      } else if (currentElement == 5) {
         if (preUpdateEvent.onGround) {
            this.sampleFallStart = true;
            if (this.landingTrickArmed) {
               preUpdateEvent.onGround = false;
               this.sendPacketUnregistered(
                  new C06PacketPlayerPosLook(preUpdateEvent.posX, preUpdateEvent.posY - 0.08, preUpdateEvent.posZ, preUpdateEvent.rotationYaw, preUpdateEvent.rotationPitch, false)
               );
               this.sendPacketUnregistered(
                  new C06PacketPlayerPosLook(preUpdateEvent.posX, preUpdateEvent.posY - 0.02, preUpdateEvent.posZ, preUpdateEvent.rotationYaw, preUpdateEvent.rotationPitch, false)
               );
               minecraft.timer.timerSpeed = 0.45F;
               this.awaitingSetback = true;
               this.landingTrickArmed = false;
            }
         } else {
            if (this.sampleFallStart && minecraft.thePlayer.fallDistance > 0.8) {
               this.groundDistance = this.getDistanceToGround();
               this.sampleFallStart = false;
            }

            this.landingTrickArmed = this.groundDistance >= 2.98 && minecraft.thePlayer.fallDistance > 2.8;
         }

         Speed speed2 = ModuleManager.speed;
         if (minecraft.thePlayer.fallDistance > 3.0F && speed2.isEnabled()) {
            speed2.setEnabled(false);
            this.speedDisabledByNoFall = true;
         }
      } else if (currentElement == 6) {
         if (minecraft.thePlayer.onGround && this.matrixFallDetected) {
            PlayerUtils.setSpeed(0.0);
            preUpdateEvent.motionX = 0.0;
            preUpdateEvent.motionZ = 0.0;
            minecraft.thePlayer.setSprinting(false);
            this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.STOP_SPRINTING));
            this.sendPacketUnregistered(
               new C04PacketPlayerPosition(minecraft.thePlayer.posX + 1000.0, minecraft.thePlayer.posY + 1000.0, minecraft.thePlayer.posZ + 1000.0, true)
            );
            this.matrixFallDetected = false;
         }

         if (minecraft.thePlayer.fallDistance >= 3.0F) {
            this.matrixFallDetected = true;
            preUpdateEvent.onGround = false;
         }
      } else if (currentElement == 7) {
         if (minecraft.thePlayer.fallDistance > 2.0F) {
            this.sendPacketUnregistered(new C05PacketPlayerLook(minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch, true));
            minecraft.thePlayer.fallDistance = 0.0F;
         }
      } else if (currentElement == 8) {
         if (minecraft.thePlayer.fallDistance > 2.0F) {
            this.sendPacketUnregistered(
               new C06PacketPlayerPosLook(
                  minecraft.thePlayer.posX,
                  minecraft.thePlayer.posY,
                  minecraft.thePlayer.posZ,
                  minecraft.thePlayer.rotationYaw,
                  minecraft.thePlayer.rotationPitch,
                  true
               )
            );
            minecraft.thePlayer.fallDistance = 0.0F;
         }
      } else if (currentElement == 9) {
         boolean flag = false;
         if (minecraft.thePlayer.fallDistance > 2.0F) {
            this.fallSpoofActive = true;
            this.spoofCount++;
            if (this.spoofCount > 3) {
               if (minecraft.thePlayer.fallDistance > 3.0F) {
                  this.spoofCount = 0;
                  flag = true;
               }
            } else {
               flag = true;
            }
         }

         if (flag) {
            this.sendPacket(new C05PacketPlayerLook(minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch, true));
            minecraft.thePlayer.fallDistance = 0.0F;
            minecraft.thePlayer.motionY = -0.02;
         }

         if (minecraft.thePlayer.onGround) {
            this.fallSpoofActive = false;
         }
      }
   };

   @Override
   protected void onToggled() {
      this.fallSpoofActive = false;
      int currentElement = this.noFall.getCurrentElement();
      if (this.isEnabled()) {
         if (currentElement == 5) {
            if (minecraft.thePlayer.onGround) {
               this.sampleFallStart = true;
            }

            minecraft.timer.timerSpeed = 1.0F;
            this.awaitingSetback = false;
            this.groundDistance = 0.0;
            if (this.isEnclosed()) {
               this.landingTrickArmed = false;
            }
         }
      } else if (currentElement == 5) {
         minecraft.timer.timerSpeed = 1.0F;
         this.landingTrickArmed = false;
      }
   }

   private boolean isEnclosed() {
      double posX2 = minecraft.thePlayer.posX;
      double posY2 = minecraft.thePlayer.posY;
      double posZ2 = minecraft.thePlayer.posZ;

      for (int i = 0; i <= 1; i++) {
         if (!this.isGlassAt(posX2 - 1.0, posY2 + 1.0, posZ2)
            || !this.isGlassAt(posX2 + 1.0, posY2 + i, posZ2)
            || !this.isGlassAt(posX2, posY2 + i, posZ2 - 1.0)
            || !this.isGlassAt(posX2, posY2 + i, posZ2 + 1.0)) {
            return false;
         }
      }

      return true;
   }

   private double getDistanceToGround() {
      double posX2 = minecraft.thePlayer.posX;
      double posY2 = minecraft.thePlayer.posY;
      double posZ2 = minecraft.thePlayer.posZ;

      for (double d = posY2; d > 0.0; d--) {
         BlockPos blockPos = new BlockPos(posX2, d, posZ2);
         if (!minecraft.theWorld.getBlockState(blockPos).getBlock().getUnlocalizedName().equals("tile.air")) {
            return posY2 - d;
         }
      }

      return -1.0;
   }

   public NoFall() {
      super(Category.PLAYER, "NoFall", "");
   }

   private boolean isGlassAt(double d, double d2, double d3) {
      BlockPos blockPos = new BlockPos(d, d2, d3);
      String unlocalizedName = minecraft.theWorld.getBlockState(blockPos).getBlock().getUnlocalizedName();
      return unlocalizedName.equals("tile.glass") || unlocalizedName.equals("tile.stainedGlass");
   }
}
