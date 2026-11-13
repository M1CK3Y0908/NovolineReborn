package wtf.novoline.utils;

public class NameUtils {
   public static String stripFormatting(String text) {
      StringBuilder stringBuilder = new StringBuilder();
      char[] cArray = text.toCharArray();

      for (int i = 0; i < cArray.length; i++) {
         char c = cArray[i];
         if (c == 167) {
            i++;
         } else {
            stringBuilder.append(c);
         }
      }

      return stringBuilder.toString();
   }
}
