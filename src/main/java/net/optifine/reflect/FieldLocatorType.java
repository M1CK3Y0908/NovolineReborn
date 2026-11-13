package net.optifine.reflect;

import java.lang.reflect.Field;
import net.optifine.Log;

public class FieldLocatorType implements IFieldLocator {
   private ReflectorClass reflectorClass = null;
   private Class classValue = null;
   private int intValue;

   public FieldLocatorType(ReflectorClass reflectorClass2, Class clazz, int i) {
      this.reflectorClass = reflectorClass2;
      this.classValue = clazz;
      this.intValue = i;
   }

   @Override
   public Field getField2() {
      Class type2 = this.reflectorClass.getType();
      if (type2 == null) {
         return null;
      }

      try {
         Field[] declaredFields = type2.getDeclaredFields();
         int i = 0;

         for (int i2 = 0; i2 < declaredFields.length; i2++) {
            Field field = declaredFields[i2];
            if (field.getType() == this.classValue) {
               if (i == this.intValue) {
                  field.setAccessible(true);
                  return field;
               }

               i++;
            }
         }

         Log.log("(Reflector) Field not present: " + type2.getName() + ".(type: " + this.classValue + ", index: " + this.intValue + ")");
         return null;
      } catch (SecurityException securityException) {
         securityException.printStackTrace();
         return null;
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return null;
      }
   }

   public FieldLocatorType(ReflectorClass reflectorClass2, Class clazz) {
      this(reflectorClass2, clazz, 0);
   }
}
