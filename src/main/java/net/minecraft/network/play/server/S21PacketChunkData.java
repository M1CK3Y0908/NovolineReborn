package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData implements Packet<INetHandlerPlayClient> {
   private int chunkX;
   private int chunkZ;
   private boolean isGroundUp;
   private S21PacketChunkData.Extracted extractedData;

   public S21PacketChunkData() {
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkX = buf.readInt();
      this.chunkZ = buf.readInt();
      this.isGroundUp = buf.readBoolean();
      this.extractedData = new S21PacketChunkData.Extracted();
      this.extractedData.dataSize = buf.readShort();
      this.extractedData.data = buf.readByteArray();
   }

   public boolean isGroundUp() {
      return this.isGroundUp;
   }

   public int getExtractedSize() {
      return this.extractedData.dataSize;
   }

   protected static int getCompressedChunkSize(int bits, boolean isOverworld, boolean isGroundUp) {
      int i = bits * 2 * 16 * 16 * 16;
      int i2 = bits * 16 * 16 * 16 / 2;
      int i3 = isOverworld ? bits * 16 * 16 * 16 / 2 : 0;
      int i4 = isGroundUp ? 256 : 0;
      return i + i2 + i3 + i4;
   }

   public static S21PacketChunkData.Extracted getExtractedData(Chunk chunk, boolean groundUp, boolean hasSky, int primaryBitMask) {
      ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
      S21PacketChunkData.Extracted extracted = new S21PacketChunkData.Extracted();
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < blockStorageArray.length; i++) {
         ExtendedBlockStorage extendedBlockStorage = blockStorageArray[i];
         if (extendedBlockStorage != null && (!groundUp || !extendedBlockStorage.isEmpty()) && (primaryBitMask & 1 << i) != 0) {
            extracted.dataSize |= 1 << i;
            arrayList.add(extendedBlockStorage);
         }
      }

      extracted.data = new byte[getCompressedChunkSize(Integer.bitCount(extracted.dataSize), hasSky, groundUp)];
      int i2 = 0;
      Iterator iterator2 = arrayList.iterator();

      while (iterator2.hasNext()) {
         for (char c : ((ExtendedBlockStorage)iterator2.next()).getData()) {
            extracted.data[i2++] = (byte)(c & 0xFF);
            extracted.data[i2++] = (byte)(c >> '\b' & 0xFF);
         }
      }

      iterator2 = arrayList.iterator();

      while (iterator2.hasNext()) {
         i2 = copyBytes(((ExtendedBlockStorage)iterator2.next()).getBlocklightArray().getData(), extracted.data, i2);
      }

      if (hasSky) {
         iterator2 = arrayList.iterator();

         while (iterator2.hasNext()) {
            i2 = copyBytes(((ExtendedBlockStorage)iterator2.next()).getSkylightArray().getData(), extracted.data, i2);
         }
      }

      if (groundUp) {
         copyBytes(chunk.getBiomeArray(), extracted.data, i2);
      }

      return extracted;
   }

   public byte[] getExtractedDataBytes() {
      return this.extractedData.data;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.chunkX);
      buf.writeInt(this.chunkZ);
      buf.writeBoolean(this.isGroundUp);
      buf.writeShort((short)(this.extractedData.dataSize & 65535));
      buf.writeByteArray(this.extractedData.data);
   }

   public S21PacketChunkData(Chunk chunkIn, boolean groundUp, int primaryBitMask) {
      this.chunkX = chunkIn.xPosition;
      this.chunkZ = chunkIn.zPosition;
      this.isGroundUp = groundUp;
      this.extractedData = getExtractedData(chunkIn, groundUp, !chunkIn.getWorld().provider.getHasNoSky(), primaryBitMask);
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleChunkData(this);
   }

   private static int copyBytes(byte[] source, byte[] destination, int offset) {
      System.arraycopy(source, 0, destination, offset, source.length);
      return offset + source.length;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public static class Extracted {
      public byte[] data;
      public int dataSize;
   }
}
