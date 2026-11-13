package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
   private final BlockSlab singleSlab;
   private final BlockSlab doubleSlab;

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return this.singleSlab.getUnlocalizedName(stack.getMetadata());
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (stack.stackSize == 0) {
         return false;
      }

      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return false;
      }

      Object slabVariant = this.singleSlab.getVariant(stack);
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this.singleSlab) {
         IProperty property = this.singleSlab.getVariantProperty();
         Comparable comparable = blockState.getValue(property);
         net.minecraft.block.BlockSlab.EnumBlockHalf blockslab$enumblockhalf = blockState.getValue(BlockSlab.HALF);
         if ((
               side == EnumFacing.UP && blockslab$enumblockhalf == net.minecraft.block.BlockSlab.EnumBlockHalf.BOTTOM
                  || side == EnumFacing.DOWN && blockslab$enumblockhalf == net.minecraft.block.BlockSlab.EnumBlockHalf.TOP
            )
            && comparable == slabVariant) {
            IBlockState blockState2 = this.doubleSlab.getDefaultState().withProperty(property, comparable);
            if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, blockState2))
               && worldIn.setBlockState(pos, blockState2, 3)) {
               worldIn.playSoundEffect(
                  pos.getX() + 0.5F,
                  pos.getY() + 0.5F,
                  pos.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
            }

            return true;
         }
      }

      return this.tryPlace(stack, worldIn, pos.offset(side), slabVariant) ? true : super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
   }

   private boolean tryPlace(ItemStack stack, World worldIn, BlockPos pos, Object variantInStack) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this.singleSlab) {
         Comparable comparable = blockState.getValue((IProperty)this.singleSlab.getVariantProperty());
         if (comparable == variantInStack) {
            IBlockState blockState2 = this.doubleSlab.getDefaultState().withProperty((IProperty)this.singleSlab.getVariantProperty(), comparable);
            if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, blockState2))
               && worldIn.setBlockState(pos, blockState2, 3)) {
               worldIn.playSoundEffect(
                  pos.getX() + 0.5F,
                  pos.getY() + 0.5F,
                  pos.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
      BlockPos pos2 = pos;
      IProperty property = this.singleSlab.getVariantProperty();
      Object slabVariant = this.singleSlab.getVariant(stack);
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this.singleSlab) {
         boolean blockState2 = blockState.getValue(BlockSlab.HALF) == net.minecraft.block.BlockSlab.EnumBlockHalf.TOP;
         if ((side == EnumFacing.UP && !blockState2 || side == EnumFacing.DOWN && blockState2) && slabVariant == blockState.getValue(property)) {
            return true;
         }
      }

      pos = pos.offset(side);
      IBlockState blockState2 = worldIn.getBlockState(pos);
      return blockState2.getBlock() == this.singleSlab && slabVariant == blockState2.getValue(property)
         ? true
         : super.canPlaceBlockOnSide(worldIn, pos2, side, player, stack);
   }

   public ItemSlab(Block block, BlockSlab singleSlab, BlockSlab doubleSlab) {
      super(block);
      this.singleSlab = singleSlab;
      this.doubleSlab = doubleSlab;
      this.setMaxDamage(0);
      this.getItem();
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }
}
