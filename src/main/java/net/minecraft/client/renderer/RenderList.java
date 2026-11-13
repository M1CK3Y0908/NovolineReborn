package net.minecraft.client.renderer;

import java.nio.IntBuffer;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;

public class RenderList extends ChunkRenderContainer {
   private double viewEntityY;
   private double viewEntityX;
   IntBuffer bufferLists = GLAllocation.createDirectIntBuffer(16);
   private double viewEntityZ;

   private void drawRegion(int arg1, int arg2, IntBuffer arg3) {
      GlStateManager.pushMatrix();
      this.handleInt(arg1, arg2);
      arg3.flip();
      GlStateManager.callLists(arg3);
      arg3.clear();
      GlStateManager.popMatrix();
   }

   @Override
   public void renderChunkLayer(EnumWorldBlockLayer layer) {
      if (this.initialized) {
         if (!Config.isRenderRegions()) {
            for (RenderChunk renderChunk : this.renderChunks) {
               ListedRenderChunk listedRenderChunk = (ListedRenderChunk)renderChunk;
               GlStateManager.pushMatrix();
               this.preRenderChunk(renderChunk);
               GL11.glCallList(listedRenderChunk.getDisplayList(layer, listedRenderChunk.getCompiledChunk()));
               GlStateManager.popMatrix();
            }
         } else {
            int i = Integer.MIN_VALUE;
            int i2 = Integer.MIN_VALUE;

            for (RenderChunk renderChunk2 : this.renderChunks) {
               ListedRenderChunk listedRenderChunk2 = (ListedRenderChunk)renderChunk2;
               if (i != renderChunk2.regionX || i2 != renderChunk2.regionZ) {
                  if (this.bufferLists.position() > 0) {
                     this.drawRegion(i, i2, this.bufferLists);
                  }

                  i = renderChunk2.regionX;
                  i2 = renderChunk2.regionZ;
               }

               if (this.bufferLists.position() >= this.bufferLists.capacity()) {
                  IntBuffer directIntBuffer = GLAllocation.createDirectIntBuffer(this.bufferLists.capacity() * 2);
                  this.bufferLists.flip();
                  directIntBuffer.put(this.bufferLists);
                  this.bufferLists = directIntBuffer;
               }

               this.bufferLists.put(listedRenderChunk2.getDisplayList(layer, listedRenderChunk2.getCompiledChunk()));
            }

            if (this.bufferLists.position() > 0) {
               this.drawRegion(i, i2, this.bufferLists);
            }
         }

         if (Config.isMultiTexture()) {
            GlStateManager.bindCurrentTexture();
         }

         GlStateManager.resetColor();
         this.renderChunks.clear();
      }
   }

   public void handleInt(int i, int i2) {
      GlStateManager.translate((float)(i - this.viewEntityX), (float)(0 - this.viewEntityY), (float)(i2 - this.viewEntityZ));
   }

   @Override
   public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
      this.viewEntityX = viewEntityXIn;
      this.viewEntityY = viewEntityYIn;
      this.viewEntityZ = viewEntityZIn;
      super.initialize(viewEntityXIn, viewEntityYIn, viewEntityZIn);
   }
}
