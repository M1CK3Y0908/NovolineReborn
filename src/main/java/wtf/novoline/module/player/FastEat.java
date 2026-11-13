package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;

public class FastEat extends Module {
   private final ComboSetting fastEat = settingManager.register(new ComboSetting(this, "Mode", 0, "Vanilla", "Verus"));
   private final CheckboxSetting groundCheck = settingManager.register(new CheckboxSetting(this, "Ground check", true));
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (!this.groundCheck.isEnabled() || minecraft.thePlayer.onGround) {
         if (minecraft.thePlayer.isEating()) {
            int currentElement = this.fastEat.getCurrentElement();
            if (currentElement == 0) {
               for (int i = 0; i < 35; i++) {
                  this.sendPacket(new C03PacketPlayer(minecraft.thePlayer.onGround));
               }
            } else if (currentElement == 1) {
               for (int i2 = 0; i2 < 2; i2++) {
                  this.sendPacketUnregistered(
                     new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround)
                  );
                  this.sendPacketUnregistered(
                     new C06PacketPlayerPosLook(
                        minecraft.thePlayer.posX,
                        minecraft.thePlayer.posY,
                        minecraft.thePlayer.posZ,
                        minecraft.thePlayer.rotationYaw,
                        minecraft.thePlayer.rotationPitch,
                        minecraft.thePlayer.onGround
                     )
                  );
               }

               for (int i3 = 0; i3 < 4; i3++) {
                  this.sendPacketUnregistered(new C03PacketPlayer(minecraft.thePlayer.onGround));
               }
            }
         }
      }
   };

   public FastEat() {
      super(Category.PLAYER, "FastEat", "Eat faster");
   }
}
