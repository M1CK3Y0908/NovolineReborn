package wtf.novoline.gui.clickgui;

import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.lwjgl.input.Mouse;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shape.OutlineRectRenderer;

public class ColorSettingRow extends SettingRow {
   private boolean pickerOpen;
   private boolean draggingAlpha;
   private float hue;
   private static final ResourceLocation PALETTE_ICON = new ResourceLocation("novoline", "images/clickgui/neverlose/palette.png");
   private float alpha;
   private String hexText;
   private float gradientHeight;
   private static final MutableMap<Integer, Integer> shadeColorCache = Maps.mutable.empty();
   private static final MutableMap<Integer, Integer> hueColorCache = Maps.mutable.empty();
   private float saturation;
   private float brightness;
   private float pickerX;
   private static final float PICKER_WIDTH = 100.0F;
   private final ColorSetting colorSetting;
   private boolean draggingHue;
   private float pickerY;
   private boolean draggingSaturationBrightness;

   private float getVerticalFraction(float f) {
      float f2 = f - clickGuiScreen.getY() - this.pickerY;
      float f3 = (this.gradientHeight - f2) / this.gradientHeight;
      return MathHelper.clamp_float(f3, 0.0F, 1.0F);
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      float f3 = clickGuiScreen.getX() + this.x;
      float f4 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      CategoryPanel categoryPanel = clickGuiScreen.getCategoryPanel();
      ColorSettingRow colorSettingRow = categoryPanel.getColorSettingRow();
      if (!(f >= f3 + this.width - 23.0F)
         || !(f <= f3 + this.width - 15.0F)
         || !(f2 >= f4 + ModulePanel.getRowHeight() - 12.0F)
         || !(f2 <= f4 + ModulePanel.getRowHeight() - 3.5F)
         || colorSettingRow != null && colorSettingRow.isInsidePicker(f, f2)) {
         if (this.pickerOpen) {
            f3 = clickGuiScreen.getX() + this.pickerX;
            f4 = clickGuiScreen.getY() + this.pickerY;
            float f5 = f3 - 100.0F - 7.0F;
            if (!(f >= f5) || !(f2 >= f4 - 5.0F) || !(f <= f3 + 13.0F) || !(f2 <= f4 + this.gradientHeight + 15.0F)) {
               if (clickGuiScreen.getCategoryPanel().getColorSettingRow() != null) {
                  clickGuiScreen.getCategoryPanel().getColorSettingRow().closePicker();
               }

               clickGuiScreen.getCategoryPanel().setColorSettingRow(null);
               this.pickerOpen = false;
               return true;
            }

            if (f >= f5 + 75.0F && f2 >= f4 + this.gradientHeight + 3.0F && f <= f5 + 95.0F && f2 <= f4 + this.gradientHeight + 12.0F) {
               GuiScreen.setClipboardString(this.hexText);
               return false;
            }

            if (f >= f5 + 96.5F && f2 >= f4 + this.gradientHeight + 3.0F && f <= f5 + 116.5F && f2 <= f4 + this.gradientHeight + 12.0F) {
               String clipboardString = GuiScreen.getClipboardString();
               if (clipboardString.startsWith("#") && (clipboardString.length() == 7 || clipboardString.length() == 9)) {
                  boolean flag = clipboardString.length() == 9;
                  float f6 = flag ? Integer.parseInt(clipboardString.substring(1, 3), 16) / 255.0F : 1.0F;
                  int i2 = RenderUtils.withAlphaFraction(Color.decode(flag ? "#" + clipboardString.substring(3) : clipboardString).getRGB(), f6);
                  this.hexText = clipboardString;
                  this.colorSetting.setColor(i2);
                  shadeColorCache.clear();
                  this.loadHsbFromColor();
                  this.applyColor();
               }

               return false;
            }

            this.draggingAlpha = f >= f3 + 6.0F && f <= f3 + 9.0F && f2 >= f4 && f2 <= f4 + this.gradientHeight;
            this.draggingHue = f >= f3 - 1.0F && f <= f3 + 4.0F && f2 >= f4 && f2 <= f4 + this.gradientHeight;
            this.draggingSaturationBrightness = f >= f3 - 100.0F - 3.0F && f <= f3 - 3.0F && f2 >= f4 && f2 <= f4 + this.gradientHeight;
            return false;
         }
      } else if (i == 0) {
         if (!this.pickerOpen) {
            this.pickerOpen = true;
            this.pickerX = f3 + this.width - 25.5F - clickGuiScreen.getX();
            this.pickerY = f4 + ModulePanel.getRowHeight() + 5.0F - clickGuiScreen.getY();
            if (clickGuiScreen.getY() + this.pickerY + getPickerBoxHeight() > clickGuiScreen.getY() + ClickGuiScreen.getHeight()) {
               this.pickerY = this.pickerY
                  - (clickGuiScreen.getY() + this.pickerY + getPickerBoxHeight() - (clickGuiScreen.getY() + ClickGuiScreen.getHeight()) + 5.0F);
            }

            shadeColorCache.clear();
            if (colorSettingRow != null && colorSettingRow != this) {
               colorSettingRow.pickerOpen = false;
            }

            categoryPanel.setColorSettingRow(this);
         } else {
            this.pickerOpen = false;
            if (categoryPanel.getColorSettingRow() != null) {
               categoryPanel.getColorSettingRow().closePicker();
            }

            categoryPanel.setColorSettingRow(null);
         }

         return false;
      }

      return true;
   }

