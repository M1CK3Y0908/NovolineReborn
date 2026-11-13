package net.minecraft.world.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.biome.BiomeGenBase;

public class ChunkProviderSettings {
   public final float coordinateScale;
   public final float heightScale;
   public final float upperLimitScale;
   public final float lowerLimitScale;
   public final float depthNoiseScaleX;
   public final float depthNoiseScaleZ;
   public final float depthNoiseScaleExponent;
   public final float mainNoiseScaleX;
   public final float mainNoiseScaleY;
   public final float mainNoiseScaleZ;
   public final float baseSize;
   public final float stretchY;
   public final float biomeDepthWeight;
   public final float biomeDepthOffSet;
   public final float biomeScaleWeight;
   public final float biomeScaleOffset;
   public final int seaLevel;
   public final boolean useCaves;
   public final boolean useDungeons;
   public final int dungeonChance;
   public final boolean useStrongholds;
   public final boolean useVillages;
   public final boolean useMineShafts;
   public final boolean useTemples;
   public final boolean useMonuments;
   public final boolean useRavines;
   public final boolean useWaterLakes;
   public final int waterLakeChance;
   public final boolean useLavaLakes;
   public final int lavaLakeChance;
   public final boolean useLavaOceans;
   public final int fixedBiome;
   public final int biomeSize;
   public final int riverSize;
   public final int dirtSize;
   public final int dirtCount;
   public final int dirtMinHeight;
   public final int dirtMaxHeight;
   public final int gravelSize;
   public final int gravelCount;
   public final int gravelMinHeight;
   public final int gravelMaxHeight;
   public final int graniteSize;
   public final int graniteCount;
   public final int graniteMinHeight;
   public final int graniteMaxHeight;
   public final int dioriteSize;
   public final int dioriteCount;
   public final int dioriteMinHeight;
   public final int dioriteMaxHeight;
   public final int andesiteSize;
   public final int andesiteCount;
   public final int andesiteMinHeight;
   public final int andesiteMaxHeight;
   public final int coalSize;
   public final int coalCount;
   public final int coalMinHeight;
   public final int coalMaxHeight;
   public final int ironSize;
   public final int ironCount;
   public final int ironMinHeight;
   public final int ironMaxHeight;
   public final int goldSize;
   public final int goldCount;
   public final int goldMinHeight;
   public final int goldMaxHeight;
   public final int redstoneSize;
   public final int redstoneCount;
   public final int redstoneMinHeight;
   public final int redstoneMaxHeight;
   public final int diamondSize;
   public final int diamondCount;
   public final int diamondMinHeight;
   public final int diamondMaxHeight;
   public final int lapisSize;
   public final int lapisCount;
   public final int lapisCenterHeight;
   public final int lapisSpread;

   private ChunkProviderSettings(ChunkProviderSettings.Factory settingsFactory) {
      this.coordinateScale = settingsFactory.coordinateScale;
      this.heightScale = settingsFactory.heightScale;
      this.upperLimitScale = settingsFactory.upperLimitScale;
      this.lowerLimitScale = settingsFactory.lowerLimitScale;
      this.depthNoiseScaleX = settingsFactory.depthNoiseScaleX;
      this.depthNoiseScaleZ = settingsFactory.depthNoiseScaleZ;
      this.depthNoiseScaleExponent = settingsFactory.depthNoiseScaleExponent;
      this.mainNoiseScaleX = settingsFactory.mainNoiseScaleX;
      this.mainNoiseScaleY = settingsFactory.mainNoiseScaleY;
      this.mainNoiseScaleZ = settingsFactory.mainNoiseScaleZ;
      this.baseSize = settingsFactory.baseSize;
      this.stretchY = settingsFactory.stretchY;
      this.biomeDepthWeight = settingsFactory.biomeDepthWeight;
      this.biomeDepthOffSet = settingsFactory.biomeDepthOffset;
      this.biomeScaleWeight = settingsFactory.biomeScaleWeight;
      this.biomeScaleOffset = settingsFactory.biomeScaleOffset;
      this.seaLevel = settingsFactory.seaLevel;
      this.useCaves = settingsFactory.useCaves;
      this.useDungeons = settingsFactory.useDungeons;
      this.dungeonChance = settingsFactory.dungeonChance;
      this.useStrongholds = settingsFactory.useStrongholds;
      this.useVillages = settingsFactory.useVillages;
      this.useMineShafts = settingsFactory.useMineShafts;
      this.useTemples = settingsFactory.useTemples;
      this.useMonuments = settingsFactory.useMonuments;
      this.useRavines = settingsFactory.useRavines;
      this.useWaterLakes = settingsFactory.useWaterLakes;
      this.waterLakeChance = settingsFactory.waterLakeChance;
      this.useLavaLakes = settingsFactory.useLavaLakes;
      this.lavaLakeChance = settingsFactory.lavaLakeChance;
      this.useLavaOceans = settingsFactory.useLavaOceans;
      this.fixedBiome = settingsFactory.fixedBiome;
      this.biomeSize = settingsFactory.biomeSize;
      this.riverSize = settingsFactory.riverSize;
      this.dirtSize = settingsFactory.dirtSize;
      this.dirtCount = settingsFactory.dirtCount;
      this.dirtMinHeight = settingsFactory.dirtMinHeight;
      this.dirtMaxHeight = settingsFactory.dirtMaxHeight;
      this.gravelSize = settingsFactory.gravelSize;
      this.gravelCount = settingsFactory.gravelCount;
      this.gravelMinHeight = settingsFactory.gravelMinHeight;
      this.gravelMaxHeight = settingsFactory.gravelMaxHeight;
      this.graniteSize = settingsFactory.graniteSize;
      this.graniteCount = settingsFactory.graniteCount;
      this.graniteMinHeight = settingsFactory.graniteMinHeight;
      this.graniteMaxHeight = settingsFactory.graniteMaxHeight;
      this.dioriteSize = settingsFactory.dioriteSize;
      this.dioriteCount = settingsFactory.dioriteCount;
      this.dioriteMinHeight = settingsFactory.dioriteMinHeight;
      this.dioriteMaxHeight = settingsFactory.dioriteMaxHeight;
      this.andesiteSize = settingsFactory.andesiteSize;
      this.andesiteCount = settingsFactory.andesiteCount;
      this.andesiteMinHeight = settingsFactory.andesiteMinHeight;
      this.andesiteMaxHeight = settingsFactory.andesiteMaxHeight;
      this.coalSize = settingsFactory.coalSize;
      this.coalCount = settingsFactory.coalCount;
      this.coalMinHeight = settingsFactory.coalMinHeight;
      this.coalMaxHeight = settingsFactory.coalMaxHeight;
      this.ironSize = settingsFactory.ironSize;
      this.ironCount = settingsFactory.ironCount;
      this.ironMinHeight = settingsFactory.ironMinHeight;
      this.ironMaxHeight = settingsFactory.ironMaxHeight;
      this.goldSize = settingsFactory.goldSize;
      this.goldCount = settingsFactory.goldCount;
      this.goldMinHeight = settingsFactory.goldMinHeight;
      this.goldMaxHeight = settingsFactory.goldMaxHeight;
      this.redstoneSize = settingsFactory.redstoneSize;
      this.redstoneCount = settingsFactory.redstoneCount;
      this.redstoneMinHeight = settingsFactory.redstoneMinHeight;
      this.redstoneMaxHeight = settingsFactory.redstoneMaxHeight;
      this.diamondSize = settingsFactory.diamondSize;
      this.diamondCount = settingsFactory.diamondCount;
      this.diamondMinHeight = settingsFactory.diamondMinHeight;
      this.diamondMaxHeight = settingsFactory.diamondMaxHeight;
      this.lapisSize = settingsFactory.lapisSize;
      this.lapisCount = settingsFactory.lapisCount;
      this.lapisCenterHeight = settingsFactory.lapisCenterHeight;
      this.lapisSpread = settingsFactory.lapisSpread;
   }

