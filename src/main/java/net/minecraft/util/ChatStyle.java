package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;

public class ChatStyle {
   private Boolean underlined;
   private static final ChatStyle rootStyle = new ChatStyle() {
      @Override
      public ChatStyle setChatClickEvent(ClickEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle createShallowCopy() {
         return this;
      }

      @Override
      public ChatStyle setColor(EnumChatFormatting color) {
         throw new UnsupportedOperationException();
      }

      @Override
      public HoverEvent getChatHoverEvent() {
         return null;
      }

      @Override
      public boolean getObfuscated() {
         return false;
      }

      @Override
      public ClickEvent getChatClickEvent() {
         return null;
      }

      @Override
      public boolean getStrikethrough() {
         return false;
      }

      @Override
      public ChatStyle setChatHoverEvent(HoverEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String getInsertion() {
         return null;
      }

      @Override
      public ChatStyle setBold(Boolean bool) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle createDeepCopy() {
         return this;
      }

      @Override
      public String getFormattingCode() {
         return "";
      }

      @Override
      public ChatStyle setUnderlined(Boolean bool2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setStrikethrough(Boolean bool3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setObfuscated(Boolean bool4) {
         throw new UnsupportedOperationException();
      }

      @Override
      public EnumChatFormatting getColor() {
         return null;
      }

      @Override
      public ChatStyle setParentStyle(ChatStyle parent) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean getBold() {
         return false;
      }

      @Override
      public boolean getUnderlined() {
         return false;
      }

      @Override
      public ChatStyle setItalic(Boolean bool5) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
         return "Style.ROOT";
      }

      @Override
      public boolean getItalic() {
         return false;
      }
   };
   private ClickEvent chatClickEvent;
   private Boolean obfuscated;
   private HoverEvent chatHoverEvent;
   private Boolean strikethrough;
   private ChatStyle parentStyle;
   private Boolean bold;
   private EnumChatFormatting color;
   private String insertion;
   private Boolean italic;

   public boolean getItalic() {
      return this.italic == null ? this.getParent().getItalic() : this.italic;
   }

   public ChatStyle setStrikethrough(Boolean strikethrough) {
      this.strikethrough = strikethrough;
      return this;
   }

   public ChatStyle setParentStyle(ChatStyle parent) {
      this.parentStyle = parent;
      return this;
   }

   public String getFormattingCode() {
      if (this.isEmpty()) {
         return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
      }

      StringBuilder stringBuilder = new StringBuilder();
      if (this.getColor() != null) {
         stringBuilder.append(this.getColor());
      }

      if (this.getBold()) {
         stringBuilder.append(EnumChatFormatting.BOLD);
      }

      if (this.getItalic()) {
         stringBuilder.append(EnumChatFormatting.ITALIC);
      }

      if (this.getUnderlined()) {
         stringBuilder.append(EnumChatFormatting.UNDERLINE);
      }

      if (this.getObfuscated()) {
         stringBuilder.append(EnumChatFormatting.OBFUSCATED);
      }

      if (this.getStrikethrough()) {
         stringBuilder.append(EnumChatFormatting.STRIKETHROUGH);
      }

      return stringBuilder.toString();
   }

   public ChatStyle createDeepCopy() {
      ChatStyle chatStyle = new ChatStyle();
      chatStyle.setBold(this.getBold());
      chatStyle.setItalic(this.getItalic());
      chatStyle.setStrikethrough(this.getStrikethrough());
      chatStyle.setUnderlined(this.getUnderlined());
      chatStyle.setObfuscated(this.getObfuscated());
      chatStyle.setColor(this.getColor());
      chatStyle.setChatClickEvent(this.getChatClickEvent());
      chatStyle.setChatHoverEvent(this.getChatHoverEvent());
      chatStyle.setInsertion(this.getInsertion());
      return chatStyle;
   }

   public ChatStyle setInsertion(String insertion) {
      this.insertion = insertion;
      return this;
   }

   public String getInsertion() {
      return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
   }

   public boolean isEmpty() {
      return this.bold == null
         && this.italic == null
         && this.strikethrough == null
         && this.underlined == null
         && this.obfuscated == null
         && this.color == null
         && this.chatClickEvent == null
         && this.chatHoverEvent == null;
   }

   public ClickEvent getChatClickEvent() {
      return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
   }

   @Override
   public int hashCode() {
      int i = this.color.hashCode();
      i = 31 * i + this.bold.hashCode();
      i = 31 * i + this.italic.hashCode();
      i = 31 * i + this.underlined.hashCode();
      i = 31 * i + this.strikethrough.hashCode();
      i = 31 * i + this.obfuscated.hashCode();
      i = 31 * i + this.chatClickEvent.hashCode();
      i = 31 * i + this.chatHoverEvent.hashCode();
      return 31 * i + this.insertion.hashCode();
   }

   public ChatStyle setItalic(Boolean italic) {
      this.italic = italic;
      return this;
   }

   public boolean getBold() {
      return this.bold == null ? this.getParent().getBold() : this.bold;
   }

   public HoverEvent getChatHoverEvent() {
      return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
   }

   public ChatStyle setChatClickEvent(ClickEvent event) {
      this.chatClickEvent = event;
      return this;
   }

   public EnumChatFormatting getColor() {
      return this.color == null ? this.getParent().getColor() : this.color;
   }

   public ChatStyle setUnderlined(Boolean underlined) {
      this.underlined = underlined;
      return this;
   }

   public boolean getUnderlined() {
      return this.underlined == null ? this.getParent().getUnderlined() : this.underlined;
   }

   private ChatStyle getParent() {
      return this.parentStyle == null ? rootStyle : this.parentStyle;
   }

   public ChatStyle setBold(Boolean boldIn) {
      this.bold = boldIn;
      return this;
   }

   public boolean getStrikethrough() {
      return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough;
   }

   public ChatStyle setChatHoverEvent(HoverEvent event) {
      this.chatHoverEvent = event;
      return this;
   }

   public ChatStyle setColor(EnumChatFormatting color) {
      this.color = color;
      return this;
   }

   public boolean getObfuscated() {
      return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated;
   }

   public ChatStyle createShallowCopy() {
      ChatStyle chatStyle = new ChatStyle();
      chatStyle.bold = this.bold;
      chatStyle.italic = this.italic;
      chatStyle.strikethrough = this.strikethrough;
      chatStyle.underlined = this.underlined;
      chatStyle.obfuscated = this.obfuscated;
      chatStyle.color = this.color;
      chatStyle.chatClickEvent = this.chatClickEvent;
      chatStyle.chatHoverEvent = this.chatHoverEvent;
      chatStyle.parentStyle = this.parentStyle;
      chatStyle.insertion = this.insertion;
      return chatStyle;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ChatStyle chatStyle)
            ? false
            : this.getBold() == chatStyle.getBold()
               && this.getColor() == chatStyle.getColor()
               && this.getItalic() == chatStyle.getItalic()
               && this.getObfuscated() == chatStyle.getObfuscated()
               && this.getStrikethrough() == chatStyle.getStrikethrough()
               && this.getUnderlined() == chatStyle.getUnderlined()
               && (this.getChatClickEvent() != null ? this.getChatClickEvent().equals(chatStyle.getChatClickEvent()) : chatStyle.getChatClickEvent() == null)
               && (this.getChatHoverEvent() != null ? this.getChatHoverEvent().equals(chatStyle.getChatHoverEvent()) : chatStyle.getChatHoverEvent() == null)
               && (this.getInsertion() != null ? this.getInsertion().equals(chatStyle.getInsertion()) : chatStyle.getInsertion() == null);
      }
   }

   public ChatStyle setObfuscated(Boolean obfuscated) {
      this.obfuscated = obfuscated;
      return this;
   }

   @Override
   public String toString() {
      boolean flag = this.parentStyle != null;
      EnumChatFormatting color2 = this.color;
      Boolean bold2 = this.bold;
      Boolean italic2 = this.italic;
      Boolean underlined2 = this.underlined;
      Boolean obfuscated2 = this.obfuscated;
      String insertion2 = this.getInsertion();
      return "Style{hasParent="
         + flag
         + ", color="
         + color2
         + ", bold="
         + bold2
         + ", italic="
         + italic2
         + ", underlined="
         + underlined2
         + ", obfuscated="
         + obfuscated2
         + ", clickEvent="
         + this.getChatClickEvent()
         + ", hoverEvent="
         + this.getChatHoverEvent()
         + ", insertion="
         + insertion2
         + "}";
   }

   public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle> {
      public ChatStyle deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
         if (json.isJsonObject()) {
            ChatStyle chatStyle = new ChatStyle();
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject == null) {
               return null;
            }

            if (jsonObject.has("bold")) {
               chatStyle.bold = jsonObject.get("bold").getAsBoolean();
            }

            if (jsonObject.has("italic")) {
               chatStyle.italic = jsonObject.get("italic").getAsBoolean();
            }

            if (jsonObject.has("underlined")) {
               chatStyle.underlined = jsonObject.get("underlined").getAsBoolean();
            }

            if (jsonObject.has("strikethrough")) {
               chatStyle.strikethrough = jsonObject.get("strikethrough").getAsBoolean();
            }

            if (jsonObject.has("obfuscated")) {
               chatStyle.obfuscated = jsonObject.get("obfuscated").getAsBoolean();
            }

            if (jsonObject.has("color")) {
               chatStyle.color = context.deserialize(jsonObject.get("color"), EnumChatFormatting.class);
            }

            if (jsonObject.has("insertion")) {
               chatStyle.insertion = jsonObject.get("insertion").getAsString();
            }

            if (jsonObject.has("clickEvent")) {
               JsonObject jsonObject2 = jsonObject.getAsJsonObject("clickEvent");
               if (jsonObject2 != null) {
                  JsonPrimitive jsonPrimitive = jsonObject2.getAsJsonPrimitive("action");
                  Action clickevent$action = jsonPrimitive == null ? null : Action.getValueByCanonicalName(jsonPrimitive.getAsString());
                  JsonPrimitive jsonPrimitive2 = jsonObject2.getAsJsonPrimitive("value");
                  String text = jsonPrimitive2 == null ? null : jsonPrimitive2.getAsString();
                  if (clickevent$action != null && text != null && clickevent$action.shouldAllowInChat()) {
                     chatStyle.chatClickEvent = new ClickEvent(clickevent$action, text);
                  }
               }
            }

            if (jsonObject.has("hoverEvent")) {
               JsonObject jsonObject3 = jsonObject.getAsJsonObject("hoverEvent");
               if (jsonObject3 != null) {
                  JsonPrimitive jsonPrimitive3 = jsonObject3.getAsJsonPrimitive("action");
                  net.minecraft.event.HoverEvent.Action hoverevent$action = jsonPrimitive3 == null
                     ? null
                     : net.minecraft.event.HoverEvent.Action.getValueByCanonicalName(jsonPrimitive3.getAsString());
                  IChatComponent chatComponent = context.deserialize(jsonObject3.get("value"), IChatComponent.class);
                  if (hoverevent$action != null && chatComponent != null && hoverevent$action.shouldAllowInChat()) {
                     chatStyle.chatHoverEvent = new HoverEvent(hoverevent$action, chatComponent);
                  }
               }
            }

            return chatStyle;
         } else {
            return null;
         }
      }

