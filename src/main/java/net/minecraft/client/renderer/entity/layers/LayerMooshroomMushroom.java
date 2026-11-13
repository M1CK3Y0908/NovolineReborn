package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderMooshroom;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerMooshroomMushroom implements LayerRenderer<EntityMooshroom> {
   private final RenderMooshroom mooshroomRenderer;
   private static final ResourceLocation LOCATION_MUSHROOM_RED = new ResourceLocation("textures/entity/cow/mushroom_red.png");
   private ModelRenderer modelRendererMushroom;
   private static boolean hasTextureMushroom = false;

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }

   public void doRenderLayer(
      EntityMooshroom entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible()) {
         BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
         if (hasTextureMushroom) {
            this.mooshroomRenderer.bindTexture(LOCATION_MUSHROOM_RED);
         } else {
            this.mooshroomRenderer.bindTexture(TextureMap.locationBlocksTexture);
         }

         GlStateManager.enableCull();
         GlStateManager.cullFace(1028);
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F, -1.0F, 1.0F);
         GlStateManager.translate(0.2F, 0.35F, 0.5F);
         GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate(-0.5F, -0.5F, 0.5F);
         if (hasTextureMushroom) {
            this.modelRendererMushroom.render(0.0625F);
         } else {
            blockRendererDispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0F);
         }

         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.1F, 0.0F, -0.6F);
         GlStateManager.rotate(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.5F, -0.5F, 0.5F);
         if (hasTextureMushroom) {
            this.modelRendererMushroom.render(0.0625F);
         } else {
            blockRendererDispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0F);
         }

         GlStateManager.popMatrix();
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         ((ModelQuadruped)this.mooshroomRenderer.getMainModel()).head.postRender(0.0625F);
         GlStateManager.scale(1.0F, -1.0F, 1.0F);
         GlStateManager.translate(0.0F, 0.7F, -0.2F);
         GlStateManager.rotate(12.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.5F, -0.5F, 0.5F);
         if (hasTextureMushroom) {
            this.modelRendererMushroom.render(0.0625F);
         } else {
            blockRendererDispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0F);
         }

         GlStateManager.popMatrix();
         GlStateManager.cullFace(1029);
         GlStateManager.disableCull();
      }
   }

   public static void update() {
      hasTextureMushroom = Config.hasResource(LOCATION_MUSHROOM_RED);
   }

   public LayerMooshroomMushroom(RenderMooshroom mooshroomRendererIn) {
      this.mooshroomRenderer = mooshroomRendererIn;
      this.modelRendererMushroom = new ModelRenderer(this.mooshroomRenderer.mainModel);
      this.modelRendererMushroom.setTextureSize(16, 16);
      this.modelRendererMushroom.rotationPointX = -6.0F;
      this.modelRendererMushroom.rotationPointZ = -8.0F;
      this.modelRendererMushroom.rotateAngleY = MathHelper.PI / 4.0F;
      int[][] iArrays = new int[][]{null, null, {16, 16, 0, 0}, {16, 16, 0, 0}, null, null};
      this.modelRendererMushroom.addBox(iArrays, 0.0F, 0.0F, 10.0F, 20.0F, 16.0F, 0.0F, 0.0F);
      int[][] iArrays2 = new int[][]{null, null, null, null, {16, 16, 0, 0}, {16, 16, 0, 0}};
      this.modelRendererMushroom.addBox(iArrays2, 10.0F, 0.0F, 0.0F, 0.0F, 16.0F, 20.0F, 0.0F);
   }
}
