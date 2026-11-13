package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
   private int[] intArray;

   @Override
   void write(DataOutput output) throws IOException {
      output.writeInt(this.intArray.length);

      for (int index = 0; index < this.intArray.length; index++) {
         output.writeInt(this.intArray[index]);
      }
   }

   @Override
   public NBTBase copy() {
      int[] intArray = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, intArray, 0, this.intArray.length);
      return new NBTTagIntArray(intArray);
   }

   NBTTagIntArray() {
   }

   public NBTTagIntArray(int[] values) {
      this.intArray = values;
   }

   @Override
   public String toString() {
      String text = "[";

      for (int i : this.intArray) {
         text = text + i + ",";
      }

      return text + "]";
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int intValue = input.readInt();
      sizeTracker.read(32 * intValue);
      this.intArray = new int[intValue];

      for (int innerIndex = 0; innerIndex < intValue; innerIndex++) {
         this.intArray[innerIndex] = input.readInt();
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.intArray);
   }

   @Override
   public boolean equals(Object other) {
      return super.equals(other) ? Arrays.equals(this.intArray, ((NBTTagIntArray)other).intArray) : false;
   }

   public int[] getIntArray() {
      return this.intArray;
   }

   @Override
   public byte getId() {
      return 11;
   }
}
