package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.DesertPyramid;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.JunglePyramid;

public class MapGenScatteredFeature extends MapGenStructure {
   private List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> scatteredFeatureSpawnList = Lists.newArrayList();
   private static final List<BiomeGenBase> biomelist = Arrays.asList(
      BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland
   );
   private int maxDistanceBetweenScatteredFeatures = 32;
   private int minDistanceBetweenScatteredFeatures = 8;

   @Override
   public String getStructureName() {
      return "Temple";
   }

   @Override
   protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int chunkX2 = chunkX;
      int chunkZ2 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      int i = chunkX / this.maxDistanceBetweenScatteredFeatures;
      int i2 = chunkZ / this.maxDistanceBetweenScatteredFeatures;
      Random random = this.worldObj.setRandomSeed(i, i2, 14357617);
      i *= this.maxDistanceBetweenScatteredFeatures;
      i2 *= this.maxDistanceBetweenScatteredFeatures;
      i += random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      i2 += random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      if (chunkX2 == i && chunkZ2 == i2) {
         BiomeGenBase biomeGenerator = this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(chunkX2 * 16 + 8, 0, chunkZ2 * 16 + 8));
         if (biomeGenerator == null) {
            return false;
         }

         for (BiomeGenBase biomeGenBase2 : biomelist) {
            if (biomeGenerator == biomeGenBase2) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isSwampHut(BlockPos pos) {
      StructureStart structureAt = this.getStructureAt(pos);
      return structureAt != null && structureAt instanceof MapGenScatteredFeature.Start && !structureAt.components.isEmpty()
         ? structureAt.components.getFirst() instanceof ComponentScatteredFeaturePieces.SwampHut
         : false;
   }

   public MapGenScatteredFeature(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("distance")) {
            this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax(
               (String)entry.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1
            );
         }
      }
   }

   @Override
   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenScatteredFeature.Start(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList() {
      return this.scatteredFeatureSpawnList;
   }

   public MapGenScatteredFeature() {
      this.scatteredFeatureSpawnList.add(new net.minecraft.world.biome.BiomeGenBase.SpawnListEntry(EntityWitch.class, 1, 1, 1));
   }

   public static class Start extends StructureStart {
      public Start(World worldIn, Random random, int i, int i2) {
         super(i, i2);
         BiomeGenBase biomeGenBase = worldIn.getBiomeGenForCoords(new BlockPos(i * 16 + 8, 0, i2 * 16 + 8));
         if (biomeGenBase == BiomeGenBase.jungle || biomeGenBase == BiomeGenBase.jungleHills) {
            JunglePyramid junglePyramid = new JunglePyramid(random, i * 16, i2 * 16);
            this.components.add(junglePyramid);
         } else if (biomeGenBase == BiomeGenBase.swampland) {
            ComponentScatteredFeaturePieces.SwampHut swampHut = new ComponentScatteredFeaturePieces.SwampHut(random, i * 16, i2 * 16);
            this.components.add(swampHut);
         } else if (biomeGenBase == BiomeGenBase.desert || biomeGenBase == BiomeGenBase.desertHills) {
            DesertPyramid desertPyramid = new DesertPyramid(random, i * 16, i2 * 16);
            this.components.add(desertPyramid);
         }

         this.updateBoundingBox();
      }

      public Start() {
      }
   }
}
