package net.optifine.reflect;

public class ReflectorFields {
   private ReflectorClass reflectorClass;
   private int intValue;
   private Class classValue;
   private ReflectorField[] reflectorFieldArray;

   public ReflectorClass getReflectorClass() {
      return this.reflectorClass;
   }

   public Class getClassValue() {
      return this.classValue;
   }

   public ReflectorField getReflectorField(int i) {
      return i >= 0 && i < this.reflectorFieldArray.length ? this.reflectorFieldArray[i] : null;
   }

   public ReflectorFields(ReflectorClass reflectorClass2, Class clazz, int i) {
      this.reflectorClass = reflectorClass2;
      this.classValue = clazz;
      if (reflectorClass2.isActive() && clazz != null) {
         this.reflectorFieldArray = new ReflectorField[i];

         for (int i2 = 0; i2 < this.reflectorFieldArray.length; i2++) {
            this.reflectorFieldArray[i2] = new ReflectorField(reflectorClass2, clazz, i2);
         }
      }
   }

   public int getIntValue() {
      return this.intValue;
   }
}
