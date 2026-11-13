package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2BPacketChangeGameState implements Packet<INetHandlerPlayClient> {
   public static final String[] MESSAGE_NAMES = new String[]{"tile.bed.notValid"};
   private float value;
   private int state;

   public S2BPacketChangeGameState() {
   }

   public int getGameState() {
      return this.state;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleChangeGameState(this);
   }

   public float getValue() {
      return this.value;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.state = buf.readUnsignedByte();
      this.value = buf.readFloat();
   }

   public S2BPacketChangeGameState(int stateIn, float valueIn) {
      this.state = stateIn;
      this.value = valueIn;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.state);
      buf.writeFloat(this.value);
   }
}
