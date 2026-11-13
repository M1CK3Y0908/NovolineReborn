package net.minecraft.util;

public class MouseFilter {
   private float smoothedDelta;
   private float accumulatedDelta;
   private float lastAmount;

   public float smooth(float delta, float smoothing) {
      this.accumulatedDelta += delta;
      delta = (this.accumulatedDelta - this.smoothedDelta) * smoothing;
      this.lastAmount = this.lastAmount + (delta - this.lastAmount) * 0.5F;
      if (delta > 0.0F && delta > this.lastAmount || delta < 0.0F && delta < this.lastAmount) {
         delta = this.lastAmount;
      }

      this.smoothedDelta += delta;
      return delta;
   }

   public void reset() {
      this.accumulatedDelta = 0.0F;
      this.smoothedDelta = 0.0F;
      this.lastAmount = 0.0F;
   }
}
