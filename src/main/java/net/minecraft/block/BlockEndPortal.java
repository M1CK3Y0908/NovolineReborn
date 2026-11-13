package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEndPortal extends BlockContainer {
   @Override
   public MapColor getMapColor(IBlockState state) {
      return MapColor.blackColor;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   protected BlockEndPortal(Material materialIn) {
      super(materialIn);
      this.setLightLevel(1.0F);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityEndPortal();
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.0625F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.DOWN ? super.shouldSideBeRendered(worldIn, pos, side) : false;
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      double xCoordinate = pos.getX() + rand.nextFloat();
      double yCoordinate = pos.getY() + 0.8F;
      double zCoordinate = pos.getZ() + rand.nextFloat();
      double d = 0.0;
      double d2 = 0.0;
      double d3 = 0.0;
      worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate, yCoordinate, zCoordinate, d, d2, d3);
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null && !worldIn.isRemote) {
         entityIn.travelToDimension(1);
      }
   }
}
