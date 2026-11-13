package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {
   private final boolean isOn;

   public BlockRedstoneOre(boolean isOn) {
      super(Material.rock);
      if (isOn) {
         this.setTickRandomly(true);
      }

      this.isOn = isOn;
   }

   @Override
   public int quantityDropped(Random random) {
      return 4 + random.nextInt(2);
   }

   @Override
   protected ItemStack createStackedBlock(IBlockState state) {
      return new ItemStack(Blocks.redstone_ore);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      this.activate(worldIn, pos);
      return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
   }

   @Override
   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      this.activate(worldIn, pos);
      super.onBlockClicked(worldIn, pos, playerIn);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this == Blocks.lit_redstone_ore) {
         worldIn.setBlockState(pos, Blocks.redstone_ore.getDefaultState());
      }
   }

   private void spawnParticles(World worldIn, BlockPos pos) {
      Random random = worldIn.rand;
      double d = 0.0625;

      for (int index = 0; index < 6; index++) {
         double xCoordinate = pos.getX() + random.nextFloat();
         double yCoordinate = pos.getY() + random.nextFloat();
         double zCoordinate = pos.getZ() + random.nextFloat();
         if (index == 0 && !worldIn.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
            yCoordinate = pos.getY() + d + 1.0;
         }

         if (index == 1 && !worldIn.getBlockState(pos.down()).getBlock().isOpaqueCube()) {
            yCoordinate = pos.getY() - d;
         }

         if (index == 2 && !worldIn.getBlockState(pos.south()).getBlock().isOpaqueCube()) {
            zCoordinate = pos.getZ() + d + 1.0;
         }

         if (index == 3 && !worldIn.getBlockState(pos.north()).getBlock().isOpaqueCube()) {
            zCoordinate = pos.getZ() - d;
         }

         if (index == 4 && !worldIn.getBlockState(pos.east()).getBlock().isOpaqueCube()) {
            xCoordinate = pos.getX() + d + 1.0;
         }

         if (index == 5 && !worldIn.getBlockState(pos.west()).getBlock().isOpaqueCube()) {
            xCoordinate = pos.getX() - d;
         }

         if (xCoordinate < pos.getX()
            || xCoordinate > pos.getX() + 1
            || yCoordinate < 0.0
            || yCoordinate > pos.getY() + 1
            || zCoordinate < pos.getZ()
            || zCoordinate > pos.getZ() + 1) {
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, xCoordinate, yCoordinate, zCoordinate, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.redstone;
   }

   private void activate(World worldIn, BlockPos pos) {
      this.spawnParticles(worldIn, pos);
      if (this == Blocks.redstone_ore) {
         worldIn.setBlockState(pos, Blocks.lit_redstone_ore.getDefaultState());
      }
   }

   @Override
   public int quantityDroppedWithBonus(int fortune, Random random) {
      return this.quantityDropped(random) + random.nextInt(fortune + 1);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.isOn) {
         this.spawnParticles(worldIn, pos);
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
      this.activate(worldIn, pos);
      super.onEntityCollidedWithBlock(worldIn, pos, entityIn);
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
      if (this.getItemDropped(state, worldIn.rand, fortune) != Item.getItemFromBlock(this)) {
         int i = 1 + worldIn.rand.nextInt(5);
         this.dropXpOnBlockBreak(worldIn, pos, i);
      }
   }
}
