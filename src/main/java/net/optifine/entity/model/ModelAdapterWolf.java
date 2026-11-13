package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.optifine.reflect.Reflector;

public class ModelAdapterWolf extends ModelAdapter {
   public ModelAdapterWolf() {
      super(EntityWolf.class, "wolf", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelWolf();
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "leg1", "leg2", "leg3", "leg4", "tail", "mane"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderWolf(renderManager, modelBase, shadowSize);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelWolf modelWolf)) {
         return null;
      } else {
         return modelPart.equals("head")
            ? modelWolf.wolfHeadMain
            : (
               modelPart.equals("body")
                  ? modelWolf.wolfBody
                  : (
                     modelPart.equals("leg1")
                        ? modelWolf.wolfLeg1
                        : (
                           modelPart.equals("leg2")
                              ? modelWolf.wolfLeg2
                              : (
                                 modelPart.equals("leg3")
                                    ? modelWolf.wolfLeg3
                                    : (
                                       modelPart.equals("leg4")
                                          ? modelWolf.wolfLeg4
                                          : (
                                             modelPart.equals("tail")
                                                ? (ModelRenderer)Reflector.createObject6(modelWolf, Reflector.reflectorField57)
                                                : (modelPart.equals("mane") ? (ModelRenderer)Reflector.createObject6(modelWolf, Reflector.reflectorField58) : null)
                                          )
                                    )
                              )
                        )
                  )
            );
      }
   }
}
