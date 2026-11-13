package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public abstract class GuiSlot {
   protected boolean showListBackground = true;
   protected float amountScrolled;
   protected int initialClickY = -2;
   protected int mouseY;
   protected int selectedElement = -1;
   private int scrollDownButtonID;
   protected int headerPadding;
   protected boolean visible = true;
   protected boolean hasListHeader;
   protected boolean showSelectionBox = true;
   private int scrollUpButtonID;
   private boolean enabled = true;
   protected final Minecraft mc;
   protected int mouseX;
   protected int width;
   protected int height;
   protected float scrollMultiplier;
   protected int top;
   protected int bottom;
   protected final int slotHeight;
   protected int left;
   protected long lastClicked;
   protected int right;

   protected int getScrollBarX() {
      return this.width / 2 + 124;
   }

   protected void bindAmountScrolled() {
      this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0F, this.getMaxScroll());
   }

   protected void handleInt3(int i, int i2) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer2.pos(this.left, i2, 0.0).tex(0.0, i2 / 32.0F).color(64, 64, 64, 255).endVertex();
      worldRenderer2.pos(this.left + this.width, i2, 0.0).tex(this.width / 32.0F, i2 / 32.0F).color(64, 64, 64, 255).endVertex();
      worldRenderer2.pos(this.left + this.width, i, 0.0).tex(this.width / 32.0F, i / 32.0F).color(64, 64, 64, 255).endVertex();
      worldRenderer2.pos(this.left, i, 0.0).tex(0.0, i / 32.0F).color(64, 64, 64, 255).endVertex();
      instance.draw();
   }

   public void setEnabled(boolean enabledIn) {
      this.enabled = enabledIn;
   }

   protected void handleInt2(int i) {
      this.hasListHeader = true;
      this.headerPadding = i;
   }

   public GuiSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
      this.mc = mcIn;
      this.width = width;
      this.height = height;
      this.top = topIn;
      this.bottom = bottomIn;
      this.slotHeight = slotHeightIn;
      this.left = 0;
      this.right = width;
   }

   public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn) {
      this.scrollUpButtonID = scrollUpButtonIDIn;
      this.scrollDownButtonID = scrollDownButtonIDIn;
   }

   public void scrollBy(int amount) {
      this.amountScrolled += amount;
      this.bindAmountScrolled();
      this.initialClickY = -2;
   }

   public int getAmountScrolled() {
      return (int)this.amountScrolled;
   }

   protected void handleHeaderClick(int mouseX, int mouseY) {
   }

   protected void drawSelectionBox(int i, int i2, int mouseXIn, int mouseYIn) {
      int size = this.getSize();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();

      for (int innerIndex = 0; innerIndex < size; innerIndex++) {
         int i3 = i2 + innerIndex * this.slotHeight + this.headerPadding;
         int i4 = this.slotHeight - 4;
         if (i3 > this.bottom || i3 + i4 < this.top) {
            this.noop(innerIndex, i3);
         }

         if (this.showSelectionBox && this.isSelected(innerIndex)) {
            int i5 = this.left + (this.width / 2 - this.getListWidth() / 2);
            int i6 = this.left + this.width / 2 + this.getListWidth() / 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture2D();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(i5, i3 + i4 + 2, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i6, i3 + i4 + 2, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i6, i3 - 2, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i5, i3 - 2, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer.pos(i5 + 1, i3 + i4 + 1, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i6 - 1, i3 + i4 + 1, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i6 - 1, i3 - 1, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(i5 + 1, i3 - 1, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
         }

         if (!(this instanceof GuiResourcePackList) || i3 >= this.top - this.slotHeight && i3 <= this.bottom) {
            this.drawSlot(innerIndex, i, i3, i4, mouseXIn, mouseYIn);
         }
      }
   }

   protected abstract int getSize();

   protected void noop(int i, int i2) {
   }

   public void setSelected(int slotIndex, int x, int y) {
      this.width = slotIndex;
      this.height = x;
      this.top = 32;
      this.bottom = y;
      this.left = 0;
      this.right = slotIndex;
   }

   public int getSlotIndexFromScreenCoords(int i, int i2) {
      int i3 = this.left + this.width / 2 - this.getListWidth() / 2;
      int i4 = this.left + this.width / 2 + this.getListWidth() / 2;
      int i5 = i2 - this.top - this.headerPadding + (int)this.amountScrolled - 4;
      int i6 = i5 / this.slotHeight;
      return i < this.getScrollBarX()
            && i >= i3
            && i <= i4
            && i6 >= 0
            && i5 >= 0
            && i6 < this.getSize()
         ? i6
         : -1;
   }

   public void drawScreen(int mouseXIn, int mouseYIn, float f) {
      if (this.visible) {
         this.mouseX = mouseXIn;
         this.mouseY = mouseYIn;
         this.drawBackground();
         int scrollBarX = this.getScrollBarX();
         int i = scrollBarX + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = instance.getWorldRenderer();
         this.drawContainerBackground(instance);
         int i2 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int i3 = this.top + 4 - (int)this.amountScrolled;
         if (this.hasListHeader) {
            this.drawListHeader(i2, i3, instance);
         }

         this.drawSelectionBox(i2, i3, mouseXIn, mouseYIn);
         GlStateManager.disableDepth();
         this.handleInt3(0, this.top);
         this.handleInt3(this.bottom, this.height);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer2.pos(this.left, this.top + 4, 0.0).tex(0.0, 1.0).color(0, 0, 0, 0).endVertex();
         worldRenderer2.pos(this.right, this.top + 4, 0.0).tex(1.0, 1.0).color(0, 0, 0, 0).endVertex();
         worldRenderer2.pos(this.right, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
         worldRenderer2.pos(this.left, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
         instance.draw();
         worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer2.pos(this.left, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer2.pos(this.right, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
         worldRenderer2.pos(this.right, this.bottom - 4, 0.0).tex(1.0, 0.0).color(0, 0, 0, 0).endVertex();
         worldRenderer2.pos(this.left, this.bottom - 4, 0.0).tex(0.0, 0.0).color(0, 0, 0, 0).endVertex();
         instance.draw();
         int maxScroll = this.getMaxScroll();
         if (maxScroll > 0) {
            int i4 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            i4 = MathHelper.clamp_int(i4, 32, this.bottom - this.top - 8);
            int i5 = (int)this.amountScrolled * (this.bottom - this.top - i4) / maxScroll + this.top;
            if (i5 < this.top) {
               i5 = this.top;
            }

            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer2.pos(scrollBarX, this.bottom, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(i, this.bottom, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(i, this.top, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(scrollBarX, this.top, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            instance.draw();
            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer2.pos(scrollBarX, i5 + i4, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer2.pos(i, i5 + i4, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            worldRenderer2.pos(i, i5, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            worldRenderer2.pos(scrollBarX, i5, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            instance.draw();
            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer2.pos(scrollBarX, i5 + i4 - 1, 0.0).tex(0.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer2.pos(i - 1, i5 + i4 - 1, 0.0).tex(1.0, 1.0).color(192, 192, 192, 255).endVertex();
            worldRenderer2.pos(i - 1, i5, 0.0).tex(1.0, 0.0).color(192, 192, 192, 255).endVertex();
            worldRenderer2.pos(scrollBarX, i5, 0.0).tex(0.0, 0.0).color(192, 192, 192, 255).endVertex();
            instance.draw();
         }

         this.drawListDecorations(mouseXIn, mouseYIn);
         GlStateManager.enableTexture2D();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public int getSlotHeight() {
      return this.slotHeight;
   }

   public void update() {
      this.showSelectionBox = false;
   }

   protected void drawListHeader(int i, int i2, Tessellator tessellator2) {
   }

   protected void drawContainerBackground(Tessellator arg1) {
      WorldRenderer worldRenderer = arg1.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(this.left, this.bottom, 0.0)
         .tex(this.left / f, (this.bottom + (int)this.amountScrolled) / f)
         .color(32, 32, 32, 255)
         .endVertex();
      worldRenderer.pos(this.right, this.bottom, 0.0)
         .tex(this.right / f, (this.bottom + (int)this.amountScrolled) / f)
         .color(32, 32, 32, 255)
         .endVertex();
      worldRenderer.pos(this.right, this.top, 0.0)
         .tex(this.right / f, (this.top + (int)this.amountScrolled) / f)
         .color(32, 32, 32, 255)
         .endVertex();
      worldRenderer.pos(this.left, this.top, 0.0)
         .tex(this.left / f, (this.top + (int)this.amountScrolled) / f)
         .color(32, 32, 32, 255)
         .endVertex();
      arg1.draw();
   }

   protected abstract void drawSlot(int i, int i2, int i3, int i4, int i5, int i6);

   public void handleMouseInput() {
      if (this.isMouseYWithinSlotBounds(this.mouseY)) {
         if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
            int i = (this.width - this.getListWidth()) / 2;
            int i2 = (this.width + this.getListWidth()) / 2;
            int i3 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int i4 = i3 / this.slotHeight;
            if (i4 < this.getSize() && this.mouseX >= i && this.mouseX <= i2 && i4 >= 0 && i3 >= 0) {
               this.elementClicked(i4, false, this.mouseX, this.mouseY);
               this.selectedElement = i4;
            } else if (this.mouseX >= i && this.mouseX <= i2 && i3 < 0) {
               this.handleHeaderClick(this.mouseX - i, this.mouseY - this.top + (int)this.amountScrolled - 4);
            }
         }

         if (!Mouse.isButtonDown(0) || !this.getEnabled()) {
            this.initialClickY = -1;
         } else if (this.initialClickY == -1) {
            boolean conditionMet = true;
            if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
               int i5 = (this.width - this.getListWidth()) / 2;
               int i6 = (this.width + this.getListWidth()) / 2;
               int i7 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
               int i8 = i7 / this.slotHeight;
               if (i8 < this.getSize() && this.mouseX >= i5 && this.mouseX <= i6 && i8 >= 0 && i7 >= 0) {
                  boolean flag = i8 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                  this.elementClicked(i8, flag, this.mouseX, this.mouseY);
                  this.selectedElement = i8;
                  this.lastClicked = Minecraft.getSystemTime();
               } else if (this.mouseX >= i5 && this.mouseX <= i6 && i7 < 0) {
                  this.handleHeaderClick(this.mouseX - i5, this.mouseY - this.top + (int)this.amountScrolled - 4);
                  conditionMet = false;
               }

               int scrollBarX = this.getScrollBarX();
               int i9 = scrollBarX + 6;
               if (this.mouseX >= scrollBarX && this.mouseX <= i9) {
                  this.scrollMultiplier = -1.0F;
                  int maxScroll = this.getMaxScroll();
                  if (maxScroll < 1) {
                     maxScroll = 1;
                  }

                  int i10 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / this.getContentHeight());
                  i10 = MathHelper.clamp_int(i10, 32, this.bottom - this.top - 8);
                  this.scrollMultiplier = this.scrollMultiplier / ((float)(this.bottom - this.top - i10) / maxScroll);
               } else {
                  this.scrollMultiplier = 1.0F;
               }

               if (conditionMet) {
                  this.initialClickY = this.mouseY;
               } else {
                  this.initialClickY = -2;
               }
            } else {
               this.initialClickY = -2;
            }
         } else if (this.initialClickY >= 0) {
            this.amountScrolled = this.amountScrolled - (this.mouseY - this.initialClickY) * this.scrollMultiplier;
            this.initialClickY = this.mouseY;
         }

         int wheel = Mouse.getEventDWheel();
         if (wheel != 0) {
            if (wheel > 0) {
               wheel = -1;
            } else if (wheel < 0) {
               wheel = 1;
            }

            this.amountScrolled = this.amountScrolled + wheel * this.slotHeight / 2;
         }
      }
   }

   protected abstract void elementClicked(int i, boolean flag, int i2, int i3);

   protected abstract boolean isSelected(int i);

   protected void drawListDecorations(int mouseX, int mouseY) {
   }

   public int getMaxScroll() {
      return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
   }

   public void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button.id == this.scrollUpButtonID) {
            this.amountScrolled = this.amountScrolled - this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         } else if (button.id == this.scrollDownButtonID) {
            this.amountScrolled = this.amountScrolled + this.slotHeight * 2 / 3;
            this.initialClickY = -2;
            this.bindAmountScrolled();
         }
      }
   }

   public boolean isMouseYWithinSlotBounds(int i) {
      return i >= this.top && i <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
   }

   protected int getContentHeight() {
      return this.getSize() * this.slotHeight + this.headerPadding;
   }

   protected abstract void drawBackground();

   public void setSlotXBoundsFromLeft(int leftIn) {
      this.left = leftIn;
      this.right = leftIn + this.width;
   }

   public int getListWidth() {
      return 220;
   }
}
