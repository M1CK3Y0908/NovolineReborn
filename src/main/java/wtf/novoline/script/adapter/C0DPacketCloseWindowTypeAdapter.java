package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C0DPacketCloseWindowTypeAdapter extends PacketTypeAdapter<C0DPacketCloseWindow> {
   public C0DPacketCloseWindow apply(ScriptValue scriptValue, C0DPacketCloseWindow c0DPacketCloseWindow) {
      c0DPacketCloseWindow.windowId = scriptValue.getInt("windowId");
      return c0DPacketCloseWindow;
   }

   protected void putPacketFields(ScriptValue scriptValue, C0DPacketCloseWindow c0DPacketCloseWindow) {
      scriptValue.put("windowId", c0DPacketCloseWindow.windowId);
   }

   public C0DPacketCloseWindowTypeAdapter(TypeRegistry typeRegistry) {
      super(13, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C0DPacketCloseWindow", typeRegistry);
   }

   public C0DPacketCloseWindow unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("windowId") ? new C0DPacketCloseWindow(scriptValue.getInt("windowId")) : new C0DPacketCloseWindow();
   }
}
