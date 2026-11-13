package wtf.novoline.gui.menu;

import net.minecraft.util.MathHelper;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.WidgetType;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.NumberSetting.IntType;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shader.FloatSupplier;

public class MenuSliderWidget extends MenuSettingWidget {
   private float displayedValue;
   private float fillWidth;
   private float labelOffset;
   private float trackExpansion;
   private boolean dragging;

   @Override
   public void render(float f, float f2, float f3) {
      NumberSetting numberSetting = (NumberSetting)this.moduleSetting;
      float f4 = this.getWidth()
         - 8.0F
         + (numberSetting.getCurrentValue() - numberSetting.getMaxValue()) * ((this.getWidth() - 8.0F) / (numberSetting.getMaxValue() - numberSetting.getMinValue()));
      if (this.dragging) {
         float f5 = numberSetting.getMinValue() + (f - this.x - 4.0F) / (this.getWidth() - 8.0F) * (numberSetting.getMaxValue() - numberSetting.getMinValue());
         numberSetting.setCurrentValue(f5);
         f4 = MathHelper.clamp_float(f, this.x + 4.0F, this.x + this.getWidth() - 4.0F) - this.x - 4.0F;
      }

      this.fillWidth = SmoothingUtils.approachWithMinimumStep(this.fillWidth, f4, 6.0F, 0.01F);
      this.displayedValue = SmoothingUtils.approachWithMinimumStep(this.displayedValue, numberSetting.getCurrentValue(), 6.0F, 0.01F);
      this.trackExpansion = SmoothingUtils.approachWithMinimumStep(this.trackExpansion, this.dragging ? 1.0F : 0.0F, 1.0F, 0.015F);
      this.labelOffset = SmoothingUtils.approachWithMinimumStep(this.labelOffset, this.dragging ? 1.0F : 0.0F, 1.0F, 0.015F);
      float f6 = this.y + this.getHeight() / 2.0F;
      RenderUtils.drawRect(
         this.x + 4.0F - this.trackExpansion,
         f6 + 3.0F - this.trackExpansion,
         this.x + this.getWidth() - 4.0F + this.trackExpansion,
         f6 + 5.0F + this.trackExpansion,
         this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(false)
      );
      if (numberSetting.getCurrentValue() > numberSetting.getMinValue()) {
         boolean selected = ModuleManager.clickGui.glowSettings.isSelected(1);
         if (MenuClickGuiScreen.shadersEnabled && selected) {
            this.menuClickGuiScreen.bloomRenderer.submit(() -> this.drawFill(f6));
         } else {
            this.drawFill(f6);
         }
      }
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (f > this.x && f < this.x + this.getWidth() && f2 > this.y && f2 < this.y + this.getHeight() && i == 0) {
         this.dragging = true;
      }
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      if (!flag && widgetType == WidgetType.REGULAR) {
         IntType type = ((NumberSetting)this.moduleSetting).getType();
         floatSupplier.drawStringLeftMiddle(
            this.moduleSetting.getName(),
            this.x + 4.0F + this.labelOffset,
            this.y + this.getHeight() / 2.0F - 1.0F,
            this.menuClickGuiScreen.getClickGuiTheme().getTextColor()
         );
         String text;
         if (type.equals(IntType.DECIMAL)) {
            text = Math.round(this.displayedValue * 100.0F) / 5 * 5 / 100.0F + "";
         } else {
            text = (int)this.displayedValue + "";
         }

         floatSupplier.drawStringLeftMiddle(
            text,
            this.x + this.getWidth() - 4.0F - Fonts.rubikRegularSize14.getStringWidth(text) - this.labelOffset,
            this.y + this.getHeight() / 2.0F - 1.0F,
            this.menuClickGuiScreen.getClickGuiTheme().getTextColor()
         );
      }
   }

   private void drawFill(float f) {
      RenderUtils.drawRect(
         this.x + 4.0F, f + 3.0F, this.x + 4.0F + this.fillWidth, f + 5.0F, this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(true)
      );
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      this.dragging = false;
   }

   @Override
   public void tick() {
   }

   @Override
   public float getHeight() {
      return 17.0F;
   }

   @Override
   public boolean keyTyped(char c, int i) {
      return true;
   }

   public MenuSliderWidget(MenuClickGuiScreen menuClickGuiScreen2, ModuleSetting moduleSetting2) {
      super(menuClickGuiScreen2, moduleSetting2);
   }

   @Override
   public void reset() {
      this.dragging = false;
   }
}
