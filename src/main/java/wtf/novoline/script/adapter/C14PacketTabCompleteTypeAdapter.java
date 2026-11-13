package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C14PacketTabCompleteTypeAdapter extends PacketTypeAdapter<C14PacketTabComplete> {
   public C14PacketTabComplete unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      if (scriptValue.containsKey("target")) {
         return new C14PacketTabComplete(scriptValue.getString("msg"), (BlockPos)scriptValue.getScriptValue("target").getJavaObject());
      } else {
         return scriptValue.containsKey("msg") ? new C14PacketTabComplete(scriptValue.getString("msg")) : new C14PacketTabComplete();
      }
   }

   public C14PacketTabCompleteTypeAdapter(TypeRegistry typeRegistry) {
      super(20, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C14PacketTabComplete", typeRegistry);
   }

   public C14PacketTabComplete apply(ScriptValue scriptValue, C14PacketTabComplete c14PacketTabComplete) {
      c14PacketTabComplete.message = scriptValue.getString("message");
      c14PacketTabComplete.targetBlock = (BlockPos)scriptValue.getScriptValue("targetBlock").getJavaObject();
      return c14PacketTabComplete;
   }

   protected void putPacketFields(ScriptValue scriptValue, C14PacketTabComplete c14PacketTabComplete) {
      scriptValue.put("message", c14PacketTabComplete.message);
      scriptValue.putObject("targetBlock", c14PacketTabComplete.targetBlock, BlockPos.class);
   }
}
