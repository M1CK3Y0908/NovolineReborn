package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTBase.NBTPrimitive {
   private float data;

   @Override
   public int getInt() {
      return MathHelper.floor_float(this.data);
   }

   @Override
   public boolean equals(Object other) {
      if (super.equals(other)) {
         NBTTagFloat nBTTagFloat = (NBTTagFloat)other;
         return this.data == nBTTagFloat.data;
      } else {
         return false;
      }
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(96L);
      this.data = input.readFloat();
   }

   @Override
   public short getShort() {
      return (short)(MathHelper.floor_float(this.data) & 65535);
   }

   @Override
   public long getLong() {
      return (long)this.data;
   }

   public NBTTagFloat(float data) {
      this.data = data;
   }

   @Override
   public byte getByte() {
      return (byte)(MathHelper.floor_float(this.data) & 0xFF);
   }

   @Override
   public String toString() {
      return this.data + "f";
   }

   @Override
   public byte getId() {
      return 5;
   }

   @Override
   public float getFloat() {
      return this.data;
   }

   @Override
   public double getDouble() {
      return this.data;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Float.floatToIntBits(this.data);
   }

   @Override
   void write(DataOutput output) throws IOException {
      output.writeFloat(this.data);
   }

   @Override
   public NBTBase copy() {
      return new NBTTagFloat(this.data);
   }

   NBTTagFloat() {
   }
}
