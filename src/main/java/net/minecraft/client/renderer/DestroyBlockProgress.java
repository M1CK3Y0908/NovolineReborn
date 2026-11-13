package net.minecraft.client.renderer;

import net.minecraft.util.BlockPos;

public class DestroyBlockProgress {
   private final int miningPlayerEntId;
   private int createdAtCloudUpdateTick;
   private int partialBlockProgress;
   private final BlockPos position;

   public void setPartialBlockDamage(int damage) {
      if (damage > 10) {
         damage = 10;
      }

      this.partialBlockProgress = damage;
   }

   public int getCreationCloudUpdateTick() {
      return this.createdAtCloudUpdateTick;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public DestroyBlockProgress(int miningPlayerEntIdIn, BlockPos positionIn) {
      this.miningPlayerEntId = miningPlayerEntIdIn;
      this.position = positionIn;
   }

   public int getPartialBlockDamage() {
      return this.partialBlockProgress;
   }

   public void setCloudUpdateTick(int createdAtCloudUpdateTickIn) {
      this.createdAtCloudUpdateTick = createdAtCloudUpdateTickIn;
   }
}