   public void drawColorPicker(float f, float f2, float f3) {
      float f4 = clickGuiScreen.getX() + this.pickerX;
      float f5 = clickGuiScreen.getY() + this.pickerY;
      float f6 = f4 - 100.0F - 7.0F;
      RenderUtils.drawShadow(f6, f5 - 5.0F, f4 + 13.0F, f5 + this.gradientHeight + 15.0F, 5.0F, -16509915);
      RenderUtils.drawRect(f6, f5 - 5.0F, f4 + 13.0F, f5 + this.gradientHeight + 15.0F, -16576486);
      Fonts.museoSans700Size14.drawStringWithShadow(this.hexText, f6 + 5.0F, f5 + this.gradientHeight + 6.0F, -6509895);
      RenderUtils.drawBorderedRect(
         f6 + 75.0F, f5 + this.gradientHeight + 3.0F, f6 + 95.0F, f5 + this.gradientHeight + 12.0F, 0.5F, -16314860, -15787215
      );
      Fonts.museoSans500Size11.drawStringWithShadow("Copy", f6 + 78.0F, f5 + this.gradientHeight + 6.5F, -6509895);
      RenderUtils.drawBorderedRect(
         f6 + 96.5F, f5 + this.gradientHeight + 3.0F, f6 + 116.5F, f5 + this.gradientHeight + 12.0F, 0.5F, -16314860, -15787215
      );
      Fonts.museoSans500Size11.drawStringWithShadow("Paste", f6 + 99.5F, f5 + this.gradientHeight + 6.5F, -6509895);
      if (this.draggingSaturationBrightness) {
         this.saturation = this.getHorizontalFraction(f);
         this.brightness = this.getVerticalFraction(f2);
         this.colorSetting.setColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
         this.applyColor();
      } else if (this.draggingHue) {
         this.hue = this.getVerticalFraction(f2);
         this.colorSetting.setColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
         shadeColorCache.clear();
         this.applyColor();
      } else if (this.draggingAlpha) {
         this.alpha = this.getVerticalFraction(f2);
         this.colorSetting.setColor(RenderUtils.withAlphaFraction(this.colorSetting.getColor(), this.alpha));
         this.applyColor();
      }

      OutlineRectRenderer outlineRectRenderer = new OutlineRectRenderer(1.0F);
      float f7 = 5.0F;

      for (int i = 360; i >= 0; i--) {
         int ifAbsent = hueColorCache.computeIfAbsent(i, integer -> Color.getHSBColor(integer.intValue() / 360.0F, 1.0F, 1.0F).getRGB());
         outlineRectRenderer.addRect(f4, f5 + (360 - i) / f7, 3.0F, 1.0F, ifAbsent);
         if (i == 0) {
            this.gradientHeight = f5 + (360 - i) / 5.0F;
         }
      }

      outlineRectRenderer.draw();
      this.gradientHeight = this.gradientHeight - f5 + 1.0F;
      float f8 = f5 + (1.0F - this.hue) * this.gradientHeight;
      RenderUtils.drawLine(f4 - 1.0F, f8, f4 + 4.0F, f8, -1, false);
      outlineRectRenderer = new OutlineRectRenderer(1.0F);

      for (int i2 = 100; i2 >= 0; i2--) {
         for (int i3 = 0; i3 < this.gradientHeight; i3++) {
            float f9 = i2 == 0 ? 0.0F : i2 / 100.0F;
            float f10 = i3 == 0 ? 0.0F : i3 / this.gradientHeight;
            int i4 = i2 | i3 << 16;
            int ifAbsent2 = shadeColorCache.computeIfAbsent(i4, integer -> Color.getHSBColor(this.hue, f9, f10).getRGB());
            outlineRectRenderer.addRect(f4 - 3.0F - 100.0F + i2, f5 - i3 + this.gradientHeight - 1.0F, 1.0F, 1.0F, ifAbsent2);
         }
      }

      outlineRectRenderer.draw();
      f7 = f4 - 100.0F + 100.0F * this.saturation;
      f8 = f5 + (1.0F - this.brightness) * this.gradientHeight;
      RenderUtils.drawBorderedRect(f7 - 4.0F, f8 - 1.0F, f7 - 2.0F, f8 + 1.0F, 0.5F, 0, -1);
      outlineRectRenderer = new OutlineRectRenderer(1.0F);
      outlineRectRenderer.addRect(f4 + 6.0F, f5, 3.0F, this.gradientHeight, -1);

      for (int i5 = 0; i5 < this.gradientHeight; i5 += 2) {
         outlineRectRenderer.addRect(f4 + 6.0F, f5 + i5, 1.0F, 1.0F, -4210753);
         outlineRectRenderer.addRect(f4 + 8.0F, f5 + i5, 1.0F, 1.0F, -4210753);
      }

      for (int i6 = 1; i6 < this.gradientHeight; i6 += 2) {
         outlineRectRenderer.addRect(f4 + 7.0F, f5 + i6, 1.0F, 1.0F, -4210753);
      }

      outlineRectRenderer.draw();
      RenderUtils.drawVerticalGradientRect(
         f4 + 6.0F, f5, f4 + 9.0F, f5 + this.gradientHeight, RenderUtils.withAlphaFraction(this.colorSetting.getColor(), 1.0F), 0
      );
      f7 = f5 + (1.0F - this.alpha) * this.gradientHeight;
      RenderUtils.drawLine(f4 + 5.0F, f7, f4 + 10.0F, f7, -1, false);
      if (!Mouse.isButtonDown(0)) {
         this.draggingHue = this.draggingSaturationBrightness = this.draggingAlpha = false;
      }
   }

