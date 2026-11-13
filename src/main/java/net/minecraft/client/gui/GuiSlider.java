package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiSlider extends GuiButton {
   private float sliderPosition = 1.0F;
   private String name;
   private final float min;
   public boolean isMouseDown;
   private final float max;
   private GuiSlider.Contract formatHelper;
   private final GuiPageButtonList.GuiResponder responder;

   public float getSliderPosition() {
      return this.sliderPosition;
   }

   private String getDisplayString() {
      return this.formatHelper == null
         ? I18n.format(this.name) + ": " + this.getValue()
         : this.formatHelper.getText(this.id, I18n.format(this.name), this.getValue());
   }

   public void handleFloat(float f) {
      this.sliderPosition = (f - this.min) / (this.max - this.min);
      this.displayString = this.getDisplayString();
   }

   @Override
   protected int getHoverState(boolean mouseOver) {
      return 0;
   }

   public GuiSlider(
      GuiPageButtonList.GuiResponder guiResponder, int idIn, int x, int y, String name, float min, float max, float defaultValue, GuiSlider.Contract formatter
   ) {
      super(idIn, x, y, 150, 20, "");
      this.name = name;
      this.min = min;
      this.max = max;
      this.sliderPosition = (defaultValue - min) / (max - min);
      this.formatHelper = formatter;
      this.responder = guiResponder;
      this.displayString = this.getDisplayString();
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.sliderPosition = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
         if (this.sliderPosition < 0.0F) {
            this.sliderPosition = 0.0F;
         }

         if (this.sliderPosition > 1.0F) {
            this.sliderPosition = 1.0F;
         }

         this.displayString = this.getDisplayString();
         this.responder.setEntryValue(this.id, this.getValue());
         this.isMouseDown = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void update() {
      this.isMouseDown = false;
   }

   @Override
   protected void handleMinecraft(Minecraft minecraft2, int i) {
      if (this.visible) {
         if (this.isMouseDown) {
            this.sliderPosition = (float)(i - (this.xPosition + 4)) / (this.width - 8);
            if (this.sliderPosition < 0.0F) {
               this.sliderPosition = 0.0F;
            }

            if (this.sliderPosition > 1.0F) {
               this.sliderPosition = 1.0F;
            }

            this.displayString = this.getDisplayString();
            this.responder.setEntryValue(this.id, this.getValue());
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderPosition * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.xPosition + (int)(this.sliderPosition * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
      }
   }

   public void setSliderPosition(float sliderPosition) {
      this.sliderPosition = sliderPosition;
      this.displayString = this.getDisplayString();
      this.responder.setEntryValue(this.id, this.getValue());
   }

   public float getValue() {
      return this.min + (this.max - this.min) * this.sliderPosition;
   }

   public interface Contract {
      String getText(int i, String text, float f);
   }
}
