package wtf.novoline.gui.clickgui;

import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.render.RenderUtils;

public class ClickGuiTheme {
   public int getDimColor() {
      return 1426063360;
   }

   public int getSecondaryColor(boolean flag) {
      return flag
         ? (
            ModuleManager.clickGui.astolfoMode.isEnabled()
               ? RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, (long)ModuleManager.clickGui.astolfoCycleTime.getCurrentValue())
               : ModuleManager.clickGui.extra.getColor()
         )
         : -1438102703;
   }

   public int getSettingBackgroundColor() {
      return -15329511;
   }

   public int getModuleBackgroundColor() {
      return -14539994;
   }

   public int getPrimaryColor(boolean flag) {
      return flag
         ? (
            ModuleManager.clickGui.astolfoMode.isEnabled()
               ? RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, (long)ModuleManager.clickGui.astolfoCycleTime.getCurrentValue())
               : ModuleManager.clickGui.primary.getColor()
         )
         : -1434550140;
   }

   public int getTextColor() {
      return -2039584;
   }

   public int getCategoryBackgroundColor() {
      return -15592683;
   }
}
