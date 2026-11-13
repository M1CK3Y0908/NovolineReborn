package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.optifine.reflect.Reflector;

public class ModelAdapterWitch extends ModelAdapter {
   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelWitch modelWitch)) {
         return null;
      } else {
         return modelPart.equals("mole")
            ? (ModelRenderer)Reflector.createObject6(modelWitch, Reflector.reflectorField53)
            : (
               modelPart.equals("hat")
                  ? (ModelRenderer)Reflector.createObject6(modelWitch, Reflector.reflectorField54)
                  : (
                     modelPart.equals("head")
                        ? modelWitch.villagerHead
                        : (
                           modelPart.equals("body")
                              ? modelWitch.villagerBody
                              : (
                                 modelPart.equals("arms")
                                    ? modelWitch.villagerArms
                                    : (
                                       modelPart.equals("left_leg")
                                          ? modelWitch.leftVillagerLeg
                                          : (modelPart.equals("right_leg") ? modelWitch.rightVillagerLeg : (modelPart.equals("nose") ? modelWitch.villagerNose : null))
                                    )
                              )
                        )
                  )
            );
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelWitch(0.0F);
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderWitch renderWitch = new RenderWitch(renderManager);
      renderWitch.mainModel = modelBase;
      renderWitch.shadowSize = shadowSize;
      return renderWitch;
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"mole", "head", "body", "arms", "right_leg", "left_leg", "nose"};
   }

   public ModelAdapterWitch() {
      super(EntityWitch.class, "witch", 0.5F);
   }
}
