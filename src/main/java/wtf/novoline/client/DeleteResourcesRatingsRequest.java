package wtf.novoline.client;

import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;

@Route(value = "/resources/{resourceId}/ratings", httpMethod = HttpMethod.DELETE)
public class DeleteResourcesRatingsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;

   private DeleteResourcesRatingsRequest(String text) {
      this.resourceId = text;
   }

   public static DeleteResourcesRatingsRequest createDeleteResourcesRatingsRequest(String text) {
      return new DeleteResourcesRatingsRequest(text);
   }
}
