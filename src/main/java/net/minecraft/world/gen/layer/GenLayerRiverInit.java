package net.minecraft.world.gen.layer;

public class GenLayerRiverInit extends GenLayer {
   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            intArray1[innerIndex + index * areaWidth] = intArray[innerIndex + index * areaWidth] > 0 ? this.nextInt(299999) + 2 : 0;
         }
      }

      return intArray1;
   }

   public GenLayerRiverInit(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }
}
