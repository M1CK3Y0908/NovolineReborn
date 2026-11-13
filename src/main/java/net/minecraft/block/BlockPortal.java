package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
   public static final PropertyEnum<net.minecraft.util.EnumFacing.Axis> AXIS = PropertyEnum.create("axis", net.minecraft.util.EnumFacing.Axis.class, net.minecraft.util.EnumFacing.Axis.X, net.minecraft.util.EnumFacing.Axis.Z);

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null) {
         entityIn.setPortal(pos);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, AXIS);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return getMetaForAxis(state.getValue(AXIS));
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      net.minecraft.util.EnumFacing.Axis enumfacing$axis = worldIn.getBlockState(pos).getValue(AXIS);
      float f = 0.125F;
      float f2 = 0.125F;
      if (enumfacing$axis == net.minecraft.util.EnumFacing.Axis.X) {
         f = 0.5F;
      }

      if (enumfacing$axis == net.minecraft.util.EnumFacing.Axis.Z) {
         f2 = 0.5F;
      }

      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f2, 0.5F + f, 1.0F, 0.5F + f2);
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      net.minecraft.util.EnumFacing.Axis enumfacing$axis = state.getValue(AXIS);
      if (enumfacing$axis == net.minecraft.util.EnumFacing.Axis.X) {
         BlockPortal.Size size = new BlockPortal.Size(worldIn, pos, net.minecraft.util.EnumFacing.Axis.X);
         if (!size.isValid() || size.portalBlockCount < size.width * size.height) {
            worldIn.setBlockState(pos, Blocks.air.getDefaultState());
         }
      } else if (enumfacing$axis == net.minecraft.util.EnumFacing.Axis.Z) {
         BlockPortal.Size size2 = new BlockPortal.Size(worldIn, pos, net.minecraft.util.EnumFacing.Axis.Z);
         if (!size2.isValid() || size2.portalBlockCount < size2.width * size2.height) {
            worldIn.setBlockState(pos, Blocks.air.getDefaultState());
         }
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (rand.nextInt(100) == 0) {
         worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
      }

      for (int index = 0; index < 4; index++) {
         double xCoordinate = pos.getX() + rand.nextFloat();
         double yCoordinate = pos.getY() + rand.nextFloat();
         double zCoordinate = pos.getZ() + rand.nextFloat();
         double d = (rand.nextFloat() - 0.5) * 0.5;
         double d2 = (rand.nextFloat() - 0.5) * 0.5;
         double d3 = (rand.nextFloat() - 0.5) * 0.5;
         int i = rand.nextInt(2) * 2 - 1;
         if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
            xCoordinate = pos.getX() + 0.5 + 0.25 * i;
            d = rand.nextFloat() * 2.0F * i;
         } else {
            zCoordinate = pos.getZ() + 0.5 + 0.25 * i;
            d3 = rand.nextFloat() * 2.0F * i;
         }

         worldIn.spawnParticle(EnumParticleTypes.PORTAL, xCoordinate, yCoordinate, zCoordinate, d, d2, d3);
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.provider.isSurfaceWorld()
         && worldIn.getGameRules().getBoolean("doMobSpawning")
         && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
         int yCoordinate = pos.getY();
         BlockPos blockPos = pos;

         while (!World.doesBlockHaveSolidTopSurface(worldIn, blockPos) && blockPos.getY() > 0) {
            blockPos = blockPos.down();
         }

         if (yCoordinate > 0 && !worldIn.getBlockState(blockPos.up()).getBlock().isNormalCube()) {
            Entity entity = ItemMonsterPlacer.spawnCreature(worldIn, 57, blockPos.getX() + 0.5, blockPos.getY() + 1.1, blockPos.getZ() + 0.5);
            if (entity != null) {
               entity.timeUntilPortal = entity.getPortalCooldown();
            }
         }
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }

   public boolean tryPlacePortal(World worldIn, BlockPos pos) {
      BlockPortal.Size size = new BlockPortal.Size(worldIn, pos, net.minecraft.util.EnumFacing.Axis.X);
      if (size.isValid() && size.portalBlockCount == 0) {
         size.placePortalBlocks();
         return true;
      } else {
         BlockPortal.Size size2 = new BlockPortal.Size(worldIn, pos, net.minecraft.util.EnumFacing.Axis.Z);
         if (size2.isValid() && size2.portalBlockCount == 0) {
            size2.placePortalBlocks();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   public net.minecraft.block.state.pattern.BlockPattern.PatternHelper getPortalPattern(World worldIn, BlockPos pos) {
      net.minecraft.util.EnumFacing.Axis axis2 = net.minecraft.util.EnumFacing.Axis.Z;
      BlockPortal.Size size = new BlockPortal.Size(worldIn, pos, net.minecraft.util.EnumFacing.Axis.X);
      LoadingCache loadingCache = BlockPattern.createLoadingCache(worldIn, true);
      if (!size.isValid()) {
         axis2 = net.minecraft.util.EnumFacing.Axis.X;
         size = new BlockPortal.Size(worldIn, pos, net.minecraft.util.EnumFacing.Axis.Z);
      }

      if (!size.isValid()) {
         return new net.minecraft.block.state.pattern.BlockPattern.PatternHelper(pos, EnumFacing.NORTH, EnumFacing.UP, loadingCache, 1, 1, 1);
      }

      int[] iArray = new int[net.minecraft.util.EnumFacing.AxisDirection.values().length];
      EnumFacing enumFacing = size.leftDir.rotateYCCW();
      BlockPos blockPos2 = size.bottomLeft.up(size.getHeight() - 1);

      for (net.minecraft.util.EnumFacing.AxisDirection axisDirection : net.minecraft.util.EnumFacing.AxisDirection.values()) {
         net.minecraft.block.state.pattern.BlockPattern.PatternHelper patternHelper = new net.minecraft.block.state.pattern.BlockPattern.PatternHelper(
            enumFacing.getAxisDirection() == axisDirection ? blockPos2 : blockPos2.offset(size.leftDir, size.getWidth() - 1),
            EnumFacing.getFacingFromAxis(axisDirection, axis2),
            EnumFacing.UP,
            loadingCache,
            size.getWidth(),
            size.getHeight(),
            1
         );

         for (int i = 0; i < size.getWidth(); i++) {
            for (int i2 = 0; i2 < size.getHeight(); i2++) {
               BlockWorldState blockWorldState = patternHelper.translateOffset(i, i2, 1);
               if (blockWorldState.getBlockState() != null && blockWorldState.getBlockState().getBlock().getMaterial() != Material.air) {
                  iArray[axisDirection.ordinal()]++;
               }
            }
         }
      }

      net.minecraft.util.EnumFacing.AxisDirection axisDirection2 = net.minecraft.util.EnumFacing.AxisDirection.POSITIVE;

      for (net.minecraft.util.EnumFacing.AxisDirection axisDirection3 : net.minecraft.util.EnumFacing.AxisDirection.values()) {
         if (iArray[axisDirection3.ordinal()] < iArray[axisDirection2.ordinal()]) {
            axisDirection2 = axisDirection3;
         }
      }

      return new net.minecraft.block.state.pattern.BlockPattern.PatternHelper(
         enumFacing.getAxisDirection() == axisDirection2 ? blockPos2 : blockPos2.offset(size.leftDir, size.getWidth() - 1),
         EnumFacing.getFacingFromAxis(axisDirection2, axis2),
         EnumFacing.UP,
         loadingCache,
         size.getWidth(),
         size.getHeight(),
         1
      );
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? net.minecraft.util.EnumFacing.Axis.Z : net.minecraft.util.EnumFacing.Axis.X);
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      net.minecraft.util.EnumFacing.Axis enumfacing$axis = null;
      IBlockState blockState = worldIn.getBlockState(pos);
      if (worldIn.getBlockState(pos).getBlock() == this) {
         enumfacing$axis = blockState.getValue(AXIS);
         if (enumfacing$axis == null) {
            return false;
         }

         if (enumfacing$axis == net.minecraft.util.EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST) {
            return false;
         }

         if (enumfacing$axis == net.minecraft.util.EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH) {
            return false;
         }
      }

      boolean flag = worldIn.getBlockState(pos.west()).getBlock() == this && worldIn.getBlockState(pos.west(2)).getBlock() != this;
      boolean flag2 = worldIn.getBlockState(pos.east()).getBlock() == this && worldIn.getBlockState(pos.east(2)).getBlock() != this;
      boolean flag3 = worldIn.getBlockState(pos.north()).getBlock() == this && worldIn.getBlockState(pos.north(2)).getBlock() != this;
      boolean flag4 = worldIn.getBlockState(pos.south()).getBlock() == this && worldIn.getBlockState(pos.south(2)).getBlock() != this;
      boolean flag5 = flag || flag2 || enumfacing$axis == net.minecraft.util.EnumFacing.Axis.X;
      boolean flag6 = flag3 || flag4 || enumfacing$axis == net.minecraft.util.EnumFacing.Axis.Z;
      return flag5 && side == EnumFacing.WEST
         ? true
         : (flag5 && side == EnumFacing.EAST ? true : (flag6 && side == EnumFacing.NORTH ? true : flag6 && side == EnumFacing.SOUTH));
   }

   public static int getMetaForAxis(net.minecraft.util.EnumFacing.Axis axis) {
      return axis == net.minecraft.util.EnumFacing.Axis.X ? 1 : (axis == net.minecraft.util.EnumFacing.Axis.Z ? 2 : 0);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   public BlockPortal() {
      super(Material.portal);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, net.minecraft.util.EnumFacing.Axis.X));
      this.setTickRandomly(true);
   }

   public static class Size {
      private int portalBlockCount = 0;
      private final World world;
      private final net.minecraft.util.EnumFacing.Axis axis;
      private final EnumFacing rightDir;
      private final EnumFacing leftDir;
      private BlockPos bottomLeft;
      private int width;
      private int height;

      public int getHeight() {
         return this.height;
      }

      protected int getDistanceUntilObsidian(BlockPos pos, EnumFacing facing) {
         int i;
         for (i = 0; i < 22; i++) {
            BlockPos blockPos = pos.offset(facing, i);
            if (!this.canPlacePortalBlock(this.world.getBlockState(blockPos).getBlock())
               || this.world.getBlockState(blockPos.down()).getBlock() != Blocks.obsidian) {
               break;
            }
         }

         Block block = this.world.getBlockState(pos.offset(facing, i)).getBlock();
         return block == Blocks.obsidian ? i : 0;
      }

      public void placePortalBlocks() {
         for (int index = 0; index < this.width; index++) {
            BlockPos blockPos = this.bottomLeft.offset(this.leftDir, index);

            for (int innerIndex = 0; innerIndex < this.height; innerIndex++) {
               this.world.setBlockState(blockPos.up(innerIndex), Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
            }
         }
      }

      public int getWidth() {
         return this.width;
      }

      public Size(World worldIn, BlockPos pos, net.minecraft.util.EnumFacing.Axis axisIn) {
         this.world = worldIn;
         this.axis = axisIn;
         if (axisIn == net.minecraft.util.EnumFacing.Axis.X) {
            this.rightDir = EnumFacing.EAST;
            this.leftDir = EnumFacing.WEST;
         } else {
            this.rightDir = EnumFacing.NORTH;
            this.leftDir = EnumFacing.SOUTH;
         }

         BlockPos pos2 = pos;

         while (pos.getY() > pos2.getY() - 21 && pos.getY() > 0 && this.canPlacePortalBlock(worldIn.getBlockState(pos.down()).getBlock())) {
            pos = pos.down();
         }

         int intValuex = this.getDistanceUntilObsidian(pos, this.rightDir) - 1;
         if (intValuex >= 0) {
            this.bottomLeft = pos.offset(this.rightDir, intValuex);
            this.width = this.getDistanceUntilObsidian(this.bottomLeft, this.leftDir);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.calculatePortalHeight();
         }
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      protected int calculatePortalHeight() {
         label56:
         for (this.height = 0; this.height < 21; this.height++) {
            for (int i = 0; i < this.width; i++) {
               BlockPos blockPos2 = this.bottomLeft.offset(this.leftDir, i).up(this.height);
               Block block2 = this.world.getBlockState(blockPos2).getBlock();
               if (!this.canPlacePortalBlock(block2)) {
                  break label56;
               }

               if (block2 == Blocks.portal) {
                  this.portalBlockCount++;
               }

               if (i == 0) {
                  block2 = this.world.getBlockState(blockPos2.offset(this.rightDir)).getBlock();
                  if (block2 != Blocks.obsidian) {
                     break label56;
                  }
               } else if (i == this.width - 1) {
                  block2 = this.world.getBlockState(blockPos2.offset(this.leftDir)).getBlock();
                  if (block2 != Blocks.obsidian) {
                     break label56;
                  }
               }
            }
         }

         for (int i2 = 0; i2 < this.width; i2++) {
            if (this.world.getBlockState(this.bottomLeft.offset(this.leftDir, i2).up(this.height)).getBlock() != Blocks.obsidian) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         }

         this.bottomLeft = null;
         this.width = 0;
         this.height = 0;
         return 0;
      }

      protected boolean canPlacePortalBlock(Block block) {
         return block.blockMaterial == Material.air || block == Blocks.fire || block == Blocks.portal;
      }
   }
}
