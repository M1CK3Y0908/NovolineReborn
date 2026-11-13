package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderMagmaCube;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMagmaCube extends ModelAdapter {
   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelMagmaCube modelMagmaCube)) {
         return null;
      } else {
         if (modelPart.equals("core")) {
            return (ModelRenderer)Reflector.createObject6(modelMagmaCube, Reflector.reflectorField47);
         }

         if (modelPart.startsWith("segment")) {
            ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelMagmaCube, Reflector.reflectorField48);
            if (modelRenderers == null) {
               return null;
            }

            int i = Config.parseInt(modelPart.substring("segment".length()), -1);
            i--;
            return i >= 0 && i < modelRenderers.length ? modelRenderers[i] : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelMagmaCube();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderMagmaCube renderMagmaCube = new RenderMagmaCube(renderManager);
      renderMagmaCube.mainModel = modelBase;
      renderMagmaCube.shadowSize = shadowSize;
      return renderMagmaCube;
   }

   public ModelAdapterMagmaCube() {
      super(EntityMagmaCube.class, "magma_cube", 0.5F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"core", "segment1", "segment2", "segment3", "segment4", "segment5", "segment6", "segment7", "segment8"};
   }
}
