package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {
   private boolean ignoreSimilarity = false;

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   protected BlockBreakable(Material material2) {
      this(material2, material2.getMaterialMapColor());
   }

   protected BlockBreakable(Material material2, MapColor mapColor) {
      super(material2, mapColor);
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      if (this == Blocks.glass || this == Blocks.stained_glass) {
         if (worldIn.getBlockState(pos.offset(side.getOpposite())) != blockState) {
            return true;
         }

         if (block == this) {
            return false;
         }
      }

      return !this.ignoreSimilarity && block == this ? false : super.shouldSideBeRendered(worldIn, pos, side);
   }
}
