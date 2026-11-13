package net.optifine.entity.model;

import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityBanner;
import net.optifine.reflect.Reflector;

public class ModelAdapterBanner extends ModelAdapter {
   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      TileEntityRendererDispatcher tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer<?> tileEntitySpecialRenderer = tileEntityRendererDispatcher.getSpecialRendererByClass(TileEntityBanner.class);
      if (!(tileEntitySpecialRenderer instanceof TileEntityBannerRenderer)) {
         return null;
      }

      if (tileEntitySpecialRenderer.getEntityClass() == null) {
         tileEntitySpecialRenderer = new TileEntityBannerRenderer();
         tileEntitySpecialRenderer.setRendererDispatcher(tileEntityRendererDispatcher);
      }

      if (!Reflector.reflectorField63.isActive()) {
         Config.warn("Field not found: TileEntityBannerRenderer.bannerModel");
         return null;
      } else {
         Reflector.checkObject4(tileEntitySpecialRenderer, Reflector.reflectorField63, modelBase);
         return tileEntitySpecialRenderer;
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"slate", "stand", "top"};
   }

   public ModelAdapterBanner() {
      super(TileEntityBanner.class, "banner", 0.0F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelBanner modelBanner)) {
         return null;
      } else {
         return modelPart.equals("slate")
            ? modelBanner.bannerSlate
            : (modelPart.equals("stand") ? modelBanner.bannerStand : (modelPart.equals("top") ? modelBanner.bannerTop : null));
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelBanner();
   }
}
