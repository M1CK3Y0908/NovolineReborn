package net.optifine.reflect;

import java.lang.reflect.Field;
import net.optifine.Log;

public class FieldLocatorName implements IFieldLocator {
   private ReflectorClass reflectorClass = null;
   private String text = null;

   @Override
   public Field getField2() {
      Class type = this.reflectorClass.getType();
      if (type == null) {
         return null;
      }

      try {
         Field field = this.getField(type, this.text);
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException noSuchFieldException) {
         Log.log("(Reflector) Field not present: " + type.getName() + "." + this.text);
         return null;
      } catch (SecurityException securityException) {
         securityException.printStackTrace();
         return null;
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return null;
      }
   }

   private Field getField(Class clazz, String text2) throws NoSuchFieldException {
      Field[] declaredFields = clazz.getDeclaredFields();

      for (int i = 0; i < declaredFields.length; i++) {
         Field field = declaredFields[i];
         if (field.getName().equals(text2)) {
            return field;
         }
      }

      if (clazz == Object.class) {
         throw new NoSuchFieldException(text2);
      } else {
         return this.getField(clazz.getSuperclass(), text2);
      }
   }

   public FieldLocatorName(ReflectorClass reflectorClass2, String text2) {
      this.reflectorClass = reflectorClass2;
      this.text = text2;
   }
}
