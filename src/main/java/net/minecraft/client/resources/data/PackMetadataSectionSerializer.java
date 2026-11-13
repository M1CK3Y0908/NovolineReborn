package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection> implements JsonSerializer<PackMetadataSection> {
   @Override
   public String getSectionName() {
      return "pack";
   }

   public JsonElement serialize(PackMetadataSection arg1, Type arg2, JsonSerializationContext arg3) {
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("pack_format", arg1.getPackFormat());
      jsonObject2.add("description", arg3.serialize(arg1.getPackDescription()));
      return jsonObject2;
   }

   public PackMetadataSection deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
      JsonObject jsonObject = arg1.getAsJsonObject();
      IChatComponent chatComponent = arg3.deserialize(jsonObject.get("description"), IChatComponent.class);
      if (chatComponent == null) {
         throw new JsonParseException("Invalid/missing description!");
      }

      int intValue = JsonUtils.getInt(jsonObject, "pack_format");
      return new PackMetadataSection(chatComponent, intValue);
   }
}
