package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class FontMetadataSectionSerializer extends BaseMetadataSectionSerializer<FontMetadataSection> {
   public FontMetadataSection deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
      JsonObject jsonObject = arg1.getAsJsonObject();
      float[] floatArray = new float[256];
      float[] floatArray1 = new float[256];
      float[] floatArray2 = new float[256];
      float f = 1.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (jsonObject.has("characters")) {
         if (!jsonObject.get("characters").isJsonObject()) {
            throw new JsonParseException("Invalid font->characters: expected object, was " + jsonObject.get("characters"));
         }

         JsonObject jsonObject2 = jsonObject.getAsJsonObject("characters");
         if (jsonObject2.has("default")) {
            if (!jsonObject2.get("default").isJsonObject()) {
               throw new JsonParseException("Invalid font->characters->default: expected object, was " + jsonObject2.get("default"));
            }

            JsonObject jsonObject3 = jsonObject2.getAsJsonObject("default");
            f = JsonUtils.getFloat(jsonObject3, "width", f);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f, "Invalid default width");
            f2 = JsonUtils.getFloat(jsonObject3, "spacing", f2);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f2, "Invalid default spacing");
            f3 = JsonUtils.getFloat(jsonObject3, "left", f2);
            Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f3, "Invalid default left");
         }

         for (int index = 0; index < 256; index++) {
            JsonElement jsonElement = jsonObject2.get(Integer.toString(index));
            float f4 = f;
            float f5 = f2;
            float f6 = f3;
            if (jsonElement != null) {
               JsonObject jsonObject4 = JsonUtils.getJsonObject(jsonElement, "characters[" + index + "]");
               f4 = JsonUtils.getFloat(jsonObject4, "width", f);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f4, "Invalid width");
               f5 = JsonUtils.getFloat(jsonObject4, "spacing", f2);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f5, "Invalid spacing");
               f6 = JsonUtils.getFloat(jsonObject4, "left", f3);
               Validate.inclusiveBetween(0.0, Float.MAX_VALUE, f6, "Invalid left");
            }

            floatArray[index] = f4;
            floatArray1[index] = f5;
            floatArray2[index] = f6;
         }
      }

      return new FontMetadataSection(floatArray, floatArray2, floatArray1);
   }

   @Override
   public String getSectionName() {
      return "font";
   }
}
