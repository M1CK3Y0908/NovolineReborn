package wtf.novoline.utils.render;

import net.minecraft.client.renderer.WorldRenderer;

public class WorldRendererWrapper {
   private WorldRenderer worldRenderer;

   public WorldRendererWrapper(WorldRenderer worldRenderer2) {
      this.worldRenderer = worldRenderer2;
   }

   public VertexFormatWrapper getVertexFormatWrapper() {
      return new VertexFormatWrapper(this.worldRenderer.getVertexFormat());
   }

   public void noColor() {
      this.worldRenderer.noColor();
   }

   public void putNormal(float f, float f2, float f3) {
      this.worldRenderer.putNormal(f, f2, f3);
   }

   public WorldRendererWrapper getWorldRendererWrapper(WorldRenderer worldRenderer2) {
      this.worldRenderer = worldRenderer2;
      return this;
   }

   public void addVertex(double d, double d2, double d3) {
      this.worldRenderer.setTranslation(d, d2, d3);
   }
}
