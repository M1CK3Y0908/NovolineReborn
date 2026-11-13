package net.optifine.config;

import java.util.Arrays;
import java.util.regex.Pattern;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.src.Config;
import net.optifine.util.StrUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class NbtTagValue {
   private static final String PATTERN = "pattern:";
   private static final int TYPE_IREGEX = 4;
   private static final String IPATTERN = "ipattern:";
   private static final int TYPE_REGEX = 3;
   private static final int intValue = 0;
   private String[] parents = null;
   private static final int intValue2 = 1;
   private static final String PREFIX_HEX_COLOR = "#";
   private static final String REGEX = "regex:";
   private String name = null;
   private static final int intValue3 = 0;
   private static final int TYPE_IPATTERN = 2;
   private static final String IREGEX = "iregex:";
   private static final Pattern PATTERN_HEX_COLOR = Pattern.compile("^#[0-9a-f]{6}+$");
   private boolean negative = false;
   private int type = 0;
   private String value = null;
   private static final int intValue4 = 1;
   private int valueFormat = 0;

   public boolean matchesValue(String nbtValue) {
      if (nbtValue == null) {
         return false;
      }

      switch (this.type) {
         case 0:
            return nbtValue.equals(this.value);
         case 1:
            return this.matchesPattern(nbtValue, this.value);
         case 2:
            return this.matchesPattern(nbtValue.toLowerCase(), this.value);
         case 3:
            return this.matchesRegex(nbtValue, this.value);
         case 4:
            return this.matchesRegex(nbtValue.toLowerCase(), this.value);
         default:
            throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.type);
      }
   }

   public NbtTagValue(String tag, String value) {
      String[] stringArray = Config.tokenize(tag, ".");
      this.parents = Arrays.copyOfRange(stringArray, 0, stringArray.length - 1);
      this.name = stringArray[stringArray.length - 1];
      if (value.startsWith("!")) {
         this.negative = true;
         value = value.substring(1);
      }

      if (value.startsWith("pattern:")) {
         this.type = 1;
         value = value.substring("pattern:".length());
      } else if (value.startsWith("ipattern:")) {
         this.type = 2;
         value = value.substring("ipattern:".length()).toLowerCase();
      } else if (value.startsWith("regex:")) {
         this.type = 3;
         value = value.substring("regex:".length());
      } else if (value.startsWith("iregex:")) {
         this.type = 4;
         value = value.substring("iregex:".length()).toLowerCase();
      } else {
         this.type = 0;
      }

      value = StringEscapeUtils.unescapeJava(value);
      if (this.type == 0 && PATTERN_HEX_COLOR.matcher(value).matches()) {
         this.valueFormat = 1;
      }

      this.value = value;
   }

   public boolean matchesBase(NBTBase nbtBase) {
      if (nbtBase == null) {
         return false;
      }

      String text = getNbtString(nbtBase, this.valueFormat);
      return this.matchesValue(text);
   }

   private static NBTBase getChildTag(NBTBase tagBase, String tag) {
      if (tagBase instanceof NBTTagCompound nBTTagCompound) {
         return nBTTagCompound.getTag(tag);
      } else if (tagBase instanceof NBTTagList nBTTagList) {
         if (tag.equals("count")) {
            return new NBTTagInt(nBTTagList.tagCount());
         }

         int i = Config.parseInt(tag, -1);
         return i >= 0 && i < nBTTagList.tagCount() ? nBTTagList.get(i) : null;
      } else {
         return null;
      }
   }

   public boolean matchesCompound(NBTTagCompound nbt) {
      if (nbt == null) {
         return false;
      }

      NBTBase nbt2 = nbt;

      for (int i = 0; i < this.parents.length; i++) {
         String text2 = this.parents[i];
         nbt2 = getChildTag(nbt2, text2);
         if (nbt2 == null) {
            return false;
         }
      }

      if (this.name.equals("*")) {
         return this.matchesAnyChild(nbt2);
      }

      nbt2 = getChildTag(nbt2, this.name);
      return nbt2 == null ? false : this.matchesBase(nbt2);
   }

   public boolean matches(NBTTagCompound nbt) {
      return this.negative ? !this.matchesCompound(nbt) : this.matchesCompound(nbt);
   }

   private boolean matchesRegex(String str, String regex) {
      return str.matches(regex);
   }

   private static String getNbtString(NBTBase nbtBase, int format) {
      if (nbtBase == null) {
         return null;
      } else if (nbtBase instanceof NBTTagString nBTTagString) {
         return nBTTagString.getString();
      } else if (nbtBase instanceof NBTTagInt nBTTagInt) {
         return format == 1 ? "#" + StrUtils.stringInc(Integer.toHexString(nBTTagInt.getInt())) : Integer.toString(nBTTagInt.getInt());
      } else if (nbtBase instanceof NBTTagByte nBTTagByte) {
         return Byte.toString(nBTTagByte.getByte());
      } else if (nbtBase instanceof NBTTagShort nBTTagShort) {
         return Short.toString(nBTTagShort.getShort());
      } else if (nbtBase instanceof NBTTagLong nBTTagLong) {
         return Long.toString(nBTTagLong.getLong());
      } else if (nbtBase instanceof NBTTagFloat nBTTagFloat) {
         return Float.toString(nBTTagFloat.getFloat());
      } else {
         return nbtBase instanceof NBTTagDouble nBTTagDouble ? Double.toString(nBTTagDouble.getDouble()) : nbtBase.toString();
      }
   }

   @Override
   public String toString() {
      StringBuffer stringBuffer = new StringBuffer();

      for (int index = 0; index < this.parents.length; index++) {
         String text2 = this.parents[index];
         if (index > 0) {
            stringBuffer.append(".");
         }

         stringBuffer.append(text2);
      }

      if (stringBuffer.length() > 0) {
         stringBuffer.append(".");
      }

      stringBuffer.append(this.name);
      stringBuffer.append(" = ");
      stringBuffer.append(this.value);
      return stringBuffer.toString();
   }

   private boolean matchesAnyChild(NBTBase tagBase) {
      if (tagBase instanceof NBTTagCompound nbtTagCompound) {
         for (String text2 : nbtTagCompound.getKeySet()) {
            NBTBase tag2 = nbtTagCompound.getTag(text2);
            if (this.matchesBase(tag2)) {
               return true;
            }
         }
      }

      if (tagBase instanceof NBTTagList nbtTagList) {
         int i = nbtTagList.tagCount();

         for (int i2 = 0; i2 < i; i2++) {
            NBTBase nbtBase2 = nbtTagList.get(i2);
            if (this.matchesBase(nbtBase2)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean matchesPattern(String str, String pattern) {
      return StrUtils.checkString2(str, pattern);
   }
}
