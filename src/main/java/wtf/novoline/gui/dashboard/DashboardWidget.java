package wtf.novoline.gui.dashboard;

public interface DashboardWidget {
   boolean handleMouseClick(float f, float f2, int i);

   void keyTyped(char c, int i);

   float getY();

   float getX();

   void draw(float f, float f2, float f3);

   void mouseReleased(float f, float f2, int i);
}
