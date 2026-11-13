package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C0BPacketEntityActionTypeAdapter extends PacketTypeAdapter<C0BPacketEntityAction> {
   protected void putPacketFields(ScriptValue scriptValue, C0BPacketEntityAction c0BPacketEntityAction) {
      scriptValue.putEnum("action", c0BPacketEntityAction.action);
      scriptValue.put("auxData", c0BPacketEntityAction.auxData);
      scriptValue.put("entityID", c0BPacketEntityAction.entityID);
   }

   public C0BPacketEntityAction unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      if (scriptValue.containsKey("auxData")) {
         return new C0BPacketEntityAction(
            (Entity)scriptValue.getScriptValue("entity").getJavaObject(), (Action)scriptValue.getEnum("action", Action.class), scriptValue.getInt("auxData")
         );
      } else {
         return scriptValue.containsKey("entity")
            ? new C0BPacketEntityAction((Entity)scriptValue.getScriptValue("entity").getJavaObject(), (Action)scriptValue.getEnum("action", Action.class))
            : new C0BPacketEntityAction();
      }
   }

   public C0BPacketEntityActionTypeAdapter(TypeRegistry typeRegistry) {
      super(11, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C0BPacketEntityAction", typeRegistry);
   }

   public C0BPacketEntityAction apply(ScriptValue scriptValue, C0BPacketEntityAction c0BPacketEntityAction) {
      c0BPacketEntityAction.action = (Action)scriptValue.getEnum("action", Action.class);
      c0BPacketEntityAction.auxData = scriptValue.getInt("auxData");
      c0BPacketEntityAction.entityID = scriptValue.getInt("entityID");
      return c0BPacketEntityAction;
   }
}
