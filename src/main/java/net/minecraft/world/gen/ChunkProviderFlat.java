package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderFlat implements IChunkProvider {
   private final IBlockState[] cachedBlockIDs = new IBlockState[256];
   private final List<MapGenStructure> structureGenerators = Lists.newArrayList();
   private World worldObj;
   private Random random;
   private final FlatGeneratorInfo flatWorldGenInfo;
   private WorldGenLakes waterLakeGenerator;
   private WorldGenLakes lavaLakeGenerator;
   private final boolean hasDungeons;
   private final boolean hasDecoration;

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      if ("Stronghold".equals(structureName)) {
         for (MapGenStructure mapGenStructure : this.structureGenerators) {
            if (mapGenStructure instanceof MapGenStronghold) {
               return mapGenStructure.getClosestStrongholdPos(worldIn, position);
            }
         }
      }

      return null;
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public void saveExtraData() {
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      Iterator iterator2 = this.structureGenerators.iterator();

      while (iterator2.hasNext()) {
         ((MapGenStructure)iterator2.next()).generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      int i = x * 16;
      int i2 = z * 16;
      BlockPos blockPos = new BlockPos(i, 0, i2);
      BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(new BlockPos(i + 16, 0, i2 + 16));
      boolean flag = false;
      this.random.setSeed(this.worldObj.getSeed());
      long l = this.random.nextLong() / 2L * 2L + 1L;
      long l2 = this.random.nextLong() / 2L * 2L + 1L;
      this.random.setSeed(x * l + z * l2 ^ this.worldObj.getSeed());
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);

      for (MapGenStructure mapGenStructure : this.structureGenerators) {
         boolean flag2 = mapGenStructure.generateStructure(this.worldObj, this.random, chunkCoordIntPair);
         if (mapGenStructure instanceof MapGenVillage) {
            flag |= flag2;
         }
      }

      if (this.waterLakeGenerator != null && !flag && this.random.nextInt(4) == 0) {
         this.waterLakeGenerator
            .generate(this.worldObj, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
      }

      if (this.lavaLakeGenerator != null && !flag && this.random.nextInt(8) == 0) {
         BlockPos blockPos2 = blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);
         if (blockPos2.getY() < this.worldObj.getSeaLevel() || this.random.nextInt(10) == 0) {
            this.lavaLakeGenerator.generate(this.worldObj, this.random, blockPos2);
         }
      }

      if (this.hasDungeons) {
         for (int i3 = 0; i3 < 8; i3++) {
            new WorldGenDungeons()
               .generate(this.worldObj, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
         }
      }

      if (this.hasDecoration) {
         biomeGenForCoords.decorate(this.worldObj, this.random, blockPos);
      }
   }

   @Override
   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.worldObj.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      ChunkPrimer chunkPrimer = new ChunkPrimer();

      for (int i = 0; i < this.cachedBlockIDs.length; i++) {
         IBlockState iBlockState = this.cachedBlockIDs[i];
         if (iBlockState != null) {
            for (int i2 = 0; i2 < 16; i2++) {
               for (int i3 = 0; i3 < 16; i3++) {
                  chunkPrimer.setBlockState(i2, i, i3, iBlockState);
               }
            }
         }
      }

      for (MapGenBase mapGenBase : this.structureGenerators) {
         mapGenBase.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk2 = new Chunk(this.worldObj, chunkPrimer, x, z);
      BiomeGenBase[] array = this.worldObj.getWorldChunkManager().getArray3((BiomeGenBase[])null, x * 16, z * 16);
      byte[] biomeArray = chunk2.getBiomeArray();

      for (int i4 = 0; i4 < biomeArray.length; i4++) {
         biomeArray[i4] = (byte)array[i4].biomeID;
      }

      chunk2.generateSkylightMap();
      return chunk2;
   }

   @Override
   public boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate) {
      return true;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   @Override
   public String makeString() {
      return "FlatLevelSource";
   }

   public ChunkProviderFlat(World worldIn, long seed, boolean generateStructures, String flatGeneratorSettings) {
      this.worldObj = worldIn;
      this.random = new Random(seed);
      this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(flatGeneratorSettings);
      if (generateStructures) {
         Map worldFeatures = this.flatWorldGenInfo.getWorldFeatures();
         if (worldFeatures.containsKey("village")) {
            Map map = (Map)worldFeatures.get("village");
            if (!map.containsKey("size")) {
               map.put("size", "1");
            }

            this.structureGenerators.add(new MapGenVillage(map));
         }

         if (worldFeatures.containsKey("biome_1")) {
            this.structureGenerators.add(new MapGenScatteredFeature((Map<String, String>)worldFeatures.get("biome_1")));
         }

         if (worldFeatures.containsKey("mineshaft")) {
            this.structureGenerators.add(new MapGenMineshaft((Map<String, String>)worldFeatures.get("mineshaft")));
         }

         if (worldFeatures.containsKey("stronghold")) {
            this.structureGenerators.add(new MapGenStronghold((Map<String, String>)worldFeatures.get("stronghold")));
         }

         if (worldFeatures.containsKey("oceanmonument")) {
            this.structureGenerators.add(new StructureOceanMonument((Map<String, String>)worldFeatures.get("oceanmonument")));
         }
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
         this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
         this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
      }

      this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
      int i = 0;
      int i2 = 0;
      boolean flag = true;

      for (FlatLayerInfo flatLayerInfo : this.flatWorldGenInfo.getFlatLayers()) {
         for (int minY = flatLayerInfo.getMinY(); minY < flatLayerInfo.getMinY() + flatLayerInfo.getLayerCount(); minY++) {
            IBlockState layerMaterial = flatLayerInfo.getLayerMaterial();
            if (layerMaterial.getBlock() != Blocks.air) {
               flag = false;
               this.cachedBlockIDs[minY] = layerMaterial;
            }
         }

         if (flatLayerInfo.getLayerMaterial().getBlock() == Blocks.air) {
            i2 += flatLayerInfo.getLayerCount();
         } else {
            i += flatLayerInfo.getLayerCount() + i2;
            i2 = 0;
         }
      }

      worldIn.setSeaLevel(i);
      this.hasDecoration = flag ? false : this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
   }
}
