package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureOceanMonumentPieces.MonumentBuilding;

public class StructureOceanMonument extends MapGenStructure {
   public static final List<BiomeGenBase> MONUMENT_SPAWN_BIOMES = Arrays.asList(
      BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver
   );
   private static final List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> GUARDIAN_SPAWN_LIST = Lists.newArrayList();
   private int spacing = 32;
   private int separation = 5;

   @Override
   public String getStructureName() {
      return "Monument";
   }

   @Override
   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public StructureOceanMonument() {
   }

   public StructureOceanMonument(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("spacing")) {
            this.spacing = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.spacing, 1);
         } else if (((String)entry.getKey()).equals("separation")) {
            this.separation = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.separation, 1);
         }
      }
   }

   @Override
   protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int chunkX2 = chunkX;
      int chunkZ2 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.spacing - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.spacing - 1;
      }

      int i = chunkX / this.spacing;
      int i2 = chunkZ / this.spacing;
      Random random2 = this.worldObj.setRandomSeed(i, i2, 10387313);
      i *= this.spacing;
      i2 *= this.spacing;
      i += (random2.nextInt(this.spacing - this.separation) + random2.nextInt(this.spacing - this.separation)) / 2;
      i2 += (random2.nextInt(this.spacing - this.separation) + random2.nextInt(this.spacing - this.separation)) / 2;
      if (chunkX2 == i && chunkZ2 == i2) {
         if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(chunkX2 * 16 + 8, 64, chunkZ2 * 16 + 8), (BiomeGenBase)null)
            != BiomeGenBase.deepOcean) {
            return false;
         }

         if (this.worldObj.getWorldChunkManager().areBiomesViable(chunkX2 * 16 + 8, chunkZ2 * 16 + 8, 29, MONUMENT_SPAWN_BIOMES)) {
            return true;
         }
      }

      return false;
   }

   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList() {
      return GUARDIAN_SPAWN_LIST;
   }

   static {
      GUARDIAN_SPAWN_LIST.add(new net.minecraft.world.biome.BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
   }

   public static class StartMonument extends StructureStart {
      private Set<ChunkCoordIntPair> processedChunks = Sets.newHashSet();
      private boolean hasGenerated;

      private void create(World worldIn, Random random2, int i, int i2) {
         random2.setSeed(worldIn.getSeed());
         long l = random2.nextLong();
         long l2 = random2.nextLong();
         long l3 = i * l;
         long l4 = i2 * l2;
         random2.setSeed(l3 ^ l4 ^ worldIn.getSeed());
         int i3 = i * 16 + 8 - 29;
         int i4 = i2 * 16 + 8 - 29;
         EnumFacing facing = Plane.HORIZONTAL.random(random2);
         this.components.add(new MonumentBuilding(random2, i3, i4, facing));
         this.updateBoundingBox();
         this.hasGenerated = true;
      }

      @Override
      public void readFromNBT(NBTTagCompound tagCompound) {
         super.readFromNBT(tagCompound);
         if (tagCompound.hasKey("Processed", 9)) {
            NBTTagList nBTTagList = tagCompound.getTagList("Processed", 10);

            for (int index = 0; index < nBTTagList.tagCount(); index++) {
               NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
               this.processedChunks.add(new ChunkCoordIntPair(nBTTagCompound.getInteger("X"), nBTTagCompound.getInteger("Z")));
            }
         }
      }

      @Override
      public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb) {
         if (!this.hasGenerated) {
            this.components.clear();
            this.create(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
         }

         super.generateStructure(worldIn, rand, structurebb);
      }

      @Override
      public boolean isValidForPostProcess(ChunkCoordIntPair pair) {
         return this.processedChunks.contains(pair) ? false : super.isValidForPostProcess(pair);
      }

      @Override
      public void writeToNBT(NBTTagCompound tagCompound) {
         super.writeToNBT(tagCompound);
         NBTTagList nbtTagList = new NBTTagList();

         for (ChunkCoordIntPair chunkCoordIntPair : this.processedChunks) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setInteger("X", chunkCoordIntPair.chunkXPos);
            nbtTagCompound.setInteger("Z", chunkCoordIntPair.chunkZPos);
            nbtTagList.appendTag(nbtTagCompound);
         }

         tagCompound.setTag("Processed", nbtTagList);
      }

      public StartMonument(World worldIn, Random random2, int i, int i2) {
         super(i, i2);
         this.create(worldIn, random2, i, i2);
      }

      public StartMonument() {
      }

      @Override
      public void setChunkProcessed(ChunkCoordIntPair pair) {
         super.setChunkProcessed(pair);
         this.processedChunks.add(pair);
      }
   }
}
