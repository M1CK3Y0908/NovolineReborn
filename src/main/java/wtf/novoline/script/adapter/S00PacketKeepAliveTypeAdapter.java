package wtf.novoline.script.adapter;

import net.minecraft.network.play.server.S00PacketKeepAlive;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class S00PacketKeepAliveTypeAdapter extends PacketTypeAdapter<S00PacketKeepAlive> {
   public S00PacketKeepAliveTypeAdapter(TypeRegistry typeRegistry) {
      super(0, HandshakingType.PLAY, ClientboundType.CLIENTBOUND, "S00PacketKeepAlive", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, S00PacketKeepAlive s00PacketKeepAlive) {
      scriptValue.putSupplier("getId", s00PacketKeepAlive::getId);
      scriptValue.putConsumer("setid", s00PacketKeepAlive::setId);
   }
}
