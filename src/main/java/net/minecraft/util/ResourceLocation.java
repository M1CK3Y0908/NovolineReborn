package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {
   protected final String resourceDomain;
   protected final String resourcePath;

   public ResourceLocation(String resourceDomainIn, String resourcePathIn) {
      this(new String[]{resourceDomainIn, resourcePathIn});
   }

   protected static String[] splitObjectName(String toSplit) {
      String[] texts = new String[]{null, toSplit};
      int i = toSplit.indexOf(58);
      if (i >= 0) {
         texts[1] = toSplit.substring(i + 1, toSplit.length());
         if (i > 1) {
            texts[0] = toSplit.substring(0, i);
         }
      }

      return texts;
   }

   @Override
   public int hashCode() {
      return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
   }

   @Override
   public String toString() {
      return this.resourceDomain + ":" + this.resourcePath;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ResourceLocation resourceLocation)
            ? false
            : this.resourceDomain.equals(resourceLocation.resourceDomain) && this.resourcePath.equals(resourceLocation.resourcePath);
      }
   }

   public ResourceLocation(String resourceName) {
      this(splitObjectName(resourceName));
   }

   public String getResourcePath() {
      return this.resourcePath;
   }

   public String getResourceDomain() {
      return this.resourceDomain;
   }

   protected ResourceLocation(String... texts) {
      this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(texts[0]) ? "minecraft" : texts[0].toLowerCase();
      this.resourcePath = texts[1];
      Validate.notNull(this.resourcePath);
   }
}
