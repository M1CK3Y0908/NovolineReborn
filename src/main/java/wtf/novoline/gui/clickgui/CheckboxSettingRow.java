package wtf.novoline.gui.clickgui;

import wtf.novoline.Novoline.Fonts;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class CheckboxSettingRow extends SettingRow {
   private int trackColor = -16116706;
   private float knobOffset;
   private int knobColor = -12826023;

   public CheckboxSettingRow(ModuleSetting moduleSetting2, float f) {
      super(moduleSetting2, f);
   }

   @Override
   public void draw(float f, float f2, float f3) {
      super.draw(f, f2, f3);
      CheckboxSetting checkboxSetting = (CheckboxSetting)this.moduleSetting;
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      CFontRenderer cFontRenderer = Fonts.museoSans700Size14;
      String name = this.moduleSetting.getName();
      float f6 = f4 + 5.0F;
      float f7 = f5 + ModulePanel.getRowHeight() / 2.0F - Fonts.museoSans700Size14.getHeight() / 2.0F + 1.0F;
      int i = checkboxSetting.isEnabled() ? -1 : -6509895;
      float f8 = f7;
      float f9 = f6;
      cFontRenderer.drawString(name, f9, f8, i);
      if (checkboxSetting.isEnabled()) {
         this.trackColor = RenderUtils.smoothBlendColors(this.trackColor, -15589560, 10.0F);
         this.knobColor = RenderUtils.smoothBlendColors(this.knobColor, -13981697, 10.0F);
         this.knobOffset = SmoothingUtils.approach(this.knobOffset, 7.0F, 10.0F);
      } else {
         this.trackColor = RenderUtils.smoothBlendColors(this.trackColor, -16116706, 10.0F);
         this.knobColor = RenderUtils.smoothBlendColors(this.knobColor, -12826023, 10.0F);
         this.knobOffset = SmoothingUtils.approach(this.knobOffset, 0.0F, 10.0F);
      }

      RenderUtils.drawRect(
         f4 + this.width - 8.0F - 7.0F,
         f5 + ModulePanel.getRowHeight() - 10.5F,
         f4 + this.width - 8.0F,
         f5 + ModulePanel.getRowHeight() - 5.5F,
         this.trackColor
      );
      RenderUtils.drawCircle(f4 + this.width - 8.0F, f5 + ModulePanel.getRowHeight() - 8.0F, 2.0F, 1.0F, this.trackColor);
      RenderUtils.drawCircle(f4 + this.width - 8.0F - 7.0F, f5 + ModulePanel.getRowHeight() - 8.0F, 2.0F, 1.0F, this.trackColor);
      RenderUtils.drawCircle(f4 + this.width - 8.0F - 7.0F + this.knobOffset, f5 + ModulePanel.getRowHeight() - 8.0F, 3.0F, 1.0F, this.knobColor);
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      float f3 = clickGuiScreen.getX() + this.x;
      float f4 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      if (f >= f3 + this.width - 8.0F - 7.0F - 3.0F
         && f <= f3 + this.width - 8.0F + 3.0F
         && f2 >= f4 + ModulePanel.getRowHeight() - 10.5F - 2.0F
         && f2 <= f4 + ModulePanel.getRowHeight() - 5.5F + 2.0F
         && i == 0) {
         ((CheckboxSetting)this.moduleSetting).toggle();
         clickGuiScreen.getCategoryPanel().refreshLayout();
      }

      return true;
   }

   public void setKnobOffset(float f) {
      this.knobOffset = f;
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
   }

   @Override
   public void resetAnimation() {
      this.trackColor = -16116706;
      this.knobColor = -12826023;
      this.knobOffset = 0.0F;
   }
}
