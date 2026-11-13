package net.optifine.config;

public class RangeInt {
   private int min;
   private int max;

   public int getMin() {
      return this.min;
   }

   public RangeInt(int min, int max) {
      this.min = Math.min(min, max);
      this.max = Math.max(min, max);
   }

   @Override
   public String toString() {
      return "min: " + this.min + ", max: " + this.max;
   }

   public int getMax() {
      return this.max;
   }

   public boolean isInRange(int val) {
      return val < this.min ? false : val <= this.max;
   }
}
