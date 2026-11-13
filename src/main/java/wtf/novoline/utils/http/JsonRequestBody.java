package wtf.novoline.utils.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class JsonRequestBody implements RequestBody {
   private final JsonElement jsonElement;
   private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

   public JsonRequestBody(JsonElement jsonElement2) {
      this.jsonElement = jsonElement2;
   }

   @Override
   public String getBodyText() {
      return gson.toJson(this.jsonElement);
   }

   @Override
   public String getContentType() {
      return "application/json";
   }

   public static JsonRequestBody createJsonRequestBody(Object object) {
      return new JsonRequestBody(gson.toJsonTree(object));
   }
}
