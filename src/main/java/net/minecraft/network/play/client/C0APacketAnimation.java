package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0APacketAnimation implements Packet<INetHandlerPlayServer> {
   public void processPacket(INetHandlerPlayServer handler) {
      handler.handleAnimation(this);
   }

   @Override
   public void writePacketData(PacketBuffer packetBuffer) throws IOException {
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) throws IOException {
   }
}
