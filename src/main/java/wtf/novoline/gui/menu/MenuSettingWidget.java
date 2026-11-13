package wtf.novoline.gui.menu;

import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.utils.render.shape.IRectRenderer;

public abstract class MenuSettingWidget implements MenuWidget {
   protected final MenuClickGuiScreen menuClickGuiScreen;
   protected float y;
   protected float x;
   protected final ModuleSetting moduleSetting;

   public MenuSettingWidget(MenuClickGuiScreen menuClickGuiScreen2, ModuleSetting moduleSetting2) {
      this.menuClickGuiScreen = menuClickGuiScreen2;
      this.moduleSetting = moduleSetting2;
   }

   public float getX() {
      return this.x;
   }

   public float getTotalHeight(float f, float f2, float f3) {
      return this.getHeight();
   }

   public void setY(float f) {
      this.y = f;
   }

   public float getWidth() {
      return MenuCategoryPanel.getWidth();
   }

   public void addRects(IRectRenderer iRectRenderer, float f) {
      iRectRenderer.addRect(this.x, this.y, this.getWidth(), this.getHeight(), this.menuClickGuiScreen.getClickGuiTheme().getSettingBackgroundColor());
   }

   public abstract float getHeight();

   public ModuleSetting getModuleSetting() {
      return this.moduleSetting;
   }

   public void setX(float f) {
      this.x = f;
   }

   public float getY() {
      return this.y;
   }
}
