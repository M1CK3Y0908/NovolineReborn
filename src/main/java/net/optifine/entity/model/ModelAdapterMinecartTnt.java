package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTntMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecartTnt extends ModelAdapterMinecart {
   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderTntMinecart renderTntMinecart = new RenderTntMinecart(renderManager);
      if (!Reflector.reflectorField61.isActive()) {
         Config.warn("Field not found: RenderMinecart.modelMinecart");
         return null;
      } else {
         Reflector.checkObject4(renderTntMinecart, Reflector.reflectorField61, modelBase);
         renderTntMinecart.shadowSize = shadowSize;
         return renderTntMinecart;
      }
   }

   public ModelAdapterMinecartTnt() {
      super(EntityMinecartTNT.class, "tnt_minecart", 0.5F);
   }
}
