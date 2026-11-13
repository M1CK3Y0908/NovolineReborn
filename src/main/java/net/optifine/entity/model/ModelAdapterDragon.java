package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.boss.EntityDragon;
import net.optifine.reflect.Reflector;

public class ModelAdapterDragon extends ModelAdapter {
   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelDragon modelDragon)) {
         return null;
      } else {
         return modelPart.equals("head")
            ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 0)
            : (
               modelPart.equals("spine")
                  ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 1)
                  : (
                     modelPart.equals("jaw")
                        ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 2)
                        : (
                           modelPart.equals("body")
                              ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 3)
                              : (
                                 modelPart.equals("rear_leg")
                                    ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 4)
                                    : (
                                       modelPart.equals("front_leg")
                                          ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 5)
                                          : (
                                             modelPart.equals("rear_leg_tip")
                                                ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 6)
                                                : (
                                                   modelPart.equals("front_leg_tip")
                                                      ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 7)
                                                      : (
                                                         modelPart.equals("rear_foot")
                                                            ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 8)
                                                            : (
                                                               modelPart.equals("front_foot")
                                                                  ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 9)
                                                                  : (
                                                                     modelPart.equals("wing")
                                                                        ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 10)
                                                                        : (
                                                                           modelPart.equals("wing_tip")
                                                                              ? (ModelRenderer)Reflector.createObject3(modelDragon, Reflector.reflectorFields2, 11)
                                                                              : null
                                                                        )
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            );
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{
         "head", "spine", "jaw", "body", "rear_leg", "front_leg", "rear_leg_tip", "front_leg_tip", "rear_foot", "front_foot", "wing", "wing_tip"
      };
   }

   @Override
   public ModelBase makeModel() {
      return new ModelDragon(0.0F);
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderDragon renderDragon = new RenderDragon(renderManager);
      renderDragon.mainModel = modelBase;
      renderDragon.shadowSize = shadowSize;
      return renderDragon;
   }

   public ModelAdapterDragon() {
      super(EntityDragon.class, "dragon", 0.5F);
   }
}
