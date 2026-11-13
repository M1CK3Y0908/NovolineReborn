package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT {
   private static final Logger logger = LogManager.getLogger();
   private static final Pattern INT_ARRAY_MATCHER = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

   private static String nextToken(String input, boolean requireNameValueSeparator) throws NBTException {
      int topLevelCharacterIndex = getTopLevelCharacterIndex(input, ':');
      int topLevelCharacterIndex2 = getTopLevelCharacterIndex(input, ',');
      if (requireNameValueSeparator) {
         if (topLevelCharacterIndex == -1) {
            throw new NBTException("Unable to locate name/value separator for string: " + input);
         }

         if (topLevelCharacterIndex2 != -1 && topLevelCharacterIndex2 < topLevelCharacterIndex) {
            throw new NBTException("Name error at: " + input);
         }
      } else if (topLevelCharacterIndex == -1 || topLevelCharacterIndex > topLevelCharacterIndex2) {
         topLevelCharacterIndex = -1;
      }

      return getSegment(input, topLevelCharacterIndex);
   }

   private static String getTagValue(String input, boolean allowBlankName) throws NBTException {
      if (allowBlankName) {
         input = input.trim();
         if (input.startsWith("{") || input.startsWith("[")) {
            return input;
         }
      }

      int topLevelCharacterIndex = getTopLevelCharacterIndex(input, ':');
      if (topLevelCharacterIndex != -1) {
         return input.substring(topLevelCharacterIndex + 1).trim();
      } else if (allowBlankName) {
         return input;
      } else {
         throw new NBTException("Unable to locate name/value separator for string: " + input);
      }
   }

   private static JsonToNBT.Any parseChildTag(String json, boolean allowBlankName) throws NBTException {
      String text = getTagName(json, allowBlankName);
      String text2 = getTagValue(json, allowBlankName);
      return parseEntry(text, text2);
   }

   private static String getSegment(String input, int separatorIndex) throws NBTException {
      Stack stack = new Stack();
      int i = separatorIndex + 1;
      boolean conditionMet = false;
      boolean conditionMet2 = false;
      boolean conditionMet3 = false;
      int innerIndex = 0;

      while (i < input.length()) {
         char c0 = input.charAt(i);
         if (c0 == '"') {
            if (isEscaped(input, i)) {
               if (!conditionMet) {
                  throw new NBTException("Illegal use of \\\": " + input);
               }
            } else {
               conditionMet = !conditionMet;
               if (conditionMet && !conditionMet3) {
                  conditionMet2 = true;
               }

               if (!conditionMet) {
                  innerIndex = i;
               }
            }
         } else if (!conditionMet) {
            if (c0 != '{' && c0 != '[') {
               if (c0 == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + input);
               }

               if (c0 == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + input);
               }

               if (c0 == ',' && stack.isEmpty()) {
                  return input.substring(0, i);
               }
            } else {
               stack.push(c0);
            }
         }

         if (!Character.isWhitespace(c0)) {
            if (!conditionMet && conditionMet2 && innerIndex != i) {
               return input.substring(0, innerIndex + 1);
            }

            conditionMet3 = true;
         }

         i++;
      }

      return input.substring(0, i);
   }

   static JsonToNBT.Any parseEntry(String... nameValuePair) throws NBTException {
      return parseTag(nameValuePair[0], nameValuePair[1]);
   }

   private static String getTagName(String input, boolean allowBlankName) throws NBTException {
      if (allowBlankName) {
         input = input.trim();
         if (input.startsWith("{") || input.startsWith("[")) {
            return "";
         }
      }

      int topLevelCharacterIndex = getTopLevelCharacterIndex(input, ':');
      if (topLevelCharacterIndex != -1) {
         return input.substring(0, topLevelCharacterIndex).trim();
      } else if (allowBlankName) {
         return "";
      } else {
         throw new NBTException("Unable to locate name/value separator for string: " + input);
      }
   }

   private static boolean isEscaped(String input, int index) {
      return index > 0 && input.charAt(index - 1) == '\\' && !isEscaped(input, index - 1);
   }

   static int countTopTags(String input) throws NBTException {
      int i = 0;
      boolean conditionMet = false;
      Stack stack = new Stack();

      for (int innerIndex = 0; innerIndex < input.length(); innerIndex++) {
         char c0 = input.charAt(innerIndex);
         if (c0 == '"') {
            if (isEscaped(input, innerIndex)) {
               if (!conditionMet) {
                  throw new NBTException("Illegal use of \\\": " + input);
               }
            } else {
               conditionMet = !conditionMet;
            }
         } else if (!conditionMet) {
            if (c0 != '{' && c0 != '[') {
               if (c0 == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + input);
               }

               if (c0 == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + input);
               }
            } else {
               if (stack.isEmpty()) {
                  i++;
               }

               stack.push(c0);
            }
         }
      }

      if (conditionMet) {
         throw new NBTException("Unbalanced quotation: " + input);
      }

      if (!stack.isEmpty()) {
         throw new NBTException("Unbalanced brackets: " + input);
      }

      if (i == 0 && !input.isEmpty()) {
         i = 1;
      }

      return i;
   }

   private static int getTopLevelCharacterIndex(String input, char targetCharacter) {
      int i = 0;
      boolean conditionMet = true;

      while (i < input.length()) {
         char c0 = input.charAt(i);
         if (c0 == '"') {
            if (!isEscaped(input, i)) {
               conditionMet = !conditionMet;
            }
         } else if (conditionMet) {
            if (c0 == targetCharacter) {
               return i;
            }

            if (c0 == '{' || c0 == '[') {
               return -1;
            }
         }

         i++;
      }

      return -1;
   }

   static JsonToNBT.Any parseTag(String tagName, String jsonValue) throws NBTException {
      jsonValue = jsonValue.trim();
      if (jsonValue.startsWith("{")) {
         jsonValue = jsonValue.substring(1, jsonValue.length() - 1);
         JsonToNBT.Compound compound = new JsonToNBT.Compound(tagName);

         while (jsonValue.length() > 0) {
            String text3 = nextToken(jsonValue, true);
            if (text3.length() > 0) {
               compound.children.add(parseChildTag(text3, false));
            }

            if (jsonValue.length() < text3.length() + 1) {
               break;
            }

            char c = jsonValue.charAt(text3.length());
            if (c != ',' && c != '{' && c != '}' && c != '[' && c != ']') {
               throw new NBTException("Unexpected token '" + c + "' at: " + jsonValue.substring(text3.length()));
            }

            jsonValue = jsonValue.substring(text3.length() + 1);
         }

         return compound;
      } else if (jsonValue.startsWith("[") && !INT_ARRAY_MATCHER.matcher(jsonValue).matches()) {
         jsonValue = jsonValue.substring(1, jsonValue.length() - 1);
         JsonToNBT.List list = new JsonToNBT.List(tagName);

         while (jsonValue.length() > 0) {
            String text4 = nextToken(jsonValue, false);
            if (text4.length() > 0) {
               list.children.add(parseChildTag(text4, true));
            }

            if (jsonValue.length() < text4.length() + 1) {
               break;
            }

            char c2 = jsonValue.charAt(text4.length());
            if (c2 != ',' && c2 != '{' && c2 != '}' && c2 != '[' && c2 != ']') {
               throw new NBTException("Unexpected token '" + c2 + "' at: " + jsonValue.substring(text4.length()));
            }

            jsonValue = jsonValue.substring(text4.length() + 1);
         }

         return list;
      } else {
         return new JsonToNBT.Primitive(tagName, jsonValue);
      }
   }

   public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException {
      jsonString = jsonString.trim();
      if (!jsonString.startsWith("{")) {
         throw new NBTException("Invalid tag encountered, expected '{' as first char.");
      } else if (countTopTags(jsonString) != 1) {
         throw new NBTException("Encountered multiple top tags, only one expected");
      } else {
         return (NBTTagCompound)parseTag("tag", jsonString).parse();
      }
   }

   abstract static class Any {
      protected String name;

      public abstract NBTBase parse() throws NBTException;
   }

   static class Compound extends JsonToNBT.Any {
      protected java.util.List<JsonToNBT.Any> children = Lists.newArrayList();

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();

         for (JsonToNBT.Any any : this.children) {
            nbtTagCompound.setTag(any.name, any.parse());
         }

         return nbtTagCompound;
      }

      public Compound(String name) {
         this.name = name;
      }

      private static NBTException createNBTException(NBTException nbtException) {
         return nbtException;
      }
   }

   static class List extends JsonToNBT.Any {
      protected java.util.List<JsonToNBT.Any> children = Lists.newArrayList();

      public List(String json) {
         this.name = json;
      }

      @Override
      public NBTBase parse() throws NBTException {
         NBTTagList nbtTagList = new NBTTagList();

         for (JsonToNBT.Any any : this.children) {
            nbtTagList.appendTag(any.parse());
         }

         return nbtTagList;
      }

      private static NBTException createNBTException(NBTException nbtException) {
         return nbtException;
      }
   }

   static class Primitive extends JsonToNBT.Any {
      private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
      private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
      private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
      private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
      private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
      private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
      private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
      private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
      protected String value;

      @Override
      public NBTBase parse() throws NBTException {
         try {
            if (DOUBLE.matcher(this.value).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.value.substring(0, this.value.length() - 1)));
            }

            if (FLOAT.matcher(this.value).matches()) {
               return new NBTTagFloat(Float.parseFloat(this.value.substring(0, this.value.length() - 1)));
            }

            if (BYTE.matcher(this.value).matches()) {
               return new NBTTagByte(Byte.parseByte(this.value.substring(0, this.value.length() - 1)));
            }

            if (LONG.matcher(this.value).matches()) {
               return new NBTTagLong(Long.parseLong(this.value.substring(0, this.value.length() - 1)));
            }

            if (SHORT.matcher(this.value).matches()) {
               return new NBTTagShort(Short.parseShort(this.value.substring(0, this.value.length() - 1)));
            }

            if (INTEGER.matcher(this.value).matches()) {
               return new NBTTagInt(Integer.parseInt(this.value));
            }

            if (DOUBLE_UNTYPED.matcher(this.value).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.value));
            }

            if (this.value.equalsIgnoreCase("true") || this.value.equalsIgnoreCase("false")) {
               return new NBTTagByte((byte)(Boolean.parseBoolean(this.value) ? 1 : 0));
            }
         } catch (NumberFormatException numberFormatException) {
            this.value = this.value.replaceAll("\\\\\"", "\"");
            return new NBTTagString(this.value);
         }

         if (this.value.startsWith("[") && this.value.endsWith("]")) {
            String text3 = this.value.substring(1, this.value.length() - 1);
            String[] texts = (String[])Iterables.toArray(SPLITTER.split(text3), String.class);

            try {
               int[] iArray = new int[texts.length];

               for (int i = 0; i < texts.length; i++) {
                  iArray[i] = Integer.parseInt(texts[i].trim());
               }

               return new NBTTagIntArray(iArray);
            } catch (NumberFormatException numberFormatException2) {
               return new NBTTagString(this.value);
            }
         } else {
            if (this.value.startsWith("\"") && this.value.endsWith("\"")) {
               this.value = this.value.substring(1, this.value.length() - 1);
            }

            this.value = this.value.replaceAll("\\\\\"", "\"");
            StringBuilder stringBuilder = new StringBuilder();

            for (int i2 = 0; i2 < this.value.length(); i2++) {
               if (i2 < this.value.length() - 1 && this.value.charAt(i2) == '\\' && this.value.charAt(i2 + 1) == '\\') {
                  stringBuilder.append('\\');
                  i2++;
               } else {
                  stringBuilder.append(this.value.charAt(i2));
               }
            }

            return new NBTTagString(stringBuilder.toString());
         }
      }

      public Primitive(String name, String value) {
         this.name = name;
         this.value = value;
      }
   }
}
