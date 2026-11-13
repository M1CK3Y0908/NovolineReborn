package net.minecraft.network;

import java.io.IOException;

public interface Packet<T extends INetHandler> {
   void processPacket(T value);

   void readPacketData(PacketBuffer packetBuffer) throws IOException;

   void writePacketData(PacketBuffer packetBuffer) throws IOException;
}
