package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {
   @SerializedName("jwtToken")
   private final String jwtToken;

   @Override
   public String toString() {
      return "AuthenticationResponse(jwtToken=" + this.getJwtToken() + ")";
   }

   @Override
   public int hashCode() {
      String jwtToken2 = this.getJwtToken();
      return 59 + (jwtToken2 == null ? 43 : jwtToken2.hashCode());
   }

   public AuthenticationResponse(String text) {
      this.jwtToken = text;
   }

   protected boolean canEqual(Object object) {
      return object instanceof AuthenticationResponse;
   }

   public String getJwtToken() {
      return this.jwtToken;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof AuthenticationResponse authenticationResponse)) {
         return false;
      } else {
         if (!authenticationResponse.canEqual(this)) {
            return false;
         }

         String jwtToken2 = this.getJwtToken();
         String jwtToken3 = authenticationResponse.getJwtToken();
         return jwtToken2 == null ? jwtToken3 == null : jwtToken2.equals(jwtToken3);
      }
   }
}
