package net.minecraft.block;

import net.minecraft.util.BlockPos;

public class BlockEventData {
   private BlockPos position;
   private int eventID;
   private int eventParameter;
   private Block blockType;

   public int getEventParameter() {
      return this.eventParameter;
   }

   public Block getBlock() {
      return this.blockType;
   }

   public int getEventID() {
      return this.eventID;
   }

   @Override
   public String toString() {
      return "TE(" + this.position + ")," + this.eventID + "," + this.eventParameter + "," + this.blockType;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public BlockEventData(BlockPos pos, Block blockType, int eventId, int i) {
      this.position = pos;
      this.eventID = eventId;
      this.eventParameter = i;
      this.blockType = blockType;
   }

   @Override
   public boolean equals(Object arg1) {
      return !(arg1 instanceof BlockEventData blockEventData)
         ? false
         : this.position.equals(blockEventData.position)
            && this.eventID == blockEventData.eventID
            && this.eventParameter == blockEventData.eventParameter
            && this.blockType == blockEventData.blockType;
   }
}
