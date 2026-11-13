package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;

public class RenderXPOrb extends Render<EntityXPOrb> {
   private static final ResourceLocation experienceOrbTextures = new ResourceLocation("textures/entity/experience_orb.png");

   public void doRender2(EntityXPOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      this.bindEntityTexture(entity);
      int textureByXP = entity.getTextureByXP();
      float f = (textureByXP % 4 * 16 + 0) / 64.0F;
      float f2 = (textureByXP % 4 * 16 + 16) / 64.0F;
      float f3 = (textureByXP / 4 * 16 + 0) / 64.0F;
      float f4 = (textureByXP / 4 * 16 + 16) / 64.0F;
      float f5 = 1.0F;
      float f6 = 0.5F;
      float f7 = 0.25F;
      int brightnessForRender = entity.getBrightnessForRender(partialTicks);
      int i = brightnessForRender % 65536;
      int i2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i / 1.0F, i2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f8 = (entity.xpColor + partialTicks) / 2.0F;
      if (Config.isCustomColors()) {
         f8 = CustomColors.getXpOrbTimer(f8);
      }

      i2 = (int)((MathHelper.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int i3 = (int)((MathHelper.sin(f8 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.3F, 0.3F, 0.3F);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      int i4 = i2;
      int i5 = 255;
      int i6 = i3;
      if (Config.isCustomColors()) {
         int xpOrbColor = CustomColors.getXpOrbColor(f8);
         if (xpOrbColor >= 0) {
            i4 = xpOrbColor >> 16 & 0xFF;
            i5 = xpOrbColor >> 8 & 0xFF;
            i6 = xpOrbColor >> 0 & 0xFF;
         }
      }

      worldRenderer.pos(0.0F - f6, 0.0F - f7, 0.0).tex(f, f4).color(i4, i5, i6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(f5 - f6, 0.0F - f7, 0.0).tex(f2, f4).color(i4, i5, i6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(f5 - f6, 1.0F - f7, 0.0).tex(f2, f3).color(i4, i5, i6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.0F - f6, 1.0F - f7, 0.0).tex(f, f3).color(i4, i5, i6, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
      instance.draw();
      GlStateManager.disableBlend();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityXPOrb entity) {
      return experienceOrbTextures;
   }

   public RenderXPOrb(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }
}
