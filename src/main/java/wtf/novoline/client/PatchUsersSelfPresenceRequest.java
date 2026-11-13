package wtf.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.ClientState;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Route(value = "/users/@self/presence", httpMethod = HttpMethod.PATCH)
@Query(QueryType.JSON)
public class PatchUsersSelfPresenceRequest extends AnnotatedApiRequest {
   @SerializedName("type")
   @Expose
   private final ClientState type;
   @SerializedName("ign")
   @Expose
   private final String ign;
   @SerializedName("serverIp")
   @Expose
   private final String serverIp;
   @SerializedName("sid")
   @Expose
   private final String sid;

   public PatchUsersSelfPresenceRequest withSid(String text) {
      return this.sid == text ? this : new PatchUsersSelfPresenceRequest(this.type, this.ign, this.serverIp, text);
   }

   public static PatchUsersSelfPresenceRequest.UpdatePresenceRequestBuilder builder() {
      return new PatchUsersSelfPresenceRequest.UpdatePresenceRequestBuilder();
   }

   PatchUsersSelfPresenceRequest(ClientState clientState, String text, String text2, String text3) {
      this.type = clientState;
      this.ign = text;
      this.serverIp = text2;
      this.sid = text3;
   }

   public static class UpdatePresenceRequestBuilder {
      private ClientState presenceType;
      private String ign;
      private String serverIp;
      private String sid;

      UpdatePresenceRequestBuilder() {
      }

      public PatchUsersSelfPresenceRequest.UpdatePresenceRequestBuilder ign(String text) {
         this.ign = text;
         return this;
      }

      @Override
      public String toString() {
         return "UpdatePresenceRequest.UpdatePresenceRequestBuilder(presenceType="
            + this.presenceType
            + ", ign="
            + this.ign
            + ", serverIp="
            + this.serverIp
            + ", sid="
            + this.sid
            + ")";
      }

      public PatchUsersSelfPresenceRequest build() {
         return new PatchUsersSelfPresenceRequest(this.presenceType, this.ign, this.serverIp, this.sid);
      }

      public PatchUsersSelfPresenceRequest.UpdatePresenceRequestBuilder presenceType(ClientState clientState) {
         this.presenceType = clientState;
         return this;
      }

      public PatchUsersSelfPresenceRequest.UpdatePresenceRequestBuilder sid(String text) {
         this.sid = text;
         return this;
      }

      public PatchUsersSelfPresenceRequest.UpdatePresenceRequestBuilder serverIp(String text) {
         this.serverIp = text;
         return this;
      }
   }
}
