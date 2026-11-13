package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase {
   protected World worldObj;
   protected int range = 8;
   protected Random rand = new Random();

   protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalChunkX, int originalChunkZ, ChunkPrimer chunkPrimerIn) {
   }

   public void generate(IChunkProvider chunkProviderIn, World worldIn, int x, int z, ChunkPrimer chunkPrimerIn) {
      int range2 = this.range;
      this.worldObj = worldIn;
      this.rand.setSeed(worldIn.getSeed());
      long l = this.rand.nextLong();
      long l2 = this.rand.nextLong();

      for (int outerIndex = x - range2; outerIndex <= x + range2; outerIndex++) {
         for (int index = z - range2; index <= z + range2; index++) {
            long l3 = outerIndex * l;
            long l4 = index * l2;
            this.rand.setSeed(l3 ^ l4 ^ worldIn.getSeed());
            this.recursiveGenerate(worldIn, outerIndex, index, x, z, chunkPrimerIn);
         }
      }
   }
}
