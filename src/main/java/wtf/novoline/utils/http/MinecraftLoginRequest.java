package wtf.novoline.utils.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.net.URI;
import wtf.novoline.enums.HttpMethod2;

public class MinecraftLoginRequest extends AuthRequest {
   @SerializedName("identityToken")
   @Expose
   private String identityToken;

   private MinecraftLoginRequest() {
      super(HttpMethod2.POST, URI.create("https://api.minecraftservices.com/authentication/login_with_xbox"));
   }

   @Override
   public HttpBody getHttpBody() {
      return JsonHttpBody.createJsonHttpBody(this);
   }

   public MinecraftLoginRequest getMinecraftLoginRequest(String text) {
      this.identityToken = text;
      return this;
   }

   public String getIdentityToken() {
      return this.identityToken;
   }

   public static MinecraftLoginRequest create() {
      return new MinecraftLoginRequest();
   }
}
