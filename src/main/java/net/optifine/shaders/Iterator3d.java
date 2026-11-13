package net.optifine.shaders;

import java.util.Iterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.optifine.BlockPosM;

public class Iterator3d implements Iterator<BlockPos> {
   private BlockPosM blockPos = new BlockPosM(0, 0, 0);
   private static final int AXIS_Y = 1;
   private int axis = 0;
   private int kX;
   private static final int AXIS_Z = 2;
   private static final int AXIS_X = 0;
   private int kY;
   private int kZ;
   private IteratorAxis iteratorAxis;

   private BlockPos reverseCoord(BlockPos pos, boolean revX, boolean revY, boolean revZ) {
      if (revX) {
         pos = new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
      }

      if (revY) {
         pos = new BlockPos(pos.getX(), -pos.getY(), pos.getZ());
      }

      if (revZ) {
         pos = new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
      }

      return pos;
   }

   public Iterator3d(BlockPos posStart, BlockPos posEnd, int width, int height) {
      boolean flag = posStart.getX() > posEnd.getX();
      boolean flag2 = posStart.getY() > posEnd.getY();
      boolean flag3 = posStart.getZ() > posEnd.getZ();
      posStart = this.reverseCoord(posStart, flag, flag2, flag3);
      posEnd = this.reverseCoord(posEnd, flag, flag2, flag3);
      this.kX = flag ? -1 : 1;
      this.kY = flag2 ? -1 : 1;
      this.kZ = flag3 ? -1 : 1;
      Vec3 vec3 = new Vec3(posEnd.getX() - posStart.getX(), posEnd.getY() - posStart.getY(), posEnd.getZ() - posStart.getZ()).normalize();
      Vec3 vec32 = new Vec3(1.0, 0.0, 0.0);
      double d = vec3.dotProduct(vec32);
      double d2 = Math.abs(d);
      Vec3 vec33 = new Vec3(0.0, 1.0, 0.0);
      double d3 = vec3.dotProduct(vec33);
      double d4 = Math.abs(d3);
      Vec3 vec34 = new Vec3(0.0, 0.0, 1.0);
      double d5 = vec3.dotProduct(vec34);
      double d6 = Math.abs(d5);
      if (d6 >= d4 && d6 >= d2) {
         this.axis = 2;
         BlockPos blockPos2 = new BlockPos(posStart.getZ(), posStart.getY() - width, posStart.getX() - height);
         BlockPos blockPos3 = new BlockPos(posEnd.getZ(), posStart.getY() + width + 1, posStart.getX() + height + 1);
         int i = posEnd.getZ() - posStart.getZ();
         double d7 = (posEnd.getY() - posStart.getY()) / (1.0 * i);
         double d8 = (posEnd.getX() - posStart.getX()) / (1.0 * i);
         this.iteratorAxis = new IteratorAxis(blockPos2, blockPos3, d7, d8);
      } else if (d4 >= d2 && d4 >= d6) {
         this.axis = 1;
         BlockPos blockPos4 = new BlockPos(posStart.getY(), posStart.getX() - width, posStart.getZ() - height);
         BlockPos blockPos5 = new BlockPos(posEnd.getY(), posStart.getX() + width + 1, posStart.getZ() + height + 1);
         int i2 = posEnd.getY() - posStart.getY();
         double d9 = (posEnd.getX() - posStart.getX()) / (1.0 * i2);
         double d10 = (posEnd.getZ() - posStart.getZ()) / (1.0 * i2);
         this.iteratorAxis = new IteratorAxis(blockPos4, blockPos5, d9, d10);
      } else {
         this.axis = 0;
         BlockPos blockPos6 = new BlockPos(posStart.getX(), posStart.getY() - width, posStart.getZ() - height);
         BlockPos blockPos7 = new BlockPos(posEnd.getX(), posStart.getY() + width + 1, posStart.getZ() + height + 1);
         int i3 = posEnd.getX() - posStart.getX();
         double d11 = (posEnd.getY() - posStart.getY()) / (1.0 * i3);
         double d12 = (posEnd.getZ() - posStart.getZ()) / (1.0 * i3);
         this.iteratorAxis = new IteratorAxis(blockPos6, blockPos7, d11, d12);
      }
   }

   @Override
   public boolean hasNext() {
      return this.iteratorAxis.hasNext();
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not supported");
   }

   public BlockPos next() {
      BlockPos blockPos = this.iteratorAxis.next();
      switch (this.axis) {
         case 0:
            this.blockPos.setXyz(blockPos.getX() * this.kX, blockPos.getY() * this.kY, blockPos.getZ() * this.kZ);
            return this.blockPos;
         case 1:
            this.blockPos.setXyz(blockPos.getY() * this.kX, blockPos.getX() * this.kY, blockPos.getZ() * this.kZ);
            return this.blockPos;
         case 2:
            this.blockPos.setXyz(blockPos.getZ() * this.kX, blockPos.getY() * this.kY, blockPos.getX() * this.kZ);
            return this.blockPos;
         default:
            this.blockPos.setXyz(blockPos.getX() * this.kX, blockPos.getY() * this.kY, blockPos.getZ() * this.kZ);
            return this.blockPos;
      }
   }
}
