package wtf.novoline.gui.dashboard;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.config.ConfigManager;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class ConfigCreatePopup implements DashboardWidget {
   private final DashboardTextField nameField = new DashboardTextField(0.0F, 0.0F, 170.0F, 20.0F, "", 24);
   private final DashboardTextField descriptionField = new DashboardTextField(0.0F, 0.0F, 170.0F, 20.0F, "", 40);
   private final DashboardButton dashboardButton = new DashboardButton(0.0F, 0.0F, 60.0F, 20.0F, "Create") {
      @Override
      public boolean handleMouseClick(float f, float f2, int i) {
         if (!ConfigCreatePopup.this.nameField.isEmpty()
            && !ConfigCreatePopup.this.descriptionField.isEmpty()
            && i == 0
            && f > this.getX()
            && f < this.getX() + this.getWidth()
            && f2 > this.getY()
            && f2 < this.getY() + this.getHeight()) {
            ConfigManager configManager = Novoline.getNovoline().getConfigRegistry().getConfigManager();
            configManager.saveConfig(ConfigCreatePopup.this.nameField.getContent());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.from(ZoneOffset.UTC));
            configManager.writeLocalInfo(
               Directories.CONFIGS_DIR.resolve(ConfigCreatePopup.this.nameField.getContent() + ".novo"),
               Novoline.username,
               ConfigCreatePopup.this.descriptionField.getContent(),
               dateTimeFormatter.format(Instant.now())
            );
            ConfigCreatePopup.this.dashboardMenu.configsPanel.reload();
            ConfigCreatePopup.this.dashboardMenu.dashboardWidget = null;
            return true;
         } else {
            return false;
         }
      }
   };
   private final DashboardMenu dashboardMenu;

   public ConfigCreatePopup(DashboardMenu dashboardMenu2) {
      this.dashboardMenu = dashboardMenu2;
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   @Override
   public void keyTyped(char c, int i) {
      this.nameField.keyTyped(c, i);
      this.descriptionField.keyTyped(c, i);
   }

   @Override
   public float getY() {
      return this.dashboardMenu.getY();
   }

   @Override
   public float getX() {
      return this.dashboardMenu.getX();
   }

   public float getWidth() {
      return DashboardMenu.getWidth();
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
         this.nameField.handleMouseClick(f, f2, i);
         this.descriptionField.handleMouseClick(f, f2, i);
         this.dashboardButton.handleMouseClick(f, f2, i);
         return false;
      }
   }

   public float getHeight() {
      return DashboardMenu.getHeight();
   }

   @Override
   public void draw(float f, float f2, float f3) {
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0F, -1879048192);
      filledPolygonRenderer.draw();
      filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX() + 100.0F, this.getY() + 60.0F, this.getWidth() - 200.0F, this.getHeight() - 120.0F, 2.0F, -14539994);
      filledPolygonRenderer.draw();
      Fonts.rubikRegularSize18.drawString("Create config", this.getX() + 100.0F + 15.0F, this.getY() + 60.0F + 15.0F, -1);
      RenderUtils.drawTextureWithAlpha(
         this.getX() + this.getWidth() - 100.0F - 23.5F,
         this.getY() + 60.0F + 12.0F,
         10.0F,
         10.0F,
         1.0F,
         new ResourceLocation("novoline", "dashboard/close.png")
      );
      Fonts.rubikRegularSize14.drawString("Name", this.getX() + 100.0F + 15.0F, this.getY() + 60.0F + 45.0F - 8.0F, -1);
      this.nameField.setPosition(this.getX() + 100.0F + 15.0F, this.getY() + 60.0F + 45.0F);
      this.nameField.draw(f, f2, f3);
      Fonts.rubikRegularSize14.drawString("Short description", this.getX() + 100.0F + 15.0F, this.getY() + 60.0F + 45.0F + 40.0F - 8.0F, -1);
      this.descriptionField.setPosition(this.getX() + 100.0F + 15.0F, this.getY() + 60.0F + 45.0F + 40.0F);
      this.descriptionField.draw(f, f2, f3);
      this.dashboardButton
         .setPosition(
            this.getX() + 100.0F + (this.getWidth() - 200.0F) / 2.0F - this.dashboardButton.getWidth() / 2.0F, this.getY() + 60.0F + 45.0F + 40.0F + 30.0F
         );
      this.dashboardButton.draw(f, f2, f3);
   }
}
