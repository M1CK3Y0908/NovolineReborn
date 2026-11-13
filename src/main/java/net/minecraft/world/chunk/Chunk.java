package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderDebug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
   private boolean isModified;
   private boolean hasEntities;
   private boolean isChunkLoaded;
   private final ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
   private final byte[] blockBiomeArray = new byte[256];
   private int heightMapMinimum;
   private final int[] precipitationHeightMap = new int[256];
   private final boolean[] updateSkylightColumns = new boolean[256];
   private boolean isLightPopulated;
   private long lastSaveTime;
   private final Map<BlockPos, TileEntity> chunkTileEntityMap = Maps.newHashMap();
   private int queuedLightChecks = 4096;
   private ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue = Queues.newConcurrentLinkedQueue();
   private long inhabitedTime;
   private final ClassInheritanceMultiMap<Entity>[] entityLists = new ClassInheritanceMultiMap[16];
   private boolean hasTicked;
   private final World worldObj;
   public final int xPosition;
   private static final Logger logger = LogManager.getLogger();
   private boolean isTerrainPopulated;
   private boolean isGapLightingUpdated;
   public final int zPosition;
   private final int[] heightMap;

   public ChunkCoordIntPair getChunkCoordIntPair() {
      return new ChunkCoordIntPair(this.xPosition, this.zPosition);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public boolean canSeeSky(BlockPos pos) {
      int xCoordinate = pos.getX() & 15;
      int yCoordinate = pos.getY();
      int zCoordinate = pos.getZ() & 15;
      return yCoordinate >= this.heightMap[zCoordinate << 4 | xCoordinate];
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      int i = pos.getX() & 15;
      int y2 = pos.getY();
      int i2 = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y2 >> 4];
      if (extendedBlockStorage != null) {
         int i3 = this.worldObj.provider.getHasNoSky() ? 0 : extendedBlockStorage.getExtSkylightValue(i, y2 & 15, i2);
         i3 -= amount;
         int extBlocklightValue = extendedBlockStorage.getExtBlocklightValue(i, y2 & 15, i2);
         if (extBlocklightValue > i3) {
            i3 = extBlocklightValue;
         }

         return i3;
      } else {
         return !this.worldObj.provider.getHasNoSky() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
      }
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      tileEntityIn.setWorldObj(this.worldObj);
      tileEntityIn.setPos(pos);
      if (this.getBlock(pos) instanceof ITileEntityProvider) {
         if (this.chunkTileEntityMap.containsKey(pos)) {
            this.chunkTileEntityMap.get(pos).invalidate();
         }

         tileEntityIn.validate();
         this.chunkTileEntityMap.put(pos, tileEntityIn);
      }
   }

   public Block getBlock(final int x, final int y, final int z) {
      try {
         return this.getBlock0(x & 15, y, z & 15);
      } catch (ReportedException reportedException) {
         reportedException.getCrashReport().makeCategory("Block being got").addCrashSectionCallable("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(new BlockPos(Chunk.this.xPosition * 16 + x, y, Chunk.this.zPosition * 16 + z));
            }
         });
         throw reportedException;
      }
   }

   public int getBlockMetadata(BlockPos pos) {
      return this.getBlockMetadata(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
   }

   public void setLightPopulated(boolean lightPopulated) {
      this.isLightPopulated = lightPopulated;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
      if (this.isChunkLoaded) {
         this.worldObj.addTileEntity(tileEntityIn);
      }
   }

   public void removeEntityAtIndex(Entity entityIn, int i) {
      if (i < 0) {
         i = 0;
      }

      if (i >= this.entityLists.length) {
         i = this.entityLists.length - 1;
      }

      this.entityLists[i].remove(entityIn);
   }

   public void setChunkModified() {
      this.isModified = true;
   }

   public Chunk(World worldIn, ChunkPrimer primer, int x, int z) {
      this(worldIn, x, z);
      boolean flag = !worldIn.provider.getHasNoSky();

      for (int i = 0; i < 16; i++) {
         for (int i2 = 0; i2 < 16; i2++) {
            for (int i3 = 0; i3 < 256; i3++) {
               int i4 = i * 256 * 16 | i2 * 256 | i3;
               IBlockState blockState = primer.getBlockState(i4);
               if (blockState.getBlock().getMaterial() != Material.air) {
                  int i5 = i3 >> 4;
                  if (this.storageArrays[i5] == null) {
                     this.storageArrays[i5] = new ExtendedBlockStorage(i5 << 4, flag);
                  }

                  this.storageArrays[i5].set(i, i3 & 15, i2, blockState);
               }
            }
         }
      }
   }

   private void recheckGaps(boolean flag) {
      this.worldObj.theProfiler.startSection("recheckGaps");
      if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16)) {
         for (int i = 0; i < 16; i++) {
            for (int i2 = 0; i2 < 16; i2++) {
               if (this.updateSkylightColumns[i + i2 * 16]) {
                  this.updateSkylightColumns[i + i2 * 16] = false;
                  int heightValue = this.getHeightValue(i, i2);
                  int i3 = this.xPosition * 16 + i;
                  int i4 = this.zPosition * 16 + i2;
                  int i5 = Integer.MAX_VALUE;

                  for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                     i5 = Math.min(i5, this.worldObj.getChunksLowestHorizon(i3 + enumFacing.getFrontOffsetX(), i4 + enumFacing.getFrontOffsetZ()));
                  }

                  this.checkSkylightNeighborHeight(i3, i4, i5);

                  for (EnumFacing enumFacing2 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                     this.checkSkylightNeighborHeight(i3 + enumFacing2.getFrontOffsetX(), i4 + enumFacing2.getFrontOffsetZ(), heightValue);
                  }

                  if (flag) {
                     this.worldObj.theProfiler.endSection();
                     return;
                  }
               }
            }
         }

         this.isGapLightingUpdated = false;
      }

      this.worldObj.theProfiler.endSection();
   }

   public BlockPos getPrecipitationHeight(BlockPos pos) {
      int i = pos.getX() & 15;
      int i2 = pos.getZ() & 15;
      int i3 = i | i2 << 4;
      BlockPos blockPos2 = new BlockPos(pos.getX(), this.precipitationHeightMap[i3], pos.getZ());
      if (blockPos2.getY() == -999) {
         int i4 = this.getTopFilledSegment() + 15;
         blockPos2 = new BlockPos(pos.getX(), i4, pos.getZ());
         int i5 = -1;

         while (blockPos2.getY() > 0 && i5 == -1) {
            Material material2 = this.getBlock(blockPos2).getMaterial();
            if (!material2.blocksMovement() && !material2.isLiquid()) {
               blockPos2 = blockPos2.down();
            } else {
               i5 = blockPos2.getY() + 1;
            }
         }

         this.precipitationHeightMap[i3] = i5;
      }

      return new BlockPos(pos.getX(), this.precipitationHeightMap[i3], pos.getZ());
   }

   public void removeEntity(Entity entityIn) {
      this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
   }

   public int getHeight(BlockPos pos) {
      return this.getHeightValue(pos.getX() & 15, pos.getZ() & 15);
   }

   public void onChunkUnload() {
      this.isChunkLoaded = false;

      for (TileEntity tileEntity2 : this.chunkTileEntityMap.values()) {
         this.worldObj.markTileEntityForRemoval(tileEntity2);
      }

      for (int i = 0; i < this.entityLists.length; i++) {
         this.worldObj.unloadEntities(this.entityLists[i]);
      }
   }

   public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
      return this.entityLists;
   }

   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType enumCreateEntityType) {
      TileEntity tileEntity = this.chunkTileEntityMap.get(pos);
      if (tileEntity == null) {
         if (enumCreateEntityType == Chunk.EnumCreateEntityType.IMMEDIATE) {
            tileEntity = this.createNewTileEntity(pos);
            this.worldObj.setTileEntity(pos, tileEntity);
         } else if (enumCreateEntityType == Chunk.EnumCreateEntityType.QUEUED) {
            this.tileEntityPosQueue.add(pos);
         }
      } else if (tileEntity.isInvalid()) {
         this.chunkTileEntityMap.remove(pos);
         return null;
      }

      return tileEntity;
   }

   public int getHeightValue(int x, int z) {
      return this.heightMap[z << 4 | x];
   }

   public void addEntity(Entity entityIn) {
      this.hasEntities = true;
      int i = MathHelper.floor_double(entityIn.posX / 16.0);
      int i2 = MathHelper.floor_double(entityIn.posZ / 16.0);
      if (i != this.xPosition || i2 != this.zPosition) {
         logger.warn(
            "Wrong location! (" + i + ", " + i2 + ") should be (" + this.xPosition + ", " + this.zPosition + "), " + entityIn,
            new Object[]{entityIn}
         );
         entityIn.setDead();
      }

      int i3 = MathHelper.floor_double(entityIn.posY / 16.0);
      if (i3 < 0) {
         i3 = 0;
      }

      if (i3 >= this.entityLists.length) {
         i3 = this.entityLists.length - 1;
      }

      entityIn.addedToChunk = true;
      entityIn.chunkCoordX = this.xPosition;
      entityIn.chunkCoordY = i3;
      entityIn.chunkCoordZ = this.zPosition;
      this.entityLists[i3].add(entityIn);
   }

   public boolean isEmpty() {
      return false;
   }

   public void checkLight() {
      this.isTerrainPopulated = true;
      this.isLightPopulated = true;
      BlockPos blockPos2 = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
      if (!this.worldObj.provider.getHasNoSky()) {
         if (this.worldObj.isAreaLoaded(blockPos2.add(-1, 0, -1), blockPos2.add(16, this.worldObj.getSeaLevel(), 16))) {
            label44:
            for (int i = 0; i < 16; i++) {
               for (int i2 = 0; i2 < 16; i2++) {
                  if (!this.recheckLightColumn(i, i2)) {
                     this.isLightPopulated = false;
                     break label44;
                  }
               }
            }

            if (this.isLightPopulated) {
               for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                  int i3 = enumFacing.getAxisDirection() == AxisDirection.POSITIVE ? 16 : 1;
                  this.worldObj.getChunkFromBlockCoords(blockPos2.offset(enumFacing, i3)).checkLightSide(enumFacing.getOpposite());
               }

               this.markSkylightColumnsForUpdate();
            }
         } else {
            this.isLightPopulated = false;
         }
      }
   }

   public int getLowestHeight() {
      return this.heightMapMinimum;
   }

   public ExtendedBlockStorage[] getBlockStorageArray() {
      return this.storageArrays;
   }

   public void onTick(boolean skipRecheckGaps) {
      if (this.isGapLightingUpdated && !this.worldObj.provider.getHasNoSky() && !skipRecheckGaps) {
         this.recheckGaps(this.worldObj.isRemote);
      }

      this.hasTicked = true;
      if (!this.isLightPopulated && this.isTerrainPopulated) {
         this.checkLight();
      }

      while (!this.tileEntityPosQueue.isEmpty()) {
         BlockPos blockPos = this.tileEntityPosQueue.poll();
         if (this.getTileEntity(blockPos, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlock(blockPos).hasTileEntity()) {
            TileEntity tileEntity = this.createNewTileEntity(blockPos);
            this.worldObj.setTileEntity(blockPos, tileEntity);
            this.worldObj.markBlockRangeForRenderUpdate(blockPos, blockPos);
         }
      }
   }

   public World getWorld() {
      return this.worldObj;
   }

   private Block getBlock0(int x, int y, int z) {
      Block air2 = Blocks.air;
      if (y >= 0 && y >> 4 < this.storageArrays.length) {
         ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y >> 4];
         if (extendedBlockStorage != null) {
            try {
               air2 = extendedBlockStorage.getBlockByExtId(x, y & 15, z);
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting block");
               throw new ReportedException(crashReport);
            }
         }
      }

      return air2;
   }

   public void populateChunk(IChunkProvider iChunkProvider, IChunkProvider iChunkProvider2, int x, int z) {
      boolean conditionMet = iChunkProvider.chunkExists(x, z - 1);
      boolean conditionMet2 = iChunkProvider.chunkExists(x + 1, z);
      boolean conditionMet3 = iChunkProvider.chunkExists(x, z + 1);
      boolean conditionMet4 = iChunkProvider.chunkExists(x - 1, z);
      boolean conditionMet5 = iChunkProvider.chunkExists(x - 1, z - 1);
      boolean conditionMet6 = iChunkProvider.chunkExists(x + 1, z + 1);
      boolean conditionMet7 = iChunkProvider.chunkExists(x - 1, z + 1);
      boolean conditionMet8 = iChunkProvider.chunkExists(x + 1, z - 1);
      if (conditionMet2 && conditionMet3 && conditionMet6) {
         if (!this.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x, z);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, this, x, z);
         }
      }

      if (conditionMet4 && conditionMet3 && conditionMet7) {
         Chunk chunk = iChunkProvider.provideChunk(x - 1, z);
         if (!chunk.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x - 1, z);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, chunk, x - 1, z);
         }
      }

      if (conditionMet && conditionMet2 && conditionMet8) {
         Chunk chunk3 = iChunkProvider.provideChunk(x, z - 1);
         if (!chunk3.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x, z - 1);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, chunk3, x, z - 1);
         }
      }

      if (conditionMet5 && conditionMet && conditionMet4) {
         Chunk chunk4 = iChunkProvider.provideChunk(x - 1, z - 1);
         if (!chunk4.isTerrainPopulated) {
            iChunkProvider.populate(iChunkProvider2, x - 1, z - 1);
         } else {
            iChunkProvider.populateChunk(iChunkProvider2, chunk4, x - 1, z - 1);
         }
      }
   }

   public int getBlockLightOpacity(BlockPos pos) {
      return this.getBlock(pos).getLightOpacity();
   }

   public void enqueueRelightChecks() {
      BlockPos blockPos2 = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

      for (int i = 0; i < 8; i++) {
         if (this.queuedLightChecks >= 4096) {
            return;
         }

         int i2 = this.queuedLightChecks % 16;
         int i3 = this.queuedLightChecks / 16 % 16;
         int i4 = this.queuedLightChecks / 256;
         this.queuedLightChecks++;

         for (int i5 = 0; i5 < 16; i5++) {
            BlockPos blockPos3 = blockPos2.add(i3, (i2 << 4) + i5, i4);
            boolean flag = i5 == 0 || i5 == 15 || i3 == 0 || i3 == 15 || i4 == 0 || i4 == 15;
            if (this.storageArrays[i2] == null && flag
               || this.storageArrays[i2] != null && this.storageArrays[i2].getBlockByExtId(i3, i5, i4).getMaterial() == Material.air) {
               for (EnumFacing enumFacing : EnumFacing.values()) {
                  BlockPos blockPos4 = blockPos3.offset(enumFacing);
                  if (this.worldObj.getBlockState(blockPos4).getBlock().getLightValue() > 0) {
                     this.worldObj.checkLight(blockPos4);
                  }
               }

               this.worldObj.checkLight(blockPos3);
            }
         }
      }
   }

   private void relightBlock(int x, int y, int z) {
      int i = this.heightMap[z << 4 | x] & 0xFF;
      int i2 = i;
      if (y > i) {
         i2 = y;
      }

      while (i2 > 0 && this.getBlockLightOpacity(x, i2 - 1, z) == 0) {
         i2--;
      }

      if (i2 != i) {
         this.worldObj.markBlocksDirtyVertical(x + this.xPosition * 16, z + this.zPosition * 16, i2, i);
         this.heightMap[z << 4 | x] = i2;
         int i3 = this.xPosition * 16 + x;
         int i4 = this.zPosition * 16 + z;
         if (!this.worldObj.provider.getHasNoSky()) {
            if (i2 < i) {
               for (int i5 = i2; i5 < i; i5++) {
                  ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i5 >> 4];
                  if (extendedBlockStorage != null) {
                     extendedBlockStorage.setExtSkylightValue(x, i5 & 15, z, 15);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, i5, (this.zPosition << 4) + z));
                  }
               }
            } else {
               for (int i6 = i; i6 < i2; i6++) {
                  ExtendedBlockStorage extendedBlockStorage2 = this.storageArrays[i6 >> 4];
                  if (extendedBlockStorage2 != null) {
                     extendedBlockStorage2.setExtSkylightValue(x, i6 & 15, z, 0);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, i6, (this.zPosition << 4) + z));
                  }
               }
            }

            int i7 = 15;

            while (i2 > 0 && i7 > 0) {
               int blockLightOpacity = this.getBlockLightOpacity(x, --i2, z);
               if (blockLightOpacity == 0) {
                  blockLightOpacity = 1;
               }

               i7 -= blockLightOpacity;
               if (i7 < 0) {
                  i7 = 0;
               }

               ExtendedBlockStorage extendedBlockStorage3 = this.storageArrays[i2 >> 4];
               if (extendedBlockStorage3 != null) {
                  extendedBlockStorage3.setExtSkylightValue(x, i2 & 15, z, i7);
               }
            }
         }

         int i8 = this.heightMap[z << 4 | x];
         int i9 = i;
         int i10 = i8;
         if (i8 < i) {
            i9 = i8;
            i10 = i;
         }

         if (i8 < this.heightMapMinimum) {
            this.heightMapMinimum = i8;
         }

         if (!this.worldObj.provider.getHasNoSky()) {
            for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
               this.updateSkylightNeighborHeight(i3 + enumFacing.getFrontOffsetX(), i4 + enumFacing.getFrontOffsetZ(), i9, i10);
            }

            this.updateSkylightNeighborHeight(i3, i4, i9, i10);
         }

         this.isModified = true;
      }
   }

   public Block getBlock(final BlockPos pos) {
      try {
         return this.getBlock0(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      } catch (ReportedException reportedException) {
         reportedException.getCrashReport().makeCategory("Block being got").addCrashSectionCallable("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(pos);
            }
         });
         throw reportedException;
      }
   }

   public boolean isTerrainPopulated() {
      return this.isTerrainPopulated;
   }

   public boolean isLightInitialized() {
      return (!this.hasEntities || this.worldObj.getTotalWorldTime() == this.lastSaveTime) && !this.isModified ? this.isModified : true;
   }

   public byte[] getBiomeArray() {
      return this.blockBiomeArray;
   }

   public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> predicate) {
      int i = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
      int i2 = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
      i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
      i2 = MathHelper.clamp_int(i2, 0, this.entityLists.length - 1);

      for (int i3 = i; i3 <= i2; i3++) {
         if (!this.entityLists[i3].isEmpty()) {
            for (Entity entity2 : this.entityLists[i3]) {
               if (entity2.getEntityBoundingBox().intersectsWith(aabb) && entity2 != entityIn) {
                  if (predicate == null || predicate.apply(entity2)) {
                     listToFill.add(entity2);
                  }

                  Entity[] parts = entity2.getParts();
                  if (parts != null) {
                     for (int i4 = 0; i4 < parts.length; i4++) {
                        entity2 = parts[i4];
                        if (entity2 != entityIn && entity2.getEntityBoundingBox().intersectsWith(aabb) && (predicate == null || predicate.apply(entity2))) {
                           listToFill.add(entity2);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays) {
      if (this.storageArrays.length != newStorageArrays.length) {
         logger.warn("Could not set level chunk sections, array length is " + newStorageArrays.length + " instead of " + this.storageArrays.length);
      } else {
         for (int i = 0; i < this.storageArrays.length; i++) {
            this.storageArrays[i] = newStorageArrays[i];
         }
      }
   }

   public void setBiomeArray(byte[] biomeArray) {
      if (this.blockBiomeArray.length != biomeArray.length) {
         logger.warn("Could not set level chunk biomes, array length is " + biomeArray.length + " instead of " + this.blockBiomeArray.length);
      } else {
         for (int i = 0; i < this.blockBiomeArray.length; i++) {
            this.blockBiomeArray[i] = biomeArray[i];
         }
      }
   }

   public void generateSkylightMap() {
      int topFilledSegment = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int innerIndex = 0; innerIndex < 16; innerIndex++) {
         for (int nestedIndex = 0; nestedIndex < 16; nestedIndex++) {
            this.precipitationHeightMap[innerIndex + (nestedIndex << 4)] = -999;

            for (int outerIndex = topFilledSegment + 16; outerIndex > 0; outerIndex--) {
               if (this.getBlockLightOpacity(innerIndex, outerIndex - 1, nestedIndex) != 0) {
                  this.heightMap[nestedIndex << 4 | innerIndex] = outerIndex;
                  if (outerIndex < this.heightMapMinimum) {
                     this.heightMapMinimum = outerIndex;
                  }
                  break;
               }
            }

            if (!this.worldObj.provider.getHasNoSky()) {
               int i = 15;
               int i2 = topFilledSegment + 16 - 1;

               while (true) {
                  int blockLightOpacity = this.getBlockLightOpacity(innerIndex, i2, nestedIndex);
                  if (blockLightOpacity == 0 && i != 15) {
                     blockLightOpacity = 1;
                  }

                  i -= blockLightOpacity;
                  if (i > 0) {
                     ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i2 >> 4];
                     if (extendedBlockStorage != null) {
                        extendedBlockStorage.setExtSkylightValue(innerIndex, i2 & 15, nestedIndex, i);
                        this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + innerIndex, i2, (this.zPosition << 4) + nestedIndex));
                     }
                  }

                  if (--i2 <= 0 || i <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.isModified = true;
   }

   public void update2() {
      this.isModified = false;
   }

   private int getBlockLightOpacity(int x, int y, int z) {
      return this.getBlock0(x, y, z).getLightOpacity();
   }

   public void setTerrainPopulated(boolean terrainPopulated) {
      this.isTerrainPopulated = terrainPopulated;
   }

   public void removeTileEntity(BlockPos pos) {
      if (this.isChunkLoaded) {
         TileEntity tileEntity = this.chunkTileEntityMap.remove(pos);
         if (tileEntity != null) {
            tileEntity.invalidate();
         }
      }
   }

   private void markSkylightColumnsForUpdate() {
      for (int index = 0; index < this.updateSkylightColumns.length; index++) {
         this.updateSkylightColumns[index] = true;
      }

      this.recheckGaps(false);
   }

   public int[] getHeightMap() {
      return this.heightMap;
   }

   public boolean isAtLocation(int x, int z) {
      return x == this.xPosition && z == this.zPosition;
   }

   public void setLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos, int value) {
      int i = pos.getX() & 15;
      int y2 = pos.getY();
      int i2 = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y2 >> 4];
      if (extendedBlockStorage == null) {
         extendedBlockStorage = this.storageArrays[y2 >> 4] = new ExtendedBlockStorage(y2 >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         this.generateSkylightMap();
      }

      this.isModified = true;
      if (enumSkyBlock == EnumSkyBlock.SKY) {
         if (!this.worldObj.provider.getHasNoSky()) {
            extendedBlockStorage.setExtSkylightValue(i, y2 & 15, i2, value);
         }
      } else if (enumSkyBlock == EnumSkyBlock.BLOCK) {
         extendedBlockStorage.setExtBlocklightValue(i, y2 & 15, i2, value);
      }
   }

   public boolean isLoaded() {
      return this.isChunkLoaded;
   }

   private TileEntity createNewTileEntity(BlockPos pos) {
      Block block = this.getBlock(pos);
      return !block.hasTileEntity() ? null : ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, this.getBlockMetadata(pos));
   }

   public boolean getAreLevelsEmpty(int startY, int endY) {
      if (startY < 0) {
         startY = 0;
      }

      if (endY >= 256) {
         endY = 255;
      }

      for (int index = startY; index <= endY; index += 16) {
         ExtendedBlockStorage extendedBlockStorage = this.storageArrays[index >> 4];
         if (extendedBlockStorage != null && !extendedBlockStorage.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   protected void generateHeightMap() {
      int topFilledSegment = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int i = 0; i < 16; i++) {
         for (int i2 = 0; i2 < 16; i2++) {
            this.precipitationHeightMap[i + (i2 << 4)] = -999;

            for (int i3 = topFilledSegment + 16; i3 > 0; i3--) {
               if (this.getBlock0(i, i3 - 1, i2).getLightOpacity() != 0) {
                  this.heightMap[i2 << 4 | i] = i3;
                  if (i3 < this.heightMapMinimum) {
                     this.heightMapMinimum = i3;
                  }
                  break;
               }
            }
         }
      }

      this.isModified = true;
   }

   public void setHasEntities(boolean hasEntitiesIn) {
      this.hasEntities = hasEntitiesIn;
   }

   public void setInhabitedTime(long newInhabitedTime) {
      this.inhabitedTime = newInhabitedTime;
   }

   public void fillChunk(byte[] bArray, int i, boolean flag) {
      int i2 = 0;
      boolean flag2 = !this.worldObj.provider.getHasNoSky();

      for (int i3 = 0; i3 < this.storageArrays.length; i3++) {
         if ((i & 1 << i3) != 0) {
            if (this.storageArrays[i3] == null) {
               this.storageArrays[i3] = new ExtendedBlockStorage(i3 << 4, flag2);
            }

            char[] data = this.storageArrays[i3].getData();

            for (int i4 = 0; i4 < data.length; i4++) {
               data[i4] = (char)((bArray[i2 + 1] & 255) << 8 | bArray[i2] & 0xFF);
               i2 += 2;
            }
         } else if (flag && this.storageArrays[i3] != null) {
            this.storageArrays[i3] = null;
         }
      }

      for (int i5 = 0; i5 < this.storageArrays.length; i5++) {
         if ((i & 1 << i5) != 0 && this.storageArrays[i5] != null) {
            NibbleArray blocklightArray = this.storageArrays[i5].getBlocklightArray();
            System.arraycopy(bArray, i2, blocklightArray.getData(), 0, blocklightArray.getData().length);
            i2 += blocklightArray.getData().length;
         }
      }

      if (flag2) {
         for (int i6 = 0; i6 < this.storageArrays.length; i6++) {
            if ((i & 1 << i6) != 0 && this.storageArrays[i6] != null) {
               NibbleArray skylightArray = this.storageArrays[i6].getSkylightArray();
               System.arraycopy(bArray, i2, skylightArray.getData(), 0, skylightArray.getData().length);
               i2 += skylightArray.getData().length;
            }
         }
      }

      if (flag) {
         System.arraycopy(bArray, i2, this.blockBiomeArray, 0, this.blockBiomeArray.length);
         int length2 = this.blockBiomeArray.length;
      }

      for (int i7 = 0; i7 < this.storageArrays.length; i7++) {
         if (this.storageArrays[i7] != null && (i & 1 << i7) != 0) {
            this.storageArrays[i7].removeInvalidBlocks();
         }
      }

      this.isLightPopulated = true;
      this.isTerrainPopulated = true;
      this.generateHeightMap();

      for (TileEntity tileEntity2 : this.chunkTileEntityMap.values()) {
         tileEntity2.updateContainingBlockInfo();
      }
   }

   public boolean isPopulated() {
      return this.hasTicked && this.isTerrainPopulated && this.isLightPopulated;
   }

   private void updateSkylightNeighborHeight(int x, int z, int startY, int endY) {
      if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x, 0, z), 16)) {
         for (int index = startY; index < endY; index++) {
            this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, index, z));
         }

         this.isModified = true;
      }
   }

   public BiomeGenBase getBiome(BlockPos pos, WorldChunkManager chunkManager) {
      int xCoordinate = pos.getX() & 15;
      int zCoordinate = pos.getZ() & 15;
      int i = this.blockBiomeArray[zCoordinate << 4 | xCoordinate] & 255;
      if (i == 255) {
         BiomeGenBase biomeGenBase = chunkManager.getBiomeGenerator(pos, BiomeGenBase.plains);
         i = biomeGenBase.biomeID;
         this.blockBiomeArray[zCoordinate << 4 | xCoordinate] = (byte)(i & 0xFF);
      }

      BiomeGenBase biomeGenBase2 = BiomeGenBase.getBiome(i);
      return biomeGenBase2 == null ? BiomeGenBase.plains : biomeGenBase2;
   }

   public boolean isLightPopulated() {
      return this.isLightPopulated;
   }

   public int getLightFor(EnumSkyBlock type, BlockPos pos) {
      int xCoordinate = pos.getX() & 15;
      int yCoordinate = pos.getY();
      int zCoordinate = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[yCoordinate >> 4];
      return extendedBlockStorage == null
         ? (this.canSeeSky(pos) ? type.defaultLightValue : 0)
         : (
            type == EnumSkyBlock.SKY
               ? (this.worldObj.provider.getHasNoSky() ? 0 : extendedBlockStorage.getExtSkylightValue(xCoordinate, yCoordinate & 15, zCoordinate))
               : (type == EnumSkyBlock.BLOCK ? extendedBlockStorage.getExtBlocklightValue(xCoordinate, yCoordinate & 15, zCoordinate) : type.defaultLightValue)
         );
   }

   public void update() {
      this.isChunkLoaded = true;
   }

   public void resetRelightChecks() {
      this.queuedLightChecks = 0;
   }

   public <T extends Entity> void getEntitiesOfTypeWithinAAAB(
      Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> predicate
   ) {
      int i = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
      int i2 = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
      i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
      i2 = MathHelper.clamp_int(i2, 0, this.entityLists.length - 1);

      for (int i3 = i; i3 <= i2; i3++) {
         for (Entity entity2 : this.entityLists[i3].getByClass(entityClass)) {
            if (entity2.getEntityBoundingBox().intersectsWith(aabb) && (predicate == null || ((com.google.common.base.Predicate)predicate).apply(entity2))) {
               listToFill.add((T)entity2);
            }
         }
      }
   }

   private void checkLightSide(EnumFacing facing) {
      if (this.isTerrainPopulated) {
         if (facing == EnumFacing.EAST) {
            for (int index = 0; index < 16; index++) {
               this.recheckLightColumn(15, index);
            }
         } else if (facing == EnumFacing.WEST) {
            for (int innerIndex = 0; innerIndex < 16; innerIndex++) {
               this.recheckLightColumn(0, innerIndex);
            }
         } else if (facing == EnumFacing.SOUTH) {
            for (int nestedIndex = 0; nestedIndex < 16; nestedIndex++) {
               this.recheckLightColumn(nestedIndex, 15);
            }
         } else if (facing == EnumFacing.NORTH) {
            for (int outerIndex = 0; outerIndex < 16; outerIndex++) {
               this.recheckLightColumn(outerIndex, 0);
            }
         }
      }
   }

   public int getTopFilledSegment() {
      for (int index = this.storageArrays.length - 1; index >= 0; index--) {
         if (this.storageArrays[index] != null) {
            return this.storageArrays[index].getYLocation();
         }
      }

      return 0;
   }

   public void onChunkLoad() {
      this.isChunkLoaded = true;
      this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

      for (int index = 0; index < this.entityLists.length; index++) {
         Iterator iterator2 = this.entityLists[index].iterator();

         while (iterator2.hasNext()) {
            ((Entity)iterator2.next()).onChunkLoad();
         }

         this.worldObj.loadEntities(this.entityLists[index]);
      }
   }

   public Random getRandom() {
      return new Random(
         this.worldObj.getSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ 987234911L
      );
   }

   public Map<BlockPos, TileEntity> getTileEntityMap() {
      return this.chunkTileEntityMap;
   }

   public IBlockState getBlockState(final BlockPos pos) {
      if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
         IBlockState iBlockState = null;
         if (pos.getY() == 60) {
            iBlockState = Blocks.barrier.getDefaultState();
         }

         if (pos.getY() == 70) {
            iBlockState = ChunkProviderDebug.getDebugBlockState(pos.getX(), pos.getZ());
         }

         return iBlockState == null ? Blocks.air.getDefaultState() : iBlockState;
      } else {
         try {
            if (pos.getY() >= 0 && pos.getY() >> 4 < this.storageArrays.length) {
               ExtendedBlockStorage extendedBlockStorage = this.storageArrays[pos.getY() >> 4];
               if (extendedBlockStorage != null) {
                  int i = pos.getX() & 15;
                  int i2 = pos.getY() & 15;
                  int i3 = pos.getZ() & 15;
                  return extendedBlockStorage.get(i, i2, i3);
               }
            }

            return Blocks.air.getDefaultState();
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting block state");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being got");
            crashReportCategory.addCrashSectionCallable("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.getCoordinateInfo(pos);
               }
            });
            throw new ReportedException(crashReport);
         }
      }
   }

   private boolean recheckLightColumn(int x, int z) {
      int topFilledSegment = this.getTopFilledSegment();
      boolean conditionMet = false;
      boolean conditionMet2 = false;
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos((this.xPosition << 4) + x, 0, (this.zPosition << 4) + z);

      for (int innerIndex = topFilledSegment + 16 - 1; innerIndex > this.worldObj.getSeaLevel() || innerIndex > 0 && !conditionMet2; innerIndex--) {
         mutableBlockPos.set(mutableBlockPos.getX(), innerIndex, mutableBlockPos.getZ());
         int blockLightOpacity = this.getBlockLightOpacity(mutableBlockPos);
         if (blockLightOpacity == 255 && mutableBlockPos.getY() < this.worldObj.getSeaLevel()) {
            conditionMet2 = true;
         }

         if (!conditionMet && blockLightOpacity > 0) {
            conditionMet = true;
         } else if (conditionMet && blockLightOpacity == 0 && !this.worldObj.checkLight(mutableBlockPos)) {
            return false;
         }
      }

      for (int outerIndex = mutableBlockPos.getY(); outerIndex > 0; outerIndex--) {
         mutableBlockPos.set(mutableBlockPos.getX(), outerIndex, mutableBlockPos.getZ());
         if (this.getBlock(mutableBlockPos).getLightValue() > 0) {
            this.worldObj.checkLight(mutableBlockPos);
         }
      }

      return true;
   }

   public IBlockState setBlockState(BlockPos pos, IBlockState state) {
      int i = pos.getX() & 15;
      int y2 = pos.getY();
      int i2 = pos.getZ() & 15;
      int i3 = i2 << 4 | i;
      if (y2 >= this.precipitationHeightMap[i3] - 1) {
         this.precipitationHeightMap[i3] = -999;
      }

      int i4 = this.heightMap[i3];
      IBlockState blockState = this.getBlockState(pos);
      if (blockState == state) {
         return null;
      }

      Block block2 = state.getBlock();
      Block block3 = blockState.getBlock();
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y2 >> 4];
      boolean flag = false;
      if (extendedBlockStorage == null) {
         if (block2 == Blocks.air) {
            return null;
         }

         extendedBlockStorage = this.storageArrays[y2 >> 4] = new ExtendedBlockStorage(y2 >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         flag = y2 >= i4;
      }

      extendedBlockStorage.set(i, y2 & 15, i2, state);
      if (block3 != block2) {
         if (!this.worldObj.isRemote) {
            block3.breakBlock(this.worldObj, pos, blockState);
         } else if (block3 instanceof ITileEntityProvider) {
            this.worldObj.removeTileEntity(pos);
         }
      }

      if (extendedBlockStorage.getBlockByExtId(i, y2 & 15, i2) != block2) {
         return null;
      }

      if (flag) {
         this.generateSkylightMap();
      } else {
         int lightOpacity = block2.getLightOpacity();
         int lightOpacity2 = block3.getLightOpacity();
         if (lightOpacity > 0) {
            if (y2 >= i4) {
               this.relightBlock(i, y2 + 1, i2);
            }
         } else if (y2 == i4 - 1) {
            this.relightBlock(i, y2, i2);
         }

         if (lightOpacity != lightOpacity2 && (lightOpacity < lightOpacity2 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
            this.propagateSkylightOcclusion(i, i2);
         }
      }

      if (block3 instanceof ITileEntityProvider) {
         TileEntity tileEntity2 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
         if (tileEntity2 != null) {
            tileEntity2.updateContainingBlockInfo();
         }
      }

      if (!this.worldObj.isRemote && block3 != block2) {
         block2.onBlockAdded(this.worldObj, pos, state);
      }

      if (block2 instanceof ITileEntityProvider) {
         TileEntity tileEntity3 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
         if (tileEntity3 == null) {
            tileEntity3 = ((ITileEntityProvider)block2).createNewTileEntity(this.worldObj, block2.getMetaFromState(state));
            this.worldObj.setTileEntity(pos, tileEntity3);
         }

         if (tileEntity3 != null) {
            tileEntity3.updateContainingBlockInfo();
         }
      }

      this.isModified = true;
      return blockState;
   }

   private void propagateSkylightOcclusion(int x, int z) {
      this.updateSkylightColumns[x + z * 16] = true;
      this.isGapLightingUpdated = true;
   }

   public void setLastSaveTime(long saveTime) {
      this.lastSaveTime = saveTime;
   }

   public void setHeightMap(int[] newHeightMap) {
      if (this.heightMap.length != newHeightMap.length) {
         logger.warn("Could not set level chunk heightmap, array length is " + newHeightMap.length + " instead of " + this.heightMap.length);
      } else {
         for (int i = 0; i < this.heightMap.length; i++) {
            this.heightMap[i] = newHeightMap[i];
         }
      }
   }

   public Chunk(World worldIn, int x, int z) {
      this.worldObj = worldIn;
      this.xPosition = x;
      this.zPosition = z;
      this.heightMap = new int[256];

      for (int i = 0; i < this.entityLists.length; i++) {
         this.entityLists[i] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      Arrays.fill(this.precipitationHeightMap, -999);
      Arrays.fill(this.blockBiomeArray, (byte)-1);
   }

   private void checkSkylightNeighborHeight(int x, int z, int maxValue) {
      int yCoordinate = this.worldObj.getHeight(new BlockPos(x, 0, z)).getY();
      if (yCoordinate > maxValue) {
         this.updateSkylightNeighborHeight(x, z, maxValue, yCoordinate + 1);
      } else if (yCoordinate < maxValue) {
         this.updateSkylightNeighborHeight(x, z, yCoordinate, maxValue + 1);
      }
   }

   private int getBlockMetadata(int x, int y, int z) {
      if (y >> 4 >= this.storageArrays.length) {
         return 0;
      }

      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y >> 4];
      return extendedBlockStorage != null ? extendedBlockStorage.getExtBlockMetadata(x, y & 15, z) : 0;
   }

   public enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
