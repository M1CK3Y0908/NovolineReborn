package wtf.novoline.script.adapter;

import wtf.novoline.event.SyncCurrentSlotEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class SyncCurrentSlotEventTypeAdapter extends EventTypeAdapter<SyncCurrentSlotEvent> {
   protected void putEvent(ScriptValue scriptValue, SyncCurrentSlotEvent syncCurrentSlotEvent) {
      scriptValue.put("clientSlot", syncCurrentSlotEvent.clientSlot);
      scriptValue.put("serverSlot", syncCurrentSlotEvent.serverSlot);
   }

   public SyncCurrentSlotEventTypeAdapter(TypeRegistry typeRegistry) {
      super("SyncCurrentSlotEvent", typeRegistry);
   }

   public SyncCurrentSlotEvent apply(ScriptValue scriptValue, SyncCurrentSlotEvent syncCurrentSlotEvent) {
      syncCurrentSlotEvent.clientSlot = scriptValue.getInt("clientSlot");
      syncCurrentSlotEvent.serverSlot = scriptValue.getInt("serverSlot");
      return syncCurrentSlotEvent;
   }
}
