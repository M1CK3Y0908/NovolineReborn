package net.minecraft.client.gui;

import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;

public class GuiSlotRealmsProxy extends GuiSlot {
   public GuiSlotRealmsProxy() {
      super((Minecraft) null, 0, 0, 0, 0, 0);
   }

   private RealmsScrolledSelectionList realmsScrolledSelectionList;

   @Override
   protected int getContentHeight() {
      return this.realmsScrolledSelectionList.getInt2();
   }

   public int getMouseY() {
      return super.mouseY;
   }

   @Override
   protected int getScrollBarX() {
      return this.realmsScrolledSelectionList.getInt4();
   }

   @Override
   protected void drawBackground() {
      this.realmsScrolledSelectionList.noop2();
   }

   @Override
   protected boolean isSelected(int i) {
      return this.realmsScrolledSelectionList.alwaysFalse();
   }

   public void handleInt(int i, Tezzelator tezzelator2) {
      this.realmsScrolledSelectionList.noop5(tezzelator2);
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
      if (this.scrollMultiplier > 0.0F && Mouse.getEventButtonState()) {
         this.realmsScrolledSelectionList.noop(this.top, this.bottom, this.headerPadding, this.amountScrolled, this.slotHeight);
      }
   }

   @Override
   protected void elementClicked(int i, boolean flag, int i2, int i3) {
      this.realmsScrolledSelectionList.noop3();
   }

   public int getMouseX() {
      return super.mouseX;
   }

   @Override
   protected void drawSelectionBox(int i, int i2, int i3, int i4) {
      int size = this.getSize();

      for (int i5 = 0; i5 < size; i5++) {
         int i6 = i2 + i5 * this.slotHeight + this.headerPadding;
         int i7 = this.slotHeight - 4;
         if (i6 > this.bottom || i6 + i7 < this.top) {
            this.noop(i5, i6);
         }

         if (this.showSelectionBox && this.isSelected(i5)) {
            this.handleInt(this.width, Tezzelator.tezzelator);
         }

         this.drawSlot(i5, i, i6, i7, i3, i4);
      }
   }

   public int getWidth() {
      return super.width;
   }

   @Override
   protected void drawSlot(int i, int i2, int i3, int i4, int i5, int i6) {
      this.realmsScrolledSelectionList.update();
   }

   @Override
   protected int getSize() {
      return this.realmsScrolledSelectionList.getInt5();
   }
}
