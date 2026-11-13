package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class GuiOptionSlider extends GuiButton {
   public boolean dragging;
   private float sliderValue = 1.0F;
   private net.minecraft.client.settings.GameSettings.Options options;
   private final float valueMin;
   private final float valueMax;

   public GuiOptionSlider(int i, int i2, int i3, net.minecraft.client.settings.GameSettings.Options options2) {
      this(i, i2, i3, options2, 0.0F, 1.0F);
   }

   @Override
   protected void handleMinecraft(Minecraft minecraft2, int i) {
      if (this.visible) {
         if (this.dragging) {
            this.sliderValue = (float)(i - (this.xPosition + 4)) / (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            float f = this.options.denormalizeValue(this.sliderValue);
            minecraft2.gameSettings.setOptionFloatValue(this.options, f);
            this.sliderValue = this.options.normalizeValue(f);
            this.displayString = minecraft2.gameSettings.getKeyBinding(this.options);
         }

         minecraft2.getTextureManager().bindTexture(buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
      }
   }

   @Override
   public void update() {
      this.dragging = false;
   }

   @Override
   protected int getHoverState(boolean mouseOver) {
      return 0;
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
         this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
         mc.gameSettings.setOptionFloatValue(this.options, this.options.denormalizeValue(this.sliderValue));
         this.displayString = mc.gameSettings.getKeyBinding(this.options);
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   public GuiOptionSlider(int buttonId, int x, int y, net.minecraft.client.settings.GameSettings.Options option, float valueMinIn, float valueMaxIn) {
      super(buttonId, x, y, 150, 20, "");
      this.options = option;
      this.valueMin = valueMinIn;
      this.valueMax = valueMaxIn;
      Minecraft minecraft = Minecraft.getMinecraft();
      this.sliderValue = option.normalizeValue(minecraft.gameSettings.getOptionFloatValue(option));
      this.displayString = minecraft.gameSettings.getKeyBinding(option);
   }
}
