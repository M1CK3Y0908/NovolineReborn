package net.optifine;

import java.util.Properties;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.config.VillagerProfession;
import net.optifine.config.Weather;
import net.optifine.reflect.Reflector;
import net.optifine.util.MathUtils;
import wtf.novoline.utils.ArrayUtils;

public class RandomEntityRule {
   private String pathProps = null;
   private ResourceLocation baseResLoc = null;
   private int[] textures = null;
   private ResourceLocation[] resourceLocations = null;
   private int[] weights = null;
   private BiomeGenBase[] biomes = null;
   private RangeListInt heights = null;
   private RangeListInt healthRange = null;
   private boolean healthPercent = false;
   private NbtTagValue nbtName = null;
   public int[] sumWeights = null;
   public int sumAllWeights = 1;
   private VillagerProfession[] professions = null;
   private EnumDyeColor[] collarColors = null;
   private Boolean baby = null;
   private RangeListInt moonPhases = null;
   private RangeListInt dayTimes = null;
   private Weather[] weatherList = null;
   private int index;

   private RangeListInt parseMinMaxHeight(Properties props, int index) {
      String propertyValue = props.getProperty("minHeight." + index);
      String propertyValue2 = props.getProperty("maxHeight." + index);
      if (propertyValue == null && propertyValue2 == null) {
         return null;
      }

      int i = 0;
      if (propertyValue != null) {
         i = Config.parseInt(propertyValue, -1);
         if (i < 0) {
            Config.warn("Invalid minHeight: " + propertyValue);
            return null;
         }
      }

      int i2 = 256;
      if (propertyValue2 != null) {
         i2 = Config.parseInt(propertyValue2, -1);
         if (i2 < 0) {
            Config.warn("Invalid maxHeight: " + propertyValue2);
            return null;
         }
      }

      if (i2 < 0) {
         Config.warn("Invalid minHeight, maxHeight: " + propertyValue + ", " + propertyValue2);
         return null;
      } else {
         RangeListInt rangeListInt = new RangeListInt();
         rangeListInt.addRange(new RangeInt(i, i2));
         return rangeListInt;
      }
   }

   public RandomEntityRule(Properties props, String pathProps, ResourceLocation baseResLoc, int index, String valTextures, ConnectedParser cp) {
      this.pathProps = pathProps;
      this.baseResLoc = baseResLoc;
      this.index = index;
      this.textures = cp.parseIntList(valTextures);
      this.weights = cp.parseIntList(props.getProperty("weights." + index));
      this.biomes = cp.parseBiomes(props.getProperty("biomes." + index));
      this.heights = cp.parseRangeListInt(props.getProperty("heights." + index));
      if (this.heights == null) {
         this.heights = this.parseMinMaxHeight(props, index);
      }

      String property = props.getProperty("health." + index);
      if (property != null) {
         this.healthPercent = property.contains("%");
         property = property.replace("%", "");
         this.healthRange = cp.parseRangeListInt(property);
      }

      this.nbtName = cp.parseNbtTagValue("name", props.getProperty("name." + index));
      this.professions = cp.parseProfessions(props.getProperty("professions." + index));
      this.collarColors = cp.parseDyeColors(props.getProperty("collarColors." + index), "collar color", ConnectedParser.DYE_COLORS_INVALID);
      this.baby = cp.parseBooleanObject(props.getProperty("baby." + index));
      this.moonPhases = cp.parseRangeListInt(props.getProperty("moonPhase." + index));
      this.dayTimes = cp.parseRangeListInt(props.getProperty("dayTime." + index));
      this.weatherList = cp.parseWeather(props.getProperty("weather." + index), "weather." + index, (Weather[])null);
   }

