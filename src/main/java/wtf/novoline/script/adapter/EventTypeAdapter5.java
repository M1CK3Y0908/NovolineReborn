package wtf.novoline.script.adapter;

import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.PacketAdapterRegistry.PacketKey;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class EventTypeAdapter5 extends EventTypeAdapter<PacketReceiveEvent> {
   protected void putEvent(ScriptValue scriptValue, PacketReceiveEvent packetReceiveEvent) {
      Class classValue = packetReceiveEvent.packet.getClass();
      if (this.typeRegistry.containsKey(classValue)) {
         scriptValue.putObject("packet", packetReceiveEvent.packet, classValue);
      }
   }

   public EventTypeAdapter5(TypeRegistry typeRegistry2) {
      super("PacketReceiveEvent", typeRegistry2);
   }

   public PacketReceiveEvent apply(ScriptValue scriptValue, PacketReceiveEvent packetReceiveEvent) {
      if (scriptValue.containsKey("packet")) {
         ScriptValue scriptValue2 = scriptValue.getScriptValue("packet");
         PacketKey packetKey = new PacketKey(
            scriptValue2.getInt("packetId"),
            (HandshakingType)scriptValue2.getEnum("packetType", HandshakingType.class),
            (ClientboundType)scriptValue2.getEnum("packetDirection", ClientboundType.class)
         );
         AbstractTypeAdapter abstractTypeAdapter = this.typeRegistry.getPacketAdapterRegistry().get(packetKey);
         if (abstractTypeAdapter != null) {
            abstractTypeAdapter.apply(scriptValue2, packetReceiveEvent.packet);
         }
      }

      return packetReceiveEvent;
   }
}
