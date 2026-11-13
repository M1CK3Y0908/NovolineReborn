package net.minecraft.world.gen.layer;

public class GenLayerZoom extends GenLayer {
   public GenLayerZoom(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int i = areaX >> 1;
      int i2 = areaY >> 1;
      int i3 = (areaWidth >> 1) + 2;
      int i4 = (areaHeight >> 1) + 2;
      int[] intArray = this.parent.getInts(i, i2, i3, i4);
      int i5 = i3 - 1 << 1;
      int i6 = i4 - 1 << 1;
      int[] intArray1 = IntCache.getIntCache(i5 * i6);

      for (int nestedIndex = 0; nestedIndex < i4 - 1; nestedIndex++) {
         int i7 = (nestedIndex << 1) * i5;
         int i8 = 0;
         int i9 = intArray[0 + (nestedIndex + 0) * i3];
         int index = intArray[0 + (nestedIndex + 1) * i3];

         while (i8 < i3 - 1) {
            this.initChunkSeed(i8 + i << 1, nestedIndex + i2 << 1);
            int i10 = intArray[i8 + 1 + (nestedIndex + 0) * i3];
            int i11 = intArray[i8 + 1 + (nestedIndex + 1) * i3];
            intArray1[i7] = i9;
            intArray1[i7++ + i5] = this.selectRandom2(i9, index);
            intArray1[i7] = this.selectRandom2(i9, i10);
            intArray1[i7++ + i5] = this.selectModeOrRandom(i9, i10, index, i11);
            i9 = i10;
            index = i11;
            i8++;
         }
      }

      int[] intArray2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index2 = 0; index2 < areaHeight; index2++) {
         System.arraycopy(intArray1, (index2 + (areaY & 1)) * i5 + (areaX & 1), intArray2, index2 * areaWidth, areaWidth);
      }

      return intArray2;
   }

   public static GenLayer createGenLayer(GenLayer genLayer, int i) {
      GenLayer genLayer2 = genLayer;

      for (int i2 = 0; i2 < i; i2++) {
         genLayer2 = new GenLayerZoom(1000L + i2, genLayer2);
      }

      return genLayer2;
   }

   protected int selectRandom2(int valueA, int valueB) {
      return this.nextInt(2) == 0 ? valueA : valueB;
   }
}
