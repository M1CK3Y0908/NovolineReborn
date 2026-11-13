package wtf.novoline.util;

public class RestClientOptions {
   private String baseUri = "https://irc-staging.novoline.wtf/api/v1";

   public RestClientOptions baseUri(String text) {
      this.baseUri = text;
      return this;
   }

   @Override
   public int hashCode() {
      String baseUri2 = this.getBaseUri();
      return 59 + (baseUri2 == null ? 43 : baseUri2.hashCode());
   }

   protected boolean canEqual(Object object) {
      return object instanceof RestClientOptions;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof RestClientOptions restClientOptions)) {
         return false;
      } else {
         if (!restClientOptions.canEqual(this)) {
            return false;
         }

         String baseUri2 = this.getBaseUri();
         String baseUri3 = restClientOptions.getBaseUri();
         return baseUri2 == null ? baseUri3 == null : baseUri2.equals(baseUri3);
      }
   }

   public String getBaseUri() {
      return this.baseUri;
   }

   @Override
   public String toString() {
      return "RestClientOptions(baseUri=" + this.getBaseUri() + ")";
   }
}
