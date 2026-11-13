package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C11PacketEnchantItemTypeAdapter extends PacketTypeAdapter<C11PacketEnchantItem> {
   public C11PacketEnchantItem apply(ScriptValue scriptValue, C11PacketEnchantItem c11PacketEnchantItem) {
      c11PacketEnchantItem.windowId = scriptValue.getInt("windowId");
      c11PacketEnchantItem.button = scriptValue.getInt("button");
      return c11PacketEnchantItem;
   }

   public C11PacketEnchantItemTypeAdapter(TypeRegistry typeRegistry) {
      super(17, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C11PacketEnchantItem", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, C11PacketEnchantItem c11PacketEnchantItem) {
      scriptValue.put("windowId", c11PacketEnchantItem.windowId);
      scriptValue.put("button", c11PacketEnchantItem.button);
   }

   public C11PacketEnchantItem unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C11PacketEnchantItem c11PacketEnchantItem = new C11PacketEnchantItem();
      if (scriptValue.containsKey("windowId")) {
         c11PacketEnchantItem = this.apply(scriptValue, c11PacketEnchantItem);
      }

      return c11PacketEnchantItem;
   }
}
