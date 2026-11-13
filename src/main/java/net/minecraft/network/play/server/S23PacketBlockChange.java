package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class S23PacketBlockChange implements Packet<INetHandlerPlayClient> {
   private IBlockState blockState;
   private BlockPos blockPosition;

   public IBlockState getBlockState() {
      return this.blockState;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.blockPosition = buf.readBlockPos();
      this.blockState = Block.BLOCK_STATE_IDS.getByValue(buf.readVarIntFromBuffer());
   }

   public S23PacketBlockChange() {
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.blockPosition);
      buf.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(this.blockState));
   }

   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   public S23PacketBlockChange(World worldIn, BlockPos blockPositionIn) {
      this.blockPosition = blockPositionIn;
      this.blockState = worldIn.getBlockState(blockPositionIn);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleBlockChange(this);
   }
}
