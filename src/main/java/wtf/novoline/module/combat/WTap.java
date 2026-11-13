package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.NumberSetting;

public class WTap extends Module {
   private final NumberSetting wTap = settingManager.register(new NumberSetting(this, "Tick", 0, 0, 10));
   private final NumberSetting chance = settingManager.register(new NumberSetting(this, "Chance", 2, 1, 20));
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (packetSendEvent.packet instanceof C02PacketUseEntity c02PacketUseEntity) {
         Entity entityByID = minecraft.theWorld.getEntityByID(c02PacketUseEntity.entityId);
         Entity entity2 = entityByID;
         if (entity2 instanceof EntityLiving && ((EntityLiving)entity2).hurtTime <= 10.0F - this.wTap.getCurrentValue()) {
            int i = ThreadLocalRandom.current().nextInt(0, 100);
            if (i % this.chance.getCurrentValue() != 0.0F) {
               return;
            }

            if (minecraft.thePlayer.isSprinting()) {
               this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.STOP_SPRINTING));
            }

            this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.START_SPRINTING));
            this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.STOP_SPRINTING));
            this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.START_SPRINTING));
            minecraft.thePlayer.setSprinting(true);
         }
      }
   };

   public WTap() {
      super(Category.COMBAT, "WTap", "Increases knockback to players");
   }
}