      public JsonElement serialize(ChatStyle style, Type type, JsonSerializationContext context) {
         if (style.isEmpty()) {
            return null;
         }

         JsonObject jsonObject4 = new JsonObject();
         if (style.bold != null) {
            jsonObject4.addProperty("bold", style.bold);
         }

         if (style.italic != null) {
            jsonObject4.addProperty("italic", style.italic);
         }

         if (style.underlined != null) {
            jsonObject4.addProperty("underlined", style.underlined);
         }

         if (style.strikethrough != null) {
            jsonObject4.addProperty("strikethrough", style.strikethrough);
         }

         if (style.obfuscated != null) {
            jsonObject4.addProperty("obfuscated", style.obfuscated);
         }

         if (style.color != null) {
            jsonObject4.add("color", context.serialize(style.color));
         }

         if (style.insertion != null) {
            jsonObject4.add("insertion", context.serialize(style.insertion));
         }

         if (style.chatClickEvent != null) {
            JsonObject jsonObject5 = new JsonObject();
            jsonObject5.addProperty("action", style.chatClickEvent.getAction().getCanonicalName());
            jsonObject5.addProperty("value", style.chatClickEvent.getValue());
            jsonObject4.add("clickEvent", jsonObject5);
         }

         if (style.chatHoverEvent != null) {
            JsonObject jsonObject6 = new JsonObject();
            jsonObject6.addProperty("action", style.chatHoverEvent.getAction().getCanonicalName());
            jsonObject6.add("value", context.serialize(style.chatHoverEvent.getValue()));
            jsonObject4.add("hoverEvent", jsonObject6);
         }

         return jsonObject4;
      }
   }
}
