package wtf.novoline.scripting;

public final class ScriptInfo {
   private final String name;
   private final String description;
   private final String author;
   private final String version;

   @Override
   public int hashCode() {
      String name2 = this.getName();
      int i = 59 + (name2 == null ? 43 : name2.hashCode());
      String description2 = this.getDescription();
      i = i * 59 + (description2 == null ? 43 : description2.hashCode());
      String author2 = this.getAuthor();
      i = i * 59 + (author2 == null ? 43 : author2.hashCode());
      String version2 = this.getVersion();
      return i * 59 + (version2 == null ? 43 : version2.hashCode());
   }

   public String getVersion() {
      return this.version;
   }

   ScriptInfo(String text, String text2, String text3, String text4) {
      this.name = text;
      this.description = text2;
      this.author = text3;
      this.version = text4;
   }

   public String getAuthor() {
      return this.author;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ScriptInfo scriptInfo)) {
         return false;
      } else {
         String name2 = this.getName();
         String name3 = scriptInfo.getName();
         if (name2 == null ? name3 == null : name2.equals(name3)) {
            String description2 = this.getDescription();
            String description3 = scriptInfo.getDescription();
            if (description2 == null ? description3 == null : description2.equals(description3)) {
               String author2 = this.getAuthor();
               String author3 = scriptInfo.getAuthor();
               if (author2 == null ? author3 == null : author2.equals(author3)) {
                  String version2 = this.getVersion();
                  String version3 = scriptInfo.getVersion();
                  return version2 == null ? version3 == null : version2.equals(version3);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static ScriptInfo.MetadataBuilder builder() {
      return new ScriptInfo.MetadataBuilder();
   }

   @Override
   public String toString() {
      return "Metadata(name="
         + this.getName()
         + ", description="
         + this.getDescription()
         + ", author="
         + this.getAuthor()
         + ", version="
         + this.getVersion()
         + ")";
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public static class MetadataBuilder {
      private String version;
      private String author;
      private String description;
      private String name;

      public ScriptInfo.MetadataBuilder name(String text) {
         this.name = text;
         return this;
      }

      public ScriptInfo.MetadataBuilder author(String text) {
         this.author = text;
         return this;
      }

      MetadataBuilder() {
      }

      public ScriptInfo.MetadataBuilder version(String text) {
         this.version = text;
         return this;
      }

      public ScriptInfo.MetadataBuilder description(String text) {
         this.description = text;
         return this;
      }

      @Override
      public String toString() {
         return "Metadata.MetadataBuilder(name="
            + this.name
            + ", description="
            + this.description
            + ", author="
            + this.author
            + ", version="
            + this.version
            + ")";
      }

      public ScriptInfo build() {
         return new ScriptInfo(this.name, this.description, this.author, this.version);
      }
   }
}
