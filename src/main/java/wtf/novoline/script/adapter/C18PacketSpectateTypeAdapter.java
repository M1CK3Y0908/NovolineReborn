package wtf.novoline.script.adapter;

import java.util.Map;
import java.util.UUID;
import net.minecraft.network.play.client.C18PacketSpectate;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C18PacketSpectateTypeAdapter extends PacketTypeAdapter<C18PacketSpectate> {
   public C18PacketSpectate unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C18PacketSpectate c18PacketSpectate = new C18PacketSpectate();
      if (scriptValue.containsKey("id")) {
         c18PacketSpectate = this.apply(scriptValue, c18PacketSpectate);
      }

      return c18PacketSpectate;
   }

   public C18PacketSpectateTypeAdapter(TypeRegistry typeRegistry) {
      super(24, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C18PacketSpectate", typeRegistry);
   }

   public C18PacketSpectate apply(ScriptValue scriptValue, C18PacketSpectate c18PacketSpectate) {
      c18PacketSpectate.id = (UUID)scriptValue.getObject("id");
      return c18PacketSpectate;
   }

   protected void putPacketFields(ScriptValue scriptValue, C18PacketSpectate c18PacketSpectate) {
      scriptValue.put("id", c18PacketSpectate.id);
   }
}
