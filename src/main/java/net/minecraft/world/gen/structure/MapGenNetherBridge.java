package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;

public class MapGenNetherBridge extends MapGenStructure {
   private List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> spawnList = Lists.newArrayList();

   public MapGenNetherBridge() {
      this.spawnList.add(new net.minecraft.world.biome.BiomeGenBase.SpawnListEntry(EntityBlaze.class, 10, 2, 3));
      this.spawnList.add(new net.minecraft.world.biome.BiomeGenBase.SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
      this.spawnList.add(new net.minecraft.world.biome.BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
      this.spawnList.add(new net.minecraft.world.biome.BiomeGenBase.SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
   }

   @Override
   public String getStructureName() {
      return "Fortress";
   }

   @Override
   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenNetherBridge.Start(this.worldObj, this.rand, chunkX, chunkZ);
   }

   @Override
   protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int i = chunkX >> 4;
      int i2 = chunkZ >> 4;
      this.rand.setSeed(i ^ i2 << 4 ^ this.worldObj.getSeed());
      this.rand.nextInt();
      return this.rand.nextInt(3) != 0
         ? false
         : (chunkX != (i << 4) + 4 + this.rand.nextInt(8) ? false : chunkZ == (i2 << 4) + 4 + this.rand.nextInt(8));
   }

   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getSpawnList() {
      return this.spawnList;
   }

   public static class Start extends StructureStart {
      public Start(World worldIn, Random random, int i, int i2) {
         super(i, i2);
         StructureNetherBridgePieces.Start start = new StructureNetherBridgePieces.Start(random, (i << 4) + 2, (i2 << 4) + 2);
         this.components.add(start);
         start.buildComponent(start, this.components, random);
         List pendingChildren2 = start.pendingChildren;

         while (!pendingChildren2.isEmpty()) {
            int i3 = random.nextInt(pendingChildren2.size());
            ((StructureComponent)pendingChildren2.remove(i3)).buildComponent(start, this.components, random);
         }

         this.updateBoundingBox();
         this.handleWorld2(worldIn, random);
      }

      public Start() {
      }
   }
}
