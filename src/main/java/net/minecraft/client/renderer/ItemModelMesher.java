package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ISmartItemModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;

public class ItemModelMesher {
   private final Map<Integer, ModelResourceLocation> simpleShapes = Maps.newHashMap();
   private final Map<Integer, IBakedModel> simpleShapesCache = Maps.newHashMap();
   private final Map<Item, ItemMeshDefinition> shapers = Maps.newHashMap();
   private final ModelManager modelManager;

   public void rebuildCache() {
      this.simpleShapesCache.clear();

      for (Entry entry : this.simpleShapes.entrySet()) {
         this.simpleShapesCache.put((Integer)entry.getKey(), this.modelManager.getModel((ModelResourceLocation)entry.getValue()));
      }
   }

   public void register(Item item, int meta, ModelResourceLocation location) {
      this.simpleShapes.put(this.getIndex(item, meta), location);
      this.simpleShapesCache.put(this.getIndex(item, meta), this.modelManager.getModel(location));
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   protected IBakedModel getItemModel(Item item, int meta) {
      return this.simpleShapesCache.get(this.getIndex(item, meta));
   }

   public IBakedModel getItemModel(ItemStack stack) {
      Item item = stack.getItem();
      IBakedModel bakedModel = this.getItemModel(item, this.getMetadata(stack));
      if (bakedModel == null) {
         ItemMeshDefinition itemMeshDefinition = this.shapers.get(item);
         if (itemMeshDefinition != null) {
            bakedModel = this.modelManager.getModel(itemMeshDefinition.getModelLocation(stack));
         }
      }

      if (Reflector.reflectorClass22.isActive() && bakedModel instanceof ISmartItemModel) {
         bakedModel = ((ISmartItemModel)bakedModel).getIBakedModel(stack);
      }

      if (bakedModel == null) {
         bakedModel = this.modelManager.getMissingModel();
      }

      if (Config.isCustomItems()) {
         bakedModel = CustomItems.getCustomItemModel(stack, bakedModel, (ResourceLocation)null, true);
      }

      return bakedModel;
   }

   public ItemModelMesher(ModelManager modelManager) {
      this.modelManager = modelManager;
   }

   protected int getMetadata(ItemStack stack) {
      return stack.isItemStackDamageable() ? 0 : stack.getMetadata();
   }

   private int getIndex(Item item, int meta) {
      return Item.getIdFromItem(item) << 16 | meta;
   }

   public void register(Item item, ItemMeshDefinition definition) {
      this.shapers.put(item, definition);
   }

   public TextureAtlasSprite getParticleIcon(Item item, int meta) {
      return this.getItemModel(new ItemStack(item, 1, meta)).getParticleTexture();
   }

   public TextureAtlasSprite getParticleIcon(Item item) {
      return this.getParticleIcon(item, 0);
   }
}
