package wtf.novoline.client;

import java.util.List;
import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.utils.http.MultipartRequestBody;
import wtf.novoline.utils.http.RequestBody;
import wtf.novoline.utils.http.MultipartRequestBody.Holder;

@Route(value = "/resources/{resourceId}/file", httpMethod = HttpMethod.POST)
public class FileUploadRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;
   private final byte[] byteArray;

   private FileUploadRequest(String text, byte[] bArray) {
      this.resourceId = text;
      this.byteArray = bArray;
   }

   @Override
   public RequestBody getRequestBody() {
      return new MultipartRequestBody(List.of(new Holder("file", "resource.bin", this.byteArray)));
   }

   public static FileUploadRequest createFileUploadRequest(String text, byte[] bArray) {
      return new FileUploadRequest(text, bArray);
   }
}
