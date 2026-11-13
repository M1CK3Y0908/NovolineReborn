package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemBucket extends Item {
   private Block isFull;

   public ItemBucket(Block containedBlock) {
      this.maxStackSize = 1;
      this.isFull = containedBlock;
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      boolean flag = this.isFull == Blocks.air;
      MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, flag);
      if (movingObjectPosition == null) {
         return itemStackIn;
      }

      if (movingObjectPosition.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         if (!worldIn.isBlockModifiable(playerIn, blockPos)) {
            return itemStackIn;
         }

         if (flag) {
            if (!playerIn.canPlayerEdit(blockPos.offset(movingObjectPosition.sideHit), movingObjectPosition.sideHit, itemStackIn)) {
               return itemStackIn;
            }

            IBlockState blockState = worldIn.getBlockState(blockPos);
            Material material = blockState.getBlock().getMaterial();
            if (material == Material.water && blockState.getValue(BlockLiquid.LEVEL) == 0) {
               worldIn.setBlockToAir(blockPos);
               playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return this.fillBucket(itemStackIn, playerIn, Items.water_bucket);
            }

            if (material == Material.lava && blockState.getValue(BlockLiquid.LEVEL) == 0) {
               worldIn.setBlockToAir(blockPos);
               playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return this.fillBucket(itemStackIn, playerIn, Items.lava_bucket);
            }
         } else {
            if (this.isFull == Blocks.air) {
               return new ItemStack(Items.bucket);
            }

            BlockPos blockPos2 = blockPos.offset(movingObjectPosition.sideHit);
            if (!playerIn.canPlayerEdit(blockPos2, movingObjectPosition.sideHit, itemStackIn)) {
               return itemStackIn;
            }

            if (this.tryPlaceContainedLiquid(worldIn, blockPos2) && !playerIn.capabilities.isCreativeMode) {
               playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return new ItemStack(Items.bucket);
            }
         }
      }

      return itemStackIn;
   }

   private ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket) {
      if (player.capabilities.isCreativeMode) {
         return emptyBuckets;
      }

      if (--emptyBuckets.stackSize <= 0) {
         return new ItemStack(fullBucket);
      }

      if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
         player.getEntityItem3(new ItemStack(fullBucket, 1, 0));
      }

      return emptyBuckets;
   }

   public boolean tryPlaceContainedLiquid(World worldIn, BlockPos pos) {
      if (this.isFull == Blocks.air) {
         return false;
      }

      Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
      boolean flag = !material.isSolid();
      if (!worldIn.isAirBlock(pos) && !flag) {
         return false;
      }

      if (worldIn.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water) {
         int xCoordinate = pos.getX();
         int yCoordinate = pos.getY();
         int zCoordinate = pos.getZ();
         worldIn.playSoundEffect(
            xCoordinate + 0.5F,
            yCoordinate + 0.5F,
            zCoordinate + 0.5F,
            "random.fizz",
            0.5F,
            2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F
         );

         for (int outerIndex = 0; outerIndex < 8; outerIndex++) {
            worldIn.spawnParticle(
               EnumParticleTypes.SMOKE_LARGE, xCoordinate + Math.random(), yCoordinate + Math.random(), zCoordinate + Math.random(), 0.0, 0.0, 0.0
            );
         }
      } else {
         if (!worldIn.isRemote && flag && !material.isLiquid()) {
            worldIn.destroyBlock(pos, true);
         }

         worldIn.setBlockState(pos, this.isFull.getDefaultState(), 3);
      }

      return true;
   }
}
