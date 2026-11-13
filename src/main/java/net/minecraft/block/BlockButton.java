package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockButton extends Block {
   private final boolean wooden;
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   public static final PropertyBool POWERED = PropertyBool.create("powered");

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote && this.wooden && !state.getValue(POWERED)) {
         this.checkForArrows(worldIn, pos, state);
      }
   }

   private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing) {
      worldIn.notifyNeighborsOfStateChange(pos, this);
      worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (state.getValue(POWERED)) {
         return true;
      }

      worldIn.setBlockState(pos, state.withProperty(POWERED, true), 3);
      worldIn.markBlockRangeForRenderUpdate(pos, pos);
      worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      return true;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return canBePlacedOn(worldIn, pos, facing.getOpposite())
         ? this.getDefaultState().withProperty(FACING, facing).withProperty(POWERED, false)
         : this.getDefaultState().withProperty(FACING, EnumFacing.DOWN).withProperty(POWERED, false);
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   protected static boolean canBePlacedOn(World worldIn, BlockPos pos, EnumFacing side) {
      BlockPos blockPos = pos.offset(side);
      return side == EnumFacing.DOWN ? World.doesBlockHaveSolidTopSurface(worldIn, blockPos) : worldIn.getBlockState(blockPos).getBlock().isNormalCube();
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && state.getValue(POWERED)) {
         if (this.wooden) {
            this.checkForArrows(worldIn, pos, state);
         } else {
            worldIn.setBlockState(pos, state.withProperty(POWERED, false));
            this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
            worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
         }
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float f = 0.1875F;
      float f2 = 0.125F;
      float f3 = 0.125F;
      this.setBlockBounds(0.5F - f, 0.5F - f2, 0.5F - f3, 0.5F + f, 0.5F + f2, 0.5F + f3);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getValue(POWERED)) {
         this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.updateBlockBounds(worldIn.getBlockState(pos));
   }

   private void updateBlockBounds(IBlockState state) {
      EnumFacing value = state.getValue(FACING);
      float f = (state.getValue(POWERED) ? 1 : 2) / 16.0F;
      switch (value) {
         case EAST:
            this.setBlockBounds(0.0F, 0.375F, 0.3125F, f, 0.625F, 0.6875F);
            break;
         case WEST:
            this.setBlockBounds(1.0F - f, 0.375F, 0.3125F, 1.0F, 0.625F, 0.6875F);
            break;
         case SOUTH:
            this.setBlockBounds(0.3125F, 0.375F, 0.0F, 0.6875F, 0.625F, f);
            break;
         case NORTH:
            this.setBlockBounds(0.3125F, 0.375F, 1.0F - f, 0.6875F, 0.625F, 1.0F);
            break;
         case UP:
            this.setBlockBounds(0.3125F, 0.0F, 0.375F, 0.6875F, 0.0F + f, 0.625F);
            break;
         case DOWN:
            this.setBlockBounds(0.3125F, 1.0F - f, 0.375F, 0.6875F, 1.0F, 0.625F);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (this.checkForDrop(worldIn, pos, state) && !canBePlacedOn(worldIn, pos, state.getValue(FACING).getOpposite())) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   private void checkForArrows(World worldIn, BlockPos pos, IBlockState state) {
      this.updateBlockBounds(state);
      boolean flag = !worldIn.getEntitiesWithinAABB(
            EntityArrow.class,
            new AxisAlignedBB(
               pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ
            )
         )
         .isEmpty();
      boolean value = state.getValue(POWERED);
      if (flag && !value) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, true));
         this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (!flag && value) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, false));
         this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      }

      if (flag) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return canBePlacedOn(worldIn, pos, side.getOpposite());
   }

   private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (this.canPlaceBlockAt(worldIn, pos)) {
         return true;
      }

      this.dropBlockAsItem(worldIn, pos, state, 0);
      worldIn.setBlockToAir(pos);
      return false;
   }

   protected BlockButton(boolean wooden) {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.wooden = wooden;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (canBePlacedOn(worldIn, pos, enumFacing)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = switch ((EnumFacing)state.getValue(FACING)) {
         case EAST -> 1;
         case WEST -> 2;
         case SOUTH -> 3;
         case NORTH -> 4;
         default -> 5;
         case DOWN -> 0;
      };
      if (state.getValue(POWERED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public int tickRate(World worldIn) {
      return this.wooden ? 30 : 20;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, switch (meta & 7) {
         case 0 -> EnumFacing.DOWN;
         case 1 -> EnumFacing.EAST;
         case 2 -> EnumFacing.WEST;
         case 3 -> EnumFacing.SOUTH;
         case 4 -> EnumFacing.NORTH;
         default -> EnumFacing.UP;
      }).withProperty(POWERED, (meta & 8) > 0);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, POWERED);
   }
}
