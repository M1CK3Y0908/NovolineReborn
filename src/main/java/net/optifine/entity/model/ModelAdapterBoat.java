package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterBoat extends ModelAdapter {
   @Override
   public String[] getModelRendererNames() {
      return new String[]{"bottom", "back", "front", "right", "left"};
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelBoat modelBoat2)) {
         return null;
      } else {
         return modelPart.equals("bottom")
            ? modelBoat2.boatSides[0]
            : (
               modelPart.equals("back")
                  ? modelBoat2.boatSides[1]
                  : (
                     modelPart.equals("front")
                        ? modelBoat2.boatSides[2]
                        : (modelPart.equals("right") ? modelBoat2.boatSides[3] : (modelPart.equals("left") ? modelBoat2.boatSides[4] : null))
                  )
            );
      }
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderBoat renderBoat = new RenderBoat(renderManager);
      if (!Reflector.reflectorField60.isActive()) {
         Config.warn("Field not found: RenderBoat.modelBoat");
         return null;
      } else {
         Reflector.checkObject4(renderBoat, Reflector.reflectorField60, modelBase);
         renderBoat.shadowSize = shadowSize;
         return renderBoat;
      }
   }

   public ModelAdapterBoat() {
      super(EntityBoat.class, "boat", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelBoat();
   }
}
