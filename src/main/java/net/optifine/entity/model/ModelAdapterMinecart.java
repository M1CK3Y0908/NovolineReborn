package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecart extends ModelAdapter {
   protected ModelAdapterMinecart(Class<?> entityClass, String name, float shadow) {
      super(entityClass, name, shadow);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelMinecart modelMinecart2)) {
         return null;
      } else {
         return modelPart.equals("bottom")
            ? modelMinecart2.sideModels[0]
            : (
               modelPart.equals("back")
                  ? modelMinecart2.sideModels[1]
                  : (
                     modelPart.equals("front")
                        ? modelMinecart2.sideModels[2]
                        : (
                           modelPart.equals("right")
                              ? modelMinecart2.sideModels[3]
                              : (modelPart.equals("left") ? modelMinecart2.sideModels[4] : (modelPart.equals("dirt") ? modelMinecart2.sideModels[5] : null))
                        )
                  )
            );
      }
   }

   public ModelAdapterMinecart() {
      super(EntityMinecart.class, "minecart", 0.5F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"bottom", "back", "front", "right", "left", "dirt"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderMinecart renderMinecart = new RenderMinecart(renderManager);
      if (!Reflector.reflectorField61.isActive()) {
         Config.warn("Field not found: RenderMinecart.modelMinecart");
         return null;
      } else {
         Reflector.checkObject4(renderMinecart, Reflector.reflectorField61, modelBase);
         renderMinecart.shadowSize = shadowSize;
         return renderMinecart;
      }
   }

   @Override
   public ModelBase makeModel() {
      return new ModelMinecart();
   }
}
