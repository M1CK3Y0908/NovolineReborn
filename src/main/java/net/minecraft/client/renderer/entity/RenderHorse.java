package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorse extends RenderLiving<EntityHorse> {
   private static final Map<String, ResourceLocation> horseTextureCache = Maps.newHashMap();
   private static final ResourceLocation whiteHorseTextures = new ResourceLocation("textures/entity/horse/horse_white.png");
   private static final ResourceLocation muleTextures = new ResourceLocation("textures/entity/horse/mule.png");
   private static final ResourceLocation donkeyTextures = new ResourceLocation("textures/entity/horse/donkey.png");
   private static final ResourceLocation zombieHorseTextures = new ResourceLocation("textures/entity/horse/horse_zombie.png");
   private static final ResourceLocation skeletonHorseTextures = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

   protected void preRenderCallback(EntityHorse entitylivingbaseIn, float partialTickTime) {
      float f = 1.0F;
      int horseType = entitylivingbaseIn.getHorseType();
      if (horseType == 1) {
         f *= 0.87F;
      } else if (horseType == 2) {
         f *= 0.92F;
      }

      GlStateManager.scale(f, f, f);
      super.preRenderCallback(entitylivingbaseIn, partialTickTime);
   }

   private ResourceLocation getHorseTexture(EntityHorse horse) {
      String text = horse.getHorseTexture();
      if (!horse.hasValidTexturePaths()) {
         return null;
      }

      ResourceLocation resourceLocation = horseTextureCache.get(text);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(text);
         Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new LayeredTexture(horse.getVariantTexturePaths()));
         horseTextureCache.put(text, resourceLocation);
      }

      return resourceLocation;
   }

   protected ResourceLocation getEntityTexture(EntityHorse entity) {
      if (!entity.canWearArmor()) {
         switch (entity.getHorseType()) {
            case 0:
            default:
               return whiteHorseTextures;
            case 1:
               return donkeyTextures;
            case 2:
               return muleTextures;
            case 3:
               return zombieHorseTextures;
            case 4:
               return skeletonHorseTextures;
         }
      } else {
         return this.getHorseTexture(entity);
      }
   }

   public RenderHorse(RenderManager rendermanagerIn, ModelHorse model, float shadowSizeIn) {
      super(rendermanagerIn, model, shadowSizeIn);
   }
}
