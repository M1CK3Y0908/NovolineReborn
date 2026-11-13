package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class RealmsSliderButton extends RealmsButton {
   public RealmsSliderButton() {
      super(0, 0, 0, (String) null);
   }

   public float floatValue;
   private float floatValue2;
   private float floatValue3;
   private int intValue;
   public boolean flag;

   @Override
   public int getInt(boolean flag2) {
      return 0;
   }

   public float getFloat4(float f) {
      f = this.getFloat(f);
      return MathHelper.clamp_float(f, this.floatValue3, this.floatValue2);
   }

   public String getString() {
      return "";
   }

   @Override
   public void handleInt3(int i) {
      this.floatValue = (float)(i - (this.getGuiButtonRealmsProxy().xPosition + 4)) / (this.getGuiButtonRealmsProxy().getButtonWidth() - 8);
      this.floatValue = MathHelper.clamp_float(this.floatValue, 0.0F, 1.0F);
      this.noop(this.getFloat3(this.floatValue));
      this.getGuiButtonRealmsProxy().displayString = this.getString();
      this.flag = true;
   }

   @Override
   public void handleInt(int i) {
      if (this.getGuiButtonRealmsProxy().visible) {
         if (this.flag) {
            this.floatValue = (float)(i - (this.getGuiButtonRealmsProxy().xPosition + 4)) / (this.getGuiButtonRealmsProxy().getButtonWidth() - 8);
            this.floatValue = MathHelper.clamp_float(this.floatValue, 0.0F, 1.0F);
            float floatValue = this.getFloat3(this.floatValue);
            this.noop(floatValue);
            this.floatValue = this.getFloat2(floatValue);
            this.getGuiButtonRealmsProxy().displayString = this.getString();
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.handleInt4(
            this.getGuiButtonRealmsProxy().xPosition + (int)(this.floatValue * (this.getGuiButtonRealmsProxy().getButtonWidth() - 8)),
            this.getGuiButtonRealmsProxy().yPosition,
            0
         );
         this.handleInt4(
            this.getGuiButtonRealmsProxy().xPosition + (int)(this.floatValue * (this.getGuiButtonRealmsProxy().getButtonWidth() - 8)) + 4,
            this.getGuiButtonRealmsProxy().yPosition,
            196
         );
      }
   }

   public void noop(float f) {
   }

   @Override
   public void update() {
      this.flag = false;
   }

   protected float getFloat(float f) {
      if (this.intValue > 0) {
         f = this.intValue * Math.round(f / this.intValue);
      }

      return f;
   }

   public float getFloat3(float f) {
      return this.getFloat4(this.floatValue3 + (this.floatValue2 - this.floatValue3) * MathHelper.clamp_float(f, 0.0F, 1.0F));
   }

   public float getFloat2(float f) {
      return MathHelper.clamp_float((this.getFloat4(f) - this.floatValue3) / (this.floatValue2 - this.floatValue3), 0.0F, 1.0F);
   }
}
