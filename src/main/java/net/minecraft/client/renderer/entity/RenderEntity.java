package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEntity extends Render<Entity> {
   @Override
   protected ResourceLocation getEntityTexture(Entity entity) {
      return null;
   }

   @Override
   public void doRender2(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      renderOffsetAABB(entity.getEntityBoundingBox(), x - entity.lastTickPosX, y - entity.lastTickPosY, z - entity.lastTickPosZ);
      GlStateManager.popMatrix();
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
   }

   public RenderEntity(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }
}
