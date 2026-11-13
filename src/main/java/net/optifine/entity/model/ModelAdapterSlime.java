package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.optifine.reflect.Reflector;

public class ModelAdapterSlime extends ModelAdapter {
   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body", "left_eye", "right_eye", "mouth"};
   }

   public ModelAdapterSlime() {
      super(EntitySlime.class, "slime", 0.25F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelSlime modelSlime)) {
         return null;
      } else {
         return modelPart.equals("body")
            ? (ModelRenderer)Reflector.createObject3(modelSlime, Reflector.reflectorFields7, 0)
            : (
               modelPart.equals("left_eye")
                  ? (ModelRenderer)Reflector.createObject3(modelSlime, Reflector.reflectorFields7, 1)
                  : (
                     modelPart.equals("right_eye")
                        ? (ModelRenderer)Reflector.createObject3(modelSlime, Reflector.reflectorFields7, 2)
                        : (modelPart.equals("mouth") ? (ModelRenderer)Reflector.createObject3(modelSlime, Reflector.reflectorFields7, 3) : null)
                  )
            );
      }
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderSlime(renderManager, modelBase, shadowSize);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSlime(16);
   }
}
