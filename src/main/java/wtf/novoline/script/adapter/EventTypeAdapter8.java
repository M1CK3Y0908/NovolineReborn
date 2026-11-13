package wtf.novoline.script.adapter;

import wtf.novoline.event.DeviceButtonDownEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter8 extends EventTypeAdapter<DeviceButtonDownEvent> {
   public void putEvent(ScriptValue scriptValue, DeviceButtonDownEvent deviceButtonDownEvent) {
      scriptValue.put("keyId", deviceButtonDownEvent.keyId);
      scriptValue.putEnum("device", deviceButtonDownEvent.mouseType);
   }

   public EventTypeAdapter8(TypeRegistry typeRegistry) {
      super("DeviceButtonDownEvent", typeRegistry);
   }
}
