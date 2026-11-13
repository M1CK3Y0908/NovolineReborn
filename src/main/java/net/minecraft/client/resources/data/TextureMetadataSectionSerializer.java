package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.util.JsonUtils;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection> {
   @Override
   public String getSectionName() {
      return "texture";
   }

   public TextureMetadataSection deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
      JsonObject jsonObject = arg1.getAsJsonObject();
      boolean conditionMet = JsonUtils.getBoolean(jsonObject, "blur", false);
      boolean conditionMet2 = JsonUtils.getBoolean(jsonObject, "clamp", false);
      List<Integer> list = Lists.newArrayList();
      if (jsonObject.has("mipmaps")) {
         try {
            JsonArray jsonArray = jsonObject.getAsJsonArray("mipmaps");

            for (int index = 0; index < jsonArray.size(); index++) {
               JsonElement jsonElement = jsonArray.get(index);
               if (jsonElement.isJsonPrimitive()) {
                  try {
                     list.add(jsonElement.getAsInt());
                  } catch (NumberFormatException numberFormatException) {
                     throw new JsonParseException("Invalid texture->mipmap->" + index + ": expected number, was " + jsonElement, numberFormatException);
                  }
               } else if (jsonElement.isJsonObject()) {
                  throw new JsonParseException("Invalid texture->mipmap->" + index + ": expected number, was " + jsonElement);
               }
            }
         } catch (ClassCastException classCastException) {
            throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + jsonObject.get("mipmaps"), classCastException);
         }
      }

      return new TextureMetadataSection(conditionMet, conditionMet2, list);
   }
}
