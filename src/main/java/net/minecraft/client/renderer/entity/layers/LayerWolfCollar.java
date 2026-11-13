package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;

public class LayerWolfCollar implements LayerRenderer<EntityWolf> {
   private final RenderWolf wolfRenderer;
   private static final ResourceLocation WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

   public LayerWolfCollar(RenderWolf wolfRendererIn) {
      this.wolfRenderer = wolfRendererIn;
   }

   public void doRenderLayer(
      EntityWolf entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
         this.wolfRenderer.bindTexture(WOLF_COLLAR);
         EnumDyeColor dyeColor = EnumDyeColor.byMetadata(entitylivingbaseIn.getCollarColor().getMetadata());
         float[] floatArray = EntitySheep.getDyeRgb(dyeColor);
         if (Config.isCustomColors()) {
            floatArray = CustomColors.getWolfCollarColors(dyeColor, floatArray);
         }

         GlStateManager.color(floatArray[0], floatArray[1], floatArray[2]);
         this.wolfRenderer.getMainModel().render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
