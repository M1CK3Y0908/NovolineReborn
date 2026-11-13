package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItemWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;

public class RenderWitch extends RenderLiving<EntityWitch> {
   private static final ResourceLocation witchTextures = new ResourceLocation("textures/entity/witch.png");

   protected ResourceLocation getEntityTexture(EntityWitch entity) {
      return witchTextures;
   }

   public void doRender2(EntityWitch entity, double x, double y, double z, float entityYaw, float partialTicks) {
      ((ModelWitch)this.mainModel).holdingItem = entity.getHeldItem() != null;
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   public RenderWitch(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelWitch(0.0F), 0.5F);
      this.addLayer(new LayerHeldItemWitch(this));
   }

   protected void preRenderCallback(EntityWitch entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scale(f, f, f);
   }
}