   public static class Factory {
      public float coordinateScale = 684.412F;
      public float heightScale = 684.412F;
      public float upperLimitScale = 512.0F;
      public float lowerLimitScale = 512.0F;
      public float depthNoiseScaleX = 200.0F;
      public float depthNoiseScaleZ = 200.0F;
      public float depthNoiseScaleExponent = 0.5F;
      public float mainNoiseScaleX = 80.0F;
      public float mainNoiseScaleY = 160.0F;
      public float mainNoiseScaleZ = 80.0F;
      public float baseSize = 8.5F;
      public float stretchY = 12.0F;
      public float biomeDepthWeight = 1.0F;
      public float biomeDepthOffset = 0.0F;
      public float biomeScaleWeight = 1.0F;
      public float biomeScaleOffset = 0.0F;
      static final Gson JSON_ADAPTER = new GsonBuilder()
         .registerTypeAdapter(ChunkProviderSettings.Factory.class, new ChunkProviderSettings.Serializer())
         .create();
      public int seaLevel = 63;
      public boolean useCaves = true;
      public boolean useDungeons = true;
      public int dungeonChance = 8;
      public boolean useStrongholds = true;
      public boolean useVillages = true;
      public boolean useMineShafts = true;
      public boolean useTemples = true;
      public boolean useMonuments = true;
      public boolean useRavines = true;
      public boolean useWaterLakes = true;
      public int waterLakeChance = 4;
      public boolean useLavaLakes = true;
      public int lavaLakeChance = 80;
      public boolean useLavaOceans = false;
      public int fixedBiome = -1;
      public int biomeSize = 4;
      public int riverSize = 4;
      public int dirtSize = 33;
      public int dirtCount = 10;
      public int dirtMinHeight = 0;
      public int dirtMaxHeight = 256;
      public int gravelSize = 33;
      public int gravelCount = 8;
      public int gravelMinHeight = 0;
      public int gravelMaxHeight = 256;
      public int graniteSize = 33;
      public int graniteCount = 10;
      public int graniteMinHeight = 0;
      public int graniteMaxHeight = 80;
      public int dioriteSize = 33;
      public int dioriteCount = 10;
      public int dioriteMinHeight = 0;
      public int dioriteMaxHeight = 80;
      public int andesiteSize = 33;
      public int andesiteCount = 10;
      public int andesiteMinHeight = 0;
      public int andesiteMaxHeight = 80;
      public int coalSize = 17;
      public int coalCount = 20;
      public int coalMinHeight = 0;
      public int coalMaxHeight = 128;
      public int ironSize = 9;
      public int ironCount = 20;
      public int ironMinHeight = 0;
      public int ironMaxHeight = 64;
      public int goldSize = 9;
      public int goldCount = 2;
      public int goldMinHeight = 0;
      public int goldMaxHeight = 32;
      public int redstoneSize = 8;
      public int redstoneCount = 8;
      public int redstoneMinHeight = 0;
      public int redstoneMaxHeight = 16;
      public int diamondSize = 8;
      public int diamondCount = 1;
      public int diamondMinHeight = 0;
      public int diamondMaxHeight = 16;
      public int lapisSize = 7;
      public int lapisCount = 1;
      public int lapisCenterHeight = 16;
      public int lapisSpread = 16;

      public Factory() {
         this.setDefaults();
      }

      public ChunkProviderSettings build() {
         return new ChunkProviderSettings(this);
      }

