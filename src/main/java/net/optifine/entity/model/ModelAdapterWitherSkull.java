package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterWitherSkull extends ModelAdapter {
   @Override
   public ModelBase makeModel() {
      return new ModelSkeletonHead();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderWitherSkull renderWitherSkull = new RenderWitherSkull(renderManager);
      if (!Reflector.reflectorField62.isActive()) {
         Config.warn("Field not found: RenderWitherSkull_model");
         return null;
      } else {
         Reflector.checkObject4(renderWitherSkull, Reflector.reflectorField62, modelBase);
         renderWitherSkull.shadowSize = shadowSize;
         return renderWitherSkull;
      }
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelSkeletonHead modelSkeletonHead)) {
         return null;
      } else {
         return modelPart.equals("head") ? modelSkeletonHead.skeletonHead : null;
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head"};
   }

   public ModelAdapterWitherSkull() {
      super(EntityWitherSkull.class, "wither_skull", 0.0F);
   }
}
