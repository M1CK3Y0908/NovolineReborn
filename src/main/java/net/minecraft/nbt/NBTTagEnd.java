package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {
   @Override
   void write(DataOutput output) throws IOException {
   }

   @Override
   public NBTBase copy() {
      return new NBTTagEnd();
   }

   @Override
   public String toString() {
      return "END";
   }

   @Override
   public byte getId() {
      return 0;
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(64L);
   }
}
