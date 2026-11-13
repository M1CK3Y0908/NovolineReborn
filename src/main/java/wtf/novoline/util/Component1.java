package wtf.novoline.util;

import java.util.ArrayList;

public class Component1 {
   private ArrayList arrayList = null;
   private int intValue = 0;
   private float floatValue = 1.0F;
   private int intValue2 = 0;

   public void update() {
      this.arrayList.clear();
      this.intValue2 = 0;
   }

   public int getInt() {
      return this.arrayList.size();
   }

   public boolean isActive() {
      return this.arrayList.isEmpty();
   }

   public Component1(int i, float f) {
      this.arrayList = new ArrayList(i);
      this.intValue = 10;
      this.floatValue = f;
   }

   public boolean canEqual(Object object) {
      return this.arrayList.contains(object);
   }

   public Component1() {
      this(10, 0.75F);
   }

   public int getIntValue2() {
      return this.intValue2;
   }
}
