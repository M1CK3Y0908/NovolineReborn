package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSilverfish extends ModelAdapter {
   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderSilverfish renderSilverfish = new RenderSilverfish(renderManager);
      renderSilverfish.mainModel = modelBase;
      renderSilverfish.shadowSize = shadowSize;
      return renderSilverfish;
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelSilverfish modelSilverfish)) {
         return null;
      } else if (modelPart.startsWith("body")) {
         ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelSilverfish, Reflector.reflectorField49);
         if (modelRenderers == null) {
            return null;
         }

         String text = modelPart.substring("body".length());
         int i = Config.parseInt(text, -1);
         i--;
         return i >= 0 && i < modelRenderers.length ? modelRenderers[i] : null;
      } else if (modelPart.startsWith("wing")) {
         ModelRenderer[] modelRenderers2 = (ModelRenderer[])Reflector.createObject6(modelSilverfish, Reflector.reflectorField50);
         if (modelRenderers2 == null) {
            return null;
         }

         String text2 = modelPart.substring("wing".length());
         int i2 = Config.parseInt(text2, -1);
         i2--;
         return i2 >= 0 && i2 < modelRenderers2.length ? modelRenderers2[i2] : null;
      } else {
         return null;
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body1", "body2", "body3", "body4", "body5", "body6", "body7", "wing1", "wing2", "wing3"};
   }

   public ModelAdapterSilverfish() {
      super(EntitySilverfish.class, "silverfish", 0.3F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSilverfish();
   }
}
