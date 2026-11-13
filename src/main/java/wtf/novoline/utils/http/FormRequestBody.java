package wtf.novoline.utils.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class FormRequestBody implements RequestBody {
   private final Map<String, Object> parameters;

   @Override
   public String getBodyText() {
      return this.createParameters(this.parameters);
   }

   public FormRequestBody(Map<String, Object> map2) {
      this.parameters = map2;
   }

   @Override
   public String getContentType() {
      return "application/x-www-form-urlencoded";
   }

   private String createParameters(Map<String, Object> map2) {
      return map2.entrySet()
         .stream()
         .filter(entry -> entry.getValue() != null)
         .map(entry -> (String)entry.getKey() + "=" + URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8))
         .collect(Collectors.joining("&"));
   }
}
