package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderEndermite;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEndermite extends ModelAdapter {
   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body1", "body2", "body3", "body4"};
   }

   public ModelAdapterEndermite() {
      super(EntityEndermite.class, "endermite", 0.3F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelEnderMite modelEnderMite)) {
         return null;
      } else if (modelPart.startsWith("body")) {
         ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelEnderMite, Reflector.reflectorField39);
         if (modelRenderers == null) {
            return null;
         }

         int i = Config.parseInt(modelPart.substring("body".length()), -1);
         i--;
         return i >= 0 && i < modelRenderers.length ? modelRenderers[i] : null;
      } else {
         return null;
      }
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderEndermite renderEndermite = new RenderEndermite(renderManager);
      renderEndermite.mainModel = modelBase;
      renderEndermite.shadowSize = shadowSize;
      return renderEndermite;
   }

   @Override
   public ModelBase makeModel() {
      return new ModelEnderMite();
   }
}
