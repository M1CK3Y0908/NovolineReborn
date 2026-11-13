package wtf.novoline.client;

import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Route(value = "/authentication/login", httpMethod = HttpMethod.POST)
@Query(QueryType.QUERY)
public class PostAuthenticationLoginRequest extends AnnotatedApiRequest {
   @PathParam("token")
   private String token;
}
