package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;

public interface IBlockAccess {
   TileEntity getTileEntity(BlockPos blockPos);

   int getCombinedLight(BlockPos blockPos, int i);

   IBlockState getBlockState(BlockPos blockPos);

   boolean isAirBlock(BlockPos blockPos);

   int getStrongPower(BlockPos blockPos, EnumFacing enumFacing);

   BiomeGenBase getBiomeGenForCoords(BlockPos blockPos);

   boolean extendedLevelsInChunkCache();

   WorldType getWorldType();
}
