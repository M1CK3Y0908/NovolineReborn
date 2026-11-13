package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.SVertexBuilder;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader {
   public void draw(WorldRenderer worldRenderer) {
      if (worldRenderer.getVertexCount() > 0) {
         if (worldRenderer.getDrawMode() == 7 && Config.isQuadsToTriangles()) {
            worldRenderer.quadsToTriangles();
         }

         VertexFormat vertexFormat = worldRenderer.getVertexFormat();
         int nextOffset = vertexFormat.getNextOffset();
         ByteBuffer byteBuffer = worldRenderer.getByteBuffer();
         List elements = vertexFormat.getElements();
         boolean active = Reflector.reflectorMethod85.isActive();
         boolean active2 = Reflector.reflectorMethod86.isActive();

         for (int i = 0; i < elements.size(); i++) {
            VertexFormatElement vertexFormatElement = (VertexFormatElement)elements.get(i);
            net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage usage = vertexFormatElement.getUsage();
            if (active) {
               Reflector.handleObject(usage, Reflector.reflectorMethod85, vertexFormat, i, nextOffset, byteBuffer);
            } else {
               int glConstant = vertexFormatElement.getType().getGlConstant();
               int index = vertexFormatElement.getIndex();
               byteBuffer.position(vertexFormat.getOffset(i));
               switch (usage) {
                  case POSITION:
                     GL11.glVertexPointer(vertexFormatElement.getElementCount(), glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
                     GL11.glTexCoordPointer(vertexFormatElement.getElementCount(), glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32888);
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                     break;
                  case COLOR:
                     GL11.glColorPointer(vertexFormatElement.getElementCount(), glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32886);
                     break;
                  case NORMAL:
                     GL11.glNormalPointer(glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32885);
               }
            }
         }

         if (worldRenderer.isMultiTexture()) {
            worldRenderer.drawMultiTexture();
         } else if (Config.isShaders()) {
            SVertexBuilder.handleInt(worldRenderer.getDrawMode(), worldRenderer.getVertexCount(), worldRenderer);
         } else {
            GL11.glDrawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount());
         }

         int i2 = 0;

         for (int size2 = elements.size(); i2 < size2; i2++) {
            VertexFormatElement vertexFormatElement2 = (VertexFormatElement)elements.get(i2);
            net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage usage2 = vertexFormatElement2.getUsage();
            if (active2) {
               Reflector.handleObject(usage2, Reflector.reflectorMethod86, vertexFormat, i2, nextOffset, byteBuffer);
            } else {
               int index2 = vertexFormatElement2.getIndex();
               switch (usage2) {
                  case POSITION:
                     GL11.glDisableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index2);
                     GL11.glDisableClientState(32888);
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                     break;
                  case COLOR:
                     GL11.glDisableClientState(32886);
                     GlStateManager.resetColor();
                     break;
                  case NORMAL:
                     GL11.glDisableClientState(32885);
               }
            }
         }
      }

      worldRenderer.reset();
   }
}
