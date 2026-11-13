package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {
   public static final String[] NBT_TYPES = new String[]{
      "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"
   };

   protected static NBTBase createNewByType(byte id) {
      switch (id) {
         case 0:
            return new NBTTagEnd();
         case 1:
            return new NBTTagByte();
         case 2:
            return new NBTTagShort();
         case 3:
            return new NBTTagInt();
         case 4:
            return new NBTTagLong();
         case 5:
            return new NBTTagFloat();
         case 6:
            return new NBTTagDouble();
         case 7:
            return new NBTTagByteArray();
         case 8:
            return new NBTTagString();
         case 9:
            return new NBTTagList();
         case 10:
            return new NBTTagCompound();
         case 11:
            return new NBTTagIntArray();
         default:
            return null;
      }
   }

   protected String getString() {
      return this.toString();
   }

   @Override
   public boolean equals(Object other) {
      return !(other instanceof NBTBase nBTBase) ? false : this.getId() == nBTBase.getId();
   }

   abstract void read(DataInput dataInput, int i, NBTSizeTracker nbtSizeTracker) throws IOException;

   public abstract byte getId();

   public abstract NBTBase copy();

   @Override
   public abstract String toString();

   @Override
   public int hashCode() {
      return this.getId();
   }

   abstract void write(DataOutput dataOutput) throws IOException;

   public boolean hasNoTags() {
      return false;
   }

   public abstract static class NBTPrimitive extends NBTBase {
      public abstract float getFloat();

      public abstract int getInt();

      public abstract short getShort();

      public abstract byte getByte();

      public abstract double getDouble();

      public abstract long getLong();
   }
}