      public void setDefaults() {
         this.coordinateScale = 684.412F;
         this.heightScale = 684.412F;
         this.upperLimitScale = 512.0F;
         this.lowerLimitScale = 512.0F;
         this.depthNoiseScaleX = 200.0F;
         this.depthNoiseScaleZ = 200.0F;
         this.depthNoiseScaleExponent = 0.5F;
         this.mainNoiseScaleX = 80.0F;
         this.mainNoiseScaleY = 160.0F;
         this.mainNoiseScaleZ = 80.0F;
         this.baseSize = 8.5F;
         this.stretchY = 12.0F;
         this.biomeDepthWeight = 1.0F;
         this.biomeDepthOffset = 0.0F;
         this.biomeScaleWeight = 1.0F;
         this.biomeScaleOffset = 0.0F;
         this.seaLevel = 63;
         this.useCaves = true;
         this.useDungeons = true;
         this.dungeonChance = 8;
         this.useStrongholds = true;
         this.useVillages = true;
         this.useMineShafts = true;
         this.useTemples = true;
         this.useMonuments = true;
         this.useRavines = true;
         this.useWaterLakes = true;
         this.waterLakeChance = 4;
         this.useLavaLakes = true;
         this.lavaLakeChance = 80;
         this.useLavaOceans = false;
         this.fixedBiome = -1;
         this.biomeSize = 4;
         this.riverSize = 4;
         this.dirtSize = 33;
         this.dirtCount = 10;
         this.dirtMinHeight = 0;
         this.dirtMaxHeight = 256;
         this.gravelSize = 33;
         this.gravelCount = 8;
         this.gravelMinHeight = 0;
         this.gravelMaxHeight = 256;
         this.graniteSize = 33;
         this.graniteCount = 10;
         this.graniteMinHeight = 0;
         this.graniteMaxHeight = 80;
         this.dioriteSize = 33;
         this.dioriteCount = 10;
         this.dioriteMinHeight = 0;
         this.dioriteMaxHeight = 80;
         this.andesiteSize = 33;
         this.andesiteCount = 10;
         this.andesiteMinHeight = 0;
         this.andesiteMaxHeight = 80;
         this.coalSize = 17;
         this.coalCount = 20;
         this.coalMinHeight = 0;
         this.coalMaxHeight = 128;
         this.ironSize = 9;
         this.ironCount = 20;
         this.ironMinHeight = 0;
         this.ironMaxHeight = 64;
         this.goldSize = 9;
         this.goldCount = 2;
         this.goldMinHeight = 0;
         this.goldMaxHeight = 32;
         this.redstoneSize = 8;
         this.redstoneCount = 8;
         this.redstoneMinHeight = 0;
         this.redstoneMaxHeight = 16;
         this.diamondSize = 8;
         this.diamondCount = 1;
         this.diamondMinHeight = 0;
         this.diamondMaxHeight = 16;
         this.lapisSize = 7;
         this.lapisCount = 1;
         this.lapisCenterHeight = 16;
         this.lapisSpread = 16;
      }

      @Override
      public String toString() {
         return JSON_ADAPTER.toJson(this);
      }

      public static ChunkProviderSettings.Factory jsonToFactory(String json) {
         if (json.length() == 0) {
            return new ChunkProviderSettings.Factory();
         }

         try {
            return JSON_ADAPTER.fromJson(json, ChunkProviderSettings.Factory.class);
         } catch (Exception exception) {
            return new ChunkProviderSettings.Factory();
         }
      }

