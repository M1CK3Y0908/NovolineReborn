package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C0FPacketConfirmTransactionTypeAdapter extends PacketTypeAdapter<C0FPacketConfirmTransaction> {
   public C0FPacketConfirmTransactionTypeAdapter(TypeRegistry typeRegistry) {
      super(15, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C0FPacketMapper", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, C0FPacketConfirmTransaction c0FPacketConfirmTransaction) {
      scriptValue.put("accepted", c0FPacketConfirmTransaction.accepted);
      scriptValue.put("uid", c0FPacketConfirmTransaction.uid);
      scriptValue.put("windowId", c0FPacketConfirmTransaction.windowId);
   }

   public C0FPacketConfirmTransaction apply(ScriptValue scriptValue, C0FPacketConfirmTransaction c0FPacketConfirmTransaction) {
      c0FPacketConfirmTransaction.accepted = scriptValue.checkString("accepted");
      c0FPacketConfirmTransaction.uid = scriptValue.getShort("uid");
      c0FPacketConfirmTransaction.windowId = scriptValue.getInt("windowId");
      return c0FPacketConfirmTransaction;
   }

   public C0FPacketConfirmTransaction unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("accepted") ? this.apply(scriptValue, new C0FPacketConfirmTransaction()) : new C0FPacketConfirmTransaction();
   }
}
