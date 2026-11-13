package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterWither extends ModelAdapter {
   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderWither renderWither = new RenderWither(renderManager);
      renderWither.mainModel = modelBase;
      renderWither.shadowSize = shadowSize;
      return renderWither;
   }

   @Override
   public ModelBase makeModel() {
      return new ModelWither(0.0F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body1", "body2", "body3", "head1", "head2", "head3"};
   }

   public ModelAdapterWither() {
      super(EntityWither.class, "wither", 0.5F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelWither modelWither)) {
         return null;
      } else if (modelPart.startsWith("body")) {
         ModelRenderer[] modelRenderers = (ModelRenderer[])Reflector.createObject6(modelWither, Reflector.reflectorField55);
         if (modelRenderers == null) {
            return null;
         }

         String text = modelPart.substring("body".length());
         int i = Config.parseInt(text, -1);
         i--;
         return i >= 0 && i < modelRenderers.length ? modelRenderers[i] : null;
      } else if (modelPart.startsWith("head")) {
         ModelRenderer[] modelRenderers2 = (ModelRenderer[])Reflector.createObject6(modelWither, Reflector.reflectorField56);
         if (modelRenderers2 == null) {
            return null;
         }

         String text2 = modelPart.substring("head".length());
         int i2 = Config.parseInt(text2, -1);
         i2--;
         return i2 >= 0 && i2 < modelRenderers2.length ? modelRenderers2[i2] : null;
      } else {
         return null;
      }
   }
}
