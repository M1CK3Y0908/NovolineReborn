package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.optifine.reflect.Reflector;

public class ModelAdapterEnderChest extends ModelAdapter {
   @Override
   public ModelBase makeModel() {
      return new ModelChest();
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"lid", "base", "knob"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      TileEntityRendererDispatcher tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer<?> tileEntitySpecialRenderer = tileEntityRendererDispatcher.getSpecialRendererByClass(TileEntityEnderChest.class);
      if (!(tileEntitySpecialRenderer instanceof TileEntityEnderChestRenderer)) {
         return null;
      }

      if (tileEntitySpecialRenderer.getEntityClass() == null) {
         tileEntitySpecialRenderer = new TileEntityEnderChestRenderer();
         tileEntitySpecialRenderer.setRendererDispatcher(tileEntityRendererDispatcher);
      }

      if (!Reflector.reflectorField70.isActive()) {
         Config.warn("Field not found: TileEntityEnderChestRenderer.modelChest");
         return null;
      } else {
         Reflector.checkObject4(tileEntitySpecialRenderer, Reflector.reflectorField70, modelBase);
         return tileEntitySpecialRenderer;
      }
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelChest modelChest2)) {
         return null;
      } else {
         return modelPart.equals("lid") ? modelChest2.chestLid : (modelPart.equals("base") ? modelChest2.chestBelow : (modelPart.equals("knob") ? modelChest2.chestKnob : null));
      }
   }

   public ModelAdapterEnderChest() {
      super(TileEntityEnderChest.class, "ender_chest", 0.0F);
   }
}
