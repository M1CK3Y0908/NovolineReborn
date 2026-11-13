package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.ResourceLocation;

public class RenderWither extends RenderLiving<EntityWither> {
   private static final ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");

   protected void preRenderCallback(EntityWither entitylivingbaseIn, float partialTickTime) {
      float f = 2.0F;
      int invulTime = entitylivingbaseIn.getInvulTime();
      if (invulTime > 0) {
         f -= (invulTime - partialTickTime) / 220.0F * 0.5F;
      }

      GlStateManager.scale(f, f, f);
   }

   public void doRender2(EntityWither entity, double x, double y, double z, float entityYaw, float partialTicks) {
      BossStatus.setBossStatus(entity, true);
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityWither entity) {
      int invulTime = entity.getInvulTime();
      return invulTime <= 0 || invulTime <= 80 && invulTime / 5 % 2 == 1 ? witherTextures : invulnerableWitherTextures;
   }

   public RenderWither(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelWither(0.0F), 1.0F);
      this.addLayer(new LayerWitherAura(this));
   }
}
