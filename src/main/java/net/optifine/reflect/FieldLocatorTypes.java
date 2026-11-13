package net.optifine.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.optifine.Log;

public class FieldLocatorTypes implements IFieldLocator {
   private Field field = null;

   public FieldLocatorTypes(Class clazz, Class[] clazzs, Class clazz2, Class[] clazzs2, String text) {
      Field[] declaredFields = clazz.getDeclaredFields();
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < declaredFields.length; i++) {
         Field field2 = declaredFields[i];
         arrayList.add(field2.getType());
      }

      ArrayList arrayList2 = new ArrayList();
      arrayList2.addAll(Arrays.asList(clazzs));
      arrayList2.add(clazz2);
      arrayList2.addAll(Arrays.asList(clazzs2));
      int i2 = Collections.indexOfSubList(arrayList, arrayList2);
      if (i2 < 0) {
         Log.log("(Reflector) Field not found: " + text);
      } else if (Collections.indexOfSubList(arrayList.subList(i2 + 1, arrayList.size()), arrayList2) >= 0) {
         Log.log("(Reflector) More than one match found for field: " + text);
      } else {
         int i3 = i2 + clazzs.length;
         this.field = declaredFields[i3];
      }
   }

   @Override
   public Field getField2() {
      return this.field;
   }
}
