package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;

public class ExchangeCodeResponse {
   @SerializedName("user_id")
   private String userId;
   @SerializedName("scope")
   private String scope;
   @SerializedName("token_type")
   private String tokenType;
   @SerializedName("refresh_token")
   private String refreshToken;
   @SerializedName("foci")
   private long foci;
   @SerializedName("access_token")
   private String accessToken;
   @SerializedName("expires_in")
   private long expiresIn;

   public void setRefreshToken(String text) {
      this.refreshToken = text;
   }

   @Override
   public String toString() {
      String tokenType2 = this.getTokenType();
      long expiresIn2 = this.getExpiresIn();
      String scope2 = this.getScope();
      String accessToken2 = this.getAccessToken();
      String refreshToken2 = this.getRefreshToken();
      long foci2 = this.getFoci();
      String userId2 = this.getUserId();
      String text = refreshToken2;
      String text2 = accessToken2;
      String text3 = scope2;
      long l = expiresIn2;
      return "ExchangeCodeResponse(tokenType="
         + tokenType2
         + ", expiresIn="
         + l
         + ", scope="
         + text3
         + ", accessToken="
         + text2
         + ", refreshToken="
         + text
         + ", userId="
         + userId2
         + ", foci="
         + foci2
         + ")";
   }

   public void setAccessToken(String text) {
      this.accessToken = text;
   }

   public long getExpiresIn() {
      return this.expiresIn;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public void setUserId(String text) {
      this.userId = text;
   }

   public void setTokenType(String text) {
      this.tokenType = text;
   }

   public long getFoci() {
      return this.foci;
   }

   public String getUserId() {
      return this.userId;
   }

   public void setScope(String text) {
      this.scope = text;
   }

   public String getTokenType() {
      return this.tokenType;
   }

   public String getScope() {
      return this.scope;
   }

   public String getRefreshToken() {
      return this.refreshToken;
   }

   @Override
   public int hashCode() {
      long expiresIn2 = this.getExpiresIn();
      int i = 59 + (int)(expiresIn2 >>> 32 ^ expiresIn2);
      long foci2 = this.getFoci();
      i = i * 59 + (int)(foci2 >>> 32 ^ foci2);
      String tokenType2 = this.getTokenType();
      i = i * 59 + (tokenType2 == null ? 43 : tokenType2.hashCode());
      String scope2 = this.getScope();
      i = i * 59 + (scope2 == null ? 43 : scope2.hashCode());
      String accessToken2 = this.getAccessToken();
      i = i * 59 + (accessToken2 == null ? 43 : accessToken2.hashCode());
      String refreshToken2 = this.getRefreshToken();
      i = i * 59 + (refreshToken2 == null ? 43 : refreshToken2.hashCode());
      String userId2 = this.getUserId();
      return i * 59 + (userId2 == null ? 43 : userId2.hashCode());
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ExchangeCodeResponse exchangeCodeResponse)) {
         return false;
      } else {
         if (!exchangeCodeResponse.canEqual(this)) {
            return false;
         }

         if (this.getExpiresIn() != exchangeCodeResponse.getExpiresIn()) {
            return false;
         }

         if (this.getFoci() != exchangeCodeResponse.getFoci()) {
            return false;
         }

         String tokenType2 = this.getTokenType();
         String tokenType3 = exchangeCodeResponse.getTokenType();
         if (tokenType2 == null ? tokenType3 == null : tokenType2.equals(tokenType3)) {
            String scope2 = this.getScope();
            String scope3 = exchangeCodeResponse.getScope();
            if (scope2 == null ? scope3 == null : scope2.equals(scope3)) {
               String accessToken2 = this.getAccessToken();
               String accessToken3 = exchangeCodeResponse.getAccessToken();
               if (accessToken2 == null ? accessToken3 == null : accessToken2.equals(accessToken3)) {
                  String refreshToken2 = this.getRefreshToken();
                  String refreshToken3 = exchangeCodeResponse.getRefreshToken();
                  if (refreshToken2 == null ? refreshToken3 == null : refreshToken2.equals(refreshToken3)) {
                     String userId2 = this.getUserId();
                     String userId3 = exchangeCodeResponse.getUserId();
                     return userId2 == null ? userId3 == null : userId2.equals(userId3);
                  } else {
                     return false;
                  }
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

   protected boolean canEqual(Object object) {
      return object instanceof ExchangeCodeResponse;
   }
}
