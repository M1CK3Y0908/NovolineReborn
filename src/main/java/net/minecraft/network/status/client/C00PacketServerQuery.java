package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C00PacketServerQuery implements Packet<INetHandlerStatusServer> {
   @Override
   public void writePacketData(PacketBuffer packetBuffer) throws IOException {
   }

   @Override
   public void readPacketData(PacketBuffer packetBuffer) throws IOException {
   }

   public void processPacket(INetHandlerStatusServer handler) {
      handler.processServerQuery(this);
   }
}
