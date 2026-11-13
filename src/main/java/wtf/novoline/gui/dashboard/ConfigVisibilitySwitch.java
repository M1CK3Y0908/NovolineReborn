package wtf.novoline.gui.dashboard;

import java.awt.Color;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.ConfigVisibility;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;

public class ConfigVisibilitySwitch implements DashboardWidget {
   private float underlineWidth;
   private final float tabWidth = 40.0F;
   private float underlineOffset;
   private float x;
   private int selectedIndex;
   private float y;

   @Override
   public float getY() {
      return this.y;
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (i == 0) {
         float f3 = 0.0F;

         for (int i2 = 0; i2 < ConfigVisibility.values().length; i2++) {
            float f4 = this.getX() + f3;
            if (f >= f4 && f <= f4 + 40.0F && f2 >= this.getY() && f2 <= this.getY() + getHeight()) {
               if (this.selectedIndex != i2) {
                  this.onSelectionChanged(this.selectedIndex, i2);
                  this.selectedIndex = i2;
               }

               return true;
            }

            f3 += 40.0F;
         }
      }

      return false;
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   @Override
   public void keyTyped(char c, int i) {
   }

   public static float getWidth() {
      return DashboardMenu.getWidth();
   }

   @Override
   public void draw(float f, float f2, float f3) {
      float f4 = 0.0F;
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), 40.0F * ConfigVisibility.values().length, getHeight(), 2.0F, new Color(18, 19, 21, 255).getRGB());
      filledPolygonRenderer.draw();
      CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;

      for (int i = 0; i < ConfigVisibility.values().length; i++) {
         float f5 = this.getX() + f4;
         float stringWidth = cFontRenderer.getStringWidth(ConfigVisibility.values()[i].getLabel());
         float f6 = f5 + 20.0F - stringWidth / 2.0F;
         if (i == this.selectedIndex) {
            this.underlineOffset = SmoothingUtils.approach(this.underlineOffset, f6 - this.x, 12.0F);
            this.underlineWidth = SmoothingUtils.approach(this.underlineWidth, stringWidth, 12.0F);
         }

         String label = ConfigVisibility.values()[i].getLabel();
         float f7 = this.getY() + getHeight() / 2.0F - 1.0F;
         int i2 = i == this.selectedIndex ? -1 : -6250336;
         float f8 = f7;
         float f9 = f6;
         cFontRenderer.drawString(label, f9, f8, i2);
         f4 += 40.0F;
      }

      filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.x + this.underlineOffset, this.getY() + getHeight() - 1.5F, this.underlineWidth, 1.5F, 1.0F, -10327822);
      filledPolygonRenderer.draw();
   }

   @Override
   public float getX() {
      return this.x;
   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public static float getHeight() {
      return 18.0F;
   }

   public void onSelectionChanged(int i, int i2) {
   }

   public ConfigVisibilitySwitch(float f, float f2) {
      this.x = f;
      this.y = f2;
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
   }
}
