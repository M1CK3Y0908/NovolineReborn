package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;
import net.optifine.reflect.Reflector;

public class ExtendedBlockStorage {
   private int lightRefCount;
   private char[] data;
   private int tickRefCount;
   private int blockRefCount;
   private NibbleArray blocklightArray;
   private NibbleArray skylightArray;

   public void setBlocklightArray(NibbleArray array) {
      this.blocklightArray = array;
   }

   public int getYLocation() {
      return this.lightRefCount;
   }

   public void set(int x, int y, int z, IBlockState state) {
      if (Reflector.reflectorClass32.checkObject(state)) {
         state = (IBlockState)Reflector.createObject4(state, Reflector.reflectorMethod93);
      }

      Block block2 = this.get(x, y, z).getBlock();
      Block block3 = state.getBlock();
      if (block2 != Blocks.air) {
         this.blockRefCount--;
         if (block2.getTickRandomly()) {
            this.tickRefCount--;
         }
      }

      if (block3 != Blocks.air) {
         this.blockRefCount++;
         if (block3.getTickRandomly()) {
            this.tickRefCount++;
         }
      }

      this.data[y << 8 | z << 4 | x] = (char)Block.BLOCK_STATE_IDS.get(state);
   }

   public Block getBlockByExtId(int x, int y, int z) {
      return this.get(x, y, z).getBlock();
   }

   public int getExtBlockMetadata(int x, int y, int z) {
      IBlockState blockState = this.get(x, y, z);
      return blockState.getBlock().getMetaFromState(blockState);
   }

   public void setExtSkylightValue(int x, int y, int z, int value) {
      this.skylightArray.set(x, y, z, value);
   }

   public int getBlockRefCount() {
      return this.blockRefCount;
   }

   public NibbleArray getBlocklightArray() {
      return this.blocklightArray;
   }

   public char[] getData() {
      return this.data;
   }

   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public void setExtBlocklightValue(int x, int y, int z, int value) {
      this.blocklightArray.set(x, y, z, value);
   }

   public void removeInvalidBlocks() {
      Blocks.air.getDefaultState();
      int i = 0;
      int i2 = 0;

      for (int i3 = 0; i3 < 16; i3++) {
         for (int i4 = 0; i4 < 16; i4++) {
            for (int i5 = 0; i5 < 16; i5++) {
               Block blockByExtId = this.getBlockByExtId(i5, i3, i4);
               if (blockByExtId != Blocks.air) {
                  i++;
                  if (blockByExtId.getTickRandomly()) {
                     i2++;
                  }
               }
            }
         }
      }

      this.blockRefCount = i;
      this.tickRefCount = i2;
   }

   public int getExtBlocklightValue(int x, int y, int z) {
      return this.blocklightArray.get(x, y, z);
   }

   public boolean getNeedsRandomTick() {
      return this.tickRefCount > 0;
   }

   public void setSkylightArray(NibbleArray array) {
      this.skylightArray = array;
   }

   public void setData(char[] dataArray) {
      this.data = dataArray;
   }

   public int getExtSkylightValue(int x, int y, int z) {
      return this.skylightArray.get(x, y, z);
   }

   public ExtendedBlockStorage(int y, boolean storeSkylight) {
      this.lightRefCount = y;
      this.data = new char[4096];
      this.blocklightArray = new NibbleArray();
      if (storeSkylight) {
         this.skylightArray = new NibbleArray();
      }
   }

   public NibbleArray getSkylightArray() {
      return this.skylightArray;
   }

   public IBlockState get(int x, int y, int z) {
      IBlockState blockState = Block.BLOCK_STATE_IDS.getByValue(this.data[y << 8 | z << 4 | x]);
      return blockState != null ? blockState : Blocks.air.getDefaultState();
   }
}
