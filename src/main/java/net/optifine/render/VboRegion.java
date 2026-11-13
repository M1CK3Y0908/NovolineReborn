package net.optifine.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.util.LinkedList;

public class VboRegion {
   private EnumWorldBlockLayer layer = null;
   private int glBufferId = OpenGlHelper.glGenBuffers();
   private int capacity = 4096;
   private int sizeUsed;
   private int positionTop = 0;
   private LinkedList<VboRange> rangeList = new LinkedList<>();
   private VboRange compactRangeLast = null;
   private IntBuffer bufferIndexVertex = Config.createDirectIntBuffer(this.capacity);
   private IntBuffer bufferCountVertex = Config.createDirectIntBuffer(this.capacity);
   private int drawMode = 7;
   private final int vertexBytes = DefaultVertexFormats.BLOCK.getNextOffset();

   public void bindBuffer() {
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
   }

   public int getPositionTop() {
      return this.positionTop;
   }

   public void unbindBuffer() {
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
   }

   public void drawArrays(int drawMode, VboRange range) {
      if (this.drawMode != drawMode) {
         if (this.bufferIndexVertex.position() > 0) {
            throw new IllegalArgumentException("Mixed region draw modes: " + this.drawMode + " != " + drawMode);
         }

         this.drawMode = drawMode;
      }

      this.bufferIndexVertex.put(range.getPosition());
      this.bufferCountVertex.put(range.getSize());
   }

   private void checkVboSize(int sizeMin) {
      if (this.capacity < sizeMin) {
         this.expandVbo(sizeMin);
      }
   }

   public void bufferData(ByteBuffer data, VboRange range) {
      int position2 = range.getPosition();
      int size2 = range.getSize();
      int i = this.toVertex(data.limit());
      if (i <= 0) {
         if (position2 >= 0) {
            range.setPosition(-1);
            range.setSize(0);
            this.rangeList.remove(range.getNode());
            this.sizeUsed -= size2;
         }
      } else {
         if (i > size2) {
            range.setPosition(this.positionTop);
            range.setSize(i);
            this.positionTop += i;
            if (position2 >= 0) {
               this.rangeList.remove(range.getNode());
            }

            this.rangeList.addLast(range.getNode());
         }

         range.setSize(i);
         this.sizeUsed += i - size2;
         this.checkVboSize(range.getPositionNext());
         long l = this.toBytes(range.getPosition());
         this.bindBuffer();
         OpenGlHelper.glBufferSubData(OpenGlHelper.GL_ARRAY_BUFFER, l, data);
         this.unbindBuffer();
         if (this.positionTop > this.sizeUsed * 11 / 10) {
            this.checkRanges();
         }
      }
   }

   private void checkRanges() {
      if (!this.rangeList.isEmpty()) {
         VboRange compactRangeLast2 = this.compactRangeLast;
         if (compactRangeLast2 == null || !this.rangeList.contains(compactRangeLast2.getNode())) {
            compactRangeLast2 = this.rangeList.getFirst().getItem();
         }

         compactRangeLast2.getPosition();
         VboRange prev = compactRangeLast2.getPrev();
         int i;
         if (prev == null) {
            i = 0;
         } else {
            i = prev.getPositionNext();
         }

         int i2 = 0;

         while (compactRangeLast2 != null && i2 < 1) {
            i2++;
            if (compactRangeLast2.getPosition() == i) {
               i += compactRangeLast2.getSize();
               compactRangeLast2 = compactRangeLast2.getNext();
            } else {
               int i3 = compactRangeLast2.getPosition() - i;
               if (compactRangeLast2.getSize() <= i3) {
                  this.copyVboData(compactRangeLast2.getPosition(), i, compactRangeLast2.getSize());
                  compactRangeLast2.setPosition(i);
                  i += compactRangeLast2.getSize();
                  compactRangeLast2 = compactRangeLast2.getNext();
               } else {
                  this.checkVboSize(this.positionTop + compactRangeLast2.getSize());
                  this.copyVboData(compactRangeLast2.getPosition(), this.positionTop, compactRangeLast2.getSize());
                  compactRangeLast2.setPosition(this.positionTop);
                  this.positionTop = this.positionTop + compactRangeLast2.getSize();
                  VboRange next = compactRangeLast2.getNext();
                  this.rangeList.remove(compactRangeLast2.getNode());
                  this.rangeList.addLast(compactRangeLast2.getNode());
                  compactRangeLast2 = next;
               }
            }
         }

         if (compactRangeLast2 == null) {
            this.positionTop = this.rangeList.getLast().getItem().getPositionNext();
         }

         this.compactRangeLast = compactRangeLast2;
      }
   }

   public void finishDraw(VboRenderList vboRenderList) {
      this.bindBuffer();
      vboRenderList.setupArrayPointers();
      this.bufferIndexVertex.flip();
      this.bufferCountVertex.flip();
      GlStateManager.glMultiDrawArrays(this.drawMode, this.bufferIndexVertex, this.bufferCountVertex);
      this.bufferIndexVertex.limit(this.bufferIndexVertex.capacity());
      this.bufferCountVertex.limit(this.bufferCountVertex.capacity());
      if (this.positionTop > this.sizeUsed * 11 / 10) {
         this.checkRanges();
      }
   }

   private void copyVboData(int posFrom, int posTo, int size) {
      long l = this.toBytes(posFrom);
      long l2 = this.toBytes(posTo);
      long l3 = this.toBytes(size);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, this.glBufferId);
      OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, l, l2, l3);
      Config.checkGlError("Copy VBO range");
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
   }

   public void deleteGlBuffers() {
      if (this.glBufferId >= 0) {
         OpenGlHelper.glDeleteBuffers(this.glBufferId);
         this.glBufferId = -1;
      }
   }

   private int toVertex(long bytes) {
      return (int)(bytes / this.vertexBytes);
   }

   private void expandVbo(int sizeMin) {
      int i = this.capacity * 6 / 4;

      while (i < sizeMin) {
         i = i * 6 / 4;
      }

      long l = this.toBytes(this.capacity);
      long l2 = this.toBytes(i);
      int i2 = OpenGlHelper.glGenBuffers();
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, i2);
      OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, l2, OpenGlHelper.GL_STATIC_DRAW);
      Config.checkGlError("Expand VBO");
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, i2);
      OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, 0L, 0L, l);
      Config.checkGlError("Copy VBO: " + l2);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
      OpenGlHelper.glDeleteBuffers(this.glBufferId);
      this.bufferIndexVertex = Config.createDirectIntBuffer(i);
      this.bufferCountVertex = Config.createDirectIntBuffer(i);
      this.glBufferId = i2;
      this.capacity = i;
   }

   public VboRegion(EnumWorldBlockLayer layer) {
      this.layer = layer;
      this.bindBuffer();
      long l = this.toBytes(this.capacity);
      OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, l, OpenGlHelper.GL_STATIC_DRAW);
      this.unbindBuffer();
   }

   private long toBytes(int vertex) {
      return (long)vertex * this.vertexBytes;
   }
}
