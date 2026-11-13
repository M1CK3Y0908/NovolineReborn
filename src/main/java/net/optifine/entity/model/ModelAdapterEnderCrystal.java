package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEnderCrystal extends ModelAdapter {
   @Override
   public ModelBase makeModel() {
      return new ModelEnderCrystal(0.0F);
   }

   public ModelAdapterEnderCrystal() {
      this("end_crystal");
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderMap().get(EntityEnderCrystal.class);
      if (!(render instanceof RenderEnderCrystal renderEnderCrystal)) {
         Config.warn("Not an instance of RenderEnderCrystal: " + render);
         return null;
      } else if (!Reflector.reflectorField38.isActive()) {
         Config.warn("Field not found: RenderEnderCrystal.modelEnderCrystal");
         return null;
      } else {
         Reflector.checkObject4(renderEnderCrystal, Reflector.reflectorField38, modelBase);
         renderEnderCrystal.shadowSize = shadowSize;
         return renderEnderCrystal;
      }
   }

   protected ModelAdapterEnderCrystal(String name) {
      super(EntityEnderCrystal.class, name, 0.5F);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"cube", "glass", "base"};
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelEnderCrystal modelEnderCrystal2)) {
         return null;
      } else {
         return modelPart.equals("cube")
            ? (ModelRenderer)Reflector.createObject3(modelEnderCrystal2, Reflector.reflectorFields3, 0)
            : (
               modelPart.equals("glass")
                  ? (ModelRenderer)Reflector.createObject3(modelEnderCrystal2, Reflector.reflectorFields3, 1)
                  : (modelPart.equals("base") ? (ModelRenderer)Reflector.createObject3(modelEnderCrystal2, Reflector.reflectorFields3, 2) : null)
            );
      }
   }
}
