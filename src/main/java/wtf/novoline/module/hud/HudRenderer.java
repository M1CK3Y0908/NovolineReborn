package wtf.novoline.module.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.novoline.Novoline;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.utils.math.SmoothingUtils;

public class HudRenderer {
   private final HudManager hudManager;

   public void renderElements(ScaledResolution scaledResolution, float f) {
      this.renderElements(scaledResolution, f, false, 0.0F, 0.0F);
   }

   public void renderForEditor(float f, float f2, float f3) {
      this.renderElements(new ScaledResolution(Minecraft.getMinecraft()), f3, true, f, f2);
   }

   private void renderElements(ScaledResolution scaledResolution, float f, boolean flag, float f2, float f3) {
      SmoothingUtils.updateDelta();
      if (Minecraft.getMinecraft().thePlayer != null) {
         GlStateManager.pushMatrix();
         float scaleFactor = scaledResolution.getScaleFactor();
         GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);
         double d = scaledResolution.getScaledWidth_double();
         double d2 = scaledResolution.getScaledHeight_double();
         scaledResolution.handleDouble2(d / 2.0 * scaleFactor);
         scaledResolution.handleDouble(d2 / 2.0 * scaleFactor);
         Novoline.getNovoline().getBus().post(new RenderScreenEvent(scaledResolution, f));

         for (HudElement hudElement : this.hudManager.getElements()) {
            if (flag) {
               float f4 = f2 * scaleFactor / 2.0F;
               float f5 = f3 * scaleFactor / 2.0F;
               hudElement.updateDragging(scaledResolution, f4, f5);
            }

            if (hudElement.isVisibleInDebugScreen() || !Minecraft.getMinecraft().gameSettings.showDebugInfo) {
               hudElement.draw(scaledResolution, f, flag);
            }
         }

         GlStateManager.popMatrix();
         Novoline.getNovoline()
            .getMenuClickGuiScreen()
            .handleFramebuffer(Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().timer.renderPartialTicks);
         GlStateManager.pushMatrix();
         GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);
         Novoline.getNovoline().getNotificationManager().renderAll(scaledResolution);
         GlStateManager.popMatrix();
      }
   }

   public HudRenderer(HudManager hudManager2) {
      this.hudManager = hudManager2;
   }
}