      @Override
      public int hashCode() {
         int i = this.coordinateScale != 0.0F ? Float.floatToIntBits(this.coordinateScale) : 0;
         i = 31 * i + (this.heightScale != 0.0F ? Float.floatToIntBits(this.heightScale) : 0);
         i = 31 * i + (this.upperLimitScale != 0.0F ? Float.floatToIntBits(this.upperLimitScale) : 0);
         i = 31 * i + (this.lowerLimitScale != 0.0F ? Float.floatToIntBits(this.lowerLimitScale) : 0);
         i = 31 * i + (this.depthNoiseScaleX != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleX) : 0);
         i = 31 * i + (this.depthNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleZ) : 0);
         i = 31 * i + (this.depthNoiseScaleExponent != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleExponent) : 0);
         i = 31 * i + (this.mainNoiseScaleX != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleX) : 0);
         i = 31 * i + (this.mainNoiseScaleY != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleY) : 0);
         i = 31 * i + (this.mainNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleZ) : 0);
         i = 31 * i + (this.baseSize != 0.0F ? Float.floatToIntBits(this.baseSize) : 0);
         i = 31 * i + (this.stretchY != 0.0F ? Float.floatToIntBits(this.stretchY) : 0);
         i = 31 * i + (this.biomeDepthWeight != 0.0F ? Float.floatToIntBits(this.biomeDepthWeight) : 0);
         i = 31 * i + (this.biomeDepthOffset != 0.0F ? Float.floatToIntBits(this.biomeDepthOffset) : 0);
         i = 31 * i + (this.biomeScaleWeight != 0.0F ? Float.floatToIntBits(this.biomeScaleWeight) : 0);
         i = 31 * i + (this.biomeScaleOffset != 0.0F ? Float.floatToIntBits(this.biomeScaleOffset) : 0);
         i = 31 * i + this.seaLevel;
         i = 31 * i + (this.useCaves ? 1 : 0);
         i = 31 * i + (this.useDungeons ? 1 : 0);
         i = 31 * i + this.dungeonChance;
         i = 31 * i + (this.useStrongholds ? 1 : 0);
         i = 31 * i + (this.useVillages ? 1 : 0);
         i = 31 * i + (this.useMineShafts ? 1 : 0);
         i = 31 * i + (this.useTemples ? 1 : 0);
         i = 31 * i + (this.useMonuments ? 1 : 0);
         i = 31 * i + (this.useRavines ? 1 : 0);
         i = 31 * i + (this.useWaterLakes ? 1 : 0);
         i = 31 * i + this.waterLakeChance;
         i = 31 * i + (this.useLavaLakes ? 1 : 0);
         i = 31 * i + this.lavaLakeChance;
         i = 31 * i + (this.useLavaOceans ? 1 : 0);
         i = 31 * i + this.fixedBiome;
         i = 31 * i + this.biomeSize;
         i = 31 * i + this.riverSize;
         i = 31 * i + this.dirtSize;
         i = 31 * i + this.dirtCount;
         i = 31 * i + this.dirtMinHeight;
         i = 31 * i + this.dirtMaxHeight;
         i = 31 * i + this.gravelSize;
         i = 31 * i + this.gravelCount;
         i = 31 * i + this.gravelMinHeight;
         i = 31 * i + this.gravelMaxHeight;
         i = 31 * i + this.graniteSize;
         i = 31 * i + this.graniteCount;
         i = 31 * i + this.graniteMinHeight;
         i = 31 * i + this.graniteMaxHeight;
         i = 31 * i + this.dioriteSize;
         i = 31 * i + this.dioriteCount;
         i = 31 * i + this.dioriteMinHeight;
         i = 31 * i + this.dioriteMaxHeight;
         i = 31 * i + this.andesiteSize;
         i = 31 * i + this.andesiteCount;
         i = 31 * i + this.andesiteMinHeight;
         i = 31 * i + this.andesiteMaxHeight;
         i = 31 * i + this.coalSize;
         i = 31 * i + this.coalCount;
         i = 31 * i + this.coalMinHeight;
         i = 31 * i + this.coalMaxHeight;
         i = 31 * i + this.ironSize;
         i = 31 * i + this.ironCount;
         i = 31 * i + this.ironMinHeight;
         i = 31 * i + this.ironMaxHeight;
         i = 31 * i + this.goldSize;
         i = 31 * i + this.goldCount;
         i = 31 * i + this.goldMinHeight;
         i = 31 * i + this.goldMaxHeight;
         i = 31 * i + this.redstoneSize;
         i = 31 * i + this.redstoneCount;
         i = 31 * i + this.redstoneMinHeight;
         i = 31 * i + this.redstoneMaxHeight;
         i = 31 * i + this.diamondSize;
         i = 31 * i + this.diamondCount;
         i = 31 * i + this.diamondMinHeight;
         i = 31 * i + this.diamondMaxHeight;
         i = 31 * i + this.lapisSize;
         i = 31 * i + this.lapisCount;
         i = 31 * i + this.lapisCenterHeight;
         return 31 * i + this.lapisSpread;
      }

      @Override
      public boolean equals(Object arg1) {
         if (this == arg1) {
            return true;
         } else if (arg1 != null && this.getClass() == arg1.getClass()) {
            ChunkProviderSettings.Factory chunkprovidersettings$factory = (ChunkProviderSettings.Factory)arg1;
            return this.andesiteCount != chunkprovidersettings$factory.andesiteCount
               ? false
               : (
                  this.andesiteMaxHeight != chunkprovidersettings$factory.andesiteMaxHeight
                     ? false
                     : (
                        this.andesiteMinHeight != chunkprovidersettings$factory.andesiteMinHeight
                           ? false
                           : (
                              this.andesiteSize != chunkprovidersettings$factory.andesiteSize
                                 ? false
                                 : (
                                    Float.compare(chunkprovidersettings$factory.baseSize, this.baseSize) != 0
                                       ? false
                                       : (
                                          Float.compare(chunkprovidersettings$factory.biomeDepthOffset, this.biomeDepthOffset) != 0
                                             ? false
                                             : (
                                                Float.compare(chunkprovidersettings$factory.biomeDepthWeight, this.biomeDepthWeight) != 0
                                                   ? false
                                                   : (
                                                      Float.compare(chunkprovidersettings$factory.biomeScaleOffset, this.biomeScaleOffset) != 0
                                                         ? false
                                                         : (
                                                            Float.compare(chunkprovidersettings$factory.biomeScaleWeight, this.biomeScaleWeight) != 0
                                                               ? false
                                                               : (
                                                                  this.biomeSize != chunkprovidersettings$factory.biomeSize
                                                                     ? false
                                                                     : (
                                                                        this.coalCount != chunkprovidersettings$factory.coalCount
                                                                           ? false
                                                                           : (
                                                                              this.coalMaxHeight != chunkprovidersettings$factory.coalMaxHeight
                                                                                 ? false
                                                                                 : (
                                                                                    this.coalMinHeight != chunkprovidersettings$factory.coalMinHeight
                                                                                       ? false
                                                                                       : (
                                                                                          this.coalSize != chunkprovidersettings$factory.coalSize
                                                                                             ? false
                                                                                             : (
                                                                                                Float.compare(
                                                                                                         chunkprovidersettings$factory.coordinateScale,
                                                                                                         this.coordinateScale
                                                                                                      )
                                                                                                      != 0
                                                                                                   ? false
                                                                                                   : (
                                                                                                      Float.compare(
                                                                                                               chunkprovidersettings$factory.depthNoiseScaleExponent,
                                                                                                               this.depthNoiseScaleExponent
                                                                                                            )
                                                                                                            != 0
                                                                                                         ? false
                                                                                                         : (
                                                                                                            Float.compare(
                                                                                                                     chunkprovidersettings$factory.depthNoiseScaleX,
                                                                                                                     this.depthNoiseScaleX
                                                                                                                  )
                                                                                                                  != 0
                                                                                                               ? false
                                                                                                               : (
                                                                                                                  Float.compare(
                                                                                                                           chunkprovidersettings$factory.depthNoiseScaleZ,
                                                                                                                           this.depthNoiseScaleZ
                                                                                                                        )
                                                                                                                        != 0
                                                                                                                     ? false
                                                                                                                     : (
                                                                                                                        this.diamondCount
                                                                                                                              != chunkprovidersettings$factory.diamondCount
                                                                                                                           ? false
                                                                                                                           : (
                                                                                                                              this.diamondMaxHeight
                                                                                                                                    != chunkprovidersettings$factory.diamondMaxHeight
                                                                                                                                 ? false
                                                                                                                                 : (
                                                                                                                                    this.diamondMinHeight
                                                                                                                                          != chunkprovidersettings$factory.diamondMinHeight
                                                                                                                                       ? false
                                                                                                                                       : (
                                                                                                                                          this.diamondSize
                                                                                                                                                != chunkprovidersettings$factory.diamondSize
                                                                                                                                             ? false
                                                                                                                                             : (
                                                                                                                                                this.dioriteCount
                                                                                                                                                      != chunkprovidersettings$factory.dioriteCount
                                                                                                                                                   ? false
                                                                                                                                                   : (
                                                                                                                                                      this.dioriteMaxHeight
                                                                                                                                                            != chunkprovidersettings$factory.dioriteMaxHeight
                                                                                                                                                         ? false
                                                                                                                                                         : (
                                                                                                                                                            this.dioriteMinHeight
                                                                                                                                                                  != chunkprovidersettings$factory.dioriteMinHeight
                                                                                                                                                               ? false
                                                                                                                                                               : (
                                                                                                                                                                  this.dioriteSize
                                                                                                                                                                        != chunkprovidersettings$factory.dioriteSize
                                                                                                                                                                     ? false
                                                                                                                                                                     : (
                                                                                                                                                                        this.dirtCount
                                                                                                                                                                              != chunkprovidersettings$factory.dirtCount
                                                                                                                                                                           ? false
                                                                                                                                                                           : (
                                                                                                                                                                              this.dirtMaxHeight
                                                                                                                                                                                    != chunkprovidersettings$factory.dirtMaxHeight
                                                                                                                                                                                 ? false
                                                                                                                                                                                 : (
                                                                                                                                                                                    this.dirtMinHeight
                                                                                                                                                                                          != chunkprovidersettings$factory.dirtMinHeight
                                                                                                                                                                                       ? false
                                                                                                                                                                                       : (
                                                                                                                                                                                          this.dirtSize
                                                                                                                                                                                                != chunkprovidersettings$factory.dirtSize
                                                                                                                                                                                             ? false
                                                                                                                                                                                             : (
                                                                                                                                                                                                this.dungeonChance
                                                                                                                                                                                                      != chunkprovidersettings$factory.dungeonChance
                                                                                                                                                                                                   ? false
                                                                                                                                                                                                   : (
                                                                                                                                                                                                      this.fixedBiome
                                                                                                                                                                                                            != chunkprovidersettings$factory.fixedBiome
                                                                                                                                                                                                         ? false
                                                                                                                                                                                                         : (
                                                                                                                                                                                                            this.goldCount
                                                                                                                                                                                                                  != chunkprovidersettings$factory.goldCount
                                                                                                                                                                                                               ? false
                                                                                                                                                                                                               : (
                                                                                                                                                                                                                  this.goldMaxHeight
                                                                                                                                                                                                                        != chunkprovidersettings$factory.goldMaxHeight
                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                        this.goldMinHeight
                                                                                                                                                                                                                              != chunkprovidersettings$factory.goldMinHeight
                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                              this.goldSize
                                                                                                                                                                                                                                    != chunkprovidersettings$factory.goldSize
                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                    this.graniteCount
                                                                                                                                                                                                                                          != chunkprovidersettings$factory.graniteCount
                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                          this.graniteMaxHeight
                                                                                                                                                                                                                                                != chunkprovidersettings$factory.graniteMaxHeight
                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                this.graniteMinHeight
                                                                                                                                                                                                                                                      != chunkprovidersettings$factory.graniteMinHeight
                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                      this.graniteSize
                                                                                                                                                                                                                                                            != chunkprovidersettings$factory.graniteSize
                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                            this.gravelCount
                                                                                                                                                                                                                                                                  != chunkprovidersettings$factory.gravelCount
                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                  this.gravelMaxHeight
                                                                                                                                                                                                                                                                        != chunkprovidersettings$factory.gravelMaxHeight
                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                        this.gravelMinHeight
                                                                                                                                                                                                                                                                              != chunkprovidersettings$factory.gravelMinHeight
                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                              this.gravelSize
                                                                                                                                                                                                                                                                                    != chunkprovidersettings$factory.gravelSize
                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                    Float.compare(
                                                                                                                                                                                                                                                                                             chunkprovidersettings$factory.heightScale,
                                                                                                                                                                                                                                                                                             this.heightScale
                                                                                                                                                                                                                                                                                          )
                                                                                                                                                                                                                                                                                          != 0
                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                          this.ironCount
                                                                                                                                                                                                                                                                                                != chunkprovidersettings$factory.ironCount
                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                this.ironMaxHeight
                                                                                                                                                                                                                                                                                                      != chunkprovidersettings$factory.ironMaxHeight
                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                      this.ironMinHeight
                                                                                                                                                                                                                                                                                                            != chunkprovidersettings$factory.ironMinHeight
                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                            this.ironSize
                                                                                                                                                                                                                                                                                                                  != chunkprovidersettings$factory.ironSize
                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                  this.lapisCenterHeight
                                                                                                                                                                                                                                                                                                                        != chunkprovidersettings$factory.lapisCenterHeight
                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                        this.lapisCount
                                                                                                                                                                                                                                                                                                                              != chunkprovidersettings$factory.lapisCount
                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                              this.lapisSize
                                                                                                                                                                                                                                                                                                                                    != chunkprovidersettings$factory.lapisSize
                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                    this.lapisSpread
                                                                                                                                                                                                                                                                                                                                          != chunkprovidersettings$factory.lapisSpread
                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                          this.lavaLakeChance
                                                                                                                                                                                                                                                                                                                                                != chunkprovidersettings$factory.lavaLakeChance
                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                Float.compare(
                                                                                                                                                                                                                                                                                                                                                         chunkprovidersettings$factory.lowerLimitScale,
                                                                                                                                                                                                                                                                                                                                                         this.lowerLimitScale
                                                                                                                                                                                                                                                                                                                                                      )
                                                                                                                                                                                                                                                                                                                                                      != 0
                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                      Float.compare(
                                                                                                                                                                                                                                                                                                                                                               chunkprovidersettings$factory.mainNoiseScaleX,
                                                                                                                                                                                                                                                                                                                                                               this.mainNoiseScaleX
                                                                                                                                                                                                                                                                                                                                                            )
                                                                                                                                                                                                                                                                                                                                                            != 0
                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                            Float.compare(
                                                                                                                                                                                                                                                                                                                                                                     chunkprovidersettings$factory.mainNoiseScaleY,
                                                                                                                                                                                                                                                                                                                                                                     this.mainNoiseScaleY
                                                                                                                                                                                                                                                                                                                                                                  )
                                                                                                                                                                                                                                                                                                                                                                  != 0
                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                  Float.compare(
                                                                                                                                                                                                                                                                                                                                                                           chunkprovidersettings$factory.mainNoiseScaleZ,
                                                                                                                                                                                                                                                                                                                                                                           this.mainNoiseScaleZ
                                                                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                                                                        != 0
                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                        this.redstoneCount
                                                                                                                                                                                                                                                                                                                                                                              != chunkprovidersettings$factory.redstoneCount
                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                              this.redstoneMaxHeight
                                                                                                                                                                                                                                                                                                                                                                                    != chunkprovidersettings$factory.redstoneMaxHeight
                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                    this.redstoneMinHeight
                                                                                                                                                                                                                                                                                                                                                                                          != chunkprovidersettings$factory.redstoneMinHeight
                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                                                                          this.redstoneSize
                                                                                                                                                                                                                                                                                                                                                                                                != chunkprovidersettings$factory.redstoneSize
                                                                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                                                                this.riverSize
                                                                                                                                                                                                                                                                                                                                                                                                      != chunkprovidersettings$factory.riverSize
                                                                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                                                                      this.seaLevel
                                                                                                                                                                                                                                                                                                                                                                                                            != chunkprovidersettings$factory.seaLevel
                                                                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                                                                            Float.compare(
                                                                                                                                                                                                                                                                                                                                                                                                                     chunkprovidersettings$factory.stretchY,
                                                                                                                                                                                                                                                                                                                                                                                                                     this.stretchY
                                                                                                                                                                                                                                                                                                                                                                                                                  )
                                                                                                                                                                                                                                                                                                                                                                                                                  != 0
                                                                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                                                                  Float.compare(
                                                                                                                                                                                                                                                                                                                                                                                                                           chunkprovidersettings$factory.upperLimitScale,
                                                                                                                                                                                                                                                                                                                                                                                                                           this.upperLimitScale
                                                                                                                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                                                                                                                        != 0
                                                                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                                                                        this.useCaves
                                                                                                                                                                                                                                                                                                                                                                                                                              != chunkprovidersettings$factory.useCaves
                                                                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                                                                              this.useDungeons
                                                                                                                                                                                                                                                                                                                                                                                                                                    != chunkprovidersettings$factory.useDungeons
                                                                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                                                                    this.useLavaLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                          != chunkprovidersettings$factory.useLavaLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                                                                                                                          this.useLavaOceans
                                                                                                                                                                                                                                                                                                                                                                                                                                                != chunkprovidersettings$factory.useLavaOceans
                                                                                                                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                this.useMineShafts
                                                                                                                                                                                                                                                                                                                                                                                                                                                      != chunkprovidersettings$factory.useMineShafts
                                                                                                                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                      this.useRavines
                                                                                                                                                                                                                                                                                                                                                                                                                                                            != chunkprovidersettings$factory.useRavines
                                                                                                                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                            this.useStrongholds
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  != chunkprovidersettings$factory.useStrongholds
                                                                                                                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  this.useTemples
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        != chunkprovidersettings$factory.useTemples
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        this.useMonuments
                                                                                                                                                                                                                                                                                                                                                                                                                                                                              != chunkprovidersettings$factory.useMonuments
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                              this.useVillages
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    != chunkprovidersettings$factory.useVillages
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    this.useWaterLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          != chunkprovidersettings$factory.useWaterLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       : this.waterLakeChance
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          == chunkprovidersettings$factory.waterLakeChance
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                     )
                                                                                                                                                                                                               )
                                                                                                                                                                                                         )
                                                                                                                                                                                                   )
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                 )
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               )
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               );
         } else {
            return false;
         }
      }
   }

   public static class Serializer implements JsonDeserializer<ChunkProviderSettings.Factory>, JsonSerializer<ChunkProviderSettings.Factory> {
      public JsonElement serialize(ChunkProviderSettings.Factory arg1, Type arg2, JsonSerializationContext arg3) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("coordinateScale", arg1.coordinateScale);
         jsonObject2.addProperty("heightScale", arg1.heightScale);
         jsonObject2.addProperty("lowerLimitScale", arg1.lowerLimitScale);
         jsonObject2.addProperty("upperLimitScale", arg1.upperLimitScale);
         jsonObject2.addProperty("depthNoiseScaleX", arg1.depthNoiseScaleX);
         jsonObject2.addProperty("depthNoiseScaleZ", arg1.depthNoiseScaleZ);
         jsonObject2.addProperty("depthNoiseScaleExponent", arg1.depthNoiseScaleExponent);
         jsonObject2.addProperty("mainNoiseScaleX", arg1.mainNoiseScaleX);
         jsonObject2.addProperty("mainNoiseScaleY", arg1.mainNoiseScaleY);
         jsonObject2.addProperty("mainNoiseScaleZ", arg1.mainNoiseScaleZ);
         jsonObject2.addProperty("baseSize", arg1.baseSize);
         jsonObject2.addProperty("stretchY", arg1.stretchY);
         jsonObject2.addProperty("biomeDepthWeight", arg1.biomeDepthWeight);
         jsonObject2.addProperty("biomeDepthOffset", arg1.biomeDepthOffset);
         jsonObject2.addProperty("biomeScaleWeight", arg1.biomeScaleWeight);
         jsonObject2.addProperty("biomeScaleOffset", arg1.biomeScaleOffset);
         jsonObject2.addProperty("seaLevel", arg1.seaLevel);
         jsonObject2.addProperty("useCaves", arg1.useCaves);
         jsonObject2.addProperty("useDungeons", arg1.useDungeons);
         jsonObject2.addProperty("dungeonChance", arg1.dungeonChance);
         jsonObject2.addProperty("useStrongholds", arg1.useStrongholds);
         jsonObject2.addProperty("useVillages", arg1.useVillages);
         jsonObject2.addProperty("useMineShafts", arg1.useMineShafts);
         jsonObject2.addProperty("useTemples", arg1.useTemples);
         jsonObject2.addProperty("useMonuments", arg1.useMonuments);
         jsonObject2.addProperty("useRavines", arg1.useRavines);
         jsonObject2.addProperty("useWaterLakes", arg1.useWaterLakes);
         jsonObject2.addProperty("waterLakeChance", arg1.waterLakeChance);
         jsonObject2.addProperty("useLavaLakes", arg1.useLavaLakes);
         jsonObject2.addProperty("lavaLakeChance", arg1.lavaLakeChance);
         jsonObject2.addProperty("useLavaOceans", arg1.useLavaOceans);
         jsonObject2.addProperty("fixedBiome", arg1.fixedBiome);
         jsonObject2.addProperty("biomeSize", arg1.biomeSize);
         jsonObject2.addProperty("riverSize", arg1.riverSize);
         jsonObject2.addProperty("dirtSize", arg1.dirtSize);
         jsonObject2.addProperty("dirtCount", arg1.dirtCount);
         jsonObject2.addProperty("dirtMinHeight", arg1.dirtMinHeight);
         jsonObject2.addProperty("dirtMaxHeight", arg1.dirtMaxHeight);
         jsonObject2.addProperty("gravelSize", arg1.gravelSize);
         jsonObject2.addProperty("gravelCount", arg1.gravelCount);
         jsonObject2.addProperty("gravelMinHeight", arg1.gravelMinHeight);
         jsonObject2.addProperty("gravelMaxHeight", arg1.gravelMaxHeight);
         jsonObject2.addProperty("graniteSize", arg1.graniteSize);
         jsonObject2.addProperty("graniteCount", arg1.graniteCount);
         jsonObject2.addProperty("graniteMinHeight", arg1.graniteMinHeight);
         jsonObject2.addProperty("graniteMaxHeight", arg1.graniteMaxHeight);
         jsonObject2.addProperty("dioriteSize", arg1.dioriteSize);
         jsonObject2.addProperty("dioriteCount", arg1.dioriteCount);
         jsonObject2.addProperty("dioriteMinHeight", arg1.dioriteMinHeight);
         jsonObject2.addProperty("dioriteMaxHeight", arg1.dioriteMaxHeight);
         jsonObject2.addProperty("andesiteSize", arg1.andesiteSize);
         jsonObject2.addProperty("andesiteCount", arg1.andesiteCount);
         jsonObject2.addProperty("andesiteMinHeight", arg1.andesiteMinHeight);
         jsonObject2.addProperty("andesiteMaxHeight", arg1.andesiteMaxHeight);
         jsonObject2.addProperty("coalSize", arg1.coalSize);
         jsonObject2.addProperty("coalCount", arg1.coalCount);
         jsonObject2.addProperty("coalMinHeight", arg1.coalMinHeight);
         jsonObject2.addProperty("coalMaxHeight", arg1.coalMaxHeight);
         jsonObject2.addProperty("ironSize", arg1.ironSize);
         jsonObject2.addProperty("ironCount", arg1.ironCount);
         jsonObject2.addProperty("ironMinHeight", arg1.ironMinHeight);
         jsonObject2.addProperty("ironMaxHeight", arg1.ironMaxHeight);
         jsonObject2.addProperty("goldSize", arg1.goldSize);
         jsonObject2.addProperty("goldCount", arg1.goldCount);
         jsonObject2.addProperty("goldMinHeight", arg1.goldMinHeight);
         jsonObject2.addProperty("goldMaxHeight", arg1.goldMaxHeight);
         jsonObject2.addProperty("redstoneSize", arg1.redstoneSize);
         jsonObject2.addProperty("redstoneCount", arg1.redstoneCount);
         jsonObject2.addProperty("redstoneMinHeight", arg1.redstoneMinHeight);
         jsonObject2.addProperty("redstoneMaxHeight", arg1.redstoneMaxHeight);
         jsonObject2.addProperty("diamondSize", arg1.diamondSize);
         jsonObject2.addProperty("diamondCount", arg1.diamondCount);
         jsonObject2.addProperty("diamondMinHeight", arg1.diamondMinHeight);
         jsonObject2.addProperty("diamondMaxHeight", arg1.diamondMaxHeight);
         jsonObject2.addProperty("lapisSize", arg1.lapisSize);
         jsonObject2.addProperty("lapisCount", arg1.lapisCount);
         jsonObject2.addProperty("lapisCenterHeight", arg1.lapisCenterHeight);
         jsonObject2.addProperty("lapisSpread", arg1.lapisSpread);
         return jsonObject2;
      }

      public ChunkProviderSettings.Factory deserialize(JsonElement arg1, Type arg2, JsonDeserializationContext arg3) throws JsonParseException {
         JsonObject jsonObject = arg1.getAsJsonObject();
         ChunkProviderSettings.Factory factory = new ChunkProviderSettings.Factory();

         try {
            factory.coordinateScale = JsonUtils.getFloat(jsonObject, "coordinateScale", factory.coordinateScale);
            factory.heightScale = JsonUtils.getFloat(jsonObject, "heightScale", factory.heightScale);
            factory.lowerLimitScale = JsonUtils.getFloat(jsonObject, "lowerLimitScale", factory.lowerLimitScale);
            factory.upperLimitScale = JsonUtils.getFloat(jsonObject, "upperLimitScale", factory.upperLimitScale);
            factory.depthNoiseScaleX = JsonUtils.getFloat(jsonObject, "depthNoiseScaleX", factory.depthNoiseScaleX);
            factory.depthNoiseScaleZ = JsonUtils.getFloat(jsonObject, "depthNoiseScaleZ", factory.depthNoiseScaleZ);
            factory.depthNoiseScaleExponent = JsonUtils.getFloat(jsonObject, "depthNoiseScaleExponent", factory.depthNoiseScaleExponent);
            factory.mainNoiseScaleX = JsonUtils.getFloat(jsonObject, "mainNoiseScaleX", factory.mainNoiseScaleX);
            factory.mainNoiseScaleY = JsonUtils.getFloat(jsonObject, "mainNoiseScaleY", factory.mainNoiseScaleY);
            factory.mainNoiseScaleZ = JsonUtils.getFloat(jsonObject, "mainNoiseScaleZ", factory.mainNoiseScaleZ);
            factory.baseSize = JsonUtils.getFloat(jsonObject, "baseSize", factory.baseSize);
            factory.stretchY = JsonUtils.getFloat(jsonObject, "stretchY", factory.stretchY);
            factory.biomeDepthWeight = JsonUtils.getFloat(jsonObject, "biomeDepthWeight", factory.biomeDepthWeight);
            factory.biomeDepthOffset = JsonUtils.getFloat(jsonObject, "biomeDepthOffset", factory.biomeDepthOffset);
            factory.biomeScaleWeight = JsonUtils.getFloat(jsonObject, "biomeScaleWeight", factory.biomeScaleWeight);
            factory.biomeScaleOffset = JsonUtils.getFloat(jsonObject, "biomeScaleOffset", factory.biomeScaleOffset);
            factory.seaLevel = JsonUtils.getInt(jsonObject, "seaLevel", factory.seaLevel);
            factory.useCaves = JsonUtils.getBoolean(jsonObject, "useCaves", factory.useCaves);
            factory.useDungeons = JsonUtils.getBoolean(jsonObject, "useDungeons", factory.useDungeons);
            factory.dungeonChance = JsonUtils.getInt(jsonObject, "dungeonChance", factory.dungeonChance);
            factory.useStrongholds = JsonUtils.getBoolean(jsonObject, "useStrongholds", factory.useStrongholds);
            factory.useVillages = JsonUtils.getBoolean(jsonObject, "useVillages", factory.useVillages);
            factory.useMineShafts = JsonUtils.getBoolean(jsonObject, "useMineShafts", factory.useMineShafts);
            factory.useTemples = JsonUtils.getBoolean(jsonObject, "useTemples", factory.useTemples);
            factory.useMonuments = JsonUtils.getBoolean(jsonObject, "useMonuments", factory.useMonuments);
            factory.useRavines = JsonUtils.getBoolean(jsonObject, "useRavines", factory.useRavines);
            factory.useWaterLakes = JsonUtils.getBoolean(jsonObject, "useWaterLakes", factory.useWaterLakes);
            factory.waterLakeChance = JsonUtils.getInt(jsonObject, "waterLakeChance", factory.waterLakeChance);
            factory.useLavaLakes = JsonUtils.getBoolean(jsonObject, "useLavaLakes", factory.useLavaLakes);
            factory.lavaLakeChance = JsonUtils.getInt(jsonObject, "lavaLakeChance", factory.lavaLakeChance);
            factory.useLavaOceans = JsonUtils.getBoolean(jsonObject, "useLavaOceans", factory.useLavaOceans);
            factory.fixedBiome = JsonUtils.getInt(jsonObject, "fixedBiome", factory.fixedBiome);
            if (factory.fixedBiome >= 38 || factory.fixedBiome < -1) {
               factory.fixedBiome = -1;
            } else if (factory.fixedBiome >= BiomeGenBase.hell.biomeID) {
               factory.fixedBiome += 2;
            }

            factory.biomeSize = JsonUtils.getInt(jsonObject, "biomeSize", factory.biomeSize);
            factory.riverSize = JsonUtils.getInt(jsonObject, "riverSize", factory.riverSize);
            factory.dirtSize = JsonUtils.getInt(jsonObject, "dirtSize", factory.dirtSize);
            factory.dirtCount = JsonUtils.getInt(jsonObject, "dirtCount", factory.dirtCount);
            factory.dirtMinHeight = JsonUtils.getInt(jsonObject, "dirtMinHeight", factory.dirtMinHeight);
            factory.dirtMaxHeight = JsonUtils.getInt(jsonObject, "dirtMaxHeight", factory.dirtMaxHeight);
            factory.gravelSize = JsonUtils.getInt(jsonObject, "gravelSize", factory.gravelSize);
            factory.gravelCount = JsonUtils.getInt(jsonObject, "gravelCount", factory.gravelCount);
            factory.gravelMinHeight = JsonUtils.getInt(jsonObject, "gravelMinHeight", factory.gravelMinHeight);
            factory.gravelMaxHeight = JsonUtils.getInt(jsonObject, "gravelMaxHeight", factory.gravelMaxHeight);
            factory.graniteSize = JsonUtils.getInt(jsonObject, "graniteSize", factory.graniteSize);
            factory.graniteCount = JsonUtils.getInt(jsonObject, "graniteCount", factory.graniteCount);
            factory.graniteMinHeight = JsonUtils.getInt(jsonObject, "graniteMinHeight", factory.graniteMinHeight);
            factory.graniteMaxHeight = JsonUtils.getInt(jsonObject, "graniteMaxHeight", factory.graniteMaxHeight);
            factory.dioriteSize = JsonUtils.getInt(jsonObject, "dioriteSize", factory.dioriteSize);
            factory.dioriteCount = JsonUtils.getInt(jsonObject, "dioriteCount", factory.dioriteCount);
            factory.dioriteMinHeight = JsonUtils.getInt(jsonObject, "dioriteMinHeight", factory.dioriteMinHeight);
            factory.dioriteMaxHeight = JsonUtils.getInt(jsonObject, "dioriteMaxHeight", factory.dioriteMaxHeight);
            factory.andesiteSize = JsonUtils.getInt(jsonObject, "andesiteSize", factory.andesiteSize);
            factory.andesiteCount = JsonUtils.getInt(jsonObject, "andesiteCount", factory.andesiteCount);
            factory.andesiteMinHeight = JsonUtils.getInt(jsonObject, "andesiteMinHeight", factory.andesiteMinHeight);
            factory.andesiteMaxHeight = JsonUtils.getInt(jsonObject, "andesiteMaxHeight", factory.andesiteMaxHeight);
            factory.coalSize = JsonUtils.getInt(jsonObject, "coalSize", factory.coalSize);
            factory.coalCount = JsonUtils.getInt(jsonObject, "coalCount", factory.coalCount);
            factory.coalMinHeight = JsonUtils.getInt(jsonObject, "coalMinHeight", factory.coalMinHeight);
            factory.coalMaxHeight = JsonUtils.getInt(jsonObject, "coalMaxHeight", factory.coalMaxHeight);
            factory.ironSize = JsonUtils.getInt(jsonObject, "ironSize", factory.ironSize);
            factory.ironCount = JsonUtils.getInt(jsonObject, "ironCount", factory.ironCount);
            factory.ironMinHeight = JsonUtils.getInt(jsonObject, "ironMinHeight", factory.ironMinHeight);
            factory.ironMaxHeight = JsonUtils.getInt(jsonObject, "ironMaxHeight", factory.ironMaxHeight);
            factory.goldSize = JsonUtils.getInt(jsonObject, "goldSize", factory.goldSize);
            factory.goldCount = JsonUtils.getInt(jsonObject, "goldCount", factory.goldCount);
            factory.goldMinHeight = JsonUtils.getInt(jsonObject, "goldMinHeight", factory.goldMinHeight);
            factory.goldMaxHeight = JsonUtils.getInt(jsonObject, "goldMaxHeight", factory.goldMaxHeight);
            factory.redstoneSize = JsonUtils.getInt(jsonObject, "redstoneSize", factory.redstoneSize);
            factory.redstoneCount = JsonUtils.getInt(jsonObject, "redstoneCount", factory.redstoneCount);
            factory.redstoneMinHeight = JsonUtils.getInt(jsonObject, "redstoneMinHeight", factory.redstoneMinHeight);
            factory.redstoneMaxHeight = JsonUtils.getInt(jsonObject, "redstoneMaxHeight", factory.redstoneMaxHeight);
            factory.diamondSize = JsonUtils.getInt(jsonObject, "diamondSize", factory.diamondSize);
            factory.diamondCount = JsonUtils.getInt(jsonObject, "diamondCount", factory.diamondCount);
            factory.diamondMinHeight = JsonUtils.getInt(jsonObject, "diamondMinHeight", factory.diamondMinHeight);
            factory.diamondMaxHeight = JsonUtils.getInt(jsonObject, "diamondMaxHeight", factory.diamondMaxHeight);
            factory.lapisSize = JsonUtils.getInt(jsonObject, "lapisSize", factory.lapisSize);
            factory.lapisCount = JsonUtils.getInt(jsonObject, "lapisCount", factory.lapisCount);
            factory.lapisCenterHeight = JsonUtils.getInt(jsonObject, "lapisCenterHeight", factory.lapisCenterHeight);
            factory.lapisSpread = JsonUtils.getInt(jsonObject, "lapisSpread", factory.lapisSpread);
         } catch (Exception exception) {
         }

         return factory;
      }
   }
}
