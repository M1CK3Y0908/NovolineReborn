package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockFalling extends Block {
   public static boolean fallInstantly;

   public static boolean canFallInto(World worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos).getBlock();
      Material material = block.blockMaterial;
      return block == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
   }

   @Override
   public int tickRate(World worldIn) {
      return 2;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         this.checkFallable(worldIn, pos);
      }
   }

   public BlockFalling() {
      super(Material.sand);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   protected void onStartFalling(EntityFallingBlock fallingEntity) {
   }

   public void onEndFalling(World worldIn, BlockPos pos) {
   }

   private void checkFallable(World worldIn, BlockPos pos) {
      if (canFallInto(worldIn, pos.down()) && pos.getY() >= 0) {
         if (fallInstantly || !worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
            worldIn.setBlockToAir(pos);
            BlockPos blockPos = pos.down();

            while (canFallInto(worldIn, blockPos) && blockPos.getY() > 0) {
               blockPos = blockPos.down();
            }

            if (blockPos.getY() > 0) {
               worldIn.setBlockState(blockPos.up(), this.getDefaultState());
            }
         } else if (!worldIn.isRemote) {
            EntityFallingBlock entityFallingBlock = new EntityFallingBlock(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, worldIn.getBlockState(pos));
            this.onStartFalling(entityFallingBlock);
            worldIn.spawnEntityInWorld(entityFallingBlock);
         }
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   public BlockFalling(Material materialIn) {
      super(materialIn);
   }
}
