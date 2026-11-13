package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C09PacketHeldItemChangeTypeAdapter extends PacketTypeAdapter<C09PacketHeldItemChange> {
   public C09PacketHeldItemChangeTypeAdapter(TypeRegistry typeRegistry) {
      super(9, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C09PacketHeldItemChange", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, C09PacketHeldItemChange c09PacketHeldItemChange) {
      scriptValue.put("slotId", c09PacketHeldItemChange.slotId);
   }

   public C09PacketHeldItemChange apply(ScriptValue scriptValue, C09PacketHeldItemChange c09PacketHeldItemChange) {
      c09PacketHeldItemChange.slotId = scriptValue.getInt("slotId");
      return c09PacketHeldItemChange;
   }

   public C09PacketHeldItemChange unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("slotId") ? new C09PacketHeldItemChange(scriptValue.getInt("slotId")) : new C09PacketHeldItemChange();
   }
}
