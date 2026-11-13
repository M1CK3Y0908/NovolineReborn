package wtf.novoline.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.render.RenderUtils;

public class TargetESP extends Module {
   private final ComboSetting targetESP = settingManager.register(new ComboSetting(this, "Mode", 0, "Circle"));
   private final NumberSetting cycleTime = settingManager.register(new NumberSetting(this, "Cycle time", 1.0F, 1.0F, 5.0F) {
      @Override
      public boolean shouldShow() {
         return TargetESP.this.targetESP.getCurrentElement() == 0;
      }
   });
   private final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -1426063361));
   @EventLink
   private final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      if (ModuleManager.killaura.isEnabled() && ModuleManager.killaura.entityLivingBase != null) {
         double d = 0.0;
         if (ModuleManager.customModel.isEnabled() && ModuleManager.customModel.appliesTo(ModuleManager.killaura.entityLivingBase)) {
            int currentElement = ModuleManager.customModel.customModel.getCurrentElement();
            if (currentElement == 0) {
               d = 0.4F;
               if (ModuleManager.killaura.entityLivingBase.isSneaking()) {
                  d = 0.15F;
               }
            }
         }

         EntityLivingBase entityLivingBase2 = ModuleManager.killaura.entityLivingBase;
         RenderManager renderManager = minecraft.getRenderManager();
         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.alphaFunc(516, 0.003921569F);
         GlStateManager.shadeModel(7425);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
         double d2 = entityLivingBase2.getEntityBoundingBox().maxY - entityLivingBase2.getEntityBoundingBox().minY + 0.2 - d;
         long l = (long)this.cycleTime.getCurrentValue() * 1000L;
         double d3 = entityLivingBase2.lastTickPosX + (entityLivingBase2.posX - entityLivingBase2.lastTickPosX) * renderWorldEvent.partialTicks - renderManager.viewerPosX;
         double d4 = InterpolationUtils.oscillate(0.2, d2, l)
            + (entityLivingBase2.lastTickPosY + (entityLivingBase2.posY - entityLivingBase2.lastTickPosY) * renderWorldEvent.partialTicks - renderManager.viewerPosY)
            - 0.1;
         double d5 = InterpolationUtils.oscillateWithOffset(0.2, d2, -0.7F, l)
            + (entityLivingBase2.lastTickPosY + (entityLivingBase2.posY - entityLivingBase2.lastTickPosY) * renderWorldEvent.partialTicks - renderManager.viewerPosY)
            - 0.1;
         double d6 = entityLivingBase2.lastTickPosZ + (entityLivingBase2.posZ - entityLivingBase2.lastTickPosZ) * renderWorldEvent.partialTicks - renderManager.viewerPosZ;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         double d7 = entityLivingBase2.getEntityBoundingBox().maxX - entityLivingBase2.getEntityBoundingBox().minX;
         int color2 = this.color.getColor();
         int i = RenderUtils.withAlpha(color2, 0);
         GL11.glBegin(3);
         RenderUtils.setColor(color2);

         for (int i2 = 0; i2 <= 360; i2++) {
            GL11.glVertex3d(d3 + Math.sin(i2 * Math.PI / 180.0) * d7, d4, d6 + Math.cos(i2 * Math.PI / 180.0) * d7);
         }

         GL11.glEnd();
         GL11.glBegin(7);

         for (int i3 = 0; i3 <= 60; i3++) {
            double d8 = i3 * 6;
            double d9 = d3 + Math.sin(Math.toRadians(d8)) * d7;
            double d10 = d6 + Math.cos(Math.toRadians(d8)) * d7;
            if (i3 > 0) {
               RenderUtils.setColor(color2);
               GL11.glVertex3d(d9, d4, d10);
               RenderUtils.setColor(i);
               GL11.glVertex3d(d9, d5, d10);
            }

            if (i3 < 60) {
               RenderUtils.setColor(i);
               GL11.glVertex3d(d9, d5, d10);
               RenderUtils.setColor(color2);
               GL11.glVertex3d(d9, d4, d10);
            }
         }

         GL11.glEnd();
         RenderUtils.resetColor();
         GlStateManager.shadeModel(7424);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.disableBlend();
         GlStateManager.enableTexture2D();
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
      }
   };

   public TargetESP() {
      super(Category.HUD, "TargetESP", "");
      this.setEnabled(true);
   }
}
