package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer implements JsonDeserializer<SoundList> {
   public SoundList deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
      JsonObject jsonObject = JsonUtils.getJsonObject(arg1, "entry");
      SoundList soundList = new SoundList();
      soundList.setReplaceExisting(JsonUtils.getBoolean(jsonObject, "replace", false));
      SoundCategory soundCategory = SoundCategory.getCategory(JsonUtils.getString(jsonObject, "category", SoundCategory.MASTER.getCategoryName()));
      soundList.setSoundCategory(soundCategory);
      Validate.notNull(soundCategory, "Invalid category");
      if (jsonObject.has("sounds")) {
         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "sounds");

         for (int index = 0; index < jsonArray.size(); index++) {
            JsonElement jsonElement = jsonArray.get(index);
            SoundList.SoundEntry soundEntry = new SoundList.SoundEntry();
            if (JsonUtils.isString(jsonElement)) {
               soundEntry.setSoundEntryName(JsonUtils.getString(jsonElement, "sound"));
            } else {
               JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonElement, "sound");
               soundEntry.setSoundEntryName(JsonUtils.getString(jsonObject2, "name"));
               if (jsonObject2.has("type")) {
                  SoundList.SoundEntry.Type soundlist$soundentry$type = SoundList.SoundEntry.Type.getType(JsonUtils.getString(jsonObject2, "type"));
                  Validate.notNull(soundlist$soundentry$type, "Invalid type");
                  soundEntry.setSoundEntryType(soundlist$soundentry$type);
               }

               if (jsonObject2.has("volume")) {
                  float floatValue = JsonUtils.getFloat(jsonObject2, "volume");
                  Validate.isTrue(floatValue > 0.0F, "Invalid volume");
                  soundEntry.setSoundEntryVolume(floatValue);
               }

               if (jsonObject2.has("pitch")) {
                  float floatValue2 = JsonUtils.getFloat(jsonObject2, "pitch");
                  Validate.isTrue(floatValue2 > 0.0F, "Invalid pitch");
                  soundEntry.setSoundEntryPitch(floatValue2);
               }

               if (jsonObject2.has("weight")) {
                  int intValue = JsonUtils.getInt(jsonObject2, "weight");
                  Validate.isTrue(intValue > 0, "Invalid weight");
                  soundEntry.setSoundEntryWeight(intValue);
               }

               if (jsonObject2.has("stream")) {
                  soundEntry.setStreaming(JsonUtils.getBoolean(jsonObject2, "stream"));
               }
            }

            soundList.getSoundList().add(soundEntry);
         }
      }

      return soundList;
   }
}
