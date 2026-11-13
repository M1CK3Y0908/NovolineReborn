package wtf.novoline.gui.dashboard;

import org.lwjgl.input.Mouse;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.DashboardTab;
import wtf.novoline.utils.render.FilledPolygonRenderer;

public class DashboardMenu implements DashboardWidget {
   public DashboardWidget dashboardWidget = null;
   private boolean dragging;
   private float x;
   private float y;
   private float dragOffsetX;
   public final DashboardTabSwitch dashboardTabSwitch;
   public final ConfigsPanel configsPanel;
   public final ScriptsPanel scriptsPanel;
   private float dragOffsetY;
   public int mouseWheelDelta;

   @Override
   public float getX() {
      return this.x;
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (f > this.getX() && f < this.getX() + getWidth() && f2 > this.getY() && f2 < this.getY() + 16.0F && i == 0) {
         this.dragOffsetX = this.x - f;
         this.dragOffsetY = this.y - f2;
         this.dragging = true;
      }

      if (this.dashboardWidget != null) {
         this.dashboardWidget.handleMouseClick(f, f2, i);
      } else {
         if (this.dashboardTabSwitch.getSelectedIndex() == DashboardTab.CONFIGS.ordinal()) {
            this.configsPanel.handleMouseClick(f, f2, i);
         } else {
            this.scriptsPanel.handleMouseClick(f, f2, i);
         }

         this.dashboardTabSwitch.handleMouseClick(f, f2, i);
      }

      return false;
   }

   @Override
   public void keyTyped(char c, int i) {
      if (this.dashboardWidget != null) {
         this.dashboardWidget.keyTyped(c, i);
      } else if (this.dashboardTabSwitch.getSelectedIndex() == DashboardTab.CONFIGS.ordinal()) {
         this.configsPanel.keyTyped(c, i);
      } else {
         this.scriptsPanel.keyTyped(c, i);
      }
   }

   public static float getHeight() {
      return 270.0F;
   }

   public DashboardMenu(float f, float f2) {
      this.x = f;
      this.y = f2;
      this.dashboardTabSwitch = new DashboardTabSwitch(this.getX() + getWidth() / 2.0F - 20.0F, this.getY() + 16.0F);
      this.configsPanel = new ConfigsPanel(this, this.getX(), this.getY());
      this.scriptsPanel = new ScriptsPanel(this, this.getX(), this.getY());
   }

   public static float getWidth() {
      return 400.0F;
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
      this.dashboardTabSwitch.setPosition(this.getX() + getWidth() / 2.0F - 20.0F, this.getY() + 16.0F);
      if (this.dashboardTabSwitch.getSelectedIndex() == DashboardTab.CONFIGS.ordinal()) {
         this.configsPanel.setPosition(this.getX(), this.getY());
      } else {
         this.scriptsPanel.setPosition(this.getX(), this.getY());
      }
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
      this.dragging = false;
      if (this.dashboardWidget != null) {
         this.dashboardWidget.mouseReleased(f, f2, i);
      } else {
         if (this.dashboardTabSwitch.getSelectedIndex() == DashboardTab.CONFIGS.ordinal()) {
            this.configsPanel.mouseReleased(f, f2, i);
         } else {
            this.scriptsPanel.mouseReleased(f, f2, i);
         }

         this.dashboardTabSwitch.mouseReleased(f, f2, i);
      }
   }

   @Override
   public void draw(float f, float f2, float f3) {
      this.mouseWheelDelta = Mouse.getDWheel();
      if (this.dragging) {
         this.setPosition(this.dragOffsetX + f, this.dragOffsetY + f2);
      }

      this.scriptsPanel.setPosition(this.getX(), this.getY());
      this.configsPanel.setPosition(this.getX(), this.getY());
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), getWidth(), getHeight(), 2.0F, -14539994);
      filledPolygonRenderer.draw();
      Fonts.rubikRegularSize18.drawString("Dashboard Menu", this.getX() + 20.0F, this.getY() + 20.0F, -1);
      this.dashboardTabSwitch.draw(f, f2, f3);
      if (this.dashboardTabSwitch.getSelectedIndex() == DashboardTab.CONFIGS.ordinal()) {
         this.configsPanel.draw(f, f2, f3);
      } else {
         this.scriptsPanel.draw(f, f2, f3);
      }

      if (this.dashboardWidget != null) {
         this.dashboardWidget.draw(f, f2, f3);
      }
   }

   @Override
   public float getY() {
      return this.y;
   }
}
