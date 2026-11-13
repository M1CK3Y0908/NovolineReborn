package net.optifine.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.optifine.Log;

public class ReflectorMethod implements IResolvable {
   private ReflectorClass reflectorClass = null;
   private String text = null;
   private Class[] classArray = null;
   private boolean flag = false;
   private Method method = null;

   @Override
   public void update3() {
      this.getMethod();
   }

   public boolean isActive() {
      return this.flag ? this.method != null : this.getMethod() != null;
   }

   public static Method createMethod(Class clazz, String text2, Class[] clazzs) {
      Method[] declaredMethods = clazz.getDeclaredMethods();

      for (int i = 0; i < declaredMethods.length; i++) {
         Method method2 = declaredMethods[i];
         if (method2.getName().equals(text2)) {
            Class[] parameterTypes = method2.getParameterTypes();
            if (Reflector.checkArray(clazzs, parameterTypes)) {
               return method2;
            }
         }
      }

      return null;
   }

   public Class getType() {
      Method method2 = this.getMethod();
      return method2 == null ? null : method2.getReturnType();
   }

   public ReflectorMethod(ReflectorClass reflectorClass2, String text2) {
      this(reflectorClass2, text2, (Class[])null);
   }

   public Method getMethod() {
      if (this.flag) {
         return this.method;
      }

      this.flag = true;
      Class type = this.reflectorClass.getType();
      if (type == null) {
         return null;
      }

      try {
         if (this.classArray == null) {
            Method[] array = createArray(type, this.text);
            if (array.length <= 0) {
               Log.log("(Reflector) Method not present: " + type.getName() + "." + this.text);
               return null;
            }

            if (array.length > 1) {
               Log.handleString3("(Reflector) More than one method found: " + type.getName() + "." + this.text);

               for (int i = 0; i < array.length; i++) {
                  Log.handleString3("(Reflector)  - " + array[i]);
               }

               return null;
            }

            this.method = array[0];
         } else {
            this.method = createMethod(type, this.text, this.classArray);
         }

         if (this.method == null) {
            Log.log("(Reflector) Method not present: " + type.getName() + "." + this.text);
            return null;
         } else {
            this.method.setAccessible(true);
            return this.method;
         }
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return null;
      }
   }

   public static Method[] createArray(Class clazz, String text2) {
      ArrayList arrayList = new ArrayList();
      Method[] declaredMethods = clazz.getDeclaredMethods();

      for (int i = 0; i < declaredMethods.length; i++) {
         Method method2 = declaredMethods[i];
         if (method2.getName().equals(text2)) {
            arrayList.add(method2);
         }
      }

      return (Method[]) arrayList.toArray(new Method[arrayList.size()]);
   }

   public ReflectorMethod(ReflectorClass reflectorClass2, String text2, Class[] clazzs) {
      this.reflectorClass = reflectorClass2;
      this.text = text2;
      this.classArray = clazzs;
      ReflectorResolver.handleIResolvable(this);
   }

   public void update2() {
      this.flag = true;
      this.method = null;
   }
}
