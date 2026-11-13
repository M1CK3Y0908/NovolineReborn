package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
   public final byte[] data;
   private final int depthBits;
   private final int depthBitsPlusFour;

   public NibbleArrayReader(byte[] bArray) {
      this.data = bArray;
      this.depthBits = 7;
      this.depthBitsPlusFour = 11;
   }

   public int get(int i, int i2, int i3) {
      int i4 = i << this.depthBitsPlusFour | i3 << this.depthBits | i2;
      int i5 = i4 >> 1;
      return (i4 & 1) == 0 ? this.data[i5] & 15 : this.data[i5] >> 4 & 15;
   }
}
