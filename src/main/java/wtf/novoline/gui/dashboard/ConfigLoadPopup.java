package wtf.novoline.gui.dashboard;

import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.gui.dashboard.ConfigsPanel.ConfigRow;
import wtf.novoline.utils.config.ConfigRegistry;
import wtf.novoline.utils.config.ConfigSection;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class ConfigLoadPopup implements DashboardWidget {
   private final DashboardCheckbox bindsCheckbox = new DashboardCheckbox("Binds");
   private final DashboardCheckbox macrosCheckbox = new DashboardCheckbox("Macros");
   private final DashboardCheckbox overlaysCheckbox = new DashboardCheckbox("Overlays");
   private final DashboardCheckbox preferencesCheckbox = new DashboardCheckbox("Preferences");
   private final DashboardButton dashboardButton = new DashboardButton(0.0F, 0.0F, 60.0F, 20.0F, "Load") {
      @Override
      public boolean handleMouseClick(float f, float f2, int i) {
         if (i == 0 && f > this.getX() && f < this.getX() + this.getWidth() && f2 > this.getY() && f2 < this.getY() + this.getHeight()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(ConfigLoadPopup.this.bindsCheckbox.isChecked() ? ConfigRegistry.featuresWithHotkeys : ConfigRegistry.featuresWithoutHotkeys);
            if (ConfigLoadPopup.this.macrosCheckbox.isChecked()) {
               arrayList.add(ConfigRegistry.macrosSection);
            }

            if (ConfigLoadPopup.this.overlaysCheckbox.isChecked()) {
               arrayList.add(ConfigRegistry.overlaySection);
            }

            if (ConfigLoadPopup.this.preferencesCheckbox.isChecked()) {
               arrayList.add(ConfigRegistry.preferencesSection);
            }

            Novoline.getNovoline().getConfigRegistry().getConfigManager().load(false, ConfigLoadPopup.this.configRow.name, (ConfigSection[]) arrayList.toArray(new ConfigSection[0]));
            ConfigLoadPopup.this.dashboardMenu.dashboardWidget = null;
            return true;
         } else {
            return false;
         }
      }
   };
   private final DashboardMenu dashboardMenu;
   private final ConfigRow configRow;

   public ConfigLoadPopup(DashboardMenu dashboardMenu2, ConfigRow configRow2) {
      this.dashboardMenu = dashboardMenu2;
      this.configRow = configRow2;
   }

   @Override
   public float getY() {
      return this.dashboardMenu.getY();
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (f >= this.getX() + this.getWidth() - 100.0F - 23.5F
         && f <= this.getX() + this.getWidth() - 100.0F - 23.5F + 10.0F
         && f2 >= this.getY() + 60.0F + 12.0F
         && f2 <= this.getY() + 60.0F + 12.0F + 10.0F) {
         this.dashboardMenu.dashboardWidget = null;
         return true;
      } else {
         this.bindsCheckbox.handleMouseClick(f, f2, i);
         this.macrosCheckbox.handleMouseClick(f, f2, i);
         this.overlaysCheckbox.handleMouseClick(f, f2, i);
         this.preferencesCheckbox.handleMouseClick(f, f2, i);
         this.dashboardButton.handleMouseClick(f, f2, i);
         return false;
      }
   }

   @Override
   public void draw(float f, float f2, float f3) {
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0F, -1879048192);
      filledPolygonRenderer.draw();
      filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX() + 100.0F, this.getY() + 60.0F, this.getWidth() - 200.0F, this.getHeight() - 120.0F, 2.0F, -14539994);
      filledPolygonRenderer.draw();
      Fonts.rubikRegularSize18.drawString("Load config", this.getX() + 100.0F + 15.0F, this.getY() + 60.0F + 15.0F, -1);
      RenderUtils.drawTextureWithAlpha(
         this.getX() + this.getWidth() - 100.0F - 23.5F,
         this.getY() + 60.0F + 12.0F,
         10.0F,
         10.0F,
         1.0F,
         new ResourceLocation("novoline", "dashboard/close.png")
      );
      this.bindsCheckbox.setPosition(this.getX() + 100.0F + (this.getWidth() - 200.0F) / 2.0F - 30.0F, this.getY() + 60.0F + 42.0F);
      this.bindsCheckbox.draw(f, f2, f3);
      this.macrosCheckbox
         .setPosition(this.getX() + 100.0F + (this.getWidth() - 200.0F) / 2.0F - 30.0F, this.bindsCheckbox.getY() + this.bindsCheckbox.getHeight() + 4.0F);
      this.macrosCheckbox.draw(f, f2, f3);
      this.overlaysCheckbox
         .setPosition(this.getX() + 100.0F + (this.getWidth() - 200.0F) / 2.0F - 30.0F, this.macrosCheckbox.getY() + this.macrosCheckbox.getHeight() + 4.0F);
      this.overlaysCheckbox.draw(f, f2, f3);
      this.preferencesCheckbox
         .setPosition(this.getX() + 100.0F + (this.getWidth() - 200.0F) / 2.0F - 30.0F, this.overlaysCheckbox.getY() + this.overlaysCheckbox.getHeight() + 4.0F);
      this.preferencesCheckbox.draw(f, f2, f3);
      this.dashboardButton
         .setPosition(
            this.getX() + 100.0F + (this.getWidth() - 200.0F) / 2.0F - this.dashboardButton.getWidth() / 2.0F, this.getY() + 60.0F + 45.0F + 40.0F + 30.0F
         );
      this.dashboardButton.draw(f, f2, f3);
   }

   @Override
   public float getX() {
      return this.dashboardMenu.getX();
   }

   public float getWidth() {
      return DashboardMenu.getWidth();
   }

   public float getHeight() {
      return DashboardMenu.getHeight();
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   @Override
   public void keyTyped(char c, int i) {
   }
}
