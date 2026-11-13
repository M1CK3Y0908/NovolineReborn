package wtf.novoline.gui.dashboard;

import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class DashboardTextField implements DashboardWidget {
   private float x;
   private float y;
   private final float width;
   private final float height;
   private String content;
   private final String placeholder;
   private boolean focused;
   private int selectionAnchor;
   private int cursorOffset;
   private final int maxLength;

   @Override
   public float getX() {
      return this.x;
   }

   @Override
   public void keyTyped(char c, int i) {
      if (this.focused) {
         if (i == 28) {
            this.focused = false;
            this.cursorOffset = 0;
            this.selectionAnchor = -1;
            return;
         }

         if (GuiScreen.isKeyComboCtrlA(i)) {
            this.cursorOffset = 0;
            this.selectionAnchor = this.content.length();
            return;
         }

         if (GuiScreen.isKeyComboCtrlC(i)) {
            this.deleteOrCopySelection(false, true);
            return;
         }

         if (GuiScreen.isKeyComboCtrlX(i)) {
            this.deleteOrCopySelection(true, true);
            return;
         }

         if (GuiScreen.isKeyComboCtrlV(i)) {
            if (this.selectionAnchor != -1) {
               this.deleteOrCopySelection(true, false);
            }

            this.content = this.content.substring(0, this.content.length() - this.cursorOffset)
               + GuiScreen.getClipboardString()
               + this.content.substring(this.content.length() - this.cursorOffset);
         }

         if (i == 14) {
            this.deleteOrCopySelection(true, false);
            return;
         }

         if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            if (this.selectionAnchor != -1) {
               this.deleteOrCopySelection(true, false);
            }

            this.content = this.content.substring(0, this.content.length() - this.cursorOffset)
               + c
               + this.content.substring(this.content.length() - this.cursorOffset);
            return;
         }

         boolean shiftKeyDown = GuiScreen.isShiftKeyDown();
         if (i == 203) {
            if (shiftKeyDown) {
               if (this.selectionAnchor < 0) {
                  this.selectionAnchor = this.cursorOffset;
               }

               if (this.selectionAnchor < this.content.length()) {
                  this.selectionAnchor++;
               }
            } else {
               if (this.selectionAnchor > -1) {
                  this.cursorOffset = this.selectionAnchor;
               }

               this.selectionAnchor = -1;
               if (this.cursorOffset < this.content.length()) {
                  this.cursorOffset++;
               }
            }
         } else if (i == 205) {
            if (shiftKeyDown) {
               if (this.selectionAnchor < 0) {
                  this.selectionAnchor = this.cursorOffset;
               }

               if (this.selectionAnchor > 0) {
                  this.selectionAnchor--;
               }
            } else {
               if (this.selectionAnchor > -1) {
                  this.cursorOffset = this.selectionAnchor;
               }

               this.selectionAnchor = -1;
               if (this.cursorOffset > 0) {
                  this.cursorOffset--;
               }
            }
         }
      }
   }

   private void deleteOrCopySelection(boolean flag, boolean flag2) {
      if (this.cursorOffset - this.selectionAnchor <= this.content.length()) {
         if (this.selectionAnchor != -1) {
            int i = this.selectionAnchor - this.cursorOffset;
            if (i > 0) {
               if (flag2) {
                  GuiScreen.setClipboardString(
                     this.content.substring(this.content.length() - this.cursorOffset - i, this.content.length() - this.cursorOffset)
                  );
               }

               if (flag) {
                  this.content = this.content.substring(0, this.content.length() - this.cursorOffset - i)
                     + this.content.substring(this.content.length() - this.cursorOffset);
               }
            } else {
               if (flag2) {
                  GuiScreen.setClipboardString(
                     this.content.substring(this.content.length() - this.cursorOffset, this.content.length() - this.cursorOffset - i)
                  );
               }

               if (flag) {
                  this.content = this.content.substring(0, this.content.length() - this.cursorOffset)
                     + this.content.substring(this.content.length() - this.cursorOffset - i);
                  this.cursorOffset += i;
               }
            }
         } else if (flag) {
            this.content = this.content.substring(0, this.content.length() - this.cursorOffset - 1)
               + this.content.substring(this.content.length() - this.cursorOffset);
         }

         if (flag) {
            this.selectionAnchor = -1;
         }
      }
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
   }

   public float getHeight() {
      return this.height;
   }

   public String getContent() {
      return this.content;
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      this.focused = f >= this.getX() && f <= this.getX() + this.width && f2 >= this.getY() && f2 <= this.getY() + this.height;
      if (this.focused) {
         return true;
      }

      this.cursorOffset = 0;
      this.selectionAnchor = -1;
      return false;
   }

   @Override
   public void draw(float f, float f2, float f3) {
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.width, this.height, 2.0F, new Color(18, 19, 21, 255).getRGB());
      filledPolygonRenderer.draw();
      float f4 = this.getX() + 4.0F;
      float f5 = this.getY() + this.getHeight() / 2.0F - 1.0F;
      if (this.content.length() > this.maxLength) {
         this.content = this.content.substring(0, this.maxLength);
      }

      RenderUtils.enableScissorDoubleScale((int)this.getX() + 2, (int)this.getY(), (int)this.getWidth() - 4, (int)this.getHeight());
      CFontRenderer cFontRenderer = Fonts.rubikRegularSize14;
      float f6 = cFontRenderer.getStringWidth(this.content) > this.getWidth() - 8.0F ? cFontRenderer.getStringWidth(this.content) - (this.getWidth() - 8.0F) : 0.0F;
      if (this.content.isEmpty() && !this.focused) {
         cFontRenderer.drawString(this.placeholder, f4, f5, -4210235);
      } else {
         cFontRenderer.drawString(this.content, f4 - f6, f5, -1);
      }

      if (this.focused) {
         float stringWidth = cFontRenderer.getStringWidth(this.content);
         if (this.cursorOffset != 0) {
            String text = this.content.substring(this.content.length() - this.cursorOffset);
            float stringWidth2 = cFontRenderer.getStringWidth(text);
            RenderUtils.drawLine(f4 + stringWidth - stringWidth2, this.getY() + 2.0F, f4 + stringWidth - stringWidth2, this.getY() + this.getHeight() - 2.0F, -2039584);
         } else if (System.nanoTime() / 1000000L % 1000L < 500L) {
            cFontRenderer.drawString("_", f4 + stringWidth - f6, f5, -1);
         }

         if (this.selectionAnchor > -1) {
            String text2 = this.content.substring(this.content.length() - this.selectionAnchor);
            float stringWidth3 = cFontRenderer.getStringWidth(text2);
            String text3 = this.content.substring(this.content.length() - this.cursorOffset);
            float stringWidth4 = cFontRenderer.getStringWidth(text3);
            RenderUtils.drawSelectionHighlight(f4 + stringWidth - stringWidth3, this.getY() + 2.0F, f4 + stringWidth - stringWidth4, this.getY() + this.getHeight() - 2.0F);
         }
      }

      RenderUtils.disableScissor();
   }

   @Override
   public float getY() {
      return this.y;
   }

   public float getWidth() {
      return this.width;
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   public boolean isEmpty() {
      return this.content.isEmpty();
   }

   public DashboardTextField(float f, float f2, float f3, float f4, String text, int i) {
      this.x = f;
      this.y = f2;
      this.width = f3;
      this.height = f4;
      this.content = "";
      this.placeholder = text;
      this.focused = false;
      this.selectionAnchor = 0;
      this.cursorOffset = 0;
      this.maxLength = i;
   }
}
