package wtf.novoline.gui.dashboard;

import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.FilledPolygonRenderer;

public class DashboardCheckbox implements DashboardWidget {
   private final String label;
   private float y;
   private float x;
   private boolean checked;

   public DashboardCheckbox(String text) {
      this.label = text;
      this.checked = true;
   }

   @Override
   public void keyTyped(char c, int i) {
   }

   public boolean isChecked() {
      return this.checked;
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (i == 0 && f >= this.getX() && f <= this.getX() + this.getWidth() && f2 >= this.getY() && f2 <= this.getY() + this.getHeight()) {
         this.checked = !this.checked;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float getY() {
      return this.y;
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   public float getHeight() {
      return 10.0F;
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
   }

   public float getWidth() {
      return 10.0F;
   }

   @Override
   public void draw(float f, float f2, float f3) {
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      float x2 = this.getX();
      float y2 = this.getY();
      float width = this.getWidth();
      float height = this.getHeight();
      int i = this.checked ? -13710223 : -12566464;
      float f4 = 2.0F;
      float f5 = height;
      float f6 = width;
      filledPolygonRenderer.putRoundedRect(x2, y2, f6, f5, f4, i);
      filledPolygonRenderer.draw();
      Fonts.rubikRegularSize14.drawString(this.label, this.getX() + this.getWidth() + 3.0F, this.getY() + this.getHeight() / 2.0F - 1.5F, -1);
   }

   @Override
   public float getX() {
      return this.x;
   }
}
