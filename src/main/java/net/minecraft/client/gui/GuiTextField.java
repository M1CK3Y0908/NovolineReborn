package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

public class GuiTextField extends Gui {
   private int selectionEnd;
   private String text = "";
   private int maxStringLength = 32;
   private boolean enableBackgroundDrawing = true;
   private boolean canLoseFocus = true;
   private boolean isEnabled = true;
   private int enabledColor = 14737632;
   private int cursorCounter;
   private int disabledColor = 7368816;
   private boolean visible = true;
   private Predicate<String> validator = Predicates.alwaysTrue();
   private final int id;
   private final FontRenderer fontRendererInstance;
   public int xPosition;
   public int yPosition;
   private GuiPageButtonList.GuiResponder guiResponder;
   private final int width;
   private int cursorPosition;
   private final int height;
   private int lineScrollOffset;
   private boolean isFocused;

   public boolean textboxKeyTyped(char typedChar, int keyCode) {
      if (!this.isFocused) {
         return false;
      }

      if (GuiScreen.isKeyComboCtrlA(keyCode)) {
         this.setCursorPositionEnd();
         this.setSelectionPos(0);
         return true;
      }

      if (GuiScreen.isKeyComboCtrlC(keyCode)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      }

      if (GuiScreen.isKeyComboCtrlV(keyCode)) {
         if (this.isEnabled) {
            this.writeText(GuiScreen.getClipboardString());
         }

         return true;
      } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         if (this.isEnabled) {
            this.writeText("");
         }

         return true;
      } else {
         switch (keyCode) {
            case 14:
               if (GuiScreen.isCtrlKeyDown()) {
                  if (this.isEnabled) {
                     this.deleteWords(-1);
                  }
               } else if (this.isEnabled) {
                  this.deleteFromCursor(-1);
               }

               return true;
            case 199:
               if (GuiScreen.isShiftKeyDown()) {
                  this.setSelectionPos(0);
               } else {
                  this.setCursorPositionZero();
               }

               return true;
            case 203:
               if (GuiScreen.isShiftKeyDown()) {
                  if (GuiScreen.isCtrlKeyDown()) {
                     this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                  } else {
                     this.setSelectionPos(this.getSelectionEnd() - 1);
                  }
               } else if (GuiScreen.isCtrlKeyDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(-1));
               } else {
                  this.moveCursorBy(-1);
               }

               return true;
            case 205:
               if (GuiScreen.isShiftKeyDown()) {
                  if (GuiScreen.isCtrlKeyDown()) {
                     this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                  } else {
                     this.setSelectionPos(this.getSelectionEnd() + 1);
                  }
               } else if (GuiScreen.isCtrlKeyDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(1));
               } else {
                  this.moveCursorBy(1);
               }

               return true;
            case 207:
               if (GuiScreen.isShiftKeyDown()) {
                  this.setSelectionPos(this.text.length());
               } else {
                  this.setCursorPositionEnd();
               }

               return true;
            case 211:
               if (GuiScreen.isCtrlKeyDown()) {
                  if (this.isEnabled) {
                     this.deleteWords(1);
                  }
               } else if (this.isEnabled) {
                  this.deleteFromCursor(1);
               }

               return true;
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                  if (this.isEnabled) {
                     this.writeText(Character.toString(typedChar));
                  }

