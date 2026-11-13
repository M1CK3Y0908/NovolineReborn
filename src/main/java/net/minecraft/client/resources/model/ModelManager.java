package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.IRegistry;

public class ModelManager implements IResourceManagerReloadListener {
   private final TextureMap texMap;
   private IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;
   private final BlockModelShapes modelProvider;
   private IBakedModel defaultModel;

   public IBakedModel getMissingModel() {
      return this.defaultModel;
   }

   public IBakedModel getModel(ModelResourceLocation modelLocation) {
      if (modelLocation == null) {
         return this.defaultModel;
      }

      IBakedModel bakedModel = this.modelRegistry.getObject(modelLocation);
      return bakedModel == null ? this.defaultModel : bakedModel;
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      ModelBakery modelBakery = new ModelBakery(resourceManager, this.texMap, this.modelProvider);
      this.modelRegistry = modelBakery.setupModelRegistry();
      this.defaultModel = this.modelRegistry.getObject(ModelBakery.MODEL_MISSING);
      this.modelProvider.reloadModels();
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.modelProvider;
   }

   public ModelManager(TextureMap textures) {
      this.texMap = textures;
      this.modelProvider = new BlockModelShapes(this);
   }

   public TextureMap getTextureMap() {
      return this.texMap;
   }
}
