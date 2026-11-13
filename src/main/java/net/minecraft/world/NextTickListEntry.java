package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class NextTickListEntry implements Comparable<NextTickListEntry> {
   public int priority;
   private static long nextTickEntryID;
   public long scheduledTime;
   private long tickEntryID;
   public final BlockPos position;
   private final Block block;

   public int compareTo(NextTickListEntry other) {
      return this.scheduledTime < other.scheduledTime
         ? -1
         : (
            this.scheduledTime > other.scheduledTime
               ? 1
               : (
                  this.priority != other.priority
                     ? this.priority - other.priority
                     : (this.tickEntryID < other.tickEntryID ? -1 : (this.tickEntryID > other.tickEntryID ? 1 : 0))
               )
         );
   }

   @Override
   public boolean equals(Object other) {
      return !(other instanceof NextTickListEntry nextTickListEntry)
         ? false
         : this.position.equals(nextTickListEntry.position) && Block.isEqualTo(this.block, nextTickListEntry.block);
   }

   public NextTickListEntry setScheduledTime(long scheduledTimeIn) {
      this.scheduledTime = scheduledTimeIn;
      return this;
   }

   public Block getBlock() {
      return this.block;
   }

   @Override
   public String toString() {
      int idFromBlock = Block.getIdFromBlock(this.block);
      long tickEntryID2 = this.tickEntryID;
      int priority2 = this.priority;
      long scheduledTime2 = this.scheduledTime;
      return idFromBlock + ": " + this.position + ", " + scheduledTime2 + ", " + priority2 + ", " + tickEntryID2;
   }

   @Override
   public int hashCode() {
      return this.position.hashCode();
   }

   public NextTickListEntry(BlockPos positionIn, Block blockIn) {
      this.tickEntryID = nextTickEntryID++;
      this.position = positionIn;
      this.block = blockIn;
   }

   public void setPriority(int priorityIn) {
      this.priority = priorityIn;
   }
}
