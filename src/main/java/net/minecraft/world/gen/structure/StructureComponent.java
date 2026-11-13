package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public abstract class StructureComponent {
   protected StructureBoundingBox boundingBox;
   protected int componentType;
   protected EnumFacing coordBaseMode;

   public NBTTagCompound createStructureBaseNBT() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setString("id", MapGenStructureIO.getStructureComponentName(this));
      nbtTagCompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
      nbtTagCompound.setInteger("O", this.coordBaseMode == null ? -1 : this.coordBaseMode.getHorizontalIndex());
      nbtTagCompound.setInteger("GD", this.componentType);
      this.writeStructureToNBT(nbtTagCompound);
      return nbtTagCompound;
   }

   protected void setBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
      BlockPos blockPos2 = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingboxIn.isVecInside(blockPos2)) {
         worldIn.setBlockState(blockPos2, blockstateIn, 2);
      }
   }

   public StructureComponent() {
   }

   protected void handleWorld2(World world2, StructureBoundingBox structureBoundingBox, Random random, int i, int i2, EnumFacing enumFacing) {
      BlockPos blockPos2 = new BlockPos(this.getXWithOffset(i, i2), this.getYWithOffset(1), this.getZWithOffset(i, i2));
      if (structureBoundingBox.isVecInside(blockPos2)) {
         ItemDoor.placeDoor(world2, blockPos2, enumFacing.rotateYCCW(), Blocks.oak_door);
      }
   }

   protected void handleWorld3(
      World world2,
      StructureBoundingBox structureBoundingBox,
      Random random,
      float f,
      int i,
      int i2,
      int i3,
      int i4,
      int i5,
      int i6,
      IBlockState iBlockState,
      IBlockState iBlockState2
   ) {
      for (int i7 = i2; i7 <= i5; i7++) {
         for (int i8 = i; i8 <= i4; i8++) {
            for (int i9 = i3; i9 <= i6; i9++) {
               if (random.nextFloat() <= f) {
                  if (i7 != i2 && i7 != i5 && i8 != i && i8 != i4 && i9 != i3 && i9 != i6) {
                     this.setBlockState(world2, iBlockState2, i8, i7, i9, structureBoundingBox);
                  } else {
                     this.setBlockState(world2, iBlockState, i8, i7, i9, structureBoundingBox);
                  }
               }
            }
         }
      }
   }

   protected void fillWithAir(World worldIn, StructureBoundingBox structurebb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for (int index = minY; index <= maxY; index++) {
         for (int innerIndex = minX; innerIndex <= maxX; innerIndex++) {
            for (int nestedIndex = minZ; nestedIndex <= maxZ; nestedIndex++) {
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), innerIndex, index, nestedIndex, structurebb);
            }
         }
      }
   }

   protected int getXWithOffset(int x, int z) {
      if (this.coordBaseMode == null) {
         return x;
      }

      switch (this.coordBaseMode) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX + x;
         case WEST:
            return this.boundingBox.maxX - z;
         case EAST:
            return this.boundingBox.minX + z;
         default:
            return x;
      }
   }

   protected IBlockState getBlockStateFromPos(World worldIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
      int xWithOffset = this.getXWithOffset(x, z);
      int yWithOffset = this.getYWithOffset(y);
      int zWithOffset = this.getZWithOffset(x, z);
      BlockPos blockPos2 = new BlockPos(xWithOffset, yWithOffset, zWithOffset);
      return !boundingboxIn.isVecInside(blockPos2) ? Blocks.air.getDefaultState() : worldIn.getBlockState(blockPos2);
   }

   public int getComponentType() {
      return this.componentType;
   }

   protected StructureComponent(int type) {
      this.componentType = type;
   }

   protected boolean checkWorld2(World world2, StructureBoundingBox structureBoundingBox, Random random, int i, int i2, int i3, List list) {
      BlockPos blockPos2 = new BlockPos(this.getXWithOffset(i, i2), this.getYWithOffset(-2), this.getZWithOffset(i, i2));
      if (structureBoundingBox.isVecInside(blockPos2) && world2.getBlockState(blockPos2).getBlock() != Blocks.dispenser) {
         world2.setBlockState(blockPos2, Blocks.dispenser.getStateFromMeta(this.getMetadataWithOffset(Blocks.dispenser, i3)), 2);
         TileEntity tileEntity2 = world2.getTileEntity(blockPos2);
         if (tileEntity2 instanceof TileEntityDispenser) {
            WeightedRandomChestContent.handleRandom(random, list, (TileEntityDispenser)tileEntity2);
         }

         return true;
      } else {
         return false;
      }
   }

   public abstract boolean addComponentParts(World world2, Random random, StructureBoundingBox structureBoundingBox);

   protected int getZWithOffset(int x, int z) {
      if (this.coordBaseMode == null) {
         return z;
      }

      switch (this.coordBaseMode) {
         case NORTH:
            return this.boundingBox.maxZ - z;
         case SOUTH:
            return this.boundingBox.minZ + z;
         case WEST:
         case EAST:
            return this.boundingBox.minZ + x;
         default:
            return z;
      }
   }

   public BlockPos getBoundingBoxCenter() {
      return new BlockPos(this.boundingBox.getCenter());
   }

   public void readStructureBaseNBT(World worldIn, NBTTagCompound tagCompound) {
      if (tagCompound.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
      }

      int integer = tagCompound.getInteger("O");
      this.coordBaseMode = integer == -1 ? null : EnumFacing.getHorizontal(integer);
      this.componentType = tagCompound.getInteger("GD");
      this.readStructureFromNBT(tagCompound);
   }

   protected void replaceAirAndLiquidDownwards(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
      int xWithOffset = this.getXWithOffset(x, z);
      int yWithOffset = this.getYWithOffset(y);
      int zWithOffset = this.getZWithOffset(x, z);
      if (boundingboxIn.isVecInside(new BlockPos(xWithOffset, yWithOffset, zWithOffset))) {
         while (
            (
                  worldIn.isAirBlock(new BlockPos(xWithOffset, yWithOffset, zWithOffset))
                     || worldIn.getBlockState(new BlockPos(xWithOffset, yWithOffset, zWithOffset)).getBlock().getMaterial().isLiquid()
               )
               && yWithOffset > 1
         ) {
            worldIn.setBlockState(new BlockPos(xWithOffset, yWithOffset, zWithOffset), blockstateIn, 2);
            yWithOffset--;
         }
      }
   }

   public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
   }

   protected abstract void writeStructureToNBT(NBTTagCompound nbtTagCompound);

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   protected int getMetadataWithOffset(Block blockIn, int meta) {
      if (blockIn == Blocks.rail) {
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.EAST) {
            if (meta == 1) {
               return 0;
            }

            return 1;
         }
      } else if (blockIn instanceof BlockDoor) {
         if (this.coordBaseMode == EnumFacing.SOUTH) {
            if (meta == 0) {
               return 2;
            }

            if (meta == 2) {
               return 0;
            }
         } else {
            if (this.coordBaseMode == EnumFacing.WEST) {
               return meta + 1 & 3;
            }

            if (this.coordBaseMode == EnumFacing.EAST) {
               return meta + 3 & 3;
            }
         }
      } else if (blockIn != Blocks.stone_stairs
         && blockIn != Blocks.oak_stairs
         && blockIn != Blocks.nether_brick_stairs
         && blockIn != Blocks.stone_brick_stairs
         && blockIn != Blocks.sandstone_stairs) {
         if (blockIn == Blocks.ladder) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            }
         } else if (blockIn == Blocks.stone_button) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (meta == 3) {
                  return 4;
               }

               if (meta == 4) {
                  return 3;
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (meta == 3) {
                  return 1;
               }

               if (meta == 4) {
                  return 2;
               }

               if (meta == 2) {
                  return 3;
               }

               if (meta == 1) {
                  return 4;
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (meta == 3) {
                  return 2;
               }

               if (meta == 4) {
                  return 1;
               }

               if (meta == 2) {
                  return 3;
               }

               if (meta == 1) {
                  return 4;
               }
            }
         } else if (blockIn == Blocks.tripwire_hook || blockIn instanceof BlockDirectional) {
            EnumFacing facing = EnumFacing.getHorizontal(meta);
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH) {
                  return facing.getOpposite().getHorizontalIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (facing == EnumFacing.NORTH) {
                  return EnumFacing.WEST.getHorizontalIndex();
               }

               if (facing == EnumFacing.SOUTH) {
                  return EnumFacing.EAST.getHorizontalIndex();
               }

               if (facing == EnumFacing.WEST) {
                  return EnumFacing.NORTH.getHorizontalIndex();
               }

               if (facing == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.getHorizontalIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (facing == EnumFacing.NORTH) {
                  return EnumFacing.EAST.getHorizontalIndex();
               }

               if (facing == EnumFacing.SOUTH) {
                  return EnumFacing.WEST.getHorizontalIndex();
               }

               if (facing == EnumFacing.WEST) {
                  return EnumFacing.NORTH.getHorizontalIndex();
               }

               if (facing == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.getHorizontalIndex();
               }
            }
         } else if (blockIn == Blocks.piston || blockIn == Blocks.sticky_piston || blockIn == Blocks.lever || blockIn == Blocks.dispenser) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (meta == EnumFacing.NORTH.getIndex() || meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.getFront(meta).getOpposite().getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            }
         }
      } else if (this.coordBaseMode == EnumFacing.SOUTH) {
         if (meta == 2) {
            return 3;
         }

         if (meta == 3) {
            return 2;
         }
      } else if (this.coordBaseMode == EnumFacing.WEST) {
         if (meta == 0) {
            return 2;
         }

         if (meta == 1) {
            return 3;
         }

         if (meta == 2) {
            return 0;
         }

         if (meta == 3) {
            return 1;
         }
      } else if (this.coordBaseMode == EnumFacing.EAST) {
         if (meta == 0) {
            return 2;
         }

         if (meta == 1) {
            return 3;
         }

         if (meta == 2) {
            return 1;
         }

         if (meta == 3) {
            return 0;
         }
      }

      return meta;
   }

   protected void fillWithBlocks(
      World worldIn,
      StructureBoundingBox boundingboxIn,
      int xMin,
      int yMin,
      int zMin,
      int xMax,
      int yMax,
      int zMax,
      IBlockState boundaryBlockState,
      IBlockState insideBlockState,
      boolean existingOnly
   ) {
      for (int index = yMin; index <= yMax; index++) {
         for (int innerIndex = xMin; innerIndex <= xMax; innerIndex++) {
            for (int nestedIndex = zMin; nestedIndex <= zMax; nestedIndex++) {
               if (!existingOnly || this.getBlockStateFromPos(worldIn, innerIndex, index, nestedIndex, boundingboxIn).getBlock().getMaterial() != Material.air) {
                  if (index != yMin && index != yMax && innerIndex != xMin && innerIndex != xMax && nestedIndex != zMin && nestedIndex != zMax) {
                     this.setBlockState(worldIn, insideBlockState, innerIndex, index, nestedIndex, boundingboxIn);
                  } else {
                     this.setBlockState(worldIn, boundaryBlockState, innerIndex, index, nestedIndex, boundingboxIn);
                  }
               }
            }
         }
      }
   }

   public void handleInt(int i) {
      this.boundingBox.offset(0, i, 0);
   }

   public static StructureComponent findIntersecting(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn) {
      for (StructureComponent structureComponent : listIn) {
         if (structureComponent.getBoundingBox() != null && structureComponent.getBoundingBox().intersectsWith(boundingboxIn)) {
            return structureComponent;
         }
      }

      return null;
   }

   protected boolean isLiquidInStructureBoundingBox(World worldIn, StructureBoundingBox boundingboxIn) {
      int i = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
      int i2 = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
      int i3 = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
      int i4 = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
      int i5 = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
      int i6 = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int nestedIndex = i; nestedIndex <= i4; nestedIndex++) {
         for (int outerIndex = i3; outerIndex <= i6; outerIndex++) {
            if (worldIn.getBlockState(mutableBlockPos.set(nestedIndex, i2, outerIndex)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(mutableBlockPos.set(nestedIndex, i5, outerIndex)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int index = i; index <= i4; index++) {
         for (int index2 = i2; index2 <= i5; index2++) {
            if (worldIn.getBlockState(mutableBlockPos.set(index, index2, i3)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(mutableBlockPos.set(index, index2, i6)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int index3 = i3; index3 <= i6; index3++) {
         for (int index4 = i2; index4 <= i5; index4++) {
            if (worldIn.getBlockState(mutableBlockPos.set(i, index4, index3)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(mutableBlockPos.set(i4, index4, index3)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected void fillWithRandomizedBlocks(
      World worldIn,
      StructureBoundingBox boundingboxIn,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      boolean alwaysReplace,
      Random rand,
      StructureComponent.BlockSelector blockSelector
   ) {
      for (int minY2 = minY; minY2 <= maxY; minY2++) {
         for (int minX2 = minX; minX2 <= maxX; minX2++) {
            for (int minZ2 = minZ; minZ2 <= maxZ; minZ2++) {
               if (!alwaysReplace || this.getBlockStateFromPos(worldIn, minX2, minY2, minZ2, boundingboxIn).getBlock().getMaterial() != Material.air) {
                  boolean flag = minY2 == minY || minY2 == maxY || minX2 == minX || minX2 == maxX || minZ2 == minZ || minZ2 == maxZ;
                  blockSelector.handleRandom(rand, flag);
                  this.setBlockState(worldIn, blockSelector.getBlockState(), minX2, minY2, minZ2, boundingboxIn);
               }
            }
         }
      }
   }

   protected boolean generateChestContents(
      World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int max
   ) {
      BlockPos blockPos2 = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingBoxIn.isVecInside(blockPos2) && worldIn.getBlockState(blockPos2).getBlock() != Blocks.chest) {
         IBlockState blockState = Blocks.chest.getDefaultState();
         worldIn.setBlockState(blockPos2, Blocks.chest.correctFacing(worldIn, blockPos2, blockState), 2);
         TileEntity tileEntity = worldIn.getTileEntity(blockPos2);
         if (tileEntity instanceof TileEntityChest) {
            WeightedRandomChestContent.generateChestContents(rand, listIn, (TileEntityChest)tileEntity, max);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void handleWorld4(World world2, StructureBoundingBox structureBoundingBox, int i, int i2, int i3, int i4, int i5, int i6, IBlockState iBlockState) {
      float f = i4 - i + 1;
      float f2 = i5 - i2 + 1;
      float f3 = i6 - i3 + 1;
      float f4 = i + f / 2.0F;
      float f5 = i3 + f3 / 2.0F;

      for (int i7 = i2; i7 <= i5; i7++) {
         float f6 = (i7 - i2) / f2;

         for (int i8 = i; i8 <= i4; i8++) {
            float f7 = (i8 - f4) / (f * 0.5F);

            for (int i9 = i3; i9 <= i6; i9++) {
               float f8 = (i9 - f5) / (f3 * 0.5F);
               float f9 = f7 * f7 + f6 * f6 + f8 * f8;
               if (f9 <= 1.05F) {
                  this.setBlockState(world2, iBlockState, i8, i7, i9, structureBoundingBox);
               }
            }
         }
      }
   }

   protected abstract void readStructureFromNBT(NBTTagCompound nbtTagCompound);

   protected void randomlyPlaceBlock(
      World worldIn, StructureBoundingBox boundingboxIn, Random rand, float chance, int x, int y, int z, IBlockState blockstateIn
   ) {
      if (rand.nextFloat() < chance) {
         this.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
      }
   }

   protected void clearCurrentPositionBlocksUpwards(World worldIn, int x, int y, int z, StructureBoundingBox structurebb) {
      BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (structurebb.isVecInside(blockPos)) {
         while (!worldIn.isAirBlock(blockPos) && blockPos.getY() < 255) {
            worldIn.setBlockState(blockPos, Blocks.air.getDefaultState(), 2);
            blockPos = blockPos.up();
         }
      }
   }

   protected int getYWithOffset(int y) {
      return this.coordBaseMode == null ? y : y + this.boundingBox.minY;
   }

   public abstract static class BlockSelector {
      protected IBlockState blockstate = Blocks.air.getDefaultState();

      public IBlockState getBlockState() {
         return this.blockstate;
      }

      public abstract void handleRandom(Random random, boolean flag);
   }
}
