package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.render.VboRegion;
import net.optifine.shaders.ShadersRender;
import org.lwjgl.opengl.GL11;

public class VboRenderList extends ChunkRenderContainer {
   private double viewEntityZ;
   private double viewEntityX;
   private double viewEntityY;

   public void setupArrayPointers() {
      if (Config.isShaders()) {
         ShadersRender.setupArrayPointersVbo();
      } else {
         GL11.glVertexPointer(3, 5126, 28, 0L);
         GL11.glColorPointer(4, 5121, 28, 12L);
         GL11.glTexCoordPointer(2, 5126, 28, 16L);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glTexCoordPointer(2, 5122, 28, 24L);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      }
   }

   @Override
   public void renderChunkLayer(EnumWorldBlockLayer layer) {
      if (this.initialized) {
         if (!Config.isRenderRegions()) {
            for (RenderChunk renderChunk : this.renderChunks) {
               VertexBuffer vertexBufferByLayer = renderChunk.getVertexBufferByLayer(layer.ordinal());
               GlStateManager.pushMatrix();
               this.preRenderChunk(renderChunk);
               renderChunk.multModelviewMatrix();
               vertexBufferByLayer.bindBuffer();
               this.setupArrayPointers();
               vertexBufferByLayer.drawArrays(7);
               GlStateManager.popMatrix();
            }
         } else {
            int i = Integer.MIN_VALUE;
            int i2 = Integer.MIN_VALUE;
            VboRegion vboRegion = null;

            for (RenderChunk renderChunk2 : this.renderChunks) {
               VertexBuffer vertexBufferByLayer2 = renderChunk2.getVertexBufferByLayer(layer.ordinal());
               VboRegion vboRegion2 = vertexBufferByLayer2.getVboRegion();
               if (vboRegion2 != vboRegion || i != renderChunk2.regionX || i2 != renderChunk2.regionZ) {
                  if (vboRegion != null) {
                     this.drawRegion(i, i2, vboRegion);
                  }

                  i = renderChunk2.regionX;
                  i2 = renderChunk2.regionZ;
                  vboRegion = vboRegion2;
               }

               vertexBufferByLayer2.drawArrays(7);
            }

            if (vboRegion != null) {
               this.drawRegion(i, i2, vboRegion);
            }
         }

         OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
         GlStateManager.resetColor();
         this.renderChunks.clear();
      }
   }

   @Override
   public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
      this.viewEntityX = viewEntityXIn;
      this.viewEntityY = viewEntityYIn;
      this.viewEntityZ = viewEntityZIn;
      super.initialize(viewEntityXIn, viewEntityYIn, viewEntityZIn);
   }

   private void drawRegion(int arg1, int arg2, VboRegion arg3) {
      GlStateManager.pushMatrix();
      this.handleInt(arg1, arg2);
      arg3.finishDraw(this);
      GlStateManager.popMatrix();
   }

   public void handleInt(int i, int i2) {
      GlStateManager.translate((float)(i - this.viewEntityX), (float)(0 - this.viewEntityY), (float)(i2 - this.viewEntityZ));
   }
}
