package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool SUSPENDED = PropertyBool.create("suspended");
   public static final PropertyBool ATTACHED = PropertyBool.create("attached");
   public static final PropertyBool DISARMED = PropertyBool.create("disarmed");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(NORTH, isConnectedTo(worldIn, pos, state, EnumFacing.NORTH))
         .withProperty(EAST, isConnectedTo(worldIn, pos, state, EnumFacing.EAST))
         .withProperty(SOUTH, isConnectedTo(worldIn, pos, state, EnumFacing.SOUTH))
         .withProperty(WEST, isConnectedTo(worldIn, pos, state, EnumFacing.WEST));
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
         worldIn.setBlockState(pos, state.withProperty(DISARMED, true), 4);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean conditionMet = state.getValue(SUSPENDED);
      boolean flag = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
      if (conditionMet != flag) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   public BlockTripWire() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(POWERED, false)
            .withProperty(SUSPENDED, false)
            .withProperty(ATTACHED, false)
            .withProperty(DISARMED, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
      this.setTickRandomly(true);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.string;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      if (state.getValue(POWERED)) {
         i = 1;
      }

      if (state.getValue(SUSPENDED)) {
         i |= 2;
      }

      if (state.getValue(ATTACHED)) {
         i |= 4;
      }

      if (state.getValue(DISARMED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(POWERED, (meta & 1) > 0)
         .withProperty(SUSPENDED, (meta & 2) > 0)
         .withProperty(ATTACHED, (meta & 4) > 0)
         .withProperty(DISARMED, (meta & 8) > 0);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.notifyHook(worldIn, pos, state.withProperty(POWERED, true));
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   public static boolean isConnectedTo(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing direction) {
      BlockPos blockPos = pos.offset(direction);
      IBlockState blockState = worldIn.getBlockState(blockPos);
      Block block = blockState.getBlock();
      if (block == Blocks.tripwire_hook) {
         EnumFacing facing = direction.getOpposite();
         return blockState.getValue(BlockTripWireHook.FACING) == facing;
      } else if (block == Blocks.tripwire) {
         boolean conditionMet = state.getValue(SUSPENDED);
         boolean conditionMet2 = blockState.getValue(SUSPENDED);
         return conditionMet == conditionMet2;
      } else {
         return false;
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && worldIn.getBlockState(pos).getValue(POWERED)) {
         this.updateState(worldIn, pos);
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState2 = worldIn.getBlockState(pos);
      boolean value = blockState2.getValue(ATTACHED);
      if (!blockState2.getValue(SUSPENDED)) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
      } else if (!value) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
      }
   }

   private void notifyHook(World worldIn, BlockPos pos, IBlockState state) {
      for (EnumFacing enumFacing : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST}) {
         for (int i = 1; i < 42; i++) {
            BlockPos blockPos2 = pos.offset(enumFacing, i);
            IBlockState blockState2 = worldIn.getBlockState(blockPos2);
            if (blockState2.getBlock() == Blocks.tripwire_hook) {
               if (blockState2.getValue(BlockTripWireHook.FACING) == enumFacing.getOpposite()) {
                  Blocks.tripwire_hook.updateTripWireState(worldIn, blockPos2, blockState2, false, true, i, state);
               }
               break;
            }

            if (blockState2.getBlock() != Blocks.tripwire) {
               break;
            }
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote && !state.getValue(POWERED)) {
         this.updateState(worldIn, pos);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   private void updateState(World worldIn, BlockPos pos) {
      IBlockState blockState2 = worldIn.getBlockState(pos);
      boolean value = blockState2.getValue(POWERED);
      boolean flag = false;
      List entitiesWithinAABBExcludingEntity = worldIn.getEntitiesWithinAABBExcludingEntity(
         (Entity)null,
         new AxisAlignedBB(
            pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ
         )
      );
      if (!entitiesWithinAABBExcludingEntity.isEmpty()) {
         Iterator iterator2 = entitiesWithinAABBExcludingEntity.iterator();

         while (iterator2.hasNext()) {
            if (!((Entity)iterator2.next()).doesEntityNotTriggerPressurePlate()) {
               flag = true;
               break;
            }
         }
      }

      if (flag != value) {
         blockState2 = blockState2.withProperty(POWERED, flag);
         worldIn.setBlockState(pos, blockState2, 3);
         this.notifyHook(worldIn, pos, blockState2);
      }

      if (flag) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      state = state.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()));
      worldIn.setBlockState(pos, state, 3);
      this.notifyHook(worldIn, pos, state);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.string;
   }
}
