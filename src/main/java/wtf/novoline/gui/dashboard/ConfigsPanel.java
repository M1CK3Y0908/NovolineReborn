package wtf.novoline.gui.dashboard;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.opengl.GL11;
import org.tinylog.Logger;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.client.ApiClient;
import wtf.novoline.client.GetResourcesRequest2;
import wtf.novoline.client.PostResourcesRatingsRequest.PositiveType;
import wtf.novoline.enums.ConfigVisibility;
import wtf.novoline.utils.config.CloudScript;
import wtf.novoline.utils.config.ConfigManager;
import wtf.novoline.utils.config.CloudScript.ConfigType;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;
import java.io.InputStream;

public class ConfigsPanel implements DashboardWidget {
   private boolean sortDescending;
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.from(ZoneOffset.UTC));
   private boolean loadingMore;
   private final List<ConfigsPanel.ConfigRow> configs = new CopyOnWriteArrayList<>();
   private final List<ConfigsPanel.ConfigRow> filteredConfigs = new CopyOnWriteArrayList<>();
   public DashboardMenu dashboardMenu;
   private float scrollOffset;
   private float x;
   public static String tooltipText;
   private float y;
   public static float tooltipY;
   private float scrollTarget;
   private boolean hasMoreCloudConfigs;
   private int sortColumn;
   public final ConfigVisibilitySwitch configVisibilitySwitch;
   public static float tooltipX;
   private ConfigsPanel.SearchField searchField;
   private final DashboardButton dashboardButton;
   private int cloudOffset;

   public void loadCloudConfigs(int i, boolean flag) {
      if (flag) {
         this.configs.clear();
      }

      Novoline.getNovoline()
         .getIrcConnection()
         .getApiClient()
         .listResources(GetResourcesRequest2.builder().type(ConfigType.CONFIG).offset(i).build())
         .whenComplete(
            (cloudListResult, throwable) -> {
               if (cloudListResult != null) {
                  ArrayList arrayList = new ArrayList<>(cloudListResult.getResources());
                  if (cloudListResult.getRemaining() <= 0L) {
                     this.hasMoreCloudConfigs = false;
                  }

                  Collections.reverse(arrayList);

                  for (CloudScript cloudScript2 : (Iterable<CloudScript>) arrayList) {
                     this.configs
                        .add(
                           new ConfigsPanel.ConfigRow(
                              this,
                              cloudScript2.getId(),
                              cloudScript2.getTotalRatings(),
                              cloudScript2.getName(),
                              cloudScript2.getShortDescription(),
                              cloudScript2.getLongDescription(),
                              cloudScript2.getUploader(),
                              dateTimeFormatter.format(cloudScript2.getCreateTime()),
                              dateTimeFormatter.format(cloudScript2.getUpdateTime()),
                              cloudScript2.getOwnRatingPower(),
                              cloudScript2.getCreateTime(),
                              cloudScript2.getUpdateTime()
                           )
                        );
                  }
               } else {
                  this.hasMoreCloudConfigs = false;
               }

               this.loadingMore = false;
            }
         );
   }

   public void drawSortArrow(float f, float f2, boolean flag) {
      float f3 = flag ? 2.5F : -2.5F;
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      RenderUtils.applyBlendFunc();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
      GL11.glLineWidth(2.0F);
      GL11.glEnable(2848);
      GL11.glBegin(3);
      GL11.glVertex2f(f, f2 + f3 / 2.0F);
      GL11.glVertex2f(f - 2.0F, f2 - f3 / 2.0F);
      GL11.glVertex2f(f - 4.0F, f2 + f3 / 2.0F);
      GL11.glEnd();
      GL11.glDisable(2848);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static float getPanelWidth() {
      return DashboardMenu.getWidth() - 40.0F;
   }

   public void loadLocalConfigs() {
      this.configs.clear();

      try {
         for (Path path2 : Files.list(Directories.CONFIGS_DIR)
            .filter(path -> Files.isRegularFile(path))
            .filter(path -> path.getFileName().toString().endsWith(".novo"))
            .collect(Collectors.toList())) {
            BasicFileAttributes attributes = Files.readAttributes(path2, BasicFileAttributes.class);
            Instant instant = attributes.creationTime().toInstant();
            Instant instant2 = attributes.lastModifiedTime().toInstant();
            ConfigsPanel.ConfigRow configRow3 = new ConfigsPanel.ConfigRow(
               this,
               FilenameUtils.removeExtension(path2.getFileName().toString()),
               "N/A",
               "N/A",
               "N/A",
               dateTimeFormatter.format(instant),
               dateTimeFormatter.format(instant2),
               instant,
               instant2
            );
            ConfigManager configManager = Novoline.getNovoline().getConfigRegistry().getConfigManager();
            String cloudResourceId = configManager.readCloudResourceId(path2);
            if (cloudResourceId != null) {
               configRow3.loadCloudInfo(cloudResourceId);
            }

            List localInfo = configManager.readLocalInfo(path2);
            if (localInfo != null) {
               configRow3.uploader = (String)localInfo.get(0);
               configRow3.shortDescription = (String)localInfo.get(1);
               configRow3.createdText = (String)localInfo.get(2);
            }

            this.configs.add(configRow3);
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
      this.searchField.setPosition(this.getX() + 12.0F + Fonts.robotoRegularSize14.getStringWidth("Vote") + 12.0F, this.getY());
      this.configVisibilitySwitch.setPosition(this.getX(), this.getY() - 30.0F);
      this.dashboardButton.setPosition(this.getX() + getPanelWidth() - 60.0F, this.getY() - 30.0F);
   }

   public void applySortBySection() {
      if (this.sortColumn == 0) {
         this.sortBy((configRow, configRow2) -> (int)(this.sortDescending ? ((ConfigsPanel.ConfigRow) configRow).rating - ((ConfigsPanel.ConfigRow) configRow2).rating : ((ConfigsPanel.ConfigRow) configRow2).rating - ((ConfigsPanel.ConfigRow) configRow).rating));
      } else if (this.sortColumn == 1) {
         if (this.searchField.isQueryEmpty()) {
            Comparator comparator = (configRow, configRow2) -> {
               if (((ConfigsPanel.ConfigRow) configRow).uploader == ((ConfigsPanel.ConfigRow) configRow2).uploader) {
                  return 0;
               } else if (((ConfigsPanel.ConfigRow) configRow).uploader.equals("N/A")) {
                  return -1;
               } else {
                  return ((ConfigsPanel.ConfigRow) configRow2).uploader.equals("N/A") ? 1 : ((ConfigsPanel.ConfigRow) configRow).uploader.compareTo(((ConfigsPanel.ConfigRow) configRow2).uploader);
               }
            };
            this.sortBy(this.sortDescending ? comparator.reversed() : comparator);
         }
      } else if (this.sortColumn == 2) {
         Comparator comparator2 = Comparator.comparing((ConfigsPanel.ConfigRow configRow) -> configRow.createdAt);
         this.sortBy(this.sortDescending ? comparator2.reversed() : comparator2);
      } else if (this.sortColumn == 3) {
         Comparator comparator3 = Comparator.comparing((ConfigsPanel.ConfigRow configRow) -> configRow.updatedAt);
         this.sortBy(this.sortDescending ? comparator3.reversed() : comparator3);
      }
   }

   public void setSortColumn(int i) {
      if (this.sortColumn == i) {
         this.sortDescending = !this.sortDescending;
      } else {
         this.sortColumn = i;
         this.sortDescending = false;
      }

      if (this.sortColumn != -1) {
         this.applySortBySection();
      }
   }

   @Override
   public void keyTyped(char c, int i) {
      this.searchField.keyTyped(c, i);
   }

   public ConfigsPanel(final DashboardMenu dashboardMenu2, float f, float f2) {
      this.dashboardMenu = dashboardMenu2;
      this.x = f;
      this.y = f2;
      this.hasMoreCloudConfigs = true;
      this.sortColumn = -1;
      this.configVisibilitySwitch = new ConfigVisibilitySwitch(this.getX(), this.getY() - 30.0F) {
         @Override
         public void onSelectionChanged(int i, int i2) {
            ConfigsPanel.this.scrollTarget = 0.0F;
            ConfigsPanel.this.scrollOffset = 0.0F;
            ConfigsPanel.this.cloudOffset = 0;
            ConfigsPanel.this.sortColumn = -1;
            ConfigsPanel.this.hasMoreCloudConfigs = true;
            if (i2 == ConfigVisibility.PUBLIC.ordinal()) {
               ConfigsPanel.this.loadCloudConfigs(0, true);
            } else if (i2 == ConfigVisibility.LOCAL.ordinal()) {
               ConfigsPanel.this.loadLocalConfigs();
            }
         }
      };
      this.searchField = new ConfigsPanel.SearchField(
         this.getX() + 12.0F + Fonts.rubikRegularSize14.getStringWidth("Vote") + 12.0F, this.getY(), 95.0F, 14.0F, "Name"
      );
      this.dashboardButton = new DashboardButton(this.getX() + getPanelWidth() - 60.0F, this.getY() - 30.0F, 60.0F, 18.0F, "Create config") {
         @Override
         public boolean handleMouseClick(float f3, float f4, int i3) {
            if (f3 >= this.getX() && f3 <= this.getX() + this.getWidth() && f4 >= this.getY() && f4 <= this.getY() + this.getHeight()) {
               dashboardMenu2.dashboardWidget = new ConfigCreatePopup(dashboardMenu2);
            }

            return true;
         }
      };
      this.loadLocalConfigs();
   }

   public void applySortByName(String text) {
      this.filteredConfigs.clear();
      this.configs.stream().filter(configRow -> configRow.name.startsWith(text)).forEach(this.filteredConfigs::add);
   }

   @Override
   public float getX() {
      return this.x + 20.0F;
   }

   public void reload() {
      this.scrollTarget = 0.0F;
      this.scrollOffset = 0.0F;
      this.cloudOffset = 0;
      this.sortColumn = -1;
      this.hasMoreCloudConfigs = true;
      int selectedIndex = this.configVisibilitySwitch.getSelectedIndex();
      if (selectedIndex == ConfigVisibility.PUBLIC.ordinal()) {
         this.loadCloudConfigs(0, true);
      } else if (selectedIndex == ConfigVisibility.LOCAL.ordinal()) {
         this.loadLocalConfigs();
      }
   }

   @Override
   public void draw(float f, float f2, float f3) {
      tooltipX = 0.0F;
      float f4 = Math.max(
         0.0F,
         (ConfigsPanel.ConfigRow.getRowHeight() + 5.0F) * (this.searchField.isQueryEmpty() ? this.configs.size() : this.filteredConfigs.size())
            - getPanelHeight()
            + 18.0F
      );
      int mouseWheelDelta2 = this.dashboardMenu.mouseWheelDelta;
      float f5 = 10.0F;
      if (mouseWheelDelta2 > 1) {
         this.scrollTarget -= f5;
      } else if (mouseWheelDelta2 < -1) {
         this.scrollTarget += f5;
      }

      if (this.scrollTarget < 0.0F) {
         this.scrollTarget = SmoothingUtils.approachWithMinimumStep(this.scrollTarget, 0.0F, 8.0F, 0.5F);
      }

      if (this.scrollTarget > f4) {
         this.scrollTarget = SmoothingUtils.approachWithMinimumStep(this.scrollTarget, f4, 8.0F, 0.5F);
      }

      this.scrollOffset = SmoothingUtils.approach(this.scrollOffset, this.scrollTarget, 5.0F);
      if (this.hasMoreCloudConfigs
         && this.searchField.isQueryEmpty()
         && this.scrollOffset > 1.0F
         && this.scrollOffset + ConfigsPanel.ConfigRow.getRowHeight() > f4
         && this.configVisibilitySwitch.getSelectedIndex() == ConfigVisibility.PUBLIC.ordinal()
         && !this.loadingMore) {
         this.loadingMore = true;
         this.cloudOffset += 20;
         this.loadCloudConfigs(this.cloudOffset, false);
      }

      CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
      this.configVisibilitySwitch.draw(f, f2, f3);
      this.searchField.draw(f, f2, f3);
      if (this.configVisibilitySwitch.getSelectedIndex() == 0) {
         this.dashboardButton.draw(f, f2, f3);
      }

      cFontRenderer.drawString("Vote", this.getX() + 12.0F, this.getY() + 5.5F, -4210235);
      if (this.sortColumn == 0) {
         this.drawSortArrow(this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") + 6.0F, this.getY() + 6.5F, this.sortDescending);
      }

      cFontRenderer.drawString("Actions", this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F, this.getY() + 5.5F, -4210235);
      cFontRenderer.drawString("Updated", this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Updated") - 12.0F - 40.0F, this.getY() + 5.5F, -4210235);
      if (this.sortColumn == 3) {
         this.drawSortArrow(
            this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Updated") - 12.0F - 40.0F + cFontRenderer.getStringWidth("Updated") + 6.0F,
            this.getY() + 6.5F,
            this.sortDescending
         );
      }

      cFontRenderer.drawString("Created", this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Created") - 12.0F - 40.0F - 40.0F, this.getY() + 5.5F, -4210235);
      if (this.sortColumn == 2) {
         this.drawSortArrow(
            this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Created") - 12.0F - 40.0F - 40.0F + cFontRenderer.getStringWidth("Created") + 6.0F,
            this.getY() + 6.5F,
            this.sortDescending
         );
      }

      cFontRenderer.drawString("Creator", this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Creator") - 12.0F - 40.0F - 40.0F - 90.0F, this.getY() + 5.5F, -4210235);
      if (this.sortColumn == 1) {
         this.drawSortArrow(
            this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Creator") - 12.0F - 40.0F - 40.0F - 90.0F + cFontRenderer.getStringWidth("Creator") + 6.0F,
            this.getY() + 6.5F,
            this.sortDescending
         );
      }

      List list2 = this.searchField.isQueryEmpty() ? this.configs : this.filteredConfigs;
      if (list2.isEmpty()) {
         CFontRenderer cFontRenderer2 = Fonts.rubikRegularSize18;
         cFontRenderer2.drawString(
            "Nothing found :(",
            this.getX() + getPanelWidth() / 2.0F - cFontRenderer2.getStringWidth("Nothing found :(") / 2.0F,
            this.getY() + getPanelHeight() / 2.0F,
            -4210235
         );
      } else {
         RenderUtils.enableScissorDoubleScale((int)this.getX(), (int)this.getY() + 18, (int)getPanelWidth(), (int)getPanelHeight() - 18);
         float f6 = -this.scrollOffset;

         for (ConfigsPanel.ConfigRow configRow3 : (Iterable<ConfigsPanel.ConfigRow>) list2) {
            configRow3.setPosition(this.getX(), this.getY() + 18.0F + f6);
            configRow3.draw(f, f2, f3);
            f6 += ConfigsPanel.ConfigRow.getRowHeight() + 5.0F;
         }

         RenderUtils.disableScissor();
      }

      if (tooltipX != 0.0F) {
         CFontRenderer cFontRenderer3 = Fonts.robotoRegularSize14;
         RenderUtils.drawTextureWithAlpha(tooltipX - 4.0F, tooltipY - 5.0F, 8.5F, 8.5F, 1.0F, new ResourceLocation("novoline", "dashboard/popup_triangle.png"));
         float f7 = cFontRenderer3.getStringWidth(tooltipText) + 16.0F;
         RenderUtils.drawRect(tooltipX - 0.5F - f7 / 2.0F, tooltipY - 20.0F, tooltipX + 1.0F + f7 / 2.0F, tooltipY, -10327822);
         cFontRenderer3.drawString(tooltipText, tooltipX - f7 / 2.0F + 8.0F, tooltipY - 10.0F - 1.0F, -1);
      }
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
      this.dashboardButton.mouseReleased(f, f2, i);
      this.configVisibilitySwitch.mouseReleased(f, f2, i);
      this.searchField.mouseReleased(f, f2, i);
   }

   public static float getPanelHeight() {
      return DashboardMenu.getHeight() - 70.0F - 20.0F;
   }

   @Override
   public float getY() {
      return this.y + 70.0F;
   }

   private void sortBy(Comparator comparator) {
      (this.searchField.isQueryEmpty() ? this.configs : this.filteredConfigs).sort(comparator);
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
      if (f >= this.getX() + 12.0F - 2.0F
         && f <= this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") + 2.0F
         && f2 >= this.getY() + 5.5F - 4.0F
         && f2 <= this.getY() + 5.5F + cFontRenderer.getHeight() + 2.0F) {
         this.setSortColumn(0);
         return true;
      }

      if (f >= this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Creator") - 12.0F - 40.0F - 40.0F - 90.0F - 2.0F
         && f <= this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Creator") - 12.0F - 40.0F - 40.0F - 90.0F + cFontRenderer.getStringWidth("Creator") + 2.0F
         && f2 >= this.getY() + 5.5F - 4.0F
         && f2 <= this.getY() + 5.5F + cFontRenderer.getHeight() + 2.0F) {
         this.setSortColumn(1);
         return true;
      }

      if (f >= this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Created") - 12.0F - 40.0F - 40.0F - 2.0F
         && f <= this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Created") - 12.0F - 40.0F - 40.0F + cFontRenderer.getStringWidth("Created") + 2.0F
         && f2 >= this.getY() + 5.5F - 4.0F
         && f2 <= this.getY() + 5.5F + cFontRenderer.getHeight() + 2.0F) {
         this.setSortColumn(2);
         return true;
      }

      if (f >= this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Updated") - 12.0F - 40.0F - 2.0F
         && f <= this.getX() + getPanelWidth() - cFontRenderer.getStringWidth("Updated") - 12.0F - 40.0F + cFontRenderer.getStringWidth("Updated") + 2.0F
         && f2 >= this.getY() + 5.5F - 4.0F
         && f2 <= this.getY() + 5.5F + cFontRenderer.getHeight() + 2.0F) {
         this.setSortColumn(3);
         return true;
      }

      this.dashboardButton.handleMouseClick(f, f2, i);
      this.configVisibilitySwitch.handleMouseClick(f, f2, i);
      this.searchField.handleMouseClick(f, f2, i);
      Iterator iterator2 = (this.searchField.isQueryEmpty() ? this.configs : this.filteredConfigs).iterator();

      while (iterator2.hasNext()) {
         ((ConfigsPanel.ConfigRow)iterator2.next()).handleMouseClick(f, f2, i);
      }

      return false;
   }

   static class ConfigRow implements DashboardWidget {
      public float y;
      public boolean downloading;
      public long longValue = 0L;
      public int detailsIconColor = -1;
      public int deleteIconColor = -1;
      public float x;
      public int actionIconColor = -1;
      private final ConfigsPanel configsPanel;
      public String id;
      public long rating;
      public String name;
      public String shortDescription;
      public String longDescription;
      public String uploader;
      public String createdText;
      public String updatedText;
      public long ownVote;
      public Instant createdAt;
      public Instant updatedAt;
      public boolean hasCloudInfo;
      public boolean ownedByUser;

      public ConfigRow(
         ConfigsPanel configsPanel2,
         String text,
         long l,
         String text2,
         String text3,
         String text4,
         String text5,
         String text6,
         String text7,
         long l2,
         Instant instant,
         Instant instant2
      ) {
         this.configsPanel = configsPanel2;
         this.id = text;
         this.rating = l;
         this.name = text2;
         this.shortDescription = text3;
         this.longDescription = text4;
         this.uploader = text5;
         this.createdText = text6;
         this.updatedText = text7;
         this.ownVote = l2;
         this.createdAt = instant;
         this.updatedAt = instant2;
         this.hasCloudInfo = true;
         this.ownedByUser = text5.equals(Novoline.username);
      }

      @Override
      public float getX() {
         return this.x;
      }

      @Override
      public boolean handleMouseClick(float f, float f2, int i) {
         if (i == 0
            && f >= this.configsPanel.getX()
            && f <= this.configsPanel.getX() + ConfigsPanel.getPanelWidth()
            && f2 >= this.configsPanel.getY() + 18.0F
            && f2 <= this.configsPanel.getY() + ConfigsPanel.getPanelHeight()) {
            if (this.isOverUpvoteButton(f, f2) && this.hasCloudInfo) {
               if (this.ownVote > 0L) {
                  Novoline.getNovoline()
                     .getIrcConnection()
                     .getApiClient()
                     .deleteResourceRating(this.id)
                     .whenComplete((voidValue, throwable) -> this.configsPanel.reload());
               } else {
                  Novoline.getNovoline()
                     .getIrcConnection()
                     .getApiClient()
                     .rateResource(this.id, PositiveType.POSITIVE)
                     .whenComplete((voidValue, throwable) -> this.configsPanel.reload());
               }
            }

            if (this.isOverDownvoteButton(f, f2) && this.hasCloudInfo) {
               if (this.ownVote < 0L) {
                  Novoline.getNovoline()
                     .getIrcConnection()
                     .getApiClient()
                     .deleteResourceRating(this.id)
                     .whenComplete((voidValue, throwable) -> this.configsPanel.reload());
               } else {
                  Novoline.getNovoline()
                     .getIrcConnection()
                     .getApiClient()
                     .rateResource(this.id, PositiveType.NEGATIVE)
                     .whenComplete((voidValue, throwable) -> this.configsPanel.reload());
               }
            }

            if (this.isOverDetailsButton(f, f2)) {
               if (this.configsPanel.configVisibilitySwitch.getSelectedIndex() == ConfigVisibility.PUBLIC.ordinal()) {
                  this.configsPanel.dashboardMenu.dashboardWidget = new ConfigDetailsPanel(this.configsPanel.dashboardMenu, this);
               } else {
                  this.configsPanel.dashboardMenu.dashboardWidget = new ConfigLoadPopup(this.configsPanel.dashboardMenu, this);
               }
            }

            if (this.isOverDeleteButton(f, f2)) {
               if (this.configsPanel.configVisibilitySwitch.getSelectedIndex() == ConfigVisibility.PUBLIC.ordinal()) {
                  if (this.ownedByUser) {
                     Novoline.getNovoline()
                        .getIrcConnection()
                        .getApiClient()
                        .deleteResource(this.id)
                        .whenComplete((voidValue, throwable) -> this.configsPanel.reload());
                  }
               } else {
                  try {
                     Path path2 = Files.list(Directories.CONFIGS_DIR)
                        .filter(path -> Files.isRegularFile(path))
                        .filter(path -> path.getFileName().toString().equals(this.name + ".novo"))
                        .findFirst()
                        .orElse(null);
                     if (path2 != null) {
                        path2.toFile().delete();
                        this.configsPanel.reload();
                     }
                  } catch (Exception exception) {
                     exception.printStackTrace();
                  }
               }
            }

            if (this.isOverActionButton(f, f2)) {
               if (this.configsPanel.configVisibilitySwitch.getSelectedIndex() == ConfigVisibility.PUBLIC.ordinal()) {
                  this.downloading = true;

                  try {
                     ApiClient apiClient = Novoline.getNovoline().getIrcConnection().getApiClient();
                     apiClient.downloadResourceFile(this.id).whenComplete((inputStream, throwable) -> {
                        if (inputStream != null) {
                           try {
                              Path path3 = Files.write(Directories.CONFIGS_DIR.resolve(this.name + ".novo"), ((InputStream) inputStream).readAllBytes());
                              Novoline.getNovoline().getConfigRegistry().getConfigManager().writeCloudInfo(path3, this.id);
                           } catch (IOException ioException) {
                              ioException.printStackTrace();
                           }
                        } else {
                           ((Throwable) throwable).printStackTrace();
                        }

                        this.downloading = false;
                     });
                  } catch (Exception exception2) {
                     exception2.printStackTrace();
                  }
               } else if (!this.hasCloudInfo) {
                  this.configsPanel.dashboardMenu.dashboardWidget = new ConfigUploadPanel(this.configsPanel.dashboardMenu, this);
               } else if (this.ownedByUser) {
                  try {
                     Path path4 = Files.list(Directories.CONFIGS_DIR)
                        .filter(path -> Files.isRegularFile(path))
                        .filter(path -> path.getFileName().toString().equals(this.name + ".novo"))
                        .findFirst()
                        .orElse(null);
                     Novoline.getNovoline().getConfigRegistry().getConfigManager().saveConfig(this.name);
                     Novoline.getNovoline()
                        .getIrcConnection()
                        .getApiClient()
                        .uploadFile(this.id, Files.readAllBytes(Objects.requireNonNull(path4)))
                        .whenComplete(
                           (voidValue, throwable) -> {
                              if (throwable != null) {
                                 Novoline.getNovoline()
                                    .getNotificationManager()
                                    .show(new Notification(NotificationType.ERROR, ((Throwable) throwable).getMessage(), "Failed to update config", 5000L));
                              }

                              Novoline.getNovoline()
                                 .getNotificationManager()
                                 .show(new Notification(NotificationType.SUCCESS, "Config updated", "Config " + this.name + " was updated", 5000L));
                           }
                        );
                  } catch (Exception exception3) {
                     Logger.error(exception3, "Unable to locate config");
                  }
               }
            }
         }

         return false;
      }

      private boolean isOverActionButton(float f, float f2) {
         if (f >= this.configsPanel.getX()
            && f <= this.configsPanel.getX() + ConfigsPanel.getPanelWidth()
            && f2 >= this.configsPanel.getY() + 18.0F
            && f2 <= this.configsPanel.getY() + ConfigsPanel.getPanelHeight()) {
            CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
            return f >= this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F
               && f <= this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F + 7.5F
               && f2 >= this.getY() + getRowHeight() / 2.0F - 3.5F
               && f2 <= this.getY() + getRowHeight() / 2.0F - 3.5F + 8.0F;
         } else {
            return false;
         }
      }

      @Override
      public void keyTyped(char c, int i) {
      }

      public static float getRowHeight() {
         return 28.0F;
      }

      private boolean isOverDetailsButton(float f, float f2) {
         if (f >= this.configsPanel.getX()
            && f <= this.configsPanel.getX() + ConfigsPanel.getPanelWidth()
            && f2 >= this.configsPanel.getY() + 18.0F
            && f2 <= this.configsPanel.getY() + ConfigsPanel.getPanelHeight()) {
            CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
            return f >= this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F
               && f <= this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 8.0F
               && f2 >= this.getY() + getRowHeight() / 2.0F - 3.5F
               && f2 <= this.getY() + getRowHeight() / 2.0F - 3.5F + 7.5F;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(float f, float f2, int i) {
      }

      private boolean isOverUpvoteButton(float f, float f2) {
         if (f >= this.configsPanel.getX()
            && f <= this.configsPanel.getX() + ConfigsPanel.getPanelWidth()
            && f2 >= this.configsPanel.getY() + 18.0F
            && f2 <= this.configsPanel.getY() + ConfigsPanel.getPanelHeight()) {
            CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
            if (f >= this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F
               && f <= this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F + 5.5F
               && f2 >= this.getY() + getRowHeight() / 2.0F - 10.0F
               && f2 <= this.getY() + getRowHeight() / 2.0F - 10.0F + 5.5F) {
               return true;
            }
         }

         return false;
      }

      private boolean isOverDownvoteButton(float f, float f2) {
         if (f >= this.configsPanel.getX()
            && f <= this.configsPanel.getX() + ConfigsPanel.getPanelWidth()
            && f2 >= this.configsPanel.getY() + 18.0F
            && f2 <= this.configsPanel.getY() + ConfigsPanel.getPanelHeight()) {
            CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
            if (f >= this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F
               && f <= this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F + 5.5F
               && f2 >= this.getY() + getRowHeight() / 2.0F + 6.5F
               && f2 <= this.getY() + getRowHeight() / 2.0F + 6.5F + 5.5F) {
               return true;
            }
         }

         return false;
      }

      public ConfigRow(ConfigsPanel configsPanel2, String text, String text2, String text3, String text4, String text5, String text6, Instant instant, Instant instant2) {
         this.configsPanel = configsPanel2;
         this.id = "-1";
         this.rating = -1L;
         this.name = text;
         this.shortDescription = text2;
         this.longDescription = text3;
         this.uploader = text4;
         this.createdText = text5;
         this.updatedText = text6;
         this.createdAt = instant;
         this.updatedAt = instant2;
         this.hasCloudInfo = false;
         this.ownedByUser = text4.equals(Novoline.username);
      }

      public void setPosition(float f, float f2) {
         this.x = f;
         this.y = f2;
      }

      @Override
      public float getY() {
         return this.y;
      }

      private boolean isOverDeleteButton(float f, float f2) {
         if (f >= this.configsPanel.getX()
            && f <= this.configsPanel.getX() + ConfigsPanel.getPanelWidth()
            && f2 >= this.configsPanel.getY() + 18.0F
            && f2 <= this.configsPanel.getY() + ConfigsPanel.getPanelHeight()) {
            CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
            return f >= this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 12.5F
               && f <= this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 12.5F + 6.5F
               && f2 >= this.getY() + getRowHeight() / 2.0F - 3.5F
               && f2 <= this.getY() + getRowHeight() / 2.0F - 3.5F + 7.5F;
         } else {
            return false;
         }
      }

      public void loadCloudInfo(String text) {
         try {
            Novoline.getNovoline().getIrcConnection().getApiClient().getResource(text).whenComplete((cloudScript, throwable) -> {
               if (cloudScript != null) {
                  DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.from(ZoneOffset.UTC));
                  this.hasCloudInfo = true;
                  this.ownedByUser = ((CloudScript) cloudScript).getUploader().equals(Novoline.username);
                  this.id = text;
                  this.rating = ((CloudScript) cloudScript).getTotalRatings();
                  this.shortDescription = ((CloudScript) cloudScript).getShortDescription();
                  this.longDescription = ((CloudScript) cloudScript).getLongDescription();
                  this.uploader = ((CloudScript) cloudScript).getUploader();
                  this.createdText = dateTimeFormatter2.format(((CloudScript) cloudScript).getCreateTime());
                  this.updatedText = dateTimeFormatter2.format(((CloudScript) cloudScript).getUpdateTime());
                  this.ownVote = ((CloudScript) cloudScript).getOwnRatingPower();
                  this.createdAt = ((CloudScript) cloudScript).getCreateTime();
                  this.updatedAt = ((CloudScript) cloudScript).getUpdateTime();
               }
            });
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      @Override
      public void draw(float f, float f2, float f3) {
         CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
         FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
         filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), ConfigsPanel.getPanelWidth(), getRowHeight(), 2.0F, new Color(18, 19, 21, 255).getRGB());
         filledPolygonRenderer.draw();
         RenderUtils.drawRect(
            this.getX(), this.getY() + getRowHeight() / 2.0F - 5.0F, this.getX() + 1.0F, this.getY() + getRowHeight() / 2.0F + 5.0F, -10327822
         );
         if (this.hasCloudInfo) {
            String text = String.valueOf(this.rating);
            CFontRenderer cFontRenderer2 = Fonts.rubikRegularSize16;
            float f4 = this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - Fonts.rubikRegularSize16.getStringWidth(text) / 2.0F - 0.5F;
            float f5 = this.getY() + getRowHeight() / 2.0F - 2.0F;
            int i = this.rating < 0L ? -1884091 : (this.rating > 0L ? -8140978 : -4210235);
            float f6 = f5;
            float f7 = f4;
            String text2 = text;
            cFontRenderer2.drawString(text2, f7, f6, i);
            float f8 = this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F;
            float f9 = this.getY() + getRowHeight() / 2.0F - 10.0F;
            int i2 = this.ownVote > 0L ? -8140978 : -1;
            ResourceLocation resourceLocation = new ResourceLocation("novoline", "dashboard/arrow_up.png");
            int i3 = i2;
            f7 = 3.5F;
            float f10 = 5.5F;
            RenderUtils.drawTintedTexture(f8, f9, f10, f7, i3, resourceLocation);
            float f11 = this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F;
            f9 = this.getY() + getRowHeight() / 2.0F + 6.5F;
            i2 = this.ownVote < 0L ? -1884091 : -1;
            ResourceLocation resourceLocation2 = new ResourceLocation("novoline", "dashboard/arrow_down.png");
            int i4 = i2;
            f7 = 3.5F;
            float f12 = 5.5F;
            RenderUtils.drawTintedTexture(f11, f9, f12, f7, i4, resourceLocation2);
         } else {
            Fonts.rubikRegularSize16
               .drawString("-", this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 2.0F, this.getY() + getRowHeight() / 2.0F - 2.0F, -11513776);
            RenderUtils.drawTintedTexture(
               this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F,
               this.getY() + getRowHeight() / 2.0F - 10.0F,
               5.5F,
               3.5F,
               -11513776,
               new ResourceLocation("novoline", "dashboard/arrow_up.png")
            );
            RenderUtils.drawTintedTexture(
               this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") / 2.0F - 3.0F,
               this.getY() + getRowHeight() / 2.0F + 6.5F,
               5.5F,
               3.5F,
               -11513776,
               new ResourceLocation("novoline", "dashboard/arrow_down.png")
            );
         }

         if (this.configsPanel.configVisibilitySwitch.getSelectedIndex() == ConfigVisibility.PUBLIC.ordinal()) {
            RenderUtils.drawTintedTexture(
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F,
               this.getY() + getRowHeight() / 2.0F - 3.5F,
               8.0F,
               8.0F,
               this.detailsIconColor,
               new ResourceLocation("novoline", "dashboard/details.png")
            );
            if (this.isOverDetailsButton(f, f2)) {
               ConfigsPanel.tooltipX = this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 4.0F;
               ConfigsPanel.tooltipY = this.getY() + getRowHeight() / 2.0F - 3.5F - 12.0F + 5.0F;
               ConfigsPanel.tooltipText = "Details";
            }
         } else {
            RenderUtils.drawTintedTexture(
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F,
               this.getY() + getRowHeight() / 2.0F - 3.5F,
               8.0F,
               8.0F,
               this.detailsIconColor,
               new ResourceLocation("novoline", "dashboard/load.png")
            );
            if (this.isOverDetailsButton(f, f2)) {
               ConfigsPanel.tooltipX = this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 4.0F;
               ConfigsPanel.tooltipY = this.getY() + getRowHeight() / 2.0F - 3.5F - 12.0F + 5.0F;
               ConfigsPanel.tooltipText = "Load";
            }
         }

         if (this.configsPanel.configVisibilitySwitch.getSelectedIndex() == ConfigVisibility.PUBLIC.ordinal()) {
            if (this.downloading) {
               GlStateManager.pushMatrix();
               GlStateManager.translate(
                  this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F + 1.0F + 4.0F,
                  this.getY() + getRowHeight() / 2.0F - 3.5F + 4.0F,
                  0.0F
               );
               GlStateManager.rotate((float)InterpolationUtils.oscillate(20.0, 340.0, 2000L), 0.0F, 0.0F, 1.0F);
               RenderUtils.drawArc(0.0F, 0.0F, 3.0F, 1.0F, -1, (int)InterpolationUtils.oscillate(20.0, 340.0, 2200L));
               GlStateManager.popMatrix();
            } else {
               GlStateManager.pushMatrix();
               RenderUtils.drawTintedTexture(
                  this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F + 1.0F,
                  this.getY() + getRowHeight() / 2.0F - 3.5F,
                  8.0F,
                  8.0F,
                  this.actionIconColor,
                  new ResourceLocation("novoline", "dashboard/download.png")
               );
               GlStateManager.popMatrix();
               if (this.isOverActionButton(f, f2)) {
                  ConfigsPanel.tooltipX = this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F + 4.0F;
                  ConfigsPanel.tooltipY = this.getY() + getRowHeight() / 2.0F - 3.5F - 12.0F + 5.0F;
                  ConfigsPanel.tooltipText = "Download";
               }
            }
         } else if (this.hasCloudInfo && !this.ownedByUser) {
            GlStateManager.pushMatrix();
            RenderUtils.drawTintedTexture(
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F + 1.0F,
               this.getY() + getRowHeight() / 2.0F - 3.5F,
               8.0F,
               8.0F,
               -11513776,
               new ResourceLocation("novoline", "dashboard/upload.png")
            );
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            RenderUtils.drawTintedTexture(
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F + 1.0F,
               this.getY() + getRowHeight() / 2.0F - 3.5F,
               8.0F,
               8.0F,
               this.actionIconColor,
               new ResourceLocation("novoline", "dashboard/upload.png")
            );
            GlStateManager.popMatrix();
            if (this.isOverActionButton(f, f2)) {
               ConfigsPanel.tooltipX = this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 14.0F + 9.5F + 4.0F;
               ConfigsPanel.tooltipY = this.getY() + getRowHeight() / 2.0F - 3.5F - 12.0F + 5.0F;
               ConfigsPanel.tooltipText = "Upload";
            }
         }

         if (!this.ownedByUser && this.configsPanel.configVisibilitySwitch.getSelectedIndex() != ConfigVisibility.LOCAL.ordinal()) {
            RenderUtils.drawTintedTexture(
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 12.5F,
               this.getY() + getRowHeight() / 2.0F - 3.5F,
               8.0F,
               8.0F,
               -11513776,
               new ResourceLocation("novoline", "dashboard/trash.png")
            );
         } else {
            RenderUtils.drawTintedTexture(
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 12.5F,
               this.getY() + getRowHeight() / 2.0F - 3.5F,
               8.0F,
               8.0F,
               this.deleteIconColor,
               new ResourceLocation("novoline", "dashboard/trash.png")
            );
            if (this.isOverDeleteButton(f, f2)) {
               ConfigsPanel.tooltipX = this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Actions") - 12.0F + 12.5F + 4.0F - 1.0F;
               ConfigsPanel.tooltipY = this.getY() + getRowHeight() / 2.0F - 3.5F - 12.0F + 5.0F;
               ConfigsPanel.tooltipText = "Delete";
            }
         }

         Fonts.rubikRegularSize14
            .drawString(this.name, this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") + 12.0F, this.getY() + getRowHeight() / 2.0F - 6.0F, -1);
         Fonts.museoSans500Size11
            .drawString(this.shortDescription, this.getX() + 12.0F + cFontRenderer.getStringWidth("Vote") + 12.0F, this.getY() + getRowHeight() / 2.0F + 4.0F, -4210235);
         Fonts.rubikRegularSize14
            .drawString(
               this.uploader,
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Creator") - 12.0F - 40.0F - 40.0F - 90.0F + 0.5F,
               this.getY() + getRowHeight() / 2.0F - 1.5F,
               -1
            );
         Fonts.rubikRegularSize14
            .drawString(
               this.createdText,
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Created") - 12.0F - 40.0F - 40.0F,
               this.getY() + getRowHeight() / 2.0F - 1.5F,
               -1
            );
         Fonts.rubikRegularSize14
            .drawString(
               this.updatedText,
               this.getX() + ConfigsPanel.getPanelWidth() - cFontRenderer.getStringWidth("Updated") - 12.0F - 40.0F,
               this.getY() + getRowHeight() / 2.0F - 1.5F,
               -1
            );
         if (f >= this.configsPanel.getX()
            && f <= this.configsPanel.getX() + ConfigsPanel.getPanelWidth()
            && f2 >= this.configsPanel.getY() + 18.0F
            && f2 <= this.configsPanel.getY() + ConfigsPanel.getPanelHeight()) {
            this.detailsIconColor = RenderUtils.smoothBlendColors(this.detailsIconColor, this.isOverDetailsButton(f, f2) ? -10000641 : -1, 10.0F);
            this.deleteIconColor = RenderUtils.smoothBlendColors(this.deleteIconColor, this.isOverDeleteButton(f, f2) ? -1618884 : -1, 10.0F);
            this.actionIconColor = RenderUtils.smoothBlendColors(this.actionIconColor, this.isOverActionButton(f, f2) ? -214272 : -1, 10.0F);
         } else {
            this.detailsIconColor = RenderUtils.smoothBlendColors(this.detailsIconColor, -1, 10.0F);
            this.deleteIconColor = RenderUtils.smoothBlendColors(this.deleteIconColor, -1, 10.0F);
            this.actionIconColor = RenderUtils.smoothBlendColors(this.actionIconColor, -1, 10.0F);
         }
      }
   }

   class SearchField implements DashboardWidget {
      private float x;
      private float y;
      private final float width;
      private final float height;
      private String query;
      private String placeholder;
      private boolean focused;
      private int selectionAnchor;
      private int cursorOffset;
      private int maxLength;

      public float getWidth() {
         return this.width;
      }

      @Override
      public float getX() {
         return this.x;
      }

      public boolean isQueryEmpty() {
         return this.query.isEmpty();
      }

      public SearchField(float f, float f2, float f3, float f4, String text) {
         this.x = f;
         this.y = f2;
         this.width = f3;
         this.height = f4;
         this.query = "";
         this.placeholder = text;
         this.focused = false;
         this.selectionAnchor = 0;
         this.cursorOffset = 0;
         this.maxLength = 16;
      }

      @Override
      public void keyTyped(char c, int i) {
         if (this.focused) {
            if (i == 28) {
               this.focused = false;
               this.cursorOffset = 0;
               this.selectionAnchor = -1;
               return;
            }

            if (GuiScreen.isKeyComboCtrlA(i)) {
               this.cursorOffset = 0;
               this.selectionAnchor = this.query.length();
               return;
            }

            if (GuiScreen.isKeyComboCtrlC(i)) {
               this.deleteOrCopySelection(false, true);
               return;
            }

            if (GuiScreen.isKeyComboCtrlX(i)) {
               this.deleteOrCopySelection(true, true);
               return;
            }

            if (GuiScreen.isKeyComboCtrlV(i)) {
               if (this.selectionAnchor != -1) {
                  this.deleteOrCopySelection(true, false);
               }

               this.query = this.query.substring(0, this.query.length() - this.cursorOffset)
                  + GuiScreen.getClipboardString()
                  + this.query.substring(this.query.length() - this.cursorOffset);
               if (!this.query.isEmpty()) {
                  ConfigsPanel.this.applySortByName(this.query);
               }
            }

            if (i == 14) {
               this.deleteOrCopySelection(true, false);
               return;
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c)) {
               if (this.selectionAnchor != -1) {
                  this.deleteOrCopySelection(true, false);
               }

               this.query = this.query.substring(0, this.query.length() - this.cursorOffset)
                  + c
                  + this.query.substring(this.query.length() - this.cursorOffset);
               if (!this.query.isEmpty()) {
                  ConfigsPanel.this.applySortByName(this.query);
               }

               return;
            }

            boolean shiftKeyDown = GuiScreen.isShiftKeyDown();
            if (i == 203) {
               if (shiftKeyDown) {
                  if (this.selectionAnchor < 0) {
                     this.selectionAnchor = this.cursorOffset;
                  }

                  if (this.selectionAnchor < this.query.length()) {
                     this.selectionAnchor++;
                  }
               } else {
                  if (this.selectionAnchor > -1) {
                     this.cursorOffset = this.selectionAnchor;
                  }

                  this.selectionAnchor = -1;
                  if (this.cursorOffset < this.query.length()) {
                     this.cursorOffset++;
                  }
               }
            } else if (i == 205) {
               if (shiftKeyDown) {
                  if (this.selectionAnchor < 0) {
                     this.selectionAnchor = this.cursorOffset;
                  }

                  if (this.selectionAnchor > 0) {
                     this.selectionAnchor--;
                  }
               } else {
                  if (this.selectionAnchor > -1) {
                     this.cursorOffset = this.selectionAnchor;
                  }

                  this.selectionAnchor = -1;
                  if (this.cursorOffset > 0) {
                     this.cursorOffset--;
                  }
               }
            }
         }
      }

      @Override
      public void mouseReleased(float f, float f2, int i) {
      }

      @Override
      public void draw(float f, float f2, float f3) {
         FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
         filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.width, this.height, 6.0F, new Color(18, 19, 21, 255).getRGB());
         filledPolygonRenderer.draw();
         RenderUtils.drawTextureWithAlpha(
            this.getX() + 4.0F, this.getY() + this.height / 2.0F - 3.0F, 6.0F, 6.5F, 1.0F, new ResourceLocation("novoline", "dashboard/search.png")
         );
         float f4 = this.getX() + 13.0F;
         float f5 = this.getY() + this.getHeight() / 2.0F - 1.0F;
         if (this.query.length() > this.maxLength) {
            this.query = this.query.substring(0, this.maxLength);
         }

         RenderUtils.enableScissorDoubleScale((int)this.getX() + 2, (int)this.getY(), (int)this.getWidth() - 4, (int)this.getHeight());
         CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
         float f6 = cFontRenderer.getStringWidth(this.query) > this.getWidth() - 8.0F ? cFontRenderer.getStringWidth(this.query) - (this.getWidth() - 8.0F) : 0.0F;
         if (this.query.isEmpty() && !this.focused) {
            cFontRenderer.drawString(this.placeholder, f4, f5, -1);
         } else {
            cFontRenderer.drawString(this.query, f4 - f6, f5, -1);
         }

         if (this.focused) {
            float stringWidth = cFontRenderer.getStringWidth(this.query);
            if (this.cursorOffset != 0) {
               String text = this.query.substring(this.query.length() - this.cursorOffset);
               float stringWidth2 = cFontRenderer.getStringWidth(text);
               RenderUtils.drawLine(f4 + stringWidth - stringWidth2, this.getY() + 2.0F, f4 + stringWidth - stringWidth2, this.getY() + this.getHeight() - 2.0F, -2039584);
            } else if (System.nanoTime() / 1000000L % 1000L < 500L) {
               cFontRenderer.drawString("_", f4 + stringWidth - f6, f5, -1);
            }

            if (this.selectionAnchor > -1) {
               String text2 = this.query.substring(this.query.length() - this.selectionAnchor);
               float stringWidth3 = cFontRenderer.getStringWidth(text2);
               String text3 = this.query.substring(this.query.length() - this.cursorOffset);
               float stringWidth4 = cFontRenderer.getStringWidth(text3);
               RenderUtils.drawSelectionHighlight(f4 + stringWidth - stringWidth3, this.getY() + 2.0F, f4 + stringWidth - stringWidth4, this.getY() + this.getHeight() - 2.0F);
            }
         }

         RenderUtils.disableScissor();
      }

      private void deleteOrCopySelection(boolean flag, boolean flag2) {
         if (this.cursorOffset - this.selectionAnchor <= this.query.length()) {
            if (this.selectionAnchor != -1) {
               int i = this.selectionAnchor - this.cursorOffset;
               if (i > 0) {
                  if (flag2) {
                     GuiScreen.setClipboardString(
                        this.query.substring(this.query.length() - this.cursorOffset - i, this.query.length() - this.cursorOffset)
                     );
                  }

                  if (flag) {
                     this.query = this.query.substring(0, this.query.length() - this.cursorOffset - i)
                        + this.query.substring(this.query.length() - this.cursorOffset);
                  }
               } else {
                  if (flag2) {
                     GuiScreen.setClipboardString(
                        this.query.substring(this.query.length() - this.cursorOffset, this.query.length() - this.cursorOffset - i)
                     );
                  }

                  if (flag) {
                     this.query = this.query.substring(0, this.query.length() - this.cursorOffset)
                        + this.query.substring(this.query.length() - this.cursorOffset - i);
                     this.cursorOffset += i;
                  }
               }
            } else if (flag) {
               this.query = this.query.substring(0, this.query.length() - this.cursorOffset - 1)
                  + this.query.substring(this.query.length() - this.cursorOffset);
            }

            if (flag) {
               this.selectionAnchor = -1;
            }
         }

         if (!this.query.isEmpty()) {
            ConfigsPanel.this.applySortByName(this.query);
         }
      }

      public float getHeight() {
         return this.height;
      }

      @Override
      public boolean handleMouseClick(float f, float f2, int i) {
         this.focused = f >= this.getX() && f <= this.getX() + this.width && f2 >= this.getY() && f2 <= this.getY() + this.height;
         if (this.focused) {
            return true;
         }

         this.cursorOffset = 0;
         this.selectionAnchor = -1;
         if (!this.query.isEmpty()) {
            ConfigsPanel.this.applySortByName(this.query);
         }

         return false;
      }

      @Override
      public float getY() {
         return this.y;
      }

      public void setPosition(float f, float f2) {
         this.x = f;
         this.y = f2;
      }
   }
}
