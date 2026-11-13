package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFish extends Render<EntityFishHook> {
   private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");

   protected ResourceLocation getEntityTexture(EntityFishHook entity) {
      return FISH_PARTICLES;
   }

   public RenderFish(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender2(EntityFishHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      this.bindEntityTexture(entity);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      worldRenderer.pos(-0.5, -0.5, 0.0).tex(0.0625, 0.1875).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.5, -0.5, 0.0).tex(0.125, 0.1875).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.5, 0.5, 0.0).tex(0.125, 0.125).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(-0.5, 0.5, 0.0).tex(0.0625, 0.125).normal(0.0F, 1.0F, 0.0F).endVertex();
      instance.draw();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      if (entity.angler != null) {
         float swingProgress = entity.angler.getSwingProgress(partialTicks);
         float f = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
         Vec3 vec3 = new Vec3(-0.36, 0.03, 0.35);
         vec3 = vec3.rotatePitch(
            -(entity.angler.prevRotationPitch + (entity.angler.rotationPitch - entity.angler.prevRotationPitch) * partialTicks) * (float) Math.PI / 180.0F
         );
         vec3 = vec3.rotateYaw(
            -(entity.angler.prevRotationYaw + (entity.angler.rotationYaw - entity.angler.prevRotationYaw) * partialTicks) * (float) Math.PI / 180.0F
         );
         vec3 = vec3.rotateYaw(f * 0.5F);
         vec3 = vec3.rotatePitch(-f * 0.7F);
         double d = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * partialTicks + vec3.xCoord;
         double d2 = entity.angler.prevPosY + (entity.angler.posY - entity.angler.prevPosY) * partialTicks + vec3.yCoord;
         double d3 = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * partialTicks + vec3.zCoord;
         double eyeHeight = entity.angler.getEyeHeight();
         if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || entity.angler != Minecraft.getMinecraft().thePlayer) {
            float f2 = (entity.angler.prevRenderYawOffset + (entity.angler.renderYawOffset - entity.angler.prevRenderYawOffset) * partialTicks)
               * (float) Math.PI
               / 180.0F;
            double d4 = MathHelper.sin(f2);
            double d5 = MathHelper.cos(f2);
            d = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * partialTicks - d5 * 0.35 - d4 * 0.8;
            d2 = entity.angler.prevPosY + eyeHeight + (entity.angler.posY - entity.angler.prevPosY) * partialTicks - 0.45;
            d3 = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * partialTicks - d4 * 0.35 + d5 * 0.8;
            eyeHeight = entity.angler.isSneaking() ? -0.1875 : 0.0;
         }

         double d6 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
         double d7 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + 0.25;
         double d8 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
         double d9 = (float)(d - d6);
         double d10 = (float)(d2 - d7) + eyeHeight;
         double d11 = (float)(d3 - d8);
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);

         for (int i = 0; i <= 16; i++) {
            float f3 = i / 16.0F;
            worldRenderer.pos(x + d9 * f3, y + d10 * (f3 * f3 + f3) * 0.5 + 0.25, z + d11 * f3).color(0, 0, 0, 255).endVertex();
         }

         instance.draw();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         super.doRender2(entity, x, y, z, entityYaw, partialTicks);
      }
   }
}
