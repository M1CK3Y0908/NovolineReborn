package wtf.novoline.utils.http;

import java.net.URI;
import wtf.novoline.enums.HttpMethod2;
import wtf.novoline.utils.MapBuilder;

public class MicrosoftTokenRequest extends AuthRequest {
   private String refreshToken;
   private String grantType;
   private String code;
   private String scope;
   private String clientId;
   private String clientSecret;
   private String redirectUri;

   public String getRedirectUri() {
      return this.redirectUri;
   }

   public MicrosoftTokenRequest refreshToken(String text) {
      this.refreshToken = text;
      return this;
   }

   public String getScope() {
      return this.scope;
   }

   public String getClientId() {
      return this.clientId;
   }

   public MicrosoftTokenRequest clientId(String text) {
      this.clientId = text;
      return this;
   }

   @Override
   public HttpBody getHttpBody() {
      return new FormHttpBody(
         MapBuilder.<String, String>create()
            .with("client_id", this.clientId)
            .with("client_secret", this.clientSecret)
            .with("code", this.code)
            .with("refresh_token", this.refreshToken)
            .with("grant_type", this.grantType)
            .with("redirect_uri", this.redirectUri)
            .with("scope", this.scope)
      );
   }

   public MicrosoftTokenRequest clientSecret(String text) {
      this.clientSecret = text;
      return this;
   }

   private MicrosoftTokenRequest() {
      super(HttpMethod2.POST, URI.create("https://login.live.com/oauth20_token.srf"));
   }

   public MicrosoftTokenRequest grantType(String text) {
      this.grantType = text;
      return this;
   }

   public MicrosoftTokenRequest code(String text) {
      this.code = text;
      return this;
   }

   public String getClientSecret() {
      return this.clientSecret;
   }

   public MicrosoftTokenRequest scope(String text) {
      this.scope = text;
      return this;
   }

   public String getGrantType() {
      return this.grantType;
   }

   public String getCode() {
      return this.code;
   }

   public static MicrosoftTokenRequest getMicrosoftTokenRequest() {
      return new MicrosoftTokenRequest();
   }

   public MicrosoftTokenRequest redirectUri(String text) {
      this.redirectUri = text;
      return this;
   }

   public String getRefreshToken() {
      return this.refreshToken;
   }
}
