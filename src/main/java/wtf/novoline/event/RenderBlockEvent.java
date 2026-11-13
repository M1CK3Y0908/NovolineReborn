package wtf.novoline.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class RenderBlockEvent extends Event {
   public IBlockState iBlockState;
   public BlockPos blockPos;
   public boolean shouldRender;

   public RenderBlockEvent(IBlockState iBlockState2, BlockPos blockPos2) {
      super(23992540611656L, true);
      this.iBlockState = iBlockState2;
      this.blockPos = blockPos2;
   }
}
