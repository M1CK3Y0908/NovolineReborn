package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook extends GuiScreen {
   private int currPage;
   private int bookImageWidth = 192;
   private int bookImageHeight = 192;
   private GuiScreenBook.NextPageButton buttonNextPage;
   private int bookTotalPages = 1;
   private String bookTitle = "";
   private List<IChatComponent> cachedPageComponents;
   private GuiButton buttonFinalize;
   private int cachedPageIndex = -1;
   private final EntityPlayer editingPlayer;
   private int updateCount;
   private GuiButton buttonSign;
   private final ItemStack bookObj;
   private GuiButton buttonCancel;
   private final boolean bookIsUnsigned;
   private static final Logger logger = LogManager.getLogger();
   private boolean bookIsModified;
   private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
   private GuiButton buttonDone;
   private GuiScreenBook.NextPageButton buttonPreviousPage;
   private NBTTagList bookPages;
   private boolean bookGettingSigned;

   private void updateButtons() {
      this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
      this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
      this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;
      if (this.bookIsUnsigned) {
         this.buttonSign.visible = !this.bookGettingSigned;
         this.buttonCancel.visible = this.bookGettingSigned;
         this.buttonFinalize.visible = this.bookGettingSigned;
         this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
      }
   }

   private void pageInsertIntoCurrent(String text3) {
      String text4 = this.pageGetCurrent() + text3;
      if (this.fontRendererObj.splitStringWidth(text4 + EnumChatFormatting.BLACK + "_", 118) <= 128 && text4.length() < 256) {
         this.pageSetCurrent((String) text4);
      }
   }

   private String pageGetCurrent() {
      return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(bookGuiTextures);
      int i = (this.width - this.bookImageWidth) / 2;
      this.drawTexturedModalRect(i, 2, 0, 0, this.bookImageWidth, this.bookImageHeight);
      if (this.bookGettingSigned) {
         String bookTitle2 = this.bookTitle;
         if (this.bookIsUnsigned) {
            if (this.updateCount / 6 % 2 == 0) {
               bookTitle2 = bookTitle2 + EnumChatFormatting.BLACK + "_";
            } else {
               bookTitle2 = bookTitle2 + EnumChatFormatting.GRAY + "_";
            }
         }

         String text3 = I18n.format("book.editTitle");
         int intValue = this.fontRendererObj.getInt3(text3);
         this.fontRendererObj.getInt(text3, i + 36 + (116 - intValue) / 2, 34, 0);
         int intValue2 = this.fontRendererObj.getInt3((String) bookTitle2);
         this.fontRendererObj.getInt((String) bookTitle2, i + 36 + (116 - intValue2) / 2, 50, 0);
         String text4 = I18n.format("book.byAuthor", this.editingPlayer.getName());
         int intValue3 = this.fontRendererObj.getInt3((String) text4);
         this.fontRendererObj.getInt(EnumChatFormatting.DARK_GRAY + text4, i + 36 + (116 - intValue3) / 2, 60, 0);
         String text5 = I18n.format("book.finalizeWarning");
         this.fontRendererObj.drawSplitString(text5, i + 36, 82, 116, 0);
      } else {
         String text6 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
         String text7 = "";
         if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            text7 = this.bookPages.getStringTagAt(this.currPage);
         }

         if (this.bookIsUnsigned) {
            if (this.fontRendererObj.getBidiFlag()) {
               text7 = text7 + "_";
            } else if (this.updateCount / 6 % 2 == 0) {
               text7 = text7 + EnumChatFormatting.BLACK + "_";
            } else {
               text7 = text7 + EnumChatFormatting.GRAY + "_";
            }
         } else if (this.cachedPageIndex != this.currPage) {
            if (ItemEditableBook.validBookTagContents(this.bookObj.getTagCompound())) {
               try {
                  IChatComponent iChatComponent = net.minecraft.util.IChatComponent.Serializer.jsonToComponent((String) text7);
                  this.cachedPageComponents = iChatComponent != null ? GuiUtilRenderComponents.splitText(iChatComponent, 116, this.fontRendererObj, true, true) : null;
               } catch (JsonParseException jsonParseException) {
                  this.cachedPageComponents = null;
               }
            } else {
               ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
               this.cachedPageComponents = Lists.newArrayList(chatComponentText);
            }

            this.cachedPageIndex = this.currPage;
         }

         int intValue4 = this.fontRendererObj.getInt3(text6);
         this.fontRendererObj.getInt(text6, i - intValue4 + this.bookImageWidth - 44, 18, 0);
         if (this.cachedPageComponents == null) {
            this.fontRendererObj.drawSplitString((String) text7, i + 36, 34, 116, 0);
         } else {
            int i2 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedPageComponents.size());

            for (int i3 = 0; i3 < i2; i3++) {
               IChatComponent iChatComponent2 = this.cachedPageComponents.get(i3);
               this.fontRendererObj.getInt(iChatComponent2.getUnformattedText(), i + 36, 34 + i3 * this.fontRendererObj.FONT_HEIGHT, 0);
            }

            IChatComponent clickedComponentAt = this.getClickedComponentAt(mouseX, mouseY);
            if (clickedComponentAt != null) {
               this.handleComponentHover(clickedComponentAt, mouseX, mouseY);
            }
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
   }

   private void keyTypedInBook(char typedChar, int keyCode) {
      if (GuiScreen.isKeyComboCtrlV(keyCode)) {
         this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
      } else {
         switch (keyCode) {
            case 14:
               String text = this.pageGetCurrent();
               if (text.length() > 0) {
                  this.pageSetCurrent(text.substring(0, text.length() - 1));
               }

               return;
            case 28:
            case 156:
               this.pageInsertIntoCurrent("\n");
               return;
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                  this.pageInsertIntoCurrent(Character.toString(typedChar));
               }
         }
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.updateCount++;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      if (this.bookIsUnsigned) {
         if (this.bookGettingSigned) {
            this.keyTypedInTitle(typedChar, keyCode);
         } else {
            this.keyTypedInBook(typedChar, keyCode);
         }
      }
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.bookIsUnsigned) {
         this.buttonList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton")));
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done")));
         this.buttonList
            .add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton")));
         this.buttonList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel")));
      } else {
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done")));
      }

      int i = (this.width - this.bookImageWidth) / 2;
      this.buttonList.add(this.buttonNextPage = new GuiScreenBook.NextPageButton(1, i + 120, 156, true));
      this.buttonList.add(this.buttonPreviousPage = new GuiScreenBook.NextPageButton(2, i + 38, 156, false));
      this.updateButtons();
   }

   @Override
   protected boolean handleComponentClick(IChatComponent component) {
      ClickEvent clickEvent = component == null ? null : component.getChatStyle().getChatClickEvent();
      if (clickEvent == null) {
         return false;
      }

      if (clickEvent.getAction() == net.minecraft.event.ClickEvent.Action.CHANGE_PAGE) {
         String text = clickEvent.getValue();

         try {
            int i = Integer.parseInt(text) - 1;
            if (i >= 0 && i < this.bookTotalPages && i != this.currPage) {
               this.currPage = i;
               this.updateButtons();
               return true;
            }
         } catch (Throwable throwable) {
         }

         return false;
      } else {
         boolean conditionMet = super.handleComponentClick(component);
         if (conditionMet && clickEvent.getAction() == net.minecraft.event.ClickEvent.Action.RUN_COMMAND) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         return conditionMet;
      }
   }

   public IChatComponent getClickedComponentAt(int mouseX, int mouseY) {
      if (this.cachedPageComponents == null) {
         return null;
      }

      int i = mouseX - (this.width - this.bookImageWidth) / 2 - 36;
      int count = mouseY - 2 - 16 - 16;
      if (i >= 0 && count >= 0) {
         int i2 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedPageComponents.size());
         if (i <= 116 && count < this.mc.fontRendererObj.FONT_HEIGHT * i2 + i2) {
            int i3 = count / this.mc.fontRendererObj.FONT_HEIGHT;
            if (i3 >= 0 && i3 < this.cachedPageComponents.size()) {
               IChatComponent iChatComponent = this.cachedPageComponents.get(i3);
               int i4 = 0;

               for (IChatComponent iChatComponent2 : iChatComponent) {
                  if (iChatComponent2 instanceof ChatComponentText) {
                     i4 += this.mc.fontRendererObj.getInt3(((ChatComponentText)iChatComponent2).getText());
                     if (i4 > i) {
                        return iChatComponent2;
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

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 0) {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.sendBookToServer(false);
         } else if (button.id == 3 && this.bookIsUnsigned) {
            this.bookGettingSigned = true;
         } else if (button.id == 1) {
            if (this.currPage < this.bookTotalPages - 1) {
               this.currPage++;
            } else if (this.bookIsUnsigned) {
               this.addNewPage();
               if (this.currPage < this.bookTotalPages - 1) {
                  this.currPage++;
               }
            }
         } else if (button.id == 2) {
            if (this.currPage > 0) {
               this.currPage--;
            }
         } else if (button.id == 5 && this.bookGettingSigned) {
            this.sendBookToServer(true);
            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (button.id == 4 && this.bookGettingSigned) {
            this.bookGettingSigned = false;
         }

         this.updateButtons();
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         IChatComponent chatComponent = this.getClickedComponentAt(mouseX, mouseY);
         if (this.handleComponentClick(chatComponent)) {
            return;
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public GuiScreenBook(EntityPlayer player, ItemStack book, boolean isUnsigned) {
      this.editingPlayer = player;
      this.bookObj = book;
      this.bookIsUnsigned = isUnsigned;
      if (book.hasTagCompound()) {
         NBTTagCompound nBTTagCompound = book.getTagCompound();
         this.bookPages = nBTTagCompound.getTagList("pages", 8);
         if (this.bookPages != null) {
            this.bookPages = (NBTTagList)this.bookPages.copy();
            this.bookTotalPages = this.bookPages.tagCount();
            if (this.bookTotalPages < 1) {
               this.bookTotalPages = 1;
            }
         }
      }

      if (this.bookPages == null && isUnsigned) {
         this.bookPages = new NBTTagList();
         this.bookPages.appendTag(new NBTTagString(""));
         this.bookTotalPages = 1;
      }
   }

   private void pageSetCurrent(String text3) {
      if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
         this.bookPages.set(this.currPage, new NBTTagString(text3));
         this.bookIsModified = true;
      }
   }

   private void addNewPage() {
      if (this.bookPages != null && this.bookPages.tagCount() < 50) {
         this.bookPages.appendTag(new NBTTagString(""));
         this.bookTotalPages++;
         this.bookIsModified = true;
      }
   }

   private void sendBookToServer(boolean publish) throws IOException {
      if (this.bookIsUnsigned && this.bookIsModified && this.bookPages != null) {
         while (this.bookPages.tagCount() > 1) {
            String text = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
            if (text.length() != 0) {
               break;
            }

            this.bookPages.removeTag(this.bookPages.tagCount() - 1);
         }

         if (this.bookObj.hasTagCompound()) {
            NBTTagCompound nBTTagCompound = this.bookObj.getTagCompound();
            nBTTagCompound.setTag("pages", this.bookPages);
         } else {
            this.bookObj.setTagInfo("pages", this.bookPages);
         }

         String message = "MC|BEdit";
         if (publish) {
            message = "MC|BSign";
            this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
            this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));

            for (int index = 0; index < this.bookPages.tagCount(); index++) {
               String text2 = this.bookPages.getStringTagAt(index);
               text2 = net.minecraft.util.IChatComponent.Serializer.componentToJson(new ChatComponentText(text2));
               this.bookPages.set(index, new NBTTagString(text2));
            }

            this.bookObj.setItem(Items.written_book);
         }

         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeItemStackToBuffer(this.bookObj);
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(message, packetBuffer));
      }
   }

   private void keyTypedInTitle(char c, int i) throws IOException {
      switch (i) {
         case 14:
            if (!this.bookTitle.isEmpty()) {
               this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
               this.updateButtons();
            }

            return;
         case 28:
         case 156:
            if (!this.bookTitle.isEmpty()) {
               this.sendBookToServer(true);
               this.mc.displayGuiScreen((GuiScreen)null);
            }

            return;
         default:
            if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(c)) {
               this.bookTitle = this.bookTitle + Character.toString(c);
               this.updateButtons();
               this.bookIsModified = true;
            }
      }
   }

   static class NextPageButton extends GuiButton {
      private final boolean isNextButton;

      public NextPageButton(int buttonId, int x, int y, boolean isNextButton) {
         super(buttonId, x, y, 23, 13, "");
         this.isNextButton = isNextButton;
      }

      @Override
      public void drawButton(Minecraft mc, int mouseX, int mouseY) {
         if (this.visible) {
            boolean flag = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
            int i = 0;
            int i2 = 192;
            if (flag) {
               i += 23;
            }

            if (!this.isNextButton) {
               i2 += 13;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, i, i2, 23, 13);
         }
      }
   }
}
