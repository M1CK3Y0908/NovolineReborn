package wtf.novoline.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C0APacketAnimation;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C0APacketAnimationTypeAdapter extends PacketTypeAdapter<C0APacketAnimation> {
   public C0APacketAnimation unpack(Map<String, Object> map) {
      return new C0APacketAnimation();
   }

   public C0APacketAnimationTypeAdapter(TypeRegistry typeRegistry) {
      super(10, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C0APacketAnimation", typeRegistry);
   }
}
