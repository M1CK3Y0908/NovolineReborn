package wtf.novoline.utils.render;

import net.minecraft.client.renderer.vertex.VertexFormat;

public class VertexFormatWrapper {
   private VertexFormat vertexFormat;

   public void clear() {
      this.vertexFormat.clear();
   }

   public int getElementCount() {
      return this.vertexFormat.getElementCount();
   }

   @Override
   public boolean equals(Object object) {
      return this.vertexFormat.equals(object);
   }

   public VertexFormatWrapper withVertexFormat(VertexFormat vertexFormat2) {
      this.vertexFormat = vertexFormat2;
      return this;
   }

   public int getColorOffset() {
      return this.vertexFormat.getColorOffset();
   }

   public int getNextOffset() {
      return this.vertexFormat.getNextOffset();
   }

   public int getIntegerSize() {
      return this.vertexFormat.getIntegerSize();
   }

   @Override
   public int hashCode() {
      return this.vertexFormat.hashCode();
   }

   public VertexFormatWrapper(VertexFormat vertexFormat2) {
      this.vertexFormat = vertexFormat2;
   }

   public int getNormalOffset() {
      return this.vertexFormat.getNormalOffset();
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   @Override
   public String toString() {
      return this.vertexFormat.toString();
   }

   public VertexFormatWrapper addElement(VertexElementWrapper vertexElementWrapper) {
      return this.withVertexFormat(this.vertexFormat.addElement(vertexElementWrapper.getVertexFormatElement()));
   }
}
