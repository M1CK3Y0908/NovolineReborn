package net.optifine.config;

import net.minecraft.src.Config;

public class RangeListInt {
   private RangeInt[] ranges = new RangeInt[0];

   public RangeListInt(RangeInt ri) {
      this.addRange(ri);
   }

   public void addRange(RangeInt ri) {
      this.ranges = (RangeInt[])Config.addObjectToArray(this.ranges, ri);
   }

   public boolean isInRange(int val) {
      for (int i = 0; i < this.ranges.length; i++) {
         if (this.ranges[i].isInRange(val)) {
            return true;
         }
      }

      return false;
   }

   public int getCountRanges() {
      return this.ranges.length;
   }

   public RangeListInt() {
   }

   @Override
   public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");

      for (int index = 0; index < this.ranges.length; index++) {
         RangeInt rangeInt = this.ranges[index];
         if (index > 0) {
            stringBuffer.append(", ");
         }

         stringBuffer.append(rangeInt.toString());
      }

      stringBuffer.append("]");
      return stringBuffer.toString();
   }

   public RangeInt getRangeInt() {
      return this.ranges[0];
   }
}
