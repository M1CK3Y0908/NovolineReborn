package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      public boolean apply(EnumFacing arg1) {
         return arg1 != EnumFacing.DOWN;
      }
   });

   protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, state.getValue(FACING))) {
         return true;
      }

      if (worldIn.getBlockState(pos).getBlock() == this) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      return false;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkForDrop(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState defaultState = this.getDefaultState();

      return switch (meta) {
         case 1 -> defaultState.withProperty(FACING, EnumFacing.EAST);
         case 2 -> defaultState.withProperty(FACING, EnumFacing.WEST);
         case 3 -> defaultState.withProperty(FACING, EnumFacing.SOUTH);
         case 4 -> defaultState.withProperty(FACING, EnumFacing.NORTH);
         default -> defaultState.withProperty(FACING, EnumFacing.UP);
      };
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return switch ((EnumFacing)state.getValue(FACING)) {
         case EAST -> 1;
         case WEST -> 2;
         case SOUTH -> 3;
         case NORTH -> 4;
         default -> 5;
      };
   }

   private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
      BlockPos blockPos = pos.offset(facing.getOpposite());
      return facing.getAxis().isHorizontal() && worldIn.isBlockNormalCube(blockPos, true) || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockPos);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (this.canPlaceAt(worldIn, pos, facing)) {
         return this.getDefaultState().withProperty(FACING, facing);
      }

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         if (worldIn.isBlockNormalCube(pos.offset(enumFacing.getOpposite()), true)) {
            return this.getDefaultState().withProperty(FACING, enumFacing);
         }
      }

      return this.getDefaultState();
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : FACING.getAllowedValues()) {
         if (this.canPlaceAt(worldIn, pos, enumFacing)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   protected BlockTorch() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      EnumFacing facing = state.getValue(FACING);
      double xCoordinate = pos.getX() + 0.5;
      double yCoordinate = pos.getY() + 0.7;
      double zCoordinate = pos.getZ() + 0.5;
      double d = 0.22;
      double d2 = 0.27;
      if (facing.getAxis().isHorizontal()) {
         EnumFacing facing2 = facing.getOpposite();
         worldIn.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL,
            xCoordinate + d2 * facing2.getFrontOffsetX(),
            yCoordinate + d,
            zCoordinate + d2 * facing2.getFrontOffsetZ(),
            0.0,
            0.0,
            0.0
         );
         worldIn.spawnParticle(
            EnumParticleTypes.FLAME,
            xCoordinate + d2 * facing2.getFrontOffsetX(),
            yCoordinate + d,
            zCoordinate + d2 * facing2.getFrontOffsetZ(),
            0.0,
            0.0,
            0.0
         );
      } else {
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate, yCoordinate, zCoordinate, 0.0, 0.0, 0.0);
         worldIn.spawnParticle(EnumParticleTypes.FLAME, xCoordinate, yCoordinate, zCoordinate, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.onNeighborChangeInternal(worldIn, pos, state);
   }

   protected boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.checkForDrop(worldIn, pos, state)) {
         return true;
      }

      EnumFacing facing = state.getValue(FACING);
      net.minecraft.util.EnumFacing.Axis enumfacing$axis = facing.getAxis();
      EnumFacing facing2 = facing.getOpposite();
      boolean conditionMet = false;
      if (enumfacing$axis.isHorizontal() && !worldIn.isBlockNormalCube(pos.offset(facing2), true)) {
         conditionMet = true;
      } else if (enumfacing$axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(facing2))) {
         conditionMet = true;
      }

      if (conditionMet) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      EnumFacing value = worldIn.getBlockState(pos).getValue(FACING);
      float f = 0.15F;
      if (value == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
      } else if (value == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
      } else if (value == EnumFacing.SOUTH) {
         this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
      } else if (value == EnumFacing.NORTH) {
         this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      } else {
         f = 0.1F;
         this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
      }

      return super.collisionRayTrace(worldIn, pos, start, end);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   private boolean canPlaceOn(World worldIn, BlockPos pos) {
      if (World.doesBlockHaveSolidTopSurface(worldIn, pos)) {
         return true;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      return block instanceof BlockFence || block == Blocks.glass || block == Blocks.cobblestone_wall || block == Blocks.stained_glass;
   }
}
