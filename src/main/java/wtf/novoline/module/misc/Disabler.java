package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.ServerConnectEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.combat.Killaura;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.utils.ScheduledTask;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.time.Stopwatch;

public class Disabler extends Module {
   private final MultiComboSetting disabler = settingManager.register(
      new MultiComboSetting(this, "Modes")
         .add("Hypixel", false)
         .add("Verus", false)
         .add("Verus2", false)
         .add("BlocksMC", false)
         .add("MatrixMovement", false)
         .add("MatrixMovement2", false)
   );
   private long cancelCount;
   private final CheckboxSetting delayPacket = settingManager.register(new CheckboxSetting(this, "Delay packet", true) {
      @Override
      public boolean shouldShow() {
         return Disabler.this.disabler.isSelected(1);
      }
   });
   private final Stopwatch setbackTimer = new Stopwatch();
   private boolean releaseReady;
   private boolean initialized;
   private final Stopwatch releaseTimer = new Stopwatch();
   private final Deque<Disabler.HeldPacket> heldPackets = new ArrayDeque<>();
   private final Queue<Packet<?>> transactionQueue = new ConcurrentLinkedQueue<>();
   private final Stopwatch queueTimer = new Stopwatch();
   @EventLink
   public final Listener<ServerConnectEvent> onServerConnect = serverConnectEvent -> {
      if (this.disabler.isSelected(0) && !HypixelState.isServerType(ServerType.HYPIXEL)) {
         this.showNotification(
            NotificationType.WARNING,
            "Disabler",
            Language.translate("feature.disabler.notification.not_available.text", minecraft.getCurrentServerData().serverIP),
            5000L
         );
      }
   };
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (this.disabler.isSelected(0)
         && HypixelState.isServerType(ServerType.HYPIXEL)
         && packetReceiveEvent.packet instanceof S08PacketPlayerPosLook s08PacketPlayerPosLook
         && minecraft.thePlayer != null) {
         if (minecraft.thePlayer.ticksExisted < 5
            || !PlayerUtils.hasBlockBelow()
            || !PlayerUtils.hasBlockBelowAt(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ)) {
            return;
         }

         if (this.setbackTimer.hasElapsed(2000L)) {
            if (minecraft.thePlayer.getDistance(s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ()) < 10.0) {
               this.cancelCount = 6L;
               packetReceiveEvent.cancel();
            }

            this.setbackTimer.reset();
         }
      }

