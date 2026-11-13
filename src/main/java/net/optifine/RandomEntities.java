package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorRaw;
import net.optifine.util.IntegratedServerUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class RandomEntities {
   public static final String TEXTURES_PAINTING = "textures/painting/";
   public static final String PROPERTIES = ".properties";
   private static TileEntityRendererDispatcher tileEntityRendererDispatcher;
   public static final String TEXTURES_ENTITY = "textures/entity/";
   public static final String OPTIFINE_RANDOM = "optifine/random/";
   public static final String TEXTURES = "textures/";
   private static final String HORSE = "horse/";
   public static final String PNG = ".png";
   public static final String MCPATCHER_MOB = "mcpatcher/mob/";
   private static Map<String, RandomEntityProperties> mapProperties = new HashMap<>();
   private static boolean active = false;
   private static RenderGlobal renderGlobal;
   private static RandomEntity randomEntity = new RandomEntity();
   private static RandomTileEntity randomTileEntity = new RandomTileEntity();
   private static boolean working = false;
   private static final String[] DEPENDANT_SUFFIXES = new String[]{
      "_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"
   };
   private static final String[] HORSE_TEXTURES = (String[])ReflectorRaw.createObject(null, EntityHorse.class, String[].class, 2);
   private static final String[] HORSE_TEXTURES_ABBR = (String[])ReflectorRaw.createObject(null, EntityHorse.class, String[].class, 3);

   public static void entityUnloaded(Entity entity, World world) {
   }

   public static void worldChanged(World oldWorld, World newWorld) {
      if (newWorld != null) {
         List loadedEntityList = newWorld.getLoadedEntityList();

         for (int i = 0; i < loadedEntityList.size(); i++) {
            entityLoaded((Entity)loadedEntityList.get(i), newWorld);
         }
      }

      randomEntity.setEntity((Entity)null);
      randomTileEntity.setTileEntity((TileEntity)null);
   }

   public static void dbg(String str) {
      Config.dbg("RandomEntities: " + str);
   }

   private static void updateEntityVillager(UUID uUID, EntityVillager ev) {
      if (IntegratedServerUtils.getEntity(uUID) instanceof EntityVillager entityVillager) {
         int profession = entityVillager.getProfession();
         ev.setProfession(profession);
         int intValue = Reflector.createInt(entityVillager, Reflector.reflectorField23, 0);
         Reflector.checkObject5(ev, Reflector.reflectorField23, intValue);
         int intValue2 = Reflector.createInt(entityVillager, Reflector.reflectorField24, 0);
         Reflector.checkObject5(ev, Reflector.reflectorField24, intValue2);
      }
   }

   private static String getHorseTexturePath(String path, int pos) {
      if (HORSE_TEXTURES != null && HORSE_TEXTURES_ABBR != null) {
         for (int index = 0; index < HORSE_TEXTURES_ABBR.length; index++) {
            String text4 = HORSE_TEXTURES_ABBR[index];
            if (path.startsWith(text4, pos)) {
               return HORSE_TEXTURES[index];
            }
         }

         return path;
      } else {
         return path;
      }
   }

   private static RandomEntityProperties parseProperties(ResourceLocation propLoc, ResourceLocation resLoc) {
      try {
         String resourcePath2 = propLoc.getResourcePath();
         dbg(resLoc.getResourcePath() + ", properties: " + resourcePath2);
         InputStream resourceStream = Config.getResourceStream(propLoc);
         if (resourceStream == null) {
            warn("Properties not found: " + resourcePath2);
            return null;
         } else {
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            RandomEntityProperties randomEntityProperties2 = new RandomEntityProperties(propertiesOrdered, resourcePath2, resLoc);
            return !randomEntityProperties2.isValid(resourcePath2) ? null : randomEntityProperties2;
         }
      } catch (FileNotFoundException fileNotFoundException) {
         warn("File not found: " + resLoc.getResourcePath());
         return null;
      } catch (IOException ioException) {
         ioException.printStackTrace();
         return null;
      }
   }

   private static String getParentTexturePath(String path) {
      for (int i = 0; i < DEPENDANT_SUFFIXES.length; i++) {
         String text4 = DEPENDANT_SUFFIXES[i];
         if (path.endsWith(text4)) {
            return StrUtils.removeSuffix(path, text4);
         }
      }

      return null;
   }

   private static RandomEntityProperties makeProperties(ResourceLocation loc, boolean mcpatcher) {
      String resourcePath = loc.getResourcePath();
      ResourceLocation resourceLocation = getLocationProperties(loc, mcpatcher);
      if (resourceLocation != null) {
         RandomEntityProperties randomEntityProperties = parseProperties(resourceLocation, loc);
         if (randomEntityProperties != null) {
            return randomEntityProperties;
         }
      }

      ResourceLocation[] aresourcelocation = getLocationsVariants(loc, mcpatcher);
      return aresourcelocation == null ? null : new RandomEntityProperties(resourcePath, aresourcelocation);
   }

   public static ResourceLocation getTextureLocation(ResourceLocation loc) {
      if (!active) {
         return loc;
      }

      if (working) {
         return loc;
      }

      ResourceLocation resourceLocation3;
      try {
         working = true;
         IRandomEntity randomEntityRendered = getRandomEntityRendered();
         if (randomEntityRendered != null) {
            String resourcePath2 = loc.getResourcePath();
            if (resourcePath2.startsWith("horse/")) {
               resourcePath2 = getHorseTexturePath(resourcePath2, "horse/".length());
            }

            if (!resourcePath2.startsWith("textures/entity/") && !resourcePath2.startsWith("textures/painting/")) {
               return loc;
            }

            RandomEntityProperties randomEntityProperties2 = mapProperties.get(resourcePath2);
            if (randomEntityProperties2 == null) {
               return loc;
            }

            return randomEntityProperties2.getTextureLocation(loc, randomEntityRendered);
         }

         resourceLocation3 = loc;
      } finally {
         working = false;
      }

      return resourceLocation3;
   }

   protected static ResourceLocation getLocationRandom(ResourceLocation loc, boolean mcpatcher) {
      String text = loc.getResourceDomain();
      String resourcePath = loc.getResourcePath();
      String message = "textures/";
      String message2 = "optifine/random/";
      if (mcpatcher) {
         message = "textures/entity/";
         message2 = "mcpatcher/mob/";
      }

      if (!resourcePath.startsWith(message)) {
         return null;
      }

      String text2 = StrUtils.replacePrefix(resourcePath, message, message2);
      return new ResourceLocation(text, text2);
   }

   public static void update() {
      mapProperties.clear();
      active = false;
      if (Config.isRandomEntities()) {
         initialize();
      }
   }

   private static ResourceLocation getLocationProperties(ResourceLocation loc, boolean mcpatcher) {
      ResourceLocation locationRandom = getLocationRandom(loc, mcpatcher);
      if (locationRandom == null) {
         return null;
      }

      String resourceDomain = locationRandom.getResourceDomain();
      String text4 = StrUtils.removeSuffix(locationRandom.getResourcePath(), ".png");
      String text5 = text4 + ".properties";
      ResourceLocation resourceLocation3 = new ResourceLocation(resourceDomain, text5);
      if (Config.hasResource(resourceLocation3)) {
         return resourceLocation3;
      }

      String parentTexturePath = getParentTexturePath(text4);
      if (parentTexturePath == null) {
         return null;
      }

      ResourceLocation resourceLocation4 = new ResourceLocation(resourceDomain, parentTexturePath + ".properties");
      return Config.hasResource(resourceLocation4) ? resourceLocation4 : null;
   }

   private static void initialize() {
      renderGlobal = Config.getRenderGlobal();
      tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      String[] texts = new String[]{"optifine/random/", "mcpatcher/mob/"};
      String[] texts2 = new String[]{".png", ".properties"};
      String[] stringArray2 = ResUtils.collectFiles(texts, texts2);
      HashSet hashSet = new HashSet();

      for (int index = 0; index < stringArray2.length; index++) {
         String text = getPathBase(StrUtils.trimTrailing(StrUtils.removeSuffix(stringArray2[index], texts2), "0123456789") + ".png");
         if (!hashSet.contains(text)) {
            hashSet.add(text);
            ResourceLocation resourceLocation3 = new ResourceLocation(text);
            if (Config.hasResource(resourceLocation3)) {
               RandomEntityProperties randomEntityProperties = mapProperties.get(text);
               if (randomEntityProperties == null) {
                  randomEntityProperties = makeProperties(resourceLocation3, false);
                  if (randomEntityProperties == null) {
                     randomEntityProperties = makeProperties(resourceLocation3, true);
                  }

                  if (randomEntityProperties != null) {
                     mapProperties.put(text, randomEntityProperties);
                  }
               }
            }
         }
      }

      active = !mapProperties.isEmpty();
   }

   private static String getPathBase(String pathRandom) {
      return pathRandom.startsWith("optifine/random/")
         ? StrUtils.replacePrefix(pathRandom, "optifine/random/", "textures/")
         : (pathRandom.startsWith("mcpatcher/mob/") ? StrUtils.replacePrefix(pathRandom, "mcpatcher/mob/", "textures/entity/") : null);
   }

   public static void entityLoaded(Entity entity, World world) {
      if (world != null) {
         DataWatcher dataWatcher = entity.getDataWatcher();
         dataWatcher.spawnPosition = entity.getPosition();
         dataWatcher.spawnBiome = world.getBiomeGenForCoords(dataWatcher.spawnPosition);
         UUID uUID = entity.getUniqueID();
         if (entity instanceof EntityVillager) {
            updateEntityVillager(uUID, (EntityVillager)entity);
         }
      }
   }

   public static void warn(String str) {
      Config.warn("RandomEntities: " + str);
   }

   private static IRandomEntity getRandomEntityRendered() {
      if (renderGlobal.renderedEntity != null) {
         randomEntity.setEntity(renderGlobal.renderedEntity);
         return randomEntity;
      }

      if (tileEntityRendererDispatcher.tileEntityRendered != null) {
         TileEntity tileEntity = tileEntityRendererDispatcher.tileEntityRendered;
         if (tileEntity.getWorld() != null) {
            randomTileEntity.setTileEntity(tileEntity);
            return randomTileEntity;
         }
      }

      return null;
   }

   protected static ResourceLocation getLocationIndexed(ResourceLocation loc, int index) {
      if (loc == null) {
         return null;
      }

      String text3 = loc.getResourcePath();
      int i = text3.lastIndexOf(46);
      if (i < 0) {
         return null;
      }

      String text4 = text3.substring(0, i);
      String text5 = text3.substring(i);
      String text6 = text4 + index + text5;
      return new ResourceLocation(loc.getResourceDomain(), text6);
   }

   private static ResourceLocation[] getLocationsVariants(ResourceLocation loc, boolean mcpatcher) {
      ArrayList arrayList = new ArrayList();
      arrayList.add(loc);
      ResourceLocation resourceLocation = getLocationRandom(loc, mcpatcher);
      if (resourceLocation == null) {
         return null;
      }

      for (int index = 1; index < arrayList.size() + 10; index++) {
         int i = index + 1;
         ResourceLocation resourceLocation2 = getLocationIndexed(resourceLocation, i);
         if (Config.hasResource(resourceLocation2)) {
            arrayList.add(resourceLocation2);
         }
      }

      if (arrayList.size() <= 1) {
         return null;
      }

      ResourceLocation[] resourceLocations = (ResourceLocation[]) arrayList.toArray(new ResourceLocation[arrayList.size()]);
      dbg(loc.getResourcePath() + ", variants: " + resourceLocations.length);
      return resourceLocations;
   }
}
