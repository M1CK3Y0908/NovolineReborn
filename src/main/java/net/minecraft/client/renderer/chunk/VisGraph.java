package net.minecraft.client.renderer.chunk;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;

public class VisGraph {
   private final BitSet opaqueBlocks = new BitSet(4096);
   private static final int INDEX_X = (int)Math.pow(16.0, 0.0);
   private static final int INDEX_Z = (int)Math.pow(16.0, 1.0);
   private static final int INDEX_Y = (int)Math.pow(16.0, 2.0);
   private static final int[] EDGE_INDICES = new int[1352];
   private int emptyBlockCount = 4096;

   static {
      int k = 0;

      for (int l = 0; l < 16; l++) {
         for (int i1 = 0; i1 < 16; i1++) {
            for (int j1 = 0; j1 < 16; j1++) {
               if (l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15) {
                  EDGE_INDICES[k++] = getIndex(l, i1, j1);
               }
            }
         }
      }
   }

   private static int getIndex(int x, int y, int z) {
      return x << 0 | y << 8 | z << 4;
   }

   private Set<EnumFacing> computeVisibleFacings(int startIndex) {
      EnumSet enumSet = EnumSet.noneOf(EnumFacing.class);
      ArrayDeque arrayDeque = new ArrayDeque(384);
      arrayDeque.add(IntegerCache.getInteger(startIndex));
      this.opaqueBlocks.set(startIndex, true);

      while (!arrayDeque.isEmpty()) {
         int i = (Integer)arrayDeque.poll();
         this.addEdges(i, enumSet);

         for (EnumFacing enumFacing : EnumFacing.VALUES) {
            int neighborIndexAtFace = this.getNeighborIndexAtFace(i, enumFacing);
            if (neighborIndexAtFace >= 0 && !this.opaqueBlocks.get(neighborIndexAtFace)) {
               this.opaqueBlocks.set(neighborIndexAtFace, true);
               arrayDeque.add(IntegerCache.getInteger(neighborIndexAtFace));
            }
         }
      }

      return enumSet;
   }

   private int getNeighborIndexAtFace(int index, EnumFacing facing) {
      switch (facing) {
         case DOWN:
            if ((index >> 8 & 15) == 0) {
               return -1;
            }

            return index - INDEX_Y;
         case UP:
            if ((index >> 8 & 15) == 15) {
               return -1;
            }

            return index + INDEX_Y;
         case NORTH:
            if ((index >> 4 & 15) == 0) {
               return -1;
            }

            return index - INDEX_Z;
         case SOUTH:
            if ((index >> 4 & 15) == 15) {
               return -1;
            }

            return index + INDEX_Z;
         case WEST:
            if ((index >> 0 & 15) == 0) {
               return -1;
            }

            return index - INDEX_X;
         case EAST:
            if ((index >> 0 & 15) == 15) {
               return -1;
            }

            return index + INDEX_X;
         default:
            return -1;
      }
   }

   public SetVisibility computeVisibility() {
      SetVisibility setVisibility = new SetVisibility();
      if (4096 - this.emptyBlockCount < 256) {
         setVisibility.setAllVisible(true);
      } else if (this.emptyBlockCount == 0) {
         setVisibility.setAllVisible(false);
      } else {
         for (int i : EDGE_INDICES) {
            if (!this.opaqueBlocks.get(i)) {
               setVisibility.setManyVisible(this.computeVisibleFacings(i));
            }
         }
      }

      return setVisibility;
   }

   private static int getIndex(BlockPos pos) {
      return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
   }

   public void setOpaqueCube(BlockPos pos) {
      this.opaqueBlocks.set(getIndex(pos), true);
      this.emptyBlockCount--;
   }

   private void addEdges(int index, Set<EnumFacing> facings) {
      int i = index >> 0 & 15;
      if (i == 0) {
         facings.add(EnumFacing.WEST);
      } else if (i == 15) {
         facings.add(EnumFacing.EAST);
      }

      int i2 = index >> 8 & 15;
      if (i2 == 0) {
         facings.add(EnumFacing.DOWN);
      } else if (i2 == 15) {
         facings.add(EnumFacing.UP);
      }

      int i3 = index >> 4 & 15;
      if (i3 == 0) {
         facings.add(EnumFacing.NORTH);
      } else if (i3 == 15) {
         facings.add(EnumFacing.SOUTH);
      }
   }
}
