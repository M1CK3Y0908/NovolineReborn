package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLockIconButton extends GuiButton {
   private boolean locked = false;

   @Override
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         GuiLockIconButton.Icon icon;
         if (this.locked) {
            if (!this.enabled) {
               icon = GuiLockIconButton.Icon.LOCKED_DISABLED;
            } else if (flag) {
               icon = GuiLockIconButton.Icon.LOCKED_HOVER;
            } else {
               icon = GuiLockIconButton.Icon.LOCKED;
            }
         } else if (!this.enabled) {
            icon = GuiLockIconButton.Icon.UNLOCKED_DISABLED;
         } else if (flag) {
            icon = GuiLockIconButton.Icon.UNLOCKED_HOVER;
         } else {
            icon = GuiLockIconButton.Icon.UNLOCKED;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, icon.getTextureX(), icon.getTextureY(), this.width, this.height);
      }
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public GuiLockIconButton(int id, int x, int y) {
      super(id, x, y, 20, 20, "");
   }

   enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int textureX;
      private final int textureY;

      public int getTextureX() {
         return this.textureX;
      }

      public int getTextureY() {
         return this.textureY;
      }

      Icon(int i, int i2) {
         this.textureX = i;
         this.textureY = i2;
      }
   }
}
