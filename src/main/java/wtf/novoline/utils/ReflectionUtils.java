package wtf.novoline.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

public final class ReflectionUtils {
   public static Method findContractMethod(Class clazz, String text) {
      for (Method method : clazz.getDeclaredMethods()) {
         ReflectionUtils.Contract annotation2 = method.getAnnotation(ReflectionUtils.Contract.class);
         if (annotation2 != null && annotation2.value().equals(text)) {
            return method;
         }
      }

      return null;
   }

   public static Method lookupContractMethod(Class clazz, String text) {
      return findContractMethod(clazz, text);
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   public @interface Contract {
      String value();
   }
}
