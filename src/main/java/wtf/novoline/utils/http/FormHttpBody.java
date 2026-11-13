package wtf.novoline.utils.http;

import java.util.Map;

public class FormHttpBody implements HttpBody {
   private final Map<String, String> parameters;

   public FormHttpBody(Map<String, String> map) {
      this.parameters = map;
   }

   @Override
   public String getContentType() {
      return "application/x-www-form-urlencoded";
   }

   @Override
   public String getBodyText() {
      return UriUtils.createParameters(this.parameters);
   }
}
