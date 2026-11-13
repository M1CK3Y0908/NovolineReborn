package wtf.novoline.client;

import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.client.discord.Snowflake;
import wtf.novoline.enums.HttpMethod;

@Route(value = "/rooms/{roomId}/recentMessages", httpMethod = HttpMethod.GET)
public class GetRoomsRecentMessagesRequest extends AnnotatedApiRequest {
   @PathParam(value = "roomId", enabled = true)
   private final Snowflake snowflake;

   public static GetRoomsRecentMessagesRequest createGetRoomsRecentMessagesRequest(Snowflake snowflake2) {
      return new GetRoomsRecentMessagesRequest(snowflake2);
   }

   private GetRoomsRecentMessagesRequest(Snowflake snowflake2) {
      this.snowflake = snowflake2;
   }
}
