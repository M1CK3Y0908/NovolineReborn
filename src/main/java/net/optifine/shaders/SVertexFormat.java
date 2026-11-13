package net.optifine.shaders;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormat {
   public static final VertexFormat defVertexFormatTextured = makeDefVertexFormatTextured();
   public static final int offsetTangent = 10;
   public static final int offsetEntity = 12;
   public static final int offsetMidTexCoord = 8;
   public static final int vertexSizeBlock = 14;

   public static VertexFormat duplicate(VertexFormat src) {
      if (src == null) {
         return null;
      }

      VertexFormat vertexFormat = new VertexFormat();
      copy(src, vertexFormat);
      return vertexFormat;
   }

   public static VertexFormat makeDefVertexFormatItem() {
      VertexFormat vertexFormat = new VertexFormat();
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.POSITION, 3));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.COLOR, 4));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.NORMAL, 3));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 1));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      return vertexFormat;
   }

   public static VertexFormat makeDefVertexFormatTextured() {
      VertexFormat vertexFormat = new VertexFormat();
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.POSITION, 3));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.NORMAL, 3));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 1));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      return vertexFormat;
   }

   public static VertexFormat makeDefVertexFormatBlock() {
      VertexFormat vertexFormat = new VertexFormat();
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.POSITION, 3));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.COLOR, 4));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(1, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.NORMAL, 3));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 1));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      return vertexFormat;
   }

   public static void setDefBakedFormat(VertexFormat vf) {
      if (vf != null) {
         vf.clear();
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.POSITION, 3));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.COLOR, 4));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.UV, 2));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 2));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.NORMAL, 3));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 1));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 2));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
         vf.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.PADDING, 4));
      }
   }

   public static void copy(VertexFormat src, VertexFormat dst) {
      if (src != null && dst != null) {
         dst.clear();

         for (int index = 0; index < src.getElementCount(); index++) {
            dst.addElement(src.getElement(index));
         }
      }
   }
}
