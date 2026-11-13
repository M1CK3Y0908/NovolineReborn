package wtf.novoline.utils.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import wtf.novoline.enums.HttpMethod2;
import wtf.novoline.lang.XboxAuthException;
import wtf.novoline.support.SneakyThrow;

public class XboxLiveAuthRequest extends AuthRequest {
   @SerializedName("RelyingParty")
   @Expose
   private String RelyingParty;
   @SerializedName("TokenType")
   @Expose
   private String TokenType;
   @SerializedName("Properties")
   @Expose
   private XboxLiveAuthRequest.Contract Properties;

   public XboxLiveAuthRequest relyingParty(String text) {
      this.RelyingParty = text;
      return this;
   }

   public XboxLiveAuthRequest tokenType(String text) {
      this.TokenType = text;
      return this;
   }

   public String getTokenType() {
      return this.TokenType;
   }

   @Override
   public void onFailedResponse(HttpResponse httpResponse) {
      try {
      if (httpResponse.statusCode() == 401) {
         JsonObject asJsonObject = JsonParser.parseString(String.valueOf(httpResponse.body())).getAsJsonObject();
         if (asJsonObject.has("XErr")) {
            long asLong = asJsonObject.get("XErr").getAsLong();
            throw new XboxAuthException(asLong);
         }
      } else {
         super.onFailedResponse(httpResponse);
      }
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public String getRelyingParty() {
      return this.RelyingParty;
   }

   public XboxLiveAuthRequest.Contract getProperties() {
      return this.Properties;
   }

   public XboxLiveAuthRequest getXboxLiveAuthRequest(XboxLiveAuthRequest.Contract contract) {
      this.Properties = contract;
      return this;
   }

   @Override
   public List getHeaders() {
      return Collections.singletonList(HttpHeader.createHttpHeader("Accept", "application/json"));
   }

   private XboxLiveAuthRequest(URI uri) {
      super(HttpMethod2.POST, uri);
   }

   public static XboxLiveAuthRequest createXboxLiveAuthRequest(URI uri) {
      return new XboxLiveAuthRequest(uri);
   }

   @Override
   public HttpBody getHttpBody() {
      return JsonHttpBody.createJsonHttpBody(this);
   }

   public interface Contract {
   }

   public static final class XBLProperties implements XboxLiveAuthRequest.Contract {
      @SerializedName("SiteName")
      @Expose
      private String siteName;
      @SerializedName("RpsTicket")
      @Expose
      private String rpsTicket;
      @SerializedName("AuthMethod")
      @Expose
      private String authMethod;

      public XboxLiveAuthRequest.XBLProperties authMethod(String text) {
         this.authMethod = text;
         return this;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof XboxLiveAuthRequest.XBLProperties xblProperties)) {
            return false;
         } else {
            String authMethod2 = this.getAuthMethod();
            String authMethod3 = xblProperties.getAuthMethod();
            if (authMethod2 == null ? authMethod3 == null : authMethod2.equals(authMethod3)) {
               String siteName2 = this.getSiteName();
               String siteName3 = xblProperties.getSiteName();
               if (siteName2 == null ? siteName3 == null : siteName2.equals(siteName3)) {
                  String rpsTicket2 = this.getRpsTicket();
                  String rpsTicket3 = xblProperties.getRpsTicket();
                  return rpsTicket2 == null ? rpsTicket3 == null : rpsTicket2.equals(rpsTicket3);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public String getAuthMethod() {
         return this.authMethod;
      }

      public String getRpsTicket() {
         return this.rpsTicket;
      }

      public XboxLiveAuthRequest.XBLProperties siteName(String text) {
         this.siteName = text;
         return this;
      }

      public String getSiteName() {
         return this.siteName;
      }

      public static XboxLiveAuthRequest.XBLProperties getXBLProperties() {
         return new XboxLiveAuthRequest.XBLProperties();
      }

      @Override
      public int hashCode() {
         String authMethod2 = this.getAuthMethod();
         int i = 59 + (authMethod2 == null ? 43 : authMethod2.hashCode());
         String siteName2 = this.getSiteName();
         i = i * 59 + (siteName2 == null ? 43 : siteName2.hashCode());
         String rpsTicket2 = this.getRpsTicket();
         return i * 59 + (rpsTicket2 == null ? 43 : rpsTicket2.hashCode());
      }

      @Override
      public String toString() {
         return "XBLAuthenticationRequest.XBLProperties(authMethod="
            + this.getAuthMethod()
            + ", siteName="
            + this.getSiteName()
            + ", rpsTicket="
            + this.getRpsTicket()
            + ")";
      }

      public XboxLiveAuthRequest.XBLProperties rpsTicket(String text) {
         this.rpsTicket = text;
         return this;
      }

      private XBLProperties() {
      }
   }

   public static final class XSTSProperties implements XboxLiveAuthRequest.Contract {
      @SerializedName("SandboxId")
      @Expose
      private String sandboxTicket;
      @SerializedName("UserTokens")
      @Expose
      private List<String> userTokens;

      private XSTSProperties() {
      }

      public XboxLiveAuthRequest.XSTSProperties userTokens(List<String> list) {
         this.userTokens = list;
         return this;
      }

      public static XboxLiveAuthRequest.XSTSProperties getXSTSProperties() {
         return new XboxLiveAuthRequest.XSTSProperties();
      }

      @Override
      public int hashCode() {
         String sandboxTicket2 = this.getSandboxTicket();
         int i = 59 + (sandboxTicket2 == null ? 43 : sandboxTicket2.hashCode());
         List userTokens2 = this.getUserTokens();
         return i * 59 + (userTokens2 == null ? 43 : userTokens2.hashCode());
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof XboxLiveAuthRequest.XSTSProperties xstsProperties)) {
            return false;
         } else {
            String sandboxTicket2 = this.getSandboxTicket();
            String sandboxTicket3 = xstsProperties.getSandboxTicket();
            if (sandboxTicket2 == null ? sandboxTicket3 == null : sandboxTicket2.equals(sandboxTicket3)) {
               List userTokens2 = this.getUserTokens();
               List userTokens3 = xstsProperties.getUserTokens();
               return userTokens2 == null ? userTokens3 == null : userTokens2.equals(userTokens3);
            } else {
               return false;
            }
         }
      }

      public String getSandboxTicket() {
         return this.sandboxTicket;
      }

      public List<String> getUserTokens() {
         return this.userTokens;
      }

      @Override
      public String toString() {
         return "XBLAuthenticationRequest.XSTSProperties(sandboxTicket=" + this.getSandboxTicket() + ", userTokens=" + this.getUserTokens() + ")";
      }

      public XboxLiveAuthRequest.XSTSProperties sandboxTicket(String text) {
         this.sandboxTicket = text;
         return this;
      }
   }
}
