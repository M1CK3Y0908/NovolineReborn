package net.optifine.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StrUtils {
   public static String stringInc(String str) {
      if (str == null) {
         str = "";
      }

      if (str.length() >= 6) {
         return str;
      }

      StringBuffer stringBuffer = new StringBuffer();
      int i = 6 - str.length();

      while (stringBuffer.length() < i) {
         stringBuffer.append('0');
      }

      return stringBuffer.toString() + str;
   }

   private static boolean checkString4(String text, String text2) {
      if (text != null && text2 != null) {
         return text.length() < text2.length() ? false : checkString3(text.substring(text.length() - text2.length(), text.length()), text2);
      } else {
         return text == text2;
      }
   }

   public static String addSuffixCheck(String str, String suffix) {
      return str != null && suffix != null ? (str.endsWith(suffix) ? str : str + suffix) : str;
   }

   public static String trim(String str, String chars) {
      if (str != null && chars != null) {
         str = trimLeading(str, chars);
         return trimTrailing(str, chars);
      } else {
         return str;
      }
   }

   public static String getSegment(String str, String start, String end) {
      if (str != null && start != null && end != null) {
         int i = str.indexOf(start);
         if (i < 0) {
            return null;
         }

         int i2 = str.indexOf(end, i);
         return i2 < 0 ? null : str.substring(i, i2 + end.length());
      } else {
         return null;
      }
   }

   public static String trimTrailing(String str, String chars) {
      if (str != null && chars != null) {
         int count = str.length();

         int i;
         for (i = count; i > 0; i--) {
            char c0 = str.charAt(i - 1);
            if (chars.indexOf(c0) < 0) {
               break;
            }
         }

         return i == count ? str : str.substring(0, i);
      } else {
         return str;
      }
   }

   public static String removePrefix(String str, String prefix) {
      if (str != null && prefix != null) {
         if (str.startsWith(prefix)) {
            str = str.substring(prefix.length());
         }

         return str;
      } else {
         return str;
      }
   }

   public static String removePrefixSuffix(String str, String[] prefixes, String[] suffixes) {
      str = removePrefix(str, prefixes);
      return removeSuffix(str, suffixes);
   }

   public static String replacePrefix(String str, String prefix, String prefixNew) {
      if (str == null || prefix == null) {
         return str;
      }

      if (!str.startsWith(prefix)) {
         return str;
      }

      if (prefixNew == null) {
         prefixNew = "";
      }

      str = str.substring(prefix.length());
      return prefixNew + str;
   }

   private static boolean checkString3(String text, String text2) {
      if (text != null && text2 != null) {
         if (text.length() != text2.length()) {
            return false;
         }

         for (int i = 0; i < text2.length(); i++) {
            char c = text2.charAt(i);
            if (c != '?' && text.charAt(i) != c) {
               return false;
            }
         }

         return true;
      } else {
         return text == text2;
      }
   }

   public static boolean endsWith(String str, String[] suffixes) {
      if (str == null) {
         return false;
      }

      if (suffixes == null) {
         return false;
      }

      for (int index = 0; index < suffixes.length; index++) {
         String text = suffixes[index];
         if (str.endsWith(text)) {
            return true;
         }
      }

      return false;
   }

   public static String replaceSuffix(String str, String suffix, String suffixNew) {
      if (str == null || suffix == null) {
         return str;
      }

      if (!str.endsWith(suffix)) {
         return str;
      }

      if (suffixNew == null) {
         suffixNew = "";
      }

      str = str.substring(0, str.length() - suffix.length());
      return str + suffixNew;
   }

   public static String removePrefix(String str, String[] prefixes) {
      if (str != null && prefixes != null) {
         int count = str.length();

         for (int innerIndex = 0; innerIndex < prefixes.length; innerIndex++) {
            String text = prefixes[innerIndex];
            str = removePrefix(str, text);
            if (str.length() != count) {
               break;
            }
         }

         return str;
      } else {
         return str;
      }
   }

   public static boolean checkString2(String text, String text2) {
      if (text2 != null && text != null) {
         if (text2.indexOf(42) < 0) {
            return text2.indexOf(63) < 0 ? text2.equals(text) : checkString3(text, text2);
         }

         ArrayList arrayList = new ArrayList();
         String text3 = '*' + "";
         if (text2.startsWith(text3)) {
            arrayList.add("");
         }

         StringTokenizer stringTokenizer = new StringTokenizer(text2, text3);

         while (stringTokenizer.hasMoreElements()) {
            arrayList.add(stringTokenizer.nextToken());
         }

         if (text2.endsWith(text3)) {
            arrayList.add("");
         }

         String text4 = (String)arrayList.get(0);
         if (!checkString(text, text4)) {
            return false;
         }

         String text5 = (String)arrayList.get(arrayList.size() - 1);
         if (!checkString4(text, text5)) {
            return false;
         }

         int i = 0;

         for (int i2 = 0; i2 < arrayList.size(); i2++) {
            String text6 = (String)arrayList.get(i2);
            if (text6.length() > 0) {
               int intValue = createInt(text, text6, i);
               if (intValue < 0) {
                  return false;
               }

               i = intValue + text6.length();
            }
         }

         return true;
      } else {
         return text2 == text;
      }
   }

   private static boolean checkString(String text, String text2) {
      if (text != null && text2 != null) {
         return text.length() < text2.length() ? false : checkString3(text.substring(0, text2.length()), text2);
      } else {
         return text == text2;
      }
   }

   public static boolean startsWith(String str, String[] prefixes) {
      if (str == null) {
         return false;
      }

      if (prefixes == null) {
         return false;
      }

      for (int index = 0; index < prefixes.length; index++) {
         String text = prefixes[index];
         if (str.startsWith(text)) {
            return true;
         }
      }

      return false;
   }

   public static String removeSuffix(String str, String[] suffixes) {
      if (str != null && suffixes != null) {
         int count = str.length();

         for (int innerIndex = 0; innerIndex < suffixes.length; innerIndex++) {
            String text = suffixes[innerIndex];
            str = removeSuffix(str, text);
            if (str.length() != count) {
               break;
            }
         }

         return str;
      } else {
         return str;
      }
   }

   public static String removePrefixSuffix(String str, String prefix, String suffix) {
      return removePrefixSuffix(str, new String[]{prefix}, new String[]{suffix});
   }

   public static String removeSuffix(String str, String suffix) {
      if (str != null && suffix != null) {
         if (str.endsWith(suffix)) {
            str = str.substring(0, str.length() - suffix.length());
         }

         return str;
      } else {
         return str;
      }
   }

   private static int createInt(String text, String text2, int i) {
      if (text != null && text2 != null) {
         if (i >= 0 && i <= text.length()) {
            if (text.length() < i + text2.length()) {
               return -1;
            }

            for (int i2 = i; i2 + text2.length() <= text.length(); i2++) {
               if (checkString3(text.substring(i2, i2 + text2.length()), text2)) {
                  return i2;
               }
            }

            return -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static String trimLeading(String str, String chars) {
      if (str != null && chars != null) {
         int count = str.length();

         for (int innerIndex = 0; innerIndex < count; innerIndex++) {
            char c0 = str.charAt(innerIndex);
            if (chars.indexOf(c0) < 0) {
               return str.substring(innerIndex);
            }
         }

         return "";
      } else {
         return str;
      }
   }
}
