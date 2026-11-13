package wtf.novoline.utils.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;

public final class HttpResponseUtils {
   public static void throwIfError(HttpResponse httpResponse) throws IOException {
      if (isJson(httpResponse)) {
         JsonObject jsonObject = parseBody(httpResponse);
         if (jsonObject.has("message")) {
            String asString = jsonObject.get("message").getAsString();
            if (!asString.isEmpty()) {
               throw new HttpResponseUtils.IOExceptionChild(asString);
            }
         }
      }

      throw new IOException("Server returned status code " + httpResponse.statusCode());
   }

   private static JsonObject parseBody(HttpResponse<?> httpResponse) {
      return JsonParser.parseString(String.valueOf(httpResponse.body())).getAsJsonObject();
   }

   private static boolean isJson(HttpResponse httpResponse) {
      Optional optional = httpResponse.headers().firstValue("Content-Type");
      return optional.isPresent() && ((String)optional.get()).contains("application/json");
   }

   public static final class IOExceptionChild extends IOException {
      public IOExceptionChild(String text) {
         super(text);
      }
   }
}
