package net.minecraft.client.resources.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ITransformation;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.novoline.api.IResourceHolder;
import wtf.novoline.util.Helper3;

public class ModelBakery {
   private static final Set<ResourceLocation> LOCATIONS_BUILTIN_TEXTURES = Sets.newHashSet(
      new ResourceLocation[]{
         new ResourceLocation("blocks/water_flow"),
         new ResourceLocation("blocks/water_still"),
         new ResourceLocation("blocks/lava_flow"),
         new ResourceLocation("blocks/lava_still"),
         new ResourceLocation("blocks/destroy_stage_0"),
         new ResourceLocation("blocks/destroy_stage_1"),
         new ResourceLocation("blocks/destroy_stage_2"),
         new ResourceLocation("blocks/destroy_stage_3"),
         new ResourceLocation("blocks/destroy_stage_4"),
         new ResourceLocation("blocks/destroy_stage_5"),
         new ResourceLocation("blocks/destroy_stage_6"),
         new ResourceLocation("blocks/destroy_stage_7"),
         new ResourceLocation("blocks/destroy_stage_8"),
         new ResourceLocation("blocks/destroy_stage_9"),
         new ResourceLocation("items/empty_armor_slot_helmet"),
         new ResourceLocation("items/empty_armor_slot_chestplate"),
         new ResourceLocation("items/empty_armor_slot_leggings"),
         new ResourceLocation("items/empty_armor_slot_boots")
      }
   );
   private final Map<ResourceLocation, TextureAtlasSprite> sprites = Maps.newHashMap();
   private final Map<ResourceLocation, ModelBlock> models = Maps.newLinkedHashMap();
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ModelResourceLocation, net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variants> variants = Maps.newLinkedHashMap();
   private final FaceBakery faceBakery = new FaceBakery();
   protected static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
   private final ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   private RegistrySimple<ModelResourceLocation, IBakedModel> bakedRegistry = new RegistrySimple<>();
   private Map<String, ResourceLocation> itemLocations = Maps.newLinkedHashMap();
   private final Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = Maps.newHashMap();
   private Map<Item, List<String>> variantNames = Maps.newIdentityHashMap();
   private static final Map<String, String> BUILT_IN_MODELS = Maps.newHashMap();
   private static final Joiner JOINER = Joiner.on(" -> ");
   private final IResourceManager resourceManager;
   private static final ModelBlock MODEL_GENERATED = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static final ModelBlock MODEL_COMPASS = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private final TextureMap textureMap;
   private static final ModelBlock MODEL_CLOCK = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private final BlockModelShapes blockModelShapes;
   private static final ModelBlock MODEL_ENTITY = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static Map<IResourceHolder<Item>, Set<String>> map = Maps.newHashMap();

   private Set<ResourceLocation> getVariantsTextureLocations() {
      HashSet hashSet = Sets.newHashSet();
      ArrayList arrayList = Lists.newArrayList(this.variants.keySet());
      Collections.sort(arrayList, new Comparator<ModelResourceLocation>() {
         public int compare(ModelResourceLocation left, ModelResourceLocation right) {
            return left.toString().compareTo(right.toString());
         }
      });

      for (ModelResourceLocation modelResourceLocation : (Iterable<ModelResourceLocation>) arrayList) {
         for (net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant variant2 : this.variants.get(modelResourceLocation).getVariants()) {
            ModelBlock modelBlock2 = this.models.get(variant2.getModelLocation());
            if (modelBlock2 == null) {
               LOGGER.warn("Missing model for: " + modelResourceLocation);
            } else {
               hashSet.addAll(this.getTextureLocations(modelBlock2));
            }
         }
      }

      hashSet.addAll(LOCATIONS_BUILTIN_TEXTURES);
      return hashSet;
   }

