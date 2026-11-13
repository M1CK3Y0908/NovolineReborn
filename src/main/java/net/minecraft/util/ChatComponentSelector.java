package net.minecraft.util;

public class ChatComponentSelector extends ChatComponentStyle {
   private final String selector;

   @Override
   public String toString() {
      return "SelectorComponent{pattern='" + this.selector + "', siblings=" + this.siblings + ", style=" + this.getChatStyle() + "}";
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ChatComponentSelector chatComponentSelector)
            ? false
            : this.selector.equals(chatComponentSelector.selector) && super.equals(other);
      }
   }

   public ChatComponentSelector(String selectorIn) {
      this.selector = selectorIn;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.selector;
   }

   public String getSelector() {
      return this.selector;
   }

   public ChatComponentSelector createCopy() {
      ChatComponentSelector chatComponentSelector2 = new ChatComponentSelector(this.selector);
      chatComponentSelector2.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent iChatComponent : this.getSiblings()) {
         chatComponentSelector2.appendSibling(iChatComponent.createCopy());
      }

      return chatComponentSelector2;
   }
}
