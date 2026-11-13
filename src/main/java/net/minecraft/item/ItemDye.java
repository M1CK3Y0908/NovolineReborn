package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemDye extends Item {
   public static final int[] dyeColors = new int[]{
      1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
   };

   public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target) {
      IBlockState blockState = worldIn.getBlockState(target);
      if (blockState.getBlock() instanceof IGrowable) {
         IGrowable growable = (IGrowable)blockState.getBlock();
         if (growable.canGrow(worldIn, target, blockState, worldIn.isRemote)) {
            if (!worldIn.isRemote) {
               if (growable.canUseBonemeal(worldIn, worldIn.rand, target, blockState)) {
                  growable.grow(worldIn, worldIn.rand, target, blockState);
               }

               stack.stackSize--;
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target) {
      if (target instanceof EntitySheep entitySheep) {
         EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
         if (!entitySheep.getSheared() && entitySheep.getFleeceColor() != dyeColor) {
            entitySheep.setFleeceColor(dyeColor);
            stack.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (int index = 0; index < 16; index++) {
         subItems.add(new ItemStack(itemIn, 1, index));
      }
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      int metadata = stack.getMetadata();
      return super.getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(metadata).getUnlocalizedName();
   }

   public static void spawnBonemealParticles(World worldIn, BlockPos pos, int amount) {
      if (amount == 0) {
         amount = 15;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      if (block.getMaterial() != Material.air) {
         block.setBlockBoundsBasedOnState(worldIn, pos);

         for (int index = 0; index < amount; index++) {
            double d = itemRand.nextGaussian() * 0.02;
            double d2 = itemRand.nextGaussian() * 0.02;
            double d3 = itemRand.nextGaussian() * 0.02;
            worldIn.spawnParticle(
               EnumParticleTypes.VILLAGER_HAPPY,
               pos.getX() + itemRand.nextFloat(),
               pos.getY() + itemRand.nextFloat() * block.getBlockBoundsMaxY(),
               pos.getZ() + itemRand.nextFloat(),
               d,
               d2,
               d3
            );
         }
      }
   }

   public ItemDye() {
      this.getItem();
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return false;
      }

      EnumDyeColor enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
      if (enumDyeColor == EnumDyeColor.WHITE) {
         if (applyBonemeal(stack, worldIn, pos)) {
            if (!worldIn.isRemote) {
               worldIn.playAuxSFX(2005, pos, 0);
            }

            return true;
         }
      } else if (enumDyeColor == EnumDyeColor.BROWN) {
         IBlockState blockState2 = worldIn.getBlockState(pos);
         if (blockState2.getBlock() == Blocks.log && blockState2.getValue(BlockPlanks.VARIANT) == net.minecraft.block.BlockPlanks.EnumType.JUNGLE) {
            if (side == EnumFacing.DOWN) {
               return false;
            }

            if (side == EnumFacing.UP) {
               return false;
            }

            pos = pos.offset(side);
            if (worldIn.isAirBlock(pos)) {
               IBlockState iBlockState = Blocks.cocoa.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
               worldIn.setBlockState(pos, iBlockState, 2);
               if (!playerIn.capabilities.isCreativeMode) {
                  stack.stackSize--;
               }
            }

            return true;
         }
      }

      return false;
   }
}
