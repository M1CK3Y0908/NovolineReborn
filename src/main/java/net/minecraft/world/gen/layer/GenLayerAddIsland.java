package net.minecraft.world.gen.layer;

public class GenLayerAddIsland extends GenLayer {
   public GenLayerAddIsland(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

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
            int i5 = intArray[innerIndex + 0 + (index + 0) * i3];
            int i6 = intArray[innerIndex + 2 + (index + 0) * i3];
            int i7 = intArray[innerIndex + 0 + (index + 2) * i3];
            int i8 = intArray[innerIndex + 2 + (index + 2) * i3];
            int i9 = intArray[innerIndex + 1 + (index + 1) * i3];
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            if (i9 != 0 || i5 == 0 && i6 == 0 && i7 == 0 && i8 == 0) {
               if (i9 > 0 && (i5 == 0 || i6 == 0 || i7 == 0 || i8 == 0)) {
                  if (this.nextInt(5) == 0) {
                     if (i9 == 4) {
                        intArray1[innerIndex + index * areaWidth] = 4;
                     } else {
                        intArray1[innerIndex + index * areaWidth] = 0;
                     }
                  } else {
                     intArray1[innerIndex + index * areaWidth] = i9;
                  }
               } else {
                  intArray1[innerIndex + index * areaWidth] = i9;
               }
            } else {
               int i10 = 1;
               int i11 = 1;
               if (i5 != 0) {
                  i10++;
                  if (this.nextInt(1) == 0) {
                     i11 = i5;
                  }
               }

               if (i6 != 0 && this.nextInt(i10++) == 0) {
                  i11 = i6;
               }

               if (i7 != 0 && this.nextInt(i10++) == 0) {
                  i11 = i7;
               }

               if (i8 != 0 && this.nextInt(i10) == 0) {
                  i11 = i8;
               }

               if (this.nextInt(3) == 0) {
                  intArray1[innerIndex + index * areaWidth] = i11;
               } else if (i11 == 4) {
                  intArray1[innerIndex + index * areaWidth] = 4;
               } else {
                  intArray1[innerIndex + index * areaWidth] = 0;
               }
            }
         }
      }

      return intArray1;
   }
}
