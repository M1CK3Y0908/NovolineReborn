package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase.NBTPrimitive {
   private long data;

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(128L);
      this.data = input.readLong();
   }

   @Override
   public NBTBase copy() {
      return new NBTTagLong(this.data);
   }

   @Override
   public String toString() {
      return this.data + "L";
   }

   @Override
   public byte getId() {
      return 4;
   }

   @Override
   public boolean equals(Object other) {
      if (super.equals(other)) {
         NBTTagLong nBTTagLong = (NBTTagLong)other;
         return this.data == nBTTagLong.data;
      } else {
         return false;
      }
   }

   NBTTagLong() {
   }

   @Override
   public int getInt() {
      return (int)(this.data & -1L);
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (int)(this.data ^ this.data >>> 32);
   }

   public NBTTagLong(long data) {
      this.data = data;
   }

   @Override
   public byte getByte() {
      return (byte)(this.data & 255L);
   }

   @Override
   public double getDouble() {
      return this.data;
   }

   @Override
   public long getLong() {
      return this.data;
   }

   @Override
   void write(DataOutput output) throws IOException {
      output.writeLong(this.data);
   }

   @Override
   public short getShort() {
      return (short)(this.data & 65535L);
   }

   @Override
   public float getFloat() {
      return (float)this.data;
   }
}
