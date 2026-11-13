package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTBase.NBTPrimitive {
   private double data;

   @Override
   public NBTBase copy() {
      return new NBTTagDouble(this.data);
   }

   NBTTagDouble() {
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(128L);
      this.data = input.readDouble();
   }

   @Override
   public boolean equals(Object other) {
      if (super.equals(other)) {
         NBTTagDouble nBTTagDouble = (NBTTagDouble)other;
         return this.data == nBTTagDouble.data;
      } else {
         return false;
      }
   }

   @Override
   public double getDouble() {
      return this.data;
   }

   @Override
   void write(DataOutput output) throws IOException {
      output.writeDouble(this.data);
   }

   public NBTTagDouble(double data) {
      this.data = data;
   }

   @Override
   public short getShort() {
      return (short)(MathHelper.floor_double(this.data) & 65535);
   }

   @Override
   public int hashCode() {
      long l = Double.doubleToLongBits(this.data);
      return super.hashCode() ^ (int)(l ^ l >>> 32);
   }

   @Override
   public String toString() {
      return this.data + "d";
   }

   @Override
   public byte getByte() {
      return (byte)(MathHelper.floor_double(this.data) & 0xFF);
   }

   @Override
   public long getLong() {
      return (long)Math.floor(this.data);
   }

   @Override
   public byte getId() {
      return 6;
   }

   @Override
   public int getInt() {
      return MathHelper.floor_double(this.data);
   }

   @Override
   public float getFloat() {
      return (float)this.data;
   }
}
