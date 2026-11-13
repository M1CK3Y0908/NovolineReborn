package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.time.Stopwatch;

public class NoRotate extends Module {
   private float lastPitch;
   private final ComboSetting noRotate = settingManager.register(new ComboSetting(this, "Mode", 0, "Normal", "Advanced"));
   private float lastYaw;
   private final NumberSetting delayMs = settingManager.register(new NumberSetting(this, "Delay (ms)", 100, 10, 1000) {
      @Override
      public boolean shouldShow() {
         return NoRotate.this.noRotate.getCurrentElement() == 1;
      }
   });
   private final CheckboxSetting smoothRotations = settingManager.register(new CheckboxSetting(this, "Smooth rotations", false) {
      @Override
      public boolean shouldShow() {
         return NoRotate.this.noRotate.getCurrentElement() == 1;
      }
   });
   private boolean correctionPending = false;
   private final Stopwatch delayTimer = new Stopwatch();
   @EventLink(4)
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      int currentElement = this.noRotate.getCurrentElement();
      if (minecraft.theWorld != null
         && minecraft.thePlayer != null
         && minecraft.getNetHandler().doneLoadingTerrain
         && minecraft.thePlayer.getHealth() > 0.0
         && minecraft.thePlayer.ticksExisted > 0) {
         if (currentElement == 0) {
            if (packetReceiveEvent.packet instanceof S08PacketPlayerPosLook s08PacketPlayerPosLook) {
               double x = s08PacketPlayerPosLook.getX();
               double y = s08PacketPlayerPosLook.getY();
               double z = s08PacketPlayerPosLook.getZ();
               float yaw = s08PacketPlayerPosLook.getYaw();
               float pitch = s08PacketPlayerPosLook.getPitch();
               if (s08PacketPlayerPosLook.getFlags().contains(EnumFlags.X)) {
                  x += minecraft.thePlayer.posX;
               }

               if (s08PacketPlayerPosLook.getFlags().contains(EnumFlags.Y)) {
                  y += minecraft.thePlayer.posY;
               }

               if (s08PacketPlayerPosLook.getFlags().contains(EnumFlags.Z)) {
                  z += minecraft.thePlayer.posZ;
               }

               if (s08PacketPlayerPosLook.getFlags().contains(EnumFlags.X_ROT)) {
                  pitch += minecraft.thePlayer.rotationPitch;
               }

               if (s08PacketPlayerPosLook.getFlags().contains(EnumFlags.Y_ROT)) {
                  yaw += minecraft.thePlayer.rotationYaw;
               }

               this.sendPacketUnregistered(new C06PacketPlayerPosLook(x, y, z, yaw, pitch, false));
               s08PacketPlayerPosLook.setYaw(minecraft.thePlayer.rotationYaw);
               s08PacketPlayerPosLook.setPitch(minecraft.thePlayer.rotationPitch);
            }
         } else if (currentElement == 1 && packetReceiveEvent.packet instanceof S08PacketPlayerPosLook) {
            this.lastYaw = minecraft.thePlayer.rotationYaw;
            this.lastPitch = minecraft.thePlayer.rotationPitch;
            this.correctionPending = true;
         }
      }
   };
   @EventLink
   private final Listener<RunTickEvent> onRunTick = runTickEvent -> {
      if (this.noRotate.getCurrentElement() == 1 && minecraft.thePlayer != null) {
         float currentValue = this.delayMs.getCurrentValue();
         if (this.correctionPending && this.delayTimer.hasElapsedFloat(currentValue)) {
            if (this.smoothRotations.isEnabled()) {
               this.sendRotationSteps(minecraft.thePlayer.rotationYaw, this.lastYaw, 0);
               this.sendRotationSteps(minecraft.thePlayer.rotationPitch, this.lastPitch, 1);
            }

            minecraft.thePlayer.rotationYaw = this.lastYaw;
            minecraft.thePlayer.rotationPitch = this.lastPitch;
            this.correctionPending = false;
            this.delayTimer.reset();
         }
      }
   };

   private void sendRotation(float f, int i) {
      this.sendPacketUnregistered(
         new C05PacketPlayerLook(
            i == 0 ? f : minecraft.thePlayer.rotationYaw, i == 1 ? f : minecraft.thePlayer.rotationPitch, minecraft.thePlayer.onGround
         )
      );
   }

   private void sendRotationSteps(float f, float f2, int i) {
      if (f < f2) {
         for (float f3 = f; f3 < f2; f3 += 5.0F) {
            this.sendRotation(f3, i);
         }
      } else {
         for (float f4 = f2; f4 > f; f4 -= 5.0F) {
            this.sendRotation(f4, i);
         }
      }
   }

   public NoRotate() {
      super(Category.PLAYER, "NoRotate", "Prevents the server from rotating you");
   }
}
