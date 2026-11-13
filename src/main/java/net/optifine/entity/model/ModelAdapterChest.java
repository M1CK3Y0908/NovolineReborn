package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityChest;
import net.optifine.reflect.Reflector;

public class ModelAdapterChest extends ModelAdapter {
   public ModelAdapterChest() {
      super(TileEntityChest.class, "chest", 0.0F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"lid", "base", "knob"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      TileEntityRendererDispatcher tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer tileEntitySpecialRenderer = tileEntityRendererDispatcher.getSpecialRendererByClass(TileEntityChest.class);
      if (!(tileEntitySpecialRenderer instanceof TileEntityChestRenderer)) {
         return null;
      }

      if (tileEntitySpecialRenderer.getEntityClass() == null) {
         tileEntitySpecialRenderer = new TileEntityChestRenderer();
         tileEntitySpecialRenderer.setRendererDispatcher(tileEntityRendererDispatcher);
      }

      if (!Reflector.reflectorField66.isActive()) {
         Config.warn("Field not found: TileEntityChestRenderer.simpleChest");
         return null;
      } else {
         Reflector.checkObject4(tileEntitySpecialRenderer, Reflector.reflectorField66, modelBase);
         return tileEntitySpecialRenderer;
      }
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelChest modelChest)) {
         return null;
      } else {
         return modelPart.equals("lid") ? modelChest.chestLid : (modelPart.equals("base") ? modelChest.chestBelow : (modelPart.equals("knob") ? modelChest.chestKnob : null));
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelChest();
   }
}
