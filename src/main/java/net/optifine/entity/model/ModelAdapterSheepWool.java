package net.optifine.entity.model;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.src.Config;

public class ModelAdapterSheepWool extends ModelAdapterQuadruped {
   @Override
   public ModelBase makeModel() {
      return new ModelSheep1();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      Render render = renderManager.getEntityRenderMap().get(EntitySheep.class);
      if (!(render instanceof RenderSheep)) {
         Config.warn("Not a RenderSheep: " + render);
         return null;
      }

      if (render.getEntityClass() == null) {
         render = new RenderSheep(renderManager, new ModelSheep2(), 0.7F);
      }

      RenderSheep renderSheep = (RenderSheep)render;
      Iterator iterator2 = renderSheep.getLayerRenderers().iterator();

      while (iterator2.hasNext()) {
         LayerRenderer layerRenderer = (LayerRenderer)iterator2.next();
         if (layerRenderer instanceof LayerSheepWool) {
            iterator2.remove();
         }
      }

      LayerSheepWool layerSheepWool = new LayerSheepWool(renderSheep);
      layerSheepWool.sheepModel = (ModelSheep1)modelBase;
      renderSheep.addLayer(layerSheepWool);
      return renderSheep;
   }

   public ModelAdapterSheepWool() {
      super(EntitySheep.class, "sheep_wool", 0.7F);
   }
}
