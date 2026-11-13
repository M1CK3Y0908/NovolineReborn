package wtf.novoline.script.adapter;

import wtf.novoline.event.Event;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter12<T extends Event> extends EventTypeAdapter<T> {
   @Override
   protected void putEvent(ScriptValue scriptValue, Event event2) {
   }

   public EventTypeAdapter12(String text, TypeRegistry typeRegistry) {
      super(text, typeRegistry);
   }
}
