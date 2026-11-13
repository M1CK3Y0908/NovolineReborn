package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
   private ChatStyle style;
   protected List<IChatComponent> siblings = Lists.newArrayList();

   @Override
   public IChatComponent setChatStyle(ChatStyle style) {
      this.style = style;
      Iterator iterator2 = this.siblings.iterator();

      while (iterator2.hasNext()) {
         ((IChatComponent)iterator2.next()).getChatStyle().setParentStyle(this.getChatStyle());
      }

      return this;
   }

   @Override
   public ChatStyle getChatStyle() {
      if (this.style == null) {
         this.style = new ChatStyle();
         Iterator iterator2 = this.siblings.iterator();

         while (iterator2.hasNext()) {
            ((IChatComponent)iterator2.next()).getChatStyle().setParentStyle(this.style);
         }
      }

      return this.style;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ChatComponentStyle chatComponentStyle)
            ? false
            : this.siblings.equals(chatComponentStyle.siblings) && this.getChatStyle().equals(chatComponentStyle.getChatStyle());
      }
   }

   public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components) {
      return Iterators.transform(Iterators.concat(Iterators.transform(components.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>() {
         public Iterator<IChatComponent> apply(IChatComponent component) {
            return component.iterator();
         }
      })), new Function<IChatComponent, IChatComponent>() {
         public IChatComponent apply(IChatComponent component) {
            IChatComponent chatComponent = component.createCopy();
            chatComponent.setChatStyle(chatComponent.getChatStyle().createDeepCopy());
            return chatComponent;
         }
      });
   }

   @Override
   public Iterator<IChatComponent> iterator() {
      return Iterators.concat(Iterators.forArray(new ChatComponentStyle[]{this}), createDeepCopyIterator(this.siblings));
   }

   @Override
   public int hashCode() {
      return 31 * this.style.hashCode() + this.siblings.hashCode();
   }

   @Override
   public IChatComponent appendSibling(IChatComponent component) {
      component.getChatStyle().setParentStyle(this.getChatStyle());
      this.siblings.add(component);
      return this;
   }

   @Override
   public IChatComponent appendText(String text) {
      return this.appendSibling(new ChatComponentText(text));
   }

   @Override
   public final String getUnformattedText() {
      StringBuilder stringBuilder = new StringBuilder();

      for (IChatComponent iChatComponent : this) {
         stringBuilder.append(iChatComponent.getUnformattedTextForChat());
      }

      return stringBuilder.toString();
   }

   @Override
   public List<IChatComponent> getSiblings() {
      return this.siblings;
   }

   @Override
   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + "}";
   }

   @Override
   public final String getFormattedText() {
      StringBuilder stringBuilder = new StringBuilder();

      for (IChatComponent iChatComponent : this) {
         stringBuilder.append(iChatComponent.getChatStyle().getFormattingCode());
         stringBuilder.append(iChatComponent.getUnformattedTextForChat());
         stringBuilder.append(EnumChatFormatting.RESET);
      }

      return stringBuilder.toString();
   }
}
