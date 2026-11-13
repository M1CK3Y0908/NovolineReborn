package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S36PacketSignEditorOpen implements Packet<INetHandlerPlayClient> {
   private BlockPos signPosition;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.signPosition);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSignEditorOpen(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.signPosition = buf.readBlockPos();
   }

   public S36PacketSignEditorOpen(BlockPos signPositionIn) {
      this.signPosition = signPositionIn;
   }

   public S36PacketSignEditorOpen() {
   }

   public BlockPos getSignPosition() {
      return this.signPosition;
   }
}
