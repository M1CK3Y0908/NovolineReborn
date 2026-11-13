package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C0EPacketClickWindowTypeAdapter extends PacketTypeAdapter<C0EPacketClickWindow> {
   public C0EPacketClickWindowTypeAdapter(TypeRegistry typeRegistry) {
      super(14, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C0EPacketClickWindow", typeRegistry);
   }

   public C0EPacketClickWindow unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("clickedItem") ? this.apply(scriptValue, new C0EPacketClickWindow()) : new C0EPacketClickWindow();
   }

   public C0EPacketClickWindow apply(ScriptValue scriptValue, C0EPacketClickWindow c0EPacketClickWindow) {
      c0EPacketClickWindow.clickedItem = (ItemStack)scriptValue.getScriptValue("clickedItem").getJavaObject();
      c0EPacketClickWindow.actionNumber = scriptValue.getShort("actionNumber");
      c0EPacketClickWindow.slotId = scriptValue.getInt("slotId");
      c0EPacketClickWindow.mode = scriptValue.getInt("mode");
      c0EPacketClickWindow.usedButton = scriptValue.getInt("usedButton");
      c0EPacketClickWindow.windowId = scriptValue.getInt("windowId");
      return c0EPacketClickWindow;
   }

   protected void putPacketFields(ScriptValue scriptValue, C0EPacketClickWindow c0EPacketClickWindow) {
      scriptValue.putObject("clickedItem", c0EPacketClickWindow.clickedItem, ItemStack.class);
      scriptValue.put("actionNumber", c0EPacketClickWindow.actionNumber);
      scriptValue.put("slotId", c0EPacketClickWindow.slotId);
      scriptValue.put("mode", c0EPacketClickWindow.mode);
      scriptValue.put("usedButton", c0EPacketClickWindow.usedButton);
      scriptValue.put("windowId", c0EPacketClickWindow.windowId);
   }
}
