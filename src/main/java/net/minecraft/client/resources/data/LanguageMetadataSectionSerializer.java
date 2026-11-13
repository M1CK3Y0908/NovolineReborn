package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.util.JsonUtils;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer<LanguageMetadataSection> {
   @Override
   public String getSectionName() {
      return "language";
   }

   public LanguageMetadataSection deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
      JsonObject asJsonObject = arg1.getAsJsonObject();
      HashSet hashSet = Sets.newHashSet();

      for (Entry entry : asJsonObject.entrySet()) {
         String text = (String)entry.getKey();
         JsonObject jsonObject = JsonUtils.getJsonObject((JsonElement)entry.getValue(), "language");
         String string = JsonUtils.getString(jsonObject, "region");
         String string2 = JsonUtils.getString(jsonObject, "name");
         boolean booleanValue = JsonUtils.getBoolean(jsonObject, "bidirectional", false);
         if (string.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + text + "'->region: empty value");
         }

         if (string2.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + text + "'->name: empty value");
         }

         if (!hashSet.add(new Language(text, string, string2, booleanValue))) {
            throw new JsonParseException("Duplicate language->'" + text + "' defined");
         }
      }

      return new LanguageMetadataSection(hashSet);
   }
}
