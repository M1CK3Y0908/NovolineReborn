package net.minecraft.world.gen.layer;

public class GenLayerSmooth extends GenLayer {
   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int i = areaX - 1;
      int i2 = areaY - 1;
      int i3 = areaWidth + 2;
      int i4 = areaHeight + 2;
      int[] intArray = this.parent.getInts(i, i2, i3, i4);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            int i5 = intArray[innerIndex + 0 + (index + 1) * i3];
            int i6 = intArray[innerIndex + 2 + (index + 1) * i3];
            int i7 = intArray[innerIndex + 1 + (index + 0) * i3];
            int i8 = intArray[innerIndex + 1 + (index + 2) * i3];
            int i9 = intArray[innerIndex + 1 + (index + 1) * i3];
            if (i5 == i6 && i7 == i8) {
               this.initChunkSeed(innerIndex + areaX, index + areaY);
               if (this.nextInt(2) == 0) {
                  i9 = i5;
               } else {
                  i9 = i7;
               }
            } else {
               if (i5 == i6) {
                  i9 = i5;
               }

               if (i7 == i8) {
                  i9 = i7;
               }
            }

            intArray1[innerIndex + index * areaWidth] = i9;
         }
      }

      return intArray1;
   }

   public GenLayerSmooth(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
   }
}
