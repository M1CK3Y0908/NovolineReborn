package net.minecraft.village;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class VillageDoorInfo {
   private final BlockPos doorBlockPos;
   private int doorOpeningRestrictionCounter;
   private final EnumFacing insideDirection;
   private final BlockPos insideBlock;
   private boolean isDetachedFromVillageFlag;
   private int lastActivityTimestamp;

   public int getDistanceToInsideBlockSq(BlockPos pos) {
      return (int)this.insideBlock.distanceSq(pos);
   }

   public int getDistanceSquared(int x, int y, int z) {
      return (int)this.doorBlockPos.distanceSq(x, y, z);
   }

   private static EnumFacing getFaceDirection(int deltaX, int deltaZ) {
      return deltaX < 0 ? EnumFacing.WEST : (deltaX > 0 ? EnumFacing.EAST : (deltaZ < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH));
   }

   public void incrementDoorOpeningRestrictionCounter() {
      this.doorOpeningRestrictionCounter++;
   }

   public void update() {
      this.isDetachedFromVillageFlag = true;
   }

   public int getInsideOffsetX() {
      return this.insideDirection.getFrontOffsetX() * 2;
   }

   public int getInsideOffsetZ() {
      return this.insideDirection.getFrontOffsetZ() * 2;
   }

   public int getInsidePosY() {
      return this.lastActivityTimestamp;
   }

   public BlockPos getDoorBlockPos() {
      return this.doorBlockPos;
   }

   public void resetDoorOpeningRestrictionCounter() {
      this.doorOpeningRestrictionCounter = 0;
   }

   public int getDoorOpeningRestrictionCounter() {
      return this.doorOpeningRestrictionCounter;
   }

   public boolean isInsideSide(BlockPos pos) {
      int xCoordinate = pos.getX() - this.doorBlockPos.getX();
      int yCoordinate = pos.getZ() - this.doorBlockPos.getY();
      return xCoordinate * this.insideDirection.getFrontOffsetX() + yCoordinate * this.insideDirection.getFrontOffsetZ() >= 0;
   }

   public VillageDoorInfo(BlockPos pos, int i, int i2, int i3) {
      this(pos, getFaceDirection(i, i2), i3);
   }

   public boolean getIsDetachedFromVillageFlag() {
      return this.isDetachedFromVillageFlag;
   }

   public int getDistanceToDoorBlockSq(BlockPos pos) {
      return (int)pos.distanceSq(this.getDoorBlockPos());
   }

   public void setLastActivityTimestamp(int timestamp) {
      this.lastActivityTimestamp = timestamp;
   }

   public BlockPos getInsideBlockPos() {
      return this.insideBlock;
   }

   public VillageDoorInfo(BlockPos pos, EnumFacing facing, int timestamp) {
      this.doorBlockPos = pos;
      this.insideDirection = facing;
      this.insideBlock = pos.offset(facing, 2);
      this.lastActivityTimestamp = timestamp;
   }
}
