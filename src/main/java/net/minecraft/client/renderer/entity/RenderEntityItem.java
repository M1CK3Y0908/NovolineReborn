package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.render.RenderUtils;

public class RenderEntityItem extends Render<EntityItem> {
   private Random renderRandom = new Random();
   private final Random RANDOM = new Random();
   private final RenderItem itemRenderer;

   private int renderDefaultMode(EntityItem entityItem, double x, double y, double z, float partialTicks, IBakedModel model) {
      ItemStack entityItem2 = entityItem.getEntityItem();
      if (entityItem2.getItem() == null) {
         return 0;
      }

      boolean gui3d = model.isGui3d();
      int stackRenderCount = this.getStackRenderCount(entityItem2);
      float f = MathHelper.sin((entityItem.getAge() + partialTicks) / 10.0F + entityItem.hoverStart) * 0.1F + 0.1F;
      float y2 = model.getItemCameraTransforms().getTransform(net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GROUND).scale.y;
      GlStateManager.translate((float)x, (float)y + f + 0.25F * y2, (float)z);
      if (gui3d || this.renderManager.options != null) {
         float f2 = ((entityItem.getAge() + partialTicks) / 20.0F + entityItem.hoverStart) * (180.0F / (float)Math.PI);
         GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
      }

      if (!gui3d) {
         float f3 = -0.0F * (stackRenderCount - 1) * 0.5F;
         float f4 = -0.0F * (stackRenderCount - 1) * 0.5F;
         float f5 = -0.046875F * (stackRenderCount - 1) * 0.5F;
         GlStateManager.translate(f3, f4, f5);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      return stackRenderCount;
   }

   public void doRender2(EntityItem entityItem2, double d, double d2, double d3, float f, float f2) {
      if (ModuleManager.itemPhysics.isEnabled()) {
         this.doRender(entityItem2, d, d2, d3, f, f2);
      } else {
         this.handleEntityItem(entityItem2, d, d2, d3, f, f2);
      }

      super.doRender2(entityItem2, d, d2, d3, f, f2);
   }

   private int getStackRenderCount(ItemStack stack) {
      int count = 1;
      if (stack.stackSize > 48) {
         count = 5;
      } else if (stack.stackSize > 32) {
         count = 4;
      } else if (stack.stackSize > 16) {
         count = 3;
      } else if (stack.stackSize > 1) {
         count = 2;
      }

      return count;
   }

   protected ResourceLocation getEntityTexture(EntityItem entity) {
      return TextureMap.locationBlocksTexture;
   }

   public RenderEntityItem(RenderManager renderManagerIn, RenderItem itemRendererIn) {
      super(renderManagerIn);
      this.itemRenderer = itemRendererIn;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   private void handleEntityItem(EntityItem entityItem2, double d, double d2, double d3, float f, float f2) {
      ItemStack entityItem3 = entityItem2.getEntityItem();
      this.renderRandom.setSeed(187L);
      boolean flag = false;
      if (this.bindEntityTexture(entityItem2)) {
         this.renderManager.renderEngine.getTexture(this.getEntityTexture(entityItem2)).noop(false);
         flag = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      GlStateManager.pushMatrix();
      IBakedModel itemModel = this.itemRenderer.getItemModelMesher().getItemModel(entityItem3);
      int i = this.renderDefaultMode(entityItem2, d, d2, d3, f2, itemModel);

      for (int i2 = 0; i2 < i; i2++) {
         if (itemModel.isGui3d()) {
            GlStateManager.pushMatrix();
            if (i2 > 0) {
               float f3 = (this.renderRandom.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f4 = (this.renderRandom.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f5 = (this.renderRandom.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(f3, f4, f5);
            }

            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            itemModel.getItemCameraTransforms().applyTransform(net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(entityItem3, itemModel);
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            itemModel.getItemCameraTransforms().applyTransform(net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(entityItem3, itemModel);
            GlStateManager.popMatrix();
            float x2 = itemModel.getItemCameraTransforms().ground.scale.x;
            float y2 = itemModel.getItemCameraTransforms().ground.scale.y;
            float z2 = itemModel.getItemCameraTransforms().ground.scale.z;
            GlStateManager.translate(0.0F * x2, 0.0F * y2, 0.046875F * z2);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindEntityTexture(entityItem2);
      if (flag) {
         this.renderManager.renderEngine.getTexture(this.getEntityTexture(entityItem2)).restoreLastBlurMipmap();
      }
   }

   private void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
      Minecraft minecraft2 = Minecraft.getMinecraft();
      double d = minecraft2.inGameHasFocus ? System.nanoTime() / 5.0E13 : 0.0;
      ItemStack entityItem2 = entity.getEntityItem();
      int i = 187;
      if (entityItem2 != null && entityItem2.getItem() != null) {
         i = Item.getIdFromItem(entityItem2.getItem()) + entityItem2.getMetadata();
      }

      this.RANDOM.setSeed(i);
      ResourceLocation entityTexture = this.getEntityTexture(entity);
      minecraft2.getTextureManager().bindTexture(entityTexture);
      minecraft2.getTextureManager().getTexture(entityTexture).noop(false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      IBakedModel itemModel = minecraft2.getRenderItem().getItemModelMesher().getItemModel(entityItem2);
      boolean gui3d = itemModel.isGui3d();
      int stackRenderCount = this.getStackRenderCount(entityItem2);
      GlStateManager.translate(x, y, z);
      if (gui3d) {
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
      }

      GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(entity.rotationYaw, 0.0F, 0.0F, 1.0F);
      if (gui3d) {
         GlStateManager.translate(0.0, 0.0, -0.08);
      } else {
         GlStateManager.translate(0.0, 0.0, -0.04);
      }

      if (gui3d || minecraft2.getRenderManager().options != null) {
         if (gui3d) {
            if (!entity.onGround) {
               d *= 2.0;
               entity.rotationPitch = (float)(entity.rotationPitch + d);
            }
         } else if (!Double.isNaN(entity.posX) && !Double.isNaN(entity.posY) && !Double.isNaN(entity.posZ) && entity.worldObj != null) {
            if (entity.onGround) {
               entity.rotationPitch = 0.0F;
            } else {
               d *= 2.0;
               entity.rotationPitch = (float)(entity.rotationPitch + d);
            }
         }

         GlStateManager.rotate(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
      }

      RenderUtils.setColor(-1);

      for (int i2 = 0; i2 < stackRenderCount; i2++) {
         GlStateManager.pushMatrix();
         if (gui3d) {
            if (i2 > 0) {
               float f = (this.RANDOM.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f2 = (this.RANDOM.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f3 = (this.RANDOM.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(f, f2, f3);
            }

            minecraft2.getRenderItem().renderItem(entityItem2, itemModel);
            GlStateManager.popMatrix();
         } else {
            minecraft2.getRenderItem().renderItem(entityItem2, itemModel);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.05375F);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      minecraft2.getTextureManager().bindTexture(entityTexture);
      minecraft2.getTextureManager().getTexture(entityTexture).restoreLastBlurMipmap();
   }
}
