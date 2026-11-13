package wtf.novoline.gui.menu;

import wtf.novoline.enums.WidgetType;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shader.FloatSupplier;

public class MenuMultiComboWidget extends MenuSettingWidget {
   private static final float OPTION_HEIGHT = 10.0F;
   private boolean open;

   @Override
   public void render(float f, float f2, float f3) {
      MultiComboSetting multiComboSetting = (MultiComboSetting)this.moduleSetting;
      int secondaryColor = this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(false);
      if (this.open) {
         float f4 = this.y + 16.5F;
         Option[] options = multiComboSetting.getElements().toArray(new Option[0]);
         RenderUtils.drawRect(
            this.x + 4.0F + 2.0F, f4, this.x + this.getWidth() - 4.0F - 2.0F, f4 + 10.0F * options.length, RenderUtils.withAlpha(secondaryColor, 68)
         );
         f4 += 10.0F * options.length;
         RenderUtils.drawVerticalGradientRect(this.x + 6.0F, f4 - 3.0F, this.x + this.getWidth() - 6.0F, f4, 3092790, 1610612736);
      }

      RenderUtils.drawVerticalGradientRect(this.x + 4.0F, this.y + 16.5F, this.x + this.getWidth() - 4.0F, this.y + 16.5F + 4.0F, 1610612736, 3092790);
      RenderUtils.drawVerticalGradientRect(this.x + 4.0F, this.y + 3.5F - 6.0F, this.x + this.getWidth() - 4.0F, this.y + 3.5F, 3092790, 721420288);
      RenderUtils.drawRect(this.x + 4.0F, this.y + 3.5F, this.x + 6.0F, this.y + 16.5F, secondaryColor);
      RenderUtils.drawRect(this.x + this.getWidth() - 6.0F, this.y + 3.5F, this.x + this.getWidth() - 4.0F, this.y + 16.5F, secondaryColor);
   }

   @Override
   public float getHeight() {
      int size2 = ((MultiComboSetting)this.moduleSetting).getElements().size();
      return 20.0F + (this.open ? size2 * 10.0F : 0.0F);
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (f > this.x + 4.5F && f < this.x + this.getWidth() - 4.5F && f2 > this.y + 3.5F && f2 < this.y + 16.5F) {
         if (i == 0) {
            this.open = !this.open;
         }
      } else if (this.open && i == 0) {
         MultiComboSetting multiComboSetting = (MultiComboSetting)this.moduleSetting;
         float f3 = this.y + 16.5F;

         for (int i2 = 0; i2 < multiComboSetting.getElements().size(); i2++) {
            if (f > this.x + 4.5F && f < this.x + this.getWidth() - 4.5F && f2 > f3 && f2 < f3 + 10.0F) {
               multiComboSetting.getElements().get(i2).toggleSelection();
               break;
            }

            f3 += 10.0F;
         }
      }
   }

   @Override
   public void reset() {
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
   }

   @Override
   public boolean keyTyped(char c, int i) {
      return true;
   }

   public MenuMultiComboWidget(MenuClickGuiScreen menuClickGuiScreen2, ModuleSetting moduleSetting2) {
      super(menuClickGuiScreen2, moduleSetting2);
   }

   @Override
   public void tick() {
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      if (widgetType == WidgetType.REGULAR) {
         int textColor = this.menuClickGuiScreen.getClickGuiTheme().getTextColor();
         int secondaryColor = this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(true);
         MultiComboSetting multiComboSetting = (MultiComboSetting)this.moduleSetting;
         if (!flag) {
            floatSupplier.drawStringTopCentered(this.moduleSetting.getName(), this.x + this.getWidth() / 2.0F, this.y + 9.0F, textColor);
         }

         if (this.open) {
            float f2 = this.y + 16.5F;

            for (Option option : multiComboSetting.getElements().toArray(new Option[0])) {
               String name = option.getName();
               if (option.isSelected() == flag) {
                  float f3 = this.x + this.getWidth() / 2.0F;
                  float f4 = f2 + 5.0F + 1.0F;
                  int i = option.isSelected() ? secondaryColor : textColor;
                  float f5 = f4;
                  float f6 = f3;
                  floatSupplier.drawStringCentered(name, f6, f5, i);
               }

               f2 += 10.0F;
            }
         }
      }
   }
}
