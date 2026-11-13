package net.minecraft.client.resources.data;

public class FontMetadataSection implements IMetadataSection {
   private final float[] charWidths;
   private final float[] charLefts;
   private final float[] charSpacings;

   public FontMetadataSection(float[] fArray, float[] fArray2, float[] fArray3) {
      this.charWidths = fArray;
      this.charLefts = fArray2;
      this.charSpacings = fArray3;
   }
}
