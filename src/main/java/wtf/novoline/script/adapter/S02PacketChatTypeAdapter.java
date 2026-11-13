package wtf.novoline.script.adapter;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class S02PacketChatTypeAdapter extends PacketTypeAdapter<S02PacketChat> {
   public S02PacketChatTypeAdapter(TypeRegistry typeRegistry) {
      super(2, HandshakingType.PLAY, ClientboundType.CLIENTBOUND, "S02PacketChat", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, S02PacketChat s02PacketChat) {
      scriptValue.putSupplier("getChatComponent", MappingUtils.mappingSupplier(IChatComponent.class, s02PacketChat::getChatComponent));
      scriptValue.putConsumer("setChatComponent", MappingUtils.mappingConsumer(IChatComponent.class, s02PacketChat::setChatComponent));
      scriptValue.putSupplier("getType", s02PacketChat::getType);
      scriptValue.putConsumer("setType", s02PacketChat::setType);
   }
}
