package wtf.novoline.gui.menu;

import wtf.novoline.enums.WidgetType;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shader.FloatSupplier;

public class MenuCheckboxWidget extends MenuSettingWidget {
   private int trackColor = -12039343;
   private int knobColor = -8486780;
   private float knobOffset = 6.0F;

   @Override
   public void tick() {
   }

   @Override
   public float getHeight() {
      return 15.0F;
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
   }

   private void drawSwitch() {
      float f = this.y + this.getHeight() / 2.0F;
      float f2 = this.x + this.getWidth() - 8.0F - this.knobOffset;
      RenderUtils.drawRect(this.x + this.getWidth() - 8.0F - 6.0F - 4.0F, f - 3.0F, f2 - 3.0F, f + 3.0F, this.trackColor);
      RenderUtils.drawRect(f2 + 3.0F, f - 3.0F, this.x + this.getWidth() - 8.0F + 4.0F, f + 3.0F, this.trackColor);
      float f3 = (3.0F - Math.abs(this.knobOffset - 3.0F)) * 0.5F;
      RenderUtils.drawRect(f2 - 3.0F, f - 5.0F - f3, f2 + 3.0F, f + 5.0F + f3, this.knobColor);
   }

   @Override
   public boolean keyTyped(char c, int i) {
      return true;
   }

   @Override
   public void render(float f, float f2, float f3) {
      CheckboxSetting checkboxSetting = (CheckboxSetting)this.moduleSetting;
      this.trackColor = RenderUtils.smoothBlendColors(this.trackColor, this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(checkboxSetting.isEnabled()), 10.0F);
      this.knobColor = RenderUtils.smoothBlendColors(this.knobColor, this.menuClickGuiScreen.getClickGuiTheme().getPrimaryColor(checkboxSetting.isEnabled()), 10.0F);
      this.knobOffset = SmoothingUtils.approach(this.knobOffset, checkboxSetting.isEnabled() ? 0.0F : 6.0F, 10.0F);
      if (this.knobOffset > 3.0) {
         this.drawSwitch();
      } else if (MenuClickGuiScreen.shadersEnabled && ModuleManager.clickGui.glowSettings.isSelected(1)) {
         this.menuClickGuiScreen.bloomRenderer.submit(this::drawSwitch);
      } else {
         this.drawSwitch();
      }
   }

   @Override
   public void reset() {
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      if (!flag && widgetType == WidgetType.REGULAR) {
         floatSupplier.drawStringLeftMiddle(
            this.moduleSetting.getName(), this.x + 4.0F, this.y + this.getHeight() / 2.0F + 1.0F, this.menuClickGuiScreen.getClickGuiTheme().getTextColor()
         );
      }
   }

   public MenuCheckboxWidget(MenuClickGuiScreen menuClickGuiScreen2, ModuleSetting moduleSetting2) {
      super(menuClickGuiScreen2, moduleSetting2);
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (f > this.x && f < this.x + this.getWidth() && f2 > this.y && f2 < this.y + this.getHeight() && i == 0) {
         ((CheckboxSetting)this.moduleSetting).toggle();
      }
   }
}
