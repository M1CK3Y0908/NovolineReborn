package wtf.novoline.gui.dashboard;

import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;

public class DashboardButton implements DashboardWidget {
   private float x;
   private float y;
   private float width;
   private float height;
   private String label;

   public float getHeight() {
      return this.height;
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      return false;
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   @Override
   public float getX() {
      return this.x;
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
   }

   @Override
   public void keyTyped(char c, int i) {
   }

   public float getWidth() {
      return this.width;
   }

   @Override
   public void draw(float f, float f2, float f3) {
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0F, -10327822);
      filledPolygonRenderer.draw();
      CFontRenderer cFontRenderer = Fonts.rubikRegularSize14;
      cFontRenderer.drawString(
         this.label, this.getX() + this.getWidth() / 2.0F - cFontRenderer.getStringWidth(this.label) / 2.0F, this.getY() + this.getHeight() / 2.0F - 1.0F, -1
      );
   }

   public DashboardButton(float f, float f2, float f3, float f4, String text) {
      this.x = f;
      this.y = f2;
      this.width = f3;
      this.height = f4;
      this.label = text;
   }

   @Override
   public float getY() {
      return this.y;
   }
}
