package wtf.novoline.script.adapter;

import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class RenderWorldEventTypeAdapter extends EventTypeAdapter<RenderWorldEvent> {
   protected void putEvent(ScriptValue scriptValue, RenderWorldEvent renderWorldEvent) {
      scriptValue.put("partialTicks", renderWorldEvent.partialTicks);
   }

   public RenderWorldEventTypeAdapter(TypeRegistry typeRegistry) {
      super("RenderWorldEvent", typeRegistry);
   }
}
