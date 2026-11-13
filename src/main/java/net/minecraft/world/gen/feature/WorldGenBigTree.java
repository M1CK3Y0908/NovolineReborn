package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenBigTree extends WorldGenAbstractTree {
   List<WorldGenBigTree.FoliageCoordinates> foliageNodes;
   private Random rand;
   private BlockPos basePos = BlockPos.ORIGIN;
   double heightAttenuation = 0.618;
   double branchSlope = 0.381;
   double scaleWidth = 1.0;
   double leafDensity = 1.0;
   int trunkSize = 1;
   int heightLimitLimit = 12;
   int heightLimit;
   int leafDistanceLimit = 4;
   int height;
   private World world;

   void generateLeaves() {
      for (WorldGenBigTree.FoliageCoordinates foliageCoordinates : this.foliageNodes) {
         this.generateLeafNode(foliageCoordinates);
      }
   }

   boolean leafNodeNeedsBase(int height) {
      return height >= this.heightLimit * 0.2;
   }

   private int getGreatestDistance(BlockPos posIn) {
      int xCoordinate = MathHelper.abs_int(posIn.getX());
      int yCoordinate = MathHelper.abs_int(posIn.getY());
      int zCoordinate = MathHelper.abs_int(posIn.getZ());
      return zCoordinate > xCoordinate && zCoordinate > yCoordinate ? zCoordinate : (yCoordinate > xCoordinate ? yCoordinate : xCoordinate);
   }

   float leafSize(int layer) {
      return layer >= 0 && layer < this.leafDistanceLimit ? (layer != 0 && layer != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
   }

   public WorldGenBigTree(boolean flag) {
      super(flag);
   }

   private net.minecraft.block.BlockLog.EnumAxis getLogAxis(BlockPos start, BlockPos position) {
      net.minecraft.block.BlockLog.EnumAxis blocklog$enumaxis = net.minecraft.block.BlockLog.EnumAxis.Y;
      int xCoordinate = Math.abs(position.getX() - start.getX());
      int zCoordinate = Math.abs(position.getZ() - start.getZ());
      int i = Math.max(xCoordinate, zCoordinate);
      if (i > 0) {
         if (xCoordinate == i) {
            blocklog$enumaxis = net.minecraft.block.BlockLog.EnumAxis.X;
         } else if (zCoordinate == i) {
            blocklog$enumaxis = net.minecraft.block.BlockLog.EnumAxis.Z;
         }
      }

      return blocklog$enumaxis;
   }

   @Override
   public void setDecorationDefaults() {
      this.leafDistanceLimit = 5;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      this.world = worldIn;
      this.basePos = position;
      this.rand = new Random(rand.nextLong());
      if (this.heightLimit == 0) {
         this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
      }

      if (!this.validTreeLocation()) {
         return false;
      }

      this.generateLeafNodeList();
      this.generateLeaves();
      this.generateTrunk();
      this.generateLeafNodeBases();
      return true;
   }

   void generateLeafNode(BlockPos pos) {
      for (int index = 0; index < this.leafDistanceLimit; index++) {
         this.generateLeafLayer(pos.up(index), this.leafSize(index), Blocks.leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false));
      }
   }

   void generateLeafNodeList() {
      this.height = (int)(this.heightLimit * this.heightAttenuation);
      if (this.height >= this.heightLimit) {
         this.height = this.heightLimit - 1;
      }

      int i = (int)(1.382 + Math.pow(this.leafDensity * this.heightLimit / 13.0, 2.0));
      if (i < 1) {
         i = 1;
      }

      int yCoordinate = this.basePos.getY() + this.height;
      int i2 = this.heightLimit - this.leafDistanceLimit;
      this.foliageNodes = Lists.newArrayList();
      this.foliageNodes.add(new WorldGenBigTree.FoliageCoordinates(this.basePos.up(i2), yCoordinate));

      for (; i2 >= 0; i2--) {
         float f = this.layerSize(i2);
         if (f >= 0.0F) {
            for (int outerIndex = 0; outerIndex < i; outerIndex++) {
               double d = this.scaleWidth * f * (this.rand.nextFloat() + 0.328);
               double d2 = this.rand.nextFloat() * 2.0F * Math.PI;
               double d3 = d * Math.sin(d2) + 0.5;
               double d4 = d * Math.cos(d2) + 0.5;
               BlockPos blockPos = this.basePos.add(d3, i2 - 1, d4);
               BlockPos blockPos2 = blockPos.up(this.leafDistanceLimit);
               if (this.checkBlockLine(blockPos, blockPos2) == -1) {
                  int xCoordinate = this.basePos.getX() - blockPos.getX();
                  int zCoordinate = this.basePos.getZ() - blockPos.getZ();
                  double yCoordinate2 = blockPos.getY() - Math.sqrt(xCoordinate * xCoordinate + zCoordinate * zCoordinate) * this.branchSlope;
                  int i3 = yCoordinate2 > yCoordinate ? yCoordinate : (int)yCoordinate2;
                  BlockPos blockPos3 = new BlockPos(this.basePos.getX(), i3, this.basePos.getZ());
                  if (this.checkBlockLine(blockPos3, blockPos) == -1) {
                     this.foliageNodes.add(new WorldGenBigTree.FoliageCoordinates(blockPos, blockPos3.getY()));
                  }
               }
            }
         }
      }
   }

   void generateTrunk() {
      BlockPos blockPos = this.basePos;
      BlockPos blockPos2 = this.basePos.up(this.height);
      Block block = Blocks.log;
      this.placeBlockLine(blockPos, blockPos2, block);
      if (this.trunkSize == 2) {
         this.placeBlockLine(blockPos.east(), blockPos2.east(), block);
         this.placeBlockLine(blockPos.east().south(), blockPos2.east().south(), block);
         this.placeBlockLine(blockPos.south(), blockPos2.south(), block);
      }
   }

   private boolean validTreeLocation() {
      Block block = this.world.getBlockState(this.basePos.down()).getBlock();
      if (block != Blocks.dirt && block != Blocks.grass && block != Blocks.farmland) {
         return false;
      }

      int i = this.checkBlockLine(this.basePos, this.basePos.up(this.heightLimit - 1));
      if (i == -1) {
         return true;
      }

      if (i < 6) {
         return false;
      }

      this.heightLimit = i;
      return true;
   }

   int checkBlockLine(BlockPos posOne, BlockPos posTwo) {
      BlockPos blockPos = posTwo.add(-posOne.getX(), -posOne.getY(), -posOne.getZ());
      int greatestDistance = this.getGreatestDistance(blockPos);
      float f = (float)blockPos.getX() / greatestDistance;
      float f2 = (float)blockPos.getY() / greatestDistance;
      float f3 = (float)blockPos.getZ() / greatestDistance;
      if (greatestDistance == 0) {
         return -1;
      }

      for (int innerIndex = 0; innerIndex <= greatestDistance; innerIndex++) {
         BlockPos blockPos2 = posOne.add(0.5F + innerIndex * f, 0.5F + innerIndex * f2, 0.5F + innerIndex * f3);
         if (!this.canGrowInto(this.world.getBlockState(blockPos2).getBlock())) {
            return innerIndex;
         }
      }

      return -1;
   }

   void generateLeafNodeBases() {
      for (WorldGenBigTree.FoliageCoordinates foliageCoordinates : this.foliageNodes) {
         int baseY2 = foliageCoordinates.getBaseY();
         BlockPos blockPos3 = new BlockPos(this.basePos.getX(), baseY2, this.basePos.getZ());
         if (!blockPos3.equals(foliageCoordinates) && this.leafNodeNeedsBase(baseY2 - this.basePos.getY())) {
            this.placeBlockLine(blockPos3, foliageCoordinates, Blocks.log);
         }
      }
   }

   void placeBlockLine(BlockPos start, BlockPos end, Block block) {
      BlockPos blockPos = end.add(-start.getX(), -start.getY(), -start.getZ());
      int greatestDistance = this.getGreatestDistance(blockPos);
      float f = (float)blockPos.getX() / greatestDistance;
      float f2 = (float)blockPos.getY() / greatestDistance;
      float f3 = (float)blockPos.getZ() / greatestDistance;

      for (int innerIndex = 0; innerIndex <= greatestDistance; innerIndex++) {
         BlockPos blockPos2 = start.add(0.5F + innerIndex * f, 0.5F + innerIndex * f2, 0.5F + innerIndex * f3);
         net.minecraft.block.BlockLog.EnumAxis blocklog$enumaxis = this.getLogAxis(start, blockPos2);
         this.setBlockAndNotifyAdequately(this.world, blockPos2, block.getDefaultState().withProperty(BlockLog.LOG_AXIS, blocklog$enumaxis));
      }
   }

   void generateLeafLayer(BlockPos position, float radius, IBlockState leafState) {
      int i = (int)(radius + 0.618);

      for (int innerIndex = -i; innerIndex <= i; innerIndex++) {
         for (int nestedIndex = -i; nestedIndex <= i; nestedIndex++) {
            if (Math.pow(Math.abs(innerIndex) + 0.5, 2.0) + Math.pow(Math.abs(nestedIndex) + 0.5, 2.0) <= radius * radius) {
               BlockPos blockPos = position.add(innerIndex, 0, nestedIndex);
               Material material = this.world.getBlockState(blockPos).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(this.world, blockPos, leafState);
               }
            }
         }
      }
   }

   float layerSize(int layer) {
      if (layer < this.heightLimit * 0.3F) {
         return -1.0F;
      }

      float f = this.heightLimit / 2.0F;
      float f2 = f - layer;
      float f3 = MathHelper.sqrt_float(f * f - f2 * f2);
      if (f2 == 0.0F) {
         f3 = f;
      } else if (Math.abs(f2) >= f) {
         return 0.0F;
      }

      return f3 * 0.5F;
   }

   static class FoliageCoordinates extends BlockPos {
      private final int baseY;

      public int getBaseY() {
         return this.baseY;
      }

      public FoliageCoordinates(BlockPos blockPos3, int i) {
         super(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
         this.baseY = i;
      }
   }
}
