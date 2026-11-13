package net.minecraft.block;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockAir extends Block {
   private static Map mapOriginalOpacity = new IdentityHashMap();

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return false;
   }

   @Override
   public int getRenderType() {
      return -1;
   }

   protected BlockAir() {
      super(Material.air);
   }

   public static void setLightOpacity(Block arg0, int arg1) {
      if (!mapOriginalOpacity.containsKey(arg0)) {
         mapOriginalOpacity.put(arg0, arg0.lightOpacity);
      }

      arg0.lightOpacity = arg1;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }
}
