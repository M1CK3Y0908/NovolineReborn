package wtf.novoline.script.adapter;

import net.minecraft.util.IChatComponent;
import wtf.novoline.event.ServerDisconnectEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class ServerDisconnectEventTypeAdapter extends EventTypeAdapter<ServerDisconnectEvent> {
   public ServerDisconnectEventTypeAdapter(TypeRegistry typeRegistry) {
      super("ServerDisconnectEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, ServerDisconnectEvent serverDisconnectEvent) {
      scriptValue.putObject("message", serverDisconnectEvent.getMessage(), IChatComponent.class);
   }
}
