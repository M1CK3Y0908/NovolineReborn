package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.network.play.client.C15PacketClientSettings;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C15PacketClientSettingsTypeAdapter extends PacketTypeAdapter<C15PacketClientSettings> {
   public C15PacketClientSettings unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      C15PacketClientSettings c15PacketClientSettings = new C15PacketClientSettings();
      if (scriptValue.containsKey("chatVisibility")) {
         c15PacketClientSettings = this.apply(scriptValue, c15PacketClientSettings);
      }

      return c15PacketClientSettings;
   }

   public C15PacketClientSettingsTypeAdapter(TypeRegistry typeRegistry) {
      super(21, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C15PacketClientSetting", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, C15PacketClientSettings c15PacketClientSettings) {
      scriptValue.putEnum("chatVisibility", c15PacketClientSettings.chatVisibility);
      scriptValue.put("enableColors", c15PacketClientSettings.enableColors);
      scriptValue.put("lang", c15PacketClientSettings.lang);
      scriptValue.put("modelPartFlags", c15PacketClientSettings.modelPartFlags);
      scriptValue.put("view", c15PacketClientSettings.view);
   }

   public C15PacketClientSettings apply(ScriptValue scriptValue, C15PacketClientSettings c15PacketClientSettings) {
      c15PacketClientSettings.chatVisibility = (EnumChatVisibility)scriptValue.getEnum("chatVisibility", EnumChatVisibility.class);
      c15PacketClientSettings.enableColors = scriptValue.checkString("enableColors");
      c15PacketClientSettings.lang = scriptValue.getString("lang");
      c15PacketClientSettings.modelPartFlags = scriptValue.getInt("modelPartFlags");
      c15PacketClientSettings.view = scriptValue.getInt("view");
      return c15PacketClientSettings;
   }
}
