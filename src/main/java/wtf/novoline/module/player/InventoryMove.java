package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.gui.clickgui.ClickGuiScreen;
import wtf.novoline.gui.menu.MenuClickGuiScreen;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.time.Stopwatch;

public class InventoryMove extends Module {
   private int groundTicks;
   public final ComboSetting inventoryMove = settingManager.register(new ComboSetting(this, "Mode", 0, "Normal", "Advanced", "Blink"));
   public final CheckboxSetting mouseMove = settingManager.register(new CheckboxSetting(this, "Mouse move", false) {
      @Override
      public boolean shouldShow() {
         return InventoryMove.this.inventoryMove.getCurrentElement() != 1;
      }
   });
   public final CheckboxSetting showStatus = settingManager.register(new CheckboxSetting(this, "Show status", true) {
      @Override
      public boolean shouldShow() {
         return InventoryMove.this.inventoryMove.getCurrentElement() == 1;
      }
   });
   private final CheckboxSetting allowSneak = settingManager.register(new CheckboxSetting(this, "Allow sneak", false));
   private final CheckboxSetting autoStop = settingManager.register(new CheckboxSetting(this, "Auto stop", false) {
      @Override
      public boolean shouldShow() {
         return InventoryMove.this.inventoryMove.getCurrentElement() == 1;
      }
   });
   private boolean openPacketResent;
   private final List<C03PacketPlayer> heldPackets = new ArrayList<>();
   private final Stopwatch timer = new Stopwatch();
   private final Packet<?> closeWindowPacket = new C0DPacketCloseWindow(0);
   public final List<Packet<?>> queuedPackets = new ArrayList<>();
   private boolean inventoryOpen = false;
   @EventLink
   public final Listener<RunTickEvent> onRunTick = runTickEvent -> {
      this.setSuffix(this.inventoryMove.getCurrentElementAsString());
      if (minecraft.thePlayer != null && minecraft.thePlayer.onGround && ++this.groundTicks > 4) {
         this.autoStopActive = false;
      }
   };
   private boolean autoStopActive;
   private boolean serverInventoryOpen;
   private boolean closeSent;
   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      if (this.autoStopActive && this.groundTicks > 0) {
         if (this.groundTicks > 1) {
            minecraft.thePlayer.movementInput.moveForward = 0.0F;
            minecraft.thePlayer.movementInput.moveStrafe = 0.0F;
         }

         playerMoveEvent.cancel();
      }
   };
   private boolean windowOpenHidden;
   private Packet<?> openInventoryPacket;
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.inventoryMove.getCurrentElement() == 2 && this.windowOpenHidden && !(minecraft.currentScreen instanceof GuiContainer)) {
         this.sendPacket(new C0DPacketCloseWindow(0));
      }
   };
   @EventLink
   public final Listener<UpdateEvent> onUpdate = updateEvent -> {
      GuiScreen currentScreen2 = minecraft.currentScreen;
      int currentElement = this.inventoryMove.getCurrentElement();
      if (currentElement == 1) {
         this.mouseMove.setEnabled(false);
         if (!PlayerUtils.isMoving() && this.queuedPackets.size() > 0) {
            this.sendPacketUnregistered(this.openInventoryPacket);
            this.queuedPackets.forEach(packet2 -> this.sendPacketUnregistered(packet2));
            this.serverInventoryOpen = true;
            this.closeSent = false;
            this.queuedPackets.clear();
            if (!this.inventoryOpen) {
               this.sendPacketUnregistered(this.closeWindowPacket);
               this.serverInventoryOpen = false;
               novoline.getNotificationManager().show(new Notification(NotificationType.INFO, "InventoryMove", "Inventory synchronized!", 1500L));
            }
         }
      }

      if (currentScreen2 != null
         && !(currentScreen2 instanceof GuiChat)
         && !(currentScreen2 instanceof GuiEditSign)
         && !(currentScreen2 instanceof GuiScreenBook)
         && !(currentScreen2 instanceof GuiCommandBlock)
         && !(currentScreen2 instanceof MenuClickGuiScreen menuClickGuiScreen && menuClickGuiScreen.getTextSelection().isFocused() && menuClickGuiScreen.getTextSelection().isVisible())
         && !(currentScreen2 instanceof ClickGuiScreen clickGuiScreen && clickGuiScreen.getCategoryTab() != null && clickGuiScreen.getCategoryTab().isFocused())
         && (!(currentScreen2 instanceof GuiContainer) || currentScreen2 instanceof GuiInventory || currentElement != 1)) {
         for (KeyBinding keyBinding : this.keyBindingArray) {
            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), GameSettings.isKeyDown(keyBinding));
         }

         if (this.allowSneak.isEnabled()) {
            KeyBinding keyBindSneak2 = minecraft.gameSettings.keyBindSneak;
            KeyBinding.setKeyBindState(keyBindSneak2.getKeyCode(), GameSettings.isKeyDown(keyBindSneak2));
         }
      }
   };
   private KeyBinding[] keyBindingArray;
   @EventLink
   public final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      int currentElement = this.inventoryMove.getCurrentElement();
      if (currentElement == 1) {
         if (this.inventoryOpen && minecraft.thePlayer != null) {
            if (PlayerUtils.isMoving()) {
               if (!this.closeSent) {
                  this.sendPacketUnregistered(this.closeWindowPacket);
                  this.closeSent = true;
                  this.openPacketResent = false;
                  this.serverInventoryOpen = false;
               }
            } else if (!this.openPacketResent && !this.serverInventoryOpen && this.queuedPackets.size() == 0) {
               this.sendPacketUnregistered(this.openInventoryPacket);
               this.openPacketResent = true;
               this.closeSent = false;
               this.serverInventoryOpen = true;
            }
         }

         if (packetSendEvent.packet instanceof C0EPacketClickWindow && PlayerUtils.isMoving() && this.inventoryOpen) {
            this.queuedPackets.add(packetSendEvent.packet);
            packetSendEvent.cancel();
         } else if (packetSendEvent.packet instanceof C16PacketClientStatus) {
            this.openInventoryPacket = packetSendEvent.packet;
            this.inventoryOpen = true;
            this.serverInventoryOpen = true;
         } else if (packetSendEvent.packet instanceof C0DPacketCloseWindow) {
            if (!this.inventoryOpen) {
               return;
            }

            this.inventoryOpen = false;
            this.closeSent = false;
            if (this.queuedPackets.size() > 0 || !this.serverInventoryOpen) {
               packetSendEvent.cancel();
               if (this.autoStop.isEnabled()) {
                  this.autoStopActive = true;
                  this.groundTicks = 0;
               }
            }

            this.serverInventoryOpen = false;
         }
      } else if (currentElement == 2) {
         if (packetSendEvent.packet instanceof C03PacketPlayer c03PacketPlayer) {
            if (this.windowOpenHidden) {
               this.heldPackets.add(c03PacketPlayer);
               packetSendEvent.cancel();
            } else if (!this.heldPackets.isEmpty()) {
               this.heldPackets.forEach(packet2 -> this.sendPacketUnregistered(packet2));
               this.heldPackets.clear();
            }
         } else if (this.shouldCancelWindowPacket(packetSendEvent.packet)) {
            packetSendEvent.cancel();
         }
      }
   };
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (this.inventoryMove.getCurrentElement() == 2 && this.shouldCancelWindowPacket(packetReceiveEvent.packet)) {
         packetReceiveEvent.cancel();
      }
   };

   @Override
   protected void onDisable() {
      this.keyBindingArray = null;
   }

   public InventoryMove() {
      super(Category.PLAYER, "InventoryMove", "");
   }

   @Override
   protected void onEnable() {
      this.keyBindingArray = new KeyBinding[]{
         minecraft.gameSettings.keyBindForward,
         minecraft.gameSettings.keyBindBack,
         minecraft.gameSettings.keyBindLeft,
         minecraft.gameSettings.keyBindRight,
         minecraft.gameSettings.keyBindJump,
         minecraft.gameSettings.keyBindSprint
      };
      this.timer.reset();
   }

   private boolean shouldCancelWindowPacket(Packet packet3) {
      Packet packet4 = packet3;
      if ((!(packet4 instanceof C16PacketClientStatus) || ((C16PacketClientStatus)packet4).status != EnumState.OPEN_INVENTORY_ACHIEVEMENT)
         && !(packet3 instanceof S2DPacketOpenWindow)) {
         if (packet3 instanceof S2EPacketCloseWindow || packet3 instanceof C0DPacketCloseWindow) {
            if (!this.windowOpenHidden) {
               return true;
            }

            this.windowOpenHidden = false;
         }
      } else {
         if (this.windowOpenHidden) {
            return true;
         }

         this.windowOpenHidden = true;
      }

      return false;
   }
}
