package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.utils.render.RenderUtils;

public class GuiButton extends Gui {
   protected int width = 200;
   protected int height = 20;
   public boolean enabled = true;
   protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
   public boolean visible = true;
   public int id;
   protected boolean hovered;
   public int xPosition;
   public int yPosition;
   public String displayString;

   public void setWidth(int width) {
      this.width = width;
   }

   public int getButtonWidth() {
      return this.width;
   }

   protected int getHoverState(boolean mouseOver) {
      int i = 1;
      if (!this.enabled) {
         i = 0;
      } else if (mouseOver) {
         i = 2;
      }

      return i;
   }

   public boolean isMouseOver() {
      return this.hovered;
   }

   public GuiButton(int buttonId, int x, int y, String buttonText) {
      this(buttonId, x, y, 200, 20, buttonText);
   }

   public void drawButtonForegroundLayer(int mouseX, int mouseY) {
   }

   public void update() {
   }

   protected void handleMinecraft(Minecraft minecraft2, int i) {
   }

   public void playPressSound(SoundHandler soundHandlerIn) {
      soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      return this.enabled
         && this.visible
         && mouseX >= this.xPosition
         && mouseY >= this.yPosition
         && mouseX < this.xPosition + this.width
         && mouseY < this.yPosition + this.height;
   }

   public int getHeight() {
      return this.height;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         FontRenderer fontRenderer = mc.fontRendererObj;
         mc.getTextureManager().bindTexture(buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         int hoverState = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         GlStateManager.blendFunc(770, 771);
         this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + hoverState * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);
         this.handleMinecraft(mc, mouseX);
         int i = 14737632;
         if (!this.enabled) {
            i = 10526880;
         } else if (this.hovered) {
            i = 16777120;
         }

         this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, i);
      }
   }

   public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      this.id = buttonId;
      this.xPosition = x;
      this.yPosition = y;
      this.width = widthIn;
      this.height = heightIn;
      this.displayString = buttonText;
   }
}
