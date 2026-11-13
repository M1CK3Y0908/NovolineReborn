package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;

public class BlockPos extends Vec3i {
   public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
   private static final int NUM_Z_BITS = NUM_X_BITS;
   private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
   private static final int Y_SHIFT = 0 + NUM_Z_BITS;
   private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
   private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
   private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
   private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

   public BlockPos(Vec3 source) {
      this(source.xCoord, source.yCoord, source.zCoord);
   }

   public BlockPos down() {
      return this.down(1);
   }

   public BlockPos subtract(Vec3i vec) {
      return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0
         ? this
         : new BlockPos(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
   }

   public BlockPos down(int offset) {
      return this.offset(EnumFacing.DOWN, offset);
   }

   public BlockPos up() {
      return this.up(1);
   }

   public BlockPos(double x, double y, double z) {
      super(x, y, z);
   }

   public BlockPos up(int offset) {
      return this.offset(EnumFacing.UP, offset);
   }

   public BlockPos offset(EnumFacing facing, int i) {
      return i == 0
         ? this
         : new BlockPos(
            this.getX() + facing.getFrontOffsetX() * i,
            this.getY() + facing.getFrontOffsetY() * i,
            this.getZ() + facing.getFrontOffsetZ() * i
         );
   }

   public BlockPos add(double x, double y, double z) {
      return x == 0.0 && y == 0.0 && z == 0.0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   public BlockPos(Vec3i source) {
      this(source.getX(), source.getY(), source.getZ());
   }

   public BlockPos(int x, int y, int z) {
      super(x, y, z);
   }

   public static BlockPos fromLong(long serialized) {
      int i = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
      int i2 = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
      int i3 = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
      return new BlockPos(i, i2, i3);
   }

   public BlockPos south(int offset) {
      return this.offset(EnumFacing.SOUTH, offset);
   }

   public BlockPos add(int x, int y, int z) {
      return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   public BlockPos offset(EnumFacing direction) {
      return this.offset(direction, 1);
   }

   public BlockPos west(int offset) {
      return this.offset(EnumFacing.WEST, offset);
   }

   public BlockPos add(Vec3i vec) {
      return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0
         ? this
         : new BlockPos(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
   }

   public BlockPos north() {
      return this.north(1);
   }

   public BlockPos south() {
      return this.south(1);
   }

   public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
      final BlockPos blockPos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
      final BlockPos blockPos2 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
      return new Iterable<BlockPos>() {
         @Override
         public Iterator<BlockPos> iterator() {
            return new AbstractIterator<BlockPos>() {
               private BlockPos lastReturned = null;

               protected BlockPos computeNext() {
                  if (this.lastReturned == null) {
                     this.lastReturned = blockPos;
                     return this.lastReturned;
                  }

                  if (this.lastReturned.equals(blockPos2)) {
                     return (BlockPos)this.endOfData();
                  }

                  int xCoordinate = this.lastReturned.getX();
                  int yCoordinate = this.lastReturned.getY();
                  int zCoordinate = this.lastReturned.getZ();
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

                  this.lastReturned = new BlockPos(xCoordinate, yCoordinate, zCoordinate);
                  return this.lastReturned;
               }
            };
         }
      };
   }

   public BlockPos west() {
      return this.west(1);
   }

   public BlockPos east(int offset) {
      return this.offset(EnumFacing.EAST, offset);
   }

   public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
      final BlockPos blockPos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
      final BlockPos blockPos2 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
      return new Iterable<BlockPos.MutableBlockPos>() {
         @Override
         public Iterator<BlockPos.MutableBlockPos> iterator() {
            return new AbstractIterator<BlockPos.MutableBlockPos>() {
               private BlockPos.MutableBlockPos theBlockPos = null;

               protected BlockPos.MutableBlockPos computeNext() {
                  if (this.theBlockPos == null) {
                     this.theBlockPos = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                     return this.theBlockPos;
                  }

                  if (this.theBlockPos.equals(blockPos2)) {
                     return (BlockPos.MutableBlockPos)this.endOfData();
                  }

                  int xCoordinate = this.theBlockPos.getX();
                  int yCoordinate = this.theBlockPos.getY();
                  int zCoordinate = this.theBlockPos.getZ();
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

                  this.theBlockPos.x = xCoordinate;
                  this.theBlockPos.y = yCoordinate;
                  this.theBlockPos.z = zCoordinate;
                  return this.theBlockPos;
               }
            };
         }
      };
   }

   public BlockPos(Entity source) {
      this(source.posX, source.posY, source.posZ);
   }

   public BlockPos crossProduct(Vec3i vec) {
      return new BlockPos(
         this.getY() * vec.getZ() - this.getZ() * vec.getY(),
         this.getZ() * vec.getX() - this.getX() * vec.getZ(),
         this.getX() * vec.getY() - this.getY() * vec.getX()
      );
   }

   public long toLong() {
      return (this.getX() & X_MASK) << X_SHIFT | (this.getY() & Y_MASK) << Y_SHIFT | (this.getZ() & Z_MASK) << 0;
   }

   public BlockPos north(int offset) {
      return this.offset(EnumFacing.NORTH, offset);
   }

   public BlockPos east() {
      return this.east(1);
   }

   public static class MutableBlockPos extends BlockPos {
      private int x;
      private int y;
      private int z;

      @Override
      public int getY() {
         return this.y;
      }

      public MutableBlockPos() {
         this(0, 0, 0);
      }

      @Override
      public int getX() {
         return this.x;
      }

      public MutableBlockPos(int x_, int y_, int z_) {
         super(0, 0, 0);
         this.x = x_;
         this.y = y_;
         this.z = z_;
      }

      @Override
      public int getZ() {
         return this.z;
      }

      public BlockPos.MutableBlockPos set(int xIn, int yIn, int zIn) {
         this.x = xIn;
         this.y = yIn;
         this.z = zIn;
         return this;
      }
   }
}
