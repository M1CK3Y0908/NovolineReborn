package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private ItemStack[] itemStacks;

   public S30PacketWindowItems() {
   }

   public S30PacketWindowItems(int windowIdIn, List<ItemStack> itemStacksIn) {
      this.windowId = windowIdIn;
      this.itemStacks = new ItemStack[itemStacksIn.size()];

      for (int index = 0; index < this.itemStacks.length; index++) {
         ItemStack itemStack = itemStacksIn.get(index);
         this.itemStacks[index] = itemStack == null ? null : itemStack.copy();
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.itemStacks.length);

      for (ItemStack itemStack2 : this.itemStacks) {
         buf.writeItemStackToBuffer(itemStack2);
      }
   }

   public int getWindowId() {
      return this.windowId;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readUnsignedByte();
      int shortValue = buf.readShort();
      this.itemStacks = new ItemStack[shortValue];

      for (int innerIndex = 0; innerIndex < shortValue; innerIndex++) {
         this.itemStacks[innerIndex] = buf.readItemStackFromBuffer();
      }
   }

   public ItemStack[] getItemStacks() {
      return this.itemStacks;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleWindowItems(this);
   }
}
