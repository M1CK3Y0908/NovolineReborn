package net.minecraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
   public static JsonObject getJsonObject(JsonObject json, String memberName, JsonObject defaultValue) {
      return json.has(memberName) ? getJsonObject(json.get(memberName), memberName) : defaultValue;
   }

   public static boolean isString(JsonObject json, String memberName) {
      return !isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isString();
   }

   public static JsonArray getJsonArray(JsonObject json, String memberName, JsonArray defaultValue) {
      return json.has(memberName) ? getJsonArray(json.get(memberName), memberName) : defaultValue;
   }

   public static String getString(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getString(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
      }
   }

   public static JsonArray getJsonArray(JsonElement json, String memberName) {
      if (json.isJsonArray()) {
         return json.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a JsonArray, was " + toString(json));
      }
   }

   public static String getString(JsonElement json, String memberName) {
      if (json.isJsonPrimitive()) {
         return json.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + toString(json));
      }
   }

   public static JsonObject getJsonObject(JsonObject base, String key) {
      if (base.has(key)) {
         return getJsonObject(base.get(key), key);
      } else {
         throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
      }
   }

   public static float getFloat(JsonElement json, String memberName) {
      if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
         return json.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a Float, was " + toString(json));
      }
   }

   public static String toString(JsonElement json) {
      String text = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(json), "...", 10);
      if (json == null) {
         return "null (missing)";
      }

      if (json.isJsonNull()) {
         return "null (json)";
      }

      if (json.isJsonArray()) {
         return "an array (" + text + ")";
      }

      if (json.isJsonObject()) {
         return "an object (" + text + ")";
      }

      if (json.isJsonPrimitive()) {
         JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
         if (jsonPrimitive.isNumber()) {
            return "a number (" + text + ")";
         }

         if (jsonPrimitive.isBoolean()) {
            return "a boolean (" + text + ")";
         }
      }

      return text;
   }

   public static int getInt(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getInt(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
      }
   }

   public static String getString(JsonObject json, String memberName, String defaultValue) {
      return json.has(memberName) ? getString(json.get(memberName), memberName) : defaultValue;
   }

   public static boolean isString(JsonElement json) {
      return !json.isJsonPrimitive() ? false : json.getAsJsonPrimitive().isString();
   }

   public static JsonArray getJsonArray(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getJsonArray(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonArray");
      }
   }

   public static boolean hasField(JsonObject json, String memberName) {
      return json == null ? false : json.get(memberName) != null;
   }

   public static boolean getBoolean(JsonObject json, String memberName, boolean defaultValue) {
      return json.has(memberName) ? getBoolean(json.get(memberName), memberName) : defaultValue;
   }

   public static float getFloat(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getFloat(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Float");
      }
   }

   public static boolean getBoolean(JsonElement json, String memberName) {
      if (json.isJsonPrimitive()) {
         return json.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a Boolean, was " + toString(json));
      }
   }

   public static boolean getBoolean(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getBoolean(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Boolean");
      }
   }

   public static JsonObject getJsonObject(JsonElement json, String memberName) {
      if (json.isJsonObject()) {
         return json.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + toString(json));
      }
   }

   public static int getInt(JsonElement json, String memberName) {
      if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
         return json.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + toString(json));
      }
   }

   public static boolean isJsonArray(JsonObject json, String memberName) {
      return !hasField(json, memberName) ? false : json.get(memberName).isJsonArray();
   }

   public static boolean isBoolean(JsonObject json, String memberName) {
      return !isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isBoolean();
   }

   public static int getInt(JsonObject json, String memberName, int defaultValue) {
      return json.has(memberName) ? getInt(json.get(memberName), memberName) : defaultValue;
   }

   public static boolean isJsonPrimitive(JsonObject json, String memberName) {
      return !hasField(json, memberName) ? false : json.get(memberName).isJsonPrimitive();
   }

   public static float getFloat(JsonObject json, String memberName, float defaultValue) {
      return json.has(memberName) ? getFloat(json.get(memberName), memberName) : defaultValue;
   }
}
