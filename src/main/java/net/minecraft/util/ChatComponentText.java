package net.minecraft.util;

public class ChatComponentText extends ChatComponentStyle {
   private final String text;

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ChatComponentText chatComponentText) ? false : this.text.equals(chatComponentText.getText()) && super.equals(other);
      }
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.text;
   }

   public String getText() {
      return this.text;
   }

   public ChatComponentText(String msg) {
      this.text = msg;
   }

   public ChatComponentText createCopy() {
      ChatComponentText chatComponentText2 = new ChatComponentText(this.text);
      chatComponentText2.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent iChatComponent : this.getSiblings()) {
         chatComponentText2.appendSibling(iChatComponent.createCopy());
      }

      return chatComponentText2;
   }

   @Override
   public String toString() {
      return "TextComponent{text='" + this.text + "', siblings=" + this.siblings + ", style=" + this.getChatStyle() + "}";
   }
}
