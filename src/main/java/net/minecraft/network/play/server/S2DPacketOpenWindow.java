package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S2DPacketOpenWindow implements Packet<INetHandlerPlayClient> {
   private IChatComponent windowTitle;
   private int slotCount;
   private int entityId;
   private String inventoryType;
   private int windowId;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeString(this.inventoryType);
      buf.writeChatComponent(this.windowTitle);
      buf.writeByte(this.slotCount);
      if (this.inventoryType.equals("EntityHorse")) {
         buf.writeInt(this.entityId);
      }
   }

   public int getSlotCount() {
      return this.slotCount;
   }

   public String getGuiId() {
      return this.inventoryType;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public int getWindowId() {
      return this.windowId;
   }

   public S2DPacketOpenWindow(int windowIdIn, String guiId, IChatComponent windowTitleIn, int slotCountIn, int incomingEntityId) {
      this(windowIdIn, guiId, windowTitleIn, slotCountIn);
      this.entityId = incomingEntityId;
   }

   public S2DPacketOpenWindow(int incomingWindowId, String incomingWindowTitle, IChatComponent windowTitleIn) {
      this(incomingWindowId, incomingWindowTitle, windowTitleIn, 0);
   }

   public IChatComponent getWindowTitle() {
      return this.windowTitle;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleOpenWindow(this);
   }

   public S2DPacketOpenWindow() {
   }

   public boolean hasSlots() {
      return this.slotCount > 0;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readUnsignedByte();
      this.inventoryType = buf.readStringFromBuffer(32);
      this.windowTitle = buf.readChatComponent();
      this.slotCount = buf.readUnsignedByte();
      if (this.inventoryType.equals("EntityHorse")) {
         this.entityId = buf.readInt();
      }
   }

   public S2DPacketOpenWindow(int windowIdIn, String guiId, IChatComponent windowTitleIn, int slotCountIn) {
      this.windowId = windowIdIn;
      this.inventoryType = guiId;
      this.windowTitle = windowTitleIn;
      this.slotCount = slotCountIn;
   }
}
