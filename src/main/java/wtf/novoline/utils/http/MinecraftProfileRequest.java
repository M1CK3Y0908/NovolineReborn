package wtf.novoline.utils.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import wtf.novoline.enums.HttpMethod2;
import wtf.novoline.lang.MicrosoftAuthException;
import wtf.novoline.support.SneakyThrow;

public class MinecraftProfileRequest extends AuthRequest {
   private final String tokenType;
   private final String accessToken;

   public String getTokenType() {
      return this.tokenType;
   }

   private MinecraftProfileRequest(String text, String text2) {
      super(HttpMethod2.GET, URI.create("https://api.minecraftservices.com/minecraft/profile"));
      this.tokenType = text;
      this.accessToken = text2;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   @Override
   public void onFailedResponse(HttpResponse httpResponse) {
      try {
      JsonObject asJsonObject = JsonParser.parseString(String.valueOf(httpResponse.body())).getAsJsonObject();
      throw new MicrosoftAuthException(asJsonObject.get("errorMessage").getAsString());
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public static MinecraftProfileRequest createMinecraftProfileRequest(String text, String text2) {
      return new MinecraftProfileRequest(text, text2);
   }

   @Override
   public List getHeaders() {
      return Collections.singletonList(HttpHeader.createHttpHeader("Authorization", this.tokenType + " " + this.accessToken));
   }
}
