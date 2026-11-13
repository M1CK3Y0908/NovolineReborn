package wtf.novoline.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.Instant;

public class InstantMillisAdapter implements JsonDeserializer<Instant>, JsonSerializer<Instant> {
   public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      return Instant.ofEpochMilli(jsonElement.getAsLong());
   }

   public JsonElement serialize(Instant instant, Type type, JsonSerializationContext jsonSerializationContext) {
      return new JsonPrimitive(instant.toEpochMilli());
   }
}
