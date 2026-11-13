package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.config.EntityClassLocator;
import net.optifine.config.IObjectLocator;
import net.optifine.config.ItemLocator;
import net.optifine.reflect.ReflectorForge;
import net.optifine.util.PropertiesOrdered;

public class DynamicLights {
   private static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
   private static DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
   private static Map<Class, Integer> mapEntityLightLevels = new HashMap<>();
   private static final int intValue = 8;
   private static final int intValue2 = 15;
   private static Map<Item, Integer> mapItemLightLevels = new HashMap<>();
   private static final double MAX_DIST = 7.5;
   private static final double MAX_DIST_SQ = 56.25;
   private static final int intValue3 = 15;
   private static final int intValue4 = 8;
   private static boolean initialized;
   private static final int LIGHT_LEVEL_BLAZE = 10;
   private static final int intValue5 = 8;
   private static long timeUpdateMs = 0L;

   public static void removeLights(RenderGlobal renderGlobal) {
      synchronized (mapDynamicLights) {
         List list = mapDynamicLights.valueList();

         for (int i = 0; i < list.size(); i++) {
            ((DynamicLight)list.get(i)).updateLitChunks(renderGlobal);
         }

         mapDynamicLights.clear();
      }
   }

   private static void updateMapDynamicLights(RenderGlobal renderGlobal) {
      WorldClient world = renderGlobal.getWorld();
      if (world != null) {
         for (Entity entity2 : world.getLoadedEntityList()) {
            if (getLightLevel(entity2) > 0) {
               int entityId = entity2.getEntityId();
               DynamicLight dynamicLight = mapDynamicLights.get(entityId);
               if (dynamicLight == null) {
                  dynamicLight = new DynamicLight(entity2);
                  mapDynamicLights.put(entityId, dynamicLight);
               }
            } else {
               int entityId2 = entity2.getEntityId();
               DynamicLight dynamicLight2 = mapDynamicLights.remove(entityId2);
               if (dynamicLight2 != null) {
                  dynamicLight2.updateLitChunks(renderGlobal);
               }
            }
         }
      }
   }

   public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {
   }

   public static double getLightLevel(BlockPos pos) {
      double d = 0.0;
      synchronized (mapDynamicLights) {
         List list = mapDynamicLights.valueList();
         int size2 = list.size();

         for (int i = 0; i < size2; i++) {
            DynamicLight dynamicLight = (DynamicLight)list.get(i);
            int lastLightLevel = dynamicLight.getLastLightLevel();
            if (lastLightLevel > 0) {
               double lastPosX = dynamicLight.getLastPosX();
               double xCoordinate = dynamicLight.getLastPosY();
               double yCoordinate = dynamicLight.getLastPosZ();
               double zCoordinate = pos.getX() - lastPosX;
               double d2 = pos.getY() - xCoordinate;
               double d3 = pos.getZ() - yCoordinate;
               double d4 = zCoordinate * zCoordinate + d2 * d2 + d3 * d3;
               if (dynamicLight.isUnderwater() && !Config.isClearWater()) {
                  lastLightLevel = Config.limit(lastLightLevel - 2, 0, 15);
                  d4 *= 2.0;
               }

               if (d4 <= 56.25) {
                  double d5 = Math.sqrt(d4);
                  double d6 = 1.0 - d5 / 7.5;
                  double d7 = d6 * lastLightLevel;
                  if (d7 > d) {
                     d = d7;
                  }
               }
            }
         }
      }

      return Config.limit(d, 0.0, 15.0);
   }

   private static void initialize() {
      initialized = true;
      mapEntityLightLevels.clear();
      mapItemLightLevels.clear();
      String[] array = ReflectorForge.getArray();

      for (int i = 0; i < array.length; i++) {
         String text = array[i];

         try {
            ResourceLocation resourceLocation = new ResourceLocation(text, "optifine/dynamic_lights.properties");
            loadModConfiguration(Config.getResourceStream(resourceLocation), resourceLocation.toString(), text);
         } catch (IOException ioException) {
         }
      }

      if (mapEntityLightLevels.size() > 0) {
         Config.dbg("DynamicLights entities: " + mapEntityLightLevels.size());
      }

      if (mapItemLightLevels.size() > 0) {
         Config.dbg("DynamicLights items: " + mapItemLightLevels.size());
      }
   }

   public static void update(RenderGlobal renderGlobal) {
      long l = System.currentTimeMillis();
      if (l >= timeUpdateMs + 50L) {
         timeUpdateMs = l;
         if (!initialized) {
            initialize();
         }

         synchronized (mapDynamicLights) {
            updateMapDynamicLights(renderGlobal);
            if (mapDynamicLights.size() > 0) {
               List list = mapDynamicLights.valueList();

               for (int i = 0; i < list.size(); i++) {
                  ((DynamicLight)list.get(i)).update(renderGlobal);
               }
            }
         }
      }
   }

