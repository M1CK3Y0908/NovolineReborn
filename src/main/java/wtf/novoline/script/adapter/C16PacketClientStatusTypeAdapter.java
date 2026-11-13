package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C16PacketClientStatusTypeAdapter extends PacketTypeAdapter<C16PacketClientStatus> {
   protected void putPacketFields(ScriptValue scriptValue, C16PacketClientStatus c16PacketClientStatus) {
      scriptValue.putEnum("status", c16PacketClientStatus.status);
   }

   public C16PacketClientStatusTypeAdapter(TypeRegistry typeRegistry) {
      super(22, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C16PacketClientStatus", typeRegistry);
   }

   public C16PacketClientStatus unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C16PacketClientStatus c16PacketClientStatus = new C16PacketClientStatus();
      if (scriptValue.containsKey("status")) {
         c16PacketClientStatus = this.apply(scriptValue, c16PacketClientStatus);
      }

      return c16PacketClientStatus;
   }

   public C16PacketClientStatus apply(ScriptValue scriptValue, C16PacketClientStatus c16PacketClientStatus) {
      c16PacketClientStatus.status = (EnumState)scriptValue.getEnum("status", EnumState.class);
      return c16PacketClientStatus;
   }
}
