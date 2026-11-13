package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
   private float alpha = 1.0F;
   private float colorR = 1.0F;
   protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private float colorG = 1.0F;
   private float colorB = 1.0F;
   protected T modelArmor;
   protected T modelLeggings;
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
   private final RendererLivingEntity<?> renderer;
   private boolean skipRenderGlint;

   private ResourceLocation getArmorResource(ItemArmor itemArmor, boolean flag, String text) {
      String name = String.format(
         "textures/models/armor/%s_layer_%d%s.png", itemArmor.getArmorMaterial().getName(), flag ? 2 : 1, text == null ? "" : String.format("_%s", text)
      );
      ResourceLocation resourceLocation = ARMOR_TEXTURE_RES_MAP.get(name);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(name);
         ARMOR_TEXTURE_RES_MAP.put(name, resourceLocation);
      }

      return resourceLocation;
   }

   private void renderLayer(
      EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale, int armorSlot
   ) {
      ItemStack currentArmor = this.getCurrentArmor(entitylivingbaseIn, armorSlot);
      if (currentArmor != null && currentArmor.getItem() instanceof ItemArmor) {
         ItemArmor itemArmor = (ItemArmor)currentArmor.getItem();
         ModelBase armorModel = this.getArmorModel(armorSlot);
         armorModel.setModelAttributes(this.renderer.getMainModel());
         armorModel.setLivingAnimations(entitylivingbaseIn, f, f2, partialTicks);
         if (Reflector.reflectorClass22.isActive()) {
            armorModel = this.getModelBase(entitylivingbaseIn, currentArmor, armorModel);
         }

         this.setModelPartVisible((T)armorModel, armorSlot);
         boolean slotForLeggings = this.isSlotForLeggings(armorSlot);
         if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, slotForLeggings ? 2 : 1, (String)null)) {
            if (Reflector.reflectorMethod58.isActive()) {
               this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, currentArmor, slotForLeggings ? 2 : 1, (String)null));
            } else {
               this.renderer.bindTexture(this.getArmorResource(itemArmor, slotForLeggings));
            }
         }

         if (Reflector.reflectorMethod58.isActive()) {
            if (ReflectorForge.checkItemArmor(itemArmor, currentArmor)) {
               int color2 = itemArmor.getColor(currentArmor);
               float f6 = (color2 >> 16 & 0xFF) / 255.0F;
               float f7 = (color2 >> 8 & 0xFF) / 255.0F;
               float f8 = (color2 & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * f6, this.colorG * f7, this.colorB * f8, this.alpha);
               armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, slotForLeggings ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, currentArmor, slotForLeggings ? 2 : 1, "overlay"));
               }
            }

            GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
            armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
            if (!this.skipRenderGlint
               && currentArmor.hasEffect()
               && (
                  !Config.isCustomItems()
                     || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, currentArmor, armorModel, f, f2, partialTicks, f3, f4, f5, scale)
               )) {
               this.renderGlint(entitylivingbaseIn, (T)armorModel, f, f2, partialTicks, f3, f4, f5, scale);
            }

            return;
         }

         switch (itemArmor.getArmorMaterial()) {
            case LEATHER:
               int color3 = itemArmor.getColor(currentArmor);
               float f9 = (color3 >> 16 & 0xFF) / 255.0F;
               float f10 = (color3 >> 8 & 0xFF) / 255.0F;
               float f11 = (color3 & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * f9, this.colorG * f10, this.colorB * f11, this.alpha);
               armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, slotForLeggings ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(itemArmor, slotForLeggings, "overlay"));
               }
            case CHAIN:
            case IRON:
            case GOLD:
            case DIAMOND:
               GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
               armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
         }

         if (!this.skipRenderGlint
            && currentArmor.isItemEnchanted()
            && (
               !Config.isCustomItems()
                  || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, currentArmor, armorModel, f, f2, partialTicks, f3, f4, f5, scale)
            )) {
            this.renderGlint(entitylivingbaseIn, (T)armorModel, f, f2, partialTicks, f3, f4, f5, scale);
         }
      }
   }

   public ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot) {
      return entitylivingbaseIn.getCurrentArmor(armorSlot - 1);
   }

   public T getArmorModel(int armorSlot) {
      return this.isSlotForLeggings(armorSlot) ? this.modelLeggings : this.modelArmor;
   }

   protected abstract void initArmor();

   protected abstract void setModelPartVisible(T value, int i);

   private void renderGlint(
      EntityLivingBase entitylivingbaseIn, T modelbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      if (!Config.isShaders() || !Shaders.isShadowPass) {
         float f6 = entitylivingbaseIn.ticksExisted + partialTicks;
         this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintBegin();
         }

         GlStateManager.enableBlend();
         GlStateManager.depthFunc(514);
         GlStateManager.depthMask(false);
         float f7 = 0.5F;
         GlStateManager.color(f7, f7, f7, 1.0F);

         for (int index = 0; index < 2; index++) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(768, 1);
            float f8 = 0.76F;
            GlStateManager.color(0.5F * f8, 0.25F * f8, 0.8F * f8, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f9 = 0.33333334F;
            GlStateManager.scale(f9, f9, f9);
            GlStateManager.rotate(30.0F - index * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f6 * (0.001F + index * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            modelbaseIn.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
         }

         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(515);
         GlStateManager.disableBlend();
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }
   }

   public ResourceLocation getArmorResource(Entity arg1, ItemStack arg2, int arg3, String arg4) {
      String name2 = ((ItemArmor)arg2.getItem()).getArmorMaterial().getName();
      String text = "minecraft";
      int i = name2.indexOf(58);
      if (i != -1) {
         text = name2.substring(0, i);
         name2 = name2.substring(i + 1);
      }

      String text2 = String.format(
         "%s:textures/models/armor/%s_layer_%d%s.png", text, name2, this.isSlotForLeggings(arg3) ? 2 : 1, arg4 == null ? "" : String.format("_%s", arg4)
      );
      text2 = Reflector.createString(Reflector.reflectorMethod58, arg1, arg2, text2, arg3, arg4);
      ResourceLocation resourceLocation2 = ARMOR_TEXTURE_RES_MAP.get(text2);
      if (resourceLocation2 == null) {
         resourceLocation2 = new ResourceLocation(text2);
         ARMOR_TEXTURE_RES_MAP.put(text2, resourceLocation2);
      }

      return resourceLocation2;
   }

   public LayerArmorBase(RendererLivingEntity<?> rendererIn) {
      this.renderer = rendererIn;
      this.initArmor();
   }

   private ResourceLocation getArmorResource(ItemArmor itemArmor, boolean flag) {
      return this.getArmorResource(itemArmor, flag, (String)null);
   }

   @Override
   public void doRenderLayer(
      EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 4);
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 3);
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 2);
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 1);
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   protected ModelBase getModelBase(EntityLivingBase entityLivingBase, ItemStack itemStack, ModelBase modelBase) {
      return modelBase;
   }

   private boolean isSlotForLeggings(int armorSlot) {
      return armorSlot == 2;
   }
}
