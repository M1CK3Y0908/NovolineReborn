package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Road;

public class MapGenVillage extends MapGenStructure {
   private int size;
   private int distance = 32;
   public static final List<BiomeGenBase> villageSpawnBiomes = Arrays.asList(BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna);
   private int minDistance = 8;

   @Override
   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenVillage.Start(this.worldObj, this.rand, chunkX, chunkZ, this.size);
   }

   public MapGenVillage() {
   }

   @Override
   protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int chunkX2 = chunkX;
      int chunkZ2 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.distance - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.distance - 1;
      }

      int i = chunkX / this.distance;
      int i2 = chunkZ / this.distance;
      Random random = this.worldObj.setRandomSeed(i, i2, 10387312);
      i *= this.distance;
      i2 *= this.distance;
      i += random.nextInt(this.distance - this.minDistance);
      i2 += random.nextInt(this.distance - this.minDistance);
      return chunkX2 == i
         && chunkZ2 == i2
         && this.worldObj.getWorldChunkManager().areBiomesViable(chunkX2 * 16 + 8, chunkZ2 * 16 + 8, 0, villageSpawnBiomes);
   }

   @Override
   public String getStructureName() {
      return "Village";
   }

   public MapGenVillage(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("size")) {
            this.size = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.size, 0);
         } else if (((String)entry.getKey()).equals("distance")) {
            this.distance = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.distance, this.minDistance + 1);
         }
      }
   }

   public static class Start extends StructureStart {
      private boolean hasMoreThanTwoComponents;

      @Override
      public void readFromNBT(NBTTagCompound tagCompound) {
         super.readFromNBT(tagCompound);
         this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
      }

      public Start() {
      }

      @Override
      public boolean isSizeableStructure() {
         return this.hasMoreThanTwoComponents;
      }

      public Start(World worldIn, Random rand, int x, int z, int size) {
         super(x, z);
         List<PieceWeight> list = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, size);
         StructureVillagePieces.Start start = new StructureVillagePieces.Start(worldIn.getWorldChunkManager(), rand, (x << 4) + 2, (z << 4) + 2, list, size);
         this.components.add(start);
         start.buildComponent(start, this.components, rand);
         List<StructureComponent> list3 = start.villageRoads;
         List<StructureComponent> list4 = start.villageComponents;

         while (!list3.isEmpty() || !list4.isEmpty()) {
            if (list3.isEmpty()) {
               int count = rand.nextInt(list4.size());
               StructureComponent structureComponent = list4.remove(count);
               structureComponent.buildComponent(start, this.components, rand);
            } else {
               int count2 = rand.nextInt(list3.size());
               StructureComponent structureComponent2 = list3.remove(count2);
               structureComponent2.buildComponent(start, this.components, rand);
            }
         }

         this.updateBoundingBox();
         int i = 0;
         Iterator iterator2 = this.components.iterator();

         while (iterator2.hasNext()) {
            if (!((StructureComponent)iterator2.next() instanceof Road)) {
               i++;
            }
         }

         this.hasMoreThanTwoComponents = i > 2;
      }

      @Override
      public void writeToNBT(NBTTagCompound tagCompound) {
         super.writeToNBT(tagCompound);
         tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
      }
   }
}
