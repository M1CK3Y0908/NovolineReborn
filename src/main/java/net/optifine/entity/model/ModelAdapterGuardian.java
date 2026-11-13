package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterGuardian extends ModelAdapter {
   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelGuardian modelGuardian)) {
         return null;
      } else {
         if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.createObject6(modelGuardian, Reflector.reflectorField42);
         }

         if (modelPart.equals("eye")) {
            return (ModelRenderer)Reflector.createObject6(modelGuardian, Reflector.reflectorField43);
         }

         if (modelPart.startsWith("spine")) {
            ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelGuardian, Reflector.reflectorField44);
            if (modelRenderers == null) {
               return null;
            }

            String text = modelPart.substring("spine".length());
            int i = Config.parseInt(text, -1);
            i--;
            return i >= 0 && i < modelRenderers.length ? modelRenderers[i] : null;
         } else if (modelPart.startsWith("tail")) {
            ModelRenderer[] modelRenderers2 = (ModelRenderer[])Reflector.createObject6(modelGuardian, Reflector.reflectorField45);
            if (modelRenderers2 == null) {
               return null;
            }

            String text2 = modelPart.substring("tail".length());
            int i2 = Config.parseInt(text2, -1);
            i2--;
            return i2 >= 0 && i2 < modelRenderers2.length ? modelRenderers2[i2] : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelGuardian();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderGuardian renderGuardian = new RenderGuardian(renderManager);
      renderGuardian.mainModel = modelBase;
      renderGuardian.shadowSize = shadowSize;
      return renderGuardian;
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{
         "body",
         "eye",
         "spine1",
         "spine2",
         "spine3",
         "spine4",
         "spine5",
         "spine6",
         "spine7",
         "spine8",
         "spine9",
         "spine10",
         "spine11",
         "spine12",
         "tail1",
         "tail2",
         "tail3"
      };
   }

   public ModelAdapterGuardian() {
      super(EntityGuardian.class, "guardian", 0.5F);
   }
}
