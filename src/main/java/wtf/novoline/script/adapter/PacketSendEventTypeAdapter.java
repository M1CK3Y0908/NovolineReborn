package wtf.novoline.script.adapter;

import net.minecraft.network.Packet;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.PacketAdapterRegistry.PacketKey;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class PacketSendEventTypeAdapter extends EventTypeAdapter<PacketSendEvent> {
   protected void putEvent(ScriptValue scriptValue, PacketSendEvent packetSendEvent) {
      Class classValue = packetSendEvent.packet.getClass();
      if (this.typeRegistry.containsKey(classValue)) {
         scriptValue.putObject("packet", packetSendEvent.packet, classValue);
      }
   }

   public PacketSendEvent apply(ScriptValue scriptValue, PacketSendEvent packetSendEvent) {
      ScriptValue scriptValue2 = scriptValue.getScriptValue("packet");
      if (scriptValue2 == null) {
         return packetSendEvent;
      }

      PacketKey packetKey = new PacketKey(
         scriptValue2.getInt("packetId"),
         (HandshakingType)scriptValue2.getEnum("packetType", HandshakingType.class),
         (ClientboundType)scriptValue2.getEnum("packetDirection", ClientboundType.class)
      );
      if (!this.typeRegistry.getPacketAdapterRegistry().containsKey(packetKey)) {
         throw new IllegalArgumentException("Couldn't find packet mapper by ID " + packetKey);
      }

      if (scriptValue2.containsKey("_jObject")) {
         packetSendEvent.packet = (Packet<?>)scriptValue2.getJavaObject();
         AbstractTypeAdapter abstractTypeAdapter = this.typeRegistry.getPacketAdapterRegistry().get(packetKey);
         abstractTypeAdapter.apply(scriptValue2, packetSendEvent.packet);
      } else {
         AbstractTypeAdapter abstractTypeAdapter2 = this.typeRegistry.getPacketAdapterRegistry().get(packetKey);
         packetSendEvent.packet = (Packet<?>)abstractTypeAdapter2.unpack(scriptValue2.asMap());
      }

      return packetSendEvent;
   }

   public PacketSendEventTypeAdapter(TypeRegistry typeRegistry2) {
      super("PacketSendEvent", typeRegistry2);
   }
}
