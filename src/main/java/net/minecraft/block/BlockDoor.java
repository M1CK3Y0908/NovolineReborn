package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);
   public static final PropertyBool OPEN = PropertyBool.create("open");
   public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE = PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class);
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyEnum<BlockDoor.EnumDoorHalf> HALF = PropertyEnum.create("half", BlockDoor.EnumDoorHalf.class);

   public static EnumFacing getFacing(IBlockAccess worldIn, BlockPos pos) {
      return getFacing(combineMetadata(worldIn, pos));
   }

   private void setBoundBasedOnMeta(int combinedMeta) {
      float f = 0.1875F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      EnumFacing facing = getFacing(combinedMeta);
      boolean isOpen = isOpen(combinedMeta);
      boolean isHingeLeft = isHingeLeft(combinedMeta);
      if (isOpen) {
         if (facing == EnumFacing.EAST) {
            if (!isHingeLeft) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
         } else if (facing == EnumFacing.SOUTH) {
            if (!isHingeLeft) {
               this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
         } else if (facing == EnumFacing.WEST) {
            if (!isHingeLeft) {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
         } else if (facing == EnumFacing.NORTH) {
            if (!isHingeLeft) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      } else if (facing == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
      } else if (facing == EnumFacing.SOUTH) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
      } else if (facing == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else if (facing == EnumFacing.NORTH) {
         this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      BlockPos blockPos = pos.down();
      if (player.capabilities.isCreativeMode && state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER && worldIn.getBlockState(blockPos).getBlock() == this) {
         worldIn.setBlockToAir(blockPos);
      }
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (this.blockMaterial == Material.iron) {
         return true;
      }

      BlockPos blockPos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
      IBlockState blockState = pos.equals(blockPos) ? state : worldIn.getBlockState(blockPos);
      if (blockState.getBlock() != this) {
         return false;
      }

      state = blockState.cycleProperty(OPEN);
      worldIn.setBlockState(blockPos, state, 2);
      worldIn.markBlockRangeForRenderUpdate(blockPos, pos);
      worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN) ? 1003 : 1006, pos, 0);
      return true;
   }

   protected BlockDoor(Material materialIn) {
      super(materialIn);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(OPEN, false)
            .withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT)
            .withProperty(POWERED, false)
            .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
      );
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return pos.getY() >= 255
         ? false
         : World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
   }

   protected static boolean isTop(int meta) {
      return (meta & 8) != 0;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return (meta & 8) > 0
         ? this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER)
            .withProperty(HINGE, (meta & 1) > 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT)
            .withProperty(POWERED, (meta & 2) > 0)
         : this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
            .withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW())
            .withProperty(OPEN, (meta & 4) > 0);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER) {
         IBlockState blockState = worldIn.getBlockState(pos.up());
         if (blockState.getBlock() == this) {
            state = state.withProperty(HINGE, blockState.getValue(HINGE)).withProperty(POWERED, blockState.getValue(POWERED));
         }
      } else {
         IBlockState blockState2 = worldIn.getBlockState(pos.down());
         if (blockState2.getBlock() == this) {
            state = state.withProperty(FACING, blockState2.getValue(FACING)).withProperty(OPEN, blockState2.getValue(OPEN));
         }
      }

      return state;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public static boolean isOpen(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return this.getItem();
   }

   protected static boolean isOpen(int combinedMeta) {
      return (combinedMeta & 4) != 0;
   }

   protected static int removeHalfBit(int meta) {
      return meta & 7;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i;
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         i = 8;
         if (state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT) {
            i = 9;
         }

         if (state.getValue(POWERED)) {
            i |= 2;
         }
      } else {
         i = 0 | state.getValue(FACING).rotateY().getHorizontalIndex();
         if (state.getValue(OPEN)) {
            i |= 4;
         }
      }

      return i;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         BlockPos blockPos = pos.down();
         IBlockState blockState = worldIn.getBlockState(blockPos);
         if (blockState.getBlock() != this) {
            worldIn.setBlockToAir(pos);
         } else if (neighborBlock != this) {
            this.onNeighborBlockChange(worldIn, blockPos, blockState, neighborBlock);
         }
      } else {
         boolean conditionMet = false;
         BlockPos blockPos2 = pos.up();
         IBlockState blockState2 = worldIn.getBlockState(blockPos2);
         if (blockState2.getBlock() != this) {
            worldIn.setBlockToAir(pos);
            conditionMet = true;
         }

         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            worldIn.setBlockToAir(pos);
            conditionMet = true;
            if (blockState2.getBlock() == this) {
               worldIn.setBlockToAir(blockPos2);
            }
         }

         if (conditionMet) {
            if (!worldIn.isRemote) {
               this.dropBlockAsItem(worldIn, pos, state, 0);
            }
         } else {
            boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockPos2);
            if ((flag || neighborBlock.canProvidePower()) && neighborBlock != this && flag != blockState2.getValue(POWERED)) {
               worldIn.setBlockState(blockPos2, blockState2.withProperty(POWERED, flag), 2);
               if (flag != state.getValue(OPEN)) {
                  worldIn.setBlockState(pos, state.withProperty(OPEN, flag), 2);
                  worldIn.markBlockRangeForRenderUpdate(pos, pos);
                  worldIn.playAuxSFXAtEntity((EntityPlayer)null, flag ? 1003 : 1006, pos, 0);
               }
            }
         }
      }
   }

   protected static boolean isHingeLeft(int combinedMeta) {
      return (combinedMeta & 16) != 0;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.getItem();
   }

   public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this) {
         BlockPos blockPos = blockState.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
         IBlockState blockState2 = pos == blockPos ? blockState : worldIn.getBlockState(blockPos);
         if (blockState2.getBlock() == this && blockState2.getValue(OPEN) != open) {
            worldIn.setBlockState(blockPos, blockState2.withProperty(OPEN, open), 2);
            worldIn.markBlockRangeForRenderUpdate(blockPos, pos);
            worldIn.playAuxSFXAtEntity((EntityPlayer)null, open ? 1003 : 1006, pos, 0);
         }
      }
   }

   private Item getItem() {
      return this == Blocks.iron_door
         ? Items.iron_door
         : (
            this == Blocks.spruce_door
               ? Items.spruce_door
               : (
                  this == Blocks.birch_door
                     ? Items.birch_door
                     : (
                        this == Blocks.jungle_door
                           ? Items.jungle_door
                           : (this == Blocks.acacia_door ? Items.acacia_door : (this == Blocks.dark_oak_door ? Items.dark_oak_door : Items.oak_door))
                     )
               )
         );
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBoundBasedOnMeta(combineMetadata(worldIn, pos));
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   public static int combineMetadata(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      int metaFromState = blockState.getBlock().getMetaFromState(blockState);
      boolean isTop = isTop(metaFromState);
      IBlockState blockState2 = worldIn.getBlockState(pos.down());
      int metaFromState2 = blockState2.getBlock().getMetaFromState(blockState2);
      int i = isTop ? metaFromState2 : metaFromState;
      IBlockState blockState3 = worldIn.getBlockState(pos.up());
      int metaFromState3 = blockState3.getBlock().getMetaFromState(blockState3);
      int i2 = isTop ? metaFromState : metaFromState3;
      boolean flag = (i2 & 1) != 0;
      boolean flag2 = (i2 & 2) != 0;
      return removeHalfBit(i) | (isTop ? 8 : 0) | (flag ? 16 : 0) | (flag2 ? 32 : 0);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal((this.getUnlocalizedName() + ".name").replaceAll("tile", "item"));
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.collisionRayTrace(worldIn, pos, start, end);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, HALF, FACING, OPEN, HINGE, POWERED);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public static EnumFacing getFacing(int combinedMeta) {
      return EnumFacing.getHorizontal(combinedMeta & 3).rotateYCCW();
   }

   public enum EnumDoorHalf implements IStringSerializable {
      UPPER,
      LOWER;

      @Override
      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }

      @Override
      public String toString() {
         return this.getName();
      }
   }

   public enum EnumHingePosition implements IStringSerializable {
      LEFT,
      RIGHT;

      @Override
      public String getName() {
         return this == LEFT ? "left" : "right";
      }

      @Override
      public String toString() {
         return this.getName();
      }
   }
}
