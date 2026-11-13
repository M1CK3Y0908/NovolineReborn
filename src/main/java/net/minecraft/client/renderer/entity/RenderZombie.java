package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped<EntityZombie> {
   private final ModelBiped zombieModel;
   private final ModelZombieVillager zombieVillagerModel;
   private final List<LayerRenderer<EntityZombie>> zombieLayerRenderers;
   private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
   private final List<LayerRenderer<EntityZombie>> zombieVillagerLayerRenderers;

   public RenderZombie(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelZombie(), 0.5F, 1.0F);
      LayerRenderer layerRenderer = this.layerRenderers.get(0);
      this.zombieModel = this.modelBipedMain;
      this.zombieVillagerModel = new ModelZombieVillager();
      this.addLayer(new LayerHeldItem(this));
      LayerBipedArmor layerBipedArmor = new LayerBipedArmor(this) {
         @Override
         protected void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      };
      this.addLayer(layerBipedArmor);
      this.zombieLayerRenderers = Lists.newArrayList(this.layerRenderers);
      if (layerRenderer instanceof LayerCustomHead) {
         this.removeLayer(layerRenderer);
         this.addLayer(new LayerCustomHead(this.zombieVillagerModel.bipedHead));
      }

      this.removeLayer(layerBipedArmor);
      this.addLayer(new LayerVillagerArmor(this));
      this.zombieVillagerLayerRenderers = Lists.newArrayList(this.layerRenderers);
   }

   protected void rotateCorpse(EntityZombie bat, float f, float f2, float partialTicks) {
      if (bat.isConverting()) {
         f2 += (float)(Math.cos(bat.ticksExisted * 3.25) * Math.PI * 0.25);
      }

      super.rotateCorpse(bat, f, f2, partialTicks);
   }

   private void setModelForZombieType(EntityZombie zombie) {
      if (zombie.isVillager()) {
         this.mainModel = this.zombieVillagerModel;
         this.layerRenderers = this.zombieVillagerLayerRenderers;
      } else {
         this.mainModel = this.zombieModel;
         this.layerRenderers = this.zombieLayerRenderers;
      }

      this.modelBipedMain = (ModelBiped)this.mainModel;
   }

   protected ResourceLocation getEntityTexture(EntityZombie entity) {
      return entity.isVillager() ? zombieVillagerTextures : zombieTextures;
   }

   public void doRender2(EntityZombie entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.setModelForZombieType(entity);
      super.doRender2(entity, x, y, z, entityYaw, partialTicks);
   }
}
