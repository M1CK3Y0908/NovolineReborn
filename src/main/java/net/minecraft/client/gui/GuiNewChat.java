package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.util.Room;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class GuiNewChat extends Gui {
   private final List<String> sentMessages = Lists.newArrayList();
   private final List<ChatLine> chatLines = Lists.newArrayList();
   private static final Logger logger = LogManager.getLogger();
   private boolean isScrolled;
   public static int scrollPos;
   private final List<ChatLine> allChatLines = Lists.newArrayList();
   public static final List<GuiNewChat.AnimatedLine> roomLines = new ArrayList<>();
   private final List<GuiNewChat.AnimatedLine> drawnChatLines = new ArrayList<>();
   private final Minecraft mc;
   public static int selectedRoom = 0;

   public boolean getChatOpen() {
      return this.mc.currentScreen instanceof GuiChat;
   }

   public int getChatWidth() {
      return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
   }

   public int getLineCount() {
      return this.getChatHeight() / 9;
   }

   public void printChatMessage(IChatComponent chatComponent) {
      this.printChatMessageWithOptionalDeletion(chatComponent, 0);
   }

   public int getChatHeight() {
      return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
   }

   public void scroll(int amount) {
      scrollPos += amount;
      int count = selectedRoom > 0 ? roomLines.size() + 1 : this.allChatLines.size();
      if (scrollPos > count - this.getLineCount()) {
         scrollPos = count - this.getLineCount();
      }

      if (scrollPos <= 0) {
         scrollPos = 0;
         this.isScrolled = false;
      }
   }

   public void addToSentMessages(String message) {
      if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
         this.sentMessages.add(message);
      }
   }

   public void deleteChatLine(int id) {
      Iterator iterator2 = this.allChatLines.iterator();

      while (iterator2.hasNext()) {
         if (((ChatLine)iterator2.next()).getChatLineID() == id) {
            iterator2.remove();
         }
      }

      iterator2 = this.chatLines.iterator();

      while (iterator2.hasNext()) {
         if (((ChatLine)iterator2.next()).getChatLineID() == id) {
            iterator2.remove();
            break;
         }
      }
   }

   public void refreshChat() {
      this.allChatLines.clear();
      this.resetScroll();

      for (int index = this.chatLines.size() - 1; index >= 0; index--) {
         ChatLine chatLine = this.chatLines.get(index);
         this.setChatLine(chatLine.getChatComponent(), chatLine.getChatLineID(), chatLine.getUpdatedCounter(), true);
      }
   }

   public GuiNewChat(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public List<String> getSentMessages() {
      return this.sentMessages;
   }

   public static int calculateChatboxHeight(float scale) {
      return MathHelper.floor_float(scale * 160 + 20);
   }

   public float getChatScale() {
      return this.mc.gameSettings.chatScale;
   }

   public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
      this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
      logger.info("[CHAT] " + chatComponent.getUnformattedText());
   }

   public void clearChatMessages() {
      this.allChatLines.clear();
      this.chatLines.clear();
      this.sentMessages.clear();
      this.drawnChatLines.clear();
   }

   public void drawChat(int updateCounter) {
      GlStateManager.enableAlpha();
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      float f = this.mc.fontRendererObj.FONT_HEIGHT + scaledResolution.getScaledHeight() - 48;
      float f2 = 0.0F;
      float chatScale2 = this.getChatScale();
      int i = MathHelper.ceiling_float_int(this.getChatWidth() / chatScale2);
      int i2 = this.getChatHeight() - this.mc.fontRendererObj.FONT_HEIGHT;
      boolean flag = this.mc.currentScreen instanceof GuiChat;
      RenderUtils.enableScissor((int)f2, (int)f - i2, (int)(f2 + 320.0F), i2 - 1);
      List list = selectedRoom == 0 ? this.drawnChatLines : roomLines;
      int color = this.mc.currentScreen instanceof GuiChat ? scrollPos : 0;
      int i3 = 0;

      for (int size2 = list.size(); i3 < size2; i3++) {
         GuiNewChat.AnimatedLine animatedLine = (GuiNewChat.AnimatedLine)list.get(i3);
         boolean flag2 = animatedLine.createdAt + 8000L < System.currentTimeMillis();
         animatedLine.animatedY = SmoothingUtils.approachWithMinimumStep(
            animatedLine.animatedY, f + (i3 - list.size() + color) * this.mc.fontRendererObj.FONT_HEIGHT, 8.0F, 0.01F
         );
         animatedLine.alpha = SmoothingUtils.approachWithMinimumStep(animatedLine.alpha, flag2 ? 6.0F : 255.0F, 4.0F, 0.05F);
         if (animatedLine.animatedY > f - i2 - this.mc.fontRendererObj.FONT_HEIGHT
            && animatedLine.animatedY < f + this.mc.fontRendererObj.FONT_HEIGHT
            && (i3 > list.size() - 11 || flag)
            && (flag || animatedLine.alpha > 6.0F)) {
            int i4 = flag ? -1 : -1 + ((int)animatedLine.alpha << 24);
            RenderUtils.drawRect(
               f2,
               animatedLine.animatedY - 1.0F,
               f2 + i,
               animatedLine.animatedY + this.mc.fontRendererObj.FONT_HEIGHT - 1.0F,
               -16777216 + ((flag ? 110 : Math.min((int)animatedLine.alpha, 110)) << 24)
            );
            GlStateManager.enableBlend();
            this.mc.fontRendererObj.drawString(animatedLine.text, f2 + 2.0F, animatedLine.animatedY, i4, true);
            GlStateManager.disableBlend();
         }
      }

      GlStateManager.disableAlpha();
      RenderUtils.disableScissor();
      if (flag) {
         f -= this.getChatHeight() + 4.0F;
         Collection collection = Novoline.getNovoline().getIrcConnection().getGatewayClient().getRooms().values();
         i3 = 1 + collection.size();
         float f3 = (i - 2.0F * (i3 - 1)) / i3;
         float f4 = 0.0F;
         float f5 = 12.0F;
         CFontRenderer cFontRenderer = Fonts.rubikRegularSize14;
         RenderUtils.drawRect(f4, f, f4 + f3, f + f5, -300608235);
         float f6 = f4 + f3 / 2.0F - cFontRenderer.getStringWidth("Minecraft") / 2.0F;
         float f7 = f + f5 / 2.0F - cFontRenderer.getHeight() / 2.0F + 1.0F;
         int i5 = selectedRoom == 0 ? -1 : -7303024;
         float f8 = f7;
         float f9 = f6;
         cFontRenderer.drawString("Minecraft", f9, f8, i5);
         f4 += f3 + 2.0F;
         int i6 = 1;

         for (Room room : (Iterable<Room>) collection) {
            RenderUtils.drawRect(f4, f, f4 + f3, f + f5, -300608235);
            String text2 = "IRC - " + room.getDisplayName();
            f7 = f4 + f3 / 2.0F - cFontRenderer.getStringWidth(text2) / 2.0F;
            float f10 = f + f5 / 2.0F - cFontRenderer.getHeight() / 2.0F + 1.0F;
            i5 = selectedRoom == i6 ? -1 : -7303024;
            f8 = f10;
            f9 = f7;
            cFontRenderer.drawString(text2, f9, f8, i5);
            f4 += f3 + 2.0F;
            i6++;
         }
      }
   }

   public static int calculateChatboxWidth(float scale) {
      return MathHelper.floor_float(scale * 280 + 40);
   }

   public IChatComponent getChatComponent(int mouseX, int mouseY) {
      if (!this.getChatOpen()) {
         return null;
      }

      int scaleFactor = new ScaledResolution(this.mc).getScaleFactor();
      float chatScale2 = this.getChatScale();
      int i = mouseX / scaleFactor - 3;
      int i2 = mouseY / scaleFactor - 40;
      i = MathHelper.floor_float(i / chatScale2);
      i2 = MathHelper.floor_float(i2 / chatScale2);
      if (i >= 0 && i2 >= 0) {
         int i3 = Math.min(this.getLineCount(), this.allChatLines.size());
         if (i <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && i2 < this.mc.fontRendererObj.FONT_HEIGHT * i3 + i3) {
            int i4 = i2 / this.mc.fontRendererObj.FONT_HEIGHT + scrollPos;
            if (i4 >= 0 && i4 < this.allChatLines.size()) {
               ChatLine chatLine2 = this.allChatLines.get(i4);
               int i5 = 0;

               for (IChatComponent iChatComponent : chatLine2.getChatComponent()) {
                  if (iChatComponent instanceof ChatComponentText) {
                     i5 += this.mc.fontRendererObj.getInt3(GuiUtilRenderComponents.removeFormattingIfDisabled(((ChatComponentText)iChatComponent).getText(), false));
                     if (i5 > i) {
                        return iChatComponent;
                     }
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
      if (chatLineId != 0) {
         this.deleteChatLine(chatLineId);
      }

      int i = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
      List list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRendererObj, false, false);
      boolean chatOpen = this.getChatOpen();

      for (IChatComponent iChatComponent : (Iterable<IChatComponent>) list) {
         if (chatOpen && scrollPos > 0) {
            this.isScrolled = true;
            this.scroll(1);
         }

         GuiNewChat.AnimatedLine animatedLine = new GuiNewChat.AnimatedLine();
         if (this.drawnChatLines.size() > 0) {
            animatedLine.animatedY = ((GuiNewChat.AnimatedLine)this.drawnChatLines.get(this.drawnChatLines.size() - 1)).animatedY
               + this.mc.fontRendererObj.FONT_HEIGHT;
         }

         animatedLine.alpha = 6.0F;
         animatedLine.createdAt = System.currentTimeMillis();
         animatedLine.text = iChatComponent.getFormattedText();
         this.drawnChatLines.add(animatedLine);

         while (this.drawnChatLines.size() > 1000) {
            this.drawnChatLines.remove(0);
         }

         this.allChatLines.add(0, new ChatLine(updateCounter, iChatComponent, chatLineId));
      }

      while (this.allChatLines.size() > 100) {
         this.allChatLines.remove(this.allChatLines.size() - 1);
      }

      if (!displayOnly) {
         this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

         while (this.chatLines.size() > 100) {
            this.chatLines.remove(this.chatLines.size() - 1);
         }
      }
   }

   public void resetScroll() {
      scrollPos = 0;
      this.isScrolled = false;
   }

   public static class AnimatedLine {
      public String text;
      public float animatedY;
      public long createdAt;
      public float alpha;
   }
}
