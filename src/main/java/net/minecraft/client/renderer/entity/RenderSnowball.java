package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderSnowball<T extends Entity> extends Render<T> {
   protected final Item item;
   private final RenderItem itemRenderer;

   @Override
   protected ResourceLocation getEntityTexture(Entity entity) {
      return TextureMap.locationBlocksTexture;
   }

   @Override
   public void doRender2(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      this.bindTexture(TextureMap.locationBlocksTexture);
      this.itemRenderer.renderItem(this.getStackToRender(entity), net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GROUND);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
   }

   public ItemStack getStackToRender(T entityIn) {
      return new ItemStack(this.item, 1, 0);
   }

   public RenderSnowball(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn) {
      super(renderManagerIn);
      this.item = itemIn;
      this.itemRenderer = itemRendererIn;
   }
}
