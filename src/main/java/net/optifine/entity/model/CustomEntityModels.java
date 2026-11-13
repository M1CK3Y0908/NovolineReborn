package net.optifine.entity.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.entity.model.anim.ModelResolver;
import net.optifine.entity.model.anim.ModelUpdater;

public class CustomEntityModels {
   private static boolean active = false;
   private static Map<Class, Render> originalEntityRenderMap = null;
   private static Map<Class, TileEntitySpecialRenderer> originalTileEntityRenderMap = null;

   private static Map<Class, TileEntitySpecialRenderer> getTileEntityRenderMap() {
      Map<Class, TileEntitySpecialRenderer> map = TileEntityRendererDispatcher.instance.mapSpecialRenderers;
      if (originalTileEntityRenderMap == null) {
         originalTileEntityRenderMap = new HashMap<>(map);
      }

      return map;
   }

   private static Map<Class, Render> getEntityRenderMap() {
      Map entityRenderMap = Minecraft.getMinecraft().getRenderManager().getEntityRenderMap();
      if (entityRenderMap == null) {
         return null;
      }

      if (originalEntityRenderMap == null) {
         originalEntityRenderMap = new HashMap<>(entityRenderMap);
      }

      return entityRenderMap;
   }

   private static ResourceLocation[] getModelLocations() {
      ArrayList arrayList = new ArrayList();
      String[] modelNames = CustomModelRegistry.getModelNames();

      for (int i = 0; i < modelNames.length; i++) {
         String text = "optifine/cem/" + modelNames[i] + ".jem";
         ResourceLocation resourceLocation2 = new ResourceLocation(text);
         if (Config.hasResource(resourceLocation2)) {
            arrayList.add(resourceLocation2);
         }
      }

      return (ResourceLocation[]) arrayList.toArray(new ResourceLocation[arrayList.size()]);
   }

   private static void checkNull(Object obj, String msg) {
      if (obj == null) {
         throw new JsonParseException(msg);
      }
   }

   private static boolean modifyModel(ModelAdapter modelAdapter, ModelBase model, CustomModelRenderer[] modelRenderers, ModelResolver mr) {
      for (int index = 0; index < modelRenderers.length; index++) {
         CustomModelRenderer customModelRenderer2 = modelRenderers[index];
         if (!modifyModel(modelAdapter, model, customModelRenderer2, mr)) {
            return false;
         }
      }

      return true;
   }

   private static boolean modifyModel(ModelAdapter modelAdapter, ModelBase model, CustomModelRenderer customModelRenderer, ModelResolver modelResolver) {
      String modelPart = customModelRenderer.getModelPart();
      ModelRenderer modelRenderer = modelAdapter.getModelRenderer(model, modelPart);
      if (modelRenderer == null) {
         Config.warn("Model part not found: " + modelPart + ", model: " + model);
         return false;
      }

      if (!customModelRenderer.isAttach()) {
         if (modelRenderer.cubeList != null) {
            modelRenderer.cubeList.clear();
         }

         if (modelRenderer.spriteList != null) {
            modelRenderer.spriteList.clear();
         }

         if (modelRenderer.childModels != null) {
            ModelRenderer[] modelRenderers2 = modelAdapter.getModelRenderers(model);
            Set set = Collections.newSetFromMap(new IdentityHashMap());
            set.addAll(Arrays.asList(modelRenderers2));
            Iterator iterator2 = modelRenderer.childModels.iterator();

            while (iterator2.hasNext()) {
               ModelRenderer modelRenderer2 = (ModelRenderer)iterator2.next();
               if (!set.contains(modelRenderer2)) {
                  iterator2.remove();
               }
            }
         }
      }

      modelRenderer.addChild(customModelRenderer.getModelRenderer());
      ModelUpdater modelUpdater = customModelRenderer.getModelUpdater();
      if (modelUpdater != null) {
         modelResolver.setThisModelRenderer(customModelRenderer.getModelRenderer());
         modelResolver.setPartModelRenderer(modelRenderer);
         if (!modelUpdater.initialize(modelResolver)) {
            return false;
         }

         customModelRenderer.getModelRenderer().setModelUpdater(modelUpdater);
      }

      return true;
   }

