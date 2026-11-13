package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C01PacketChatMessageTypeAdapter extends PacketTypeAdapter<C01PacketChatMessage> {
   protected void putPacketFields(ScriptValue scriptValue, C01PacketChatMessage c01PacketChatMessage) {
      scriptValue.put("message", c01PacketChatMessage.message);
   }

   public C01PacketChatMessage unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("message") ? new C01PacketChatMessage(scriptValue.getString("message")) : new C01PacketChatMessage();
   }

   public C01PacketChatMessage apply(ScriptValue scriptValue, C01PacketChatMessage c01PacketChatMessage) {
      c01PacketChatMessage.message = scriptValue.getString("message");
      return c01PacketChatMessage;
   }

   public C01PacketChatMessageTypeAdapter(TypeRegistry typeRegistry) {
      super(1, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C01PacketChatMessage", typeRegistry);
   }
}
