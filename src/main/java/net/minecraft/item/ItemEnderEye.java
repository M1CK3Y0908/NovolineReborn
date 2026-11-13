package net.minecraft.item;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {
   public ItemEnderEye() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, false);
      if (movingObjectPosition != null
         && movingObjectPosition.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK
         && worldIn.getBlockState(movingObjectPosition.getBlockPos()).getBlock() == Blocks.end_portal_frame) {
         return itemStackIn;
      }

      if (!worldIn.isRemote) {
         BlockPos blockPos = worldIn.getStrongholdPos("Stronghold", new BlockPos(playerIn));
         if (blockPos != null) {
            EntityEnderEye entityEnderEye = new EntityEnderEye(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ);
            entityEnderEye.moveTowards(blockPos);
            worldIn.spawnEntityInWorld(entityEnderEye);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1002, new BlockPos(playerIn), 0);
            if (!playerIn.capabilities.isCreativeMode) {
               itemStackIn.stackSize--;
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         }
      }

      return itemStackIn;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack) || blockState.getBlock() != Blocks.end_portal_frame || blockState.getValue(BlockEndPortalFrame.EYE)) {
         return false;
      }

      if (worldIn.isRemote) {
         return true;
      }

      worldIn.setBlockState(pos, blockState.withProperty(BlockEndPortalFrame.EYE, true), 2);
      worldIn.updateComparatorOutputLevel(pos, Blocks.end_portal_frame);
      stack.stackSize--;

      for (int i = 0; i < 16; i++) {
         double d = pos.getX() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double d2 = pos.getY() + 0.8125F;
         double d3 = pos.getZ() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double d4 = 0.0;
         double d5 = 0.0;
         double d6 = 0.0;
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, d4, d5, d6);
      }

      EnumFacing value = blockState.getValue(BlockEndPortalFrame.FACING);
      int i2 = 0;
      int i3 = 0;
      boolean flag = false;
      boolean flag2 = true;
      EnumFacing enumFacing = value.rotateY();

      for (int i4 = -2; i4 <= 2; i4++) {
         BlockPos blockPos2 = pos.offset(enumFacing, i4);
         IBlockState blockState2 = worldIn.getBlockState(blockPos2);
         if (blockState2.getBlock() == Blocks.end_portal_frame) {
            if (!blockState2.getValue(BlockEndPortalFrame.EYE)) {
               flag2 = false;
               break;
            }

            i3 = i4;
            if (!flag) {
               i2 = i4;
               flag = true;
            }
         }
      }

      if (flag2 && i3 == i2 + 2) {
         BlockPos blockPos3 = pos.offset(value, 4);

         for (int i5 = i2; i5 <= i3; i5++) {
            BlockPos blockPos4 = blockPos3.offset(enumFacing, i5);
            IBlockState blockState3 = worldIn.getBlockState(blockPos4);
            if (blockState3.getBlock() != Blocks.end_portal_frame || !blockState3.getValue(BlockEndPortalFrame.EYE)) {
               flag2 = false;
               break;
            }
         }

         for (int i6 = i2 - 1; i6 <= i3 + 1; i6 += 4) {
            blockPos3 = pos.offset(enumFacing, i6);

            for (int i7 = 1; i7 <= 3; i7++) {
               BlockPos blockPos5 = blockPos3.offset(value, i7);
               IBlockState blockState4 = worldIn.getBlockState(blockPos5);
               if (blockState4.getBlock() != Blocks.end_portal_frame || !blockState4.getValue(BlockEndPortalFrame.EYE)) {
                  flag2 = false;
                  break;
               }
            }
         }

         if (flag2) {
            for (int i8 = i2; i8 <= i3; i8++) {
               blockPos3 = pos.offset(enumFacing, i8);

               for (int i9 = 1; i9 <= 3; i9++) {
                  BlockPos blockPos6 = blockPos3.offset(value, i9);
                  worldIn.setBlockState(blockPos6, Blocks.end_portal.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
