package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow extends Render<EntityArrow> {
   private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

   protected ResourceLocation getEntityTexture(EntityArrow entity) {
      return arrowTextures;
   }

   public void doRender2(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.bindEntityTexture(entity);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      float f = 0.0F;
      float f2 = 0.5F;
      float f3 = 0 / 32.0F;
      float f4 = 5 / 32.0F;
      float f5 = 0.0F;
      float f6 = 0.15625F;
      float f7 = 5 / 32.0F;
      float f8 = 10 / 32.0F;
      float f9 = 0.05625F;
      GlStateManager.enableRescaleNormal();
      float f10 = entity.arrowShake - partialTicks;
      if (f10 > 0.0F) {
         float f11 = -MathHelper.sin(f10 * 3.0F) * f10;
         GlStateManager.rotate(f11, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(f9, f9, f9);
      GlStateManager.translate(-4.0F, 0.0F, 0.0F);
      GL11.glNormal3f(f9, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-7.0, -2.0, -2.0).tex(f5, f7).endVertex();
      worldRenderer.pos(-7.0, -2.0, 2.0).tex(f6, f7).endVertex();
      worldRenderer.pos(-7.0, 2.0, 2.0).tex(f6, f8).endVertex();
      worldRenderer.pos(-7.0, 2.0, -2.0).tex(f5, f8).endVertex();
      instance.draw();
      GL11.glNormal3f(-f9, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-7.0, 2.0, -2.0).tex(f5, f7).endVertex();
      worldRenderer.pos(-7.0, 2.0, 2.0).tex(f6, f7).endVertex();
      worldRenderer.pos(-7.0, -2.0, 2.0).tex(f6, f8).endVertex();
      worldRenderer.pos(-7.0, -2.0, -2.0).tex(f5, f8).endVertex();
      instance.draw();

      for (int i = 0; i < 4; i++) {
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, f9);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(-8.0, -2.0, 0.0).tex(f, f3).endVertex();
         worldRenderer.pos(8.0, -2.0, 0.0).tex(f2, f3).endVertex();
         worldRenderer.pos(8.0, 2.0, 0.0).tex(f2, f4).endVertex();
         worldRenderer.pos(-8.0, 2.0, 0.0).tex(f, f4).endVertex();
         instance.draw();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
   }

   public RenderArrow(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }
}
