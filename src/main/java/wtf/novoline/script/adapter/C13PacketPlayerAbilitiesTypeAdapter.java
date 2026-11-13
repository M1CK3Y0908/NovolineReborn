package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C13PacketPlayerAbilitiesTypeAdapter extends PacketTypeAdapter<C13PacketPlayerAbilities> {
   public C13PacketPlayerAbilities apply(ScriptValue scriptValue, C13PacketPlayerAbilities c13PacketPlayerAbilities) {
      c13PacketPlayerAbilities.allowFlying = scriptValue.checkString("allowFlying");
      c13PacketPlayerAbilities.flying = scriptValue.checkString("flying");
      c13PacketPlayerAbilities.creativeMode = scriptValue.checkString("creativeMode");
      c13PacketPlayerAbilities.flySpeed = scriptValue.getFloat("flySpeed");
      c13PacketPlayerAbilities.invulnerable = scriptValue.checkString("invulnerable");
      c13PacketPlayerAbilities.walkSpeed = scriptValue.getFloat("walkSpeed");
      return c13PacketPlayerAbilities;
   }

   public C13PacketPlayerAbilitiesTypeAdapter(TypeRegistry typeRegistry) {
      super(19, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C13PacketPlayAbilities", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, C13PacketPlayerAbilities c13PacketPlayerAbilities) {
      scriptValue.put("allowFlying", c13PacketPlayerAbilities.allowFlying);
      scriptValue.put("flying", c13PacketPlayerAbilities.flying);
      scriptValue.put("creativeMode", c13PacketPlayerAbilities.creativeMode);
      scriptValue.put("flySpeed", c13PacketPlayerAbilities.flySpeed);
      scriptValue.put("invulnerable", c13PacketPlayerAbilities.invulnerable);
      scriptValue.put("walkSpeed", c13PacketPlayerAbilities.walkSpeed);
   }

   public C13PacketPlayerAbilities unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("capabilities")
         ? new C13PacketPlayerAbilities((PlayerCapabilities)scriptValue.getScriptValue("capabilities").getJavaObject())
         : new C13PacketPlayerAbilities();
   }
}
