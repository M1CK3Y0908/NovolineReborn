package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import wtf.novoline.module.ModuleManager;

public class BlockBarrier extends Block {
   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return ModuleManager.seeBarriers.isEnabled() ? EnumWorldBlockLayer.TRANSLUCENT : super.getBlockLayer();
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   @Override
   public int getRenderType() {
      return ModuleManager.seeBarriers.isEnabled() ? 3 : -1;
   }

   @Override
   public float getAmbientOcclusionLightValue() {
      return 1.0F;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
      if (ModuleManager.seeBarriers.isEnabled()) {
         IBlockState blockState = iBlockAccess.getBlockState(blockPos);
         Block block2 = blockState.getBlock();
         if (iBlockAccess.getBlockState(blockPos.offset(enumFacing.getOpposite())) != blockState) {
            return true;
         }

         if (block2 == this) {
            return false;
         }
      }

      return super.shouldSideBeRendered(iBlockAccess, blockPos, enumFacing);
   }

   protected BlockBarrier() {
      super(Material.barrier);
      this.setBlockUnbreakable();
      this.setResistance(6000001.0F);
      this.disableStats();
      this.translucent = true;
   }
}
