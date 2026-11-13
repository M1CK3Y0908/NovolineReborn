package wtf.novoline.gui.clickgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;

public class ClickGuiScreen extends GuiScreen {
   private static final ResourceLocation PROFILE_PICTURE = new ResourceLocation("novoline", "images/clickgui/neverlose/profile_picture.png");
   private boolean dragging;
   private final List<CategoryPanel> categoryPanels = new ArrayList<>();
   private CategoryPanel categoryPanel;
   private float scrollOffset;
   private CategoryPanel searchPanel;
   private CategoryTab categoryTab;
   private float dragOffsetX;
   private float mainCategoriesHeight;
   private Category category;
   private boolean initialized = false;
   private float y;
   private float dragOffsetY;
   private static final ResourceLocation SEARCH_ICON = new ResourceLocation("novoline", "images/clickgui/neverlose/search.png");
   private float x;
   public float scrollTarget;

   public void selectCategoryPanel(CategoryPanel categoryPanel3) {
      if (this.categoryPanel != categoryPanel3) {
         if (this.categoryPanel != null) {
            this.categoryPanel.setComboSettingRow(null);
            this.categoryPanel.setSelected(false);
         }

         this.scrollTarget = 0.0F;
         this.scrollOffset = -10.0F;
         this.categoryPanel = categoryPanel3;
         this.categoryPanel.setSelected(true);
         this.categoryPanel.resetAnimations();
      }
   }

   public float getDragOffsetX() {
      return this.dragOffsetX;
   }

   public Category getCategory() {
      return this.category;
   }

