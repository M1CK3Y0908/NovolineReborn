package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C12PacketUpdateSignTypeAdapter extends PacketTypeAdapter<C12PacketUpdateSign> {
   protected void putPacketFields(ScriptValue scriptValue, C12PacketUpdateSign c12PacketUpdateSign) {
      scriptValue.putArray("lines", c12PacketUpdateSign.lines, IChatComponent.class);
      scriptValue.putObject("pos", c12PacketUpdateSign.pos, BlockPos.class);
   }

   public C12PacketUpdateSign apply(ScriptValue scriptValue, C12PacketUpdateSign c12PacketUpdateSign) {
      c12PacketUpdateSign.lines = (IChatComponent[])scriptValue.getArray("lines", IChatComponent.class);
      c12PacketUpdateSign.pos = (BlockPos)scriptValue.getScriptValue("pos").getJavaObject();
      return c12PacketUpdateSign;
   }

   public C12PacketUpdateSignTypeAdapter(TypeRegistry typeRegistry) {
      super(18, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C12PacketUpdateSign", typeRegistry);
   }

   public C12PacketUpdateSign unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C12PacketUpdateSign c12PacketUpdateSign = new C12PacketUpdateSign();
      if (scriptValue.containsKey("lines")) {
         c12PacketUpdateSign = this.apply(scriptValue, c12PacketUpdateSign);
      }

      return c12PacketUpdateSign;
   }
}
