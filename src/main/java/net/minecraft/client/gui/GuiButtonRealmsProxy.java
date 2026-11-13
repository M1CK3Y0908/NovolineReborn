package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;

public class GuiButtonRealmsProxy extends GuiButton {
   private RealmsButton realmsButton;

   @Override
   public int getButtonWidth() {
      return super.getButtonWidth();
   }

   @Override
   public void handleMinecraft(Minecraft minecraft2, int i) {
      this.realmsButton.handleInt(i);
   }

   public RealmsButton getRealmsButton() {
      return this.realmsButton;
   }

   public void setDisplayString(String text) {
      super.displayString = text;
   }

   @Override
   public int getHoverState(boolean flag) {
      return this.realmsButton.getInt(flag);
   }

   @Override
   public void update() {
      this.realmsButton.update();
   }

   public GuiButtonRealmsProxy(RealmsButton realmsButton2, int i, int i2, int i3, String text) {
      super(i, i2, i3, text);
      this.realmsButton = realmsButton2;
   }

   @Override
   public boolean mousePressed(Minecraft minecraft2, int i, int i2) {
      if (super.mousePressed(minecraft2, i, i2)) {
         this.realmsButton.handleInt3(i);
      }

      return super.mousePressed(minecraft2, i, i2);
   }

   public int getId() {
      return super.id;
   }

   public boolean isEnabled() {
      return super.enabled;
   }

   public int getInt(boolean flag) {
      return super.getHoverState(flag);
   }

   public int getInt2() {
      return this.height;
   }

   public int getYPosition() {
      return super.yPosition;
   }
}
