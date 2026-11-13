package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S05PacketSpawnPosition implements Packet<INetHandlerPlayClient> {
   private BlockPos spawnBlockPos;

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.spawnBlockPos = buf.readBlockPos();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.spawnBlockPos);
   }

   public BlockPos getSpawnPos() {
      return this.spawnBlockPos;
   }

   public S05PacketSpawnPosition(BlockPos spawnBlockPosIn) {
      this.spawnBlockPos = spawnBlockPosIn;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnPosition(this);
   }

   public S05PacketSpawnPosition() {
   }
}
