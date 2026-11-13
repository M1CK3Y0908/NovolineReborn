package wtf.novoline.script;

import java.math.BigDecimal;
import java.util.Locale;

public final class ValueUtils {
   public static String valueOf(Object object) {
      return String.valueOf(object);
   }

   public static BigDecimal createBigDecimal(Object object) {
      return new BigDecimal(valueOf(object));
   }

   public static String lowerCaseName(Enum enumValue) {
      if (enumValue == null) {
         throw new IllegalArgumentException("enum == null");
      } else {
         return enumValue.name().toLowerCase(Locale.ROOT);
      }
   }

   public static boolean booleanValue(Object object) {
      return (Boolean)object;
   }
}
