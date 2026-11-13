package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.event.SlowdownEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.player.PlayerUtils;

public class NoSlowDown extends Module {
   private final ComboSetting noSlowDown = settingManager.register(new ComboSetting(this, "Mode", 1, "Vanilla", "NCP", "Multiplier", "Hypixel"));
   private final NumberSetting strafeMultiplier = settingManager.register(new NumberSetting(this, "Strafe multiplier", 0.2F, 0.1F, 1.0F) {
      @Override
      public boolean shouldShow() {
         return NoSlowDown.this.noSlowDown.getCurrentElement() == 2;
      }
   });
   private final NumberSetting forwardMultiplier = settingManager.register(new NumberSetting(this, "Forward multiplier", 0.2F, 0.1F, 1.0F) {
      @Override
      public boolean shouldShow() {
         return NoSlowDown.this.noSlowDown.getCurrentElement() == 2;
      }
   });
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      int currentElement = this.noSlowDown.getCurrentElement();
      if (this.isUsingItem() && PlayerUtils.isMoving()) {
         if (currentElement == 1) {
            if (minecraft.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
               this.sendPacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
         } else if (currentElement == 3 && HypixelState.isServerType(ServerType.HYPIXEL)) {
            if (minecraft.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
               this.sendPacketUnregistered(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getHeldItem()));
            }

            if (minecraft.thePlayer.getItemInUseDuration() == 1) {
               this.sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
            }
         }
      }
   };
   @EventLink
   private final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = onUpdateWalkingPlayerEvent -> {
      int currentElement = this.noSlowDown.getCurrentElement();
      if (this.isUsingItem() && PlayerUtils.isMoving() && currentElement == 1) {
         this.sendPacketUnregistered(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getHeldItem()));
      }
   };
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (this.noSlowDown.getCurrentElement() == 3) {
         Packet packet2 = packetReceiveEvent.packet;
         if (packet2 instanceof S02PacketChat && ((S02PacketChat)packet2).getChatComponent().getUnformattedText().equals("Height limit for building is 256 blocks")) {
            packetReceiveEvent.cancel();
         }
      }
   };
   @EventLink
   private final Listener<SlowdownEvent> onSlowdown = slowdownEvent -> {
      if (this.noSlowDown.getCurrentElement() == 2) {
         slowdownEvent.strafeMultiplier = this.strafeMultiplier.getCurrentValue();
         slowdownEvent.forwardMultiplier = this.forwardMultiplier.getCurrentValue();
      } else {
         slowdownEvent.cancel();
      }
   };
   @EventLink
   private final Listener<RunTickEvent> onRunTick = runTickEvent -> this.setSuffix(this.noSlowDown.getCurrentElementAsString());

   private boolean isUsingItem() {
      return minecraft.thePlayer != null
         && minecraft.thePlayer.getHeldItem() != null
         && minecraft.gameSettings.keyBindUseItem.isKeyDown()
         && !ModuleManager.killaura.isAutoBlocked()
         && !(minecraft.thePlayer.getHeldItem().getItem() instanceof ItemBlock);
   }

   public NoSlowDown() {
      super(Category.PLAYER, "NoSlowDown", "No slowdown when blocking or using items");
   }
}
