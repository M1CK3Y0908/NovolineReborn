package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive implements Packet<INetHandlerPlayServer> {
   public int key;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.key);
   }

   public C00PacketKeepAlive(int key) {
      this.key = key;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.key = buf.readVarIntFromBuffer();
   }

   public C00PacketKeepAlive() {
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processKeepAlive(this);
   }
}
