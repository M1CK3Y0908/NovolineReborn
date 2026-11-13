package net.minecraft.nbt;

public class NBTSizeTracker {
   private long read;
   private final long max;
   public static final NBTSizeTracker INFINITE = new NBTSizeTracker(0L) {
      @Override
      public void read(long bits) {
      }
   };

   public void read(long bits) {
      this.read += bits / 8L;
      if (this.read > this.max) {
         long max2 = this.max;
         long read2 = this.read;
         throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + read2 + "bytes where max allowed: " + max2);
      }
   }

   public NBTSizeTracker(long max) {
      this.max = max;
   }
}
