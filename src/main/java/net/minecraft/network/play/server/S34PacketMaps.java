package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class S34PacketMaps implements Packet<INetHandlerPlayClient> {
   private int mapId;
   private byte mapScale;
   private Vec4b[] mapVisiblePlayersVec4b;
   private int mapMinX;
   private int mapMinY;
   private int mapMaxX;
   private int mapMaxY;
   private byte[] mapDataBytes;

   public void setMapdataTo(MapData mapdataIn) {
      mapdataIn.scale = this.mapScale;
      mapdataIn.mapDecorations.clear();

      for (int index = 0; index < this.mapVisiblePlayersVec4b.length; index++) {
         Vec4b nestedIndex = this.mapVisiblePlayersVec4b[index];
         mapdataIn.mapDecorations.put("icon-" + index, nestedIndex);
      }

      for (int innerIndex = 0; innerIndex < this.mapMaxX; innerIndex++) {
         for (int nestedIndex = 0; nestedIndex < this.mapMaxY; nestedIndex++) {
            mapdataIn.colors[this.mapMinX + innerIndex + (this.mapMinY + nestedIndex) * 128] = this.mapDataBytes[innerIndex + nestedIndex * this.mapMaxX];
         }
      }
   }

   public S34PacketMaps(int mapIdIn, byte scale, Collection<Vec4b> visiblePlayers, byte[] colors, int minX, int minY, int maxX, int maxY) {
      this.mapId = mapIdIn;
      this.mapScale = scale;
      this.mapVisiblePlayersVec4b = visiblePlayers.toArray(new Vec4b[visiblePlayers.size()]);
      this.mapMinX = minX;
      this.mapMinY = minY;
      this.mapMaxX = maxX;
      this.mapMaxY = maxY;
      this.mapDataBytes = new byte[maxX * maxY];

      for (int index = 0; index < maxX; index++) {
         for (int innerIndex = 0; innerIndex < maxY; innerIndex++) {
            this.mapDataBytes[index + innerIndex * maxX] = colors[minX + index + (minY + innerIndex) * 128];
         }
      }
   }

   public int getMapId() {
      return this.mapId;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMaps(this);
   }

   public S34PacketMaps() {
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.mapId);
      buf.writeByte(this.mapScale);
      buf.writeVarIntToBuffer(this.mapVisiblePlayersVec4b.length);

      for (Vec4b vec4b : this.mapVisiblePlayersVec4b) {
         buf.writeByte((vec4b.getIconBit() & 15) << 4 | vec4b.getRotation() & 15);
         buf.writeByte(vec4b.getX());
         buf.writeByte(vec4b.getY());
      }

      buf.writeByte(this.mapMaxX);
      if (this.mapMaxX > 0) {
         buf.writeByte(this.mapMaxY);
         buf.writeByte(this.mapMinX);
         buf.writeByte(this.mapMinY);
         buf.writeByteArray(this.mapDataBytes);
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.mapId = buf.readVarIntFromBuffer();
      this.mapScale = buf.readByte();
      this.mapVisiblePlayersVec4b = new Vec4b[buf.readVarIntFromBuffer()];

      for (int index = 0; index < this.mapVisiblePlayersVec4b.length; index++) {
         short byteValue = buf.readByte();
         this.mapVisiblePlayersVec4b[index] = new Vec4b((byte)(byteValue >> 4 & 15), buf.readByte(), buf.readByte(), (byte)(byteValue & 15));
      }

      this.mapMaxX = buf.readUnsignedByte();
      if (this.mapMaxX > 0) {
         this.mapMaxY = buf.readUnsignedByte();
         this.mapMinX = buf.readUnsignedByte();
         this.mapMinY = buf.readUnsignedByte();
         this.mapDataBytes = buf.readByteArray();
      }
   }
}