   public float getY() {
      return this.y;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   public void setScrollOffset(float f) {
      this.scrollOffset = f;
   }

   public float getScrollOffset() {
      return this.scrollOffset;
   }

   public void setSearchPanel(CategoryPanel categoryPanel3) {
      this.searchPanel = categoryPanel3;
   }

   public float getDragOffsetY() {
      return this.dragOffsetY;
   }

   public void setDragOffsetX(float f) {
      this.dragOffsetX = f;
   }

   public static float getFooterHeight() {
      return 30.0F;
   }

   public void setDragOffsetY(float f) {
      this.dragOffsetY = f;
   }

   public void setCategory(Category category2) {
      this.category = category2;
   }

   public CategoryPanel getSearchPanel() {
      return this.searchPanel;
   }

   public float getMainCategoriesHeight() {
      return this.mainCategoriesHeight;
   }

   @Override
   public void handleMouseInput() throws IOException {
      if (this.categoryPanel != null && Mouse.getEventDWheel() != 0) {
         int i = MathHelper.clamp_int(Mouse.getEventDWheel(), -1, 1);
         this.scrollTarget -= i * 20;
         this.scrollTarget = MathHelper.clamp_float(this.scrollTarget, 0.0F, this.categoryPanel.getMaxScroll());
         this.categoryPanel.setKeyBindPopup(null);
         if (this.categoryPanel.getColorSettingRow() != null) {
            this.categoryPanel.getColorSettingRow().closePicker();
         }

         this.categoryPanel.setColorSettingRow(null);
      }

      super.handleMouseInput();
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
   }

   @Override
   protected void keyTyped(char c, int i) throws IOException {
      this.categoryTab.handleKeyboardPress(c, i);
      if (this.categoryTab != null && !this.categoryTab.getSearchText().isEmpty()) {
         this.searchPanel.handleKeyboardPress(c, i);
      } else {
         if (this.categoryPanel != null) {
            KeyBindPopup keyBindPopup = this.categoryPanel.getKeyBindPopup();
            if (keyBindPopup != null) {
               if (i == 1 && !keyBindPopup.isListening()) {
                  this.categoryPanel.setKeyBindPopup(null);
                  this.categoryPanel.setComboSettingRow(null);
               } else {
                  keyBindPopup.handleKeyboardPress(c, i);
               }

               return;
            }
         }

         if (!this.categoryPanels.isEmpty()) {
            this.categoryPanels.forEach(categoryPanel2 -> categoryPanel2.handleKeyboardPress(c, i));
         }
      }

      if (i == ModuleManager.clickGui.getKeyBind()) {
         ModuleManager.clickGui.disable();
      }

      super.keyTyped(c, i);
   }

   public List<CategoryPanel> getCategoryPanels() {
      return this.categoryPanels;
   }

   public static float getWidth() {
      return 450.0F;
   }

   @Override
   protected void mouseClicked(int i, int i2, int i3) throws IOException {
      if (i3 == 0) {
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         if (i >= 10.0F && i <= 30.0F && i2 >= scaledResolution.getScaledHeight() - 20.0F - 10.0F && i2 <= scaledResolution.getScaledHeight() - 20.0F - 10.0F + 20.0F) {
            Novoline.getNovoline().getMenuClickGuiScreen().returnToClickGui = true;
            Novoline.getNovoline().getMenuClickGuiScreen().dashboardOpen = true;
            ModuleManager.clickGui.clickGui.setCurrentElement(0);
            ModuleManager.clickGui.disable();
            ModuleManager.clickGui.enable();
            return;
         }
      }

      this.categoryTab.handleMouseClick(i, i2, i3);
      if (i > this.x && i < this.x + getWidth() && i2 > this.y && i2 < this.y + getHeaderHeight() && i3 == 0) {
         this.dragOffsetX = this.x - i;
         this.dragOffsetY = this.y - i2;
         this.dragging = true;
      }

      if (this.categoryTab != null && !this.categoryTab.getSearchText().isEmpty()) {
         if (this.searchPanel.handleMouseClick(i, i2, i3)) {
            for (CategoryPanel categoryPanel3 : this.categoryPanels) {
               if (categoryPanel3.handleMouseClick(i, i2, i3) && categoryPanel3.isOverTab(i, i2)) {
                  this.categoryTab.setSearchText("");
                  this.updateSearch();
               }
            }
         }
      } else {
         for (CategoryPanel categoryPanel4 : this.categoryPanels) {
            if (!categoryPanel4.handleMouseClick(i, i2, i3)) {
               break;
            }
         }
      }
   }

   public float getX() {
      return this.x;
   }

   public void setCategoryTab(CategoryTab categoryTab2) {
      this.categoryTab = categoryTab2;
   }

   public ClickGuiScreen() {
      this.x = this.y = 100.0F;
      KeyBindPopup.noop();
   }

   public void setY(float f) {
      this.y = f;
   }

   public float getSmoothedScroll() {
      return this.scrollOffset;
   }

   public void updateSearch() {
      if (this.categoryTab != null && !this.categoryTab.getSearchText().isEmpty()) {
         this.scrollTarget = 0.0F;
         this.scrollOffset = -10.0F;
         String text = this.categoryTab.getSearchText().toLowerCase(Locale.ROOT);
         ArrayList arrayList = new ArrayList();
         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = getWidth() - CategoryPanel.getTabWidth() - 20.0F;

         for (Module module2 : ModuleManager.modules) {
            float f4 = CategoryPanel.getTabWidth() + 10.0F;
            float f5 = getHeaderHeight() + 10.0F + f2;
            float f6 = 0.0F;
            ArrayList arrayList2 = new ArrayList();
            boolean flag = module2.getName().toLowerCase(Locale.ROOT).startsWith(text);
            boolean flag2 = !Novoline.getNovoline()
               .getSettingManager()
               .getListByFeature(module2)
               .stream()
               .map(ModuleSetting.class::cast)
               .filter(moduleSetting -> moduleSetting.getName().toLowerCase(Locale.ROOT).startsWith(text))
               .map(moduleSetting -> {
                  if (moduleSetting instanceof CheckboxSetting) {
                     return new CheckboxSettingRow(moduleSetting, f3);
                  } else if (moduleSetting instanceof NumberSetting) {
                     return new SliderSettingRow(moduleSetting, f3);
                  } else if (!(moduleSetting instanceof ComboSetting) && !(moduleSetting instanceof MultiComboSetting)) {
                     return moduleSetting instanceof ColorSetting ? new ColorSettingRow(moduleSetting, f3) : null;
                  } else {
                     return new ComboSettingRow(moduleSetting, f3);
                  }
               })
               .filter(Objects::nonNull)
               .peek(arrayList2::add)
               .toList()
               .isEmpty();
            if (flag || flag2) {
               ModulePanel modulePanel = flag
                  ? new ModulePanel(module2, f4, f5, f3)
                  : new ModulePanel(module2.getCategory().getDisplayName() + " > " + module2.getName(), arrayList2, f6, f4, f5, f3);
               modulePanel.layoutRows();
               arrayList.add(modulePanel);
               float f7 = ModulePanel.getRowHeight() + modulePanel.getSettingsHeight() + 5.0F;
               f += f7;
               f2 += f7;
            }
         }

         this.searchPanel = new CategoryPanel(arrayList, f, 0.0F, 0.0F);
      }
   }

   public void setScrollTarget(float f) {
      this.scrollTarget = f;
   }

   public void reset() {
      this.categoryTab = new CategoryTab("Search", SEARCH_ICON, this.x, this.y, 200.0F, 20.0F);
      this.initialized = true;
      this.categoryPanels.clear();
      float f = 0.0F;

      for (Category category2 : Category.values()) {
         if (category2 != Category.SCRIPTS) {
            this.categoryPanels
               .add(
                  new CategoryPanel(
                     ClickGuiScreen.CombatType.byCategory(category2),
                     category2,
                     0.0F,
                     10.0F + Fonts.museoSans900Size36.getHeight() + 10.0F + Fonts.museoSans900Size12.getHeight() + 4.0F + f
                  )
               );
            f += CategoryPanel.getTabHeight() + 4.0F;
         }
      }

      this.mainCategoriesHeight = f;
      f += Fonts.museoSans900Size36.getHeight();
      this.categoryPanels
         .add(
            new CategoryPanel(
               ClickGuiScreen.CombatType.SCRIPTS.icon,
               Category.SCRIPTS,
               0.0F,
               5 + Fonts.museoSans900Size36.getHeight() + 10.0F + Fonts.museoSans900Size12.getHeight() + 4.0F + f
            )
         );
      this.categoryPanel = this.category == null
         ? this.categoryPanels.get(0)
         : this.categoryPanels.stream().filter(categoryPanel2 -> categoryPanel2.getCategory() == this.category).findFirst().orElseThrow();
      this.categoryPanel.setSelected(true);
   }

   public void setX(float f) {
      this.x = f;
   }

   public CategoryPanel getCategoryPanel() {
      return this.categoryTab != null && !this.categoryTab.getSearchText().isEmpty() ? this.searchPanel : this.categoryPanel;
   }

   @Override
   protected void mouseReleased(int i, int i2, int i3) {
      this.categoryTab.handleMouseRelease(i, i2, i3);
      this.dragging = false;
      if (this.categoryTab != null && !this.categoryTab.getSearchText().isEmpty()) {
         this.searchPanel.handleMouseRelease(i, i2, i3);
      } else if (!this.categoryPanels.isEmpty()) {
         this.categoryPanels.forEach(categoryPanel2 -> categoryPanel2.handleMouseRelease(i, i2, i3));
      }
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.scrollOffset = SmoothingUtils.approachWithMinimumStep(this.scrollOffset, this.scrollTarget, 10.0F, 0.05F);
      if (this.dragging) {
         this.x = this.dragOffsetX + i;
         this.y = this.dragOffsetY + i2;
      }

      this.categoryTab.setHeight(ModulePanel.getRowHeight());
      this.categoryTab.setX(this.x + CategoryPanel.getTabWidth() + 10.0F);
      this.categoryTab.setY(this.y + getHeaderHeight() / 2.0F - ModulePanel.getRowHeight() / 2.0F);
      RenderUtils.drawShadow(this.x, this.y, this.x + getWidth(), this.y + getHeight(), 2.0F, -300407760);
      RenderUtils.drawRect(this.x, this.y, this.x + getWidth(), this.y + getHeight(), -872018407);
      RenderUtils.drawRect(this.x + CategoryPanel.getTabWidth(), this.y, this.x + getWidth(), this.y + getHeight(), -16185331);
      RenderUtils.drawLine(this.x + CategoryPanel.getTabWidth(), this.y, this.x + CategoryPanel.getTabWidth(), this.y + getHeight(), 2.0F, -16311256, true);
      Fonts.museoSans900Size32
         .drawString(
            "NOVOLINE",
            this.x + CategoryPanel.getTabWidth() / 2.0F - Fonts.museoSans900Size32.getStringWidth("NOVOLINE") / 2.0F - 0.5F,
            this.y + 10.0F - 0.5F,
            -14506000
         );
      Fonts.museoSans900Size32
         .drawString("NOVOLINE", this.x + CategoryPanel.getTabWidth() / 2.0F - Fonts.museoSans900Size32.getStringWidth("NOVOLINE") / 2.0F, this.y + 10.0F, -1);
      RenderUtils.drawLine(this.x + CategoryPanel.getTabWidth(), this.y + getHeaderHeight(), this.x + getWidth(), this.y + getHeaderHeight(), 1342203824);
      Fonts.museoSans900Size12.drawString("Main", this.x + 10.0F, this.y + 10.0F + Fonts.museoSans900Size36.getHeight() + 10.0F, -10720647);
      Fonts.museoSans900Size12
         .drawString(
            "Other",
            this.x + 10.0F,
            this.y + 32.0F + Fonts.museoSans900Size12.getHeight() + 10.0F + this.mainCategoriesHeight + Fonts.museoSans900Size12.getHeight(),
            -10720647
         );

      for (CategoryPanel categoryPanel3 : this.categoryPanels) {
         categoryPanel3.drawTab(i, i2, f);
         if ((this.categoryTab == null || this.categoryTab.getSearchText().isEmpty()) && categoryPanel3.equals(this.categoryPanel)) {
            categoryPanel3.beginFramebuffer();
            categoryPanel3.draw(i, i2, f);
            categoryPanel3.drawFramebuffer();
         }
      }

      if (this.categoryTab != null && !this.categoryTab.getSearchText().isEmpty() && this.searchPanel != null) {
         this.searchPanel.beginFramebuffer();
         this.searchPanel.draw(i, i2, f);
         this.searchPanel.drawFramebuffer();
      }

      if (this.categoryTab != null) {
         this.categoryTab.draw(i, i2, f);
      }

      RenderUtils.drawLine(
         this.x, this.y + getHeight() - getFooterHeight(), this.x + CategoryPanel.getTabWidth(), this.y + getHeight() - getFooterHeight(), 1342203824
      );
      RenderUtils.drawTextureWithAlpha(this.x + 5.0F, this.y + getHeight() - getFooterHeight() / 2.0F - 10.0F, 20.0F, 20.0F, 1.0F, PROFILE_PICTURE);
      Fonts.museoSans700Size14.drawString(Novoline.username, this.x + 30.0F, this.y + getHeight() - 20.0F, -1);
      Fonts.museoSans900Size12.drawString("User ID:", this.x + 30.0F, this.y + getHeight() - 12.0F, -10720647);
      Fonts.museoSans900Size12
         .drawString(Novoline.userId, this.x + 30.0F + Fonts.museoSans900Size12.getStringWidth("User ID: "), this.y + getHeight() - 12.0F, -13981697);
      RenderUtils.drawTextureWithAlpha(
         10.0F, new ScaledResolution(this.mc).getScaledHeight() - 20.0F - 10.0F, 20.0F, 20.0F, 1.0F, new ResourceLocation("novoline", "gui/script.png")
      );
   }

   public static float getHeaderHeight() {
      return 10.0F + Fonts.museoSans900Size36.getHeight() + 7.0F;
   }

   public void setMainCategoriesHeight(float f) {
      this.mainCategoriesHeight = f;
   }

   @Override
   public void startClosing() {
      ModuleManager.clickGui.disable();
      this.category = this.categoryPanel.getCategory();
      Keyboard.enableRepeatEvents(false);
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public static float getHeight() {
      return 350.0F;
   }

   public CategoryTab getCategoryTab() {
      return this.categoryTab;
   }

   enum CombatType {
      COMBAT(Category.COMBAT, new ResourceLocation("novoline", "images/clickgui/neverlose/combat.png")),
      PLAYER(Category.PLAYER, new ResourceLocation("novoline", "images/clickgui/neverlose/player.png")),
      MOVEMENT(Category.MOVEMENT, new ResourceLocation("novoline", "images/clickgui/neverlose/movement.png")),
      VISUAL(Category.VISUAL, new ResourceLocation("novoline", "images/clickgui/neverlose/visual.png")),
      WORLD(Category.WORLD, new ResourceLocation("novoline", "images/clickgui/neverlose/world.png")),
      MISC(Category.MISC, new ResourceLocation("novoline", "images/clickgui/neverlose/misc.png")),
      HUD(Category.HUD, new ResourceLocation("novoline", "images/clickgui/neverlose/hud.png")),
      SCRIPTS(Category.SCRIPTS, new ResourceLocation("novoline", "images/clickgui/neverlose/scripts.png"));

      private final Category category;
      private final ResourceLocation icon;

      public Category getCategory() {
         return this.category;
      }

      public ResourceLocation getResourceLocation() {
         return this.icon;
      }

      public static ResourceLocation byCategory(Category category2) {
         return Arrays.stream(values()).filter(combatType -> combatType.category.equals(category2)).findFirst().map(combatType -> combatType.icon).orElse(null);
      }

      CombatType(Category category2, ResourceLocation resourceLocation) {
         this.category = category2;
         this.icon = resourceLocation;
      }
   }
}
