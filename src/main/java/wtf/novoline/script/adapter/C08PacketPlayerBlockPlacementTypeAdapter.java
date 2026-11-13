package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C08PacketPlayerBlockPlacementTypeAdapter extends PacketTypeAdapter<C08PacketPlayerBlockPlacement> {
   public C08PacketPlayerBlockPlacementTypeAdapter(TypeRegistry typeRegistry) {
      super(8, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C08PacketPlayerBlockPlacement", typeRegistry);
   }

   public C08PacketPlayerBlockPlacement apply(ScriptValue scriptValue, C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement) {
      c08PacketPlayerBlockPlacement.facingX = scriptValue.getFloat("facingX");
      c08PacketPlayerBlockPlacement.facingY = scriptValue.getFloat("facingY");
      c08PacketPlayerBlockPlacement.facingZ = scriptValue.getFloat("facingZ");
      c08PacketPlayerBlockPlacement.placedBlockDirection = scriptValue.getInt("placedBlockDirection");
      c08PacketPlayerBlockPlacement.position = (BlockPos)scriptValue.getScriptValue("position").getJavaObject();
      c08PacketPlayerBlockPlacement.stack = (ItemStack)scriptValue.getScriptValue("stack").getJavaObject();
      return c08PacketPlayerBlockPlacement;
   }

   protected void putPacketFields(ScriptValue scriptValue, C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement) {
      scriptValue.put("facingX", c08PacketPlayerBlockPlacement.facingX);
      scriptValue.put("facingY", c08PacketPlayerBlockPlacement.facingY);
      scriptValue.put("facingZ", c08PacketPlayerBlockPlacement.facingZ);
      scriptValue.put("placedBlockDirection", c08PacketPlayerBlockPlacement.placedBlockDirection);
      scriptValue.putObject("position", c08PacketPlayerBlockPlacement.position, BlockPos.class);
      scriptValue.putObject("stack", c08PacketPlayerBlockPlacement.stack, ItemStack.class);
   }

   public C08PacketPlayerBlockPlacement unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      if (scriptValue.containsKey("position")) {
         return new C08PacketPlayerBlockPlacement(
            (BlockPos)scriptValue.getScriptValue("position").getJavaObject(),
            scriptValue.getInt("placedBlockDirection"),
            (ItemStack)scriptValue.getScriptValue("stack").getJavaObject(),
            scriptValue.getFloat("facingX"),
            scriptValue.getFloat("facingY"),
            scriptValue.getFloat("facingZ")
         );
      } else {
         return scriptValue.containsKey("stack")
            ? new C08PacketPlayerBlockPlacement((ItemStack)scriptValue.getScriptValue("stack").getJavaObject())
            : new C08PacketPlayerBlockPlacement();
      }
   }
}
