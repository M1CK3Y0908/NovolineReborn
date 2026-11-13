package wtf.novoline.gui.menu;

import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.render.RenderUtils;

public class MenuLayout {
   public float getPanelBlurSigma() {
      return 0.8F;
   }

   public float getBackgroundBlurSigma() {
      return 0.8F;
   }

   public float getBackgroundDimAmount() {
      return 0.3F;
   }

   public float getBloomSigma() {
      return 0.05F;
   }

   public float getPanelOpacity() {
      return 0.8F;
   }

   public float getBloomGamma() {
      return 1.0F;
   }

   public int getDesaturationColor() {
      return ModuleManager.clickGui.astolfoMode.isEnabled()
         ? RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, (long)ModuleManager.clickGui.astolfoCycleTime.getCurrentValue())
         : ModuleManager.clickGui.desaturation.getColor();
   }

   public int getPanelBlurRadius() {
      return 20;
   }

   public int getBloomRadius() {
      return 25;
   }

   public float getBloomExposure() {
      return 1.5F;
   }

   public float getPanelTextureAlpha() {
      return 0.08F;
   }

   public int getBackgroundBlurRadius() {
      return 10;
   }

   public float getDesaturationFactor() {
      return 0.8F;
   }
}
