package wtf.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.client.discord.Snowflake;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Query(QueryType.JSON)
@Route(value = "/rooms/{roomId}/messages", httpMethod = HttpMethod.PUT)
public class PutRoomsMessagesRequest extends AnnotatedApiRequest {
   @PathParam(value = "roomId", enabled = true)
   private final Snowflake snowflake;
   @SerializedName("content")
   @Expose
   private final String content;

   private PutRoomsMessagesRequest(Snowflake snowflake2, String text) {
      this.snowflake = snowflake2;
      this.content = text;
   }

   public static PutRoomsMessagesRequest createPutRoomsMessagesRequest(Snowflake snowflake2, String text) {
      return new PutRoomsMessagesRequest(snowflake2, text);
   }
}
