package wtf.novoline.gui.clickgui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.NumberSetting.IntType;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;

public class SliderSettingRow extends SettingRow {
   private boolean dragging;
   private boolean editingText;
   private float knobOffset;
   private String inputText = "";
   private static final DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

   public boolean isEditingText() {
      return this.editingText;
   }

   @Override
   public void resetAnimation() {
      this.knobOffset = 0.0F;
   }

   private String getDigitLabel(int i) {
      return i == 11 ? "0" : String.valueOf(i - 1);
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      this.dragging = false;
   }

   @Override
   public void draw(float f, float f2, float f3) {
      super.draw(f, f2, f3);
      NumberSetting numberSetting = (NumberSetting)this.moduleSetting;
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      Fonts.museoSans700Size14
         .drawString(
            this.moduleSetting.getName(),
            f4 + 5.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F - Fonts.museoSans700Size14.getHeight() / 2.0F + 1.0F,
            -6509895
         );
      float stringWidth = Fonts.museoSans700Size14.getStringWidth(this.moduleSetting.getName());
      if (stringWidth + 5.0F > this.width / 2.0F - 5.0F) {
         GlStateManager.alphaFunc(516, 0.001F);
         RenderUtils.drawRect(f4 + this.width / 2.0F - 3.0F, f5, f4 + this.width, f5 + ModulePanel.getRowHeight(), -16577254);
         RenderUtils.drawHorizontalGradientRect(
            f4 + this.width / 2.0F - 10.0F - 3.0F, f5, f4 + this.width / 2.0F - 3.0F, f5 + ModulePanel.getRowHeight(), 199962, -16577254
         );
         GlStateManager.alphaFunc(516, 0.1F);
      }

      float f6 = this.width / 2.0F
         + (this.width - 27.0F - this.width / 2.0F)
         + (numberSetting.getCurrentValue() - numberSetting.getMaxValue()) * ((this.width - 27.0F - this.width / 2.0F) / (numberSetting.getMaxValue() - numberSetting.getMinValue()));
      if (this.dragging) {
         float f7 = numberSetting.getMinValue()
            + (f - f4 - this.width / 2.0F) / (this.width - 27.0F - this.width / 2.0F) * (numberSetting.getMaxValue() - numberSetting.getMinValue());
         numberSetting.setCurrentValue(f7);
         f6 = MathHelper.clamp_float(f, f4 + this.width / 2.0F, f4 + this.width - 27.0F) - f4;
      }

      this.knobOffset = SmoothingUtils.approach(this.knobOffset, f6 - this.width / 2.0F, 10.0F);
      RenderUtils.drawLine(
         f4 + this.width / 2.0F,
         f5 + ModulePanel.getRowHeight() / 2.0F,
         f4 + this.width - 27.0F,
         f5 + ModulePanel.getRowHeight() / 2.0F,
         1.5F,
         -16750672,
         false
      );
      RenderUtils.drawCircle(f4 + this.width / 2.0F + this.knobOffset, f5 + ModulePanel.getRowHeight() / 2.0F, 3.0F, 1.0F, -16611376);
      RenderUtils.drawShadow(
         f4 + this.width - 21.0F - 0.5F,
         f5 + ModulePanel.getRowHeight() / 2.0F - 3.0F - 0.5F,
         f4 + this.width - 5.0F + 0.5F,
         f5 + ModulePanel.getRowHeight() / 2.0F + 3.0F + 0.5F,
         0.5F,
         -14931383
      );
      RenderUtils.drawRect(
         f4 + this.width - 21.0F,
         f5 + ModulePanel.getRowHeight() / 2.0F - 3.0F,
         f4 + this.width - 5.0F,
         f5 + ModulePanel.getRowHeight() / 2.0F + 3.0F,
         -16314860
      );
      RenderUtils.enableScissor((int)(f4 + this.width - 20.0F), (int)(f5 + ModulePanel.getRowHeight() / 2.0F - 3.0F - 0.5F), 14, 7);
      IntType type = numberSetting.getType();
      String text2 = this.editingText
         ? this.inputText
         : (type == IntType.DECIMAL ? decimalFormat.format(numberSetting.getCurrentValue()) : String.valueOf((int)numberSetting.getCurrentValue()));
      float stringWidth2 = Fonts.museoSans500Size11.getStringWidth(text2);
      int i = this.editingText ? -1 : -6509895;
      if (stringWidth2 > 14.0F) {
         Fonts.museoSans500Size11.drawString(text2, f4 + this.width - 20.0F, f5 + ModulePanel.getRowHeight() / 2.0F - 0.5F, i);
      } else {
         Fonts.museoSans500Size11.drawString(text2, f4 + this.width - 13.0F - stringWidth2 / 2.0F, f5 + ModulePanel.getRowHeight() / 2.0F - 0.5F, i);
      }

      RenderUtils.disableScissor();
   }

   public SliderSettingRow(ModuleSetting moduleSetting2, float f) {
      super(moduleSetting2, f);
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      float f3 = clickGuiScreen.getX() + this.x;
      float f4 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      this.applyInputText();
      this.editingText = false;
      if (f >= f3 + this.width / 2.0F
         && f <= f3 + this.width - 27.0F
         && f2 >= f4 + ModulePanel.getRowHeight() / 2.0F - 4.0F
         && f2 <= f4 + ModulePanel.getRowHeight() / 2.0F + 4.0F) {
         if (i == 0) {
            this.dragging = true;
         }
      } else if (f >= f3 + this.width - 21.0F
         && f <= f3 + this.width - 5.0F
         && f2 >= f4 + ModulePanel.getRowHeight() / 2.0F - 3.0F
         && f2 <= f4 + ModulePanel.getRowHeight() / 2.0F + 3.0F
         && i == 0) {
         NumberSetting numberSetting = (NumberSetting)this.moduleSetting;
         this.editingText = true;
         this.inputText = decimalFormat.format(numberSetting.getCurrentValue());
      }

      return true;
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
      if (i == 14) {
         if (!this.inputText.isEmpty()) {
            this.inputText = this.inputText.substring(0, this.inputText.length() - 1);
         }
      } else if (i == 28) {
         this.applyInputText();
         this.editingText = false;
      } else if (i >= 2 && i <= 11) {
         this.inputText = this.inputText + this.getDigitLabel(i);
      } else if (i == 52) {
         if (!this.inputText.contains(".")) {
            this.inputText = this.inputText + ".";
         }
      } else if (i == 12 && this.inputText.isEmpty()) {
         this.inputText = this.inputText + "-";
      }
   }

   private void applyInputText() {
      if (this.editingText) {
         NumberSetting numberSetting = (NumberSetting)this.moduleSetting;

         try {
            numberSetting.setCurrentValueIgnoreNormalize(Float.parseFloat(decimalFormat.format(Float.parseFloat(this.inputText))));
            clickGuiScreen.getCategoryPanel().refreshLayout();
         } catch (Exception exception) {
         }
      }
   }
}
