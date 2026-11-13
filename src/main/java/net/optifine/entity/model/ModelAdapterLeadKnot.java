package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderLeashKnot;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterLeadKnot extends ModelAdapter {
   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelLeashKnot modelLeashKnot)) {
         return null;
      } else {
         return modelPart.equals("knot") ? modelLeashKnot.knotRenderer : null;
      }
   }

   public ModelAdapterLeadKnot() {
      super(EntityLeashKnot.class, "lead_knot", 0.0F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"knot"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderLeashKnot renderLeashKnot = new RenderLeashKnot(renderManager);
      if (!Reflector.reflectorField46.isActive()) {
         Config.warn("Field not found: RenderLeashKnot.leashKnotModel");
         return null;
      } else {
         Reflector.checkObject4(renderLeashKnot, Reflector.reflectorField46, modelBase);
         renderLeashKnot.shadowSize = shadowSize;
         return renderLeashKnot;
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelLeashKnot();
   }
}
