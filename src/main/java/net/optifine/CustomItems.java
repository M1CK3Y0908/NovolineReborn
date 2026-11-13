package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.NbtTagValue;
import net.optifine.render.Blender;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class CustomItems {
   public static final int MASK_POTION_SPLASH = 16384;
   public static final String TEXTURE_POTION_BOTTLE_DRINKABLE = "texture.potion_bottle_drinkable";
   public static final String ITEMS_POTION_BOTTLE_SPLASH = "items/potion_bottle_splash";
   public static final String TEXTURE_POTION_BOTTLE_SPLASH = "texture.potion_bottle_splash";
   public static final int MASK_POTION_EXTENDED = 64;
   private static final String NORMAL = "normal";
   private static final String SPLASH = "splash";
   public static final int MASK_POTION_NAME = 63;
   public static final String TEXTURE_POTION_OVERLAY = "texture.potion_overlay";
   public static final String ITEMS_POTION_BOTTLE_DRINKABLE = "items/potion_bottle_drinkable";
   private static final String LINGER = "linger";
   public static final String ITEMS_POTION_OVERLAY = "items/potion_overlay";
   private static CustomItemProperties[][] itemProperties = (CustomItemProperties[][])null;
   private static CustomItemProperties[][] enchantmentProperties = (CustomItemProperties[][])null;
   private static Map mapPotionIds = null;
   private static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   private static boolean useGlint = true;
   private static boolean renderOffHand = false;
   private static final int[][] EMPTY_INT2_ARRAY = new int[0][];

   private static ResourceLocation getCustomArmorLocation(ItemStack itemStack, int layer, String overlay) {
      CustomItemProperties customItemProperties3 = getCustomItemProperties(itemStack, 3);
      if (customItemProperties3 == null) {
         return null;
      }

      if (customItemProperties3.mapTextureLocations == null) {
         return customItemProperties3.textureLocation;
      }

      Item item2 = itemStack.getItem();
      if (!(item2 instanceof ItemArmor)) {
         return null;
      }

      String text = ((ItemArmor)item2).getArmorMaterial().getName();
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("texture.");
      stringBuffer.append(text);
      stringBuffer.append("_layer_");
      stringBuffer.append(layer);
      if (overlay != null) {
         stringBuffer.append("_");
         stringBuffer.append(overlay);
      }

      String text2 = stringBuffer.toString();
      ResourceLocation resourceLocation2 = (ResourceLocation)customItemProperties3.mapTextureLocations.get(text2);
      return resourceLocation2 == null ? customItemProperties3.textureLocation : resourceLocation2;
   }

   public static void update() {
      itemProperties = (CustomItemProperties[][])null;
      enchantmentProperties = (CustomItemProperties[][])null;
      useGlint = true;
      if (Config.isCustomItems()) {
         readCitProperties("mcpatcher/cit.properties");
         IResourcePack[] resourcePacks = Config.getResourcePacks();

         for (int i = resourcePacks.length - 1; i >= 0; i--) {
            update(resourcePacks[i]);
         }

         update(Config.getDefaultResourcePack());
         if (itemProperties.length <= 0) {
            itemProperties = (CustomItemProperties[][])null;
         }

         if (enchantmentProperties.length <= 0) {
            enchantmentProperties = (CustomItemProperties[][])null;
         }
      }
   }

   public static boolean renderCustomArmorEffect(
      EntityLivingBase entity,
      ItemStack itemStack,
      ModelBase model,
      float limbSwing,
      float prevLimbSwing,
      float partialTicks,
      float timeLimbSwing,
      float yaw,
      float pitch,
      float scale
   ) {
      if (enchantmentProperties == null) {
         return false;
      }

      if (Config.isShaders() && Shaders.isShadowPass) {
         return false;
      }

      if (itemStack == null) {
         return false;
      }

      int[][] intArray = getEnchantmentIdLevels(itemStack);
      if (intArray.length <= 0) {
         return false;
      }

      HashSet hashSet = null;
      boolean conditionMet = false;
      TextureManager textureManager = Config.getTextureManager();

      for (int index = 0; index < intArray.length; index++) {
         int i = intArray[index][0];
         if (i >= 0 && i < enchantmentProperties.length) {
            CustomItemProperties[] customItemPropertiesArray = enchantmentProperties[i];
            if (customItemPropertiesArray != null) {
               for (int nestedIndex = 0; nestedIndex < customItemPropertiesArray.length; nestedIndex++) {
                  CustomItemProperties customItemProperties3 = customItemPropertiesArray[nestedIndex];
                  if (hashSet == null) {
                     hashSet = new HashSet();
                  }

                  if (hashSet.add(i) && matchesProperties(customItemProperties3, itemStack, intArray) && customItemProperties3.textureLocation != null) {
                     textureManager.bindTexture(customItemProperties3.textureLocation);
                     float textureWidth = customItemProperties3.getTextureWidth(textureManager);
                     if (!conditionMet) {
                        conditionMet = true;
                        if (Config.isShaders()) {
                           ShadersRender.renderEnchantedGlintBegin();
                        }

                        GlStateManager.enableBlend();
                        GlStateManager.depthFunc(514);
                        GlStateManager.depthMask(false);
                     }

                     Blender.setupBlend(customItemProperties3.blend, 1.0F);
                     GlStateManager.disableLighting();
                     GlStateManager.matrixMode(5890);
                     GlStateManager.loadIdentity();
                     GlStateManager.rotate(customItemProperties3.rotation, 0.0F, 0.0F, 1.0F);
                     float f = textureWidth / 8.0F;
                     GlStateManager.scale(f, f / 2.0F, f);
                     float f2 = customItemProperties3.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(0.0F, f2, 0.0F);
                     GlStateManager.matrixMode(5888);
                     model.render(entity, limbSwing, prevLimbSwing, timeLimbSwing, yaw, pitch, scale);
                  }
               }
            }
         }
      }

      if (conditionMet) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(515);
         GlStateManager.disableBlend();
         if (Config.isShaders()) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }

      return conditionMet;
   }

   private static CustomItemProperties getCustomItemProperties(ItemStack itemStack, int type) {
      if (itemProperties == null) {
         return null;
      }

      if (itemStack == null) {
         return null;
      }

      int idFromItem = Item.getIdFromItem(itemStack.getItem());
      if (idFromItem >= 0 && idFromItem < itemProperties.length) {
         CustomItemProperties[] customItemPropertiesArray = itemProperties[idFromItem];
         if (customItemPropertiesArray != null) {
            for (int i = 0; i < customItemPropertiesArray.length; i++) {
               CustomItemProperties customItemProperties3 = customItemPropertiesArray[i];
               if (customItemProperties3.type == type && matchesProperties(customItemProperties3, itemStack, (int[][])null)) {
                  return customItemProperties3;
               }
            }
         }
      }

      return null;
   }

   private static int[][] getEnchantmentIdLevels(ItemStack itemStack) {
      NBTTagList nbtTagList = itemStack.getItem() == Items.enchanted_book ? Items.enchanted_book.getEnchantments(itemStack) : itemStack.getEnchantmentTagList();
      if (nbtTagList != null && nbtTagList.tagCount() > 0) {
         int[][] iArrays = new int[nbtTagList.tagCount()][2];

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = nbtTagList.getCompoundTagAt(i);
            short shortValue = compoundTagAt.getShort("id");
            short shortValue2 = compoundTagAt.getShort("lvl");
            iArrays[i][0] = shortValue;
            iArrays[i][1] = shortValue2;
         }

         return iArrays;
      } else {
         return EMPTY_INT2_ARRAY;
      }
   }

   private static Map makePotionImageProperties(IResourcePack rp, String type, int itemId) {
      HashMap hashMap = new HashMap();
      String text2 = type + "/";
      String[] texts = new String[]{"mcpatcher/cit/potion/" + text2, "mcpatcher/cit/Potion/" + text2};
      String[] texts2 = new String[]{".png"};
      String[] texts3 = ResUtils.collectFiles(rp, texts, texts2);

      for (int i = 0; i < texts3.length; i++) {
         String text3 = texts3[i];
         Properties properties2 = makePotionProperties(StrUtils.removePrefixSuffix(text3, texts, texts2), type, itemId, text3);
         if (properties2 != null) {
            String text4 = StrUtils.removeSuffix(text3, texts2) + ".properties";
            CustomItemProperties customItemProperties3 = new CustomItemProperties(properties2, text4);
            hashMap.put(text4, customItemProperties3);
         }
      }

      return hashMap;
   }

   private static Comparator getPropertiesComparator() {
      return new Comparator() {
         @Override
         public int compare(Object left, Object right) {
            CustomItemProperties customItemProperties = (CustomItemProperties)left;
            CustomItemProperties customItemProperties2 = (CustomItemProperties)right;
            return customItemProperties.layer != customItemProperties2.layer
               ? customItemProperties.layer - customItemProperties2.layer
               : (
                  customItemProperties.weight != customItemProperties2.weight
                     ? customItemProperties2.weight - customItemProperties.weight
                     : (
                        !customItemProperties.basePath.equals(customItemProperties2.basePath)
                           ? customItemProperties.basePath.compareTo(customItemProperties2.basePath)
                           : customItemProperties.name.compareTo(customItemProperties2.name)
                     )
               );
         }
      };
   }

   private static void update(IResourcePack rp) {
      String[] texts = ResUtils.collectFiles(rp, "mcpatcher/cit/", ".properties", (String[])null);
      Map map = makeAutoImageProperties(rp);
      if (map.size() > 0) {
         Set set2 = map.keySet();
         String[] texts2 = (String[]) set2.toArray(new String[set2.size()]);
         texts = (String[])Config.addObjectsToArray(texts, texts2);
      }

      Arrays.sort(texts);
      List list2 = makePropertyList(itemProperties);
      List list3 = makePropertyList(enchantmentProperties);

      for (int i = 0; i < texts.length; i++) {
         String text2 = texts[i];
         Config.dbg("CustomItems: " + text2);

         try {
            CustomItemProperties customItemProperties3 = null;
            if (map.containsKey(text2)) {
               customItemProperties3 = (CustomItemProperties)map.get(text2);
            }

            if (customItemProperties3 == null) {
               ResourceLocation resourceLocation2 = new ResourceLocation(text2);
               InputStream inputStream = rp.getInputStream(resourceLocation2);
               if (inputStream == null) {
                  Config.warn("CustomItems file not found: " + text2);
                  continue;
               }

               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               inputStream.close();
               customItemProperties3 = new CustomItemProperties(propertiesOrdered, text2);
            }

            if (customItemProperties3.isValid(text2)) {
               addToItemList(customItemProperties3, list2);
               addToEnchantmentList(customItemProperties3, list3);
            }
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("CustomItems file not found: " + text2);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      itemProperties = propertyListToArray(list2);
      enchantmentProperties = propertyListToArray(list3);
      Comparator propertiesComparator = getPropertiesComparator();

      for (int i2 = 0; i2 < itemProperties.length; i2++) {
         CustomItemProperties[] customItemPropertiesArray = itemProperties[i2];
         if (customItemPropertiesArray != null) {
            Arrays.sort(customItemPropertiesArray, propertiesComparator);
         }
      }

      for (int i3 = 0; i3 < enchantmentProperties.length; i3++) {
         CustomItemProperties[] customItemPropertiesArray2 = enchantmentProperties[i3];
         if (customItemPropertiesArray2 != null) {
            Arrays.sort(customItemPropertiesArray2, propertiesComparator);
         }
      }
   }

   private static void readCitProperties(String fileName) {
      try {
         InputStream resourceStream = Config.getResourceStream(new ResourceLocation(fileName));
         if (resourceStream == null) {
            return;
         }

         Config.dbg("CustomItems: Loading " + fileName);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         useGlint = Config.parseBoolean(propertiesOrdered.getProperty("useGlint"), true);
      } catch (FileNotFoundException fileNotFoundException) {
         return;
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private static void addToItemList(CustomItemProperties cp, List itemList) {
      if (cp.items != null) {
         for (int index = 0; index < cp.items.length; index++) {
            int i = cp.items[index];
            if (i <= 0) {
               Config.warn("Invalid item ID: " + i);
            } else {
               addToList(cp, itemList, i);
            }
         }
      }
   }

   public static void updateModels() {
      for (CustomItemProperties customItemProperties3 : getAllProperties()) {
         if (customItemProperties3.type == 1) {
            TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
            customItemProperties3.updateModelTexture(textureMapBlocks, itemModelGenerator);
            customItemProperties3.updateModelsFull();
         }
      }
   }

   private static void addToList(CustomItemProperties cp, List lists, int id) {
      while (id >= lists.size()) {
         lists.add(null);
      }

      List list = (List)lists.get(id);
      if (list == null) {
         list = new ArrayList();
         lists.set(id, list);
      }

      list.add(cp);
   }

   private static Map getMapPotionIds() {
      if (mapPotionIds == null) {
         mapPotionIds = new LinkedHashMap();
         mapPotionIds.put("water", getPotionId(0, 0));
         mapPotionIds.put("awkward", getPotionId(0, 1));
         mapPotionIds.put("thick", getPotionId(0, 2));
         mapPotionIds.put("potent", getPotionId(0, 3));
         mapPotionIds.put("regeneration", getPotionIds(1));
         mapPotionIds.put("movespeed", getPotionIds(2));
         mapPotionIds.put("fireresistance", getPotionIds(3));
         mapPotionIds.put("poison", getPotionIds(4));
         mapPotionIds.put("heal", getPotionIds(5));
         mapPotionIds.put("nightvision", getPotionIds(6));
         mapPotionIds.put("clear", getPotionId(7, 0));
         mapPotionIds.put("bungling", getPotionId(7, 1));
         mapPotionIds.put("charming", getPotionId(7, 2));
         mapPotionIds.put("rank", getPotionId(7, 3));
         mapPotionIds.put("weakness", getPotionIds(8));
         mapPotionIds.put("damageboost", getPotionIds(9));
         mapPotionIds.put("moveslowdown", getPotionIds(10));
         mapPotionIds.put("leaping", getPotionIds(11));
         mapPotionIds.put("harm", getPotionIds(12));
         mapPotionIds.put("waterbreathing", getPotionIds(13));
         mapPotionIds.put("invisibility", getPotionIds(14));
         mapPotionIds.put("thin", getPotionId(15, 0));
         mapPotionIds.put("debonair", getPotionId(15, 1));
         mapPotionIds.put("sparkling", getPotionId(15, 2));
         mapPotionIds.put("stinky", getPotionId(15, 3));
         mapPotionIds.put("mundane", getPotionId(0, 4));
         mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
         mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
         mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
         mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
         mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
         mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
         mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
         mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
      }

      return mapPotionIds;
   }

   public static void loadModels(ModelBakery modelBakery) {
      Iterator iterator2 = getAllProperties().iterator();

      while (iterator2.hasNext()) {
         ((CustomItemProperties)iterator2.next()).loadModels(modelBakery);
      }
   }

   private static Properties makePotionProperties(String name, String type, int itemId, String path) {
      if (StrUtils.endsWith(name, new String[]{"_n", "_s"})) {
         return null;
      }

      if (name.equals("empty") && type.equals("normal")) {
         itemId = Item.getIdFromItem(Items.glass_bottle);
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.put("type", "item");
         propertiesOrdered.put("items", itemId + "");
         return propertiesOrdered;
      }

      int[] iArray = (int[])getMapPotionIds().get(name);
      if (iArray == null) {
         Config.warn("Potion not found for image: " + path);
         return null;
      }

      StringBuffer stringBuffer = new StringBuffer();

      for (int index = 0; index < iArray.length; index++) {
         int i = iArray[index];
         if (type.equals("splash")) {
            i |= 16384;
         }

         if (index > 0) {
            stringBuffer.append(" ");
         }

         stringBuffer.append(i);
      }

      int i2 = 16447;
      if (name.equals("water") || name.equals("mundane")) {
         i2 = 16511;
      }

      PropertiesOrdered propertiesOrdered2 = new PropertiesOrdered();
      propertiesOrdered2.put("type", "item");
      propertiesOrdered2.put("items", itemId + "");
      propertiesOrdered2.put("damage", stringBuffer.toString() + "");
      propertiesOrdered2.put("damageMask", i2 + "");
      if (type.equals("splash")) {
         propertiesOrdered2.put("texture.potion_bottle_splash", name);
      } else {
         propertiesOrdered2.put("texture.potion_bottle_drinkable", name);
      }

      return propertiesOrdered2;
   }

   public static void updateIcons(TextureMap textureMap) {
      Iterator iterator2 = getAllProperties().iterator();

      while (iterator2.hasNext()) {
         ((CustomItemProperties)iterator2.next()).updateIcons(textureMap);
      }
   }

   public static IBakedModel getCustomItemModel(ItemStack itemStack, IBakedModel model, ResourceLocation modelLocation, boolean fullModel) {
      if (!fullModel && model.isGui3d()) {
         return model;
      }

      if (itemProperties == null) {
         return model;
      }

      CustomItemProperties customItemProperties = getCustomItemProperties(itemStack, 1);
      if (customItemProperties == null) {
         return model;
      }

      IBakedModel bakedModel = customItemProperties.getBakedModel(modelLocation, fullModel);
      return bakedModel != null ? bakedModel : model;
   }

   public static boolean bindCustomArmorTexture(ItemStack itemStack, int layer, String overlay) {
      if (itemProperties == null) {
         return false;
      }

      ResourceLocation resourceLocation = getCustomArmorLocation(itemStack, layer, overlay);
      if (resourceLocation == null) {
         return false;
      }

      Config.getTextureManager().bindTexture(resourceLocation);
      return true;
   }

   private static int[] getPotionIds(int baseId) {
      return new int[]{baseId, baseId + 16, baseId + 32, baseId + 48};
   }

   private static List<CustomItemProperties> getAllProperties() {
      ArrayList arrayList = new ArrayList();
      addAll(itemProperties, arrayList);
      addAll(enchantmentProperties, arrayList);
      return arrayList;
   }

   private static List makePropertyList(CustomItemProperties[][] propsArr) {
      ArrayList arrayList = new ArrayList();
      if (propsArr != null) {
         for (int index = 0; index < propsArr.length; index++) {
            CustomItemProperties[] customItemPropertiesArray = propsArr[index];
            ArrayList arrayList2 = null;
            if (customItemPropertiesArray != null) {
               arrayList2 = new ArrayList<>(Arrays.asList(customItemPropertiesArray));
            }

            arrayList.add(arrayList2);
         }
      }

      return arrayList;
   }

   private static boolean matchesProperties(CustomItemProperties cip, ItemStack itemStack, int[][] enchantmentIdLevels) {
      Item item = itemStack.getItem();
      if (cip.damage != null) {
         int itemDamage = itemStack.getItemDamage();
         if (cip.damageMask != 0) {
            itemDamage &= cip.damageMask;
         }

         if (cip.damagePercent) {
            int maxDamage = item.getMaxDamage();
            itemDamage = (int)((double)(itemDamage * 100) / maxDamage);
         }

         if (!cip.damage.isInRange(itemDamage)) {
            return false;
         }
      }

      if (cip.stackSize != null && !cip.stackSize.isInRange(itemStack.stackSize)) {
         return false;
      }

      int[][] intArray = enchantmentIdLevels;
      if (cip.enchantmentIds != null) {
         if (enchantmentIdLevels == null) {
            intArray = getEnchantmentIdLevels(itemStack);
         }

         boolean conditionMet = false;

         for (int nestedIndex = 0; nestedIndex < intArray.length; nestedIndex++) {
            int i = intArray[nestedIndex][0];
            if (cip.enchantmentIds.isInRange(i)) {
               conditionMet = true;
               break;
            }
         }

         if (!conditionMet) {
            return false;
         }
      }

      if (cip.enchantmentLevels != null) {
         if (intArray == null) {
            intArray = getEnchantmentIdLevels(itemStack);
         }

         boolean conditionMet2 = false;

         for (int index = 0; index < intArray.length; index++) {
            int i2 = intArray[index][1];
            if (cip.enchantmentLevels.isInRange(i2)) {
               conditionMet2 = true;
               break;
            }
         }

         if (!conditionMet2) {
            return false;
         }
      }

      if (cip.nbtTagValues != null) {
         NBTTagCompound nBTTagCompound = itemStack.getTagCompound();

         for (int innerIndex = 0; innerIndex < cip.nbtTagValues.length; innerIndex++) {
            NbtTagValue nbtTagValue = cip.nbtTagValues[innerIndex];
            if (!nbtTagValue.matches(nBTTagCompound)) {
               return false;
            }
         }
      }

      if (cip.hand != 0) {
         if (cip.hand == 1 && renderOffHand) {
            return false;
         }

         if (cip.hand == 2 && !renderOffHand) {
            return false;
         }
      }

      return true;
   }

   public static boolean renderCustomEffect(RenderItem renderItem, ItemStack itemStack, IBakedModel model) {
      if (enchantmentProperties == null) {
         return false;
      }

      if (itemStack == null) {
         return false;
      }

      int[][] intArray = getEnchantmentIdLevels(itemStack);
      if (intArray.length <= 0) {
         return false;
      }

      HashSet hashSet = null;
      boolean conditionMet = false;
      TextureManager textureManager = Config.getTextureManager();

      for (int index = 0; index < intArray.length; index++) {
         int i = intArray[index][0];
         if (i >= 0 && i < enchantmentProperties.length) {
            CustomItemProperties[] customItemPropertiesArray = enchantmentProperties[i];
            if (customItemPropertiesArray != null) {
               for (int nestedIndex = 0; nestedIndex < customItemPropertiesArray.length; nestedIndex++) {
                  CustomItemProperties customItemProperties3 = customItemPropertiesArray[nestedIndex];
                  if (hashSet == null) {
                     hashSet = new HashSet();
                  }

                  if (hashSet.add(i) && matchesProperties(customItemProperties3, itemStack, intArray) && customItemProperties3.textureLocation != null) {
                     textureManager.bindTexture(customItemProperties3.textureLocation);
                     float textureWidth = customItemProperties3.getTextureWidth(textureManager);
                     if (!conditionMet) {
                        conditionMet = true;
                        GlStateManager.depthMask(false);
                        GlStateManager.depthFunc(514);
                        GlStateManager.disableLighting();
                        GlStateManager.matrixMode(5890);
                     }

                     Blender.setupBlend(customItemProperties3.blend, 1.0F);
                     GlStateManager.pushMatrix();
                     GlStateManager.scale(textureWidth / 2.0F, textureWidth / 2.0F, textureWidth / 2.0F);
                     float f = customItemProperties3.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                     GlStateManager.translate(f, 0.0F, 0.0F);
                     GlStateManager.rotate(customItemProperties3.rotation, 0.0F, 0.0F, 1.0F);
                     renderItem.renderModel(model, -1);
                     GlStateManager.popMatrix();
                  }
               }
            }
         }
      }

      if (conditionMet) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         textureManager.bindTexture(TextureMap.locationBlocksTexture);
      }

      return conditionMet;
   }

   public static boolean isUseGlint() {
      return useGlint;
   }

   private static void addToEnchantmentList(CustomItemProperties cp, List enchantmentList) {
      if (cp.type == 2 && cp.enchantmentIds != null) {
         for (int index = 0; index < 256; index++) {
            if (cp.enchantmentIds.isInRange(index)) {
               addToList(cp, enchantmentList, index);
            }
         }
      }
   }

   private static void addAll(CustomItemProperties[][] cipsArr, List<CustomItemProperties> list) {
      if (cipsArr != null) {
         for (int index = 0; index < cipsArr.length; index++) {
            CustomItemProperties[] customItemPropertiesArray = cipsArr[index];
            if (customItemPropertiesArray != null) {
               for (int innerIndex = 0; innerIndex < customItemPropertiesArray.length; innerIndex++) {
                  CustomItemProperties customItemProperties3 = customItemPropertiesArray[innerIndex];
                  if (customItemProperties3 != null) {
                     list.add(customItemProperties3);
                  }
               }
            }
         }
      }
   }

   private static CustomItemProperties[][] propertyListToArray(List lists) {
      CustomItemProperties[][] acustomitemproperties = new CustomItemProperties[lists.size()][];

      for (int index = 0; index < lists.size(); index++) {
         List list = (List)lists.get(index);
         if (list != null) {
            CustomItemProperties[] customItemPropertiesArray = (CustomItemProperties[]) list.toArray(new CustomItemProperties[list.size()]);
            Arrays.sort(customItemPropertiesArray, new CustomItemsComparator());
            acustomitemproperties[index] = customItemPropertiesArray;
         }
      }

      return acustomitemproperties;
   }

   private static int[] getPotionId(int baseId, int subId) {
      return new int[]{baseId + subId * 16};
   }

   private static Map makeAutoImageProperties(IResourcePack rp) {
      HashMap hashMap = new HashMap();
      hashMap.putAll(makePotionImageProperties(rp, "normal", Item.getIdFromItem(Items.potionitem)));
      hashMap.putAll(makePotionImageProperties(rp, "splash", Item.getIdFromItem(Items.potionitem)));
      hashMap.putAll(makePotionImageProperties(rp, "linger", Item.getIdFromItem(Items.potionitem)));
      return hashMap;
   }
}
