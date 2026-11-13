package net.optifine.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectorRaw {
   public static ReflectorField createReflectorField(Class clazz, Class clazz2, int i) {
      Field field = createField2(clazz, clazz2, i);
      if (field == null) {
         return null;
      }

      ReflectorClass reflectorClass = new ReflectorClass(clazz);
      return new ReflectorField(reflectorClass, field.getName());
   }

   public static Field[] createArray4(Class clazz, Class clazz2) {
      try {
         return createArray2(clazz.getDeclaredFields(), clazz2);
      } catch (Exception exception) {
         return null;
      }
   }

   public static Field createField2(Class clazz, Class clazz2, int i) {
      Field[] array = createArray4(clazz, clazz2);
      return i >= 0 && i < array.length ? array[i] : null;
   }

   public static Field createField(Class clazz, Field field, Class clazz2) {
      Field[] array = createArray(clazz, field, clazz2);
      return 0 < array.length ? array[0] : null;
   }

   public static Field[] createArray(Class clazz, Field field, Class clazz2) {
      try {
         List list = Arrays.asList(clazz.getDeclaredFields());
         int i = list.indexOf(field);
         if (i < 0) {
            return new Field[0];
         }

         List list2 = list.subList(i + 1, list.size());
         return createArray2((Field[]) list2.toArray(new Field[list2.size()]), clazz2);
      } catch (Exception exception) {
         return null;
      }
   }

   public static Field[] createArray3(Object object, Field[] fields, Class clazz, Object object2) {
      try {
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() == clazz) {
               boolean staticValue = Modifier.isStatic(field.getModifiers());
               if ((object != null || staticValue) && (object == null || !staticValue)) {
                  field.setAccessible(true);
                  Object object3 = field.get(object);
                  if (object3 == object2) {
                     arrayList.add(field);
                  } else if (object3 != null && object2 != null && object3.equals(object2)) {
                     arrayList.add(field);
                  }
               }
            }
         }

         return (Field[]) arrayList.toArray(new Field[arrayList.size()]);
      } catch (Exception exception) {
         return null;
      }
   }

   public static Field[] createArray2(Field[] fields, Class clazz) {
      try {
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() == clazz) {
               field.setAccessible(true);
               arrayList.add(field);
            }
         }

         return (Field[]) arrayList.toArray(new Field[arrayList.size()]);
      } catch (Exception exception) {
         return null;
      }
   }

   public static Object createObject(Object object, Class clazz, Class clazz2, int i) {
      ReflectorField reflectorField = createReflectorField(clazz, clazz2, i);
      return reflectorField == null ? null : (!reflectorField.isActive() ? null : Reflector.createObject6(object, reflectorField));
   }
}
