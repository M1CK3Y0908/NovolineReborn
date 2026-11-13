package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {
   private Map<ChunkCoordIntPair, NBTTagCompound> chunksToRemove = new ConcurrentHashMap<>();
   private Set<ChunkCoordIntPair> pendingAnvilChunksCoordinates = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private boolean savingExtraData = false;
   private static final Logger logger = LogManager.getLogger();
   private final File chunkSaveLocation;

   @Override
   public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);
      NBTTagCompound nBTTagCompound = this.chunksToRemove.get(chunkCoordIntPair);
      if (nBTTagCompound == null) {
         DataInputStream dataInputStream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, x, z);
         if (dataInputStream == null) {
            return null;
         }

         nBTTagCompound = CompressedStreamTools.read(dataInputStream);
      }

      return this.checkedReadChunkFromNBT(worldIn, x, z, nBTTagCompound);
   }

   private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound chunkData) {
      chunkData.setByte("V", (byte)1);
      chunkData.setInteger("xPos", chunkIn.xPosition);
      chunkData.setInteger("zPos", chunkIn.zPosition);
      chunkData.setLong("LastUpdate", worldIn.getTotalWorldTime());
      chunkData.setIntArray("HeightMap", chunkIn.getHeightMap());
      chunkData.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
      chunkData.setBoolean("LightPopulated", chunkIn.isLightPopulated());
      chunkData.setLong("InhabitedTime", chunkIn.getInhabitedTime());
      ExtendedBlockStorage[] blockStorageArray = chunkIn.getBlockStorageArray();
      NBTTagList nbtTagList = new NBTTagList();
      boolean flag = !worldIn.provider.getHasNoSky();

      for (ExtendedBlockStorage extendedBlockStorage : blockStorageArray) {
         if (extendedBlockStorage != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Y", (byte)(extendedBlockStorage.getYLocation() >> 4 & 0xFF));
            byte[] bArray = new byte[extendedBlockStorage.getData().length];
            NibbleArray nibbleArray = new NibbleArray();
            NibbleArray nibbleArray2 = null;

            for (int i2 = 0; i2 < extendedBlockStorage.getData().length; i2++) {
               char c = extendedBlockStorage.getData()[i2];
               int i3 = i2 & 15;
               int i4 = i2 >> 8 & 15;
               int i5 = i2 >> 4 & 15;
               if (c >> '\f' != 0) {
                  if (nibbleArray2 == null) {
                     nibbleArray2 = new NibbleArray();
                  }

                  nibbleArray2.set(i3, i4, i5, c >> '\f');
               }

               bArray[i2] = (byte)(c >> 4 & 0xFF);
               nibbleArray.set(i3, i4, i5, c & 15);
            }

            nbtTagCompound.setByteArray("Blocks", bArray);
            nbtTagCompound.setByteArray("Data", nibbleArray.getData());
            if (nibbleArray2 != null) {
               nbtTagCompound.setByteArray("Add", nibbleArray2.getData());
            }

            nbtTagCompound.setByteArray("BlockLight", extendedBlockStorage.getBlocklightArray().getData());
            if (flag) {
               nbtTagCompound.setByteArray("SkyLight", extendedBlockStorage.getSkylightArray().getData());
            } else {
               nbtTagCompound.setByteArray("SkyLight", new byte[extendedBlockStorage.getBlocklightArray().getData().length]);
            }

            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      chunkData.setTag("Sections", nbtTagList);
      chunkData.setByteArray("Biomes", chunkIn.getBiomeArray());
      chunkIn.setHasEntities(false);
      NBTTagList nbtTagList2 = new NBTTagList();

      for (int i6 = 0; i6 < chunkIn.getEntityLists().length; i6++) {
         for (Entity entity2 : chunkIn.getEntityLists()[i6]) {
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            if (entity2.writeToNBTOptional(nbtTagCompound2)) {
               chunkIn.setHasEntities(true);
               nbtTagList2.appendTag(nbtTagCompound2);
            }
         }
      }

      chunkData.setTag("Entities", nbtTagList2);
      NBTTagList nbtTagList3 = new NBTTagList();

      for (TileEntity tileEntity : chunkIn.getTileEntityMap().values()) {
         NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
         tileEntity.writeToNBT(nbtTagCompound3);
         nbtTagList3.appendTag(nbtTagCompound3);
      }

      chunkData.setTag("TileEntities", nbtTagList3);
      List list = worldIn.getList3(chunkIn);
      if (list != null) {
         long totalWorldTime = worldIn.getTotalWorldTime();
         NBTTagList nbtTagList4 = new NBTTagList();

         for (NextTickListEntry nextTickListEntry : (Iterable<NextTickListEntry>) list) {
            NBTTagCompound nbtTagCompound4 = new NBTTagCompound();
            ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(nextTickListEntry.getBlock());
            nbtTagCompound4.setString("i", nameForObject == null ? "" : nameForObject.toString());
            nbtTagCompound4.setInteger("x", nextTickListEntry.position.getX());
            nbtTagCompound4.setInteger("y", nextTickListEntry.position.getY());
            nbtTagCompound4.setInteger("z", nextTickListEntry.position.getZ());
            nbtTagCompound4.setInteger("t", (int)(nextTickListEntry.scheduledTime - totalWorldTime));
            nbtTagCompound4.setInteger("p", nextTickListEntry.priority);
            nbtTagList4.appendTag(nbtTagCompound4);
         }

         chunkData.setTag("TileTicks", nbtTagList4);
      }
   }

   public AnvilChunkLoader(File chunkSaveLocationIn) {
      this.chunkSaveLocation = chunkSaveLocationIn;
   }

   protected Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound chunkData) {
      if (!chunkData.hasKey("Level", 10)) {
         logger.error("Chunk file at " + x + "," + z + " is missing level data, skipping");
         return null;
      }

      NBTTagCompound nBTTagCompound = chunkData.getCompoundTag("Level");
      if (!nBTTagCompound.hasKey("Sections", 9)) {
         logger.error("Chunk file at " + x + "," + z + " is missing block data, skipping");
         return null;
      }

      Chunk chunk = this.readChunkFromNBT(worldIn, nBTTagCompound);
      if (!chunk.isAtLocation(x, z)) {
         logger.error(
            "Chunk file at "
               + x
               + ","
               + z
               + " is in the wrong location; relocating. (Expected "
               + x
               + ", "
               + z
               + ", got "
               + chunk.xPosition
               + ", "
               + chunk.zPosition
               + ")"
         );
         nBTTagCompound.setInteger("xPos", x);
         nBTTagCompound.setInteger("zPos", z);
         chunk = this.readChunkFromNBT(worldIn, nBTTagCompound);
      }

      return chunk;
   }

   protected void addChunkToPending(ChunkCoordIntPair chunkCoords, NBTTagCompound chunkData) {
      if (!this.pendingAnvilChunksCoordinates.contains(chunkCoords)) {
         this.chunksToRemove.put(chunkCoords, chunkData);
      }

      ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
   }

   @Override
   public void chunkTick() {
   }

   @Override
   public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
   }

   private void writeChunkData(ChunkCoordIntPair chunkCoords, NBTTagCompound chunkData) throws IOException {
      DataOutputStream dataOutputStream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, chunkCoords.chunkXPos, chunkCoords.chunkZPos);
      CompressedStreamTools.write(chunkData, dataOutputStream);
      dataOutputStream.close();
   }

   @Override
   public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {
      worldIn.checkSessionLock();

      try {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         nbtTagCompound.setTag("Level", nbtTagCompound2);
         this.writeChunkToNBT(chunkIn, worldIn, nbtTagCompound2);
         this.addChunkToPending(chunkIn.getChunkCoordIntPair(), nbtTagCompound);
      } catch (Exception exception) {
         logger.error("Failed to save chunk", exception);
      }
   }

   @Override
   public void saveExtraData() {
      try {
         this.savingExtraData = true;

         while (true) {
            if (this.writeNextIO()) {
            }
         }
      } finally {
         this.savingExtraData = false;
      }
   }

   private Chunk readChunkFromNBT(World worldIn, NBTTagCompound chunkData) {
      int integer = chunkData.getInteger("xPos");
      int integer2 = chunkData.getInteger("zPos");
      Chunk chunk2 = new Chunk(worldIn, integer, integer2);
      chunk2.setHeightMap(chunkData.getIntArray("HeightMap"));
      chunk2.setTerrainPopulated(chunkData.getBoolean("TerrainPopulated"));
      chunk2.setLightPopulated(chunkData.getBoolean("LightPopulated"));
      chunk2.setInhabitedTime(chunkData.getLong("InhabitedTime"));
      NBTTagList tagList = chunkData.getTagList("Sections", 10);
      ExtendedBlockStorage[] extendedBlockStorages = new ExtendedBlockStorage[16];
      boolean flag = !worldIn.provider.getHasNoSky();

      for (int i2 = 0; i2 < tagList.tagCount(); i2++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i2);
         byte byteValue = compoundTagAt.getByte("Y");
         ExtendedBlockStorage extendedBlockStorage = new ExtendedBlockStorage(byteValue << 4, flag);
         byte[] byteArray = compoundTagAt.getByteArray("Blocks");
         NibbleArray nibbleArray = new NibbleArray(compoundTagAt.getByteArray("Data"));
         NibbleArray nibbleArray2 = compoundTagAt.hasKey("Add", 7) ? new NibbleArray(compoundTagAt.getByteArray("Add")) : null;
         char[] cArray = new char[byteArray.length];

         for (int i3 = 0; i3 < cArray.length; i3++) {
            int i4 = i3 & 15;
            int i5 = i3 >> 8 & 15;
            int i6 = i3 >> 4 & 15;
            int i7 = nibbleArray2 != null ? nibbleArray2.get(i4, i5, i6) : 0;
            cArray[i3] = (char)(i7 << 12 | (byteArray[i3] & 255) << 4 | nibbleArray.get(i4, i5, i6));
         }

         extendedBlockStorage.setData(cArray);
         extendedBlockStorage.setBlocklightArray(new NibbleArray(compoundTagAt.getByteArray("BlockLight")));
         if (flag) {
            extendedBlockStorage.setSkylightArray(new NibbleArray(compoundTagAt.getByteArray("SkyLight")));
         }

         extendedBlockStorage.removeInvalidBlocks();
         extendedBlockStorages[byteValue] = extendedBlockStorage;
      }

      chunk2.setStorageArrays(extendedBlockStorages);
      if (chunkData.hasKey("Biomes", 7)) {
         chunk2.setBiomeArray(chunkData.getByteArray("Biomes"));
      }

      NBTTagList tagList2 = chunkData.getTagList("Entities", 10);
      if (tagList2 != null) {
         for (int i8 = 0; i8 < tagList2.tagCount(); i8++) {
            NBTTagCompound compoundTagAt2 = tagList2.getCompoundTagAt(i8);
            Entity entityFromNBT = EntityList.createEntityFromNBT(compoundTagAt2, worldIn);
            chunk2.setHasEntities(true);
            if (entityFromNBT != null) {
               chunk2.addEntity(entityFromNBT);
               Entity entity2 = entityFromNBT;

               for (NBTTagCompound nbtTagCompound = compoundTagAt2; nbtTagCompound.hasKey("Riding", 10); nbtTagCompound = nbtTagCompound.getCompoundTag("Riding")) {
                  Entity entityFromNBT2 = EntityList.createEntityFromNBT(nbtTagCompound.getCompoundTag("Riding"), worldIn);
                  if (entityFromNBT2 != null) {
                     chunk2.addEntity(entityFromNBT2);
                     entity2.mountEntity(entityFromNBT2);
                  }

                  entity2 = entityFromNBT2;
               }
            }
         }
      }

      NBTTagList tagList3 = chunkData.getTagList("TileEntities", 10);
      if (tagList3 != null) {
         for (int i9 = 0; i9 < tagList3.tagCount(); i9++) {
            NBTTagCompound compoundTagAt3 = tagList3.getCompoundTagAt(i9);
            TileEntity andLoadEntity = TileEntity.createAndLoadEntity(compoundTagAt3);
            if (andLoadEntity != null) {
               chunk2.addTileEntity(andLoadEntity);
            }
         }
      }

      if (chunkData.hasKey("TileTicks", 9)) {
         NBTTagList tagList4 = chunkData.getTagList("TileTicks", 10);
         if (tagList4 != null) {
            for (int i10 = 0; i10 < tagList4.tagCount(); i10++) {
               NBTTagCompound compoundTagAt4 = tagList4.getCompoundTagAt(i10);
               Block block2;
               if (compoundTagAt4.hasKey("i", 8)) {
                  block2 = Block.getBlockFromName(compoundTagAt4.getString("i"));
               } else {
                  block2 = Block.getBlockById(compoundTagAt4.getInteger("i"));
               }

               worldIn.scheduleBlockUpdate(
                  new BlockPos(compoundTagAt4.getInteger("x"), compoundTagAt4.getInteger("y"), compoundTagAt4.getInteger("z")), block2, compoundTagAt4.getInteger("t"), compoundTagAt4.getInteger("p")
               );
            }
         }
      }

      return chunk2;
   }

   @Override
   public boolean writeNextIO() {
      if (this.chunksToRemove.isEmpty()) {
         if (this.savingExtraData) {
            logger.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.chunkSaveLocation.getName()});
         }

         return false;
      } else {
         ChunkCoordIntPair chunkCoordIntPair = this.chunksToRemove.keySet().iterator().next();

         try {
            this.pendingAnvilChunksCoordinates.add(chunkCoordIntPair);
            NBTTagCompound nbtTagCompound = this.chunksToRemove.remove(chunkCoordIntPair);
            if (nbtTagCompound != null) {
               try {
                  this.writeChunkData(chunkCoordIntPair, nbtTagCompound);
               } catch (Exception exception) {
                  logger.error("Failed to save chunk", exception);
               }
            }
         } finally {
            this.pendingAnvilChunksCoordinates.remove(chunkCoordIntPair);
         }

         return true;
      }
   }
}
