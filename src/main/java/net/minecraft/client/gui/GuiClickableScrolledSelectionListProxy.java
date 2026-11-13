package net.minecraft.client.gui;

import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.client.Minecraft;

public class GuiClickableScrolledSelectionListProxy extends GuiSlot {
   public GuiClickableScrolledSelectionListProxy() {
      super((Minecraft) null, 0, 0, 0, 0, 0);
   }

   private RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList;

   @Override
   protected int getScrollBarX() {
      return this.realmsClickableScrolledSelectionList.getInt7();
   }

   public int getMouseX() {
      return super.mouseX;
   }

   @Override
   protected boolean isSelected(int i) {
      return this.realmsClickableScrolledSelectionList.alwaysFalse();
   }

   public int getMouseY() {
      return super.mouseY;
   }

   public int getWidth() {
      return super.width;
   }

   @Override
   protected int getSize() {
      return this.realmsClickableScrolledSelectionList.getInt();
   }

   @Override
   public void handleMouseInput() {
      super.handleMouseInput();
   }

   @Override
   protected int getContentHeight() {
      return this.realmsClickableScrolledSelectionList.getInt3();
   }

   @Override
   protected void drawBackground() {
      this.realmsClickableScrolledSelectionList.noop();
   }

   @Override
   protected void elementClicked(int i, boolean flag, int i2, int i3) {
      this.realmsClickableScrolledSelectionList.noop2();
   }

   @Override
   protected void drawSlot(int i, int i2, int i3, int i4, int i5, int i6) {
      this.realmsClickableScrolledSelectionList.update();
   }
}
