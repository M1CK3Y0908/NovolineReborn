package net.minecraft.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;

public class GenLayerBiome extends GenLayer {
   private BiomeGenBase[] warmBiomes = new BiomeGenBase[]{
      BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.savanna, BiomeGenBase.savanna, BiomeGenBase.plains
   };
   private BiomeGenBase[] mediumBiomes = new BiomeGenBase[]{
      BiomeGenBase.forest, BiomeGenBase.roofedForest, BiomeGenBase.extremeHills, BiomeGenBase.plains, BiomeGenBase.birchForest, BiomeGenBase.swampland
   };
   private BiomeGenBase[] coldBiomes = new BiomeGenBase[]{BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.taiga, BiomeGenBase.plains};
   private BiomeGenBase[] iceBiomes = new BiomeGenBase[]{BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.coldTaiga};
   private final ChunkProviderSettings settings;

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int i2 = 0; i2 < areaWidth; i2++) {
            this.initChunkSeed(i2 + areaX, i + areaY);
            int i3 = ints[i2 + i * areaWidth];
            int i4 = (i3 & 3840) >> 8;
            i3 &= -3841;
            if (this.settings != null && this.settings.fixedBiome >= 0) {
               intCache[i2 + i * areaWidth] = this.settings.fixedBiome;
            } else if (isBiomeOceanic(i3)) {
               intCache[i2 + i * areaWidth] = i3;
            } else if (i3 == BiomeGenBase.mushroomIsland.biomeID) {
               intCache[i2 + i * areaWidth] = i3;
            } else if (i3 == 1) {
               if (i4 > 0) {
                  if (this.nextInt(3) == 0) {
                     intCache[i2 + i * areaWidth] = BiomeGenBase.mesaPlateau.biomeID;
                  } else {
                     intCache[i2 + i * areaWidth] = BiomeGenBase.mesaPlateau_F.biomeID;
                  }
               } else {
                  intCache[i2 + i * areaWidth] = this.warmBiomes[this.nextInt(this.warmBiomes.length)].biomeID;
               }
            } else if (i3 == 2) {
               if (i4 > 0) {
                  intCache[i2 + i * areaWidth] = BiomeGenBase.jungle.biomeID;
               } else {
                  intCache[i2 + i * areaWidth] = this.mediumBiomes[this.nextInt(this.mediumBiomes.length)].biomeID;
               }
            } else if (i3 == 3) {
               if (i4 > 0) {
                  intCache[i2 + i * areaWidth] = BiomeGenBase.megaTaiga.biomeID;
               } else {
                  intCache[i2 + i * areaWidth] = this.coldBiomes[this.nextInt(this.coldBiomes.length)].biomeID;
               }
            } else if (i3 == 4) {
               intCache[i2 + i * areaWidth] = this.iceBiomes[this.nextInt(this.iceBiomes.length)].biomeID;
            } else {
               intCache[i2 + i * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
            }
         }
      }

      return intCache;
   }

   public GenLayerBiome(long seed, GenLayer parent, WorldType worldType, String options) {
      super(seed);
      this.parent = parent;
      if (worldType == WorldType.DEFAULT_1_1) {
         this.warmBiomes = new BiomeGenBase[]{
            BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga
         };
         this.settings = null;
      } else if (worldType == WorldType.CUSTOMIZED) {
         this.settings = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(options).build();
      } else {
         this.settings = null;
      }
   }
}
