package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GuiUtilRenderComponents {
   public static List<IChatComponent> splitText(IChatComponent iChatComponent, int i, FontRenderer fontRenderer, boolean flag, boolean flag2) {
      int i2 = 0;
      ChatComponentText chatComponentText = new ChatComponentText("");
      List<IChatComponent> list = Lists.newArrayList();
      List<IChatComponent> list2 = Lists.newArrayList(iChatComponent);

      for (int innerIndex = 0; innerIndex < list2.size(); innerIndex++) {
         IChatComponent chatComponent2 = list2.get(innerIndex);
         String text = chatComponent2.getUnformattedTextForChat();
         boolean conditionMet = false;
         if (text.contains("\n")) {
            int i3 = text.indexOf(10);
            String text2 = text.substring(i3 + 1);
            text = text.substring(0, i3 + 1);
            ChatComponentText chatComponentText2 = new ChatComponentText(text2);
            chatComponentText2.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
            list2.add(innerIndex + 1, chatComponentText2);
            conditionMet = true;
         }

         String text3 = removeFormattingIfDisabled(chatComponent2.getChatStyle().getFormattingCode() + text, flag2);
         String text4 = text3.endsWith("\n") ? text3.substring(0, text3.length() - 1) : text3;
         int intValue = fontRenderer.getInt3(text4);
         ChatComponentText chatComponentText3 = new ChatComponentText(text4);
         chatComponentText3.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
         if (i2 + intValue > i) {
            String text5 = fontRenderer.trimStringToWidth(text3, i - i2, false);
            String text6 = text5.length() < text3.length() ? text3.substring(text5.length()) : null;
            if (text6 != null && text6.length() > 0) {
               int i4 = text5.lastIndexOf(" ");
               if (i4 >= 0 && fontRenderer.getInt3(text3.substring(0, i4)) > 0) {
                  text5 = text3.substring(0, i4);
                  if (flag) {
                     i4++;
                  }

                  text6 = text3.substring(i4);
               } else if (i2 > 0 && !text3.contains(" ")) {
                  text5 = "";
                  text6 = text3;
               }

               ChatComponentText chatComponentText4 = new ChatComponentText(text6);
               chatComponentText4.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
               list2.add(innerIndex + 1, chatComponentText4);
            }

            intValue = fontRenderer.getInt3(text5);
            chatComponentText3 = new ChatComponentText(text5);
            chatComponentText3.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
            conditionMet = true;
         }

         if (i2 + intValue <= i) {
            i2 += intValue;
            chatComponentText.appendSibling(chatComponentText3);
         } else {
            conditionMet = true;
         }

         if (conditionMet) {
            list.add(chatComponentText);
            i2 = 0;
            chatComponentText = new ChatComponentText("");
         }
      }

      list.add(chatComponentText);
      return list;
   }

   public static String removeFormattingIfDisabled(String text, boolean enableFormatting) {
      return !enableFormatting && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(text) : text;
   }
}
