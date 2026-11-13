package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
   public abstract GuiListExtended.IGuiListEntry getListEntry(int i);

   @Override
   protected boolean isSelected(int slotIndex) {
      return false;
   }

   @Override
   protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
   }

   @Override
   protected void drawSlot(int entryID, int i, int i2, int i3, int mouseXIn, int mouseYIn) {
      this.getListEntry(entryID)
         .drawEntry(entryID, i, i2, this.getListWidth(), i3, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
   }

   public GuiListExtended(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
   }

   @Override
   protected void drawBackground() {
   }

   public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
      if (this.isMouseYWithinSlotBounds(mouseY)) {
         int slotIndexFromScreenCoords = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
         if (slotIndexFromScreenCoords >= 0) {
            int i = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int i2 = this.top + 4 - this.getAmountScrolled() + slotIndexFromScreenCoords * this.slotHeight + this.headerPadding;
            int i3 = mouseX - i;
            int i4 = mouseY - i2;
            if (this.getListEntry(slotIndexFromScreenCoords).mousePressed(slotIndexFromScreenCoords, mouseX, mouseY, mouseEvent, i3, i4)) {
               this.setEnabled(false);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected void noop(int i, int i2) {
      this.getListEntry(i).noop(i2);
   }

   public boolean checkInt(int i, int i2) {
      for (int i3 = 0; i3 < this.getSize(); i3++) {
         int i4 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
         int i5 = this.top + 4 - this.getAmountScrolled() + i3 * this.slotHeight + this.headerPadding;
         int i6 = i - i4;
         int i7 = i2 - i5;
         this.getListEntry(i3).update();
      }

      this.setEnabled(true);
      return false;
   }

   public interface IGuiListEntry {
      void drawEntry(int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean flag);

      void update();

      void noop(int i);

      boolean mousePressed(int i, int i2, int i3, int i4, int i5, int i6);
   }
}
