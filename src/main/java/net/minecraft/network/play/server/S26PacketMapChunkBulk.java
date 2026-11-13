package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk implements Packet<INetHandlerPlayClient> {
   private int[] xPositions;
   private int[] zPositions;
   private S21PacketChunkData.Extracted[] chunksData;
   private boolean isOverworld;

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMapChunkBulk(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.isOverworld = buf.readBoolean();
      int varIntFromBuffer = buf.readVarIntFromBuffer();
      this.xPositions = new int[varIntFromBuffer];
      this.zPositions = new int[varIntFromBuffer];
      this.chunksData = new S21PacketChunkData.Extracted[varIntFromBuffer];

      for (int innerIndex = 0; innerIndex < varIntFromBuffer; innerIndex++) {
         this.xPositions[innerIndex] = buf.readInt();
         this.zPositions[innerIndex] = buf.readInt();
         this.chunksData[innerIndex] = new S21PacketChunkData.Extracted();
         this.chunksData[innerIndex].dataSize = buf.readShort() & '\uffff';
         this.chunksData[innerIndex].data = new byte[S21PacketChunkData.getCompressedChunkSize(
            Integer.bitCount(this.chunksData[innerIndex].dataSize), this.isOverworld, true
         )];
      }

      for (int nestedIndex = 0; nestedIndex < varIntFromBuffer; nestedIndex++) {
         buf.readBytes(this.chunksData[nestedIndex].data);
      }
   }

   public int getChunkZ(int index) {
      return this.zPositions[index];
   }

   public int getChunkX(int index) {
      return this.xPositions[index];
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBoolean(this.isOverworld);
      buf.writeVarIntToBuffer(this.chunksData.length);

      for (int index = 0; index < this.xPositions.length; index++) {
         buf.writeInt(this.xPositions[index]);
         buf.writeInt(this.zPositions[index]);
         buf.writeShort((short)(this.chunksData[index].dataSize & 65535));
      }

      for (int innerIndex = 0; innerIndex < this.xPositions.length; innerIndex++) {
         buf.writeBytes(this.chunksData[innerIndex].data);
      }
   }

   public int getChunkCount() {
      return this.xPositions.length;
   }

   public S26PacketMapChunkBulk(List<Chunk> chunks) {
      int count = chunks.size();
      this.xPositions = new int[count];
      this.zPositions = new int[count];
      this.chunksData = new S21PacketChunkData.Extracted[count];
      this.isOverworld = !chunks.get(0).getWorld().provider.getHasNoSky();

      for (int innerIndex = 0; innerIndex < count; innerIndex++) {
         Chunk chunk = chunks.get(innerIndex);
         S21PacketChunkData.Extracted s21packetchunkdata$extracted = S21PacketChunkData.getExtractedData(chunk, true, this.isOverworld, 65535);
         this.xPositions[innerIndex] = chunk.xPosition;
         this.zPositions[innerIndex] = chunk.zPosition;
         this.chunksData[innerIndex] = s21packetchunkdata$extracted;
      }
   }

   public byte[] getChunkBytes(int index) {
      return this.chunksData[index].data;
   }

   public int getChunkSize(int index) {
      return this.chunksData[index].dataSize;
   }

   public S26PacketMapChunkBulk() {
   }
}
