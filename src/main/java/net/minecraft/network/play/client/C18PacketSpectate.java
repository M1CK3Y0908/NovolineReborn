package net.minecraft.network.play.client;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C18PacketSpectate implements Packet<INetHandlerPlayServer> {
   public UUID id;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeUuid(this.id);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.id = buf.readUuid();
   }

   public C18PacketSpectate() {
   }

   public C18PacketSpectate(UUID id) {
      this.id = id;
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.handleSpectate(this);
   }
}
