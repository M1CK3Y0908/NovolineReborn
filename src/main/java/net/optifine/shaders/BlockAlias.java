package net.optifine.shaders;

import java.util.ArrayList;
import java.util.HashSet;
import net.minecraft.src.Config;
import net.optifine.config.MatchBlock;

public class BlockAlias {
   private int blockAliasId;
   private MatchBlock[] matchBlocks;

   @Override
   public String toString() {
      return "block." + this.blockAliasId + "=" + Config.arrayToString(this.matchBlocks);
   }

   public boolean matches(int id, int metadata) {
      for (int i = 0; i < this.matchBlocks.length; i++) {
         if (this.matchBlocks[i].matches(id, metadata)) {
            return true;
         }
      }

      return false;
   }

   public int getBlockAliasId() {
      return this.blockAliasId;
   }

   public BlockAlias(int blockAliasId, MatchBlock[] matchBlocks) {
      this.blockAliasId = blockAliasId;
      this.matchBlocks = matchBlocks;
   }

   public int[] getMatchBlockIds() {
      HashSet hashSet = new HashSet();

      for (int i = 0; i < this.matchBlocks.length; i++) {
         int blockId = this.matchBlocks[i].getBlockId();
         hashSet.add(blockId);
      }

      Integer[] integers = (Integer[]) hashSet.toArray(new Integer[hashSet.size()]);
      return Config.toPrimitive(integers);
   }

   public MatchBlock[] getMatchBlocks(int matchBlockId) {
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < this.matchBlocks.length; index++) {
         MatchBlock matchBlock = this.matchBlocks[index];
         if (matchBlock.getBlockId() == matchBlockId) {
            arrayList.add(matchBlock);
         }
      }

      return (MatchBlock[]) arrayList.toArray(new MatchBlock[arrayList.size()]);
   }
}
