package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase {
   private WorldGenIceSpike iceSpikeGen = new WorldGenIceSpike();
   private WorldGenIcePath icePathGen = new WorldGenIcePath();
   private boolean spikes;

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return new WorldGenTaiga2(false);
   }

   public BiomeGenSnow(int id, boolean spikes) {
      super(id);
      this.spikes = spikes;
      if (spikes) {
         this.topBlock = Blocks.snow.getDefaultState();
      }

      this.spawnableCreatureList.clear();
   }

   @Override
   protected BiomeGenBase createMutatedBiome(int id) {
      BiomeGenBase biomeGenBase = new BiomeGenSnow(id, true)
         .setBiomeColor(13828095, true)
         .setBiomeName(this.biomeName + " Spikes")
         .setEnableSnow()
         .setTemperatureRainfall(0.0F, 0.5F)
         .setHeight(new BiomeGenBase.Height(this.minHeight + 0.1F, this.maxHeight + 0.1F));
      biomeGenBase.minHeight = this.minHeight + 0.3F;
      biomeGenBase.maxHeight = this.maxHeight + 0.4F;
      return biomeGenBase;
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.spikes) {
         for (int index = 0; index < 3; index++) {
            int i = rand.nextInt(16) + 8;
            int i2 = rand.nextInt(16) + 8;
            this.iceSpikeGen.generate(worldIn, rand, worldIn.getHeight(pos.add(i, 0, i2)));
         }

         for (int outerIndex = 0; outerIndex < 2; outerIndex++) {
            int i3 = rand.nextInt(16) + 8;
            int i4 = rand.nextInt(16) + 8;
            this.icePathGen.generate(worldIn, rand, worldIn.getHeight(pos.add(i3, 0, i4)));
         }
      }

      super.decorate(worldIn, rand, pos);
   }
}
