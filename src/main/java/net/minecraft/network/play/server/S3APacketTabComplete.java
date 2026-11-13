package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3APacketTabComplete implements Packet<INetHandlerPlayClient> {
   private String[] matches;

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleTabComplete(this);
   }

   public String[] getMatches2() {
      return this.matches;
   }

   public S3APacketTabComplete(String[] matchesIn) {
      this.matches = matchesIn;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.matches.length);

      for (String text : this.matches) {
         buf.writeString(text);
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.matches = new String[buf.readVarIntFromBuffer()];

      for (int index = 0; index < this.matches.length; index++) {
         this.matches[index] = buf.readStringFromBuffer(32767);
      }
   }

   public String[] getMatches() {
      return this.matches;
   }

   public S3APacketTabComplete() {
   }
}
