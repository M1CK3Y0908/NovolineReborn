package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoor extends Item {
   private Block block;

   public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door) {
      BlockPos blockPos = pos.offset(facing.rotateY());
      BlockPos blockPos2 = pos.offset(facing.rotateYCCW());
      int i = (worldIn.getBlockState(blockPos2).getBlock().isNormalCube() ? 1 : 0)
         + (worldIn.getBlockState(blockPos2.up()).getBlock().isNormalCube() ? 1 : 0);
      int i2 = (worldIn.getBlockState(blockPos).getBlock().isNormalCube() ? 1 : 0)
         + (worldIn.getBlockState(blockPos.up()).getBlock().isNormalCube() ? 1 : 0);
      boolean flag = worldIn.getBlockState(blockPos2).getBlock() == door || worldIn.getBlockState(blockPos2.up()).getBlock() == door;
      boolean flag2 = worldIn.getBlockState(blockPos).getBlock() == door || worldIn.getBlockState(blockPos.up()).getBlock() == door;
      boolean conditionMet3 = false;
      if (flag && !flag2 || i2 > i) {
         conditionMet3 = true;
      }

      BlockPos blockPos3 = pos.up();
      IBlockState blockState = door.getDefaultState()
         .withProperty(BlockDoor.FACING, facing)
         .withProperty(BlockDoor.HINGE, conditionMet3 ? net.minecraft.block.BlockDoor.EnumHingePosition.RIGHT : net.minecraft.block.BlockDoor.EnumHingePosition.LEFT);
      worldIn.setBlockState(pos, blockState.withProperty(BlockDoor.HALF, net.minecraft.block.BlockDoor.EnumDoorHalf.LOWER), 2);
      worldIn.setBlockState(blockPos3, blockState.withProperty(BlockDoor.HALF, net.minecraft.block.BlockDoor.EnumDoorHalf.UPPER), 2);
      worldIn.notifyNeighborsOfStateChange(pos, door);
      worldIn.notifyNeighborsOfStateChange(blockPos3, door);
   }

   public ItemDoor(Block block) {
      this.block = block;
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side != EnumFacing.UP) {
         return false;
      }

      if (!worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) {
         pos = pos.offset(side);
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (!this.block.canPlaceBlockAt(worldIn, pos)) {
         return false;
      }

      placeDoor(worldIn, pos, EnumFacing.fromAngle(playerIn.rotationYaw), this.block);
      stack.stackSize--;
      return true;
   }
}
