package net.optifine.override;

import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.DynamicLights;
import net.optifine.util.ArrayCache;

public class ChunkCacheOF implements IBlockAccess {
   private static final ArrayCache cacheCombinedLights = new ArrayCache(int.class);
   private final boolean dynamicLights = Config.isDynamicLights();
   private IBlockState[] blockStates;
   private final ChunkCache chunkCache;
   private final int sizeX;
   private final int sizeY;
   private final int sizeZ;
   private final int sizeXY;
   private final int arraySize;
   private int[] combinedLights;
   private final int posX;
   private final int posY;
   private static final ArrayCache cacheBlockStates = new ArrayCache(IBlockState.class);
   private final int posZ;

   private int getPositionIndex(BlockPos pos) {
      int xCoordinate = pos.getX() - this.posX;
      if (xCoordinate >= 0 && xCoordinate < this.sizeX) {
         int yCoordinate = pos.getY() - this.posY;
         if (yCoordinate >= 0 && yCoordinate < this.sizeY) {
            int zCoordinate = pos.getZ() - this.posZ;
            return zCoordinate >= 0 && zCoordinate < this.sizeZ ? zCoordinate * this.sizeXY + yCoordinate * this.sizeX + xCoordinate : -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private int getCombinedLightRaw(BlockPos pos, int lightValue) {
      int combinedLight = this.chunkCache.getCombinedLight(pos, lightValue);
      if (this.dynamicLights && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
         combinedLight = DynamicLights.getCombinedLight(pos, combinedLight);
      }

      return combinedLight;
   }

   public void renderStart() {
      if (this.combinedLights == null) {
         this.combinedLights = (int[])cacheCombinedLights.allocate(this.arraySize);
      }

      Arrays.fill(this.combinedLights, -1);
      if (this.blockStates == null) {
         this.blockStates = (IBlockState[])cacheBlockStates.allocate(this.arraySize);
      }

      Arrays.fill(this.blockStates, null);
   }

   @Override
   public boolean isAirBlock(BlockPos pos) {
      return this.chunkCache.isAirBlock(pos);
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      int positionIndex = this.getPositionIndex(pos);
      if (positionIndex >= 0 && positionIndex < this.arraySize && this.blockStates != null) {
         IBlockState blockState = this.blockStates[positionIndex];
         if (blockState == null) {
            blockState = this.chunkCache.getBlockState(pos);
            this.blockStates[positionIndex] = blockState;
         }

         return blockState;
      } else {
         return this.chunkCache.getBlockState(pos);
      }
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return this.chunkCache.extendedLevelsInChunkCache();
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      return this.chunkCache.getTileEntity(pos);
   }

   public ChunkCacheOF(ChunkCache chunkCache2, BlockPos blockPos, BlockPos blockPos2) {
      this.chunkCache = chunkCache2;
      int i = blockPos.getX() - 1 >> 4;
      int i2 = blockPos.getY() - 1 >> 4;
      int i3 = blockPos.getZ() - 1 >> 4;
      int i4 = blockPos2.getX() + 1 >> 4;
      int i5 = blockPos2.getY() + 1 >> 4;
      int i6 = blockPos2.getZ() + 1 >> 4;
      this.sizeX = i4 - i + 1 << 4;
      this.sizeY = i5 - i2 + 1 << 4;
      this.sizeZ = i6 - i3 + 1 << 4;
      this.sizeXY = this.sizeX * this.sizeY;
      this.arraySize = this.sizeX * this.sizeY * this.sizeZ;
      this.posX = i << 4;
      this.posY = i2 << 4;
      this.posZ = i3 << 4;
   }

   public void renderFinish() {
      cacheCombinedLights.free(this.combinedLights);
      this.combinedLights = null;
      cacheBlockStates.free(this.blockStates);
      this.blockStates = null;
   }

   @Override
   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return this.chunkCache.getStrongPower(pos, direction);
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int positionIndex = this.getPositionIndex(pos);
      if (positionIndex >= 0 && positionIndex < this.arraySize && this.combinedLights != null) {
         int i = this.combinedLights[positionIndex];
         if (i == -1) {
            i = this.getCombinedLightRaw(pos, lightValue);
            this.combinedLights[positionIndex] = i;
         }

         return i;
      } else {
         return this.getCombinedLightRaw(pos, lightValue);
      }
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
      return this.chunkCache.getBiomeGenForCoords(pos);
   }

   @Override
   public WorldType getWorldType() {
      return this.chunkCache.getWorldType();
   }
}
