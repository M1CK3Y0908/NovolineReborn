package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityBat;
import net.optifine.reflect.Reflector;

public class ModelAdapterBat extends ModelAdapter {
   @Override
   public ModelBase makeModel() {
      return new ModelBat();
   }

   public ModelAdapterBat() {
      super(EntityBat.class, "bat", 0.25F);
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderBat renderBat = new RenderBat(renderManager);
      renderBat.mainModel = modelBase;
      renderBat.shadowSize = shadowSize;
      return renderBat;
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "right_wing", "left_wing", "outer_right_wing", "outer_left_wing"};
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelBat modelBat)) {
         return null;
      } else {
         return modelPart.equals("head")
            ? (ModelRenderer)Reflector.createObject3(modelBat, Reflector.reflectorFields, 0)
            : (
               modelPart.equals("body")
                  ? (ModelRenderer)Reflector.createObject3(modelBat, Reflector.reflectorFields, 1)
                  : (
                     modelPart.equals("right_wing")
                        ? (ModelRenderer)Reflector.createObject3(modelBat, Reflector.reflectorFields, 2)
                        : (
                           modelPart.equals("left_wing")
                              ? (ModelRenderer)Reflector.createObject3(modelBat, Reflector.reflectorFields, 3)
                              : (
                                 modelPart.equals("outer_right_wing")
                                    ? (ModelRenderer)Reflector.createObject3(modelBat, Reflector.reflectorFields, 4)
                                    : (modelPart.equals("outer_left_wing") ? (ModelRenderer)Reflector.createObject3(modelBat, Reflector.reflectorFields, 5) : null)
                              )
                        )
                  )
            );
      }
   }
}
