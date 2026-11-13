package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDebug implements IChunkProvider {
   private final World world;
   private static final List<IBlockState> blockStates = Lists.newArrayList();
   private static final int gridWidth;
   private static final int gridHeight;

   static {
      for (Block block2 : Block.blockRegistry) {
         blockStates.addAll(block2.getBlockState().getValidStates());
      }

      gridWidth = MathHelper.ceiling_float_int(MathHelper.sqrt_float(blockStates.size()));
      gridHeight = MathHelper.ceiling_float_int((float)blockStates.size() / gridWidth);
   }

   public ChunkProviderDebug(World worldIn) {
      this.world = worldIn;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
   }

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.world.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void saveExtraData() {
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   public static IBlockState getDebugBlockState(int x, int z) {
      IBlockState blockState = null;
      if (x > 0 && z > 0 && x % 2 != 0 && z % 2 != 0) {
         x /= 2;
         z /= 2;
         if (x <= gridWidth && z <= gridHeight) {
            int i = MathHelper.abs_int(x * gridWidth + z);
            if (i < blockStates.size()) {
               blockState = blockStates.get(i);
            }
         }
      }

      return blockState;
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      ChunkPrimer chunkPrimer = new ChunkPrimer();

      for (int index = 0; index < 16; index++) {
         for (int innerIndex = 0; innerIndex < 16; innerIndex++) {
            int i = x * 16 + index;
            int i2 = z * 16 + innerIndex;
            chunkPrimer.setBlockState(index, 60, innerIndex, Blocks.barrier.getDefaultState());
            IBlockState blockState = getDebugBlockState(i, i2);
            if (blockState != null) {
               chunkPrimer.setBlockState(index, 70, innerIndex, blockState);
            }
         }
      }

      Chunk chunk2 = new Chunk(this.world, chunkPrimer, x, z);
      chunk2.generateSkylightMap();
      BiomeGenBase[] abiomegenbase = this.world.getWorldChunkManager().getArray3((BiomeGenBase[])null, x * 16, z * 16);
      byte[] byteArray = chunk2.getBiomeArray();

      for (int index2 = 0; index2 < byteArray.length; index2++) {
         byteArray[index2] = (byte)abiomegenbase[index2].biomeID;
      }

      chunk2.generateSkylightMap();
      return chunk2;
   }

   @Override
   public String makeString() {
      return "DebugLevelSource";
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate) {
      return true;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }
}
