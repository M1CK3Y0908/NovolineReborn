package wtf.novoline.utils;

public class ArrayUtils {
   public static boolean contains(Object[] objects, Object object) {
      if (objects == null) {
         return false;
      }

      for (int i = 0; i < objects.length; i++) {
         if (objects[i] == object) {
            return true;
         }
      }

      return false;
   }

   public static String join(Object[] objects, String text) {
      if (objects == null) {
         return "";
      }

      StringBuffer stringBuffer = new StringBuffer(objects.length * 5);

      for (int i = 0; i < objects.length; i++) {
         Object object = objects[i];
         if (i > 0) {
            stringBuffer.append(text);
         }

         stringBuffer.append(String.valueOf(object));
      }

      return stringBuffer.toString();
   }

   public static String joinWithComma(Object[] objects) {
      return join(objects, ", ");
   }
}
