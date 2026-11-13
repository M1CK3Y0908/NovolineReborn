package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S03PacketEnableCompression implements Packet<INetHandlerLoginClient> {
   private int compressionTreshold;

   public int getCompressionTreshold() {
      return this.compressionTreshold;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.compressionTreshold);
   }

   public S03PacketEnableCompression(int compressionTresholdIn) {
      this.compressionTreshold = compressionTresholdIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.compressionTreshold = buf.readVarIntFromBuffer();
   }

   public S03PacketEnableCompression() {
   }

   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleEnableCompression(this);
   }
}
