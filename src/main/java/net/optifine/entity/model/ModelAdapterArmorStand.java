package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.src.Config;

public class ModelAdapterArmorStand extends ModelAdapterBiped {
   public ModelAdapterArmorStand() {
      super(EntityArmorStand.class, "armor_stand", 0.0F);
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      ArmorStandRenderer armorStandRenderer = new ArmorStandRenderer(renderManager);
      armorStandRenderer.mainModel = modelBase;
      armorStandRenderer.shadowSize = shadowSize;
      return armorStandRenderer;
   }

   @Override
   public ModelBase makeModel() {
      return new ModelArmorStand();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelArmorStand modelArmorStand)) {
         return null;
      } else {
         return modelPart.equals("right")
            ? modelArmorStand.standRightSide
            : (
               modelPart.equals("left")
                  ? modelArmorStand.standLeftSide
                  : (
                     modelPart.equals("waist")
                        ? modelArmorStand.standWaist
                        : (modelPart.equals("base") ? modelArmorStand.standBase : super.getModelRenderer(modelArmorStand, modelPart))
                  )
            );
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return (String[])Config.addObjectsToArray(super.getModelRendererNames(), new String[]{"right", "left", "waist", "base"});
   }
}
