package net.minecraft.world.gen.layer;

public class GenLayerFuzzyZoom extends GenLayerZoom {
   public GenLayerFuzzyZoom(long l, GenLayer genLayer) {
      super(l, genLayer);
   }

   @Override
   protected int selectModeOrRandom(int value1, int value2, int value3, int value4) {
      return this.selectRandom(value1, value2, value3, value4);
   }
}
