package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntitySkull;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadHumanoid extends ModelAdapter {
   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      TileEntityRendererDispatcher tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer<?> tileEntitySpecialRenderer = tileEntityRendererDispatcher.getSpecialRendererByClass(TileEntitySkull.class);
      if (!(tileEntitySpecialRenderer instanceof TileEntitySkullRenderer)) {
         return null;
      }

      if (tileEntitySpecialRenderer.getEntityClass() == null) {
         tileEntitySpecialRenderer = new TileEntitySkullRenderer();
         tileEntitySpecialRenderer.setRendererDispatcher(tileEntityRendererDispatcher);
      }

      if (!Reflector.reflectorField74.isActive()) {
         Config.warn("Field not found: TileEntitySkullRenderer.humanoidHead");
         return null;
      } else {
         Reflector.checkObject4(tileEntitySpecialRenderer, Reflector.reflectorField74, modelBase);
         return tileEntitySpecialRenderer;
      }
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelHumanoidHead modelHumanoidHead)) {
         return null;
      } else {
         return modelPart.equals("head")
            ? modelHumanoidHead.skeletonHead
            : (
               modelPart.equals("head2")
                  ? (!Reflector.reflectorField33.isActive() ? null : (ModelRenderer)Reflector.createObject6(modelHumanoidHead, Reflector.reflectorField33))
                  : null
            );
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelHumanoidHead();
   }

   public ModelAdapterHeadHumanoid() {
      super(TileEntitySkull.class, "head_humanoid", 0.0F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head"};
   }
}
