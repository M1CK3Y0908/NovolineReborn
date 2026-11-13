package wtf.novoline.client;

public class ApiHeader {
   private final String name;
   private final String value;

   private ApiHeader(String text, String text2) {
      this.name = text;
      this.value = text2;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ApiHeader apiHeader)) {
         return false;
      } else if (!apiHeader.canEqual(this)) {
         return false;
      } else {
         String name2 = this.getName();
         String name3 = apiHeader.getName();
         if (name2 == null ? name3 == null : name2.equals(name3)) {
            String value2 = this.getValue();
            String value3 = apiHeader.getValue();
            return value2 == null ? value3 == null : value2.equals(value3);
         } else {
            return false;
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "Header(name=" + this.getName() + ", value=" + this.getValue() + ")";
   }

   @Override
   public int hashCode() {
      String name2 = this.getName();
      int i = 59 + (name2 == null ? 43 : name2.hashCode());
      String value2 = this.getValue();
      return i * 59 + (value2 == null ? 43 : value2.hashCode());
   }

   protected boolean canEqual(Object object) {
      return object instanceof ApiHeader;
   }

   public static ApiHeader createApiHeader(String text, String text2) {
      return new ApiHeader(text, text2);
   }
}
