package wtf.novoline.gui.dashboard;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.client.ApiClient;
import wtf.novoline.client.GetResourcesCommentsRequest;
import wtf.novoline.gui.dashboard.ScriptsPanel.ScriptRow;
import wtf.novoline.utils.config.CloudScript;
import wtf.novoline.utils.config.ConfigComment;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class ScriptDetailsPanel implements DashboardWidget {
   private final List<ScriptDetailsPanel.DashboardWidgetImpl2> comments = new ArrayList<>();
   private final DashboardMenu dashboardMenu;
   private final ScriptRow scriptRow;
   private float scrollOffset;
   private float scrollTarget;
   private long positiveRatings;
   private long negativeRatings;

   public float getHeight() {
      return DashboardMenu.getHeight();
   }

   public float getWidth() {
      return DashboardMenu.getWidth();
   }

   @Override
   public float getY() {
      return this.dashboardMenu.getY();
   }

   @Override
   public void draw(float f, float f2, float f3) {
      float f4 = 0.0F;

      for (ScriptDetailsPanel.DashboardWidgetImpl2 dashboardWidgetImpl2 : this.comments) {
         f4 += dashboardWidgetImpl2.getHeight();
      }

      float f5 = Math.max(0.0F, f4 - 60.0F);
      int mouseWheelDelta2 = this.dashboardMenu.mouseWheelDelta;
      float f6 = 10.0F;
      if (mouseWheelDelta2 > 1) {
         this.scrollTarget -= f6;
      } else if (mouseWheelDelta2 < -1) {
         this.scrollTarget += f6;
      }

      if (this.scrollTarget < 0.0F) {
         this.scrollTarget = SmoothingUtils.approachWithMinimumStep(this.scrollTarget, 0.0F, 8.0F, 0.5F);
      }

      if (this.scrollTarget > f5) {
         this.scrollTarget = SmoothingUtils.approachWithMinimumStep(this.scrollTarget, f5, 8.0F, 0.5F);
      }

      this.scrollOffset = SmoothingUtils.approach(this.scrollOffset, this.scrollTarget, 5.0F);
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0F, -1879048192);
      filledPolygonRenderer.draw();
      FilledPolygonRenderer filledPolygonRenderer2 = new FilledPolygonRenderer();
      filledPolygonRenderer2.putRoundedRect(this.getX() + 15.0F, this.getY() + 15.0F, this.getWidth() - 30.0F, this.getHeight() - 30.0F, 2.0F, -14539994);
      filledPolygonRenderer2.draw();
      Fonts.rubikSemiboldSize18.drawString("Details", this.getX() + 15.0F + 16.0F, this.getY() + 15.0F + 20.0F, -1);
      FilledPolygonRenderer filledPolygonRenderer3 = new FilledPolygonRenderer();
      filledPolygonRenderer3.putRoundedRect(this.getX() + 15.0F + 16.0F, this.getY() + 15.0F + 20.0F + 14.0F, 16.0F, 1.5F, 1.0F, -10327822);
      filledPolygonRenderer3.draw();
      RenderUtils.drawTextureWithAlpha(
         this.getX() + this.getWidth() - 15.0F - 15.0F - 10.0F,
         this.getY() + 15.0F + 20.0F - 2.5F,
         10.0F,
         10.0F,
         1.0F,
         new ResourceLocation("novoline", "dashboard/close.png")
      );
      ScriptDetailsPanel.DashboardWidgetImpl dashboardWidgetImpl = new ScriptDetailsPanel.DashboardWidgetImpl(
         this, this.scriptRow.author, this.scriptRow.name, this.scriptRow.longDescription, this.positiveRatings, this.negativeRatings
      );
      dashboardWidgetImpl.draw(f, f2, f3);
      Fonts.rubikRegularSize18.drawString("Comments", this.getX() + 30.0F, dashboardWidgetImpl.getY() + dashboardWidgetImpl.getHeight() + 14.0F, -1);
      Fonts.rubikRegularSize18
         .drawString(
            "( " + this.comments.size() + " )",
            this.getX() + 30.0F + Fonts.rubikRegularSize18.getStringWidth("Comments") + 2.0F,
            dashboardWidgetImpl.getY() + dashboardWidgetImpl.getHeight() + 14.0F,
            -4210235
         );
      float f7 = dashboardWidgetImpl.getY() + dashboardWidgetImpl.getHeight() + 14.0F + Fonts.rubikRegularSize18.getHeight() + 12.0F;
      f6 = f7 - this.scrollOffset;
      RenderUtils.enableScissorDoubleScale((int)this.getX(), (int)f7, (int)this.getWidth(), (int)(this.getY() + this.getHeight() - f7 - 20.0F));

      for (ScriptDetailsPanel.DashboardWidgetImpl2 dashboardWidgetImpl22 : this.comments) {
         dashboardWidgetImpl22.setPosition(0.0F, f6);
         dashboardWidgetImpl22.draw(f, f2, f3);
         f6 += dashboardWidgetImpl22.getHeight() + 4.0F;
      }

      RenderUtils.disableScissor();
   }

   @Override
   public float getX() {
      return this.dashboardMenu.getX();
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (f >= this.getX() + this.getWidth() - 15.0F - 15.0F - 10.0F
         && f <= this.getX() + this.getWidth() - 15.0F - 15.0F - 10.0F + 10.0F
         && f2 >= this.getY() + 15.0F + 20.0F - 2.5F
         && f2 <= this.getY() + 15.0F + 20.0F - 2.5F + 10.0F) {
         this.dashboardMenu.dashboardWidget = null;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void keyTyped(char c, int i) {
   }

   public ScriptDetailsPanel(DashboardMenu dashboardMenu2, ScriptRow scriptRow2) {
      this.dashboardMenu = dashboardMenu2;
      this.scriptRow = scriptRow2;
      if (scriptRow2.hasCloudInfo) {
         try {
            ApiClient apiClient = Novoline.getNovoline().getIrcConnection().getApiClient();
            CloudScript cloudScript = (CloudScript)apiClient.getResource(scriptRow2.id).get();
            this.positiveRatings = cloudScript.getPositiveRatings();
            this.negativeRatings = cloudScript.getNegativeRatings();
            Collection collection = apiClient.getResourceComments(GetResourcesCommentsRequest.builder().resourceId(scriptRow2.id).build()).get();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy").withZone(ZoneId.from(ZoneOffset.UTC));

            for (ConfigComment configComment : (Iterable<ConfigComment>) collection) {
               this.comments
                  .add(new ScriptDetailsPanel.DashboardWidgetImpl2(this, 0.0F, configComment.getAuthor(), configComment.getContent(), dateTimeFormatter.format(configComment.getCreateTimestamp())));
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   @Override
   public void mouseReleased(float f, float f2, int i) {
   }

   class DashboardWidgetImpl implements DashboardWidget {
      private final ScriptDetailsPanel scriptDetailsPanel;
      private final String uploader;
      private final String name;
      private final String description;
      private final long positiveRatings;
      private final long negativeRatings;

      @Override
      public void draw(float f, float f2, float f3) {
         FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
         filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0F, -15592683);
         filledPolygonRenderer.draw();
         CFontRenderer cFontRenderer = Fonts.rubikRegularSize14;
         cFontRenderer.drawString("Name", this.getX() + 15.0F, this.getY() + 15.0F, -4210235);
         cFontRenderer.drawString(this.name, this.getX() + 15.0F, this.getY() + 15.0F + cFontRenderer.getHeight() + 6.0F, -1);
         cFontRenderer.drawString("Description", this.getX() + 15.0F, this.getY() + 15.0F + cFontRenderer.getHeight() + 6.0F + 14.0F, -4210235);
         cFontRenderer.drawString(this.description, this.getX() + 15.0F, this.getY() + 15.0F + cFontRenderer.getHeight() + 6.0F + 14.0F + cFontRenderer.getHeight() + 6.0F, -1);
         cFontRenderer.drawString("Uploader", this.getX() + this.getWidth() / 2.0F - cFontRenderer.getStringWidth("Uploader") + 7.5F, this.getY() + 15.0F, -4210235);
         cFontRenderer.drawString(
            this.uploader, this.getX() + this.getWidth() / 2.0F - cFontRenderer.getStringWidth("Uploader") + 7.5F, this.getY() + 15.0F + cFontRenderer.getHeight() + 6.0F, -1
         );
         String text = String.valueOf(this.negativeRatings);
         cFontRenderer.drawString(text, this.getX() + this.getWidth() - 15.0F - cFontRenderer.getStringWidth(text), this.getY() + 15.0F + cFontRenderer.getHeight() + 5.5F, -1);
         RenderUtils.drawTextureWithAlpha(
            this.getX() + this.getWidth() - 15.0F - cFontRenderer.getStringWidth(text) - 8.0F,
            this.getY() + 15.0F + cFontRenderer.getHeight() + 3.5F,
            6.5F,
            6.5F,
            1.0F,
            new ResourceLocation("novoline", "dashboard/dislike.png")
         );
         String text2 = String.valueOf(this.positiveRatings);
         cFontRenderer.drawString(
            text2,
            this.getX() + this.getWidth() - 15.0F - cFontRenderer.getStringWidth(text) - 8.0F - cFontRenderer.getStringWidth(text2) - 4.0F,
            this.getY() + 15.0F + cFontRenderer.getHeight() + 5.5F,
            -1
         );
         RenderUtils.drawTextureWithAlpha(
            this.getX() + this.getWidth() - 15.0F - cFontRenderer.getStringWidth(text) - 8.0F - cFontRenderer.getStringWidth(text2) - 4.0F - 8.0F,
            this.getY() + 15.0F + cFontRenderer.getHeight() + 3.5F,
            6.5F,
            6.5F,
            1.0F,
            new ResourceLocation("novoline", "dashboard/like.png")
         );
         cFontRenderer.drawString(
            "Votes",
            this.getX() + this.getWidth() - 15.0F - cFontRenderer.getStringWidth(text) - 8.0F - cFontRenderer.getStringWidth(text2) - 4.0F - 8.0F,
            this.getY() + 15.0F,
            -4210235
         );
      }

      public float getWidth() {
         return this.scriptDetailsPanel.getWidth() - 60.0F;
      }

      @Override
      public void keyTyped(char c, int i) {
      }

      @Override
      public boolean handleMouseClick(float f, float f2, int i) {
         return false;
      }

      @Override
      public float getX() {
         return this.scriptDetailsPanel.getX() + 30.0F;
      }

      public float getHeight() {
         return 66.0F;
      }

      public DashboardWidgetImpl(ScriptDetailsPanel scriptDetailsPanel2, String text, String text2, String text3, long l, long l2) {
         this.scriptDetailsPanel = scriptDetailsPanel2;
         this.uploader = text;
         this.name = text2;
         this.description = text3;
         this.positiveRatings = l;
         this.negativeRatings = l2;
      }

      @Override
      public float getY() {
         return this.scriptDetailsPanel.getY() + 60.0F;
      }

      @Override
      public void mouseReleased(float f, float f2, int i) {
      }
   }

   class DashboardWidgetImpl2 implements DashboardWidget {
      private float height;
      private final ScriptDetailsPanel scriptDetailsPanel;
      private float y;
      private final String author;
      private final String content;
      private final String date;

      @Override
      public float getX() {
         return this.scriptDetailsPanel.getX() + 30.0F;
      }

      @Override
      public float getY() {
         return this.y;
      }

      @Override
      public void mouseReleased(float f, float f2, int i) {
      }

      @Override
      public boolean handleMouseClick(float f, float f2, int i) {
         return false;
      }

      @Override
      public void keyTyped(char c, int i) {
      }

      public DashboardWidgetImpl2(ScriptDetailsPanel scriptDetailsPanel2, float f, String text, String text2, String text3) {
         this.scriptDetailsPanel = scriptDetailsPanel2;
         this.y = f;
         this.author = text;
         this.content = text2;
         this.date = text3;
      }

      @Override
      public void draw(float f, float f2, float f3) {
         CFontRenderer cFontRenderer = Fonts.rubikRegularSize14;
         this.height = 34.0F;
         FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
         filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0F, -15592683);
         filledPolygonRenderer.draw();
         cFontRenderer.drawString(
            this.date,
            this.getX() + this.getWidth() - 15.0F - cFontRenderer.getStringWidth(this.date),
            this.getY() + this.getHeight() / 2.0F - cFontRenderer.getHeight() - 1.0F,
            -4210235
         );
         cFontRenderer.drawString(this.author, this.getX() + 15.0F, this.getY() + this.getHeight() / 2.0F - cFontRenderer.getHeight() - 1.0F, -1);
         cFontRenderer.drawString(this.content, this.getX() + 15.0F, this.getY() + this.getHeight() / 2.0F + cFontRenderer.getHeight() - 3.0F, -4210235);
      }

      public void setPosition(float f, float f2) {
         this.y = f2;
      }

      public float getHeight() {
         return this.height;
      }

      public float getWidth() {
         return this.scriptDetailsPanel.getWidth() - 60.0F;
      }
   }
}
