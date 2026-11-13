package net.minecraft.client.renderer.vertex;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.optifine.render.VboRange;
import net.optifine.render.VboRegion;
import org.lwjgl.opengl.GL11;

public class VertexBuffer {
   private final VertexFormat vertexFormat;
   private VboRegion vboRegion;
   private int count;
   private VboRange vboRange;
   private int glBufferId;
   private int drawMode;

   public void setVboRegion(VboRegion arg1) {
      if (arg1 != null) {
         this.deleteGlBuffers();
         this.vboRegion = arg1;
         this.vboRange = new VboRange();
      }
   }

   public void deleteGlBuffers() {
      if (this.glBufferId >= 0) {
         OpenGlHelper.glDeleteBuffers(this.glBufferId);
         this.glBufferId = -1;
      }
   }

   public void bufferData(ByteBuffer byteBuffer) {
      if (this.vboRegion != null) {
         this.vboRegion.bufferData(byteBuffer, this.vboRange);
      } else {
         this.bindBuffer();
         OpenGlHelper.handleInt5(OpenGlHelper.GL_ARRAY_BUFFER, byteBuffer);
         this.unbindBuffer();
         this.count = byteBuffer.limit() / this.vertexFormat.getNextOffset();
      }
   }

   public VertexBuffer(VertexFormat vertexFormatIn) {
      this.vertexFormat = vertexFormatIn;
      this.glBufferId = OpenGlHelper.glGenBuffers();
   }

   public VboRegion getVboRegion() {
      return this.vboRegion;
   }

   public void bindBuffer() {
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
   }

   public void setDrawMode(int arg1) {
      this.drawMode = arg1;
   }

   public VboRange getVboRange() {
      return this.vboRange;
   }

   public void drawArrays(int mode) {
      if (this.drawMode > 0) {
         mode = this.drawMode;
      }

      if (this.vboRegion != null) {
         this.vboRegion.drawArrays(mode, this.vboRange);
      } else {
         GL11.glDrawArrays(mode, 0, this.count);
      }
   }

   public void unbindBuffer() {
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
   }

   public int getDrawMode() {
      return this.drawMode;
   }
}
