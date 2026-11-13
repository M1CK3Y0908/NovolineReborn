package wtf.novoline.client;

public interface ApiRequestHandler {
   void addBearerHeader(ApiRequest apiRequest);

   class BearerTokenHandler implements ApiRequestHandler {
      private final String token;

      @Override
      public void addBearerHeader(ApiRequest apiRequest) {
         ApiHeader apiHeader = ApiHeader.createApiHeader("Authorization", "Bearer " + this.token);
         apiRequest.getHeaders().add(apiHeader);
      }

      public BearerTokenHandler(String text) {
         this.token = text;
      }

      @Override
      public String toString() {
         return this.token;
      }
   }
}
