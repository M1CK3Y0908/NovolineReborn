package wtf.novoline.gui.dashboard;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.util.ResourceLocation;
import org.tinylog.Logger;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.client.ApiClient;
import wtf.novoline.client.PutResourcesRequest;
import wtf.novoline.client.PutResourcesRequest.ConfigType;
import wtf.novoline.gui.dashboard.ConfigsPanel.ConfigRow;
import wtf.novoline.utils.config.CloudScript;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class ConfigUploadPanel implements DashboardWidget {
   private final DashboardTextField dashboardTextField = new DashboardTextField(0.0F, 0.0F, 170.0F, 20.0F, "", 240);
   private final DashboardButton dashboardButton = new DashboardButton(0.0F, 0.0F, 60.0F, 20.0F, "Upload") {
      @Override
      public boolean handleMouseClick(float f, float f2, int i) {
         if (i == 0 && f > this.getX() && f < this.getX() + this.getWidth() && f2 > this.getY() && f2 < this.getY() + this.getHeight()) {
            try {
               Path path2 = Files.list(Directories.CONFIGS_DIR)
                  .filter(path -> Files.isRegularFile(path))
                  .filter(path -> path.getFileName().toString().equals(ConfigUploadPanel.this.configRow.name + ".novo"))
                  .findFirst()
                  .orElse(null);
               if (path2 != null) {
                  ApiClient apiClient = Novoline.getNovoline().getIrcConnection().getApiClient();
                  CloudScript cloudScript = apiClient.createResource(
                        PutResourcesRequest.builder()
                           .type(ConfigType.CONFIG)
                           .name(ConfigUploadPanel.this.configRow.name)
                           .shortDescription(ConfigUploadPanel.this.configRow.shortDescription)
                           .longDescription(ConfigUploadPanel.this.dashboardTextField.getContent())
                           .build()
                     )
                     .get();
                  apiClient.uploadFile(cloudScript.getId(), Files.readAllBytes(path2));
                  Novoline.getNovoline().getConfigRegistry().getConfigManager().writeCloudInfo(path2, cloudScript.getId());
                  ConfigUploadPanel.this.dashboardMenu.configsPanel.reload();
               }
            } catch (Exception exception) {
               Novoline.getNovoline().getNotificationManager().show(new Notification(NotificationType.ERROR, exception.getMessage(), "Failed to upload", 7500L));
               Logger.error(exception, "Failed to upload resource");
            }

            ConfigUploadPanel.this.dashboardMenu.dashboardWidget = null;
            return true;
         } else {
            return false;
         }
      }
   };
   private final DashboardMenu dashboardMenu;
   private final ConfigRow configRow;

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (f >= this.getX() + this.getWidth() - 100.0F - 23.5F
         && f <= this.getX() + this.getWidth() - 100.0F - 23.5F + 10.0F
         && f2 >= this.getY() + 60.0F + 12.0F
         && f2 <= this.getY() + 60.0F + 12.0F + 10.0F) {
         this.dashboardMenu.dashboardWidget = null;
         return true;
      } else {
         this.dashboardTextField.handleMouseClick(f, f2, i);
         this.dashboardButton.handleMouseClick(f, f2, i);
         return false;
      }
   }

   public float getWidth() {
      return DashboardMenu.getWidth();
   }

   @Override
   public void keyTyped(char c, int i) {
      this.dashboardTextField.keyTyped(c, i);
   }

   @Override
   public float getY() {
      return this.dashboardMenu.getY();
   }

   public float getHeight() {
      return DashboardMenu.getHeight();
   }

   public ConfigUploadPanel(DashboardMenu dashboardMenu2, ConfigRow configRow2) {
      this.dashboardMenu = dashboardMenu2;
      this.configRow = configRow2;
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   @Override
   public void draw(float f, float f2, float f3) {
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0F, -1879048192);
      filledPolygonRenderer.draw();
      filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX() + 100.0F, this.getY() + 80.0F, this.getWidth() - 200.0F, this.getHeight() - 160.0F, 2.0F, -14539994);
      filledPolygonRenderer.draw();
      Fonts.rubikRegularSize18.drawString("Upload config", this.getX() + 100.0F + 15.0F, this.getY() + 80.0F + 15.0F, -1);
      RenderUtils.drawTextureWithAlpha(
         this.getX() + this.getWidth() - 100.0F - 23.5F,
         this.getY() + 80.0F + 12.0F,
         10.0F,
         10.0F,
         1.0F,
         new ResourceLocation("novoline", "dashboard/close.png")
      );
      Fonts.rubikRegularSize14.drawString("Long description", this.getX() + 100.0F + 15.0F, this.getY() + 80.0F + 45.0F - 8.0F, -1);
      this.dashboardTextField.setPosition(this.getX() + 100.0F + 15.0F, this.getY() + 80.0F + 45.0F);
      this.dashboardTextField.draw(f, f2, f3);
      this.dashboardButton
         .setPosition(this.getX() + 100.0F + (this.getWidth() - 200.0F) / 2.0F - this.dashboardButton.getWidth() / 2.0F, this.getY() + 80.0F + 45.0F + 35.0F);
      this.dashboardButton.draw(f, f2, f3);
   }

   @Override
   public float getX() {
      return this.dashboardMenu.getX();
   }
}
