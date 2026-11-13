package wtf.novoline.script.adapter;

import wtf.novoline.event.SlowdownEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class SlowdownEventTypeAdapter extends EventTypeAdapter<SlowdownEvent> {
   public SlowdownEvent apply(ScriptValue scriptValue, SlowdownEvent slowdownEvent) {
      slowdownEvent.forwardMultiplier = scriptValue.getFloat("forwardMultiplier");
      slowdownEvent.strafeMultiplier = scriptValue.getFloat("strafeMultiplier");
      return slowdownEvent;
   }

   protected void putEvent(ScriptValue scriptValue, SlowdownEvent slowdownEvent) {
      scriptValue.put("forwardMultiplier", slowdownEvent.forwardMultiplier);
      scriptValue.put("strafeMultiplier", slowdownEvent.strafeMultiplier);
   }

   public SlowdownEventTypeAdapter(TypeRegistry typeRegistry) {
      super("SlowdownEvent", typeRegistry);
   }
}
