package net.minecraft.client.renderer.vertex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
   private final VertexFormatElement.EnumUsage usage;
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.EnumType type;
   private int index;
   private int elementCount;

   public VertexFormatElement(int indexIn, VertexFormatElement.EnumType typeIn, VertexFormatElement.EnumUsage usageIn, int count) {
      if (!this.isValidUsage(indexIn, usageIn)) {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.EnumUsage.UV;
      } else {
         this.usage = usageIn;
      }

      this.type = typeIn;
      this.index = indexIn;
      this.elementCount = count;
   }

   @Override
   public boolean equals(Object arg1) {
      if (this == arg1) {
         return true;
      } else if (arg1 != null && this.getClass() == arg1.getClass()) {
         VertexFormatElement vertexFormatElement = (VertexFormatElement)arg1;
         return this.elementCount != vertexFormatElement.elementCount
            ? false
            : (this.index != vertexFormatElement.index ? false : (this.type != vertexFormatElement.type ? false : this.usage == vertexFormatElement.usage));
      } else {
         return false;
      }
   }

   public final int getElementCount() {
      return this.elementCount;
   }

   public final int getSize() {
      return this.type.getSize() * this.elementCount;
   }

   public final VertexFormatElement.EnumUsage getUsage() {
      return this.usage;
   }

   public final boolean isPositionElement() {
      return this.usage == VertexFormatElement.EnumUsage.POSITION;
   }

   public final int getIndex() {
      return this.index;
   }

   @Override
   public int hashCode() {
      int i = this.type.hashCode();
      i = 31 * i + this.usage.hashCode();
      i = 31 * i + this.index;
      return 31 * i + this.elementCount;
   }

   @Override
   public String toString() {
      return this.elementCount + "," + this.usage.getDisplayName() + "," + this.type.getDisplayName();
   }

   public final VertexFormatElement.EnumType getType() {
      return this.type;
   }

   private final boolean isValidUsage(int indexIn, VertexFormatElement.EnumUsage usageIn) {
      return indexIn == 0 || usageIn == VertexFormatElement.EnumUsage.UV;
   }

   public enum EnumType {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String displayName;
      private final int glConstant;

      public int getGlConstant() {
         return this.glConstant;
      }

      public int getSize() {
         return this.size;
      }

      EnumType(int i, String text, int i2) {
         this.size = i;
         this.displayName = text;
         this.glConstant = i2;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }

   public enum EnumUsage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      MATRIX("Bone Matrix"),
      BLEND_WEIGHT("Blend Weight"),
      PADDING("Padding");

      private final String displayName;

      public String getDisplayName() {
         return this.displayName;
      }

      EnumUsage(String text) {
         this.displayName = text;
      }
   }
}
