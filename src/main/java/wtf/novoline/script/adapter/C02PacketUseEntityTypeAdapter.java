package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.Vec3;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C02PacketUseEntityTypeAdapter extends PacketTypeAdapter<C02PacketUseEntity> {
   protected void putPacketFields(ScriptValue scriptValue, C02PacketUseEntity c02PacketUseEntity) {
      scriptValue.put("entityId", c02PacketUseEntity.entityId);
      scriptValue.putEnum("action", c02PacketUseEntity.action);
      scriptValue.putObject("hitVec", c02PacketUseEntity.hitVec, Vec3.class);
   }

   public C02PacketUseEntity unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      if (scriptValue.containsKey("hitVec")) {
         return new C02PacketUseEntity((Entity)scriptValue.getScriptValue("entity").getJavaObject(), (Vec3)scriptValue.getScriptValue("hitVec").getJavaObject());
      } else {
         return scriptValue.containsKey("action")
            ? new C02PacketUseEntity((Entity)scriptValue.getScriptValue("entity").getJavaObject(), (Action)scriptValue.getEnum("action", Action.class))
            : new C02PacketUseEntity();
      }
   }

   public C02PacketUseEntity apply(ScriptValue scriptValue, C02PacketUseEntity c02PacketUseEntity) {
      c02PacketUseEntity.entityId = scriptValue.getInt("entityId");
      c02PacketUseEntity.action = (Action)scriptValue.getEnum("action", Action.class);
      c02PacketUseEntity.hitVec = (Vec3)scriptValue.getScriptValue("hitVec").getJavaObject();
      return c02PacketUseEntity;
   }

   public C02PacketUseEntityTypeAdapter(TypeRegistry typeRegistry) {
      super(1, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C02PacketUseEntity", typeRegistry);
   }
}
