package net.minecraft.client.renderer;


public class StitcherException extends RuntimeException {
   private final net.minecraft.client.renderer.texture.Stitcher.Holder holder;

   public StitcherException(net.minecraft.client.renderer.texture.Stitcher.Holder holder2, String text) {
      super(text);
      this.holder = holder2;
   }
}
