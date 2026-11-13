package wtf.novoline.utils.render;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class VertexElementWrapper {
   private VertexFormatElement vertexFormatElement;

   public int getElementCount() {
      return this.vertexFormatElement.getElementCount();
   }

   @Override
   public boolean equals(Object object) {
      return this.vertexFormatElement.equals(object);
   }

   public VertexElementWrapper(VertexFormatElement vertexFormatElement2) {
      this.vertexFormatElement = vertexFormatElement2;
   }

   public int getSize() {
      return this.vertexFormatElement.getSize();
   }

   public VertexFormatElement getVertexFormatElement() {
      return this.vertexFormatElement;
   }

   @Override
   public int hashCode() {
      return this.vertexFormatElement.hashCode();
   }

   public int getIndex() {
      return this.vertexFormatElement.getIndex();
   }

   @Override
   public String toString() {
      return this.vertexFormatElement.toString();
   }
}
