package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C0CPacketInput;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C0CPacketInputTypeAdapter extends PacketTypeAdapter<C0CPacketInput> {
   public C0CPacketInputTypeAdapter(TypeRegistry typeRegistry) {
      super(12, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C0CPacketInput", typeRegistry);
   }

   public C0CPacketInput apply(ScriptValue scriptValue, C0CPacketInput c0CPacketInput) {
      c0CPacketInput.forwardSpeed = scriptValue.getFloat("forwardSpeed");
      c0CPacketInput.strafeSpeed = scriptValue.getFloat("strafeSpeed");
      c0CPacketInput.jumping = scriptValue.checkString("jumping");
      c0CPacketInput.sneaking = scriptValue.checkString("sneaking");
      return c0CPacketInput;
   }

   public ScriptValue put(ScriptValue scriptValue, C0CPacketInput c0CPacketInput) {
      scriptValue.put("forwardSpeed", c0CPacketInput.forwardSpeed);
      scriptValue.put("strafeSpeed", c0CPacketInput.strafeSpeed);
      scriptValue.put("jumping", c0CPacketInput.jumping);
      scriptValue.put("sneaking", c0CPacketInput.sneaking);
      return scriptValue;
   }

   public C0CPacketInput unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("strafeSpeed")
         ? new C0CPacketInput(scriptValue.getFloat("strafeSpeed"), scriptValue.getFloat("forwardSpeed"), scriptValue.checkString("jumping"), scriptValue.checkString("sneaking"))
         : new C0CPacketInput();
   }
}
