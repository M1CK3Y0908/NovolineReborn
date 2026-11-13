package net.minecraft.client.resources;

public class Language implements Comparable<Language> {
   private final String languageCode;
   private final String region;
   private final String name;
   private final boolean bidirectional;

   public Language(String languageCodeIn, String regionIn, String nameIn, boolean bidirectionalIn) {
      this.languageCode = languageCodeIn;
      this.region = regionIn;
      this.name = nameIn;
      this.bidirectional = bidirectionalIn;
   }

   public int compareTo(Language arg1) {
      return this.languageCode.compareTo(arg1.languageCode);
   }

   @Override
   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   @Override
   public int hashCode() {
      return this.languageCode.hashCode();
   }

   @Override
   public boolean equals(Object arg1) {
      return this == arg1 ? true : (!(arg1 instanceof Language) ? false : this.languageCode.equals(((Language)arg1).languageCode));
   }

   public String getLanguageCode() {
      return this.languageCode;
   }
}
