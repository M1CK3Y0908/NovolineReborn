package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase {
   protected Map<Long, StructureStart> structureMap = Maps.newHashMap();
   private MapGenStructureData structureData;

   protected StructureStart getStructureAt(BlockPos pos) {
      for (StructureStart structureStart2 : this.structureMap.values()) {
         if (structureStart2.isSizeableStructure() && structureStart2.getBoundingBox().isVecInside(pos)) {
            Iterator iterator2 = structureStart2.getComponents().iterator();

            while (iterator2.hasNext()) {
               if (((StructureComponent)iterator2.next()).getBoundingBox().isVecInside(pos)) {
                  return structureStart2;
               }
            }
         }
      }

      return null;
   }

   public boolean isInsideStructure(BlockPos pos) {
      this.initializeStructureData(this.worldObj);
      return this.getStructureAt(pos) != null;
   }

   public abstract String getStructureName();

   public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos) {
      this.worldObj = worldIn;
      this.initializeStructureData(worldIn);
      this.rand.setSeed(worldIn.getSeed());
      long l = this.rand.nextLong();
      long l2 = this.rand.nextLong();
      long l3 = (pos.getX() >> 4) * l;
      long l4 = (pos.getZ() >> 4) * l2;
      this.rand.setSeed(l3 ^ l4 ^ worldIn.getSeed());
      this.recursiveGenerate(worldIn, pos.getX() >> 4, pos.getZ() >> 4, 0, 0, (ChunkPrimer)null);
      double d2 = Double.MAX_VALUE;
      BlockPos blockPos = null;

      for (StructureStart structureStart2 : this.structureMap.values()) {
         if (structureStart2.isSizeableStructure()) {
            StructureComponent structureComponent = structureStart2.getComponents().get(0);
            BlockPos boundingBoxCenter = structureComponent.getBoundingBoxCenter();
            double d3 = boundingBoxCenter.distanceSq(pos);
            if (d3 < d2) {
               d2 = d3;
               blockPos = boundingBoxCenter;
            }
         }
      }

      if (blockPos != null) {
         return blockPos;
      }

      List coordList = this.getCoordList();
      if (coordList != null) {
         BlockPos blockPos2 = null;

         for (BlockPos blockPos3 : (Iterable<BlockPos>) coordList) {
            double d4 = blockPos3.distanceSq(pos);
            if (d4 < d2) {
               d2 = d4;
               blockPos2 = blockPos3;
            }
         }

         return blockPos2;
      } else {
         return null;
      }
   }

   private void setStructureStart(int chunkX, int chunkZ, StructureStart start) {
      this.structureData.writeInstance(start.writeStructureComponentsToNBT(chunkX, chunkZ), chunkX, chunkZ);
      this.structureData.markDirty();
   }

   private static ReportedException createReportedException(ReportedException reportedException) {
      return reportedException;
   }

   public boolean generateStructure(World worldIn, Random randomIn, ChunkCoordIntPair chunkCoord) {
      this.initializeStructureData(worldIn);
      int i = (chunkCoord.chunkXPos << 4) + 8;
      int i2 = (chunkCoord.chunkZPos << 4) + 8;
      boolean flag = false;

      for (StructureStart structureStart2 : this.structureMap.values()) {
         if (structureStart2.isSizeableStructure()
            && structureStart2.isValidForPostProcess(chunkCoord)
            && structureStart2.getBoundingBox().intersectsWith(i, i2, i + 15, i2 + 15)) {
            structureStart2.generateStructure(worldIn, randomIn, new StructureBoundingBox(i, i2, i + 15, i2 + 15));
            structureStart2.setChunkProcessed(chunkCoord);
            flag = true;
            this.setStructureStart(structureStart2.getChunkPosX(), structureStart2.getChunkPosZ(), structureStart2);
         }
      }

      return flag;
   }

   @Override
   protected final void recursiveGenerate(World worldIn, final int chunkX, final int chunkZ, int i, int i2, ChunkPrimer chunkPrimerIn) {
      this.initializeStructureData(worldIn);
      if (!this.structureMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ))) {
         this.rand.nextInt();

         try {
            if (this.canSpawnStructureAtCoords(chunkX, chunkZ)) {
               StructureStart structureStart = this.getStructureStart(chunkX, chunkZ);
               this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ), structureStart);
               this.setStructureStart(chunkX, chunkZ, structureStart);
            }
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception preparing structure feature");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Feature being prepared");
            crashReportCategory.addCrashSectionCallable("Is feature chunk", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.canSpawnStructureAtCoords(chunkX, chunkZ) ? "True" : "False";
               }
            });
            crashReportCategory.addCrashSection("Chunk location", String.format("%d,%d", chunkX, chunkZ));
            crashReportCategory.addCrashSectionCallable("Chunk pos hash", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
               }
            });
            crashReportCategory.addCrashSectionCallable("Structure type", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.getClass().getCanonicalName();
               }
            });
            throw new ReportedException(crashReport);
         }
      }
   }

   protected abstract StructureStart getStructureStart(int i, int i2);

   private void initializeStructureData(World worldIn) {
      if (this.structureData == null) {
         this.structureData = (MapGenStructureData)worldIn.loadItemData(MapGenStructureData.class, this.getStructureName());
         if (this.structureData == null) {
            this.structureData = new MapGenStructureData(this.getStructureName());
            worldIn.setItemData(this.getStructureName(), this.structureData);
         } else {
            NBTTagCompound tagCompound = this.structureData.getTagCompound();

            for (String text : tagCompound.getKeySet()) {
               NBTBase tag = tagCompound.getTag(text);
               if (tag.getId() == 10) {
                  NBTTagCompound nbtTagCompound = (NBTTagCompound)tag;
                  if (nbtTagCompound.hasKey("ChunkX") && nbtTagCompound.hasKey("ChunkZ")) {
                     int integer = nbtTagCompound.getInteger("ChunkX");
                     int integer2 = nbtTagCompound.getInteger("ChunkZ");
                     StructureStart structureStart2 = MapGenStructureIO.getStructureStart(nbtTagCompound, worldIn);
                     if (structureStart2 != null) {
                        this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(integer, integer2), structureStart2);
                     }
                  }
               }
            }
         }
      }
   }

   protected abstract boolean canSpawnStructureAtCoords(int i, int i2);

   protected List<BlockPos> getCoordList() {
      return null;
   }

   public boolean isPositionInStructure(World worldIn, BlockPos pos) {
      this.initializeStructureData(worldIn);

      for (StructureStart structureStart2 : this.structureMap.values()) {
         if (structureStart2.isSizeableStructure() && structureStart2.getBoundingBox().isVecInside(pos)) {
            return true;
         }
      }

      return false;
   }
}
