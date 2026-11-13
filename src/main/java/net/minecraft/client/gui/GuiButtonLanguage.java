package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonLanguage extends GuiButton {
   public GuiButtonLanguage() {
      super(0, 0, 0, (String) null);
   }

   @Override
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         int i = 106;
         if (flag) {
            i = 106 + this.height;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, i, this.width, this.height);
      }
   }
}
