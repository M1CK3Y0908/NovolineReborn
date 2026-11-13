package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache {
   private LongHashMap<BiomeCache.Block> cacheMap = new LongHashMap<>();
   private long lastCleanupTime;
   private List<BiomeCache.Block> cache = Lists.newArrayList();
   private final WorldChunkManager chunkManager;

   public BiomeGenBase[] getCachedBiomes(int x, int z) {
      return this.getBiomeCacheBlock(x, z).biomes;
   }

   public void cleanupCache() {
      long currentTimeMillis = MinecraftServer.getCurrentTimeMillis();
      long l = currentTimeMillis - this.lastCleanupTime;
      if (l > 7500L || l < 0L) {
         this.lastCleanupTime = currentTimeMillis;

         for (int nestedIndex = 0; nestedIndex < this.cache.size(); nestedIndex++) {
            BiomeCache.Block biomecache$block = this.cache.get(nestedIndex);
            long l2 = currentTimeMillis - biomecache$block.lastAccessTime;
            if (l2 > 30000L || l2 < 0L) {
               this.cache.remove(nestedIndex--);
               long l3 = biomecache$block.xPosition & 4294967295L | (biomecache$block.zPosition & 4294967295L) << 32;
               this.cacheMap.remove(l3);
            }
         }
      }
   }

   public BiomeCache.Block getBiomeCacheBlock(int x, int z) {
      x >>= 4;
      z >>= 4;
      long l = x & 4294967295L | (z & 4294967295L) << 32;
      BiomeCache.Block biomecache$block = this.cacheMap.getValueByKey(l);
      if (biomecache$block == null) {
         biomecache$block = new BiomeCache.Block(x, z);
         this.cacheMap.add(l, biomecache$block);
         this.cache.add(biomecache$block);
      }

      biomecache$block.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
      return biomecache$block;
   }

   public BiomeCache(WorldChunkManager chunkManagerIn) {
      this.chunkManager = chunkManagerIn;
   }

   public BiomeGenBase getBiomeAt(int x, int z, BiomeGenBase fallback) {
      BiomeGenBase biomeGenBase = this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
      return biomeGenBase == null ? fallback : biomeGenBase;
   }

   public class Block {
      public long lastAccessTime;
      public float[] rainfallValues = new float[256];
      public BiomeGenBase[] biomes = new BiomeGenBase[256];
      public int xPosition;
      public int zPosition;

      public BiomeGenBase getBiomeGenAt(int x, int z) {
         return this.biomes[x & 15 | (z & 15) << 4];
      }

      public Block(int x, int z) {
         this.xPosition = x;
         this.zPosition = z;
         BiomeCache.this.chunkManager.getArray2(this.rainfallValues, x << 4, z << 4);
         BiomeCache.this.chunkManager.getArray(this.biomes, x << 4, z << 4, false);
      }
   }
}