   private void loadVariantModels() {
      for (ResourceLocation resourceLocation : this.variants.keySet()) {
         Iterator iterator2 = this.variants.get(resourceLocation).getVariants().iterator();

         while (iterator2.hasNext()) {
            ResourceLocation modelLocation2 = ((net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant)iterator2.next()).getModelLocation();
            if (this.models.get(modelLocation2) == null) {
               try {
                  ModelBlock modelBlock2 = this.loadModel(modelLocation2);
                  this.models.put(modelLocation2, modelBlock2);
               } catch (Exception exception2) {
                  LOGGER.warn("Unable to load block model: '" + modelLocation2 + "' for variant: '" + resourceLocation + "'", exception2);
               }
            }
         }
      }
   }

   private ModelBlock makeItemModel(ModelBlock model) {
      return this.itemModelGenerator.makeItemModel(this.textureMap, model);
   }

   protected IBakedModel bakeModel(ModelBlock model, ITransformation transform, boolean uvLock) {
      TextureAtlasSprite textureAtlasSprite = this.sprites.get(new ResourceLocation(model.resolveTextureName("particle")));
      SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model).setTexture(textureAtlasSprite);

      for (BlockPart blockPart : model.getElements()) {
         for (EnumFacing enumFacing : blockPart.mapFaces.keySet()) {
            BlockPartFace blockPartFace = blockPart.mapFaces.get(enumFacing);
            TextureAtlasSprite textureAtlasSprite2 = this.sprites.get(new ResourceLocation(model.resolveTextureName(blockPartFace.texture)));
            boolean flag = true;
            if (Reflector.reflectorClass22.isActive()) {
               flag = Helper3.checkMatrix4f(transform.getMatrix4f());
            }

            if (blockPartFace.cullFace != null && flag) {
               builder.addFaceQuad(transform.rotate(blockPartFace.cullFace), this.makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite2, enumFacing, transform, uvLock));
            } else {
               builder.addGeneralQuad(this.makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite2, enumFacing, transform, uvLock));
            }
         }
      }

      return builder.makeBakedModel();
   }

   public IBakedModel bakeModel(ModelBlock modelBlockIn, ModelRotation modelRotationIn, boolean uvLocked) {
      return this.bakeModel(modelBlockIn, (ITransformation)modelRotationIn, uvLocked);
   }

   private Set<ResourceLocation> getTextureLocations(ModelBlock model) {
      HashSet hashSet = Sets.newHashSet();
      Iterator iterator2 = model.getElements().iterator();

      while (iterator2.hasNext()) {
         for (BlockPartFace blockPartFace : ((BlockPart)iterator2.next()).mapFaces.values()) {
            ResourceLocation resourceLocation4 = new ResourceLocation(model.resolveTextureName(blockPartFace.texture));
            hashSet.add(resourceLocation4);
         }
      }

      hashSet.add(new ResourceLocation(model.resolveTextureName("particle")));
      return hashSet;
   }

   private Set<ResourceLocation> getItemsTextureLocations() {
      HashSet hashSet = Sets.newHashSet();

      for (ResourceLocation resourceLocation4 : this.itemLocations.values()) {
         ModelBlock modelBlock2 = this.models.get(resourceLocation4);
         if (modelBlock2 != null) {
            hashSet.add(new ResourceLocation(modelBlock2.resolveTextureName("particle")));
            if (this.hasItemModel(modelBlock2)) {
               for (String text2 : ItemModelGenerator.LAYERS) {
                  ResourceLocation resourceLocation5 = new ResourceLocation(modelBlock2.resolveTextureName(text2));
                  if (modelBlock2.getRootModel() == MODEL_COMPASS && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourceLocation5)) {
                     TextureAtlasSprite.setLocationNameCompass(resourceLocation5.toString());
                  } else if (modelBlock2.getRootModel() == MODEL_CLOCK && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourceLocation5)) {
                     TextureAtlasSprite.setLocationNameClock(resourceLocation5.toString());
                  }

                  hashSet.add(resourceLocation5);
               }
            } else if (!this.isCustomRenderer(modelBlock2)) {
               for (BlockPart blockPart : modelBlock2.getElements()) {
                  for (BlockPartFace blockPartFace : blockPart.mapFaces.values()) {
                     ResourceLocation resourceLocation6 = new ResourceLocation(modelBlock2.resolveTextureName(blockPartFace.texture));
                     hashSet.add(resourceLocation6);
                  }
               }
            }
         }
      }

      return hashSet;
   }

   private ResourceLocation getBlockStateLocation(ResourceLocation location) {
      return new ResourceLocation(location.getResourceDomain(), "blockstates/" + location.getResourcePath() + ".json");
   }

   private void registerVariant(ModelBlockDefinition definition, ModelResourceLocation modelLocation) {
      this.variants.put(modelLocation, definition.getVariants(modelLocation.getVariant()));
   }

   private ModelBlock loadModel(ResourceLocation modelLocation) throws IOException {
      String resourcePath2 = modelLocation.getResourcePath();
      if ("builtin/generated".equals(resourcePath2)) {
         return MODEL_GENERATED;
      }

      if ("builtin/compass".equals(resourcePath2)) {
         return MODEL_COMPASS;
      }

      if ("builtin/clock".equals(resourcePath2)) {
         return MODEL_CLOCK;
      }

      if ("builtin/entity".equals(resourcePath2)) {
         return MODEL_ENTITY;
      }

      Reader reader;
      if (resourcePath2.startsWith("builtin/")) {
         String text2 = resourcePath2.substring("builtin/".length());
         String text3 = BUILT_IN_MODELS.get(text2);
         if (text3 == null) {
            throw new FileNotFoundException(modelLocation.toString());
         }

         reader = new StringReader(text3);
      } else {
         modelLocation = this.getModelLocation(modelLocation);
         IResource resource = this.resourceManager.getResource(modelLocation);
         reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
      }

      ModelBlock modelBlock2;
      try {
         ModelBlock modelBlock3 = ModelBlock.deserialize(reader);
         modelBlock3.name = modelLocation.toString();
         modelBlock2 = modelBlock3;
         String basePath2 = TextureUtils.getBasePath(modelLocation.getResourcePath());
         fixModelLocations(modelBlock3, basePath2);
      } finally {
         reader.close();
      }

      return modelBlock2;
   }

   private List<String> getVariantNames(Item item) {
      List<String> list = this.variantNames.get(item);
      if (list == null) {
         list = Collections.singletonList(Item.itemRegistry.getNameForObject(item).toString());
      }

      return list;
   }

   public void loadItemModel(String itemName, ResourceLocation modelLocation, ResourceLocation itemLocation) {
      this.itemLocations.put(itemName, modelLocation);
      if (this.models.get(modelLocation) == null) {
         try {
            ModelBlock modelBlock = this.loadModel(modelLocation);
            this.models.put(modelLocation, modelBlock);
         } catch (Exception exception2) {
            LOGGER.warn("Unable to load item model: '{}' for item: '{}'", modelLocation, itemLocation);
            LOGGER.warn(exception2.getClass().getName() + ": " + exception2.getMessage());
         }
      }
   }

   protected BakedQuad makeBakedQuad(
      BlockPart part, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ITransformation transform, boolean uvLock
   ) {
      return this.faceBakery.makeBakedQuad(part.positionFrom, part.positionTo, face, sprite, facing, transform, part.partRotation, uvLock, part.shade);
   }

   private void bakeItemModels() {
      for (ResourceLocation resourceLocation4 : this.itemLocations.values()) {
         ModelBlock modelBlock2 = this.models.get(resourceLocation4);
         if (this.hasItemModel(modelBlock2)) {
            ModelBlock modelBlock3 = this.makeItemModel(modelBlock2);
            if (modelBlock3 != null) {
               modelBlock3.name = resourceLocation4.toString();
            }

            this.models.put(resourceLocation4, modelBlock3);
         } else if (this.isCustomRenderer(modelBlock2)) {
            this.models.put(resourceLocation4, modelBlock2);
         }
      }

      for (TextureAtlasSprite textureAtlasSprite : this.sprites.values()) {
         if (!textureAtlasSprite.hasAnimationMetadata()) {
            textureAtlasSprite.clearFramesTextureData();
         }
      }
   }

   private void loadSprites() {
      final Set<ResourceLocation> set = this.getVariantsTextureLocations();
      set.addAll(this.getItemsTextureLocations());
      set.remove(TextureMap.LOCATION_MISSING_TEXTURE);
      IIconCreator iIconCreator = new IIconCreator() {
         @Override
         public void registerSprites(TextureMap iconRegistry) {
            for (ResourceLocation resourceLocation4 : set) {
               TextureAtlasSprite textureAtlasSprite = iconRegistry.registerSprite(resourceLocation4);
               ModelBakery.this.sprites.put(resourceLocation4, textureAtlasSprite);
            }
         }
      };
      this.textureMap.loadSprites(this.resourceManager, iIconCreator);
      this.sprites.put(new ResourceLocation("missingno"), this.textureMap.getMissingSprite());
   }

   private boolean hasItemModel(ModelBlock model) {
      if (model == null) {
         return false;
      }

      ModelBlock modelBlock = model.getRootModel();
      return modelBlock == MODEL_GENERATED || modelBlock == MODEL_COMPASS || modelBlock == MODEL_CLOCK;
   }

   private ResourceLocation getItemLocation(String itemModelName) {
      ResourceLocation resourceLocation4 = new ResourceLocation(itemModelName);
      if (Reflector.reflectorClass22.isActive()) {
         resourceLocation4 = new ResourceLocation(itemModelName.replaceAll("#.*", ""));
      }

      return new ResourceLocation(resourceLocation4.getResourceDomain(), "item/" + resourceLocation4.getResourcePath());
   }

   private void loadModelsCheck() {
      this.loadModels();
      Iterator iterator2 = this.models.values().iterator();

      while (iterator2.hasNext()) {
         ((ModelBlock)iterator2.next()).getParentFromMap(this.models);
      }

      ModelBlock.checkModelHierarchy(this.models);
   }

   public ModelBlock getModelBlock(ResourceLocation location) {
      return this.models.get(location);
   }

   public ModelBakery(IResourceManager resourceManager, TextureMap textureMap, BlockModelShapes blockModelShapes) {
      this.resourceManager = resourceManager;
      this.textureMap = textureMap;
      this.blockModelShapes = blockModelShapes;
   }

   static {
      BUILT_IN_MODELS.put(
         "missing",
         "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}"
      );
      MODEL_GENERATED.name = "generation marker";
      MODEL_COMPASS.name = "compass generation marker";
      MODEL_CLOCK.name = "class generation marker";
      MODEL_ENTITY.name = "block entity marker";
   }

   private ResourceLocation getModelLocation(ResourceLocation location) {
      ResourceLocation location2 = location;
      String resourcePath = location.getResourcePath();
      if (!resourcePath.startsWith("mcpatcher") && !resourcePath.startsWith("optifine")) {
         return new ResourceLocation(location.getResourceDomain(), "models/" + location.getResourcePath() + ".json");
      }

      if (!resourcePath.endsWith(".json")) {
         location2 = new ResourceLocation(location.getResourceDomain(), resourcePath + ".json");
      }

      return location2;
   }

   private void loadVariantItemModels() {
      this.loadVariants(this.blockModelShapes.getBlockStateMapper().putAllStateModelLocations().values());
      this.variants
         .put(
            MODEL_MISSING,
            new net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variants(
               MODEL_MISSING.getVariant(),
               Lists.newArrayList(
                  new net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant[]{
                     new net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant(new ResourceLocation(MODEL_MISSING.getResourcePath()), ModelRotation.X0_Y0, false, 1)
                  }
               )
            )
         );
      ResourceLocation resourceLocation4 = new ResourceLocation("item_frame");
      ModelBlockDefinition modelBlockDefinition = this.getModelBlockDefinition(resourceLocation4);
      this.registerVariant(modelBlockDefinition, new ModelResourceLocation(resourceLocation4, "normal"));
      this.registerVariant(modelBlockDefinition, new ModelResourceLocation(resourceLocation4, "map"));
      this.loadVariantModels();
      this.loadItemModels();
   }

   public static ResourceLocation fixModelLocation(ResourceLocation location, String basePath) {
      if (location != null && basePath != null) {
         if (!location.getResourceDomain().equals("minecraft")) {
            return location;
         }

         String resourcePath = location.getResourcePath();
         String text = fixResourcePath(resourcePath, basePath);
         if (text != resourcePath) {
            location = new ResourceLocation(location.getResourceDomain(), text);
         }

         return location;
      } else {
         return location;
      }
   }

   private ModelBlockDefinition getModelBlockDefinition(ResourceLocation location) {
      ResourceLocation blockStateLocation = this.getBlockStateLocation(location);
      ModelBlockDefinition modelBlockDefinition2 = this.blockDefinitions.get(blockStateLocation);
      if (modelBlockDefinition2 == null) {
         ArrayList<ModelBlockDefinition> arrayList = Lists.newArrayList();

         try {
            for (IResource iResource : this.resourceManager.getAllResources(blockStateLocation)) {
               InputStream inputStream = null;

               try {
                  inputStream = iResource.getInputStream();
                  ModelBlockDefinition modelBlockDefinition3 = ModelBlockDefinition.parseFromReader(new InputStreamReader(inputStream, Charsets.UTF_8));
                  arrayList.add(modelBlockDefinition3);
               } catch (Exception exception2) {
                  throw new RuntimeException(
                     "Encountered an exception when loading model definition of '"
                        + location
                        + "' from: '"
                        + iResource.getResourceLocation()
                        + "' in resourcepack: '"
                        + iResource.getResourcePackName()
                        + "'",
                     exception2
                  );
               } finally {
                  IOUtils.closeQuietly(inputStream);
               }
            }
         } catch (IOException ioException) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + blockStateLocation.toString(), ioException);
         }

         modelBlockDefinition2 = new ModelBlockDefinition(arrayList);
         this.blockDefinitions.put(blockStateLocation, modelBlockDefinition2);
      }

      return modelBlockDefinition2;
   }

   private List<ResourceLocation> getParentPath(ResourceLocation modelLocation) {
      List<ResourceLocation> list = Lists.newArrayList(new ResourceLocation[]{modelLocation});
      ResourceLocation modelLocation2 = modelLocation;

      while ((modelLocation2 = this.getParentLocation(modelLocation2)) != null) {
         list.add(0, modelLocation2);
      }

      return list;
   }

   private static String fixResourcePath(String resourcePath, String basePath) {
      resourcePath = TextureUtils.fixResourcePath(resourcePath, basePath);
      resourcePath = StrUtils.removeSuffix(resourcePath, ".json");
      return StrUtils.removeSuffix(resourcePath, ".png");
   }

   public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry() {
      this.loadVariantItemModels();
      this.loadModelsCheck();
      this.loadSprites();
      this.bakeItemModels();
      this.bakeBlockModels();
      return this.bakedRegistry;
   }

   private ResourceLocation getParentLocation(ResourceLocation location) {
      for (Entry entry : this.models.entrySet()) {
         ModelBlock modelBlock2 = (ModelBlock)entry.getValue();
         if (modelBlock2 != null && location.equals(modelBlock2.getParentLocation())) {
            return (ResourceLocation)entry.getKey();
         }
      }

      return null;
   }

   private void bakeBlockModels() {
      for (ModelResourceLocation modelResourceLocation : this.variants.keySet()) {
         WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
         int i = 0;

         for (net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant variant2 : this.variants.get(modelResourceLocation).getVariants()) {
            ModelBlock modelBlock2 = this.models.get(variant2.getModelLocation());
            if (modelBlock2 != null && modelBlock2.isResolved()) {
               i++;
               builder.add(this.bakeModel(modelBlock2, variant2.getRotation(), variant2.isUvLocked()), variant2.getWeight());
            } else {
               LOGGER.warn("Missing model for: " + modelResourceLocation);
            }
         }

         if (i == 0) {
            LOGGER.warn("No weighted models for: " + modelResourceLocation);
         } else if (i == 1) {
            this.bakedRegistry.putObject(modelResourceLocation, builder.first());
         } else {
            this.bakedRegistry.putObject(modelResourceLocation, builder.build());
         }
      }

      for (Entry entry : this.itemLocations.entrySet()) {
         ResourceLocation resourceLocation4 = (ResourceLocation)entry.getValue();
         ModelResourceLocation modelResourceLocation2 = new ModelResourceLocation((String)entry.getKey(), "inventory");
         if (Reflector.reflectorMethod104.isActive()) {
            modelResourceLocation2 = (ModelResourceLocation)Reflector.createObject2(Reflector.reflectorMethod104, entry.getKey());
         }

         ModelBlock modelBlock3 = this.models.get(resourceLocation4);
         if (modelBlock3 == null || !modelBlock3.isResolved()) {
            LOGGER.warn("Missing model for: " + resourceLocation4);
         } else if (this.isCustomRenderer(modelBlock3)) {
            this.bakedRegistry.putObject(modelResourceLocation2, new BuiltInModel(modelBlock3.getAllTransforms()));
         } else {
            this.bakedRegistry.putObject(modelResourceLocation2, this.bakeModel(modelBlock3, ModelRotation.X0_Y0, false));
         }
      }
   }

   private void loadVariants(Collection<ModelResourceLocation> modelLocations) {
      for (ModelResourceLocation modelResourceLocation : modelLocations) {
         try {
            ModelBlockDefinition modelBlockDefinition2 = this.getModelBlockDefinition(modelResourceLocation);

            try {
               this.registerVariant(modelBlockDefinition2, modelResourceLocation);
            } catch (Exception exception2) {
               LOGGER.warn("Unable to load variant: " + modelResourceLocation.getVariant() + " from " + modelResourceLocation, exception2);
            }
         } catch (Exception exception3) {
            LOGGER.warn("Unable to load definition " + modelResourceLocation, exception3);
         }
      }
   }

   private void loadItemModels() {
      this.registerVariantNames();

      for (Item item2 : Item.itemRegistry) {
         for (String text2 : this.getVariantNames(item2)) {
            ResourceLocation itemLocation2 = this.getItemLocation(text2);
            this.itemLocations.put(text2, itemLocation2);
            if (this.models.get(itemLocation2) == null) {
               try {
                  ModelBlock modelBlock2 = this.loadModel(itemLocation2);
                  this.models.put(itemLocation2, modelBlock2);
               } catch (Exception exception2) {
                  LOGGER.warn("Unable to load item model: '" + itemLocation2 + "' for item: '" + Item.itemRegistry.getNameForObject(item2) + "'", exception2);
               }
            }
         }
      }
   }

   private boolean isCustomRenderer(ModelBlock model) {
      return model == null ? false : model.getRootModel() == MODEL_ENTITY;
   }

   public static void fixModelLocations(ModelBlock model, String basePath) {
      ResourceLocation resourceLocation4 = fixModelLocation(model.getParentLocation(), basePath);
      if (resourceLocation4 != model.getParentLocation()) {
         Reflector.checkObject4(model, Reflector.reflectorField36, resourceLocation4);
      }

      Map map2 = (Map)Reflector.createObject6(model, Reflector.reflectorField37);
      if (map2 != null) {
         for (Entry entry : (Iterable<Map.Entry>) map2.entrySet()) {
            String text2 = (String)entry.getValue();
            String text3 = fixResourcePath(text2, basePath);
            if (text3 != text2) {
               entry.setValue(text3);
            }
         }
      }
   }

   private void registerVariantNames() {
      this.variantNames.clear();
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stone),
            Lists.newArrayList(new String[]{"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"})
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.dirt), Lists.newArrayList(new String[]{"dirt", "coarse_dirt", "podzol"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.planks),
            Lists.newArrayList(new String[]{"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"})
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.sapling),
            Lists.newArrayList(new String[]{"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"})
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.sand), Lists.newArrayList(new String[]{"sand", "red_sand"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log), Lists.newArrayList(new String[]{"oak_log", "spruce_log", "birch_log", "jungle_log"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.leaves), Lists.newArrayList(new String[]{"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sponge), Lists.newArrayList(new String[]{"sponge", "sponge_wet"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sandstone), Lists.newArrayList(new String[]{"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.red_sandstone), Lists.newArrayList(new String[]{"red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.tallgrass), Lists.newArrayList(new String[]{"dead_bush", "tall_grass", "fern"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.deadbush), Lists.newArrayList(new String[]{"dead_bush"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.wool),
            Lists.newArrayList(
               new String[]{
                  "black_wool",
                  "red_wool",
                  "green_wool",
                  "brown_wool",
                  "blue_wool",
                  "purple_wool",
                  "cyan_wool",
                  "silver_wool",
                  "gray_wool",
                  "pink_wool",
                  "lime_wool",
                  "yellow_wool",
                  "light_blue_wool",
                  "magenta_wool",
                  "orange_wool",
                  "white_wool"
               }
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.yellow_flower), Lists.newArrayList(new String[]{"dandelion"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.red_flower),
            Lists.newArrayList(
               new String[]{"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stone_slab),
            Lists.newArrayList(
               new String[]{"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"}
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.stone_slab2), Lists.newArrayList(new String[]{"red_sandstone_slab"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_glass),
            Lists.newArrayList(
               new String[]{
                  "black_stained_glass",
                  "red_stained_glass",
                  "green_stained_glass",
                  "brown_stained_glass",
                  "blue_stained_glass",
                  "purple_stained_glass",
                  "cyan_stained_glass",
                  "silver_stained_glass",
                  "gray_stained_glass",
                  "pink_stained_glass",
                  "lime_stained_glass",
                  "yellow_stained_glass",
                  "light_blue_stained_glass",
                  "magenta_stained_glass",
                  "orange_stained_glass",
                  "white_stained_glass"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.monster_egg),
            Lists.newArrayList(
               new String[]{
                  "stone_monster_egg",
                  "cobblestone_monster_egg",
                  "stone_brick_monster_egg",
                  "mossy_brick_monster_egg",
                  "cracked_brick_monster_egg",
                  "chiseled_brick_monster_egg"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stonebrick),
            Lists.newArrayList(new String[]{"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"})
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.wooden_slab),
            Lists.newArrayList(new String[]{"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"})
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.cobblestone_wall), Lists.newArrayList(new String[]{"cobblestone_wall", "mossy_cobblestone_wall"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.anvil), Lists.newArrayList(new String[]{"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.quartz_block), Lists.newArrayList(new String[]{"quartz_block", "chiseled_quartz_block", "quartz_column"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_hardened_clay),
            Lists.newArrayList(
               new String[]{
                  "black_stained_hardened_clay",
                  "red_stained_hardened_clay",
                  "green_stained_hardened_clay",
                  "brown_stained_hardened_clay",
                  "blue_stained_hardened_clay",
                  "purple_stained_hardened_clay",
                  "cyan_stained_hardened_clay",
                  "silver_stained_hardened_clay",
                  "gray_stained_hardened_clay",
                  "pink_stained_hardened_clay",
                  "lime_stained_hardened_clay",
                  "yellow_stained_hardened_clay",
                  "light_blue_stained_hardened_clay",
                  "magenta_stained_hardened_clay",
                  "orange_stained_hardened_clay",
                  "white_stained_hardened_clay"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_glass_pane),
            Lists.newArrayList(
               new String[]{
                  "black_stained_glass_pane",
                  "red_stained_glass_pane",
                  "green_stained_glass_pane",
                  "brown_stained_glass_pane",
                  "blue_stained_glass_pane",
                  "purple_stained_glass_pane",
                  "cyan_stained_glass_pane",
                  "silver_stained_glass_pane",
                  "gray_stained_glass_pane",
                  "pink_stained_glass_pane",
                  "lime_stained_glass_pane",
                  "yellow_stained_glass_pane",
                  "light_blue_stained_glass_pane",
                  "magenta_stained_glass_pane",
                  "orange_stained_glass_pane",
                  "white_stained_glass_pane"
               }
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.leaves2), Lists.newArrayList(new String[]{"acacia_leaves", "dark_oak_leaves"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log2), Lists.newArrayList(new String[]{"acacia_log", "dark_oak_log"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.prismarine), Lists.newArrayList(new String[]{"prismarine", "prismarine_bricks", "dark_prismarine"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.carpet),
            Lists.newArrayList(
               new String[]{
                  "black_carpet",
                  "red_carpet",
                  "green_carpet",
                  "brown_carpet",
                  "blue_carpet",
                  "purple_carpet",
                  "cyan_carpet",
                  "silver_carpet",
                  "gray_carpet",
                  "pink_carpet",
                  "lime_carpet",
                  "yellow_carpet",
                  "light_blue_carpet",
                  "magenta_carpet",
                  "orange_carpet",
                  "white_carpet"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.double_plant),
            Lists.newArrayList(new String[]{"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"})
         );
      this.variantNames.put(Items.bow, Lists.newArrayList(new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
      this.variantNames.put(Items.coal, Lists.newArrayList(new String[]{"coal", "charcoal"}));
      this.variantNames.put(Items.fishing_rod, Lists.newArrayList(new String[]{"fishing_rod", "fishing_rod_cast"}));
      this.variantNames.put(Items.fish, Lists.newArrayList(new String[]{"cod", "salmon", "clownfish", "pufferfish"}));
      this.variantNames.put(Items.cooked_fish, Lists.newArrayList(new String[]{"cooked_cod", "cooked_salmon"}));
      this.variantNames
         .put(
            Items.dye,
            Lists.newArrayList(
               new String[]{
                  "dye_black",
                  "dye_red",
                  "dye_green",
                  "dye_brown",
                  "dye_blue",
                  "dye_purple",
                  "dye_cyan",
                  "dye_silver",
                  "dye_gray",
                  "dye_pink",
                  "dye_lime",
                  "dye_yellow",
                  "dye_light_blue",
                  "dye_magenta",
                  "dye_orange",
                  "dye_white"
               }
            )
         );
      this.variantNames.put(Items.potionitem, Lists.newArrayList(new String[]{"bottle_drinkable", "bottle_splash"}));
      this.variantNames.put(Items.skull, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence_gate), Lists.newArrayList(new String[]{"oak_fence_gate"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence), Lists.newArrayList(new String[]{"oak_fence"}));
      this.variantNames.put(Items.oak_door, Lists.newArrayList(new String[]{"oak_door"}));

      for (Entry entry : map.entrySet()) {
         this.variantNames.put((Item)((IResourceHolder)entry.getKey()).getObject(), Lists.newArrayList(((Set)entry.getValue()).iterator()));
      }

      CustomItems.update();
      CustomItems.loadModels(this);
   }

   private void loadModels() {
      Deque<ResourceLocation> deque = Queues.newArrayDeque();
      Set<ResourceLocation> set = Sets.newHashSet();

      for (ResourceLocation resourceLocation2 : this.models.keySet()) {
         set.add(resourceLocation2);
         ResourceLocation resourceLocation3 = this.models.get(resourceLocation2).getParentLocation();
         if (resourceLocation3 != null) {
            deque.add(resourceLocation3);
         }
      }

      while (!deque.isEmpty()) {
         ResourceLocation resourceLocation4 = deque.pop();

         try {
            if (this.models.get(resourceLocation4) != null) {
               continue;
            }

            ModelBlock modelBlock2 = this.loadModel(resourceLocation4);
            this.models.put(resourceLocation4, modelBlock2);
            ResourceLocation parentLocation = modelBlock2.getParentLocation();
            if (parentLocation != null && !set.contains(parentLocation)) {
               deque.add(parentLocation);
            }
         } catch (Exception exception2) {
            LOGGER.warn("In parent chain: " + JOINER.join(this.getParentPath(resourceLocation4)) + "; unable to load model: '" + resourceLocation4 + "'");
         }

         set.add(resourceLocation4);
      }
   }
}
