package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer
   extends BaseMetadataSectionSerializer<AnimationMetadataSection>
   implements JsonSerializer<AnimationMetadataSection> {
   @Override
   public String getSectionName() {
      return "animation";
   }

   private AnimationFrame parseAnimationFrame(int i, JsonElement jsonElement2) {
      if (jsonElement2.isJsonPrimitive()) {
         return new AnimationFrame(JsonUtils.getInt(jsonElement2, "frames[" + i + "]"));
      }

      if (jsonElement2.isJsonObject()) {
         JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement2, "frames[" + i + "]");
         int intValue = JsonUtils.getInt(jsonObject, "time", -1);
         if (jsonObject.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, intValue, "Invalid frame time");
         }

         int intValue2 = JsonUtils.getInt(jsonObject, "index");
         Validate.inclusiveBetween(0L, 2147483647L, intValue2, "Invalid frame index");
         return new AnimationFrame(intValue2, intValue);
      } else {
         return null;
      }
   }

   public AnimationMetadataSection deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
      List<AnimationFrame> list = Lists.newArrayList();
      JsonObject jsonObject = JsonUtils.getJsonObject(arg1, "metadata section");
      int intValue = JsonUtils.getInt(jsonObject, "frametime", 1);
      if (intValue != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, intValue, "Invalid default frame time");
      }

      if (jsonObject.has("frames")) {
         try {
            JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "frames");

            for (int innerIndex = 0; innerIndex < jsonArray.size(); innerIndex++) {
               JsonElement jsonElement = jsonArray.get(innerIndex);
               AnimationFrame animationFrame = this.parseAnimationFrame(innerIndex, jsonElement);
               if (animationFrame != null) {
                  list.add(animationFrame);
               }
            }
         } catch (ClassCastException classCastException) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), classCastException);
         }
      }

      int intValue2 = JsonUtils.getInt(jsonObject, "width", -1);
      int intValue3 = JsonUtils.getInt(jsonObject, "height", -1);
      if (intValue2 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, intValue2, "Invalid width");
      }

      if (intValue3 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, intValue3, "Invalid height");
      }

      boolean conditionMet = JsonUtils.getBoolean(jsonObject, "interpolate", false);
      return new AnimationMetadataSection(list, intValue2, intValue3, intValue, conditionMet);
   }

   public JsonElement serialize(AnimationMetadataSection arg1, Type arg2, JsonSerializationContext arg3) {
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("frametime", arg1.getFrameTime());
      if (arg1.getFrameWidth() != -1) {
         jsonObject2.addProperty("width", arg1.getFrameWidth());
      }

      if (arg1.getFrameHeight() != -1) {
         jsonObject2.addProperty("height", arg1.getFrameHeight());
      }

      if (arg1.getFrameCount() > 0) {
         JsonArray jsonArray2 = new JsonArray();

         for (int index = 0; index < arg1.getFrameCount(); index++) {
            if (arg1.frameHasTime(index)) {
               JsonObject jsonObject3 = new JsonObject();
               jsonObject3.addProperty("index", arg1.getFrameIndex(index));
               jsonObject3.addProperty("time", arg1.getFrameTimeSingle(index));
               jsonArray2.add(jsonObject3);
            } else {
               jsonArray2.add(new JsonPrimitive(arg1.getFrameIndex(index)));
            }
         }

         jsonObject2.add("frames", jsonArray2);
      }

      return jsonObject2;
   }
}
