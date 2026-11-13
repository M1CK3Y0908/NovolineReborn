package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;

public class FastBow extends Module {
   private final ComboSetting fastBow = settingManager.register(new ComboSetting(this, "Mode", 0, "Vanilla", "Bypass"));
   private final NumberSetting delay = settingManager.register(new NumberSetting(this, "Delay", 10, 1, 20));
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.fastBow.getCurrentElement() == 0 && minecraft.thePlayer != null && minecraft.thePlayer.onGround && this.isDrawingBow()) {
         minecraft.rightClickDelayTimer = 0;
         this.sendPacket(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getHeldItem()));

         for (int i = 0; i < this.delay.getCurrentValue() - minecraft.thePlayer.getItemInUseDuration(); i++) {
            this.sendPacket(
               new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, minecraft.thePlayer.onGround)
            );
         }

         this.sendPacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      }
   };
   private int drawTicks = 0;
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (this.fastBow.getCurrentElement() == 1 && packetSendEvent.packet instanceof C03PacketPlayer c03PacketPlayer) {
         if (this.isDrawingBow()) {
            c03PacketPlayer.y -= 1.0E-4;
            if (++this.drawTicks >= this.delay.getCurrentValue()) {
               if (minecraft.thePlayer.getItemInUseDuration() > 0) {
                  this.sendPacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
               }

               this.drawTicks = 0;
            }

            if (this.drawTicks == 1) {
               this.sendPacket(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getHeldItem()));
            }
         } else {
            this.drawTicks = 0;
         }
      }
   };

   private boolean isDrawingBow() {
      if (minecraft.thePlayer == null) {
         return false;
      }

      ItemStack heldItem = minecraft.thePlayer.getHeldItem();
      return minecraft.thePlayer.isUsingItem() && heldItem != null && heldItem.getItem() instanceof ItemBow;
   }

   public FastBow() {
      super(Category.COMBAT, "FastBow", "Shoots your bow rapidly");
   }
}
