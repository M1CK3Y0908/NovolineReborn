package wtf.novoline.gui.clickgui;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.ModuleSetting.CheckboxType;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class ComboSettingRow extends SettingRow {
   private int boxColor = -16314860;
   private float arrowOffset;
   private boolean followsScroll = true;
   private int optionCount;
   private float dropdownHeight;

   @Override
   public void resetAnimation() {
      this.dropdownHeight = 0.0F;
      this.boxColor = -16314860;
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
   }

   @Override
   public void draw(float f, float f2, float f3) {
      if (this.followsScroll) {
         super.draw(f, f2, f3);
      }

      boolean flag = clickGuiScreen.getCategoryPanel().getComboSettingRow() != null && clickGuiScreen.getCategoryPanel().getComboSettingRow().equals(this);
      this.boxColor = RenderUtils.smoothBlendColors(this.boxColor, flag ? -16311766 : -16314860, 10.0F);
      int i = !this.isMultiCombo() ? ((ComboSetting)this.moduleSetting).getCurrentElement() : -1;
      int i2 = -1;
      List list = null;
      String[] texts = null;
      if (this.moduleSetting instanceof MultiComboSetting multiComboSetting) {
         i2 = multiComboSetting.getElements().size();
         list = multiComboSetting.getElements();
      } else if (this.moduleSetting instanceof ComboSetting comboSetting) {
         i2 = comboSetting.getElements().length;
         texts = comboSetting.getElements();
      }

      this.optionCount = i2;
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() - (this.followsScroll ? clickGuiScreen.getScrollOffset() : 0.0F) + this.y;
      CFontRenderer cFontRenderer = Fonts.museoSans700Size14;
      int height = cFontRenderer.getHeight();
      cFontRenderer.drawString(this.moduleSetting.getName(), f4 + 5.0F, f5 + ModulePanel.getRowHeight() / 2.0F - height / 2.0F + 1.0F, -6509895);
      if (flag) {
         this.dropdownHeight = SmoothingUtils.approachWithMinimumStep(this.dropdownHeight, 8.0F * i2, 10.0F, 0.05F);
         RenderUtils.drawShadow(
            f4 + this.width / 2.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F - 4.0F,
            f4 + this.width - 4.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F + 4.0F + this.dropdownHeight,
            1.5F,
            -1439942055
         );
      } else {
         RenderUtils.drawShadow(
            f4 + this.width / 2.0F - 0.5F,
            f5 + ModulePanel.getRowHeight() / 2.0F - 4.0F - 0.5F,
            f4 + this.width - 4.0F + 0.5F,
            f5 + ModulePanel.getRowHeight() / 2.0F + 4.0F + 0.5F,
            0.5F,
            -14931383
         );
      }

      RenderUtils.drawRect(
         f4 + this.width / 2.0F,
         f5 + ModulePanel.getRowHeight() / 2.0F - 4.0F,
         f4 + this.width - 4.0F,
         f5 + ModulePanel.getRowHeight() / 2.0F + 4.0F,
         this.boxColor
      );
      if (this.isMultiCombo() && this.moduleSetting instanceof MultiComboSetting multiComboSetting2) {
         String text = multiComboSetting2.getElements().stream().filter(Option::isSelected).map(Option::getName).collect(Collectors.joining(", "));
         boolean empty = text.isEmpty();
         RenderUtils.enableScissor(
            (int)(f4 + this.width / 2.0F),
            (int)(f5 + ModulePanel.getRowHeight() / 2.0F - 4.0F),
            (int)(f4 + this.width - 4.0F - (f4 + this.width / 2.0F)),
            8
         );
         String text2 = empty ? "Select..." : text;
         float f6 = f4 + this.width / 2.0F + 1.0F;
         float f7 = f5 + ModulePanel.getRowHeight() / 2.0F - height / 2.0F + 1.0F;
         int i3 = empty ? -11773335 : (flag ? -1 : -6509895);
         float f8 = f7;
         float f9 = f6;
         cFontRenderer.drawString(text2, f9, f8, i3);
         RenderUtils.disableScissor();
         GlStateManager.alphaFunc(516, 0.001F);
         RenderUtils.drawRect(
            f4 + this.width - 12.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F - 4.0F,
            f4 + this.width - 4.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F + 4.0F,
            this.boxColor
         );
         RenderUtils.drawHorizontalGradientRect(
            f4 + this.width - 12.0F - 10.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F - 4.0F,
            f4 + this.width - 12.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F + 4.0F,
            RenderUtils.withAlphaFraction(this.boxColor, 0.0F),
            this.boxColor
         );
         GlStateManager.alphaFunc(516, 0.1F);
      } else {
         String currentElementAsString = ((ComboSetting)this.moduleSetting).getCurrentElementAsString();
         float f10 = f4 + this.width / 2.0F + 1.0F;
         float f11 = f5 + ModulePanel.getRowHeight() / 2.0F - height / 2.0F + 1.0F;
         int i4 = flag ? -1 : -6509895;
         float f12 = f11;
         float f13 = f10;
         cFontRenderer.drawString(currentElementAsString, f13, f12, i4);
      }

      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 1);
      RenderUtils.setColor(flag ? -1 : -6509895);
      this.arrowOffset = SmoothingUtils.approach(this.arrowOffset, flag ? 2.5F : -2.5F, 10.0F);
      float f14 = f4 + this.width - 6.0F;
      float f15 = f5 + ModulePanel.getRowHeight() / 2.0F;
      GL11.glLineWidth(2.0F);
      GL11.glEnable(2848);
      GL11.glBegin(3);
      GL11.glVertex2f(f14, f15 + this.arrowOffset / 2.0F);
      GL11.glVertex2f(f14 - 2.0F, f15 - this.arrowOffset / 2.0F);
      GL11.glVertex2f(f14 - 4.0F, f15 + this.arrowOffset / 2.0F);
      GL11.glEnd();
      GL11.glDisable(2848);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      if (flag) {
         f14 = i2 * 8.0F;

         for (int i5 = i2 - 1; i5 >= 0; i5--) {
            String text3 = this.isMultiCombo() ? ((Option)list.get(i5)).getName() : texts[i5];
            float f16 = Math.min(this.dropdownHeight, f14);
            RenderUtils.drawRect(
               f4 + this.width / 2.0F,
               f5 + ModulePanel.getRowHeight() / 2.0F - 4.0F + f16,
               f4 + this.width - 4.0F,
               f5 + ModulePanel.getRowHeight() / 2.0F + 4.0F + f16,
               this.boxColor
            );
            float f17 = f4 + this.width / 2.0F + 1.0F;
            float f18 = f5 + ModulePanel.getRowHeight() / 2.0F - height / 2.0F + 1.0F + f16;
            int i6 = this.isMultiCombo() ? (((Option)list.get(i5)).isSelected() ? -1 : -6509895) : (i5 == i ? -1 : -6509895);
            float f19 = f18;
            float f20 = f17;
            cFontRenderer.drawString(text3, f20, f19, i6);
            f14 -= 8.0F;
         }
      }
   }

   public ComboSettingRow(ModuleSetting moduleSetting2, float f) {
      super(moduleSetting2, f);
   }

   private boolean isMultiCombo() {
      return this.moduleSetting.getCheckboxType().equals(CheckboxType.MULTI_COMBO);
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      float f3 = clickGuiScreen.getX() + this.x;
      float f4 = clickGuiScreen.getY() - (this.followsScroll ? clickGuiScreen.getScrollOffset() : 0.0F) + this.y;
      ComboSettingRow comboSettingRow = clickGuiScreen.getCategoryPanel().getComboSettingRow();
      boolean flag = comboSettingRow != null && comboSettingRow.equals(this);
      if (f >= f3 + this.width / 2.0F
         && f <= f3 + this.width - 4.0F
         && f2 >= f4 + ModulePanel.getRowHeight() / 2.0F - 4.0F
         && f2 <= f4 + ModulePanel.getRowHeight() / 2.0F + 4.0F) {
         if (i == 0) {
            clickGuiScreen.getCategoryPanel().setComboSettingRow(flag ? null : this);
            return false;
         }
      } else if (flag) {
         int i2 = -1;
         if (this.moduleSetting instanceof MultiComboSetting multiComboSetting) {
            i2 = multiComboSetting.getElements().size();
         } else if (this.moduleSetting instanceof ComboSetting comboSetting) {
            i2 = comboSetting.getElements().length;
         }

         boolean flag2 = true;
         float f5 = i2 * 8.0F;

         for (int i3 = i2 - 1; i3 >= 0; i3--) {
            if (f >= f3 + this.width / 2.0F
               && f <= f3 + this.width - 4.0F
               && f2 >= f4 + ModulePanel.getRowHeight() / 2.0F - 4.0F + f5
               && f2 <= f4 + ModulePanel.getRowHeight() / 2.0F + 4.0F + f5) {
               if (this.moduleSetting instanceof MultiComboSetting multiComboSetting2) {
                  multiComboSetting2.getElements().get(i3).toggleSelection();
                  multiComboSetting2.interactCallback();
                  clickGuiScreen.getCategoryPanel().refreshLayout();
               } else {
                  ComboSetting comboSetting2 = (ComboSetting)this.moduleSetting;
                  comboSetting2.setCurrentElement(i3);
                  comboSetting2.interactCallback();
                  clickGuiScreen.getCategoryPanel().refreshLayout();
               }

               flag2 = false;
            }

            f5 -= 8.0F;
         }

         if (!this.isMultiCombo()) {
            clickGuiScreen.getCategoryPanel().setComboSettingRow(null);
         }

         return flag2;
      }

      return true;
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
   }

   public void detachFromScroll() {
      this.followsScroll = false;
   }

   public int getOptionCount() {
      return this.optionCount;
   }
}
