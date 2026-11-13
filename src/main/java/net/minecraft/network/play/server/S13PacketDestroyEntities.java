package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities implements Packet<INetHandlerPlayClient> {
   private int[] entityIDs;

   public int[] getEntityIDs() {
      return this.entityIDs;
   }

   public S13PacketDestroyEntities(int... entityIDsIn) {
      this.entityIDs = entityIDsIn;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityIDs.length);

      for (int index = 0; index < this.entityIDs.length; index++) {
         buf.writeVarIntToBuffer(this.entityIDs[index]);
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityIDs = new int[buf.readVarIntFromBuffer()];

      for (int index = 0; index < this.entityIDs.length; index++) {
         this.entityIDs[index] = buf.readVarIntFromBuffer();
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleDestroyEntities(this);
   }

   public S13PacketDestroyEntities() {
   }
}
