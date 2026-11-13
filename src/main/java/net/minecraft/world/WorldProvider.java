package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderDebug;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.render.RenderUtils;

public abstract class WorldProvider {
   protected final float[] lightBrightnessTable = new float[16];
   private final float[] colorsSunriseSunset = new float[4];
   protected int dimensionId;
   protected World worldObj;
   protected WorldChunkManager worldChunkMgr;
   private String generatorSettings;
   protected boolean hasNoSky;
   public static final float[] moonPhaseFactors = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   private WorldType terrainType;
   protected boolean isHellWorld;

   public boolean doesXZShowFog(int x, int z) {
      return false;
   }

   public BlockPos getSpawnCoordinate() {
      return null;
   }

   public abstract String getDimensionName();

   public boolean canCoordinateBeSpawn(int x, int z) {
      return this.worldObj.getGroundAboveSeaLevel(new BlockPos(x, 0, z)) == Blocks.grass;
   }

   public WorldBorder getWorldBorder() {
      return new WorldBorder();
   }

   public WorldChunkManager getWorldChunkManager() {
      return this.worldChunkMgr;
   }

   public boolean getHasNoSky() {
      return this.hasNoSky;
   }

   protected void generateLightBrightnessTable() {
      float f = 0.0F;

      for (int index = 0; index <= 15; index++) {
         float f2 = 1.0F - index / 15.0F;
         this.lightBrightnessTable[index] = (1.0F - f2) / (f2 * 3.0F + 1.0F) * (1.0F - f) + f;
      }
   }

   public int getMoonPhase(long worldTime) {
      return (int)(worldTime / 24000L % 8L + 8L) % 8;
   }

   public boolean isSkyColored() {
      return true;
   }

   public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
      float f = 0.4F;
      float f2 = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) - 0.0F;
      float f3 = -0.0F;
      if (f2 >= f3 - f && f2 <= f3 + f) {
         float f4 = (f2 - f3) / f * 0.5F + 0.5F;
         float f5 = 1.0F - (1.0F - MathHelper.sin(f4 * (float) Math.PI)) * 0.99F;
         f5 *= f5;
         this.colorsSunriseSunset[0] = f4 * 0.3F + 0.7F;
         this.colorsSunriseSunset[1] = f4 * f4 * 0.7F + 0.2F;
         this.colorsSunriseSunset[2] = f4 * f4 * 0.0F + 0.2F;
         this.colorsSunriseSunset[3] = f5;
         return this.colorsSunriseSunset;
      } else {
         return null;
      }
   }

   public boolean canRespawnHere() {
      return true;
   }

   public int getDimensionId() {
      return this.dimensionId;
   }

   public boolean doesWaterVaporize() {
      return this.isHellWorld;
   }

   public final void registerWorld(World worldIn) {
      this.worldObj = worldIn;
      this.terrainType = worldIn.getWorldInfo().getTerrainType();
      this.generatorSettings = worldIn.getWorldInfo().getGeneratorOptions();
      this.registerWorldChunkManager();
      this.generateLightBrightnessTable();
   }

   public double getVoidFogYFactor() {
      return this.terrainType == WorldType.FLAT ? 1.0 : 0.03125;
   }

   protected void registerWorldChunkManager() {
      WorldType worldType = this.worldObj.getWorldInfo().getTerrainType();
      if (worldType == WorldType.FLAT) {
         FlatGeneratorInfo flatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(this.worldObj.getWorldInfo().getGeneratorOptions());
         this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.getBiomeFromBiomeList(flatGeneratorInfo.getBiome(), BiomeGenBase.DEFAULT_BIOME), 0.5F);
      } else if (worldType == WorldType.DEBUG_WORLD) {
         this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.0F);
      } else {
         this.worldChunkMgr = new WorldChunkManager(this.worldObj);
      }
   }

   public static WorldProvider getProviderForDimension(int dimension) {
      return dimension == -1 ? new WorldProviderHell() : (dimension == 0 ? new WorldProviderSurface() : (dimension == 1 ? new WorldProviderEnd() : null));
   }

   public abstract String getInternalNameSuffix();

   public int getAverageGroundLevel() {
      return this.terrainType == WorldType.FLAT ? 4 : this.worldObj.getSeaLevel() + 1;
   }

   public float[] getLightBrightnessTable() {
      return this.lightBrightnessTable;
   }

   public IChunkProvider createChunkGenerator() {
      return this.terrainType == WorldType.FLAT
         ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings)
         : (
            this.terrainType == WorldType.DEBUG_WORLD
               ? new ChunkProviderDebug(this.worldObj)
               : (
                  this.terrainType == WorldType.CUSTOMIZED
                     ? new ChunkProviderGenerate(
                        this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings
                     )
                     : new ChunkProviderGenerate(
                        this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings
                     )
               )
         );
   }

   public float calculateCelestialAngle(long worldTime, float partialTicks) {
      float f = ((int)(worldTime % 24000L) + partialTicks) / 24000.0F - 0.25F;
      if (f < 0.0F) {
         f++;
      }

      if (f > 1.0F) {
         f--;
      }

      f = 1.0F - (float)((Math.cos(f * Math.PI) + 1.0) / 2.0);
      return f + (f - f) / 3.0F;
   }

   public boolean isSurfaceWorld() {
      return true;
   }

   public float getCloudHeight() {
      return 128.0F;
   }

   public Vec3 getFogColor(float f, float f2) {
      float f3 = MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      f3 = MathHelper.clamp_float(f3, 0.0F, 1.0F);
      float f4 = 0.7529412F;
      float f5 = 0.84705883F;
      float f6 = 1.0F;
      f4 *= f3 * 0.94F + 0.06F;
      f5 *= f3 * 0.94F + 0.06F;
      f6 *= f3 * 0.91F + 0.09F;
      if (ModuleManager.worldColor.isEnabled() && ModuleManager.worldColor.worldColor.isSelected(0)) {
         float[] fArray = RenderUtils.unpackColorNormalized(ModuleManager.worldColor.fogColor.getColor());
         if (ModuleManager.worldColor.colorType.getCurrentElement() == 1) {
            fArray = RenderUtils.unpackColorNormalized(RenderUtils.indexedHsbColorMirrored(1, 1, 1.0F, 1.0F, 3000L));
         }

         f4 = fArray[0];
         f5 = fArray[1];
         f6 = fArray[2];
      }

      return new Vec3(f4, f5, f6);
   }
}
