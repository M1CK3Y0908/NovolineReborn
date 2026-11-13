package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1CPacketEntityMetadata implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private List<net.minecraft.entity.DataWatcher.WatchableObject> watchableObjects;

   public S1CPacketEntityMetadata(int entityIdIn, DataWatcher dataWatcher, boolean includeAll) {
      this.entityId = entityIdIn;
      if (includeAll) {
         this.watchableObjects = dataWatcher.getAllWatched();
      } else {
         this.watchableObjects = dataWatcher.getChanged();
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      this.watchableObjects = DataWatcher.readWatchedListFromPacketBuffer(buf);
   }

   public List<net.minecraft.entity.DataWatcher.WatchableObject> getWatchableObjects() {
      return this.watchableObjects;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      DataWatcher.writeWatchedListToPacketBuffer(this.watchableObjects, buf);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public S1CPacketEntityMetadata() {
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityMetadata(this);
   }
}
