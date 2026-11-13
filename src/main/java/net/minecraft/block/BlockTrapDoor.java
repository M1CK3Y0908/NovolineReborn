package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);
   public static final PropertyBool OPEN = PropertyBool.create("open");
   public static final PropertyEnum<BlockTrapDoor.DoorHalf> HALF = PropertyEnum.create("half", BlockTrapDoor.DoorHalf.class);

   private static boolean isValidSupportBlock(Block blockIn) {
      return blockIn.blockMaterial.isOpaque() && blockIn.isFullCube()
         || blockIn == Blocks.glowstone
         || blockIn instanceof BlockSlab
         || blockIn instanceof BlockStairs;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBounds(worldIn.getBlockState(pos));
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   protected static EnumFacing getFacing(int meta) {
      switch (meta & 3) {
         case 0:
            return EnumFacing.NORTH;
         case 1:
            return EnumFacing.SOUTH;
         case 2:
            return EnumFacing.WEST;
         case 3:
         default:
            return EnumFacing.EAST;
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float f = 0.1875F;
      this.setBlockBounds(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState defaultState = this.getDefaultState();
      if (facing.getAxis().isHorizontal()) {
         defaultState = defaultState.withProperty(FACING, facing).withProperty(OPEN, false);
         defaultState = defaultState.withProperty(HALF, hitY > 0.5F ? BlockTrapDoor.DoorHalf.TOP : BlockTrapDoor.DoorHalf.BOTTOM);
      }

      return defaultState;
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.collisionRayTrace(worldIn, pos, start, end);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return !side.getAxis().isVertical() && isValidSupportBlock(worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock());
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | getMetaForFacing(state.getValue(FACING));
      if (state.getValue(OPEN)) {
         i |= 4;
      }

      if (state.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   protected BlockTrapDoor(Material materialIn) {
      super(materialIn);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, false).withProperty(HALF, BlockTrapDoor.DoorHalf.BOTTOM)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   protected static int getMetaForFacing(EnumFacing facing) {
      switch (facing) {
         case NORTH:
            return 0;
         case SOUTH:
            return 1;
         case WEST:
            return 2;
         case EAST:
         default:
            return 3;
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, getFacing(meta))
         .withProperty(OPEN, (meta & 4) != 0)
         .withProperty(HALF, (meta & 8) == 0 ? BlockTrapDoor.DoorHalf.BOTTOM : BlockTrapDoor.DoorHalf.TOP);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         BlockPos blockPos = pos.offset(state.getValue(FACING).getOpposite());
         if (!isValidSupportBlock(worldIn.getBlockState(blockPos).getBlock())) {
            worldIn.setBlockToAir(pos);
            this.dropBlockAsItem(worldIn, pos, state, 0);
         } else {
            boolean blockPowered = worldIn.isBlockPowered(pos);
            if ((blockPowered || neighborBlock.canProvidePower()) && state.getValue(OPEN) != blockPowered) {
               worldIn.setBlockState(pos, state.withProperty(OPEN, blockPowered), 2);
               worldIn.playAuxSFXAtEntity((EntityPlayer)null, blockPowered ? 1003 : 1006, pos, 0);
            }
         }
      }
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return !worldIn.getBlockState(pos).getValue(OPEN);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   public void setBounds(IBlockState state) {
      if (state.getBlock() == this) {
         boolean flag = state.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP;
         Boolean value = state.getValue(OPEN);
         EnumFacing value2 = state.getValue(FACING);
         if (flag) {
            this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
         } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
         }

         if (value) {
            if (value2 == EnumFacing.NORTH) {
               this.setBlockBounds(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
            }

            if (value2 == EnumFacing.SOUTH) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
            }

            if (value2 == EnumFacing.WEST) {
               this.setBlockBounds(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if (value2 == EnumFacing.EAST) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
            }
         }
      }
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, OPEN, HALF);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (this.blockMaterial == Material.iron) {
         return true;
      }

      state = state.cycleProperty(OPEN);
      worldIn.setBlockState(pos, state, 2);
      worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN) ? 1003 : 1006, pos, 0);
      return true;
   }

   public enum DoorHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      @Override
      public String toString() {
         return this.name;
      }

      DoorHalf(String text) {
         this.name = text;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
