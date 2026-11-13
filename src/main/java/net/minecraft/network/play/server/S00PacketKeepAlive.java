package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S00PacketKeepAlive implements Packet<INetHandlerPlayClient> {
   public int id;

   public void setId(int i) {
      this.id = i;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleKeepAlive(this);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.id);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.id = buf.readVarIntFromBuffer();
   }

   public int getId() {
      return this.id;
   }

   public S00PacketKeepAlive(int idIn) {
      this.id = idIn;
   }

   public S00PacketKeepAlive() {
   }
}
