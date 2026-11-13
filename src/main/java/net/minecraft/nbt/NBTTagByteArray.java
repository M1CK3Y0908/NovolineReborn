package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
   private byte[] data;

   public byte[] getByteArray() {
      return this.data;
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int intValue = input.readInt();
      sizeTracker.read(8 * intValue);
      this.data = new byte[intValue];
      input.readFully(this.data);
   }

   NBTTagByteArray() {
   }

   @Override
   public byte getId() {
      return 7;
   }

   @Override
   void write(DataOutput output) throws IOException {
      output.writeInt(this.data.length);
      output.write(this.data);
   }

   @Override
   public String toString() {
      return "[" + this.data.length + " bytes]";
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.data);
   }

   @Override
   public NBTBase copy() {
      byte[] byteArray = new byte[this.data.length];
      System.arraycopy(this.data, 0, byteArray, 0, this.data.length);
      return new NBTTagByteArray(byteArray);
   }

   @Override
   public boolean equals(Object other) {
      return super.equals(other) ? Arrays.equals(this.data, ((NBTTagByteArray)other).data) : false;
   }

   public NBTTagByteArray(byte[] data) {
      this.data = data;
   }
}
