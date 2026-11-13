package net.optifine;

import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

public interface IRandomEntity {
   int getId();

   String getName();

   BlockPos getSpawnPosition();

   int getHealth();

   BiomeGenBase getSpawnBiome();

   int getMaxHealth();
}
