package wtf.novoline.gui.clickgui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;

public class CategoryPanel implements ClickGuiComponent {
   private Framebuffer framebuffer;
   private float maxScroll;
   private KeyBindPopup keyBindPopup;
   private List<ModulePanel> modulePanels = new ArrayList<>();
   private final ResourceLocation icon;
   private ColorSettingRow colorSettingRow;
   private final Category category;
   private final float x;
   private final float y;
   private float fadeAlpha;
   private ComboSettingRow comboSettingRow;
   private boolean selected;

   public boolean isOverTab(float f, float f2) {
      float f3 = clickGuiScreen.getX() + this.x;
      float f4 = clickGuiScreen.getY() + this.y;
      return f > f3 && f < f3 + getTabWidth() && f2 > f4 && f2 < f4 + getTabHeight();
   }

   public void resetAnimations() {
      this.fadeAlpha = 0.0F;
      this.modulePanels.forEach(ModulePanel::resetRowAnimations);
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      CategoryPanel categoryPanel = clickGuiScreen.getCategoryPanel();
      if (categoryPanel != null && categoryPanel.equals(this) && !this.modulePanels.isEmpty()) {
         this.modulePanels.forEach(modulePanel -> modulePanel.handleMouseRelease(f, f2, i));
      }
   }

   public KeyBindPopup getKeyBindPopup() {
      return this.keyBindPopup;
   }

   public void layoutModuleRows() {
      this.modulePanels.forEach(ModulePanel::layoutRows);
   }

   public static float getTabHeight() {
      return 14.0F;
   }

   public Category getCategory() {
      return this.category;
   }

   public float getX() {
      return this.x;
   }

   public void setKeyBindPopup(KeyBindPopup keyBindPopup2) {
      this.keyBindPopup = keyBindPopup2;
   }

   public void beginFramebuffer() {
      this.framebuffer = RenderUtils.ensureFramebuffer(this.framebuffer);
      this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.framebuffer.framebufferClear();
      this.framebuffer.bindFramebuffer(true);
   }

   public void layoutModulePanels() {
      if (clickGuiScreen.getCategoryTab().getSearchText().isEmpty()) {
         float f = ClickGuiScreen.getWidth() / 2.0F - 65.0F;
         float f2 = getTabWidth() + 10.0F;
         float f3 = ClickGuiScreen.getHeaderHeight() + 10.0F;
         float f4 = f2 + f + 10.0F;
         float f5 = f3;

         for (ModulePanel modulePanel2 : this.modulePanels) {
            boolean flag = f5 < f3;
            float f6 = flag ? f5 : f3;
            float f7 = flag ? f4 : f2;
            modulePanel2.setX(f7);
            modulePanel2.setY(f6);
            modulePanel2.setWidth(f);
            float f8 = ModulePanel.getRowHeight() + modulePanel2.getRowsHeight();
            if (flag) {
               f5 += f8 + 5.0F;
            } else {
               f3 += f8 + 5.0F;
            }
         }

         this.maxScroll = Math.max(0.0F, Math.max(f3, f5) - ClickGuiScreen.getHeight());
      } else {
         float f9 = 0.0F;
         float f10 = ClickGuiScreen.getWidth() - getTabWidth() - 20.0F;

         for (ModulePanel modulePanel3 : this.modulePanels) {
            float f11 = getTabWidth() + 10.0F;
            float f12 = ClickGuiScreen.getHeaderHeight() + 10.0F + f9;
            modulePanel3.setSettingsHeight(modulePanel3.getRowsHeight());
            modulePanel3.setWidth(f10);
            modulePanel3.setX(f11);
            modulePanel3.setY(f12);
            float f13 = ModulePanel.getRowHeight() + modulePanel3.getSettingsHeight() + 5.0F;
            this.maxScroll += f13;
            f9 += f13;
         }
      }
   }

   public boolean isSelected() {
      return this.selected;
   }

   public CategoryPanel(ResourceLocation resourceLocation, Category category2, float f, float f2) {
      this.icon = resourceLocation;
      this.category = category2;
      this.x = f;
      this.y = f2;
      this.selected = false;

      for (Module module2 : ModuleManager.modules) {
         if (module2.getCategory().equals(category2)) {
            this.modulePanels.add(new ModulePanel(module2, 0.0F, 0.0F, 0.0F));
         }
      }

      this.layoutModulePanels();
      this.layoutModuleRows();
   }

   public void setColorSettingRow(ColorSettingRow colorSettingRow2) {
      this.colorSettingRow = colorSettingRow2;
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (this.isOverTab(f, f2)) {
         clickGuiScreen.selectCategoryPanel(this);
      }

      CategoryPanel categoryPanel = clickGuiScreen.getCategoryPanel();
      if (categoryPanel != null && categoryPanel.equals(this)) {
         if (this.colorSettingRow != null && !this.colorSettingRow.handleMouseClick(f, f2, i)) {
            return false;
         }

         Iterator iterator2 = this.modulePanels.iterator();

         while (iterator2.hasNext()) {
            if (!((ModulePanel)iterator2.next()).handleMouseClick(f, f2, i)) {
               return false;
            }
         }
      }

      return true;
   }

   public ColorSettingRow getColorSettingRow() {
      return this.colorSettingRow;
   }

   public float getMaxScroll() {
      float maxScroll2 = this.maxScroll;
      if (this.comboSettingRow != null) {
         float f = this.comboSettingRow.getOptionCount() * 8.0F;
         float height = ClickGuiScreen.getHeight();
         if (this.comboSettingRow.getY() - clickGuiScreen.getScrollOffset() + 8.0F + f > height) {
            maxScroll2 += f;
         }
      }

      return maxScroll2;
   }

