package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
   private String data;

   public NBTTagString(String data) {
      this.data = data;
      if (data == null) {
         throw new IllegalArgumentException("Empty string not allowed");
      }
   }

   @Override
   void write(DataOutput output) throws IOException {
      output.writeUTF(this.data);
   }

   @Override
   public NBTBase copy() {
      return new NBTTagString(this.data);
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data.hashCode();
   }

   public NBTTagString() {
      this.data = "";
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(288L);
      this.data = input.readUTF();
      sizeTracker.read(16 * this.data.length());
   }

   @Override
   public String getString() {
      return this.data;
   }

   @Override
   public String toString() {
      return "\"" + this.data.replace("\"", "\\\"") + "\"";
   }

   @Override
   public boolean equals(Object other) {
      if (!super.equals(other)) {
         return false;
      }

      NBTTagString nBTTagString = (NBTTagString)other;
      return this.data == null && nBTTagString.data == null || this.data != null && this.data.equals(nBTTagString.data);
   }

   @Override
   public byte getId() {
      return 8;
   }

   @Override
   public boolean hasNoTags() {
      return this.data.isEmpty();
   }
}