                  return true;
               } else {
                  return false;
               }
         }
      }
   }

   public void setText(String textIn) {
      if (this.validator.apply(textIn)) {
         if (textIn.length() > this.maxStringLength) {
            this.text = textIn.substring(0, this.maxStringLength);
         } else {
            this.text = textIn;
         }

         this.setCursorPositionEnd();
      }
   }

   public void setGuiResponder(GuiPageButtonList.GuiResponder guiResponder) {
      this.guiResponder = guiResponder;
   }

   public int getNthWordFromCursor(int wordOffset) {
      return this.getNthWordFromPos(wordOffset, this.getCursorPosition());
   }

   public boolean getVisible() {
      return this.visible;
   }

   public void updateCursorCounter() {
      this.cursorCounter++;
   }

   public void update() {
      this.enableBackgroundDrawing = false;
   }

   public String getSelectedText() {
      int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int i2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      return this.text.substring(i, i2);
   }

   public void deleteWords(int wordOffset) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(wordOffset) - this.cursorPosition);
         }
      }
   }

   public int getWidth() {
      return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
   }

   public void deleteFromCursor(int charsToDelete) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean flag = charsToDelete < 0;
            int i = flag ? this.cursorPosition + charsToDelete : this.cursorPosition;
            int i2 = flag ? this.cursorPosition : this.cursorPosition + charsToDelete;
            String message = "";
            if (i >= 0) {
               message = this.text.substring(0, i);
            }

            if (i2 < this.text.length()) {
               message = message + this.text.substring(i2);
            }

            if (this.validator.apply(message)) {
               this.text = message;
               if (flag) {
                  this.moveCursorBy(charsToDelete);
               }

               if (this.guiResponder != null) {
                  this.guiResponder.setEntryValue(this.id, this.text);
               }
            }
         }
      }
   }

   public void drawTextBox() {
      if (this.getVisible()) {
         if (this.getEnableBackgroundDrawing()) {
            handleInt(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
            handleInt(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
         }

         int i = this.isEnabled ? this.enabledColor : this.disabledColor;
         int i2 = this.cursorPosition - this.lineScrollOffset;
         int i3 = this.selectionEnd - this.lineScrollOffset;
         String text = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         boolean flag = i2 >= 0 && i2 <= text.length();
         boolean flag2 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
         int i4 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
         int i5 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
         int i6 = i4;
         if (i3 > text.length()) {
            i3 = text.length();
         }

         if (text.length() > 0) {
            String text2 = flag ? text.substring(0, i2) : text;
            i6 = this.fontRendererInstance.drawStringWithShadow(text2, i4, i5, i);
         }

         boolean flag3 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
         int i7 = i6;
         if (!flag) {
            i7 = i2 > 0 ? i4 + this.width : i4;
         } else if (flag3) {
            i7 = i6 - 1;
            i6--;
         }

         if (text.length() > 0 && flag && i2 < text.length()) {
            this.fontRendererInstance.drawStringWithShadow(text.substring(i2), i6, i5, i);
         }

         if (flag2) {
            if (flag3) {
               Gui.handleInt(i7, i5 - 1, i7 + 1, i5 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
            } else {
               this.fontRendererInstance.drawStringWithShadow("_", i7, i5, i);
            }
         }

         if (i3 != i2) {
            int i8 = i4 + this.fontRendererInstance.getInt3(text.substring(0, i3));
            this.drawCursorVertical(i7, i5 - 1, i8 - 1, i5 + 1 + this.fontRendererInstance.FONT_HEIGHT);
         }
      }
   }

   public boolean getEnableBackgroundDrawing() {
      return this.enableBackgroundDrawing;
   }

   public void setValidator(Predicate<String> validator) {
      this.validator = validator;
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setFocused(boolean focused) {
      if (focused && !this.isFocused) {
         this.cursorCounter = 0;
      }

      this.isFocused = focused;
   }

   public int getId() {
      return this.id;
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      boolean flag = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
      if (this.canLoseFocus) {
         this.setFocused(flag);
      }

      if (this.isFocused && flag && mouseButton == 0) {
         int i = mouseX - this.xPosition;
         if (this.enableBackgroundDrawing) {
            i -= 4;
         }

         String text = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(text, i).length() + this.lineScrollOffset);
      }
   }

   public void moveCursorBy(int numChars) {
      this.setCursorPosition(this.selectionEnd + numChars);
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public int findNthWordFromPos(int wordOffset, int cursorPos, boolean skipSpaces) {
      int cursorPos2 = cursorPos;
      boolean flag = wordOffset < 0;
      int i = Math.abs(wordOffset);

      for (int nestedIndex = 0; nestedIndex < i; nestedIndex++) {
         if (!flag) {
            int count = this.text.length();
            cursorPos2 = this.text.indexOf(32, cursorPos2);
            if (cursorPos2 == -1) {
               cursorPos2 = count;
            } else {
               while (skipSpaces && cursorPos2 < count && this.text.charAt(cursorPos2) == ' ') {
                  cursorPos2++;
               }
            }
         } else {
            while (skipSpaces && cursorPos2 > 0 && this.text.charAt(cursorPos2 - 1) == ' ') {
               cursorPos2--;
            }

            while (cursorPos2 > 0 && this.text.charAt(cursorPos2 - 1) != ' ') {
               cursorPos2--;
            }
         }
      }

      return cursorPos2;
   }

   public void setCanLoseFocus(boolean canLoseFocus) {
      this.canLoseFocus = canLoseFocus;
   }

   public int getNthWordFromPos(int wordOffset, int cursorPos) {
      return this.findNthWordFromPos(wordOffset, cursorPos, true);
   }

   public void update2() {
      this.disabledColor = -1;
   }

   public void setMaxStringLength(int maxLength) {
      this.maxStringLength = maxLength;
      if (this.text.length() > maxLength) {
         this.text = this.text.substring(0, maxLength);
      }
   }

   public void setCursorPosition(int position) {
      this.cursorPosition = position;
      int count = this.text.length();
      this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, count);
      this.setSelectionPos(this.cursorPosition);
   }

   public GuiTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
      this.id = componentId;
      this.fontRendererInstance = fontrendererObj;
      this.xPosition = x;
      this.yPosition = y;
      this.width = par5Width;
      this.height = par6Height;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setTextColor(int color) {
      this.enabledColor = color;
   }

   public int getMaxStringLength() {
      return this.maxStringLength;
   }

   public boolean isFocused() {
      return this.isFocused;
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public void writeText(String textToWrite) {
      String text3 = "";
      String text4 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
      int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int i2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      int i3 = this.maxStringLength - this.text.length() - (i - i2);
      if (this.text.length() > 0) {
         text3 = this.text.substring(0, i) + "";
      }

      int i4;
      if (i3 < text4.length()) {
         text3 = text3 + text4.substring(0, i3);
         i4 = i3;
      } else {
         text3 = text3 + text4;
         i4 = text4.length();
      }

      if (this.text.length() > 0 && i2 < this.text.length()) {
         text3 = text3 + this.text.substring(i2);
      }

      if (this.validator.apply(text3)) {
         this.text = text3;
         this.moveCursorBy(i - this.selectionEnd + i4);
         if (this.guiResponder != null) {
            this.guiResponder.setEntryValue(this.id, this.text);
         }
      }
   }

   public int getSelectionEnd() {
      return this.selectionEnd;
   }

   public String getText() {
      return this.text;
   }

   public void setSelectionPos(int position) {
      int length2 = this.text.length();
      if (position > length2) {
         position = length2;
      }

      if (position < 0) {
         position = 0;
      }

      this.selectionEnd = position;
      if (this.fontRendererInstance != null) {
         if (this.lineScrollOffset > length2) {
            this.lineScrollOffset = length2;
         }

         int width2 = this.getWidth();
         int i = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), width2).length() + this.lineScrollOffset;
         if (position == this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - this.fontRendererInstance.trimStringToWidth(this.text, width2, true).length();
         }

         if (position > i) {
            this.lineScrollOffset += position - i;
         } else if (position <= this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - (this.lineScrollOffset - position);
         }

         this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, length2);
      }
   }

   public void setEnabled(boolean enabled) {
      this.isEnabled = enabled;
   }

   private void drawCursorVertical(int left, int top, int right, int bottom) {
      if (left < right) {
         int left2 = left;
         left = right;
         right = left2;
      }

      if (top < bottom) {
         int top2 = top;
         top = bottom;
         bottom = top2;
      }

      if (right > this.xPosition + this.width) {
         right = this.xPosition + this.width;
      }

      if (left > this.xPosition + this.width) {
         left = this.xPosition + this.width;
      }

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(5387);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(left, bottom, 0.0).endVertex();
      worldRenderer.pos(right, bottom, 0.0).endVertex();
      worldRenderer.pos(right, top, 0.0).endVertex();
      worldRenderer.pos(left, top, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }
}
