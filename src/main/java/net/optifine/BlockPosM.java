package net.optifine;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

public class BlockPosM extends BlockPos {
   private BlockPosM[] facings;
   private int mx;
   private int my;
   private boolean needsUpdate;
   private int mz;
   private int level;

   public BlockPosM set(Vec3i vec) {
      this.setXyz(vec.getX(), vec.getY(), vec.getZ());
      return this;
   }

   @Override
   public int getX() {
      return this.mx;
   }

   public BlockPosM(int x, int y, int z) {
      this(x, y, z, 0);
   }

   public BlockPosM(double xIn, double yIn, double zIn) {
      this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
   }

   @Override
   public int getZ() {
      return this.mz;
   }

   public BlockPos toImmutable() {
      return new BlockPos(this.mx, this.my, this.mz);
   }

   @Override
   public BlockPos offset(EnumFacing facing) {
      if (this.level <= 0) {
         return super.offset(facing, 1);
      }

      if (this.facings == null) {
         this.facings = new BlockPosM[EnumFacing.VALUES.length];
      }

      if (this.needsUpdate) {
         this.update();
      }

      int index2 = facing.getIndex();
      BlockPosM blockPosM = this.facings[index2];
      if (blockPosM == null) {
         int i = this.mx + facing.getFrontOffsetX();
         int i2 = this.my + facing.getFrontOffsetY();
         int i3 = this.mz + facing.getFrontOffsetZ();
         blockPosM = new BlockPosM(i, i2, i3, this.level - 1);
         this.facings[index2] = blockPosM;
      }

      return blockPosM;
   }

   public void setXyz(int x, int y, int z) {
      this.mx = x;
      this.my = y;
      this.mz = z;
      this.needsUpdate = true;
   }

   private void update() {
      for (int index = 0; index < 6; index++) {
         BlockPosM blockPosM = this.facings[index];
         if (blockPosM != null) {
            EnumFacing enumFacing = EnumFacing.VALUES[index];
            int i = this.mx + enumFacing.getFrontOffsetX();
            int i2 = this.my + enumFacing.getFrontOffsetY();
            int i3 = this.mz + enumFacing.getFrontOffsetZ();
            blockPosM.setXyz(i, i2, i3);
         }
      }

      this.needsUpdate = false;
   }

   public void setXyz(double xIn, double yIn, double zIn) {
      this.setXyz(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
   }

   @Override
   public BlockPos offset(EnumFacing facing, int i) {
      return i == 1 ? this.offset(facing) : super.offset(facing, i);
   }

   public static Iterable getAllInBoxMutable(BlockPos from, BlockPos to) {
      final BlockPos blockPos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
      final BlockPos blockPos2 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
      return new Iterable() {
         @Override
         public Iterator iterator() {
            return new AbstractIterator() {
               private BlockPosM theBlockPosM = null;

               protected Object computeNext() {
                  return this.computeNext0();
               }

               protected BlockPosM computeNext0() {
                  if (this.theBlockPosM == null) {
                     this.theBlockPosM = new BlockPosM(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 3);
                     return this.theBlockPosM;
                  }

                  if (this.theBlockPosM.equals(blockPos2)) {
                     return (BlockPosM)this.endOfData();
                  }

                  int xCoordinate = this.theBlockPosM.getX();
                  int yCoordinate = this.theBlockPosM.getY();
                  int zCoordinate = this.theBlockPosM.getZ();
                  if (xCoordinate < blockPos2.getX()) {
                     xCoordinate++;
                  } else if (yCoordinate < blockPos2.getY()) {
                     xCoordinate = blockPos.getX();
                     yCoordinate++;
                  } else if (zCoordinate < blockPos2.getZ()) {
                     xCoordinate = blockPos.getX();
                     yCoordinate = blockPos.getY();
                     zCoordinate++;
                  }

                  this.theBlockPosM.setXyz(xCoordinate, yCoordinate, zCoordinate);
                  return this.theBlockPosM;
               }
            };
         }
      };
   }

   @Override
   public int getY() {
      return this.my;
   }

   public BlockPosM(int x, int y, int z, int level) {
      super(0, 0, 0);
      this.mx = x;
      this.my = y;
      this.mz = z;
      this.level = level;
   }
}
