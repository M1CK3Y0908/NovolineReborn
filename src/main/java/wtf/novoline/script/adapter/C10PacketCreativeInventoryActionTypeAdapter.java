package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C10PacketCreativeInventoryActionTypeAdapter extends PacketTypeAdapter<C10PacketCreativeInventoryAction> {
   protected void putPacketFields(ScriptValue scriptValue, C10PacketCreativeInventoryAction c10PacketCreativeInventoryAction) {
      scriptValue.put("slotId", c10PacketCreativeInventoryAction.slotId);
      scriptValue.putObject("stack", c10PacketCreativeInventoryAction.stack, ItemStack.class);
   }

   public C10PacketCreativeInventoryAction unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C10PacketCreativeInventoryAction c10PacketCreativeInventoryAction = new C10PacketCreativeInventoryAction();
      if (scriptValue.containsKey("slotId")) {
         c10PacketCreativeInventoryAction = this.apply(scriptValue, c10PacketCreativeInventoryAction);
      }

      return c10PacketCreativeInventoryAction;
   }

   public C10PacketCreativeInventoryActionTypeAdapter(TypeRegistry typeRegistry) {
      super(16, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C10PacketCreativeInventoryAction", typeRegistry);
   }

   public C10PacketCreativeInventoryAction apply(ScriptValue scriptValue, C10PacketCreativeInventoryAction c10PacketCreativeInventoryAction) {
      c10PacketCreativeInventoryAction.slotId = scriptValue.getInt("slotId");
      c10PacketCreativeInventoryAction.stack = (ItemStack)scriptValue.getScriptValue("stack").getJavaObject();
      return c10PacketCreativeInventoryAction;
   }
}
