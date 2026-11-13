package wtf.novoline.gui.clickgui;

import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.utils.render.RenderUtils;

public abstract class SettingRow implements ClickGuiComponent {
   protected final ModuleSetting moduleSetting;
   protected float y;
   protected float width;
   protected float x;

   public void setX(float f) {
      this.x = f;
   }

   public SettingRow(ModuleSetting moduleSetting2, float f) {
      this.moduleSetting = moduleSetting2;
      this.width = f;
   }

   public float getWidth() {
      return this.width;
   }

   public void setWidth(float f) {
      this.width = f;
   }

   public ModuleSetting getModuleSetting() {
      return this.moduleSetting;
   }

   public void setY(float f) {
      this.y = f;
   }

   public void draw(float f, float f2, float f3) {
      float f4 = clickGuiScreen.getX() + this.x;
      float f5 = clickGuiScreen.getY() - clickGuiScreen.getScrollOffset() + this.y;
      RenderUtils.drawRect(f4, f5, f4 + this.width, f5 + ModulePanel.getRowHeight(), -16577254);
   }

   public float getY() {
      return this.y;
   }

   public abstract void resetAnimation();

   public float getX() {
      return this.x;
   }
}
