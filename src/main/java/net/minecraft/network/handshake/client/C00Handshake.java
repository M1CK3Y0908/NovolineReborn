package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake implements Packet<INetHandlerHandshakeServer> {
   private int protocolVersion;
   private EnumConnectionState requestedState;
   private String ip;
   private int port;

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.protocolVersion = buf.readVarIntFromBuffer();
      this.ip = buf.readStringFromBuffer(255);
      this.port = buf.readUnsignedShort();
      this.requestedState = EnumConnectionState.getById(buf.readVarIntFromBuffer());
   }

   public EnumConnectionState getRequestedState() {
      return this.requestedState;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.protocolVersion);
      buf.writeString(this.ip);
      buf.writeShort(this.port);
      buf.writeVarIntToBuffer(this.requestedState.getId());
   }

   public void processPacket(INetHandlerHandshakeServer handler) {
      handler.processHandshake(this);
   }

   public C00Handshake() {
   }

   public C00Handshake(String text, int i, EnumConnectionState enumConnectionState) {
      this.protocolVersion = 47;
      this.ip = text;
      this.port = i;
      this.requestedState = enumConnectionState;
   }
}
