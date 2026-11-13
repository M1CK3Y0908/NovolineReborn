package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlock {
   public ItemSnow(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.getItem();
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (stack.stackSize == 0) {
         return false;
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      BlockPos blockPos = pos;
      if ((side != EnumFacing.UP || block != this.block) && !block.isReplaceable(worldIn, pos)) {
         blockPos = pos.offset(side);
         blockState = worldIn.getBlockState(blockPos);
         block = blockState.getBlock();
      }

      if (block == this.block) {
         int value = blockState.getValue(BlockSnow.LAYERS);
         if (value <= 7) {
            IBlockState blockState2 = blockState.withProperty(BlockSnow.LAYERS, value + 1);
            AxisAlignedBB axisAlignedBB = this.block.getCollisionBoundingBox(worldIn, blockPos, blockState2);
            if (axisAlignedBB != null && worldIn.checkNoEntityCollision(axisAlignedBB) && worldIn.setBlockState(blockPos, blockState2, 2)) {
               worldIn.playSoundEffect(
                  blockPos.getX() + 0.5F,
                  blockPos.getY() + 0.5F,
                  blockPos.getZ() + 0.5F,
                  this.block.stepSound.getPlaceSound(),
                  (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.block.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
               return true;
            }
         }
      }

      return super.onItemUse(stack, playerIn, worldIn, blockPos, side, hitX, hitY, hitZ);
   }
}
