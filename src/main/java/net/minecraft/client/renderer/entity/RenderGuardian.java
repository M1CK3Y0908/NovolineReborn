package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderGuardian extends RenderLiving<EntityGuardian> {
   private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
   int beamTextureVersion = ((ModelGuardian)this.mainModel).getModelVersion();
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");

   public boolean shouldRender(EntityGuardian livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      }

      if (livingEntity.hasTargetedEntity()) {
         EntityLivingBase entityLivingBase = livingEntity.getTargetedEntity();
         if (entityLivingBase != null) {
            Vec3 vec3 = this.getInterpolatedPosition(entityLivingBase, entityLivingBase.height * 0.5, 1.0F);
            Vec3 vec32 = this.getInterpolatedPosition(livingEntity, livingEntity.getEyeHeight(), 1.0F);
            if (camera.isBoundingBoxInFrustum(AxisAlignedBB.fromBounds(vec32.xCoord, vec32.yCoord, vec32.zCoord, vec3.xCoord, vec3.yCoord, vec3.zCoord))) {
               return true;
            }
         }
      }

      return false;
   }

   public void doRender2(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (this.beamTextureVersion != ((ModelGuardian)this.mainModel).getModelVersion()) {
         this.mainModel = new ModelGuardian();
         this.beamTextureVersion = ((ModelGuardian)this.mainModel).getModelVersion();
      }

      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
      EntityLivingBase targetedEntity = entity.getTargetedEntity();
      if (targetedEntity != null) {
         float attackAnimationScale = entity.getAttackAnimationScale(partialTicks);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         this.bindTexture(GUARDIAN_BEAM_TEXTURE);
         GL11.glTexParameterf(3553, 10242, 10497.0F);
         GL11.glTexParameterf(3553, 10243, 10497.0F);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         float f = 240.0F;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f);
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         float f2 = (float)entity.worldObj.getTotalWorldTime() + partialTicks;
         float f3 = f2 * 0.5F % 1.0F;
         float eyeHeight = entity.getEyeHeight();
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)x, (float)y + eyeHeight, (float)z);
         Vec3 interpolatedPosition = this.getInterpolatedPosition(targetedEntity, targetedEntity.height * 0.5, partialTicks);
         Vec3 interpolatedPosition2 = this.getInterpolatedPosition(entity, eyeHeight, partialTicks);
         Vec3 vec33 = interpolatedPosition.subtract(interpolatedPosition2);
         double d = vec33.lengthVector() + 1.0;
         vec33 = vec33.normalize();
         float f4 = (float)Math.acos(vec33.yCoord);
         float f5 = (float)Math.atan2(vec33.zCoord, vec33.xCoord);
         GlStateManager.rotate(((float) (Math.PI / 2) + -f5) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(f4 * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         double d2 = f2 * 0.05 * (1.0 - 1 * 2.5);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         float f6 = attackAnimationScale * attackAnimationScale;
         int i = 64 + (int)(f6 * 240.0F);
         int i2 = 32 + (int)(f6 * 192.0F);
         int i3 = 128 - (int)(f6 * 64.0F);
         double d3 = 1 * 0.2;
         double d4 = d3 * 1.41;
         double d5 = 0.0 + Math.cos(d2 + (Math.PI * 3.0 / 4.0)) * d4;
         double d6 = 0.0 + Math.sin(d2 + (Math.PI * 3.0 / 4.0)) * d4;
         double d7 = 0.0 + Math.cos(d2 + (Math.PI / 4)) * d4;
         double d8 = 0.0 + Math.sin(d2 + (Math.PI / 4)) * d4;
         double d9 = 0.0 + Math.cos(d2 + (Math.PI * 5.0 / 4.0)) * d4;
         double d10 = 0.0 + Math.sin(d2 + (Math.PI * 5.0 / 4.0)) * d4;
         double d11 = 0.0 + Math.cos(d2 + (Math.PI * 7.0 / 4.0)) * d4;
         double d12 = 0.0 + Math.sin(d2 + (Math.PI * 7.0 / 4.0)) * d4;
         double d13 = 0.0 + Math.cos(d2 + Math.PI) * d3;
         double d14 = 0.0 + Math.sin(d2 + Math.PI) * d3;
         double d15 = 0.0 + Math.cos(d2 + 0.0) * d3;
         double d16 = 0.0 + Math.sin(d2 + 0.0) * d3;
         double d17 = 0.0 + Math.cos(d2 + (Math.PI / 2)) * d3;
         double d18 = 0.0 + Math.sin(d2 + (Math.PI / 2)) * d3;
         double d19 = 0.0 + Math.cos(d2 + (Math.PI * 3.0 / 2.0)) * d3;
         double d20 = 0.0 + Math.sin(d2 + (Math.PI * 3.0 / 2.0)) * d3;
         double d21 = -1.0F + f3;
         double d22 = d * (0.5 / d3) + d21;
         worldRenderer.pos(d13, d, d14).tex(0.4999, d22).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d13, 0.0, d14).tex(0.4999, d21).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d15, 0.0, d16).tex(0.0, d21).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d15, d, d16).tex(0.0, d22).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d17, d, d18).tex(0.4999, d22).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d17, 0.0, d18).tex(0.4999, d21).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d19, 0.0, d20).tex(0.0, d21).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d19, d, d20).tex(0.0, d22).color(i, i2, i3, 255).endVertex();
         double d23 = 0.0;
         if (entity.ticksExisted % 2 == 0) {
            d23 = 0.5;
         }

         worldRenderer.pos(d5, d, d6).tex(0.5, d23 + 0.5).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d7, d, d8).tex(1.0, d23 + 0.5).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d11, d, d12).tex(1.0, d23).color(i, i2, i3, 255).endVertex();
         worldRenderer.pos(d9, d, d10).tex(0.5, d23).color(i, i2, i3, 255).endVertex();
         instance.draw();
         GlStateManager.popMatrix();
      }
   }

   protected void preRenderCallback(EntityGuardian entitylivingbaseIn, float partialTickTime) {
      if (entitylivingbaseIn.isElder()) {
         GlStateManager.scale(2.35F, 2.35F, 2.35F);
      }
   }

   public RenderGuardian(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelGuardian(), 0.5F);
   }

   private Vec3 getInterpolatedPosition(EntityLivingBase entityLivingBaseIn, double heightOffset, float partialTicks) {
      double xCoordinate = entityLivingBaseIn.lastTickPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.lastTickPosX) * partialTicks;
      double yCoordinate = heightOffset + entityLivingBaseIn.lastTickPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.lastTickPosY) * partialTicks;
      double zCoordinate = entityLivingBaseIn.lastTickPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.lastTickPosZ) * partialTicks;
      return new Vec3(xCoordinate, yCoordinate, zCoordinate);
   }

   protected ResourceLocation getEntityTexture(EntityGuardian entity) {
      return entity.isElder() ? GUARDIAN_ELDER_TEXTURE : GUARDIAN_TEXTURE;
   }
}
