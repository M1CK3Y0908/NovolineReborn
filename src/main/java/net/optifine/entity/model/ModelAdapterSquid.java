package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSquid;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSquid extends ModelAdapter {
   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelSquid modelSquid)) {
         return null;
      } else {
         if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.createObject6(modelSquid, Reflector.reflectorField51);
         }

         if (modelPart.startsWith("tentacle")) {
            ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelSquid, Reflector.reflectorField52);
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
      return new RenderSquid(renderManager, modelBase, shadowSize);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8"};
   }

   public ModelAdapterSquid() {
      super(EntitySquid.class, "squid", 0.7F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSquid();
   }
}
