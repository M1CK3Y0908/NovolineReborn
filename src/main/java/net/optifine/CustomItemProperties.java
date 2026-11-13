package net.optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.IntGetter;
import net.optifine.config.NbtTagValue;
import net.optifine.config.ParserEnchantmentId;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.reflect.Reflector;
import net.optifine.render.Blender;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties {
   public String name = null;
   public static final int intValue = 1;
   public String basePath = null;
   public int type = 1;
   public static final int intValue2 = 2;
   public int[] items = null;
   public String texture = null;
   public Map<String, String> mapTextures = null;
   public String model = null;
   public Map<String, String> mapModels = null;
   public RangeListInt damage = null;
   public boolean damagePercent = false;
   public int damageMask = 0;
   public RangeListInt stackSize = null;
   public RangeListInt enchantmentIds = null;
   public static final int intValue3 = 0;
   public RangeListInt enchantmentLevels = null;
   public NbtTagValue[] nbtTagValues = null;
   public static final int intValue4 = 0;
   public int hand = 0;
   public int blend = 1;
   public float speed = 0.0F;
   public float rotation = 0.0F;
   public static final String INVENTORY = "inventory";
   public int layer = 0;
   public float duration = 1.0F;
   public int weight = 0;
   public ResourceLocation textureLocation = null;
   public Map mapTextureLocations = null;
   public TextureAtlasSprite sprite = null;
   public Map mapSprites = null;
   public static final int intValue5 = 2;
   public IBakedModel bakedModelTexture = null;
   public Map<String, IBakedModel> mapBakedModelsTexture = null;
   public IBakedModel bakedModelFull = null;
   public static final int intValue6 = 1;
   public Map<String, IBakedModel> mapBakedModelsFull = null;
   public static final int TYPE_ARMOR = 3;
   private int textureWidth = 0;
   private int textureHeight = 0;

   private static Map getMatchingProperties(Properties props, String keyPrefix) {
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (String text : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         String propertyValue = props.getProperty(text);
         if (text.startsWith(keyPrefix)) {
            linkedHashMap.put(text, propertyValue);
         }
      }

      return linkedHashMap;
   }

   private boolean isUseTint() {
      return true;
   }

   public void loadModels(ModelBakery modelBakery) {
      if (this.model != null) {
         loadItemModel(modelBakery, this.model);
      }

      if (this.type == 1 && this.mapModels != null) {
         for (String text3 : this.mapModels.keySet()) {
            String text4 = this.mapModels.get(text3);
            String text5 = StrUtils.removePrefix(text3, "model.");
            if (text5.startsWith("bow") || text5.startsWith("fishing_rod") || text5.startsWith("shield")) {
               loadItemModel(modelBakery, text4);
            }
         }
      }
   }

   private ResourceLocation getSpriteLocation(ResourceLocation resLoc) {
      String resourcePath2 = resLoc.getResourcePath();
      resourcePath2 = StrUtils.removePrefix(resourcePath2, "textures/");
      resourcePath2 = StrUtils.removeSuffix(resourcePath2, ".png");
      return new ResourceLocation(resLoc.getResourceDomain(), resourcePath2);
   }

   private static String parseName(String path) {
      String text = path;
      int i = path.lastIndexOf(47);
      if (i >= 0) {
         text = path.substring(i + 1);
      }

      int i2 = text.lastIndexOf(46);
      if (i2 >= 0) {
         text = text.substring(0, i2);
      }

      return text;
   }

   private int[] detectItems() {
      Item item = Item.getByNameOrId(this.name);
      if (item == null) {
         return null;
      }

      int idFromItem = Item.getIdFromItem(item);
      return idFromItem <= 0 ? null : new int[]{idFromItem};
   }

   public void updateIcons(TextureMap textureMap) {
      if (this.texture != null) {
         this.textureLocation = this.getTextureLocation(this.texture);
         if (this.type == 1) {
            ResourceLocation spriteLocation = this.getSpriteLocation(this.textureLocation);
            this.sprite = textureMap.registerSprite(spriteLocation);
         }
      }

      if (this.mapTextures != null) {
         this.mapTextureLocations = new HashMap();
         this.mapSprites = new HashMap();

         for (String text3 : this.mapTextures.keySet()) {
            String text4 = this.mapTextures.get(text3);
            ResourceLocation textureLocation2 = this.getTextureLocation(text4);
            this.mapTextureLocations.put(text3, textureLocation2);
            if (this.type == 1) {
               ResourceLocation spriteLocation2 = this.getSpriteLocation(textureLocation2);
               TextureAtlasSprite textureAtlasSprite = textureMap.registerSprite(spriteLocation2);
               this.mapSprites.put(text3, textureAtlasSprite);
            }
         }
      }
   }

   private static String parseModel(String modelStr, String path, String basePath, int type, Map<String, String> mapModelNames) {
      if (modelStr != null) {
         if (modelStr.endsWith(".json")) {
            modelStr = modelStr.substring(0, modelStr.length() - ".json".length());
         }

         return fixModelName(modelStr, basePath);
      } else {
         if (type == 3) {
            return null;
         }

         if (mapModelNames != null) {
            String text3 = mapModelNames.get("model.bow_standby");
            if (text3 != null) {
               return text3;
            }
         }

         return modelStr;
      }
   }

   private ResourceLocation getTextureLocation(String texName) {
      if (texName == null) {
         return null;
      }

      ResourceLocation resourceLocation = new ResourceLocation(texName);
      String resourceDomain = resourceLocation.getResourceDomain();
      String resourcePath2 = resourceLocation.getResourcePath();
      if (!resourcePath2.contains("/")) {
         resourcePath2 = "textures/items/" + resourcePath2;
      }

      String text3 = resourcePath2 + ".png";
      ResourceLocation resourceLocation2 = new ResourceLocation(resourceDomain, text3);
      if (!Config.hasResource(resourceLocation2)) {
         Config.warn("File not found: " + text3);
      }

      return resourceLocation2;
   }

   private static void checkNull(Object obj, String msg) throws NullPointerException {
      if (obj == null) {
         throw new NullPointerException(msg);
      }
   }

   @Override
   public String toString() {
      return this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
   }

   public IBakedModel getBakedModel(ResourceLocation modelLocation, boolean fullModel) {
      IBakedModel iBakedModel;
      Map map2;
      if (fullModel) {
         iBakedModel = this.bakedModelFull;
         map2 = this.mapBakedModelsFull;
      } else {
         iBakedModel = this.bakedModelTexture;
         map2 = this.mapBakedModelsTexture;
      }

      if (modelLocation != null && map2 != null) {
         String resourcePath = modelLocation.getResourcePath();
         IBakedModel bakedModel2 = (IBakedModel)map2.get(resourcePath);
         if (bakedModel2 != null) {
            return bakedModel2;
         }
      }

      return iBakedModel;
   }

   private int getInt(String text3) {
      if (text3 == null) {
         return 0;
      } else {
         text3 = text3.trim();
         int i = Config.parseInt(text3, Integer.MIN_VALUE);
         if (i == Integer.MIN_VALUE) {
            Config.warn("Invalid integer: " + text3);
            return 0;
         } else {
            return i;
         }
      }
   }

   private RangeListInt parseRangeListInt(String str) {
      return this.parseRangeListInt(str, (IntGetter)null);
   }

   public CustomItemProperties(Properties props, String path) {
      this.name = parseName(path);
      this.basePath = parseBasePath(path);
      this.type = this.parseType(props.getProperty("type"));
      this.items = this.parseItems(props.getProperty("items"), props.getProperty("matchItems"));
      this.mapModels = parseModels(props, this.basePath);
      this.model = parseModel(props.getProperty("model"), path, this.basePath, this.type, this.mapModels);
      this.mapTextures = parseTextures(props, this.basePath);
      boolean flag = this.mapModels == null && this.model == null;
      this.texture = parseTexture(
         props.getProperty("texture"), props.getProperty("tile"), props.getProperty("source"), path, this.basePath, this.type, this.mapTextures, flag
      );
      String property = props.getProperty("damage");
      if (property != null) {
         this.damagePercent = property.contains("%");
         property = property.replace("%", "");
         this.damage = this.parseRangeListInt(property);
         this.damageMask = this.getInt(props.getProperty("damageMask"));
      }

      this.stackSize = this.parseRangeListInt(props.getProperty("stackSize"));
      this.enchantmentIds = this.parseRangeListInt(props.getProperty("enchantmentIDs"), new ParserEnchantmentId());
      this.enchantmentLevels = this.parseRangeListInt(props.getProperty("enchantmentLevels"));
      this.nbtTagValues = this.parseNbtTagValues(props);
      this.hand = this.parseHand(props.getProperty("hand"));
      this.blend = Blender.parseBlend(props.getProperty("blend"));
      this.speed = this.parseFloat(props.getProperty("speed"), 0.0F);
      this.rotation = this.parseFloat(props.getProperty("rotation"), 0.0F);
      this.layer = this.getInt(props.getProperty("layer"));
      this.weight = this.getInt(props.getProperty("weight"));
      this.duration = this.parseFloat(props.getProperty("duration"), 1.0F);
   }

   private String getMapTexture(Map<String, String> map, String key, String def) {
      if (map == null) {
         return def;
      }

      String text = map.get(key);
      return text == null ? def : text;
   }

   private int parseType(String str) {
      if (str == null) {
         return 1;
      }

      if (str.equals("item")) {
         return 1;
      }

      if (str.equals("enchantment")) {
         return 2;
      }

      if (str.equals("armor")) {
         return 3;
      }

      Config.warn("Unknown method: " + str);
      return 0;
   }

   private static String parseBasePath(String path) {
      int i = path.lastIndexOf(47);
      return i < 0 ? "" : path.substring(0, i);
   }

   private static IBakedModel bakeModel(TextureMap textureMap, ModelBlock modelBlockIn, boolean useTint) {
      ModelRotation modelRotation = ModelRotation.X0_Y0;
      String text3 = modelBlockIn.resolveTextureName("particle");
      TextureAtlasSprite atlasSprite = textureMap.getAtlasSprite(new ResourceLocation(text3).toString());
      net.minecraft.client.resources.model.SimpleBakedModel.Builder builder = new net.minecraft.client.resources.model.SimpleBakedModel.Builder(modelBlockIn).setTexture(atlasSprite);

      for (BlockPart blockPart : modelBlockIn.getElements()) {
         for (EnumFacing enumFacing : blockPart.mapFaces.keySet()) {
            BlockPartFace blockPartFace = blockPart.mapFaces.get(enumFacing);
            if (!useTint) {
               blockPartFace = new BlockPartFace(blockPartFace.cullFace, -1, blockPartFace.texture, blockPartFace.blockFaceUV);
            }

            String text4 = modelBlockIn.resolveTextureName(blockPartFace.texture);
            TextureAtlasSprite atlasSprite2 = textureMap.getAtlasSprite(new ResourceLocation(text4).toString());
            BakedQuad bakedQuad = createBakedQuad(blockPart, blockPartFace, atlasSprite2, enumFacing, modelRotation);
            if (blockPartFace.cullFace == null) {
               builder.addGeneralQuad(bakedQuad);
            } else {
               builder.addFaceQuad(modelRotation.rotateFace(blockPartFace.cullFace), bakedQuad);
            }
         }
      }

      return builder.makeBakedModel();
   }

   private RangeListInt parseRangeListInt(String str, IntGetter parser) {
      if (str == null) {
         return null;
      }

      String[] stringArray = Config.tokenize(str, " ");
      RangeListInt rangeListInt = new RangeListInt();

      for (int index = 0; index < stringArray.length; index++) {
         String text3 = stringArray[index];
         if (parser != null) {
            int intValue = parser.getInt(text3);
            if (intValue != Integer.MIN_VALUE) {
               rangeListInt.addRange(new RangeInt(intValue, intValue));
               continue;
            }
         }

         RangeInt rangeInt = this.parseRangeInt(text3);
         if (rangeInt == null) {
            Config.warn("Invalid range list: " + str);
            return null;
         }

         rangeListInt.addRange(rangeInt);
      }

      return rangeListInt;
   }

   private RangeInt parseRangeInt(String str) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      if (str.length() - str.replace("-", "").length() > 1) {
         Config.warn("Invalid range: " + str);
         return null;
      }

      String[] texts = Config.tokenize(str, "- ");
      int[] iArray = new int[texts.length];

      for (int i = 0; i < texts.length; i++) {
         String text3 = texts[i];
         int i2 = Config.parseInt(text3, -1);
         if (i2 < 0) {
            Config.warn("Invalid range: " + str);
            return null;
         }

         iArray[i] = i2;
      }

      if (iArray.length == 1) {
         int i3 = iArray[0];
         if (str.startsWith("-")) {
            return new RangeInt(0, i3);
         } else {
            return str.endsWith("-") ? new RangeInt(i3, 65535) : new RangeInt(i3, i3);
         }
      } else if (iArray.length == 2) {
         int i4 = Math.min(iArray[0], iArray[1]);
         int i5 = Math.max(iArray[0], iArray[1]);
         return new RangeInt(i4, i5);
      } else {
         Config.warn("Invalid range: " + str);
         return null;
      }
   }

   private int[] parseItems(String str, String str2) {
      if (str == null) {
         str = str2;
      }

      if (str == null) {
         return null;
      }

      str = str.trim();
      TreeSet treeSet = new TreeSet();
      String[] stringArray = Config.tokenize(str, " ");

      for (int index = 0; index < stringArray.length; index++) {
         String intArray = stringArray[index];
         int i = Config.parseInt(intArray, -1);
         if (i >= 0) {
            treeSet.add(new Integer(i));
         } else {
            if (intArray.contains("-")) {
               String[] stringArray1 = Config.tokenize(intArray, "-");
               if (stringArray1.length == 2) {
                  int i2 = Config.parseInt(stringArray1[0], -1);
                  int i3 = Config.parseInt(stringArray1[1], -1);
                  if (i2 >= 0 && i3 >= 0) {
                     int i4 = Math.min(i2, i3);
                     int i5 = Math.max(i2, i3);

                     for (int i6 = i4; i6 <= i5; i6++) {
                        treeSet.add(new Integer(i6));
                     }
                     continue;
                  }
               }
            }

            Item item = Item.getByNameOrId(intArray);
            if (item == null) {
               Config.warn("Item not found: " + intArray);
            } else {
               int idFromItem = Item.getIdFromItem(item);
               if (idFromItem <= 0) {
                  Config.warn("Item not found: " + intArray);
               } else {
                  treeSet.add(new Integer(idFromItem));
               }
            }
         }
      }

      Integer[] integers = (Integer[]) treeSet.toArray(new Integer[treeSet.size()]);
      int[] intArray = new int[integers.length];

      for (int outerIndex = 0; outerIndex < intArray.length; outerIndex++) {
         intArray[outerIndex] = integers[outerIndex];
      }

      return intArray;
   }

   private static String fixModelName(String modelName, String basePath) {
      modelName = TextureUtils.fixResourcePath(modelName, basePath);
      boolean flag = modelName.startsWith("block/") || modelName.startsWith("item/");
      if (!modelName.startsWith(basePath) && !flag && !modelName.startsWith("mcpatcher/")) {
         modelName = basePath + "/" + modelName;
      }

      if (modelName.endsWith(".json")) {
         modelName = modelName.substring(0, modelName.length() - ".json".length());
      }

      if (modelName.startsWith("/")) {
         modelName = modelName.substring(1);
      }

      return modelName;
   }

   private static Map parseTextures(Properties props, String basePath) {
      Map matchingProperties = getMatchingProperties(props, "texture.");
      if (matchingProperties.size() <= 0) {
         return null;
      }

      Set set = matchingProperties.keySet();
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object propertyKey : set) {
         String text = (String)matchingProperties.get(propertyKey);
         text = fixTextureName(text, basePath);
         linkedHashMap.put(propertyKey, text);
      }

      return linkedHashMap;
   }

   private static BakedQuad createBakedQuad(BlockPart blockPart, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, ModelRotation modelRotation) {
      return new FaceBakery().getBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPart.partRotation, blockPart.shade);
   }

   private static Map parseModels(Properties props, String basePath) {
      Map matchingProperties = getMatchingProperties(props, "model.");
      if (matchingProperties.size() <= 0) {
         return null;
      }

      Set set = matchingProperties.keySet();
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Object propertyKey : set) {
         String text = (String)matchingProperties.get(propertyKey);
         text = fixModelName(text, basePath);
         linkedHashMap.put(propertyKey, text);
      }

      return linkedHashMap;
   }

   private int parseHand(String str) {
      if (str == null) {
         return 0;
      }

      str = str.toLowerCase();
      if (str.equals("any")) {
         return 0;
      }

      if (str.equals("main")) {
         return 1;
      }

      if (str.equals("off")) {
         return 2;
      }

      Config.warn("Invalid hand: " + str);
      return 0;
   }

   private NbtTagValue[] parseNbtTagValues(Properties props) {
      Map propertyKey = getMatchingProperties(props, "nbt.");
      if (propertyKey.size() <= 0) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (String text3 : (Iterable<String>) propertyKey.keySet()) {
         String text4 = (String)propertyKey.get(text3);
         String text5 = text3.substring("nbt.".length());
         NbtTagValue nbtTagValue = new NbtTagValue(text5, text4);
         arrayList.add(nbtTagValue);
      }

      return (NbtTagValue[]) arrayList.toArray(new NbtTagValue[arrayList.size()]);
   }

   private String[] getModelTextures() {
      if (this.type == 1 && this.items.length == 1) {
         Item itemById = Item.getItemById(this.items[0]);
         if (itemById == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0) {
            RangeInt rangeInt2 = this.damage.getRangeInt();
            boolean flag = (rangeInt2.getMin() & 16384) != 0;
            String mapTexture = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
            String text3;
            if (flag) {
               text3 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash");
            } else {
               text3 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
            }

            return new String[]{mapTexture, text3};
         }

         if (itemById instanceof ItemArmor itemArmor && itemArmor.getArmorMaterial() == net.minecraft.item.ItemArmor.ArmorMaterial.LEATHER) {
            String text4 = "helmet";
            if (itemArmor.armorType == 0) {
               text4 = "helmet";
            }

            if (itemArmor.armorType == 1) {
               text4 = "chestplate";
            }

            if (itemArmor.armorType == 2) {
               text4 = "leggings";
            }

            if (itemArmor.armorType == 3) {
               text4 = "boots";
            }

            String text5 = "leather_" + text4;
            String mapTexture2 = this.getMapTexture(this.mapTextures, "texture." + text5, "items/" + text5);
            String mapTexture3 = this.getMapTexture(this.mapTextures, "texture." + text5 + "_overlay", "items/" + text5 + "_overlay");
            return new String[]{mapTexture2, mapTexture3};
         }
      }

      return new String[]{this.texture};
   }

   private static void loadItemModel(ModelBakery modelBakery, String model) {
      ResourceLocation modelLocation2 = getModelLocation(model);
      ModelResourceLocation modelResourceLocation = new ModelResourceLocation(modelLocation2, "inventory");
      if (Reflector.reflectorClass42.isActive()) {
         try {
            Object object = Reflector.reflectorField20.getObject();
            checkNull(object, "vanillaLoader is null");
            Object object2 = Reflector.createObject4(object, Reflector.reflectorMethod105, modelResourceLocation);
            checkNull(object2, "iModel is null");
            Map map2 = (Map)Reflector.createObject6(modelBakery, Reflector.reflectorField18);
            checkNull(map2, "stateModels is null");
            map2.put(modelResourceLocation, object2);
            Set set = (Set)Reflector.createObject6(modelBakery, Reflector.reflectorField19);
            checkNull(set, "registryTextures is null");
            Collection collection = (Collection)Reflector.createObject4(object2, Reflector.reflectorMethod94);
            checkNull(collection, "modelTextures is null");
            set.addAll(collection);
         } catch (Exception exception) {
            Config.warn("Error registering model with ModelLoader: " + modelResourceLocation + ", " + exception.getClass().getName() + ": " + exception.getMessage());
         }
      } else {
         modelBakery.loadItemModel(modelLocation2.toString(), modelResourceLocation, modelLocation2);
      }
   }

   private static ResourceLocation getModelLocation(String modelName) {
      return Reflector.reflectorClass42.isActive() && !modelName.startsWith("mcpatcher/") && !modelName.startsWith("optifine/")
         ? new ResourceLocation("models/" + modelName)
         : new ResourceLocation(modelName);
   }

   private float parseFloat(String str, float defVal) {
      if (str == null) {
         return defVal;
      } else {
         str = str.trim();
         float f = Config.parseFloat(str, Float.MIN_VALUE);
         if (f == Float.MIN_VALUE) {
            Config.warn("Invalid float: " + str);
            return defVal;
         } else {
            return f;
         }
      }
   }

   public void updateModelTexture(TextureMap textureMap, ItemModelGenerator itemModelGenerator) {
      if (this.texture != null || this.mapTextures != null) {
         String[] stringArray1 = this.getModelTextures();
         boolean useTint2 = this.isUseTint();
         this.bakedModelTexture = makeBakedModel(textureMap, itemModelGenerator, stringArray1, useTint2);
         if (this.type == 1 && this.mapTextures != null) {
            for (String text3 : this.mapTextures.keySet()) {
               String text4 = this.mapTextures.get(text3);
               String text5 = StrUtils.removePrefix(text3, "texture.");
               if (text5.startsWith("bow") || text5.startsWith("fishing_rod") || text5.startsWith("shield")) {
                  String[] texts = new String[]{text4};
                  IBakedModel iBakedModel = makeBakedModel(textureMap, itemModelGenerator, texts, useTint2);
                  if (this.mapBakedModelsTexture == null) {
                     this.mapBakedModelsTexture = new HashMap<>();
                  }

                  this.mapBakedModelsTexture.put(text5, iBakedModel);
               }
            }
         }
      }
   }

   private static String fixTextureName(String iconName, String basePath) {
      iconName = TextureUtils.fixResourcePath(iconName, basePath);
      if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/")) {
         iconName = basePath + "/" + iconName;
      }

      if (iconName.endsWith(".png")) {
         iconName = iconName.substring(0, iconName.length() - 4);
      }

      if (iconName.startsWith("/")) {
         iconName = iconName.substring(1);
      }

      return iconName;
   }

   public float getTextureWidth(TextureManager textureManager) {
      if (this.textureWidth <= 0) {
         if (this.textureLocation != null) {
            int glTextureId = textureManager.getTexture(this.textureLocation).getGlTextureId();
            int boundTexture = GlStateManager.getBoundTexture();
            GlStateManager.bindTexture(glTextureId);
            this.textureWidth = GL11.glGetTexLevelParameteri(3553, 0, 4096);
            GlStateManager.bindTexture(boundTexture);
         }

         if (this.textureWidth <= 0) {
            this.textureWidth = 16;
         }
      }

      return this.textureWidth;
   }

   private static IBakedModel makeBakedModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator, String[] textures, boolean useTint) {
      String[] texts = new String[textures.length];

      for (int i = 0; i < texts.length; i++) {
         String text3 = textures[i];
         texts[i] = StrUtils.removePrefix(text3, "textures/");
      }

      ModelBlock modelBlock2 = makeModelBlock(texts);
      ModelBlock modelBlock3 = itemModelGenerator.makeItemModel(textureMap, modelBlock2);
      return bakeModel(textureMap, modelBlock3, useTint);
   }

   private static String parseTexture(
      String texStr, String texStr2, String texStr3, String path, String basePath, int type, Map<String, String> mapTexs, boolean textureFromPath
   ) {
      if (texStr == null) {
         texStr = texStr2;
      }

      if (texStr == null) {
         texStr = texStr3;
      }

      if (texStr != null) {
         if (texStr.endsWith(".png")) {
            texStr = texStr.substring(0, texStr.length() - ".png".length());
         }

         return fixTextureName(texStr, basePath);
      } else {
         if (type == 3) {
            return null;
         }

         if (mapTexs != null) {
            String text3 = mapTexs.get("texture.bow_standby");
            if (text3 != null) {
               return text3;
            }
         }

         if (!textureFromPath) {
            return null;
         }

         String path2 = path;
         int i = path.lastIndexOf(47);
         if (i >= 0) {
            path2 = path.substring(i + 1);
         }

         int i2 = path2.lastIndexOf(46);
         if (i2 >= 0) {
            path2 = path2.substring(0, i2);
         }

         return fixTextureName(path2, basePath);
      }
   }

   public void updateModelsFull() {
      ModelManager modelManager = Config.getModelManager();
      IBakedModel missingModel = modelManager.getMissingModel();
      if (this.model != null) {
         ResourceLocation modelLocation2 = getModelLocation(this.model);
         ModelResourceLocation modelResourceLocation = new ModelResourceLocation(modelLocation2, "inventory");
         this.bakedModelFull = modelManager.getModel(modelResourceLocation);
         if (this.bakedModelFull == missingModel) {
            Config.warn("Custom Items: Model not found " + modelResourceLocation.getResourcePath());
            this.bakedModelFull = null;
         }
      }

      if (this.type == 1 && this.mapModels != null) {
         for (String text3 : this.mapModels.keySet()) {
            String text4 = this.mapModels.get(text3);
            String text5 = StrUtils.removePrefix(text3, "model.");
            if (text5.startsWith("bow") || text5.startsWith("fishing_rod") || text5.startsWith("shield")) {
               ResourceLocation modelLocation3 = getModelLocation(text4);
               ModelResourceLocation modelResourceLocation2 = new ModelResourceLocation(modelLocation3, "inventory");
               IBakedModel model2 = modelManager.getModel(modelResourceLocation2);
               if (model2 == missingModel) {
                  Config.warn("Custom Items: Model not found " + modelResourceLocation2.getResourcePath());
               } else {
                  if (this.mapBakedModelsFull == null) {
                     this.mapBakedModelsFull = new HashMap<>();
                  }

                  this.mapBakedModelsFull.put(text5, model2);
               }
            }
         }
      }
   }

   private static ModelBlock makeModelBlock(String[] modelTextures) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");

      for (int index = 0; index < modelTextures.length; index++) {
         String modelBlock = modelTextures[index];
         if (index > 0) {
            stringBuffer.append(", ");
         }

         stringBuffer.append("\"layer" + index + "\": \"" + modelBlock + "\"");
      }

      stringBuffer.append("}}");
      String text2 = stringBuffer.toString();
      return ModelBlock.deserialize(text2);
   }

   public boolean isValid(String path) {
      if (this.name == null || this.name.length() <= 0) {
         Config.warn("No name found: " + path);
         return false;
      }

      if (this.basePath == null) {
         Config.warn("No base path found: " + path);
         return false;
      }

      if (this.type == 0) {
         Config.warn("No type defined: " + path);
         return false;
      }

      if (this.type == 1 || this.type == 3) {
         if (this.items == null) {
            this.items = this.detectItems();
         }

         if (this.items == null) {
            Config.warn("No items defined: " + path);
            return false;
         }
      }

      if (this.texture == null && this.mapTextures == null && this.model == null && this.mapModels == null) {
         Config.warn("No texture or model specified: " + path);
         return false;
      } else if (this.type == 2 && this.enchantmentIds == null) {
         Config.warn("No enchantmentIDs specified: " + path);
         return false;
      } else {
         return true;
      }
   }
}
