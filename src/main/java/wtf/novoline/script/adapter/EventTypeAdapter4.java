package wtf.novoline.script.adapter;

import wtf.novoline.event.ServerConnectEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter4 extends EventTypeAdapter<ServerConnectEvent> {
   public EventTypeAdapter4(TypeRegistry typeRegistry) {
      super("ServerConnectEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, ServerConnectEvent serverConnectEvent) {
      scriptValue.put("connectingHostname", serverConnectEvent.connectingHostname);
      scriptValue.put("connectingIp", serverConnectEvent.connectingIp);
   }
}