   public boolean isValid(String path) {
      if (this.textures == null || this.textures.length == 0) {
         Config.warn("Invalid skins for rule: " + this.index);
         return false;
      }

      if (this.resourceLocations != null) {
         return true;
      }

      this.resourceLocations = new ResourceLocation[this.textures.length];
      boolean conditionMet = this.pathProps.startsWith("mcpatcher/mob/");
      ResourceLocation resourceLocation = RandomEntities.getLocationRandom(this.baseResLoc, conditionMet);
      if (resourceLocation == null) {
         Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
         return false;
      }

      for (int index = 0; index < this.resourceLocations.length; index++) {
         int i = this.textures[index];
         if (i <= 1) {
            this.resourceLocations[index] = this.baseResLoc;
         } else {
            ResourceLocation resourceLocation2 = RandomEntities.getLocationIndexed(resourceLocation, i);
            if (resourceLocation2 == null) {
               Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
               return false;
            }

            if (!Config.hasResource(resourceLocation2)) {
               Config.warn("Texture not found: " + resourceLocation2.getResourcePath());
               return false;
            }

            this.resourceLocations[index] = resourceLocation2;
         }
      }

      if (this.weights != null) {
         if (this.weights.length > this.resourceLocations.length) {
            Config.warn("More weights defined than skins, trimming weights: " + path);
            int[] intArray = new int[this.resourceLocations.length];
            System.arraycopy(this.weights, 0, intArray, 0, intArray.length);
            this.weights = intArray;
         }

         if (this.weights.length < this.resourceLocations.length) {
            Config.warn("Less weights defined than skins, expanding weights: " + path);
            int[] intArray1 = new int[this.resourceLocations.length];
            System.arraycopy(this.weights, 0, intArray1, 0, this.weights.length);
            int average = MathUtils.getAverage(this.weights);

            for (int innerIndex = this.weights.length; innerIndex < intArray1.length; innerIndex++) {
               intArray1[innerIndex] = average;
            }

            this.weights = intArray1;
         }

         this.sumWeights = new int[this.weights.length];
         int i2 = 0;

         for (int index2 = 0; index2 < this.weights.length; index2++) {
            if (this.weights[index2] < 0) {
               Config.warn("Invalid weight: " + this.weights[index2]);
               return false;
            }

            i2 += this.weights[index2];
            this.sumWeights[index2] = i2;
         }

         this.sumAllWeights = i2;
         if (this.sumAllWeights <= 0) {
            Config.warn("Invalid sum of all weights: " + i2);
            this.sumAllWeights = 1;
         }
      }

      if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
         Config.warn("Invalid professions or careers: " + path);
         return false;
      } else if (this.collarColors == ConnectedParser.DYE_COLORS_INVALID) {
         Config.warn("Invalid collar colors: " + path);
         return false;
      } else {
         return true;
      }
   }

   public boolean matches(IRandomEntity randomEntity) {
      if (this.biomes != null && !Matches.biome(randomEntity.getSpawnBiome(), this.biomes)) {
         return false;
      }

      if (this.heights != null) {
         BlockPos spawnPosition = randomEntity.getSpawnPosition();
         if (spawnPosition != null && !this.heights.isInRange(spawnPosition.getY())) {
            return false;
         }
      }

      if (this.healthRange != null) {
         int health2 = randomEntity.getHealth();
         if (this.healthPercent) {
            int maxHealth = randomEntity.getMaxHealth();
            if (maxHealth > 0) {
               health2 = (int)((double)(health2 * 100) / maxHealth);
            }
         }

         if (!this.healthRange.isInRange(health2)) {
            return false;
         }
      }

      if (this.nbtName != null) {
         String name2 = randomEntity.getName();
         if (!this.nbtName.matchesValue(name2)) {
            return false;
         }
      }

      if (this.professions != null && randomEntity instanceof RandomEntity randomEntity2 && randomEntity2.getEntity() instanceof EntityVillager entityVillager) {
         int profession = entityVillager.getProfession();
         int intValue = Reflector.createInt(entityVillager, Reflector.reflectorField23, -1);
         if (profession < 0 || intValue < 0) {
            return false;
         }

         boolean flag = false;

         for (int i = 0; i < this.professions.length; i++) {
            if (this.professions[i].matches(profession, intValue)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         }
      }

      if (this.collarColors != null && randomEntity instanceof RandomEntity randomEntity3 && randomEntity3.getEntity() instanceof EntityWolf entityWolf) {
         if (!entityWolf.isTamed()) {
            return false;
         }

         EnumDyeColor collarColor = entityWolf.getCollarColor();
         if (!Config.equalsOne(collarColor, this.collarColors)) {
            return false;
         }
      }

      if (this.baby != null && randomEntity instanceof RandomEntity randomEntity4 && randomEntity4.getEntity() instanceof EntityLiving entityLiving && entityLiving.isChild() != this.baby) {
         return false;
      } else {
         if (this.moonPhases != null) {
            WorldClient theWorld2 = Config.getMinecraft().theWorld;
            if (theWorld2 != null) {
               int moonPhase2 = theWorld2.getMoonPhase();
               if (!this.moonPhases.isInRange(moonPhase2)) {
                  return false;
               }
            }
         }

         if (this.dayTimes != null) {
            WorldClient theWorld3 = Config.getMinecraft().theWorld;
            if (theWorld3 != null) {
               int i2 = (int)theWorld3.getWorldInfo().getWorldTime();
               if (!this.dayTimes.isInRange(i2)) {
                  return false;
               }
            }
         }

         if (this.weatherList != null) {
            WorldClient theWorld4 = Config.getMinecraft().theWorld;
            if (theWorld4 != null) {
               Weather weather2 = Weather.getWeather(theWorld4, 0.0F);
               if (!ArrayUtils.contains(this.weatherList, weather2)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public ResourceLocation getTextureLocation(ResourceLocation loc, int randomId) {
      if (this.resourceLocations != null && this.resourceLocations.length != 0) {
         int i = 0;
         if (this.weights == null) {
            i = randomId % this.resourceLocations.length;
         } else {
            int i2 = randomId % this.sumAllWeights;

            for (int nestedIndex = 0; nestedIndex < this.sumWeights.length; nestedIndex++) {
               if (this.sumWeights[nestedIndex] > i2) {
                  i = nestedIndex;
                  break;
               }
            }
         }

         return this.resourceLocations[i];
      } else {
         return loc;
      }
   }
}
