package net.minecraft.entity.ai;

public abstract class EntityAIBase {
   private int mutexBits;

   public boolean isInterruptible() {
      return true;
   }

   public boolean continueExecuting() {
      return this.shouldExecute();
   }

   public void resetTask() {
   }

   public abstract boolean shouldExecute();

   public int getMutexBits() {
      return this.mutexBits;
   }

   public void startExecuting() {
   }

   public void setMutexBits(int mutexBitsIn) {
      this.mutexBits = mutexBitsIn;
   }

   public void updateTask() {
   }
}
