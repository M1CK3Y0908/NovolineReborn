package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenStronghold extends MapGenStructure {
   private ChunkCoordIntPair[] structureCoords = new ChunkCoordIntPair[3];
   private boolean ranBiomeCheck;
   private double distance = 32.0;
   private int spread = 3;
   private List<BiomeGenBase> allowedBiomes = Lists.newArrayList();

   public MapGenStronghold(Map<String, String> settings) {
      this();

      for (Entry entry : settings.entrySet()) {
         if (((String)entry.getKey()).equals("distance")) {
            this.distance = MathHelper.parseDoubleWithDefaultAndMax((String)entry.getValue(), this.distance, 1.0);
         } else if (((String)entry.getKey()).equals("count")) {
            this.structureCoords = new ChunkCoordIntPair[MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.structureCoords.length, 1)];
         } else if (((String)entry.getKey()).equals("spread")) {
            this.spread = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.spread, 1);
         }
      }
   }

   @Override
   protected List<BlockPos> getCoordList() {
      ArrayList arrayList = Lists.newArrayList();

      for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
         if (chunkCoordIntPair != null) {
            arrayList.add(chunkCoordIntPair.getBlockPos());
         }
      }

      return arrayList;
   }

   public MapGenStronghold() {
      for (BiomeGenBase biomeGenBase : BiomeGenBase.getBiomeGenArray()) {
         if (biomeGenBase != null && biomeGenBase.minHeight > 0.0F) {
            this.allowedBiomes.add(biomeGenBase);
         }
      }
   }

   @Override
   protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      if (!this.ranBiomeCheck) {
         Random random2 = new Random();
         random2.setSeed(this.worldObj.getSeed());
         double d = random2.nextDouble() * Math.PI * 2.0;
         int i = 1;

         for (int i2 = 0; i2 < this.structureCoords.length; i2++) {
            double d2 = (1.25 * i + random2.nextDouble()) * this.distance * i;
            int i3 = (int)Math.round(Math.cos(d) * d2);
            int i4 = (int)Math.round(Math.sin(d) * d2);
            BlockPos biomePosition = this.worldObj.getWorldChunkManager().findBiomePosition((i3 << 4) + 8, (i4 << 4) + 8, 112, this.allowedBiomes, random2);
            if (biomePosition != null) {
               i3 = biomePosition.getX() >> 4;
               i4 = biomePosition.getZ() >> 4;
            }

            this.structureCoords[i2] = new ChunkCoordIntPair(i3, i4);
            d += (Math.PI * 2) * i / this.spread;
            if (i2 == this.spread) {
               i += 2 + random2.nextInt(5);
               this.spread = this.spread + 1 + random2.nextInt(2);
            }
         }

         this.ranBiomeCheck = true;
      }

      for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
         if (chunkX == chunkCoordIntPair.chunkXPos && chunkZ == chunkCoordIntPair.chunkZPos) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String getStructureName() {
      return "Stronghold";
   }

   @Override
   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      MapGenStronghold.Start start = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ);

      while (start.getComponents().isEmpty() || ((StructureStrongholdPieces.Stairs2)start.getComponents().get(0)).strongholdPortalRoom == null) {
         start = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ);
      }

      return start;
   }

   public static class Start extends StructureStart {
      public Start(World worldIn, Random random, int chunkX, int chunkZ) {
         super(chunkX, chunkZ);
         StructureStrongholdPieces.prepareStructurePieces();
         StructureStrongholdPieces.Stairs2 stairs2 = new StructureStrongholdPieces.Stairs2(random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
         this.components.add(stairs2);
         stairs2.buildComponent(stairs2, this.components, random);
         List pendingChildren2 = stairs2.pendingChildren;

         while (!pendingChildren2.isEmpty()) {
            int i = random.nextInt(pendingChildren2.size());
            ((StructureComponent)pendingChildren2.remove(i)).buildComponent(stairs2, this.components, random);
         }

         this.updateBoundingBox();
         this.handleWorld(worldIn, random);
      }

      public Start() {
      }
   }
}
