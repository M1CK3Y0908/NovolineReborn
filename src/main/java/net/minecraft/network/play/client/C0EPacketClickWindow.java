package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0EPacketClickWindow implements Packet<INetHandlerPlayServer> {
   public int windowId;
   public int slotId;
   public int usedButton;
   public ItemStack clickedItem;
   public short actionNumber;
   public int mode;

   public C0EPacketClickWindow(int windowId, int slotId, int usedButton, int mode, ItemStack clickedItem, short actionNumber) {
      this.windowId = windowId;
      this.slotId = slotId;
      this.usedButton = usedButton;
      this.clickedItem = clickedItem != null ? clickedItem.copy() : null;
      this.actionNumber = actionNumber;
      this.mode = mode;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.slotId);
      buf.writeByte(this.usedButton);
      buf.writeShort(this.actionNumber);
      buf.writeByte(this.mode);
      buf.writeItemStackToBuffer(this.clickedItem);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.slotId = buf.readShort();
      this.usedButton = buf.readByte();
      this.actionNumber = buf.readShort();
      this.mode = buf.readByte();
      this.clickedItem = buf.readItemStackFromBuffer();
   }

   public C0EPacketClickWindow() {
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processClickWindow(this);
   }
}
