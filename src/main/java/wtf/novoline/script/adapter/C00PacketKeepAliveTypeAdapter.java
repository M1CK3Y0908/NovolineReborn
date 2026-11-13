package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C00PacketKeepAliveTypeAdapter extends PacketTypeAdapter<C00PacketKeepAlive> {
   public C00PacketKeepAlive unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("key") ? this.apply(scriptValue, new C00PacketKeepAlive()) : new C00PacketKeepAlive();
   }

   public C00PacketKeepAlive apply(ScriptValue scriptValue, C00PacketKeepAlive c00PacketKeepAlive) {
      c00PacketKeepAlive.key = scriptValue.getInt("key");
      return c00PacketKeepAlive;
   }

   public C00PacketKeepAliveTypeAdapter(TypeRegistry typeRegistry) {
      super(0, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C00PacketKeepAlive", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, C00PacketKeepAlive c00PacketKeepAlive) {
      scriptValue.put("key", c00PacketKeepAlive.key);
   }
}
