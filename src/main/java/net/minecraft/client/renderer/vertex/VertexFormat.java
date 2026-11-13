package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
   private final List<VertexFormatElement> elements = Lists.newArrayList();
   private final List<Integer> offsets = Lists.newArrayList();
   private int nextOffset = 0;
   private int colorElementOffset = -1;
   private List<Integer> uvOffsetsById = Lists.newArrayList();
   private static final Logger LOGGER = LogManager.getLogger();
   private int normalElementOffset = -1;

   private boolean hasPosition() {
      int i = 0;

      for (int size2 = this.elements.size(); i < size2; i++) {
         if (this.elements.get(i).isPositionElement()) {
            return true;
         }
      }

      return false;
   }

   public int getIntegerSize() {
      return this.getNextOffset() / 4;
   }

   public boolean hasNormal() {
      return this.normalElementOffset >= 0;
   }

   public List<VertexFormatElement> getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public int getColorOffset() {
      return this.colorElementOffset;
   }

   public boolean hasColor() {
      return this.uvOffsetsById.size() - 1 >= 0;
   }

   public int getUvOffsetById(int id) {
      return this.uvOffsetsById.get(id);
   }

   public VertexFormat addElement(VertexFormatElement element) {
      if (element.isPositionElement() && this.hasPosition()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      }

      this.elements.add(element);
      this.offsets.add(this.nextOffset);
      switch (element.getUsage()) {
         case NORMAL:
            this.normalElementOffset = this.nextOffset;
            break;
         case COLOR:
            this.colorElementOffset = this.nextOffset;
            break;
         case UV:
            this.uvOffsetsById.add(element.getIndex(), this.nextOffset);
      }

      this.nextOffset = this.nextOffset + element.getSize();
      return this;
   }

   public int getNormalOffset() {
      return this.normalElementOffset;
   }

   public int getOffset(int i) {
      return this.offsets.get(i);
   }

   public int getNextOffset() {
      return this.nextOffset;
   }

   @Override
   public String toString() {
      String text = "format: " + this.elements.size() + " elements: ";

      for (int index = 0; index < this.elements.size(); index++) {
         text = text + this.elements.get(index).toString();
         if (index != this.elements.size() - 1) {
            text = text + " ";
         }
      }

      return text;
   }

   public VertexFormat(VertexFormat vertexFormatIn) {
      this();

      for (int index = 0; index < vertexFormatIn.getElementCount(); index++) {
         this.addElement(vertexFormatIn.getElement(index));
      }

      this.nextOffset = vertexFormatIn.getNextOffset();
   }

   @Override
   public boolean equals(Object arg1) {
      if (this == arg1) {
         return true;
      } else if (arg1 != null && this.getClass() == arg1.getClass()) {
         VertexFormat vertexFormat = (VertexFormat)arg1;
         return this.nextOffset != vertexFormat.nextOffset
            ? false
            : (!this.elements.equals(vertexFormat.elements) ? false : this.offsets.equals(vertexFormat.offsets));
      } else {
         return false;
      }
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorElementOffset = -1;
      this.uvOffsetsById.clear();
      this.normalElementOffset = -1;
      this.nextOffset = 0;
   }

   @Override
   public int hashCode() {
      int i = this.elements.hashCode();
      i = 31 * i + this.offsets.hashCode();
      return 31 * i + this.nextOffset;
   }

   public VertexFormatElement getElement(int index) {
      return this.elements.get(index);
   }

   public VertexFormat() {
   }
}
