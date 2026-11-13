package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C17PacketCustomPayloadTypeAdapter extends PacketTypeAdapter<C17PacketCustomPayload> {
   public static Class eraseReferenceType(Class clazz) {
      return !clazz.isPrimitive() ? Object.class : clazz;
   }

   public C17PacketCustomPayloadTypeAdapter(TypeRegistry typeRegistry) {
      super(23, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C17PacketCustomPayload", typeRegistry);
      typeRegistry.put(PacketBuffer.class, new PacketBufferTypeAdapter(typeRegistry));
   }

   public C17PacketCustomPayload apply(ScriptValue scriptValue, C17PacketCustomPayload c17PacketCustomPayload) {
      c17PacketCustomPayload.data = (PacketBuffer)scriptValue.getScriptValue("data").getJavaObject();
      c17PacketCustomPayload.channel = scriptValue.getString("channel");
      return c17PacketCustomPayload;
   }

   public C17PacketCustomPayload unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C17PacketCustomPayload c17PacketCustomPayload = new C17PacketCustomPayload();
      if (scriptValue.containsKey("channel")) {
         c17PacketCustomPayload = this.apply(scriptValue, c17PacketCustomPayload);
      }

      return c17PacketCustomPayload;
   }

   protected void putPacketFields(ScriptValue scriptValue, C17PacketCustomPayload c17PacketCustomPayload) {
      scriptValue.putObject("data", c17PacketCustomPayload.data, PacketBuffer.class);
      scriptValue.put("channel", c17PacketCustomPayload.channel);
   }
}
