package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S32PacketConfirmTransaction implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private boolean accepted;
   private short actionNumber;

   public void setActionNumber(short s) {
      this.actionNumber = s;
   }

   public S32PacketConfirmTransaction() {
   }

   public void setAccepted(boolean flag) {
      this.accepted = flag;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.actionNumber);
      buf.writeBoolean(this.accepted);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readUnsignedByte();
      this.actionNumber = buf.readShort();
      this.accepted = buf.readBoolean();
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleConfirmTransaction(this);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public void setWindowId(int i) {
      this.windowId = i;
   }

   public S32PacketConfirmTransaction(int windowIdIn, short actionNumberIn, boolean acceptedIn) {
      this.windowId = windowIdIn;
      this.actionNumber = actionNumberIn;
      this.accepted = acceptedIn;
   }

   public boolean isAccepted() {
      return this.accepted;
   }

   public short getActionNumber() {
      return this.actionNumber;
   }
}
