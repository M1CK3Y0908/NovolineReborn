package wtf.novoline.utils.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class JsonHttpBody implements HttpBody {
   private final JsonElement jsonElement;
   private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

   public static JsonHttpBody createJsonHttpBody(Object object) {
      return new JsonHttpBody(gson.toJsonTree(object));
   }

   public JsonHttpBody(JsonElement jsonElement2) {
      this.jsonElement = jsonElement2;
   }

   @Override
   public String getContentType() {
      return "application/json";
   }

   @Override
   public String getBodyText() {
      return gson.toJson(this.jsonElement);
   }
}
