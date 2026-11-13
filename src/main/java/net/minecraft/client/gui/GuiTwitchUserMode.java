package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IStream;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class GuiTwitchUserMode extends GuiScreen {
   private int intValue;
   private static final EnumChatFormatting enumChatFormatting = EnumChatFormatting.DARK_GREEN;
   private final List<IChatComponent> list = Lists.newArrayList();
   private final IStream iStream;
   private static final EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.RED;
   private final ChatUserInfo chatUserInfo;
   private final IChatComponent iChatComponent;
   private static final EnumChatFormatting enumChatFormatting3 = EnumChatFormatting.DARK_PURPLE;

   @Override
   public void initGui() {
      int i = this.width / 3;
      int i2 = i - 130;
      this.buttonList.add(new GuiButton(1, i * 0 + i2 / 2, this.height - 70, 130, 20, I18n.format("stream.userinfo.timeout")));
      this.buttonList.add(new GuiButton(0, i * 1 + i2 / 2, this.height - 70, 130, 20, I18n.format("stream.userinfo.ban")));
      this.buttonList.add(new GuiButton(2, i * 2 + i2 / 2, this.height - 70, 130, 20, I18n.format("stream.userinfo.mod")));
      this.buttonList.add(new GuiButton(5, i * 0 + i2 / 2, this.height - 45, 130, 20, I18n.format("gui.cancel")));
      this.buttonList.add(new GuiButton(3, i * 1 + i2 / 2, this.height - 45, 130, 20, I18n.format("stream.userinfo.unban")));
      this.buttonList.add(new GuiButton(4, i * 2 + i2 / 2, this.height - 45, 130, 20, I18n.format("stream.userinfo.unmod")));
      int i3 = 0;

      for (IChatComponent iChatComponent2 : this.list) {
         i3 = Math.max(i3, this.fontRendererObj.getInt3(iChatComponent2.getFormattedText()));
      }

      this.intValue = this.width / 2 - i3 / 2;
   }

   @Override
   protected void actionPerformed(GuiButton guiButton) throws IOException {
      if (guiButton.enabled) {
         if (guiButton.id == 0) {
            this.iStream.handleString2("/ban " + this.chatUserInfo.displayName);
         } else if (guiButton.id == 3) {
            this.iStream.handleString2("/unban " + this.chatUserInfo.displayName);
         } else if (guiButton.id == 2) {
            this.iStream.handleString2("/mod " + this.chatUserInfo.displayName);
         } else if (guiButton.id == 4) {
            this.iStream.handleString2("/unmod " + this.chatUserInfo.displayName);
         } else if (guiButton.id == 1) {
            this.iStream.handleString2("/timeout " + this.chatUserInfo.displayName);
         }

         this.mc.displayGuiScreen((GuiScreen)null);
      }
   }

   public GuiTwitchUserMode(IStream iStream2, ChatUserInfo chatUserInfo2) {
      this.iStream = iStream2;
      this.chatUserInfo = chatUserInfo2;
      this.iChatComponent = new ChatComponentText(chatUserInfo2.displayName);
      this.list.addAll(createList(chatUserInfo2.modes, chatUserInfo2.subscriptions, iStream2));
   }

   public static IChatComponent createIChatComponent2(ChatUserMode chatUserMode, String text, boolean flag) {
      ChatComponentTranslation chatComponentTranslation = null;
      if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR) {
         chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.administrator");
         chatComponentTranslation.getChatStyle().setColor(enumChatFormatting3);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_BANNED) {
         if (text == null) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.banned");
         } else if (flag) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.banned.self");
         } else {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.banned.other", text);
         }

         chatComponentTranslation.getChatStyle().setColor(enumChatFormatting2);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_BROADCASTER) {
         if (text == null) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.broadcaster");
         } else if (flag) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.broadcaster.self");
         } else {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.broadcaster.other");
         }

         chatComponentTranslation.getChatStyle().setColor(enumChatFormatting);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_MODERATOR) {
         if (text == null) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.moderator");
         } else if (flag) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.moderator.self");
         } else {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.moderator.other", text);
         }

         chatComponentTranslation.getChatStyle().setColor(enumChatFormatting);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_STAFF) {
         chatComponentTranslation = new ChatComponentTranslation("stream.user.mode.staff");
         chatComponentTranslation.getChatStyle().setColor(enumChatFormatting3);
      }

      return chatComponentTranslation;
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.iChatComponent.getUnformattedText(), this.width / 2, 70, 16777215);
      int i3 = 80;

      for (IChatComponent iChatComponent2 : this.list) {
         this.drawString(this.fontRendererObj, iChatComponent2.getFormattedText(), this.intValue, i3, 16777215);
         i3 += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(i, i2, f);
   }

   public static List createList(Set set, Set set2, IStream iStream2) {
      String text = iStream2 == null ? null : iStream2.getString();
      boolean flag = iStream2 != null && iStream2.isActive10();
      ArrayList arrayList = Lists.newArrayList();
      Iterator iterator2 = set.iterator();

      while (iterator2.hasNext()) {
         IChatComponent iChatComponent2 = createIChatComponent2((ChatUserMode)iterator2.next(), text, flag);
         if (iChatComponent2 != null) {
            ChatComponentText chatComponentText = new ChatComponentText("- ");
            chatComponentText.appendSibling(iChatComponent2);
            arrayList.add(chatComponentText);
         }
      }

      iterator2 = set2.iterator();

      while (iterator2.hasNext()) {
         IChatComponent iChatComponent3 = createIChatComponent((ChatUserSubscription)iterator2.next(), text, flag);
         if (iChatComponent3 != null) {
            ChatComponentText chatComponentText2 = new ChatComponentText("- ");
            chatComponentText2.appendSibling(iChatComponent3);
            arrayList.add(chatComponentText2);
         }
      }

      return arrayList;
   }

   public static IChatComponent createIChatComponent(ChatUserSubscription chatUserSubscription, String text, boolean flag) {
      ChatComponentTranslation chatComponentTranslation = null;
      if (chatUserSubscription == ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) {
         if (text == null) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.subscription.subscriber");
         } else if (flag) {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.subscription.subscriber.self");
         } else {
            chatComponentTranslation = new ChatComponentTranslation("stream.user.subscription.subscriber.other", text);
         }

         chatComponentTranslation.getChatStyle().setColor(enumChatFormatting);
      } else if (chatUserSubscription == ChatUserSubscription.TTV_CHAT_USERSUB_TURBO) {
         chatComponentTranslation = new ChatComponentTranslation("stream.user.subscription.turbo");
         chatComponentTranslation.getChatStyle().setColor(enumChatFormatting3);
      }

      return chatComponentTranslation;
   }
}
