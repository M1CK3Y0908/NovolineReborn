package net.minecraft.world.gen.layer;

public class GenLayerAddSnow extends GenLayer {
   public GenLayerAddSnow(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int i = areaX - 1;
      int i2 = areaY - 1;
      int i3 = areaWidth + 2;
      int i4 = areaHeight + 2;
      int[] ints = this.parent.getInts(i, i2, i3, i4);
      int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i5 = 0; i5 < areaHeight; i5++) {
         for (int i6 = 0; i6 < areaWidth; i6++) {
            int i7 = ints[i6 + 1 + (i5 + 1) * i3];
            this.initChunkSeed(i6 + areaX, i5 + areaY);
            if (i7 == 0) {
               intCache[i6 + i5 * areaWidth] = 0;
            } else {
               int i8 = this.nextInt(6);
               byte b;
               if (i8 == 0) {
                  b = 4;
               } else if (i8 <= 1) {
                  b = 3;
               } else {
                  b = 1;
               }

               intCache[i6 + i5 * areaWidth] = b;
            }
         }
      }

      return intCache;
   }
}
