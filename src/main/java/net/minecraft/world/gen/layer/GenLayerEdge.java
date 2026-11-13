package net.minecraft.world.gen.layer;

public class GenLayerEdge extends GenLayer {
   private final GenLayerEdge.Mode mode;

   public GenLayerEdge(long seed, GenLayer parent, GenLayerEdge.Mode mode) {
      super(seed);
      this.parent = parent;
      this.mode = mode;
   }

   private int[] getIntsHeatIce(int areaX, int areaY, int areaWidth, int areaHeight) {
      int innerIndex = areaX - 1;
      int i = areaY - 1;
      int i2 = 1 + areaWidth + 1;
      int i3 = 1 + areaHeight + 1;
      int[] ints = this.parent.getInts(innerIndex, i, i2, i3);
      int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i4 = 0; i4 < areaHeight; i4++) {
         for (int i5 = 0; i5 < areaWidth; i5++) {
            int i6 = ints[i5 + 1 + (i4 + 1) * i2];
            if (i6 == 4) {
               int i7 = ints[i5 + 1 + (i4 + 1 - 1) * i2];
               int i8 = ints[i5 + 1 + 1 + (i4 + 1) * i2];
               int i9 = ints[i5 + 1 - 1 + (i4 + 1) * i2];
               int i10 = ints[i5 + 1 + (i4 + 1 + 1) * i2];
               boolean flag = i7 == 2 || i8 == 2 || i9 == 2 || i10 == 2;
               if (i7 == 1 || i8 == 1 || i9 == 1 || i10 == 1 || flag) {
                  i6 = 3;
               }
            }

            intCache[i5 + i4 * areaWidth] = i6;
         }
      }

      return intCache;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      switch (this.mode) {
         case COOL_WARM:
         default:
            return this.getIntsCoolWarm(areaX, areaY, areaWidth, areaHeight);
         case HEAT_ICE:
            return this.getIntsHeatIce(areaX, areaY, areaWidth, areaHeight);
         case SPECIAL:
            return this.getIntsSpecial(areaX, areaY, areaWidth, areaHeight);
      }
   }

   private int[] getIntsSpecial(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            int i = intArray[innerIndex + index * areaWidth];
            if (i != 0 && this.nextInt(13) == 0) {
               i |= 1 + this.nextInt(15) << 8 & 3840;
            }

            intArray1[innerIndex + index * areaWidth] = i;
         }
      }

      return intArray1;
   }

   private int[] getIntsCoolWarm(int areaX, int areaY, int areaWidth, int areaHeight) {
      int i = areaX - 1;
      int i2 = areaY - 1;
      int i3 = 1 + areaWidth + 1;
      int i4 = 1 + areaHeight + 1;
      int[] intArray = this.parent.getInts(i, i2, i3, i4);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            int i5 = intArray[innerIndex + 1 + (index + 1) * i3];
            if (i5 == 1) {
               int i6 = intArray[innerIndex + 1 + (index + 1 - 1) * i3];
               int i7 = intArray[innerIndex + 1 + 1 + (index + 1) * i3];
               int i8 = intArray[innerIndex + 1 - 1 + (index + 1) * i3];
               int i9 = intArray[innerIndex + 1 + (index + 1 + 1) * i3];
               boolean flag = i6 == 3 || i7 == 3 || i8 == 3 || i9 == 3;
               boolean flag2 = i6 == 4 || i7 == 4 || i8 == 4 || i9 == 4;
               if (flag || flag2) {
                  i5 = 2;
               }
            }

            intArray1[innerIndex + index * areaWidth] = i5;
         }
      }

      return intArray1;
   }

   public enum Mode {
      COOL_WARM,
      HEAT_ICE,
      SPECIAL;
   }
}
