package net.minecraft.item;

import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {
   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      if (side != EnumFacing.UP) {
         return false;
      }

      boolean replaceable = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
      if (!replaceable) {
         pos = pos.up();
      }

      EnumFacing horizontal = EnumFacing.getHorizontal(MathHelper.floor_double(playerIn.rotationYaw * 4.0F / 360.0F + 0.5) & 3);
      BlockPos blockPos = pos.offset(horizontal);
      if (playerIn.canPlayerEdit(pos, side, stack) && playerIn.canPlayerEdit(blockPos, side, stack)) {
         boolean replaceable2 = worldIn.getBlockState(blockPos).getBlock().isReplaceable(worldIn, blockPos);
         boolean flag = replaceable || worldIn.isAirBlock(pos);
         boolean flag2 = replaceable2 || worldIn.isAirBlock(blockPos);
         if (flag && flag2 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos.down())) {
            IBlockState iBlockState = Blocks.bed
               .getDefaultState()
               .withProperty(BlockBed.OCCUPIED, false)
               .withProperty(BlockBed.FACING, horizontal)
               .withProperty(BlockBed.PART, net.minecraft.block.BlockBed.EnumPartType.FOOT);
            if (worldIn.setBlockState(pos, iBlockState, 3)) {
               IBlockState iBlockState2 = iBlockState.withProperty(BlockBed.PART, net.minecraft.block.BlockBed.EnumPartType.HEAD);
               worldIn.setBlockState(blockPos, iBlockState2, 3);
            }

            stack.stackSize--;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public ItemBed() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }
}
