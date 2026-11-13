package net.minecraft.world.gen.layer;

public class GenLayerIsland extends GenLayer {
   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(areaX + innerIndex, areaY + index);
            intArray[innerIndex + index * areaWidth] = this.nextInt(10) == 0 ? 1 : 0;
         }
      }

      if (areaX > -areaWidth && areaX <= 0 && areaY > -areaHeight && areaY <= 0) {
         intArray[-areaX + -areaY * areaWidth] = 1;
      }

      return intArray;
   }

   public GenLayerIsland(long l) {
      super(l);
   }
}
