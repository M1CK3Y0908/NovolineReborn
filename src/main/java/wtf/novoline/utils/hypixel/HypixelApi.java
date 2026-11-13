package wtf.novoline.utils.hypixel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import wtf.novoline.gson.AnnotatedJsonDeserializer;
import wtf.novoline.gson.InstantMillisAdapter;
import wtf.novoline.lang.ApiException;
import wtf.novoline.lang.ApiParseException;
import wtf.novoline.util.BedWarsStatsWrapper;
import wtf.novoline.util.SkyWarsStatsWrapper;
import wtf.novoline.utils.hypixel.HypixelApiEndpoints.Endpoint;

public class HypixelApi {
   private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0";
   private String apiKey;
   private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new InstantMillisAdapter())
      .registerTypeAdapter(SkyWarsStatsWrapper.class, new AnnotatedJsonDeserializer())
      .registerTypeAdapter(BedWarsStatsWrapper.class, new AnnotatedJsonDeserializer())
      .create();
   private final HttpClient httpClient = HttpClient.newBuilder().version(java.net.http.HttpClient.Version.HTTP_2).build();

   public void setApiKey(String text) {
      this.apiKey = text;
   }

   public PunishmentStats getPunishmentStats() throws ApiException {
      if (this.apiKey == null) {
         throw new ApiException("Authentication token is not set");
      } else {
         return (PunishmentStats)this.request(HypixelApiEndpoints.PUNISHMENT_STATS.withHeaders("API-Key", this.apiKey), PunishmentStats.class);
      }
   }

   private Object sendAndDeserialize(java.net.http.HttpRequest.Builder builder, Class clazz) throws ApiException {
      JsonObject jsonObject = this.getJsonObject(builder);
      if (jsonObject.get("success").getAsBoolean()) {
         if (jsonObject.has("cache")) {
            jsonObject.remove("cache");
            jsonObject.remove("cached");
            jsonObject.remove("success");
            jsonObject.remove("_id");
            String text = jsonObject.keySet().toArray(String[]::new)[0];
            return this.gson.fromJson(jsonObject.get(text), clazz);
         } else {
            return this.gson.fromJson(jsonObject, clazz);
         }
      } else if (jsonObject.has("cause")) {
         String text2 = jsonObject.get("cause").toString();
         throw new ApiException("Request failed: " + text2);
      } else {
         throw new ApiException("Unexpected error occurred");
      }
   }

   public String getApiKey() {
      return this.apiKey;
   }

   private JsonObject getJsonObject(java.net.http.HttpRequest.Builder builder) throws ApiException {
      HttpRequest httpRequest = builder.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:93.0) Gecko/20100101 Firefox/93.0").build();

      try {
         HttpResponse httpResponse = this.httpClient.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());

         try {
            return JsonParser.parseString((String)httpResponse.body()).getAsJsonObject();
         } catch (JsonSyntaxException jsonSyntaxException) {
            throw new ApiParseException("Server returned status code " + httpResponse.statusCode());
         }
      } catch (IOException | InterruptedException exception) {
         throw new ApiException("Failed to send HTTP request", exception);
      }
   }

   public HypixelPlayer getHypixelPlayer(String text) throws ApiException {
      return (HypixelPlayer)this.request(HypixelApiEndpoints.PLAYER_BY_NAME.withPathParam("name", text), HypixelPlayer.class);
   }

   private Object request(Endpoint endpoint, Class clazz) throws ApiException {
      return this.sendAndDeserialize(endpoint.getBuilderWithoutBody(), clazz);
   }
}
