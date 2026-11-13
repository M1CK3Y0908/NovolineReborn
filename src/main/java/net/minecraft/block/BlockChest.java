package net.minecraft.block;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);
   public final int chestType;

   private boolean isBelowSolidBlock(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
   }

   @Override
   public int getRenderType() {
      return 2;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityChest();
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkForSurroundingChests(worldIn, pos, state);

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos5 = pos.offset(enumFacing);
         IBlockState blockState2 = worldIn.getBlockState(blockPos5);
         if (blockState2.getBlock() == this) {
            this.checkForSurroundingChests(worldIn, blockPos5, blockState2);
         }
      }
   }

   @Override
   public boolean canProvidePower() {
      return this.chestType == 1;
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      if (!this.canProvidePower()) {
         return 0;
      }

      int i = 0;
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityChest) {
         i = ((TileEntityChest)tileEntity).numPlayersUsing;
      }

      return MathHelper.clamp_int(i, 0, 15);
   }

   public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (!(tileEntity instanceof TileEntityChest)) {
         return null;
      }

      ILockableContainer lockableContainer = (TileEntityChest)tileEntity;
      if (this.isBlocked(worldIn, pos)) {
         return null;
      }

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos5 = pos.offset(enumFacing);
         if (worldIn.getBlockState(blockPos5).getBlock() == this) {
            if (this.isBlocked(worldIn, blockPos5)) {
               return null;
            }

            TileEntity tileEntity2 = worldIn.getTileEntity(blockPos5);
            if (tileEntity2 instanceof TileEntityChest) {
               if (enumFacing != EnumFacing.WEST && enumFacing != EnumFacing.NORTH) {
                  lockableContainer = new InventoryLargeChest("container.chestDouble", lockableContainer, (TileEntityChest)tileEntity2);
               } else {
                  lockableContainer = new InventoryLargeChest("container.chestDouble", (TileEntityChest)tileEntity2, lockableContainer);
               }
            }
         }
      }

      return lockableContainer;
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof IInventory) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
         worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      int i = 0;
      BlockPos blockPos = pos.west();
      BlockPos blockPos2 = pos.east();
      BlockPos blockPos3 = pos.north();
      BlockPos blockPos4 = pos.south();
      if (worldIn.getBlockState(blockPos).getBlock() == this) {
         if (this.isDoubleChest(worldIn, blockPos)) {
            return false;
         }

         i++;
      }

      if (worldIn.getBlockState(blockPos2).getBlock() == this) {
         if (this.isDoubleChest(worldIn, blockPos2)) {
            return false;
         }

         i++;
      }

      if (worldIn.getBlockState(blockPos3).getBlock() == this) {
         if (this.isDoubleChest(worldIn, blockPos3)) {
            return false;
         }

         i++;
      }

      if (worldIn.getBlockState(blockPos4).getBlock() == this) {
         if (this.isDoubleChest(worldIn, blockPos4)) {
            return false;
         }

         i++;
      }

      return i <= 1;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      EnumFacing facing = EnumFacing.getHorizontal(MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3).getOpposite();
      state = state.withProperty(FACING, facing);
      BlockPos blockPos = pos.north();
      BlockPos blockPos2 = pos.south();
      BlockPos blockPos3 = pos.west();
      BlockPos blockPos4 = pos.east();
      boolean flag = this == worldIn.getBlockState(blockPos).getBlock();
      boolean flag2 = this == worldIn.getBlockState(blockPos2).getBlock();
      boolean flag3 = this == worldIn.getBlockState(blockPos3).getBlock();
      boolean flag4 = this == worldIn.getBlockState(blockPos4).getBlock();
      if (!flag && !flag2 && !flag3 && !flag4) {
         worldIn.setBlockState(pos, state, 3);
      } else if (facing.getAxis() != net.minecraft.util.EnumFacing.Axis.X || !flag && !flag2) {
         if (facing.getAxis() == net.minecraft.util.EnumFacing.Axis.Z && (flag3 || flag4)) {
            if (flag3) {
               worldIn.setBlockState(blockPos3, state, 3);
            } else {
               worldIn.setBlockState(blockPos4, state, 3);
            }

            worldIn.setBlockState(pos, state, 3);
         }
      } else {
         if (flag) {
            worldIn.setBlockState(blockPos, state, 3);
         } else {
            worldIn.setBlockState(blockPos2, state, 3);
         }

         worldIn.setBlockState(pos, state, 3);
      }

      if (stack.hasDisplayName()) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityChest) {
            ((TileEntityChest)tileEntity).setCustomName(stack.getDisplayName());
         }
      }
   }

   public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state) {
      if (worldIn.isRemote) {
         return state;
      }

      IBlockState blockState2 = worldIn.getBlockState(pos.north());
      IBlockState blockState3 = worldIn.getBlockState(pos.south());
      IBlockState blockState4 = worldIn.getBlockState(pos.west());
      IBlockState blockState5 = worldIn.getBlockState(pos.east());
      EnumFacing value = state.getValue(FACING);
      Block block11 = blockState2.getBlock();
      Block block12 = blockState3.getBlock();
      Block block2 = blockState4.getBlock();
      Block block3 = blockState5.getBlock();
      if (block11 != this && block12 != this) {
         boolean fullBlock = block11.isFullBlock();
         boolean fullBlock2 = block12.isFullBlock();
         if (block2 == this || block3 == this) {
            BlockPos blockPos5 = block2 == this ? pos.west() : pos.east();
            IBlockState blockState6 = worldIn.getBlockState(blockPos5.north());
            IBlockState blockState7 = worldIn.getBlockState(blockPos5.south());
            value = EnumFacing.SOUTH;
            EnumFacing enumFacing;
            if (block2 == this) {
               enumFacing = blockState4.getValue(FACING);
            } else {
               enumFacing = blockState5.getValue(FACING);
            }

            if (enumFacing == EnumFacing.NORTH) {
               value = EnumFacing.NORTH;
            }

            Block block4 = blockState6.getBlock();
            Block block5 = blockState7.getBlock();
            if ((fullBlock || block4.isFullBlock()) && !fullBlock2 && !block5.isFullBlock()) {
               value = EnumFacing.SOUTH;
            }

            if ((fullBlock2 || block5.isFullBlock()) && !fullBlock && !block4.isFullBlock()) {
               value = EnumFacing.NORTH;
            }
         }
      } else {
         BlockPos blockPos6 = block11 == this ? pos.north() : pos.south();
         IBlockState blockState8 = worldIn.getBlockState(blockPos6.west());
         IBlockState blockState9 = worldIn.getBlockState(blockPos6.east());
         value = EnumFacing.EAST;
         EnumFacing enumFacing2;
         if (block11 == this) {
            enumFacing2 = blockState2.getValue(FACING);
         } else {
            enumFacing2 = blockState3.getValue(FACING);
         }

         if (enumFacing2 == EnumFacing.WEST) {
            value = EnumFacing.WEST;
         }

         Block block6 = blockState8.getBlock();
         Block block7 = blockState9.getBlock();
         if ((block2.isFullBlock() || block6.isFullBlock()) && !block3.isFullBlock() && !block7.isFullBlock()) {
            value = EnumFacing.EAST;
         }

         if ((block3.isFullBlock() || block7.isFullBlock()) && !block2.isFullBlock() && !block6.isFullBlock()) {
            value = EnumFacing.WEST;
         }
      }

      state = state.withProperty(FACING, value);
      worldIn.setBlockState(pos, state, 3);
      return state;
   }

   protected BlockChest(int type) {
      super(Material.wood);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.chestType = type;
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
   }

   public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumFacing = null;

      for (EnumFacing enumFacing2 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         IBlockState blockState2 = worldIn.getBlockState(pos.offset(enumFacing2));
         if (blockState2.getBlock() == this) {
            return state;
         }

         if (blockState2.getBlock().isFullBlock()) {
            if (enumFacing != null) {
               enumFacing = null;
               break;
            }

            enumFacing = enumFacing2;
         }
      }

      if (enumFacing != null) {
         return state.withProperty(FACING, enumFacing.getOpposite());
      }

      EnumFacing value = state.getValue(FACING);
      if (worldIn.getBlockState(pos.offset(value)).getBlock().isFullBlock()) {
         value = value.getOpposite();
      }

      if (worldIn.getBlockState(pos.offset(value)).getBlock().isFullBlock()) {
         value = value.rotateY();
      }

      if (worldIn.getBlockState(pos.offset(value)).getBlock().isFullBlock()) {
         value = value.getOpposite();
      }

      return state.withProperty(FACING, value);
   }

   private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos) {
      Iterator iterator2 = worldIn.getEntitiesWithinAABB(
            EntityOcelot.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)
         )
         .iterator();

      while (iterator2.hasNext()) {
         if (((EntityOcelot)((Entity)iterator2.next())).isSitting()) {
            return true;
         }
      }

      return false;
   }

   private boolean isDoubleChest(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() != this) {
         return false;
      }

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock() == this) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos.north()).getBlock() == this) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
      } else if (worldIn.getBlockState(pos.south()).getBlock() == this) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
      } else if (worldIn.getBlockState(pos.west()).getBlock() == this) {
         this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      } else if (worldIn.getBlockState(pos.east()).getBlock() == this) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
      } else {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      }
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return side == EnumFacing.UP ? this.getWeakPower(worldIn, pos, state, side) : 0;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      ILockableContainer lockableContainer = this.getLockableContainer(worldIn, pos);
      if (lockableContainer != null) {
         playerIn.displayGUIChest(lockableContainer);
         if (this.chestType == 0) {
            playerIn.triggerAchievement(StatList.chestOpenedStat);
         } else if (this.chestType == 1) {
            playerIn.triggerAchievement(StatList.trappedChestTriggeredStat);
         }
      }

      return true;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getIndex();
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityChest) {
         tileEntity.updateContainingBlockInfo();
      }
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
   public boolean hasComparatorInputOverride() {
      return true;
   }

   private boolean isBlocked(World worldIn, BlockPos pos) {
      return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
   }
}
