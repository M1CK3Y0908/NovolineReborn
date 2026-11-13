package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

public class S22PacketMultiBlockChange implements Packet<INetHandlerPlayClient> {
   private ChunkCoordIntPair chunkPosition;
   private S22PacketMultiBlockChange.BlockUpdateData[] changedBlocks;

   public S22PacketMultiBlockChange(int changedBlockCount, short[] crampedPositionsIn, Chunk chunkIn) {
      this.chunkPosition = new ChunkCoordIntPair(chunkIn.xPosition, chunkIn.zPosition);
      this.changedBlocks = new S22PacketMultiBlockChange.BlockUpdateData[changedBlockCount];

      for (int index = 0; index < this.changedBlocks.length; index++) {
         this.changedBlocks[index] = new S22PacketMultiBlockChange.BlockUpdateData(crampedPositionsIn[index], chunkIn);
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkPosition = new ChunkCoordIntPair(buf.readInt(), buf.readInt());
      this.changedBlocks = new S22PacketMultiBlockChange.BlockUpdateData[buf.readVarIntFromBuffer()];

      for (int index = 0; index < this.changedBlocks.length; index++) {
         this.changedBlocks[index] = new S22PacketMultiBlockChange.BlockUpdateData(
            buf.readShort(), Block.BLOCK_STATE_IDS.getByValue(buf.readVarIntFromBuffer())
         );
      }
   }

   public S22PacketMultiBlockChange.BlockUpdateData[] getChangedBlocks() {
      return this.changedBlocks;
   }

   public S22PacketMultiBlockChange() {
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMultiBlockChange(this);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.chunkPosition.chunkXPos);
      buf.writeInt(this.chunkPosition.chunkZPos);
      buf.writeVarIntToBuffer(this.changedBlocks.length);

      for (S22PacketMultiBlockChange.BlockUpdateData blockUpdateData : this.changedBlocks) {
         buf.writeShort(blockUpdateData.getCompressedPos());
         buf.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(blockUpdateData.getBlockState()));
      }
   }

   public class BlockUpdateData {
      private final short chunkPosCrammed;
      private final IBlockState blockState;

      public short getCompressedPos() {
         return this.chunkPosCrammed;
      }

      public IBlockState getBlockState() {
         return this.blockState;
      }

      public BlockUpdateData(short compressedPos, Chunk chunkIn) {
         this.chunkPosCrammed = compressedPos;
         this.blockState = chunkIn.getBlockState(this.getPos());
      }

      public BlockUpdateData(short compressedPos, IBlockState state) {
         this.chunkPosCrammed = compressedPos;
         this.blockState = state;
      }

      public BlockPos getPos() {
         return new BlockPos(
            S22PacketMultiBlockChange.this.chunkPosition.getBlock(this.chunkPosCrammed >> 12 & 15, this.chunkPosCrammed & 255, this.chunkPosCrammed >> 8 & 15)
         );
      }
   }
}
