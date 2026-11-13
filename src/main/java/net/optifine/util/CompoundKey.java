package net.optifine.util;

import net.minecraft.src.Config;

public class CompoundKey {
   private int intValue = 0;
   private Object[] objectArray;

   public CompoundKey(Object object, Object object2, Object object3) {
      this(new Object[]{object, object2, object3});
   }

   public Object[] getArray() {
      return (Object[])this.objectArray.clone();
   }

   private static boolean checkObject(Object object, Object object2) {
      return object == object2 ? true : (object == null ? false : (object2 == null ? false : object.equals(object2)));
   }

   @Override
   public String toString() {
      return "[" + Config.arrayToString(this.objectArray) + "]";
   }

   public CompoundKey(Object[] objects) {
      this.objectArray = (Object[])objects.clone();
   }

   @Override
   public int hashCode() {
      if (this.intValue == 0) {
         this.intValue = 7;

         for (int i = 0; i < this.objectArray.length; i++) {
            Object object = this.objectArray[i];
            if (object != null) {
               this.intValue = 31 * this.intValue + object.hashCode();
            }
         }
      }

      return this.intValue;
   }

   @Override
   public boolean equals(Object object) {
      if (object == null) {
         return false;
      }

      if (object == this) {
         return true;
      }

      if (!(object instanceof CompoundKey)) {
         return false;
      }

      Object[] objectArray2 = ((CompoundKey)object).getObjectArray();
      if (objectArray2.length != this.objectArray.length) {
         return false;
      }

      for (int i = 0; i < this.objectArray.length; i++) {
         if (!checkObject(this.objectArray[i], objectArray2[i])) {
            return false;
         }
      }

      return true;
   }

   public CompoundKey(Object object, Object object2) {
      this(new Object[]{object, object2});
   }

   private Object[] getObjectArray() {
      return this.objectArray;
   }
}