   public static boolean isActive() {
      return active;
   }

   private static IEntityRenderer makeEntityRender(ModelAdapter modelAdapter, CustomEntityRenderer cer) {
      ResourceLocation resourceLocation = cer.getTextureLocation();
      CustomModelRenderer[] acustommodelrenderer = cer.getCustomModelRenderers();
      float shadowSize = cer.getShadowSize();
      if (shadowSize < 0.0F) {
         shadowSize = modelAdapter.getShadowSize();
      }

      ModelBase modelBase = modelAdapter.makeModel();
      if (modelBase == null) {
         return null;
      }

      ModelResolver modelResolver2 = new ModelResolver(modelAdapter, modelBase, acustommodelrenderer);
      if (!modifyModel(modelAdapter, modelBase, acustommodelrenderer, modelResolver2)) {
         return null;
      }

      IEntityRenderer entityRenderer = modelAdapter.makeEntityRender(modelBase, shadowSize);
      if (entityRenderer == null) {
         throw new JsonParseException("Entity renderer is null, model: " + modelAdapter.getName() + ", adapter: " + modelAdapter.getClass().getName());
      }

      if (resourceLocation != null) {
         entityRenderer.setLocationTextureCustom(resourceLocation);
      }

      return entityRenderer;
   }

   private static IEntityRenderer parseEntityRender(JsonObject obj, String path) {
      CustomEntityRenderer customEntityRenderer = CustomEntityModelParser.parseEntityRender(obj, path);
      String name = customEntityRenderer.getName();
      ModelAdapter modelAdapter = CustomModelRegistry.getModelAdapter(name);
      checkNull(modelAdapter, "Entity not found: " + name);
      Class oclass = modelAdapter.getEntityClass();
      checkNull(oclass, "Entity class not found: " + name);
      IEntityRenderer entityRenderer = makeEntityRender(modelAdapter, customEntityRenderer);
      if (entityRenderer == null) {
         return null;
      }

      entityRenderer.setEntityClass(oclass);
      return entityRenderer;
   }

   private static IEntityRenderer parseEntityRender(ResourceLocation location) {
      try {
         JsonObject jsonObject = CustomEntityModelParser.loadJson(location);
         return parseEntityRender(jsonObject, location.getResourcePath());
      } catch (IOException ioException) {
         Config.error(ioException.getClass().getName() + ": " + ioException.getMessage());
         return null;
      } catch (JsonParseException jsonParseException) {
         Config.error(jsonParseException.getClass().getName() + ": " + jsonParseException.getMessage());
         return null;
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static void update() {
      Map<Class, Render> map = getEntityRenderMap();
      Map<Class, TileEntitySpecialRenderer> map1 = getTileEntityRenderMap();
      if (map == null) {
         Config.warn("Entity render map not found, custom entity models are DISABLED.");
      } else if (map1 == null) {
         Config.warn("Tile entity render map not found, custom entity models are DISABLED.");
      } else {
         active = false;
         map.clear();
         map1.clear();
         map.putAll(originalEntityRenderMap);
         map1.putAll(originalTileEntityRenderMap);
         if (Config.isCustomEntityModels()) {
            ResourceLocation[] aresourcelocation = getModelLocations();

            for (int index = 0; index < aresourcelocation.length; index++) {
               ResourceLocation resourceLocation2 = aresourcelocation[index];
               Config.dbg("CustomEntityModel: " + resourceLocation2.getResourcePath());
               IEntityRenderer entityRenderer = parseEntityRender(resourceLocation2);
               if (entityRenderer != null) {
                  Class oclass = entityRenderer.getEntityClass();
                  if (oclass != null) {
                     if (entityRenderer instanceof Render) {
                        map.put(oclass, (Render)entityRenderer);
                     } else if (entityRenderer instanceof TileEntitySpecialRenderer) {
                        map1.put(oclass, (TileEntitySpecialRenderer)entityRenderer);
                     } else {
                        Config.warn("Unknown renderer type: " + entityRenderer.getClass().getName());
                     }

                     active = true;
                  }
               }
            }
         }
      }
   }
}
