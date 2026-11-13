package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScaledResolution {
   private int scaledWidth;
   private int scaledHeight;
   private int scaleFactor;
   private double scaledWidthD;
   private double scaledHeightD;

   public double getScaledHeight_double() {
      return this.scaledHeightD;
   }

   public int getScaledHeight() {
      return this.scaledHeight;
   }

   public double getScaledWidth_double() {
      return this.scaledWidthD;
   }

   public float getFloat2() {
      return (float)this.getScaledHeight_double();
   }

   public float getFloat() {
      return (float)this.getScaledWidth_double();
   }

   public void handleDouble(double d) {
      this.scaledHeightD = d;
      this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
   }

   public int getScaledWidth() {
      return this.scaledWidth;
   }

   public int getScaleFactor() {
      return this.scaleFactor;
   }

   public ScaledResolution(Minecraft minecraft2) {
      this.scaledWidth = minecraft2.displayWidth;
      this.scaledHeight = minecraft2.displayHeight;
      this.scaleFactor = 1;
      boolean isUnicode = minecraft2.isUnicode();
      int guiScale2 = minecraft2.gameSettings.guiScale;
      if (guiScale2 == 0) {
         guiScale2 = 1000;
      }

      while (this.scaleFactor < guiScale2 && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
         this.scaleFactor++;
      }

      if (isUnicode && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
         this.scaleFactor--;
      }

      this.scaledWidthD = (double)this.scaledWidth / this.scaleFactor;
      this.scaledHeightD = (double)this.scaledHeight / this.scaleFactor;
      this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
      this.scaledHeight = MathHelper.ceiling_double_int(this.scaledHeightD);
   }

   public void handleDouble2(double d) {
      this.scaledWidthD = d;
      this.scaledWidth = MathHelper.ceiling_double_int(this.scaledWidthD);
   }
}
