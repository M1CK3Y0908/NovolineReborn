package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;

public class RegionFile {
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private final File fileName;
   private int sizeDelta;
   private long lastModified;
   private RandomAccessFile dataFile;
   private static final byte[] emptySector = new byte[4096];
   private List<Boolean> sectorFree;

   public DataOutputStream getChunkDataOutputStream(int x, int z) {
      return this.outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(x, z)));
   }

   private int getOffset(int x, int z) {
      return this.offsets[x + z * 32];
   }

   private void setChunkTimestamp(int x, int z, int timestamp) throws IOException {
      this.chunkTimestamps[x + z * 32] = timestamp;
      this.dataFile.seek(4096 + (x + z * 32) * 4);
      this.dataFile.writeInt(timestamp);
   }

   public boolean isChunkSaved(int x, int z) {
      return this.getOffset(x, z) != 0;
   }

   private boolean outOfBounds(int x, int z) {
      return x < 0 || x >= 32 || z < 0 || z >= 32;
   }

   public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
      if (this.outOfBounds(x, z)) {
         return null;
      }

      try {
         int offset2 = this.getOffset(x, z);
         if (offset2 == 0) {
            return null;
         } else {
            int i = offset2 >> 8;
            int i2 = offset2 & 0xFF;
            if (i + i2 > this.sectorFree.size()) {
               return null;
            } else {
               this.dataFile.seek(i * 4096);
               int intValue = this.dataFile.readInt();
               if (intValue > 4096 * i2) {
                  return null;
               } else if (intValue <= 0) {
                  return null;
               } else {
                  byte byteValue = this.dataFile.readByte();
                  if (byteValue == 1) {
                     byte[] bArray = new byte[intValue - 1];
                     this.dataFile.read(bArray);
                     return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bArray))));
                  } else if (byteValue == 2) {
                     byte[] bArray2 = new byte[intValue - 1];
                     this.dataFile.read(bArray2);
                     return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bArray2))));
                  } else {
                     return null;
                  }
               }
            }
         }
      } catch (IOException ioException) {
         return null;
      }
   }

   public RegionFile(File fileNameIn) {
      this.fileName = fileNameIn;
      this.sizeDelta = 0;

      try {
         if (fileNameIn.exists()) {
            this.lastModified = fileNameIn.lastModified();
         }

         this.dataFile = new RandomAccessFile(fileNameIn, "rw");
         if (this.dataFile.length() < 4096L) {
            for (int index = 0; index < 1024; index++) {
               this.dataFile.writeInt(0);
            }

            for (int index2 = 0; index2 < 1024; index2++) {
               this.dataFile.writeInt(0);
            }

            this.sizeDelta += 8192;
         }

         if ((this.dataFile.length() & 4095L) != 0L) {
            for (int innerIndex = 0; innerIndex < (this.dataFile.length() & 4095L); innerIndex++) {
               this.dataFile.write(0);
            }
         }

         int count = (int)this.dataFile.length() / 4096;
         this.sectorFree = Lists.newArrayListWithCapacity(count);

         for (int innerIndex2 = 0; innerIndex2 < count; innerIndex2++) {
            this.sectorFree.add(true);
         }

         this.sectorFree.set(0, false);
         this.sectorFree.set(1, false);
         this.dataFile.seek(0L);

         for (int outerIndex = 0; outerIndex < 1024; outerIndex++) {
            int intValue = this.dataFile.readInt();
            this.offsets[outerIndex] = intValue;
            if (intValue != 0 && (intValue >> 8) + (intValue & 0xFF) <= this.sectorFree.size()) {
               for (int outerIndex2 = 0; outerIndex2 < (intValue & 0xFF); outerIndex2++) {
                  this.sectorFree.set((intValue >> 8) + outerIndex2, false);
               }
            }
         }

         for (int index3 = 0; index3 < 1024; index3++) {
            int intValue2 = this.dataFile.readInt();
            this.chunkTimestamps[index3] = intValue2;
         }
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private void write(int sectorNumber, byte[] data, int length) throws IOException {
      this.dataFile.seek(sectorNumber * 4096);
      this.dataFile.writeInt(length + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(data, 0, length);
   }

   public void close() throws IOException {
      if (this.dataFile != null) {
         this.dataFile.close();
      }
   }

   private void setOffset(int x, int z, int offset) throws IOException {
      this.offsets[x + z * 32] = offset;
      this.dataFile.seek((x + z * 32) * 4);
      this.dataFile.writeInt(offset);
   }

   protected synchronized void write(int x, int z, byte[] data, int length) {
      try {
         int offset2 = this.getOffset(x, z);
         int i = offset2 >> 8;
         int i2 = offset2 & 0xFF;
         int i3 = (length + 5) / 4096 + 1;
         if (i3 >= 256) {
            return;
         }

         if (i != 0 && i2 == i3) {
            this.write(i, data, length);
         } else {
            for (int i4 = 0; i4 < i2; i4++) {
               this.sectorFree.set(i + i4, true);
            }

            int i5 = this.sectorFree.indexOf(true);
            int i6 = 0;
            if (i5 != -1) {
               for (int i7 = i5; i7 < this.sectorFree.size(); i7++) {
                  if (i6 != 0) {
                     if (this.sectorFree.get(i7)) {
                        i6++;
                     } else {
                        i6 = 0;
                     }
                  } else if (this.sectorFree.get(i7)) {
                     i5 = i7;
                     i6 = 1;
                  }

                  if (i6 >= i3) {
                     break;
                  }
               }
            }

            if (i6 >= i3) {
               i = i5;
               this.setOffset(x, z, i5 << 8 | i3);

               for (int i8 = 0; i8 < i3; i8++) {
                  this.sectorFree.set(i + i8, false);
               }

               this.write(i, data, length);
            } else {
               this.dataFile.seek(this.dataFile.length());
               i = this.sectorFree.size();

               for (int i9 = 0; i9 < i3; i9++) {
                  this.dataFile.write(emptySector);
                  this.sectorFree.add(false);
               }

               this.sizeDelta += 4096 * i3;
               this.write(i, data, length);
               this.setOffset(x, z, i << 8 | i3);
            }
         }

         this.setChunkTimestamp(x, z, (int)(MinecraftServer.getCurrentTimeMillis() / 1000L));
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private int chunkX;
      private int chunkZ;

      public ChunkBuffer(int x, int z) {
         super(8096);
         this.chunkX = x;
         this.chunkZ = z;
      }

      @Override
      public void close() throws IOException {
         RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
      }
   }
}