   public static int getLightLevel(ItemStack itemStack) {
      if (itemStack == null) {
         return 0;
      }

      Item item = itemStack.getItem();
      if (item instanceof ItemBlock itemBlock) {
         Block block = itemBlock.getBlock();
         if (block != null) {
            return block.getLightValue();
         }
      }

      if (item == Items.lava_bucket) {
         return Blocks.lava.getLightValue();
      }

      if (item == Items.blaze_rod || item == Items.blaze_powder) {
         return 10;
      }

      if (item == Items.glowstone_dust) {
         return 8;
      }

      if (item == Items.prismarine_crystals) {
         return 8;
      }

      if (item == Items.magma_cream) {
         return 8;
      }

      if (item == Items.nether_star) {
         return Blocks.beacon.getLightValue() / 2;
      }

      if (!mapItemLightLevels.isEmpty()) {
         Integer integer = mapItemLightLevels.get(item);
         if (integer != null) {
            return integer;
         }
      }

      return 0;
   }

   public static ItemStack getItemStack(EntityItem entityItem) {
      return entityItem.getDataWatcher().getWatchableObjectItemStack(10);
   }

   public static int getLightLevel(Entity entity) {
      if (entity == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight()) {
         return 0;
      } else if (entity instanceof EntityPlayer entityPlayer && entityPlayer.isSpectator()) {
         return 0;
      } else {
         if (entity.isBurning()) {
            return 15;
         }

         if (!mapEntityLightLevels.isEmpty()) {
            Integer integer2 = mapEntityLightLevels.get(entity.getClass());
            if (integer2 != null) {
               return integer2;
            }
         }

         if (entity instanceof EntityFireball) {
            return 15;
         } else if (entity instanceof EntityTNTPrimed) {
            return 15;
         } else if (entity instanceof EntityBlaze entityBlaze) {
            return entityBlaze.isCharged() ? 15 : 10;
         } else if (entity instanceof EntityMagmaCube entityMagmaCube) {
            return entityMagmaCube.squishFactor > 0.6 ? 13 : 8;
         } else if (entity instanceof EntityCreeper entityCreeper && entityCreeper.getCreeperFlashIntensity(0.0F) > 0.001) {
            return 15;
         } else if (entity instanceof EntityLivingBase entityLivingBase) {
            int lightLevel = getLightLevel(entityLivingBase.getHeldItem());
            int lightLevel2 = getLightLevel(entityLivingBase.getEquipmentInSlot(4));
            return Math.max(lightLevel, lightLevel2);
         } else {
            return entity instanceof EntityItem entityItem2 ? getLightLevel(getItemStack(entityItem2)) : 0;
         }
      }
   }

   public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {
      synchronized (mapDynamicLights) {
         DynamicLight dynamicLight = mapDynamicLights.remove(entityIn.getEntityId());
         if (dynamicLight != null) {
            dynamicLight.updateLitChunks(renderGlobal);
         }
      }
   }

   public static int getCount() {
      synchronized (mapDynamicLights) {
         return mapDynamicLights.size();
      }
   }

   public static int getCombinedLight(Entity entity, int combinedLight) {
      double lightLevel = getLightLevel(entity);
      return getCombinedLight(lightLevel, combinedLight);
   }

   public static void clear() {
      synchronized (mapDynamicLights) {
         mapDynamicLights.clear();
      }
   }

   public static int getCombinedLight(double lightPlayer, int combinedLight) {
      if (lightPlayer > 0.0) {
         int i = (int)(lightPlayer * 16.0);
         int i2 = combinedLight & 0xFF;
         if (i > i2) {
            combinedLight &= -256;
            combinedLight |= i;
         }
      }

      return combinedLight;
   }

   private static void loadModLightLevels(String prop, Map mapLightLevels, IObjectLocator ol, ConnectedParser cp, String path, String modId) {
      if (prop != null) {
         String[] stringArray = Config.tokenize(prop, " ");

         for (int index = 0; index < stringArray.length; index++) {
            String text = stringArray[index];
            String[] stringArray1 = Config.tokenize(text, ":");
            if (stringArray1.length != 2) {
               cp.warn("Invalid entry: " + text + ", in:" + path);
            } else {
               String text2 = stringArray1[0];
               String text3 = stringArray1[1];
               String text4 = modId + ":" + text2;
               ResourceLocation resourceLocation = new ResourceLocation(text4);
               Object targetObject = ol.getObject(resourceLocation);
               if (targetObject == null) {
                  cp.warn("Object not found: " + text4);
               } else {
                  int i = cp.parseInt(text3, -1);
                  if (i >= 0 && i <= 15) {
                     mapLightLevels.put(targetObject, new Integer(i));
                  } else {
                     cp.warn("Invalid light level: " + text);
                  }
               }
            }
         }
      }
   }

   public static int getCombinedLight(BlockPos pos, int combinedLight) {
      double lightLevel = getLightLevel(pos);
      return getCombinedLight(lightLevel, combinedLight);
   }

   private static void loadModConfiguration(InputStream in, String path, String modId) {
      if (in != null) {
         try {
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(in);
            in.close();
            Config.dbg("DynamicLights: Parsing " + path);
            ConnectedParser connectedParser = new ConnectedParser("DynamicLights");
            loadModLightLevels(propertiesOrdered.getProperty("entities"), mapEntityLightLevels, new EntityClassLocator(), connectedParser, path, modId);
            loadModLightLevels(propertiesOrdered.getProperty("items"), mapItemLightLevels, new ItemLocator(), connectedParser, path, modId);
         } catch (IOException ioException) {
            Config.warn("DynamicLights: Error reading " + path);
         }
      }
   }
}
