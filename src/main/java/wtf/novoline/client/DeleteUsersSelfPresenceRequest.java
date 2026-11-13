package wtf.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Route(value = "/users/@self/presence", httpMethod = HttpMethod.DELETE)
@Query(QueryType.JSON)
public class DeleteUsersSelfPresenceRequest extends AnnotatedApiRequest {
   @SerializedName("sid")
   @Expose
   private String sid;
   @SerializedName("hide")
   @Expose
   private boolean hide;
}