   public void refreshLayout() {
      this.modulePanels.forEach(ModulePanel::refreshSettings);
      clickGuiScreen.getCategoryPanel().layoutModulePanels();
      clickGuiScreen.getCategoryPanel().layoutModuleRows();
      clickGuiScreen.setScrollTarget(MathHelper.clamp_float(clickGuiScreen.scrollTarget, 0.0F, this.getMaxScroll()));
   }

   public ComboSettingRow getComboSettingRow() {
      return this.comboSettingRow;
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
      CategoryPanel categoryPanel = clickGuiScreen.getCategoryPanel();
      if (categoryPanel != null && categoryPanel.equals(this)) {
         this.modulePanels.forEach(modulePanel -> modulePanel.handleKeyboardPress(c, i));
      }
   }

   public static float getTabWidth() {
      return 100.0F;
   }

   public CategoryPanel(List<ModulePanel> list, float f, float f2, float f3) {
      this.icon = null;
      this.category = null;
      this.x = f2;
      this.y = f3;
      this.selected = true;
      this.modulePanels = list;
      this.maxScroll = Math.max(0.0F, f - ClickGuiScreen.getHeight());
   }

   public void draw(float f, float f2, float f3) {
      if (!this.modulePanels.isEmpty()) {
         Iterator iterator2 = this.modulePanels.iterator();

         while (iterator2.hasNext()) {
            ((ModulePanel)iterator2.next()).draw(f, f2, f3);
         }

         if (this.comboSettingRow != null) {
            this.comboSettingRow.draw(f, f2, f3);
         }

         if (this.keyBindPopup != null) {
            this.keyBindPopup.render(f, f2, f3);
         }

         if (this.colorSettingRow != null) {
            this.colorSettingRow.drawColorPicker(f, f2, f3);
         }
      }
   }

   public float getY() {
      return this.y;
   }

   public void setSelected(boolean flag) {
      this.selected = flag;
   }

   public void drawTab(float f, float f2, float f3) {
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() + this.y;
      if (this.selected) {
         RenderUtils.drawShadow(f4 + 6.0F, f5, f4 + getTabWidth() - 6.0F, f5 + getTabHeight(), 2.0F, 1879074736);
         RenderUtils.drawRect(f4 + 6.0F, f5, f4 + getTabWidth() - 6.0F, f5 + getTabHeight(), 1879074736);
      }

      if (this.icon != null) {
         RenderUtils.drawTextureWithAlpha(f4 + 10.0F, f5 + getTabHeight() / 2.0F - 4.0F, 8.0F, 8.0F, 1.0F, this.icon);
      }

      Fonts.museoSans700Size14
         .drawString(this.category.getDisplayName(), f4 + 22.0F, f5 + getTabHeight() / 2.0F - Fonts.museoSans700Size14.getHeight() / 2.0F + 1.0F, -1);
   }

   public void drawFramebuffer() {
      this.fadeAlpha = SmoothingUtils.approachWithMinimumStep(this.fadeAlpha, 255.0F, 10.0F, 0.05F);
      Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
      GlStateManager.color(1.0F, 1.0F, 1.0F, this.fadeAlpha / 255.0F);
      GlStateManager.alphaFunc(516, 0.001F);
      RenderUtils.applyBlendFunc();
      GlStateManager.enableBlend();
      GlStateManager.enableTexture2D();
      this.framebuffer.bindFramebufferTexture();
      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      GlStateManager.pushMatrix();
      RenderUtils.drawFlippedTexturedQuad(
         scaledResolution,
         clickGuiScreen.getX() + getTabWidth(),
         clickGuiScreen.getY() + ClickGuiScreen.getHeaderHeight(),
         ClickGuiScreen.getWidth() - getTabWidth(),
         ClickGuiScreen.getHeight() - ClickGuiScreen.getHeaderHeight()
      );
      GlStateManager.popMatrix();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.disableBlend();
      RenderUtils.applyBlendFunc();
      RenderUtils.drawVerticalGradientRect(
         clickGuiScreen.getX() + getTabWidth(),
         clickGuiScreen.getY() + ClickGuiScreen.getHeaderHeight(),
         clickGuiScreen.getX() + getTabWidth() + (ClickGuiScreen.getWidth() - getTabWidth()),
         clickGuiScreen.getY() + ClickGuiScreen.getHeaderHeight() + 10.0F,
         -16185331,
         591885
      );
      RenderUtils.drawVerticalGradientRect(
         clickGuiScreen.getX() + getTabWidth(),
         clickGuiScreen.getY() + ClickGuiScreen.getHeaderHeight() + (ClickGuiScreen.getHeight() - ClickGuiScreen.getHeaderHeight()) - 10.0F,
         clickGuiScreen.getX() + getTabWidth() + (ClickGuiScreen.getWidth() - getTabWidth()),
         clickGuiScreen.getY() + ClickGuiScreen.getHeaderHeight() + (ClickGuiScreen.getHeight() - ClickGuiScreen.getHeaderHeight()),
         591885,
         -16185331
      );
   }

   public void setComboSettingRow(ComboSettingRow comboSettingRow2) {
      if (this.comboSettingRow != comboSettingRow2) {
         this.comboSettingRow = comboSettingRow2;
         if (this.comboSettingRow != null) {
            this.comboSettingRow.resetAnimation();
         }
      }
   }
}
