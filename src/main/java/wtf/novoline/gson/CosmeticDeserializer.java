package wtf.novoline.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import wtf.novoline.enums.CapeType;
import wtf.novoline.utils.cosmetic.Cosmetic;

public class CosmeticDeserializer implements JsonDeserializer<Cosmetic> {
   private static final Gson gson = new GsonBuilder().create();

   public Cosmetic deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      if (!asJsonObject.has("type")) {
         throw new JsonParseException("Cosmetic has no type");
      }

      CapeType capeType = CapeType.valueOf(asJsonObject.get("type").getAsString());
      return gson.fromJson(asJsonObject, capeType.getClassValue());
   }
}
