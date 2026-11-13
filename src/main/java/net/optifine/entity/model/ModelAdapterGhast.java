package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGhast;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterGhast extends ModelAdapter {
   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8", "tentacle9"};
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelGhast modelGhast)) {
         return null;
      } else {
         if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.createObject6(modelGhast, Reflector.reflectorField40);
         }

         if (modelPart.startsWith("tentacle")) {
            ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelGhast, Reflector.reflectorField41);
            if (modelRenderers == null) {
               return null;
            }

            int i = Config.parseInt(modelPart.substring("tentacle".length()), -1);
            i--;
            return i >= 0 && i < modelRenderers.length ? modelRenderers[i] : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderGhast renderGhast = new RenderGhast(renderManager);
      renderGhast.mainModel = modelBase;
      renderGhast.shadowSize = shadowSize;
      return renderGhast;
   }

   @Override
   public ModelBase makeModel() {
      return new ModelGhast();
   }

   public ModelAdapterGhast() {
      super(EntityGhast.class, "ghast", 0.5F);
   }
}
