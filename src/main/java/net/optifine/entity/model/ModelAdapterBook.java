package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.optifine.reflect.Reflector;

public class ModelAdapterBook extends ModelAdapter {
   public ModelAdapterBook() {
      super(TileEntityEnchantmentTable.class, "book", 0.0F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelBook modelBook2)) {
         return null;
      } else {
         return modelPart.equals("cover_right")
            ? modelBook2.coverRight
            : (
               modelPart.equals("cover_left")
                  ? modelBook2.coverLeft
                  : (
                     modelPart.equals("pages_right")
                        ? modelBook2.pagesRight
                        : (
                           modelPart.equals("pages_left")
                              ? modelBook2.pagesLeft
                              : (
                                 modelPart.equals("flipping_page_right")
                                    ? modelBook2.flippingPageRight
                                    : (
                                       modelPart.equals("flipping_page_left")
                                          ? modelBook2.flippingPageLeft
                                          : (modelPart.equals("book_spine") ? modelBook2.bookSpine : null)
                                    )
                              )
                        )
                  )
            );
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelBook();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      TileEntityRendererDispatcher tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer<?> tileEntitySpecialRenderer = tileEntityRendererDispatcher.getSpecialRendererByClass(TileEntityEnchantmentTable.class);
      if (!(tileEntitySpecialRenderer instanceof TileEntityEnchantmentTableRenderer)) {
         return null;
      }

      if (tileEntitySpecialRenderer.getEntityClass() == null) {
         tileEntitySpecialRenderer = new TileEntityEnchantmentTableRenderer();
         tileEntitySpecialRenderer.setRendererDispatcher(tileEntityRendererDispatcher);
      }

      if (!Reflector.reflectorField69.isActive()) {
         Config.warn("Field not found: TileEntityEnchantmentTableRenderer.modelBook");
         return null;
      } else {
         Reflector.checkObject4(tileEntitySpecialRenderer, Reflector.reflectorField69, modelBase);
         return tileEntitySpecialRenderer;
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"cover_right", "cover_left", "pages_right", "pages_left", "flipping_page_right", "flipping_page_left", "book_spine"};
   }
}
