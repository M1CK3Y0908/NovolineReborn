package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {
   public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData anvilConverterData, NBTTagCompound compound, WorldChunkManager worldChunkManager) {
      compound.setInteger("xPos", anvilConverterData.x);
      compound.setInteger("zPos", anvilConverterData.z);
      compound.setLong("LastUpdate", anvilConverterData.lastUpdated);
      int[] intArray = new int[anvilConverterData.heightmap.length];

      for (int index = 0; index < anvilConverterData.heightmap.length; index++) {
         intArray[index] = anvilConverterData.heightmap[index];
      }

      compound.setIntArray("HeightMap", intArray);
      compound.setBoolean("TerrainPopulated", anvilConverterData.terrainPopulated);
      NBTTagList nbtTagList = new NBTTagList();

      for (int innerIndex = 0; innerIndex < 8; innerIndex++) {
         boolean conditionMet = true;

         for (int nestedIndex = 0; nestedIndex < 16 && conditionMet; nestedIndex++) {
            for (int outerIndex = 0; outerIndex < 16 && conditionMet; outerIndex++) {
               for (int index2 = 0; index2 < 16; index2++) {
                  int i = nestedIndex << 11 | index2 << 7 | outerIndex + (innerIndex << 4);
                  int i2 = anvilConverterData.blocks[i];
                  if (i2 != 0) {
                     conditionMet = false;
                     break;
                  }
               }
            }
         }

         if (!conditionMet) {
            byte[] byteArray1 = new byte[4096];
            NibbleArray nibbleArray = new NibbleArray();
            NibbleArray nibbleArray2 = new NibbleArray();
            NibbleArray nibbleArray3 = new NibbleArray();

            for (int index3 = 0; index3 < 16; index3++) {
               for (int outerIndex2 = 0; outerIndex2 < 16; outerIndex2++) {
                  for (int index4 = 0; index4 < 16; index4++) {
                     int i3 = index3 << 11 | index4 << 7 | outerIndex2 + (innerIndex << 4);
                     int i4 = anvilConverterData.blocks[i3];
                     byteArray1[outerIndex2 << 8 | index4 << 4 | index3] = (byte)(i4 & 0xFF);
                     nibbleArray.set(index3, outerIndex2, index4, anvilConverterData.data.get(index3, outerIndex2 + (innerIndex << 4), index4));
                     nibbleArray2.set(index3, outerIndex2, index4, anvilConverterData.skyLight.get(index3, outerIndex2 + (innerIndex << 4), index4));
                     nibbleArray3.set(index3, outerIndex2, index4, anvilConverterData.blockLight.get(index3, outerIndex2 + (innerIndex << 4), index4));
                  }
               }
            }

            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Y", (byte)(innerIndex & 0xFF));
            nbtTagCompound.setByteArray("Blocks", byteArray1);
            nbtTagCompound.setByteArray("Data", nibbleArray.getData());
            nbtTagCompound.setByteArray("SkyLight", nibbleArray2.getData());
            nbtTagCompound.setByteArray("BlockLight", nibbleArray3.getData());
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Sections", nbtTagList);
      byte[] byteArray = new byte[256];
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int index5 = 0; index5 < 16; index5++) {
         for (int index6 = 0; index6 < 16; index6++) {
            mutableBlockPos.set(anvilConverterData.x << 4 | index5, 0, anvilConverterData.z << 4 | index6);
            byteArray[index6 << 4 | index5] = (byte)(worldChunkManager.getBiomeGenerator(mutableBlockPos, BiomeGenBase.DEFAULT_BIOME).biomeID & 0xFF);
         }
      }

      compound.setByteArray("Biomes", byteArray);
      compound.setTag("Entities", anvilConverterData.entities);
      compound.setTag("TileEntities", anvilConverterData.tileEntities);
      if (anvilConverterData.tileTicks != null) {
         compound.setTag("TileTicks", anvilConverterData.tileTicks);
      }
   }

   public static ChunkLoader.AnvilConverterData load(NBTTagCompound nbt) {
      int integer = nbt.getInteger("xPos");
      int integer2 = nbt.getInteger("zPos");
      ChunkLoader.AnvilConverterData anvilConverterData = new ChunkLoader.AnvilConverterData(integer, integer2);
      anvilConverterData.blocks = nbt.getByteArray("Blocks");
      anvilConverterData.data = new NibbleArrayReader(nbt.getByteArray("Data"));
      anvilConverterData.skyLight = new NibbleArrayReader(nbt.getByteArray("SkyLight"));
      anvilConverterData.blockLight = new NibbleArrayReader(nbt.getByteArray("BlockLight"));
      anvilConverterData.heightmap = nbt.getByteArray("HeightMap");
      anvilConverterData.terrainPopulated = nbt.getBoolean("TerrainPopulated");
      anvilConverterData.entities = nbt.getTagList("Entities", 10);
      anvilConverterData.tileEntities = nbt.getTagList("TileEntities", 10);
      anvilConverterData.tileTicks = nbt.getTagList("TileTicks", 10);

      try {
         anvilConverterData.lastUpdated = nbt.getLong("LastUpdate");
      } catch (ClassCastException classCastException) {
         anvilConverterData.lastUpdated = nbt.getInteger("LastUpdate");
      }

      return anvilConverterData;
   }

   public static class AnvilConverterData {
      public NBTTagList tileEntities;
      public final int x;
      public NBTTagList entities;
      public final int z;
      public long lastUpdated;
      public boolean terrainPopulated;
      public NibbleArrayReader data;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader blockLight;
      public byte[] heightmap;
      public NBTTagList tileTicks;
      public byte[] blocks;

      public AnvilConverterData(int xIn, int zIn) {
         this.x = xIn;
         this.z = zIn;
      }
   }
}
