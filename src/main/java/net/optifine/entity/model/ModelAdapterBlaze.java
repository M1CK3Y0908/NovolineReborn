package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBlaze;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterBlaze extends ModelAdapter {
   @Override
   public ModelBase makeModel() {
      return new ModelBlaze();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderBlaze renderBlaze = new RenderBlaze(renderManager);
      renderBlaze.mainModel = modelBase;
      renderBlaze.shadowSize = shadowSize;
      return renderBlaze;
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "stick1", "stick2", "stick3", "stick4", "stick5", "stick6", "stick7", "stick8", "stick9", "stick10", "stick11", "stick12"};
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelBlaze modelBlaze)) {
         return null;
      } else {
         if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.createObject6(modelBlaze, Reflector.reflectorField34);
         }

         if (modelPart.startsWith("stick")) {
            ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelBlaze, Reflector.reflectorField35);
            if (modelRenderers == null) {
               return null;
            }

            int i = Config.parseInt(modelPart.substring("stick".length()), -1);
            i--;
            return i >= 0 && i < modelRenderers.length ? modelRenderers[i] : null;
         } else {
            return null;
         }
      }
   }

   public ModelAdapterBlaze() {
      super(EntityBlaze.class, "blaze", 0.5F);
   }
}
