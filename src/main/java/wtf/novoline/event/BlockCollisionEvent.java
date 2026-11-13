package wtf.novoline.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockCollisionEvent extends Event {
   public BlockPos blockPos;
   public IBlockState iBlockState;
   public AxisAlignedBB axisAlignedBB;

   public BlockCollisionEvent(BlockPos blockPos2, IBlockState iBlockState2, AxisAlignedBB axisAlignedBB2) {
      super(23992540611656L, true);
      this.blockPos = blockPos2;
      this.iBlockState = iBlockState2;
      this.axisAlignedBB = axisAlignedBB2;
   }
}
