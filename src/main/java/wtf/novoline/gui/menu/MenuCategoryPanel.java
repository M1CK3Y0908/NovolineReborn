package wtf.novoline.gui.menu;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.WidgetType;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shader.FloatSupplier;
import wtf.novoline.utils.render.shape.IRectRenderer;

public class MenuCategoryPanel implements MenuWidget {
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private static final Novoline novoline = Novoline.getNovoline();
   private MenuClickGuiScreen menuClickGuiScreen;
   private boolean dragging;
   private float dragOffsetX;
   public float contentHeight;
   private Category category;
   private float x;
   private float y;
   private boolean expanded;
   public final List<MenuModuleWidget> moduleWidgets;
   private float dragOffsetY;

   public List<MenuModuleWidget> getModuleWidgets() {
      return this.moduleWidgets;
   }

   @Override
   public void render(float f, float f2, float f3) {
      float f4 = 10.0F;
      RenderUtils.drawTextureWithAlpha(this.x + 3.0F, this.y + getHeaderHeight() / 2.0F - f4 / 2.0F, f4, f4, 0.85F, this.category.getIcon());
      int secondaryColor = this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(this.expanded);
      if (this.expanded && !this.moduleWidgets.isEmpty()) {
         int i = 0;

         for (int size2 = this.moduleWidgets.size(); i < size2; i++) {
            MenuModuleWidget menuModuleWidget = this.moduleWidgets.get(i);
            if (i == size2 - 1) {
               float bottomY = this.getBottomY();
               float f5 = bottomY - 2.5F;
               if (MenuClickGuiScreen.shadersEnabled && ModuleManager.clickGui.glowSettings.getElements().get(2).isSelected()) {
                  this.menuClickGuiScreen
                     .bloomRenderer
                     .submit(() -> RenderUtils.drawRect(menuModuleWidget.getX(), f5, menuModuleWidget.getX() + MenuModuleWidget.getWidth(), bottomY, secondaryColor));
               } else {
                  RenderUtils.drawRect(menuModuleWidget.getX(), f5, menuModuleWidget.getX() + MenuModuleWidget.getWidth(), bottomY, secondaryColor);
               }
            }

            menuModuleWidget.render(f, f2, f3);
         }

         RenderUtils.drawVerticalGradientRect(this.x, this.y + getHeaderHeight(), this.x + getWidth(), this.y + getHeaderHeight() + 6.0F, 1879048192, 3092790);
      } else {
         RenderUtils.drawRect(this.x, this.y + getHeaderHeight(), this.x + MenuModuleWidget.getWidth(), this.y + getHeaderHeight() + 2.5F, secondaryColor);
      }
   }

   public void setX(float f) {
      this.x = f;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public Category getCategory() {
      return this.category;
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (f > this.x && f < this.x + getWidth() && f2 > this.y && f2 < this.y + getHeaderHeight()) {
         if (i == 0) {
            this.dragOffsetX = this.x - f;
            this.dragOffsetY = this.y - f2;
            this.dragging = true;
         } else if (i == 1) {
            this.expanded = !this.expanded;
         }
      }

      if (this.expanded) {
         Iterator iterator2 = this.moduleWidgets.iterator();

         while (iterator2.hasNext()) {
            ((MenuWidget)iterator2.next()).handleMouseClick(f, f2, i);
         }
      }
   }

   @Override
   public void reset() {
      this.dragging = false;
      Iterator iterator2 = this.moduleWidgets.iterator();

      while (iterator2.hasNext()) {
         ((MenuWidget)iterator2.next()).reset();
      }
   }

   @Override
   public void tick() {
   }

   public static float getWidth() {
      return 110.0F;
   }

   public void resetButtons() {
      this.moduleWidgets.clear();
      CFontRenderer cFontRenderer = Fonts.rubikRegularSize16;
      List list = ModuleManager.modules
         .stream()
         .filter(module2 -> module2.getCategory().equals(this.category))
         .sorted((module2, module3) -> Integer.compare(cFontRenderer.getStringWidth(module3.getName()), cFontRenderer.getStringWidth(module2.getName())))
         .toList();
      float f = this.y + getHeaderHeight();

      for (Module module4 : (Iterable<Module>) list) {
         this.moduleWidgets.add(new MenuModuleWidget(this.menuClickGuiScreen, module4, this.x, f));
         f += MenuModuleWidget.getHeaderHeight();
      }
   }

   @Override
   public boolean keyTyped(char c, int i) {
      Iterator iterator2 = this.moduleWidgets.iterator();

      while (iterator2.hasNext()) {
         if (!((MenuWidget)iterator2.next()).keyTyped(c, i)) {
            return false;
         }
      }

      return true;
   }

   public MenuCategoryPanel(MenuClickGuiScreen menuClickGuiScreen2, Category category2, float f, float f2) {
      this.menuClickGuiScreen = menuClickGuiScreen2;
      this.category = category2;
      this.x = f;
      this.y = f2;
      this.expanded = true;
      this.moduleWidgets = new CopyOnWriteArrayList<>();
      this.resetButtons();
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      this.dragging = false;
      if (this.expanded) {
         Iterator iterator2 = this.moduleWidgets.iterator();

         while (iterator2.hasNext()) {
            ((MenuWidget)iterator2.next()).handleMouseRelease(f, f2, i);
         }
      }
   }

   public static float getHeaderHeight() {
      return 20.0F;
   }

   public void setExpanded(boolean flag) {
      this.expanded = flag;
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      if (!flag && widgetType == WidgetType.HEADER) {
         String displayName = this.category.getDisplayName();
         floatSupplier.drawStringLeftMiddle(displayName, this.x + 18.0F, this.y + getHeaderHeight() / 2.0F + 1.0F, -2039584);
      }

      if (this.expanded) {
         Iterator iterator2 = this.moduleWidgets.iterator();

         while (iterator2.hasNext()) {
            ((MenuModuleWidget)iterator2.next()).renderText(floatSupplier, widgetType, f, flag);
         }
      }
   }

   public float getBottomY() {
      return this.y + this.contentHeight + getHeaderHeight();
   }

   public float updateLayout(float f, float f2, float f3) {
      if (this.dragging) {
         this.x = this.dragOffsetX + f;
         this.y = this.dragOffsetY + f2;
      }

      float f4 = 0.0F;
      float f5 = this.y + getHeaderHeight();
      if (this.expanded) {
         for (MenuModuleWidget menuModuleWidget : this.moduleWidgets) {
            menuModuleWidget.setPosition(this.x, f5 + f4);
            float totalHeight = menuModuleWidget.getTotalHeight(f, f2, f3);
            f4 += totalHeight;
         }
      }

      this.contentHeight = f4 + 2.5F;
      return getHeaderHeight() + this.contentHeight;
   }

   public float getY() {
      return this.y;
   }

   public void addRects(IRectRenderer iRectRenderer, float f) {
      iRectRenderer.addRect(this.x - 2.0F, this.y, getWidth() + 4.0F, getHeaderHeight(), this.menuClickGuiScreen.getClickGuiTheme().getCategoryBackgroundColor());
      if (this.expanded) {
         Iterator iterator2 = this.moduleWidgets.iterator();

         while (iterator2.hasNext()) {
            ((MenuModuleWidget)iterator2.next()).addRects(iRectRenderer, f);
         }
      }
   }

   public void setY(float f) {
      this.y = f;
   }

   public float getX() {
      return this.x;
   }
}
