package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntitySign;
import net.optifine.reflect.Reflector;

public class ModelAdapterSign extends ModelAdapter {
   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      TileEntityRendererDispatcher tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer tileEntitySpecialRenderer = tileEntityRendererDispatcher.getSpecialRendererByClass(TileEntitySign.class);
      if (!(tileEntitySpecialRenderer instanceof TileEntitySignRenderer)) {
         return null;
      }

      if (tileEntitySpecialRenderer.getEntityClass() == null) {
         tileEntitySpecialRenderer = new TileEntitySignRenderer();
         tileEntitySpecialRenderer.setRendererDispatcher(tileEntityRendererDispatcher);
      }

      if (!Reflector.reflectorField72.isActive()) {
         Config.warn("Field not found: TileEntitySignRenderer.model");
         return null;
      } else {
         Reflector.checkObject4(tileEntitySpecialRenderer, Reflector.reflectorField72, modelBase);
         return tileEntitySpecialRenderer;
      }
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelSign modelSign)) {
         return null;
      } else {
         return modelPart.equals("board") ? modelSign.signBoard : (modelPart.equals("stick") ? modelSign.signStick : null);
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSign();
   }

   public ModelAdapterSign() {
      super(TileEntitySign.class, "sign", 0.0F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"board", "stick"};
   }
}
