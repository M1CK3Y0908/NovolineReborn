package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentTranslation extends ChatComponentStyle {
   private final Object syncLock = new Object();
   public static final Pattern stringVariablePattern = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
   private long lastTranslationUpdateTimeInMilliseconds = -1L;
   List<IChatComponent> children = Lists.newArrayList();
   private final String key;
   private final Object[] formatArgs;

   @Override
   public Iterator<IChatComponent> iterator() {
      this.ensureInitialized();
      return Iterators.concat(createDeepCopyIterator(this.children), createDeepCopyIterator(this.siblings));
   }

   public String getKey() {
      return this.key;
   }

   @Override
   public IChatComponent setChatStyle(ChatStyle style) {
      super.setChatStyle(style);

      for (Object object : this.formatArgs) {
         if (object instanceof IChatComponent) {
            ((IChatComponent)object).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }

      if (this.lastTranslationUpdateTimeInMilliseconds > -1L) {
         for (IChatComponent iChatComponent : this.children) {
            iChatComponent.getChatStyle().setParentStyle(style);
         }
      }

      return this;
   }

   public ChatComponentTranslation(String translationKey, Object... args) {
      this.key = translationKey;
      this.formatArgs = args;

      for (Object object : args) {
         if (object instanceof IChatComponent) {
            ((IChatComponent)object).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }
   }

   protected void initializeFromFormat(String format) {
      Matcher matcher2 = stringVariablePattern.matcher(format);
      int i = 0;
      int i2 = 0;

      try {
         while (matcher2.find(i2)) {
            int i3 = matcher2.start();
            int i4 = matcher2.end();
            if (i3 > i2) {
               ChatComponentText chatComponentText = new ChatComponentText(String.format(format.substring(i2, i3)));
               chatComponentText.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(chatComponentText);
            }

            String text = matcher2.group(2);
            String text2 = format.substring(i3, i4);
            if ("%".equals(text) && "%%".equals(text2)) {
               ChatComponentText chatComponentText2 = new ChatComponentText("%");
               chatComponentText2.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(chatComponentText2);
            } else {
               if (!"s".equals(text)) {
                  throw new ChatComponentTranslationFormatException(this, "Unsupported format: '" + text2 + "'");
               }

               String text3 = matcher2.group(1);
               int i5 = text3 != null ? Integer.parseInt(text3) - 1 : i++;
               if (i5 < this.formatArgs.length) {
                  this.children.add(this.getFormatArgumentAsComponent(i5));
               }
            }

            i2 = i4;
         }

         if (i2 < format.length()) {
            ChatComponentText chatComponentText3 = new ChatComponentText(String.format(format.substring(i2)));
            chatComponentText3.getChatStyle().setParentStyle(this.getChatStyle());
            this.children.add(chatComponentText3);
         }
      } catch (IllegalFormatException illegalFormatException) {
         throw new ChatComponentTranslationFormatException(this, illegalFormatException);
      }
   }

   @Override
   public String getUnformattedTextForChat() {
      this.ensureInitialized();
      StringBuilder stringBuilder = new StringBuilder();

      for (IChatComponent iChatComponent : this.children) {
         stringBuilder.append(iChatComponent.getUnformattedTextForChat());
      }

      return stringBuilder.toString();
   }

   synchronized void ensureInitialized() {
      synchronized (this.syncLock) {
         long lastTranslationUpdateTimeInMilliseconds2 = StatCollector.getLastTranslationUpdateTimeInMilliseconds();
         if (lastTranslationUpdateTimeInMilliseconds2 == this.lastTranslationUpdateTimeInMilliseconds) {
            return;
         }

         this.lastTranslationUpdateTimeInMilliseconds = lastTranslationUpdateTimeInMilliseconds2;
         this.children.clear();
      }

      try {
         this.initializeFromFormat(StatCollector.translateToLocal(this.key));
      } catch (ChatComponentTranslationFormatException chatComponentTranslationFormatException) {
         this.children.clear();

         try {
            this.initializeFromFormat(StatCollector.translateToFallback(this.key));
         } catch (ChatComponentTranslationFormatException chatComponentTranslationFormatException2) {
            throw chatComponentTranslationFormatException;
         }
      }
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ChatComponentTranslation chatComponentTranslation)
            ? false
            : Arrays.equals(this.formatArgs, chatComponentTranslation.formatArgs) && this.key.equals(chatComponentTranslation.key) && super.equals(other);
      }
   }

   @Override
   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      return 31 * i + Arrays.hashCode(this.formatArgs);
   }

   public Object[] getFormatArgs() {
      return this.formatArgs;
   }

   public ChatComponentTranslation createCopy() {
      Object[] objects = new Object[this.formatArgs.length];

      for (int i = 0; i < this.formatArgs.length; i++) {
         if (this.formatArgs[i] instanceof IChatComponent) {
            objects[i] = ((IChatComponent)this.formatArgs[i]).createCopy();
         } else {
            objects[i] = this.formatArgs[i];
         }
      }

      ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation(this.key, objects);
      chatComponentTranslation2.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent iChatComponent : this.getSiblings()) {
         chatComponentTranslation2.appendSibling(iChatComponent.createCopy());
      }

      return chatComponentTranslation2;
   }

   private IChatComponent getFormatArgumentAsComponent(int index) {
      if (index >= this.formatArgs.length) {
         throw new ChatComponentTranslationFormatException(this, index);
      }

      Object object = this.formatArgs[index];
      IChatComponent iChatComponent;
      if (object instanceof IChatComponent) {
         iChatComponent = (IChatComponent)object;
      } else {
         iChatComponent = new ChatComponentText(object == null ? "null" : object.toString());
         iChatComponent.getChatStyle().setParentStyle(this.getChatStyle());
      }

      return iChatComponent;
   }

   @Override
   public String toString() {
      return "TranslatableComponent{key='"
         + this.key
         + "', args="
         + Arrays.toString(this.formatArgs)
         + ", siblings="
         + this.siblings
         + ", style="
         + this.getChatStyle()
         + "}";
   }
}
