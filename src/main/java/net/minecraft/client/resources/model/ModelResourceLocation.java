package net.minecraft.client.resources.model;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModelResourceLocation extends ResourceLocation {
   private final String variant;

   protected ModelResourceLocation(String... texts) {
      super(texts[0], texts[1]);
      this.variant = StringUtils.isEmpty(texts[2]) ? "normal" : texts[2].toLowerCase();
   }

   public ModelResourceLocation(String text2, String text3) {
      this(parsePathString(text2 + "#" + (text3 == null ? "normal" : text3)));
   }

   public String getVariant() {
      return this.variant;
   }

   @Override
   public String toString() {
      return super.toString() + "#" + this.variant;
   }

   @Override
   public boolean equals(Object arg1) {
      if (this == arg1) {
         return true;
      } else if (arg1 instanceof ModelResourceLocation && super.equals(arg1)) {
         ModelResourceLocation modelResourceLocation = (ModelResourceLocation)arg1;
         return this.variant.equals(modelResourceLocation.variant);
      } else {
         return false;
      }
   }

   public ModelResourceLocation(ResourceLocation resourceLocation, String text2) {
      this(resourceLocation.toString(), text2);
   }

   protected static String[] parsePathString(String text2) {
      String[] texts = new String[]{null, text2, null};
      int i = text2.indexOf(35);
      String text = text2;
      if (i >= 0) {
         texts[2] = text2.substring(i + 1, text2.length());
         if (i > 1) {
            text = text2.substring(0, i);
         }
      }

      System.arraycopy(ResourceLocation.splitObjectName(text), 0, texts, 0, 2);
      return texts;
   }

   @Override
   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }
}
