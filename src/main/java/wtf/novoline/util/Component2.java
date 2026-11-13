package wtf.novoline.util;

public class Component2 {
   private int intValue;
   private int intValue2;
   private int[] intArray;

   public int[] getIntArray() {
      return this.intArray;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void update() {
      this.intValue2 = 0;
      this.intValue = 0;
   }

   public int getIntValue2() {
      return this.intValue2;
   }
}
