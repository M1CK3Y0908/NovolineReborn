package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C07PacketPlayerDiggingTypeAdapter extends PacketTypeAdapter<C07PacketPlayerDigging> {
   public C07PacketPlayerDigging unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("facing")
         ? new C07PacketPlayerDigging(
            (Action)scriptValue.getEnum("status", Action.class),
            (BlockPos)scriptValue.getScriptValue("position").getJavaObject(),
            (EnumFacing)scriptValue.getScriptValue("facing").getJavaObject()
         )
         : new C07PacketPlayerDigging();
   }

   protected void putPacketFields(ScriptValue scriptValue, C07PacketPlayerDigging c07PacketPlayerDigging) {
      scriptValue.putObject("facing", c07PacketPlayerDigging.facing, EnumFacing.class);
      scriptValue.putObject("position", c07PacketPlayerDigging.position, BlockPos.class);
      scriptValue.putEnum("status", c07PacketPlayerDigging.status);
   }

   public C07PacketPlayerDiggingTypeAdapter(TypeRegistry typeRegistry) {
      super(7, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C07PacketPlayerDigging", typeRegistry);
   }

   public C07PacketPlayerDigging apply(ScriptValue scriptValue, C07PacketPlayerDigging c07PacketPlayerDigging) {
      c07PacketPlayerDigging.facing = (EnumFacing)scriptValue.getScriptValue("facing").getJavaObject();
      c07PacketPlayerDigging.position = (BlockPos)scriptValue.getScriptValue("position").getJavaObject();
      c07PacketPlayerDigging.status = (Action)scriptValue.getEnum("status", Action.class);
      return c07PacketPlayerDigging;
   }
}
