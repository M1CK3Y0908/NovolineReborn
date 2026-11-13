package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper {
   private static final BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getGrassColorAtPos(blockPosition);
      }
   };
   private static final BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getFoliageColorAtPos(blockPosition);
      }
   };
   private static final BiomeColorHelper.ColorResolver WATER_COLOR_MULTIPLIER = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.waterColorMultiplier;
      }
   };

   public static int getWaterColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
      return getColorAtPos(blockAccess, pos, WATER_COLOR_MULTIPLIER);
   }

   public static int getFoliageColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
      return getColorAtPos(blockAccess, pos, FOLIAGE_COLOR);
   }

   private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver) {
      int i = 0;
      int i2 = 0;
      int i3 = 0;

      for (net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
         int colorAtPos = colorResolver.getColorAtPos(blockAccess.getBiomeGenForCoords(mutableBlockPos), mutableBlockPos);
         i += (colorAtPos & 0xFF0000) >> 16;
         i2 += (colorAtPos & 0xFF00) >> 8;
         i3 += colorAtPos & 0xFF;
      }

      return (i / 9 & 0xFF) << 16 | (i2 / 9 & 0xFF) << 8 | i3 / 9 & 0xFF;
   }

   public static int getGrassColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
      return getColorAtPos(blockAccess, pos, GRASS_COLOR);
   }

   interface ColorResolver {
      int getColorAtPos(BiomeGenBase biomeGenBase, BlockPos blockPos);
   }
}
