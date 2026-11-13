package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public enum EnumChatFormatting {
   BLACK("BLACK", '0', 0),
   DARK_BLUE("DARK_BLUE", '1', 1),
   DARK_GREEN("DARK_GREEN", '2', 2),
   DARK_AQUA("DARK_AQUA", '3', 3),
   DARK_RED("DARK_RED", '4', 4),
   DARK_PURPLE("DARK_PURPLE", '5', 5),
   GOLD("GOLD", '6', 6),
   GRAY("GRAY", '7', 7),
   DARK_GRAY("DARK_GRAY", '8', 8),
   BLUE("BLUE", '9', 9),
   GREEN("GREEN", 'a', 10),
   AQUA("AQUA", 'b', 11),
   RED("RED", 'c', 12),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
   YELLOW("YELLOW", 'e', 14),
   WHITE("WHITE", 'f', 15),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1);

   private final String name;
   private final char formattingCode;
   private final boolean fancyStyling;
   private final int colorIndex;
   private static final Map<String, EnumChatFormatting> nameMapping = Maps.newHashMap();
   private final String controlString;
   private static final Pattern formattingCodePattern = Pattern.compile("(?i)" + '§' + "[0-9A-FK-OR]");

   static {
      for (EnumChatFormatting enumChatFormatting : values()) {
         nameMapping.put(cleanName(enumChatFormatting.name), enumChatFormatting);
      }
   }

   public static Collection getCollection() {
      ArrayList arrayList = Lists.newArrayList();

      for (EnumChatFormatting enumChatFormatting : values()) {
         if (enumChatFormatting.isColor()) {
         }

         if (!enumChatFormatting.isFancyStyling()) {
            arrayList.add(enumChatFormatting.getFriendlyName());
         }
      }

      return arrayList;
   }

   EnumChatFormatting(String text2, char c2, int i2) {
      this(text2, c2, false, i2);
   }

   public boolean isFancyStyling() {
      return this.fancyStyling;
   }

   public static EnumChatFormatting getValueByName(String friendlyName) {
      return friendlyName == null ? null : nameMapping.get(cleanName(friendlyName));
   }

   @Override
   public String toString() {
      return this.controlString;
   }

   private static String cleanName(String name) {
      return name.toLowerCase().replaceAll("[^a-z]", "");
   }

   public String getFriendlyName() {
      return this.name().toLowerCase();
   }

   public boolean isColor() {
      return !this.fancyStyling && this != RESET;
   }

   public static String getTextWithoutFormattingCodes(String text) {
      return text == null ? null : formattingCodePattern.matcher(text).replaceAll("");
   }

   public static EnumChatFormatting getByColorIndex(int colorIndex) {
      if (colorIndex < 0) {
         return RESET;
      }

      for (EnumChatFormatting enumChatFormatting : values()) {
         if (enumChatFormatting.getColorIndex() == colorIndex) {
            return enumChatFormatting;
         }
      }

      return null;
   }

   public int getColorIndex() {
      return this.colorIndex;
   }

   EnumChatFormatting(String text2, char c2, boolean flag, int i2) {
      this.name = text2;
      this.formattingCode = c2;
      this.fancyStyling = flag;
      this.colorIndex = i2;
      this.controlString = "§" + c2;
   }

   EnumChatFormatting(String text2, char c2, boolean flag) {
      this(text2, c2, flag, -1);
   }
}
