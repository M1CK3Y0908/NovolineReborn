package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public static final PropertyDirection FACING = BlockPistonExtension.FACING;
   public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return null;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      TileEntityPiston tileEntity2 = this.getTileEntity(worldIn, pos);
      if (tileEntity2 != null) {
         Block block2 = tileEntity2.getPistonState().getBlock();
         if (block2 == this || block2.getMaterial() == Material.air) {
            return;
         }

         float progress2 = tileEntity2.getProgress(0.0F);
         if (tileEntity2.isExtending()) {
            progress2 = 1.0F - progress2;
         }

         block2.setBlockBoundsBasedOnState(worldIn, pos);
         if (block2 == Blocks.piston || block2 == Blocks.sticky_piston) {
            progress2 = 0.0F;
         }

         EnumFacing facing2 = tileEntity2.getFacing();
         this.minX = block2.getBlockBoundsMinX() - facing2.getFrontOffsetX() * progress2;
         this.minY = block2.getBlockBoundsMinY() - facing2.getFrontOffsetY() * progress2;
         this.minZ = block2.getBlockBoundsMinZ() - facing2.getFrontOffsetZ() * progress2;
         this.maxX = block2.getBlockBoundsMaxX() - facing2.getFrontOffsetX() * progress2;
         this.maxY = block2.getBlockBoundsMaxY() - facing2.getFrontOffsetY() * progress2;
         this.maxZ = block2.getBlockBoundsMaxZ() - facing2.getFrontOffsetZ() * progress2;
      }
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, TYPE);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityPiston) {
         ((TileEntityPiston)tileEntity).clearPistonTileEntity();
      } else {
         super.breakBlock(worldIn, pos, state);
      }
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      return null;
   }

   public static TileEntity newTileEntity(IBlockState state, EnumFacing facing, boolean extending, boolean renderHead) {
      return new TileEntityPiston(state, facing, extending, renderHead);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | state.getValue(FACING).getIndex();
      if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
         i |= 8;
      }

      return i;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return false;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         worldIn.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   public AxisAlignedBB getBoundingBox(World worldIn, BlockPos pos, IBlockState extendingBlock, float progress, EnumFacing direction) {
      if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air) {
         AxisAlignedBB axisAlignedBB = extendingBlock.getBlock().getCollisionBoundingBox(worldIn, pos, extendingBlock);
         if (axisAlignedBB == null) {
            return null;
         }

         double minX2 = axisAlignedBB.minX;
         double minY2 = axisAlignedBB.minY;
         double minZ2 = axisAlignedBB.minZ;
         double maxX2 = axisAlignedBB.maxX;
         double maxY2 = axisAlignedBB.maxY;
         double maxZ2 = axisAlignedBB.maxZ;
         if (direction.getFrontOffsetX() < 0) {
            minX2 -= direction.getFrontOffsetX() * progress;
         } else {
            maxX2 -= direction.getFrontOffsetX() * progress;
         }

         if (direction.getFrontOffsetY() < 0) {
            minY2 -= direction.getFrontOffsetY() * progress;
         } else {
            maxY2 -= direction.getFrontOffsetY() * progress;
         }

         if (direction.getFrontOffsetZ() < 0) {
            minZ2 -= direction.getFrontOffsetZ() * progress;
         } else {
            maxZ2 -= direction.getFrontOffsetZ() * progress;
         }

         return new AxisAlignedBB(minX2, minY2, minZ2, maxX2, maxY2, maxZ2);
      } else {
         return null;
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      TileEntityPiston tileEntityPiston = this.getTileEntity(worldIn, pos);
      if (tileEntityPiston == null) {
         return null;
      }

      float progress2 = tileEntityPiston.getProgress(0.0F);
      if (tileEntityPiston.isExtending()) {
         progress2 = 1.0F - progress2;
      }

      return this.getBoundingBox(worldIn, pos, tileEntityPiston.getPistonState(), progress2, tileEntityPiston.getFacing());
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      BlockPos blockPos = pos.offset(state.getValue(FACING).getOpposite());
      IBlockState blockState = worldIn.getBlockState(blockPos);
      if (blockState.getBlock() instanceof BlockPistonBase && blockState.getValue(BlockPistonBase.EXTENDED)) {
         worldIn.setBlockToAir(blockPos);
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         worldIn.getTileEntity(pos);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, BlockPistonExtension.getFacing(meta))
         .withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return false;
   }

   private TileEntityPiston getTileEntity(IBlockAccess worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntityPiston ? (TileEntityPiston)tileEntity : null;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         TileEntityPiston tileEntityPiston = this.getTileEntity(worldIn, pos);
         if (tileEntityPiston != null) {
            IBlockState blockState = tileEntityPiston.getPistonState();
            blockState.getBlock().dropBlockAsItem(worldIn, pos, blockState, 0);
         }
      }
   }

   public BlockPistonMoving() {
      super(Material.piston);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT)
      );
      this.setHardness(-1.0F);
   }
}
