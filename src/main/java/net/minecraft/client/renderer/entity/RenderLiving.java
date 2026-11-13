package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;

public abstract class RenderLiving<T extends EntityLiving> extends RendererLivingEntity<T> {
   public void doRender2(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
      this.renderLeash(entity, x, y, z, entityYaw, partialTicks);
   }

   private double interpolateValue(double start, double end, double pct) {
      return start + (end - start) * pct;
   }

   protected void renderLeash(T entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!Config.isShaders() || !Shaders.isShadowPass) {
         Entity leashedToEntity = entityLivingIn.getLeashedToEntity();
         if (leashedToEntity != null) {
            y -= (1.6 - entityLivingIn.height) * 0.5;
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            double d = this.interpolateValue(leashedToEntity.prevRotationYaw, leashedToEntity.rotationYaw, partialTicks * 0.5F) * (float) (Math.PI / 180.0);
            double d2 = this.interpolateValue(leashedToEntity.prevRotationPitch, leashedToEntity.rotationPitch, partialTicks * 0.5F) * (float) (Math.PI / 180.0);
            double d3 = Math.cos(d);
            double d4 = Math.sin(d);
            double d5 = Math.sin(d2);
            if (leashedToEntity instanceof EntityHanging) {
               d3 = 0.0;
               d4 = 0.0;
               d5 = -1.0;
            }

            double d6 = Math.cos(d2);
            double d7 = this.interpolateValue(leashedToEntity.prevPosX, leashedToEntity.posX, partialTicks) - d3 * 0.7 - d4 * 0.5 * d6;
            double d8 = this.interpolateValue(leashedToEntity.prevPosY + leashedToEntity.getEyeHeight() * 0.7, leashedToEntity.posY + leashedToEntity.getEyeHeight() * 0.7, partialTicks)
               - d5 * 0.5
               - 0.25;
            double d9 = this.interpolateValue(leashedToEntity.prevPosZ, leashedToEntity.posZ, partialTicks) - d4 * 0.7 + d3 * 0.5 * d6;
            double d10 = this.interpolateValue(entityLivingIn.prevRenderYawOffset, entityLivingIn.renderYawOffset, partialTicks) * (float) (Math.PI / 180.0)
               + (Math.PI / 2);
            d3 = Math.cos(d10) * entityLivingIn.width * 0.4;
            d4 = Math.sin(d10) * entityLivingIn.width * 0.4;
            double d11 = this.interpolateValue(entityLivingIn.prevPosX, entityLivingIn.posX, partialTicks) + d3;
            double d12 = this.interpolateValue(entityLivingIn.prevPosY, entityLivingIn.posY, partialTicks);
            double d13 = this.interpolateValue(entityLivingIn.prevPosZ, entityLivingIn.posZ, partialTicks) + d4;
            x += d3;
            z += d4;
            double d14 = (float)(d7 - d11);
            double d15 = (float)(d8 - d12);
            double d16 = (float)(d9 - d13);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            if (Config.isShaders()) {
               Shaders.beginLeash();
            }

            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= 24; i++) {
               float f = 0.5F;
               float f2 = 0.4F;
               float f3 = 0.3F;
               if (i % 2 == 0) {
                  f *= 0.7F;
                  f2 *= 0.7F;
                  f3 *= 0.7F;
               }

               float f4 = i / 24.0F;
               worldRenderer.pos(x + d14 * f4 + 0.0, y + d15 * (f4 * f4 + f4) * 0.5 + ((24.0F - i) / 18.0F + 0.125F), z + d16 * f4)
                  .color(f, f2, f3, 1.0F)
                  .endVertex();
               worldRenderer.pos(x + d14 * f4 + 0.025, y + d15 * (f4 * f4 + f4) * 0.5 + ((24.0F - i) / 18.0F + 0.125F) + 0.025, z + d16 * f4)
                  .color(f, f2, f3, 1.0F)
                  .endVertex();
            }

            instance.draw();
            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int i2 = 0; i2 <= 24; i2++) {
               float f5 = 0.5F;
               float f6 = 0.4F;
               float f7 = 0.3F;
               if (i2 % 2 == 0) {
                  f5 *= 0.7F;
                  f6 *= 0.7F;
                  f7 *= 0.7F;
               }

               float f8 = i2 / 24.0F;
               worldRenderer.pos(x + d14 * f8 + 0.0, y + d15 * (f8 * f8 + f8) * 0.5 + ((24.0F - i2) / 18.0F + 0.125F) + 0.025, z + d16 * f8)
                  .color(f5, f6, f7, 1.0F)
                  .endVertex();
               worldRenderer.pos(x + d14 * f8 + 0.025, y + d15 * (f8 * f8 + f8) * 0.5 + ((24.0F - i2) / 18.0F + 0.125F), z + d16 * f8 + 0.025)
                  .color(f5, f6, f7, 1.0F)
                  .endVertex();
            }

            instance.draw();
            if (Config.isShaders()) {
               Shaders.endLeash();
            }

            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
         }
      }
   }

   public void setLightmap(T entityLivingIn, float partialTicks) {
      int brightnessForRender = entityLivingIn.getBrightnessForRender(partialTicks);
      int i = brightnessForRender % 65536;
      int i2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i / 1.0F, i2 / 1.0F);
   }

   public RenderLiving(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
      super(rendermanagerIn, modelbaseIn, shadowsizeIn);
   }

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      } else if (livingEntity.getLeashed() && livingEntity.getLeashedToEntity() != null) {
         Entity entity = livingEntity.getLeashedToEntity();
         return camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox());
      } else {
         return false;
      }
   }

   protected boolean canRenderName(T entity) {
      return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
   }
}
