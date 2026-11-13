package net.optifine.config;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.src.Config;

public class MatchBlock {
   private int blockId = -1;
   private int[] metadatas = null;

   public int[] getMetadatas() {
      return this.metadatas;
   }

   public boolean matches(int id, int metadata) {
      return id != this.blockId ? false : Matches.metadata(metadata, this.metadatas);
   }

   public int getBlockId() {
      return this.blockId;
   }

   public MatchBlock(int blockId, int[] metadatas) {
      this.blockId = blockId;
      this.metadatas = metadatas;
   }

   public boolean matches(BlockStateBase blockState) {
      return blockState.getBlockId() != this.blockId ? false : Matches.metadata(blockState.getMetadata(), this.metadatas);
   }

   public MatchBlock(int blockId) {
      this.blockId = blockId;
   }

   @Override
   public String toString() {
      return this.blockId + ":" + Config.arrayToString(this.metadatas);
   }
}
