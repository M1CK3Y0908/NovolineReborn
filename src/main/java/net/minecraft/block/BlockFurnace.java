package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {
   private static boolean keepInventory;
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);
   private final boolean isBurning;

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.furnace);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing facing = EnumFacing.getFront(meta);
      if (facing.getAxis() == net.minecraft.util.EnumFacing.Axis.Y) {
         facing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, facing);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityFurnace) {
         playerIn.displayGUIChest((TileEntityFurnace)tileEntity);
         playerIn.triggerAchievement(StatList.furnaceInteractionStat);
      }

      return true;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityFurnace();
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
      if (stack.hasDisplayName()) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityFurnace) {
            ((TileEntityFurnace)tileEntity).setCustomInventoryName(stack.getDisplayName());
         }
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.isBurning) {
         EnumFacing facing = state.getValue(FACING);
         double xCoordinate = pos.getX() + 0.5;
         double yCoordinate = pos.getY() + rand.nextDouble() * 6.0 / 16.0;
         double zCoordinate = pos.getZ() + 0.5;
         double d = 0.52;
         double d2 = rand.nextDouble() * 0.6 - 0.3;
         switch (facing) {
            case WEST:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate - d, yCoordinate, zCoordinate + d2, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, xCoordinate - d, yCoordinate, zCoordinate + d2, 0.0, 0.0, 0.0);
               break;
            case EAST:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate + d, yCoordinate, zCoordinate + d2, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, xCoordinate + d, yCoordinate, zCoordinate + d2, 0.0, 0.0, 0.0);
               break;
            case NORTH:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate + d2, yCoordinate, zCoordinate - d, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, xCoordinate + d2, yCoordinate, zCoordinate - d, 0.0, 0.0, 0.0);
               break;
            case SOUTH:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate + d2, yCoordinate, zCoordinate + d, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, xCoordinate + d2, yCoordinate, zCoordinate + d, 0.0, 0.0, 0.0);
         }
      }
   }

   private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         Block block = worldIn.getBlockState(pos.north()).getBlock();
         Block block4 = worldIn.getBlockState(pos.south()).getBlock();
         Block block5 = worldIn.getBlockState(pos.west()).getBlock();
         Block block6 = worldIn.getBlockState(pos.east()).getBlock();
         EnumFacing facing = state.getValue(FACING);
         if (facing == EnumFacing.NORTH && block.isFullBlock() && !block4.isFullBlock()) {
            facing = EnumFacing.SOUTH;
         } else if (facing == EnumFacing.SOUTH && block4.isFullBlock() && !block.isFullBlock()) {
            facing = EnumFacing.NORTH;
         } else if (facing == EnumFacing.WEST && block5.isFullBlock() && !block6.isFullBlock()) {
            facing = EnumFacing.EAST;
         } else if (facing == EnumFacing.EAST && block6.isFullBlock() && !block5.isFullBlock()) {
            facing = EnumFacing.WEST;
         }

         worldIn.setBlockState(pos, state.withProperty(FACING, facing), 2);
      }
   }

   protected BlockFurnace(boolean isBurning) {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.isBurning = isBurning;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.furnace);
   }

   public static void setState(boolean active, World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      keepInventory = true;
      if (active) {
         worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
         worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
      } else {
         worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
         worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
      }

      keepInventory = false;
      if (tileEntity != null) {
         tileEntity.validate();
         worldIn.setTileEntity(pos, tileEntity);
      }
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getIndex();
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.setDefaultFacing(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState state) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!keepInventory) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityFurnace) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityFurnace)tileEntity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }
      }

      super.breakBlock(worldIn, pos, state);
   }
}
