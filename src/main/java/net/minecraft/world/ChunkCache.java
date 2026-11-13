package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
   protected World worldObj;
   protected int chunkX;
   protected int chunkZ;
   protected Chunk[][] chunkArray;
   protected boolean hasExtendedLevels;

   public ChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
      this.worldObj = worldIn;
      this.chunkX = posFromIn.getX() - subIn >> 4;
      this.chunkZ = posFromIn.getZ() - subIn >> 4;
      int xCoordinate = posToIn.getX() + subIn >> 4;
      int zCoordinate = posToIn.getZ() + subIn >> 4;
      this.chunkArray = new Chunk[xCoordinate - this.chunkX + 1][zCoordinate - this.chunkZ + 1];
      this.hasExtendedLevels = true;

      for (int nestedIndex = this.chunkX; nestedIndex <= xCoordinate; nestedIndex++) {
         for (int outerIndex = this.chunkZ; outerIndex <= zCoordinate; outerIndex++) {
            this.chunkArray[nestedIndex - this.chunkX][outerIndex - this.chunkZ] = worldIn.getChunkFromChunkCoords(nestedIndex, outerIndex);
         }
      }

      for (int index = posFromIn.getX() >> 4; index <= posToIn.getX() >> 4; index++) {
         for (int innerIndex = posFromIn.getZ() >> 4; innerIndex <= posToIn.getZ() >> 4; innerIndex++) {
            Chunk chunk2 = this.chunkArray[index - this.chunkX][innerIndex - this.chunkZ];
            if (chunk2 != null && !chunk2.getAreLevelsEmpty(posFromIn.getY(), posToIn.getY())) {
               this.hasExtendedLevels = false;
            }
         }
      }
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int xCoordinate = (pos.getX() >> 4) - this.chunkX;
         int zCoordinate = (pos.getZ() >> 4) - this.chunkZ;
         if (xCoordinate >= 0 && xCoordinate < this.chunkArray.length && zCoordinate >= 0 && zCoordinate < this.chunkArray[xCoordinate].length) {
            Chunk chunk2 = this.chunkArray[xCoordinate][zCoordinate];
            if (chunk2 != null) {
               return chunk2.getBlockState(pos);
            }
         }
      }

      return Blocks.air.getDefaultState();
   }

   @Override
   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      IBlockState blockState = this.getBlockState(pos);
      return blockState.getBlock().getStrongPower(this, pos, blockState, direction);
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return this.hasExtendedLevels;
   }

   private int getLightForExt(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      if (enumSkyBlock == EnumSkyBlock.SKY && this.worldObj.provider.getHasNoSky()) {
         return 0;
      }

      if (pos.getY() >= 0 && pos.getY() < 256) {
         if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
            int i = 0;

            for (EnumFacing enumFacing : EnumFacing.values()) {
               int lightFor = this.getLightFor(enumSkyBlock, pos.offset(enumFacing));
               if (lightFor > i) {
                  i = lightFor;
               }

               if (i >= 15) {
                  return i;
               }
            }

            return i;
         } else {
            int i2 = (pos.getX() >> 4) - this.chunkX;
            int i3 = (pos.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[i2][i3].getLightFor(enumSkyBlock, pos);
         }
      } else {
         return enumSkyBlock.defaultLightValue;
      }
   }

   @Override
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int lightForExt = this.getLightForExt(EnumSkyBlock.SKY, pos);
      int lightForExt2 = this.getLightForExt(EnumSkyBlock.BLOCK, pos);
      if (lightForExt2 < lightValue) {
         lightForExt2 = lightValue;
      }

      return lightForExt << 20 | lightForExt2 << 4;
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
      return this.worldObj.getBiomeGenForCoords(pos);
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      int xCoordinate = (pos.getX() >> 4) - this.chunkX;
      int zCoordinate = (pos.getZ() >> 4) - this.chunkZ;
      return this.chunkArray[xCoordinate][zCoordinate].getTileEntity(pos, net.minecraft.world.chunk.Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   public int getLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int xCoordinate = (pos.getX() >> 4) - this.chunkX;
         int zCoordinate = (pos.getZ() >> 4) - this.chunkZ;
         return this.chunkArray[xCoordinate][zCoordinate].getLightFor(enumSkyBlock, pos);
      } else {
         return enumSkyBlock.defaultLightValue;
      }
   }

   @Override
   public WorldType getWorldType() {
      return this.worldObj.getWorldType();
   }
}
