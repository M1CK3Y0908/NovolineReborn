package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics implements Packet<INetHandlerPlayClient> {
   private Map<StatBase, Integer> statsMap;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.statsMap.size());

      for (Entry entry : this.statsMap.entrySet()) {
         buf.writeString(((StatBase)entry.getKey()).statId);
         buf.writeVarIntToBuffer((Integer)entry.getValue());
      }
   }

   public S37PacketStatistics() {
   }

   public Map<StatBase, Integer> getStats() {
      return this.statsMap;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      int varIntFromBuffer = buf.readVarIntFromBuffer();
      this.statsMap = Maps.newHashMap();

      for (int innerIndex = 0; innerIndex < varIntFromBuffer; innerIndex++) {
         StatBase statBase = StatList.getOneShotStat(buf.readStringFromBuffer(32767));
         int varIntFromBuffer2 = buf.readVarIntFromBuffer();
         if (statBase != null) {
            this.statsMap.put(statBase, varIntFromBuffer2);
         }
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleStatistics(this);
   }

   public S37PacketStatistics(Map<StatBase, Integer> statsIn) {
      this.statsMap = statsIn;
   }
}
