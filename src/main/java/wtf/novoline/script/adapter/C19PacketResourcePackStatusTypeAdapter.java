package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.client.C19PacketResourcePackStatus.Action;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C19PacketResourcePackStatusTypeAdapter extends PacketTypeAdapter<C19PacketResourcePackStatus> {
   public C19PacketResourcePackStatusTypeAdapter(TypeRegistry typeRegistry) {
      super(25, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C19PacketResourcePackStatus", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, C19PacketResourcePackStatus c19PacketResourcePackStatus) {
      scriptValue.put("hash", c19PacketResourcePackStatus.hash);
      scriptValue.putEnum("status", c19PacketResourcePackStatus.status);
   }

   public C19PacketResourcePackStatus unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C19PacketResourcePackStatus c19PacketResourcePackStatus = new C19PacketResourcePackStatus();
      if (scriptValue.containsKey("hash")) {
         c19PacketResourcePackStatus = this.apply(scriptValue, c19PacketResourcePackStatus);
      }

      return c19PacketResourcePackStatus;
   }

   public C19PacketResourcePackStatus apply(ScriptValue scriptValue, C19PacketResourcePackStatus c19PacketResourcePackStatus) {
      c19PacketResourcePackStatus.hash = scriptValue.getString("hash");
      c19PacketResourcePackStatus.status = (Action)scriptValue.getEnum("status", Action.class);
      return c19PacketResourcePackStatus;
   }
}