      if (this.disabler.isSelected(3) && minecraft.thePlayer != null && packetReceiveEvent.packet instanceof S08PacketPlayerPosLook s08PacketPlayerPosLook2) {
         double d = s08PacketPlayerPosLook2.getX() - minecraft.thePlayer.posX;
         double d2 = s08PacketPlayerPosLook2.getY() - minecraft.thePlayer.posY;
         double d3 = s08PacketPlayerPosLook2.getZ() - minecraft.thePlayer.posZ;
         if (Math.sqrt(d * d + d2 * d2 + d3 * d3) < 8.0) {
            this.sendPacketUnregistered(new C06PacketPlayerPosLook(s08PacketPlayerPosLook2.getX(), s08PacketPlayerPosLook2.getY(), s08PacketPlayerPosLook2.getZ(), s08PacketPlayerPosLook2.getYaw(), s08PacketPlayerPosLook2.getPitch(), true));
            packetReceiveEvent.cancel();
         }
      }
   };
   @EventLink
   public final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (this.disabler.isSelected(0) && HypixelState.isServerType(ServerType.HYPIXEL)) {
         if (packetSendEvent.packet instanceof C03PacketPlayer c03PacketPlayer) {
            if (this.cancelCount > 0L) {
               packetSendEvent.cancel();
               this.cancelCount--;
               return;
            }

            if (!c03PacketPlayer.moving && c03PacketPlayer.rotating) {
               packetSendEvent.cancel();
            }
         }

         if (minecraft.thePlayer != null && minecraft.thePlayer.ticksExisted < 120) {
            if (packetSendEvent.packet instanceof C03PacketPlayer c03PacketPlayer2) {
               if (!c03PacketPlayer2.moving && !c03PacketPlayer2.rotating) {
                  packetSendEvent.cancel();
               } else {
                  this.heldPackets.push(new Disabler.HeldPacket(c03PacketPlayer2));
                  packetSendEvent.cancel();
               }
            }

            if (packetSendEvent.packet instanceof C00PacketKeepAlive) {
               this.heldPackets.push(new Disabler.HeldPacket(packetSendEvent.packet));
               packetSendEvent.cancel();
            }

            if (packetSendEvent.packet instanceof C0FPacketConfirmTransaction) {
               this.heldPackets.push(new Disabler.HeldPacket(packetSendEvent.packet));
               packetSendEvent.cancel();
            }
         }
      }

      if (this.disabler.isSelected(1) && minecraft.thePlayer != null) {
         if (packetSendEvent.packet instanceof C03PacketPlayer c03PacketPlayer3) {
            if (!minecraft.thePlayer.onGround && Math.abs(minecraft.thePlayer.motionY) > 0.2 && this.disabler.isSelected(7)) {
               c03PacketPlayer3.onGround = minecraft.thePlayer.ticksExisted % 2 == 0;
            }
         } else if (packetSendEvent.packet instanceof C02PacketUseEntity c02PacketUseEntity && c02PacketUseEntity.getAction() == Action.ATTACK) {
            this.attackedThisTick = true;
         }
      }

      if (this.disabler.isSelected(2) && packetSendEvent.packet instanceof C0FPacketConfirmTransaction) {
         if (this.delayPacket.isEnabled()) {
            novoline.getTaskScheduler().schedule(new ScheduledTask(18000) {
               @Override
               public void run() {
                  if (Disabler.access$200().thePlayer != null) {
                     Disabler.access$100(Disabler.this, packetSendEvent.packet);
                  }
               }
            });
         } else {
            for (int i = 0; i < 4; i++) {
               this.transactionQueue.add(packetSendEvent.packet);
            }
         }

         packetSendEvent.cancel();
      }

      if (this.disabler.isSelected(3) && minecraft.thePlayer != null) {
         if (packetSendEvent.packet instanceof C0FPacketConfirmTransaction) {
            for (int i2 = 0; i2 < 2; i2++) {
               novoline.getTaskScheduler().schedule(new ScheduledTask(9000) {
                  @Override
                  public void run() {
                     if (Disabler.access$000().thePlayer != null) {
                        Disabler.access$300(Disabler.this, packetSendEvent.packet);
                     }
                  }
               });
            }

            packetSendEvent.cancel();
         } else if (packetSendEvent.packet instanceof C0BPacketEntityAction) {
            packetSendEvent.cancel();
         }
      }
   };
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.disabler.isSelected(0) && HypixelState.isServerType(ServerType.HYPIXEL)) {
         if (this.cancelCount > 0L) {
            return;
         }

         if (this.releaseTimer.hasElapsed(minecraft.thePlayer.ticksExisted < 120 ? 1920L : ThreadLocalRandom.current().nextInt(1, 250))) {
            while (!this.heldPackets.isEmpty()) {
               this.sendPacketUnregistered(this.heldPackets.removeLast().packet);
            }

            this.releaseTimer.reset();
         }

         if (minecraft.thePlayer.ticksExisted == 120) {
            preUpdateEvent.posX++;
            preUpdateEvent.posZ++;
         }
      }

      if (this.disabler.isSelected(4)) {
         this.sendPacketUnregistered(
            new C0BPacketEntityAction(minecraft.thePlayer, net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_FALL_FLYING)
         );
      }

      if (this.disabler.isSelected(5) && minecraft.thePlayer != null) {
         if (this.itemStack != minecraft.thePlayer.getHeldItem()
            && minecraft.thePlayer.getHeldItem() != null
            && minecraft.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            for (int i = 0; i < 9; i++) {
               ItemStack itemStack2 = minecraft.thePlayer.inventory.mainInventory[i];
               if (itemStack2 != null && itemStack2.getItem() != null && !(itemStack2.getItem() instanceof ItemSword)) {
                  this.sendPacketUnregistered(new C09PacketHeldItemChange(i));
                  break;
               }
            }
         }

         BlockPos blockPos = new BlockPos(-1, -1, -1);
         ItemStack heldItem = minecraft.thePlayer.getHeldItem();
         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         ItemStack itemStack3 = heldItem;
         this.sendPacketUnregistered(new C08PacketPlayerBlockPlacement(blockPos, 255, itemStack3, f3, f2, f));
         this.itemStack = minecraft.thePlayer.getHeldItem();
      }
   };
   @EventLink
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (this.disabler.isSelected(1)) {
         Killaura killaura2 = ModuleManager.killaura;
         if (!this.attackedThisTick && (killaura2.entityLivingBase == null || !killaura2.isEnabled() || !killaura2.isAttackReady()) && this.initialized) {
            BlockPos blockPos = new BlockPos(-1, -1, -1);
            ItemStack itemStack2 = new ItemStack(Items.water_bucket);
            float f = 0.0F;
            float f2 = 0.5F;
            float f3 = 0.0F;
            ItemStack itemStack3 = itemStack2;
            this.sendPacket(new C08PacketPlayerBlockPlacement(blockPos, 255, itemStack3, f3, f2, f));
            blockPos = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 1.5, minecraft.thePlayer.posZ);
            Block stone2 = Blocks.stone;
            WorldClient theWorld2 = minecraft.theWorld;
            BlockPos blockPos2 = new BlockPos(-1, -1, -1);
            itemStack2 = new ItemStack(stone2.getItem(theWorld2, blockPos2));
            f = 0.0F;
            f2 = 0.94F;
            f3 = 0.0F;
            itemStack3 = itemStack2;
            this.sendPacket(new C08PacketPlayerBlockPlacement(blockPos, 1, itemStack3, f3, f2, f));
         }

         this.attackedThisTick = false;
         this.initialized = true;
      }

      if (this.disabler.isSelected(2)
         && this.queueTimer.hasElapsed(this.delayPacket.isEnabled() ? 18000L : 9000L)
         && (this.releaseReady || !this.delayPacket.isEnabled())) {
         this.releaseReady = false;

         do {
            Packet packet2 = this.transactionQueue.poll();
            if (packet2 != null) {
               this.sendPacketUnregistered(packet2);
            }
         } while (this.transactionQueue.size() > 22);

         this.queueTimer.reset();
         if (this.delayPacket.isEnabled()) {
            this.showNotification(NotificationType.SUCCESS, "Disabler", Language.translate("feature.disabler.notification.fly.text"), 3000L);
         }
      }

      if (this.disabler.isSelected(3) && minecraft.thePlayer != null && minecraft.thePlayer.ticksExisted % 45 == 0) {
         if (!this.flyNotified) {
            this.showNotification(NotificationType.SUCCESS, "Disabler", Language.translate("feature.disabler.notification.fly.text"), 3000L);
            this.flyNotified = true;
         }

         this.sendPacket(new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 11.725, minecraft.thePlayer.posZ, false));
      }
   };
   private boolean attackedThisTick;
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      this.heldPackets.clear();
      this.queueTimer.reset();
      this.flyNotified = false;
      this.attackedThisTick = false;
      this.releaseReady = true;
   };
   private ItemStack itemStack;
   private boolean flyNotified;

   static Minecraft access$000() {
      return minecraft;
   }

   static void access$100(Disabler disabler2, Packet packet2) {
      disabler2.sendPacketUnregistered(packet2);
   }

   static Minecraft access$200() {
      return minecraft;
   }

   static void access$300(Disabler disabler2, Packet packet2) {
      disabler2.sendPacketUnregistered(packet2);
   }

   @Override
   protected void onEnable() {
      this.transactionQueue.clear();
      this.heldPackets.clear();
   }

   public Disabler() {
      super(Category.MISC, "Disabler", "");
   }

   static class HeldPacket {
      private final Packet<?> packet;
      private long queuedAt;

      public HeldPacket(Packet<?> packet2) {
         this.packet = packet2;
         this.queuedAt = System.currentTimeMillis();
      }
   }
}
