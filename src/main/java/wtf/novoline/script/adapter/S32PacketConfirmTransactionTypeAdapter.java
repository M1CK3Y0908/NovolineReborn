package wtf.novoline.script.adapter;

import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class S32PacketConfirmTransactionTypeAdapter extends PacketTypeAdapter<S32PacketConfirmTransaction> {
   public S32PacketConfirmTransactionTypeAdapter(TypeRegistry typeRegistry) {
      super(50, HandshakingType.PLAY, ClientboundType.CLIENTBOUND, "S32PacketConfirmTransaction", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, S32PacketConfirmTransaction s32PacketConfirmTransaction) {
      scriptValue.putSupplier("getWindowId", s32PacketConfirmTransaction::getWindowId);
      scriptValue.putConsumer("setWindowId", s32PacketConfirmTransaction::setWindowId);
      scriptValue.putSupplier("getActionNumber", s32PacketConfirmTransaction::getActionNumber);
      scriptValue.putConsumer("setActionNumber", s32PacketConfirmTransaction::setActionNumber);
      scriptValue.putSupplier("isAccepted", s32PacketConfirmTransaction::isAccepted);
      scriptValue.putConsumer("setAccepted", s32PacketConfirmTransaction::setAccepted);
   }
}
