package wtf.novoline.utils.account;

import com.google.gson.annotations.SerializedName;

public class MicrosoftOAuthToken {
   @SerializedName("access_token")
   private String accessToken;
   @SerializedName("token_type")
   private String tokenType;
   @SerializedName("expires_in")
   private long expiresIn;
   @SerializedName("username")
   private String username;

   public String getAccessToken() {
      return this.accessToken;
   }

   public long getExpiresIn() {
      return this.expiresIn;
   }

   public String getTokenType() {
      return this.tokenType;
   }

   @Override
   public String toString() {
      long expiresIn2 = this.getExpiresIn();
      return "MCXboxLoginResponse(username="
         + this.getUsername()
         + ", accessToken="
         + this.getAccessToken()
         + ", tokenType="
         + this.getTokenType()
         + ", expiresIn="
         + expiresIn2
         + ")";
   }

   public void setAccessToken(String text) {
      this.accessToken = text;
   }

   public void setTokenType(String text) {
      this.tokenType = text;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof MicrosoftOAuthToken microsoftOAuthToken)) {
         return false;
      } else {
         if (!microsoftOAuthToken.canEqual(this)) {
            return false;
         }

         if (this.getExpiresIn() != microsoftOAuthToken.getExpiresIn()) {
            return false;
         }

         String username2 = this.getUsername();
         String username3 = microsoftOAuthToken.getUsername();
         if (username2 == null ? username3 == null : username2.equals(username3)) {
            String accessToken2 = this.getAccessToken();
            String accessToken3 = microsoftOAuthToken.getAccessToken();
            if (accessToken2 == null ? accessToken3 == null : accessToken2.equals(accessToken3)) {
               String tokenType2 = this.getTokenType();
               String tokenType3 = microsoftOAuthToken.getTokenType();
               return tokenType2 == null ? tokenType3 == null : tokenType2.equals(tokenType3);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String text) {
      this.username = text;
   }

   @Override
   public int hashCode() {
      long expiresIn2 = this.getExpiresIn();
      int i = 59 + (int)(expiresIn2 >>> 32 ^ expiresIn2);
      String username2 = this.getUsername();
      i = i * 59 + (username2 == null ? 43 : username2.hashCode());
      String accessToken2 = this.getAccessToken();
      i = i * 59 + (accessToken2 == null ? 43 : accessToken2.hashCode());
      String tokenType2 = this.getTokenType();
      return i * 59 + (tokenType2 == null ? 43 : tokenType2.hashCode());
   }

   protected boolean canEqual(Object object) {
      return object instanceof MicrosoftOAuthToken;
   }
}
