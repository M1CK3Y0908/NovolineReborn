package wtf.novoline.utils.hypixel;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;

public class HypixelApiEndpoints {
   private static final String HTTPS_API_SK1ER_CLUB = "https://api.sk1er.club";
   private static final String HTTPS_API_HYPIXEL_NET = "https://api.hypixel.net";
   public static final HypixelApiEndpoints.Endpoint PUNISHMENT_STATS = HypixelApiEndpoints.Endpoint.createEndpoint(
      "https://api.hypixel.net", "GET", "/punishmentstats"
   );
   public static final HypixelApiEndpoints.Endpoint PLAYER_BY_NAME = HypixelApiEndpoints.Endpoint.createEndpoint(
      "https://api.sk1er.club", "GET", "/player/:name"
   );

   public static final class Endpoint {
      private final String baseUrl;
      private final String httpMethod;
      private final String path;
      private String[] headers;

      public Builder getBuilder(BodyPublisher bodyPublisher) {
         Builder builder = HttpRequest.newBuilder(URI.create(this.baseUrl + this.path)).method(this.httpMethod, bodyPublisher);
         if (this.headers.length > 0) {
            builder.headers(this.headers);
         }

         return builder;
      }

      public String getBaseUrl() {
         return this.baseUrl;
      }

      public Endpoint(String text, String text2, String text3, String[] texts) {
         this.baseUrl = text;
         this.httpMethod = text2;
         this.path = text3;
         this.headers = texts;
      }

      public HypixelApiEndpoints.Endpoint withPathParam(String text, Object object) {
         return this.withPath(this.path.replace(":" + text, object.toString()));
      }

      public String getPath() {
         return this.path;
      }

      public HypixelApiEndpoints.Endpoint withHeaders(String... texts) {
         this.headers = texts;
         return this;
      }

      public HypixelApiEndpoints.Endpoint withPath(String text) {
         return this.path == text ? this : new HypixelApiEndpoints.Endpoint(this.baseUrl, this.httpMethod, text, this.headers);
      }

      public String getHttpMethod() {
         return this.httpMethod;
      }

      public static HypixelApiEndpoints.Endpoint createEndpoint(String text, String text2, String text3) {
         return new HypixelApiEndpoints.Endpoint(text, text2, text3, new String[0]);
      }

      public Builder getBuilderWithoutBody() {
         return this.getBuilder(BodyPublishers.noBody());
      }
   }
}
