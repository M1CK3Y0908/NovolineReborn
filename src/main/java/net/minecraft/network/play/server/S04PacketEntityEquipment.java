package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S04PacketEntityEquipment implements Packet<INetHandlerPlayClient> {
   private int equipmentSlot;
   private int entityID;
   private ItemStack itemStack;

   public int getEquipmentSlot() {
      return this.equipmentSlot;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarIntFromBuffer();
      this.equipmentSlot = buf.readShort();
      this.itemStack = buf.readItemStackFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityID);
      buf.writeShort(this.equipmentSlot);
      buf.writeItemStackToBuffer(this.itemStack);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityEquipment(this);
   }

   public int getEntityID() {
      return this.entityID;
   }

   public S04PacketEntityEquipment() {
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public S04PacketEntityEquipment(int entityIDIn, int equipmentSlotIn, ItemStack itemStackIn) {
      this.entityID = entityIDIn;
      this.equipmentSlot = equipmentSlotIn;
      this.itemStack = itemStackIn == null ? null : itemStackIn.copy();
   }
}
