package net.minecraft.client.renderer;

import java.util.ArrayDeque;
import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.optifine.DynamicLights;

public class RegionRenderCache extends ChunkCache {
   private static final IBlockState DEFAULT_STATE = Blocks.air.getDefaultState();
   private final BlockPos position;
   private static ArrayDeque<int[]> cacheLights = new ArrayDeque<>();
   private int[] combinedLights;
   private static ArrayDeque<IBlockState[]> cacheStates = new ArrayDeque<>();
   private static int maxCacheSize = Config.limit(Runtime.getRuntime().availableProcessors(), 1, 32);
   private IBlockState[] blockStates;

   public RegionRenderCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
      super(worldIn, posFromIn, posToIn, subIn);
      this.position = posFromIn.subtract(new Vec3i(subIn, subIn, subIn));
      this.combinedLights = getArray();
      Arrays.fill(this.combinedLights, -1);
      this.blockStates = getArray2();
   }

   private static IBlockState[] getArray2() {
      synchronized (cacheStates) {
         IBlockState[] iBlockStates = cacheStates.pollLast();
         if (iBlockStates != null && iBlockStates.length >= 8000) {
            Arrays.fill(iBlockStates, null);
         } else {
            iBlockStates = new IBlockState[8000];
         }

         return iBlockStates;
      }
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      int positionIndex = this.getPositionIndex(pos);
      IBlockState blockState = this.blockStates[positionIndex];
      if (blockState == null) {
         blockState = this.getBlockStateRaw(pos);
         this.blockStates[positionIndex] = blockState;
      }

      return blockState;
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int positionIndex = this.getPositionIndex(pos);
      int i = this.combinedLights[positionIndex];
      if (i == -1) {
         i = super.getCombinedLight(pos, lightValue);
         if (Config.isDynamicLights() && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
            i = DynamicLights.getCombinedLight(pos, i);
         }

         this.combinedLights[positionIndex] = i;
      }

      return i;
   }

   private static int[] getArray() {
      synchronized (cacheLights) {
         int[] iArray = cacheLights.pollLast();
         if (iArray == null || iArray.length < 8000) {
            iArray = new int[8000];
         }

         return iArray;
      }
   }

   private IBlockState getBlockStateRaw(BlockPos pos) {
      return super.getBlockState(pos);
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      int xCoordinate = (pos.getX() >> 4) - this.chunkX;
      int zCoordinate = (pos.getZ() >> 4) - this.chunkZ;
      return this.chunkArray[xCoordinate][zCoordinate].getTileEntity(pos, net.minecraft.world.chunk.Chunk.EnumCreateEntityType.QUEUED);
   }

   private int getPositionIndex(BlockPos blockPos) {
      int xCoordinate = blockPos.getX() - this.position.getX();
      int yCoordinate = blockPos.getY() - this.position.getY();
      int zCoordinate = blockPos.getZ() - this.position.getZ();
      return xCoordinate * 400 + zCoordinate * 20 + yCoordinate;
   }
}
