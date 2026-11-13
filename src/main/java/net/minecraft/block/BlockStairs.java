package net.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);
   public static final PropertyEnum<BlockStairs.EnumHalf> HALF = PropertyEnum.create("half", BlockStairs.EnumHalf.class);
   private int rayTracePass;
   private final Block modelBlock;
   public static final PropertyEnum<BlockStairs.EnumShape> SHAPE = PropertyEnum.create("shape", BlockStairs.EnumShape.class);
   private final IBlockState modelState;
   private static final int[][] cornerIndexLookup = new int[][]{{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
   private boolean hasRaytraced;

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.modelBlock.breakBlock(worldIn, pos, this.modelState);
   }

   @Override
   public void updateTick(World world2, BlockPos blockPos, IBlockState iBlockState, Random random) {
      this.modelBlock.updateTick(world2, blockPos, iBlockState, random);
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
      this.modelBlock.onEntityCollidedWithBlock(worldIn, pos, entityIn);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (this.setPrimaryCollisionBounds(worldIn, pos)) {
         switch (this.getInnerShape(worldIn, pos)) {
            case 0:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
               break;
            case 1:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_RIGHT);
               break;
            case 2:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_LEFT);
         }
      } else {
         switch (this.getOuterShape(worldIn, pos)) {
            case 0:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
               break;
            case 1:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
               break;
            case 2:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
         }
      }

      return state;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      if (this.hasRaytraced) {
         this.setBlockBounds(
            0.5F * (this.rayTracePass % 2),
            0.5F * (this.rayTracePass / 4 % 2),
            0.5F * (this.rayTracePass / 2 % 2),
            0.5F + 0.5F * (this.rayTracePass % 2),
            0.5F + 0.5F * (this.rayTracePass / 4 % 2),
            0.5F + 0.5F * (this.rayTracePass / 2 % 2)
         );
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState iBlockState = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
      iBlockState = iBlockState.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
      return facing == EnumFacing.DOWN || facing != EnumFacing.UP && !(hitY <= 0.5)
         ? iBlockState.withProperty(HALF, BlockStairs.EnumHalf.TOP)
         : iBlockState.withProperty(HALF, BlockStairs.EnumHalf.BOTTOM);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   protected BlockStairs(IBlockState modelState) {
      super(modelState.getBlock().blockMaterial);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(HALF, BlockStairs.EnumHalf.BOTTOM)
            .withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT)
      );
      this.modelBlock = modelState.getBlock();
      this.modelState = modelState;
      this.setHardness(this.modelBlock.blockHardness);
      this.setResistance(this.modelBlock.blockResistance / 3.0F);
      this.setStepSound(this.modelBlock.stepSound);
      this.setLightOpacity(255);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isCollidable() {
      return this.modelBlock.isCollidable();
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.onNeighborBlockChange(worldIn, pos, this.modelState, Blocks.air);
      this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
   }

   public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getValue(HALF) == BlockStairs.EnumHalf.TOP) {
         this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBaseCollisionBounds(worldIn, pos);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      boolean conditionMet = this.setPrimaryCollisionBounds(worldIn, pos);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      if (conditionMet && this.setCornerCollisionBounds(worldIn, pos)) {
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }

      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(HALF, (meta & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM)
         .withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
   }

   public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      IBlockState blockState2 = worldIn.getBlockState(pos);
      return isBlockStairs(blockState2.getBlock()) && blockState2.getValue(HALF) == state.getValue(HALF) && blockState2.getValue(FACING) == state.getValue(FACING);
   }

   public int getOuterShape(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState blockState2 = blockAccess.getBlockState(pos);
      EnumFacing value = blockState2.getValue(FACING);
      BlockStairs.EnumHalf value2 = blockState2.getValue(HALF);
      boolean flag = value2 == BlockStairs.EnumHalf.TOP;
      if (value == EnumFacing.EAST) {
         IBlockState blockState3 = blockAccess.getBlockState(pos.east());
         if (isBlockStairs(blockState3.getBlock()) && value2 == blockState3.getValue(HALF)) {
            EnumFacing value3 = blockState3.getValue(FACING);
            if (value3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               return flag ? 1 : 2;
            }

            if (value3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               return flag ? 2 : 1;
            }
         }
      } else if (value == EnumFacing.WEST) {
         IBlockState blockState4 = blockAccess.getBlockState(pos.west());
         if (isBlockStairs(blockState4.getBlock()) && value2 == blockState4.getValue(HALF)) {
            EnumFacing value4 = blockState4.getValue(FACING);
            if (value4 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               return flag ? 2 : 1;
            }

            if (value4 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               return flag ? 1 : 2;
            }
         }
      } else if (value == EnumFacing.SOUTH) {
         IBlockState blockState5 = blockAccess.getBlockState(pos.south());
         if (isBlockStairs(blockState5.getBlock()) && value2 == blockState5.getValue(HALF)) {
            EnumFacing value5 = blockState5.getValue(FACING);
            if (value5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               return flag ? 2 : 1;
            }

            if (value5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               return flag ? 1 : 2;
            }
         }
      } else if (value == EnumFacing.NORTH) {
         IBlockState blockState6 = blockAccess.getBlockState(pos.north());
         if (isBlockStairs(blockState6.getBlock()) && value2 == blockState6.getValue(HALF)) {
            EnumFacing value6 = blockState6.getValue(FACING);
            if (value6 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               return flag ? 1 : 2;
            }

            if (value6 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               return flag ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public static boolean isBlockStairs(Block blockIn) {
      return blockIn instanceof BlockStairs;
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
   }

   public boolean setPrimaryCollisionBounds(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState blockState2 = blockAccess.getBlockState(pos);
      EnumFacing value = blockState2.getValue(FACING);
      BlockStairs.EnumHalf value2 = blockState2.getValue(HALF);
      boolean flag = value2 == BlockStairs.EnumHalf.TOP;
      float f = 0.5F;
      float f2 = 1.0F;
      if (flag) {
         f = 0.0F;
         f2 = 0.5F;
      }

      float f3 = 0.0F;
      float f4 = 1.0F;
      float f5 = 0.0F;
      float f6 = 0.5F;
      boolean flag2 = true;
      if (value == EnumFacing.EAST) {
         f3 = 0.5F;
         f6 = 1.0F;
         IBlockState blockState3 = blockAccess.getBlockState(pos.east());
         if (isBlockStairs(blockState3.getBlock()) && value2 == blockState3.getValue(HALF)) {
            EnumFacing value3 = blockState3.getValue(FACING);
            if (value3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               f6 = 0.5F;
               flag2 = false;
            } else if (value3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               f5 = 0.5F;
               flag2 = false;
            }
         }
      } else if (value == EnumFacing.WEST) {
         f4 = 0.5F;
         f6 = 1.0F;
         IBlockState blockState4 = blockAccess.getBlockState(pos.west());
         if (isBlockStairs(blockState4.getBlock()) && value2 == blockState4.getValue(HALF)) {
            EnumFacing value4 = blockState4.getValue(FACING);
            if (value4 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               f6 = 0.5F;
               flag2 = false;
            } else if (value4 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               f5 = 0.5F;
               flag2 = false;
            }
         }
      } else if (value == EnumFacing.SOUTH) {
         f5 = 0.5F;
         f6 = 1.0F;
         IBlockState blockState5 = blockAccess.getBlockState(pos.south());
         if (isBlockStairs(blockState5.getBlock()) && value2 == blockState5.getValue(HALF)) {
            EnumFacing value5 = blockState5.getValue(FACING);
            if (value5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               f4 = 0.5F;
               flag2 = false;
            } else if (value5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               f3 = 0.5F;
               flag2 = false;
            }
         }
      } else if (value == EnumFacing.NORTH) {
         IBlockState blockState6 = blockAccess.getBlockState(pos.north());
         if (isBlockStairs(blockState6.getBlock()) && value2 == blockState6.getValue(HALF)) {
            EnumFacing value6 = blockState6.getValue(FACING);
            if (value6 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               f4 = 0.5F;
               flag2 = false;
            } else if (value6 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               f3 = 0.5F;
               flag2 = false;
            }
         }
      }

      this.setBlockBounds(f3, f, f5, f4, f2, f6);
      return flag2;
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return this.modelBlock.getMapColor(this.modelState);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      byte b = 0;
      if (state.getValue(HALF) == BlockStairs.EnumHalf.TOP) {
         b = 4;
      }

      return b | 5 - state.getValue(FACING).getIndex();
   }

   @Override
   public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion) {
      return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
   }

   @Override
   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return this.modelBlock.canCollideCheck(state, hitIfLiquid);
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      MovingObjectPosition[] movingObjectPositions = new MovingObjectPosition[8];
      IBlockState blockState2 = worldIn.getBlockState(pos);
      int horizontalIndex = blockState2.getValue(FACING).getHorizontalIndex();
      boolean flag = blockState2.getValue(HALF) == BlockStairs.EnumHalf.TOP;
      int[] iArray = cornerIndexLookup[horizontalIndex + (flag ? 4 : 0)];
      this.hasRaytraced = true;

      for (int i = 0; i < 8; i++) {
         this.rayTracePass = i;
         if (Arrays.binarySearch(iArray, i) < 0) {
            movingObjectPositions[i] = super.collisionRayTrace(worldIn, pos, start, end);
         }
      }

      for (int i2 : iArray) {
         movingObjectPositions[i2] = null;
      }

      MovingObjectPosition movingObjectPosition = null;
      double d = 0.0;

      for (MovingObjectPosition movingObjectPosition2 : movingObjectPositions) {
         if (movingObjectPosition2 != null) {
            double d2 = movingObjectPosition2.hitVec.squareDistanceTo(end);
            if (d2 > d) {
               movingObjectPosition = movingObjectPosition2;
               d = d2;
            }
         }
      }

      return movingObjectPosition;
   }

   @Override
   public void randomDisplayTick(World world2, BlockPos blockPos, IBlockState iBlockState, Random random) {
      this.modelBlock.randomDisplayTick(world2, blockPos, iBlockState, random);
   }

   @Override
   public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
      this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      return this.modelBlock.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
   }

   @Override
   public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
      return this.modelBlock.getMixedBrightnessForBlock(worldIn, pos);
   }

   public int getInnerShape(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState blockState2 = blockAccess.getBlockState(pos);
      EnumFacing value = blockState2.getValue(FACING);
      BlockStairs.EnumHalf value2 = blockState2.getValue(HALF);
      boolean flag = value2 == BlockStairs.EnumHalf.TOP;
      if (value == EnumFacing.EAST) {
         IBlockState blockState3 = blockAccess.getBlockState(pos.west());
         if (isBlockStairs(blockState3.getBlock()) && value2 == blockState3.getValue(HALF)) {
            EnumFacing value3 = blockState3.getValue(FACING);
            if (value3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               return flag ? 1 : 2;
            }

            if (value3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               return flag ? 2 : 1;
            }
         }
      } else if (value == EnumFacing.WEST) {
         IBlockState blockState4 = blockAccess.getBlockState(pos.east());
         if (isBlockStairs(blockState4.getBlock()) && value2 == blockState4.getValue(HALF)) {
            EnumFacing value4 = blockState4.getValue(FACING);
            if (value4 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               return flag ? 2 : 1;
            }

            if (value4 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               return flag ? 1 : 2;
            }
         }
      } else if (value == EnumFacing.SOUTH) {
         IBlockState blockState5 = blockAccess.getBlockState(pos.north());
         if (isBlockStairs(blockState5.getBlock()) && value2 == blockState5.getValue(HALF)) {
            EnumFacing value5 = blockState5.getValue(FACING);
            if (value5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               return flag ? 2 : 1;
            }

            if (value5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               return flag ? 1 : 2;
            }
         }
      } else if (value == EnumFacing.NORTH) {
         IBlockState blockState6 = blockAccess.getBlockState(pos.south());
         if (isBlockStairs(blockState6.getBlock()) && value2 == blockState6.getValue(HALF)) {
            EnumFacing value6 = blockState6.getValue(FACING);
            if (value6 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               return flag ? 1 : 2;
            }

            if (value6 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               return flag ? 2 : 1;
            }
         }
      }

      return 0;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, HALF, SHAPE);
   }

   @Override
   public float getExplosionResistance(Entity exploder) {
      return this.modelBlock.getExplosionResistance(exploder);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.modelBlock.canPlaceBlockAt(worldIn, pos);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.modelBlock.getBlockLayer();
   }

   public boolean setCornerCollisionBounds(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState blockState2 = blockAccess.getBlockState(pos);
      EnumFacing value = blockState2.getValue(FACING);
      BlockStairs.EnumHalf value2 = blockState2.getValue(HALF);
      boolean flag = value2 == BlockStairs.EnumHalf.TOP;
      float f = 0.5F;
      float f2 = 1.0F;
      if (flag) {
         f = 0.0F;
         f2 = 0.5F;
      }

      float f3 = 0.0F;
      float f4 = 0.5F;
      float f5 = 0.5F;
      float f6 = 1.0F;
      boolean flag2 = false;
      if (value == EnumFacing.EAST) {
         IBlockState blockState3 = blockAccess.getBlockState(pos.west());
         if (isBlockStairs(blockState3.getBlock()) && value2 == blockState3.getValue(HALF)) {
            EnumFacing value3 = blockState3.getValue(FACING);
            if (value3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               f5 = 0.0F;
               f6 = 0.5F;
               flag2 = true;
            } else if (value3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               f5 = 0.5F;
               f6 = 1.0F;
               flag2 = true;
            }
         }
      } else if (value == EnumFacing.WEST) {
         IBlockState blockState4 = blockAccess.getBlockState(pos.east());
         if (isBlockStairs(blockState4.getBlock()) && value2 == blockState4.getValue(HALF)) {
            f3 = 0.5F;
            f4 = 1.0F;
            EnumFacing value4 = blockState4.getValue(FACING);
            if (value4 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), blockState2)) {
               f5 = 0.0F;
               f6 = 0.5F;
               flag2 = true;
            } else if (value4 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), blockState2)) {
               f5 = 0.5F;
               f6 = 1.0F;
               flag2 = true;
            }
         }
      } else if (value == EnumFacing.SOUTH) {
         IBlockState blockState5 = blockAccess.getBlockState(pos.north());
         if (isBlockStairs(blockState5.getBlock()) && value2 == blockState5.getValue(HALF)) {
            f5 = 0.0F;
            f6 = 0.5F;
            EnumFacing value5 = blockState5.getValue(FACING);
            if (value5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               flag2 = true;
            } else if (value5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               f3 = 0.5F;
               f4 = 1.0F;
               flag2 = true;
            }
         }
      } else if (value == EnumFacing.NORTH) {
         IBlockState blockState6 = blockAccess.getBlockState(pos.south());
         if (isBlockStairs(blockState6.getBlock()) && value2 == blockState6.getValue(HALF)) {
            EnumFacing value6 = blockState6.getValue(FACING);
            if (value6 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), blockState2)) {
               flag2 = true;
            } else if (value6 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), blockState2)) {
               f3 = 0.5F;
               f4 = 1.0F;
               flag2 = true;
            }
         }
      }

      if (flag2) {
         this.setBlockBounds(f3, f, f5, f4, f2, f6);
      }

      return flag2;
   }

   public enum EnumHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      @Override
      public String getName() {
         return this.name;
      }

      EnumHalf(String text) {
         this.name = text;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   public enum EnumShape implements IStringSerializable {
      STRAIGHT("straight"),
      INNER_LEFT("inner_left"),
      INNER_RIGHT("inner_right"),
      OUTER_LEFT("outer_left"),
      OUTER_RIGHT("outer_right");

      private final String name;

      EnumShape(String text) {
         this.name = text;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
