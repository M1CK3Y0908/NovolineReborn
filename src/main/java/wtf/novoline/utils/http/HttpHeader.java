package wtf.novoline.utils.http;

public class HttpHeader {
   private final String name;
   private final String value;

   @Override
   public int hashCode() {
      String name2 = this.getName();
      int i = 59 + (name2 == null ? 43 : name2.hashCode());
      String value2 = this.getValue();
      return i * 59 + (value2 == null ? 43 : value2.hashCode());
   }

   public String getName() {
      return this.name;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof HttpHeader httpHeader)) {
         return false;
      } else if (!httpHeader.canEqual(this)) {
         return false;
      } else {
         String name2 = this.getName();
         String name3 = httpHeader.getName();
         if (name2 == null ? name3 == null : name2.equals(name3)) {
            String value2 = this.getValue();
            String value3 = httpHeader.getValue();
            return value2 == null ? value3 == null : value2.equals(value3);
         } else {
            return false;
         }
      }
   }

   @Override
   public String toString() {
      return "Header(name=" + this.getName() + ", value=" + this.getValue() + ")";
   }

   public static HttpHeader createHttpHeader(String text, String text2) {
      return new HttpHeader(text, text2);
   }

   protected boolean canEqual(Object object) {
      return object instanceof HttpHeader;
   }

   private HttpHeader(String text, String text2) {
      this.name = text;
      this.value = text2;
   }

   public String getValue() {
      return this.value;
   }
}
