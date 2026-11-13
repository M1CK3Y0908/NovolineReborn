package net.minecraft.world.gen.layer;

public class GenLayerVoronoiZoom extends GenLayer {
   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      areaX -= 2;
      areaY -= 2;
      int index = areaX >> 2;
      int i = areaY >> 2;
      int i2 = (areaWidth >> 2) + 2;
      int i3 = (areaHeight >> 2) + 2;
      int[] ints = this.parent.getInts(index, i, i2, i3);
      int i4 = i2 - 1 << 2;
      int i5 = i3 - 1 << 2;
      int[] intCache = IntCache.getIntCache(i4 * i5);

      for (int i6 = 0; i6 < i3 - 1; i6++) {
         int i7 = 0;
         int i8 = ints[0 + (i6 + 0) * i2];
         int i9 = ints[0 + (i6 + 1) * i2];

         while (i7 < i2 - 1) {
            this.initChunkSeed(i7 + index << 2, i6 + i << 2);
            double d = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double d2 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(i7 + index + 1 << 2, i6 + i << 2);
            double d3 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d4 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(i7 + index << 2, i6 + i + 1 << 2);
            double d5 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double d6 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            this.initChunkSeed(i7 + index + 1 << 2, i6 + i + 1 << 2);
            double d7 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d8 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            int i10 = ints[i7 + 1 + (i6 + 0) * i2] & 0xFF;
            int i11 = ints[i7 + 1 + (i6 + 1) * i2] & 0xFF;

            for (int i12 = 0; i12 < 4; i12++) {
               int i13 = ((i6 << 2) + i12) * i4 + (i7 << 2);

               for (int i14 = 0; i14 < 4; i14++) {
                  double d9 = (i12 - d2) * (i12 - d2) + (i14 - d) * (i14 - d);
                  double d10 = (i12 - d4) * (i12 - d4) + (i14 - d3) * (i14 - d3);
                  double d11 = (i12 - d6) * (i12 - d6) + (i14 - d5) * (i14 - d5);
                  double d12 = (i12 - d8) * (i12 - d8) + (i14 - d7) * (i14 - d7);
                  if (d9 < d10 && d9 < d11 && d9 < d12) {
                     intCache[i13++] = i8;
                  } else if (d10 < d9 && d10 < d11 && d10 < d12) {
                     intCache[i13++] = i10;
                  } else if (d11 < d9 && d11 < d10 && d11 < d12) {
                     intCache[i13++] = i9;
                  } else {
                     intCache[i13++] = i11;
                  }
               }
            }

            i8 = i10;
            i9 = i11;
            i7++;
         }
      }

      int[] intCache2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i15 = 0; i15 < areaHeight; i15++) {
         System.arraycopy(intCache, (i15 + (areaY & 3)) * i4 + (areaX & 3), intCache2, i15 * areaWidth, areaWidth);
      }

      return intCache2;
   }

   public GenLayerVoronoiZoom(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
   }
}
