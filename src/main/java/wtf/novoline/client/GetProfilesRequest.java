package wtf.novoline.client;

import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;

@Route(value = "/profiles/{userId}", httpMethod = HttpMethod.GET)
public class GetProfilesRequest extends AnnotatedApiRequest {
   @PathParam(value = "userId", enabled = true)
   private long userId;
}
