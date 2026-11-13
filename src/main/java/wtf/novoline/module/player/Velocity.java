package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;

public class Velocity extends Module {
   private final ComboSetting velocity = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Vanilla", "Percent", "Matrix", "AAC 5.2.0 Flag", "MatrixReduce")
   );
   private final NumberSetting xzPercent = settingManager.register(new NumberSetting(this, "XZ Percent", 0, 0, 100) {
      @Override
      public boolean shouldShow() {
         return Velocity.this.velocity.getCurrentElement() == 1;
      }
   });
   private final NumberSetting yPercent = settingManager.register(new NumberSetting(this, "Y Percent", 0, 0, 100) {
      @Override
      public boolean shouldShow() {
         return Velocity.this.velocity.getCurrentElement() == 1;
      }
   });
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.velocity.getCurrentElementAsString());
      if (this.velocity.getCurrentElement() == 4 && minecraft.thePlayer.hurtTime > 0) {
         if (minecraft.thePlayer.onGround) {
            if (minecraft.thePlayer.hurtTime <= 6) {
               minecraft.thePlayer.motionX *= 0.7;
               minecraft.thePlayer.motionZ *= 0.7;
            }

            if (minecraft.thePlayer.hurtTime <= 5) {
               minecraft.thePlayer.motionX *= 0.8;
               minecraft.thePlayer.motionZ *= 0.8;
            }
         } else if (minecraft.thePlayer.hurtTime <= 10) {
            minecraft.thePlayer.motionX *= 0.6;
            minecraft.thePlayer.motionZ *= 0.6;
         }
      }
   };
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (minecraft.thePlayer != null) {
         int currentElement = this.velocity.getCurrentElement();
         if (packetReceiveEvent.packet instanceof S12PacketEntityVelocity s12PacketEntityVelocity) {
            if (s12PacketEntityVelocity.getEntityID() == minecraft.thePlayer.getEntityId()) {
               if (currentElement == 0) {
                  packetReceiveEvent.cancel();
               } else if (currentElement == 1) {
                  packetReceiveEvent.cancel();
                  if (this.xzPercent.getCurrentValue() > 0.0F) {
                     double d = s12PacketEntityVelocity.getMotionX() / 8000.0 * (this.xzPercent.getCurrentValue() / 100.0);
                     double d2 = s12PacketEntityVelocity.getMotionZ() / 8000.0 * (this.xzPercent.getCurrentValue() / 100.0);
                     minecraft.thePlayer.motionX = d;
                     minecraft.thePlayer.motionZ = d2;
                  }

                  if (this.yPercent.getCurrentValue() > 0.0F) {
                     minecraft.thePlayer.motionY = s12PacketEntityVelocity.getMotionY() / 8000.0 * (this.yPercent.getCurrentValue() / 100.0);
                  }
               } else if (currentElement == 2) {
                  packetReceiveEvent.cancel();
                  minecraft.thePlayer.motionY = s12PacketEntityVelocity.getMotionY() / 8000.0;
               } else if (currentElement == 3) {
                  packetReceiveEvent.cancel();
                  this.sendPacketUnregistered(new C04PacketPlayerPosition(minecraft.thePlayer.posX, Double.MAX_VALUE, minecraft.thePlayer.posZ, true));
               }
            }
         } else {
            Packet packet2 = packetReceiveEvent.packet;
            if (packet2 instanceof S27PacketExplosion && currentElement == 0) {
               packetReceiveEvent.cancel();
            }
         }
      }
   };

   public Velocity() {
      super(Category.PLAYER, "Velocity", "");
   }
}
