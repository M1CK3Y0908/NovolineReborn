package wtf.novoline.client;

import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;

@Route(value = "/resources/{resourceId}/file", httpMethod = HttpMethod.GET)
public class GetResourcesFileRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;

   public static GetResourcesFileRequest createGetResourcesFileRequest(String text) {
      return new GetResourcesFileRequest(text);
   }

   private GetResourcesFileRequest(String text) {
      this.resourceId = text;
   }
}
