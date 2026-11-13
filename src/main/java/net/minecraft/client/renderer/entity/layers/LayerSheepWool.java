package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;

public class LayerSheepWool implements LayerRenderer<EntitySheep> {
   public ModelSheep1 sheepModel = new ModelSheep1();
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final RenderSheep sheepRenderer;

   public LayerSheepWool(RenderSheep sheepRendererIn) {
      this.sheepRenderer = sheepRendererIn;
   }

   public void doRenderLayer(
      EntitySheep entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible()) {
         this.sheepRenderer.bindTexture(TEXTURE);
         if (entitylivingbaseIn.hasCustomName() && "jeb_".equals(entitylivingbaseIn.getCustomNameTag())) {
            int i = entitylivingbaseIn.ticksExisted / 25 + entitylivingbaseIn.getEntityId();
            int length2 = EnumDyeColor.values().length;
            int i2 = i % length2;
            int i3 = (i + 1) % length2;
            float f6 = (entitylivingbaseIn.ticksExisted % 25 + partialTicks) / 25.0F;
            float[] dyeRgb = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(i2));
            float[] dyeRgb2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(i3));
            if (Config.isCustomColors()) {
               dyeRgb = CustomColors.getSheepColors(EnumDyeColor.byMetadata(i2), dyeRgb);
               dyeRgb2 = CustomColors.getSheepColors(EnumDyeColor.byMetadata(i3), dyeRgb2);
            }

            GlStateManager.color(
               dyeRgb[0] * (1.0F - f6) + dyeRgb2[0] * f6, dyeRgb[1] * (1.0F - f6) + dyeRgb2[1] * f6, dyeRgb[2] * (1.0F - f6) + dyeRgb2[2] * f6
            );
         } else {
            float[] dyeRgb3 = EntitySheep.getDyeRgb(entitylivingbaseIn.getFleeceColor());
            if (Config.isCustomColors()) {
               dyeRgb3 = CustomColors.getSheepColors(entitylivingbaseIn.getFleeceColor(), dyeRgb3);
            }

            GlStateManager.color(dyeRgb3[0], dyeRgb3[1], dyeRgb3[2]);
         }

         this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
         this.sheepModel.setLivingAnimations(entitylivingbaseIn, f, f2, partialTicks);
         this.sheepModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
