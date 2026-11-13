package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.util.List;

public class XBLAuthenticationResponse {
   @SerializedName("Token")
   private String token;
   @SerializedName("DisplayClaims")
   private XBLAuthenticationResponse.DisplayClaims displayClaims;
   @SerializedName("NotAfter")
   private Instant notAfter;
   @SerializedName("IssueInstant")
   private Instant issueInstant;

   @Override
   public int hashCode() {
      Instant issueInstant2 = this.getIssueInstant();
      int i = 59 + (issueInstant2 == null ? 43 : issueInstant2.hashCode());
      Instant notAfter2 = this.getNotAfter();
      i = i * 59 + (notAfter2 == null ? 43 : notAfter2.hashCode());
      String token2 = this.getToken();
      i = i * 59 + (token2 == null ? 43 : token2.hashCode());
      XBLAuthenticationResponse.DisplayClaims displayClaims2 = this.getDisplayClaims();
      return i * 59 + (displayClaims2 == null ? 43 : displayClaims2.hashCode());
   }

   public Instant getNotAfter() {
      return this.notAfter;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof XBLAuthenticationResponse xblAuthenticationResponse)) {
         return false;
      } else {
         if (!xblAuthenticationResponse.canEqual(this)) {
            return false;
         }

         Instant issueInstant2 = this.getIssueInstant();
         Instant issueInstant3 = xblAuthenticationResponse.getIssueInstant();
         if (issueInstant2 == null ? issueInstant3 == null : issueInstant2.equals(issueInstant3)) {
            Instant notAfter2 = this.getNotAfter();
            Instant notAfter3 = xblAuthenticationResponse.getNotAfter();
            if (notAfter2 == null ? notAfter3 == null : notAfter2.equals(notAfter3)) {
               String token2 = this.getToken();
               String token3 = xblAuthenticationResponse.getToken();
               if (token2 == null ? token3 == null : token2.equals(token3)) {
                  XBLAuthenticationResponse.DisplayClaims displayClaims2 = this.getDisplayClaims();
                  XBLAuthenticationResponse.DisplayClaims displayClaims3 = xblAuthenticationResponse.getDisplayClaims();
                  return displayClaims2 == null ? displayClaims3 == null : displayClaims2.equals(displayClaims3);
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

   public Instant getIssueInstant() {
      return this.issueInstant;
   }

   public void setDisplayClaims(XBLAuthenticationResponse.DisplayClaims displayClaims2) {
      this.displayClaims = displayClaims2;
   }

   public void setIssueInstant(Instant instant) {
      this.issueInstant = instant;
   }

   public void setToken(String text) {
      this.token = text;
   }

   public String getToken() {
      return this.token;
   }

   @Override
   public String toString() {
      return "XBLAuthenticationResponse(issueInstant="
         + this.getIssueInstant()
         + ", notAfter="
         + this.getNotAfter()
         + ", token="
         + this.getToken()
         + ", displayClaims="
         + this.getDisplayClaims()
         + ")";
   }

   protected boolean canEqual(Object object) {
      return object instanceof XBLAuthenticationResponse;
   }

   public XBLAuthenticationResponse.DisplayClaims getDisplayClaims() {
      return this.displayClaims;
   }

   public void setNotAfter(Instant instant) {
      this.notAfter = instant;
   }

   public static final class Component {
      @SerializedName("uhs")
      private String uhs;

      @Override
      public int hashCode() {
         String uhs2 = this.getUhs();
         return 59 + (uhs2 == null ? 43 : uhs2.hashCode());
      }

      public String getUhs() {
         return this.uhs;
      }

      public void setUhs(String text) {
         this.uhs = text;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof XBLAuthenticationResponse.Component component)) {
            return false;
         } else {
            String uhs2 = this.getUhs();
            String uhs3 = component.getUhs();
            return uhs2 == null ? uhs3 == null : uhs2.equals(uhs3);
         }
      }

      @Override
      public String toString() {
         return "XBLAuthenticationResponse.DisplayClaims.XUI(uhs=" + this.getUhs() + ")";
      }
   }

   public static final class DisplayClaims {
      @SerializedName("xui")
      private List<XBLAuthenticationResponse.Component> xui;

      @Override
      public String toString() {
         return "XBLAuthenticationResponse.DisplayClaims(xui=" + this.getXui() + ")";
      }

      @Override
      public int hashCode() {
         List xui2 = this.getXui();
         return 59 + (xui2 == null ? 43 : xui2.hashCode());
      }

      public void setXui(List<XBLAuthenticationResponse.Component> list) {
         this.xui = list;
      }

      public List<XBLAuthenticationResponse.Component> getXui() {
         return this.xui;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof XBLAuthenticationResponse.DisplayClaims displayClaims2)) {
            return false;
         } else {
            List xui2 = this.getXui();
            List xui3 = displayClaims2.getXui();
            return xui2 == null ? xui3 == null : xui2.equals(xui3);
         }
      }
   }
}
