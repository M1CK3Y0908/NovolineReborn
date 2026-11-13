package wtf.novoline.utils.render;

import java.awt.Dimension;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GLAllocation;

public class MipmapData {
   private int unusedInt;
   private IntBuffer[] intBufferArray;
   private int[][] intArray;
   private String unusedText;
   private boolean unusedFlag;
   private int[] intArray2;
   private Dimension[] dimensionArray;
   private int unusedInt2;

   public static IntBuffer[] createBuffers(Dimension[] dimensions, int[][] iArrays) {
      if (dimensions == null) {
         return null;
      }

      IntBuffer[] intBuffers = new IntBuffer[dimensions.length];

      for (int i = 0; i < dimensions.length; i++) {
         Dimension dimension = dimensions[i];
         IntBuffer directIntBuffer = GLAllocation.createDirectIntBuffer(dimension.width * dimension.height);
         int[] iArray = iArrays[i];
         directIntBuffer.clear();
         directIntBuffer.put(iArray);
         directIntBuffer.clear();
         intBuffers[i] = directIntBuffer;
      }

      return intBuffers;
   }

   public static int averageFourColors(int i, int i2, int i3, int i4) {
      int i5 = averageTwoColors(i, i2);
      int i6 = averageTwoColors(i3, i4);
      return averageTwoColors(i5, i6);
   }

   private static int averageTwoColors(int i, int i2) {
      int i3 = (i & 0xFF000000) >> 24 & 0xFF;
      int i4 = (i2 & 0xFF000000) >> 24 & 0xFF;
      int i5 = (i3 + i4) / 2;
      if (i3 == 0 && i4 == 0) {
         i3 = 1;
         i4 = 1;
      } else {
         if (i3 == 0) {
            i = i2;
            i5 /= 2;
         }

         if (i4 == 0) {
            i2 = i;
            i5 /= 2;
         }
      }

      int i6 = (i >> 16 & 0xFF) * i3;
      int i7 = (i >> 8 & 0xFF) * i3;
      int i8 = (i & 0xFF) * i3;
      int i9 = (i2 >> 16 & 0xFF) * i4;
      int i10 = (i2 >> 8 & 0xFF) * i4;
      int i11 = (i2 & 0xFF) * i4;
      int i12 = (i6 + i9) / (i3 + i4);
      int i13 = (i7 + i10) / (i3 + i4);
      int i14 = (i8 + i11) / (i3 + i4);
      return i5 << 24 | i12 << 16 | i13 << 8 | i14;
   }
}
