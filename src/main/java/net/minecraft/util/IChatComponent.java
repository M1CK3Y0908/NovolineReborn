package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

public interface IChatComponent extends Iterable<IChatComponent> {
   IChatComponent appendText(String text2);

   String getFormattedText();

   String getUnformattedText();

   String getUnformattedTextForChat();

   IChatComponent appendSibling(IChatComponent iChatComponent);

   IChatComponent setChatStyle(ChatStyle chatStyle);

   ChatStyle getChatStyle();

   IChatComponent createCopy();

   List<IChatComponent> getSiblings();

   class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent> {
      private static final Gson GSON;

      public static IChatComponent jsonToComponent(String json) {
         return GSON.fromJson(json, IChatComponent.class);
      }

      public IChatComponent deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
         if (json.isJsonPrimitive()) {
            return new ChatComponentText(json.getAsString());
         }

         if (!json.isJsonObject()) {
            if (json.isJsonArray()) {
               JsonArray asJsonArray = json.getAsJsonArray();
               IChatComponent iChatComponent = null;

               for (JsonElement jsonElement2 : asJsonArray) {
                  IChatComponent iChatComponent2 = this.deserialize(jsonElement2, jsonElement2.getClass(), context);
                  if (iChatComponent == null) {
                     iChatComponent = iChatComponent2;
                  } else {
                     iChatComponent.appendSibling(iChatComponent2);
                  }
               }

               return iChatComponent;
            } else {
               throw new JsonParseException("Don't know how to turn " + json.toString() + " into a Component");
            }
         } else {
            JsonObject asJsonObject = json.getAsJsonObject();
            ChatComponentStyle chatComponentStyle;
            if (asJsonObject.has("text")) {
               chatComponentStyle = new ChatComponentText(asJsonObject.get("text").getAsString());
            } else if (asJsonObject.has("translate")) {
               String asString = asJsonObject.get("translate").getAsString();
               if (asJsonObject.has("with")) {
                  JsonArray asJsonArray2 = asJsonObject.getAsJsonArray("with");
                  Object[] objects = new Object[asJsonArray2.size()];

                  for (int i = 0; i < objects.length; i++) {
                     objects[i] = this.deserialize(asJsonArray2.get(i), type, context);
                     if (objects[i] instanceof ChatComponentText chatComponentText && chatComponentText.getChatStyle().isEmpty() && chatComponentText.getSiblings().isEmpty()) {
                        objects[i] = chatComponentText.getText();
                     }
                  }

                  chatComponentStyle = new ChatComponentTranslation(asString, objects);
               } else {
                  chatComponentStyle = new ChatComponentTranslation(asString);
               }
            } else if (asJsonObject.has("score")) {
               JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("score");
               if (!asJsonObject2.has("name") || !asJsonObject2.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               chatComponentStyle = new ChatComponentScore(JsonUtils.getString(asJsonObject2, "name"), JsonUtils.getString(asJsonObject2, "objective"));
               if (asJsonObject2.has("value")) {
                  ((ChatComponentScore)chatComponentStyle).setValue(JsonUtils.getString(asJsonObject2, "value"));
               }
            } else {
               if (!asJsonObject.has("selector")) {
                  throw new JsonParseException("Don't know how to turn " + json.toString() + " into a Component");
               }

               chatComponentStyle = new ChatComponentSelector(JsonUtils.getString(asJsonObject, "selector"));
            }

            if (asJsonObject.has("extra")) {
               JsonArray asJsonArray3 = asJsonObject.getAsJsonArray("extra");
               if (asJsonArray3.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for (int i2 = 0; i2 < asJsonArray3.size(); i2++) {
                  chatComponentStyle.appendSibling(this.deserialize(asJsonArray3.get(i2), type, context));
               }
            }

            chatComponentStyle.setChatStyle(context.deserialize(json, ChatStyle.class));
            return chatComponentStyle;
         }
      }

      public JsonElement serialize(IChatComponent component, Type type, JsonSerializationContext context) {
         if (component instanceof ChatComponentText && component.getChatStyle().isEmpty() && component.getSiblings().isEmpty()) {
            return new JsonPrimitive(((ChatComponentText)component).getText());
         }

         JsonObject jsonObject2 = new JsonObject();
         if (!component.getChatStyle().isEmpty()) {
            this.serializeChatStyle(component.getChatStyle(), jsonObject2, context);
         }

         if (!component.getSiblings().isEmpty()) {
            JsonArray jsonArray = new JsonArray();

            for (IChatComponent iChatComponent : component.getSiblings()) {
               jsonArray.add(this.serialize(iChatComponent, iChatComponent.getClass(), context));
            }

            jsonObject2.add("extra", jsonArray);
         }

         if (component instanceof ChatComponentText) {
            jsonObject2.addProperty("text", ((ChatComponentText)component).getText());
         } else if (component instanceof ChatComponentTranslation chatComponentTranslation) {
            jsonObject2.addProperty("translate", chatComponentTranslation.getKey());
            if (chatComponentTranslation.getFormatArgs() != null && chatComponentTranslation.getFormatArgs().length > 0) {
               JsonArray jsonArray2 = new JsonArray();

               for (Object object : chatComponentTranslation.getFormatArgs()) {
                  if (object instanceof IChatComponent) {
                     jsonArray2.add(this.serialize((IChatComponent)object, object.getClass(), context));
                  } else {
                     jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
                  }
               }

               jsonObject2.add("with", jsonArray2);
            }
         } else if (component instanceof ChatComponentScore chatComponentScore) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("name", chatComponentScore.getName());
            jsonObject3.addProperty("objective", chatComponentScore.getObjective());
            jsonObject3.addProperty("value", chatComponentScore.getUnformattedTextForChat());
            jsonObject2.add("score", jsonObject3);
         } else {
            if (!(component instanceof ChatComponentSelector chatComponentSelector)) {
               throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
            }

            jsonObject2.addProperty("selector", chatComponentSelector.getSelector());
         }

         return jsonObject2;
      }

      public static String componentToJson(IChatComponent component) {
         return GSON.toJson(component);
      }

      private void serializeChatStyle(ChatStyle style, JsonObject jsonObject, JsonSerializationContext ctx) {
         JsonElement jsonElement = ctx.serialize(style);
         if (jsonElement.isJsonObject()) {
            for (Entry<String, JsonElement> entry : ((JsonObject)jsonElement).entrySet()) {
               jsonObject.add(entry.getKey(), entry.getValue());
            }
         }
      }

      static {
         GsonBuilder gsonBuilder = new GsonBuilder();
         gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
         gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
         gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         GSON = gsonBuilder.create();
      }
   }
}
