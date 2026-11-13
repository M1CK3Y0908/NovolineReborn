package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonRealmsProxy;
import net.minecraft.util.ResourceLocation;

public class RealmsButton {
   protected static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/widgets.png");
   private GuiButtonRealmsProxy guiButtonRealmsProxy;

   public int getInt2() {
      return this.guiButtonRealmsProxy.getId();
   }

   public void handleInt(int i) {
   }

   public int getInt(boolean flag) {
      return this.guiButtonRealmsProxy.getInt(flag);
   }

   public void handleInt3(int i) {
   }

   public void handleInt2(int i, int i2) {
      this.guiButtonRealmsProxy.drawButton(Minecraft.getMinecraft(), i, i2);
   }

   public void handleInt4(int i, int i2, int i3) {
      this.guiButtonRealmsProxy.drawTexturedModalRect(i, i2, i3, 66, 4, 20);
   }

   public int getInt4() {
      return this.guiButtonRealmsProxy.getButtonWidth();
   }

   public int getInt5() {
      return this.guiButtonRealmsProxy.getInt2();
   }

   public void update() {
   }

   public void handleString(String text) {
      this.guiButtonRealmsProxy.setDisplayString(text);
   }

   public GuiButton getGuiButtonRealmsProxy() {
      return this.guiButtonRealmsProxy;
   }

   public int getInt3() {
      return this.guiButtonRealmsProxy.getYPosition();
   }

   public RealmsButton(int i, int i2, int i3, String text) {
      this.guiButtonRealmsProxy = new GuiButtonRealmsProxy(this, i, i2, i3, text);
   }

   public boolean isActive() {
      return this.guiButtonRealmsProxy.isEnabled();
   }
}