   public boolean isPickerOpen() {
      return this.pickerOpen;
   }

   public void closePicker() {
      this.pickerOpen = false;
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
   }

   public boolean isInsidePicker(float f, float f2) {
      float f3 = clickGuiScreen.getX() + this.pickerX;
      float f4 = clickGuiScreen.getY() + this.pickerY;
      float f5 = f3 - 100.0F - 7.0F;
      return f >= f5 && f2 >= f4 - 5.0F && f <= f3 + 13.0F && f2 <= f4 + this.gradientHeight + 15.0F;
   }

   private void loadHsbFromColor() {
      float[] fArray = RenderUtils.unpackColor255(this.colorSetting.getColor());
      float[] fArray2 = new float[3];
      Color.RGBtoHSB((int)fArray[0], (int)fArray[1], (int)fArray[2], fArray2);
      this.hue = fArray2[0];
      this.saturation = fArray2[1];
      this.brightness = fArray2[2];
      this.alpha = fArray[3] / 255.0F;
   }

   public ColorSettingRow(ModuleSetting moduleSetting2, float f) {
      super(moduleSetting2, f);
      this.colorSetting = (ColorSetting)moduleSetting2;
      this.loadHsbFromColor();
      this.updateHexText();
      shadeColorCache.clear();
   }

   private void applyColor() {
      this.colorSetting.interactCallback();
      this.updateHexText();
   }

   private void updateHexText() {
      float[] fArray = RenderUtils.unpackColor255(this.colorSetting.getColor());
      this.hexText = String.format("#%02x%02x%02x%02x", (int)fArray[3], (int)fArray[0], (int)fArray[1], (int)fArray[2]);
   }

   public static int getPickerBoxHeight() {
      return 88;
   }

   private float getHorizontalFraction(float f) {
      return MathHelper.clamp_float((f - clickGuiScreen.getX() - this.pickerX + 3.0F + 100.0F) / 100.0F, 0.0F, 1.0F);
   }

   @Override
   public void draw(float f, float f2, float f3) {
      super.draw(f, f2, f3);
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      Fonts.museoSans700Size14
         .drawString(
            this.moduleSetting.getName(),
            f4 + 5.0F,
            f5 + ModulePanel.getRowHeight() / 2.0F - Fonts.museoSans700Size14.getHeight() / 2.0F + 1.0F,
            -6509895
         );
      RenderUtils.drawCircle(f4 + this.width - 8.0F, f5 + ModulePanel.getRowHeight() - 8.0F, 3.0F, 1.0F, this.colorSetting.getColor());
      RenderUtils.drawTextureWithAlpha(f4 + this.width - 23.0F, f5 + ModulePanel.getRowHeight() - 12.0F, 8.0F, 8.0F, 1.0F, PALETTE_ICON);
   }

   @Override
   public void resetAnimation() {
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      this.draggingHue = this.draggingSaturationBrightness = this.draggingAlpha = false;
      clickGuiScreen.getCategoryPanel().refreshLayout();
   }
}
