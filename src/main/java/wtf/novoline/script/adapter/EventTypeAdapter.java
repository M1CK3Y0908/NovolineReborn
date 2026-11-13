package wtf.novoline.script.adapter;

import wtf.novoline.event.Event;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public abstract class EventTypeAdapter<T extends Event> extends AbstractTypeAdapter<T> {
   protected abstract void putEvent(ScriptValue scriptValue, T value);

   public EventTypeAdapter(String text, TypeRegistry typeRegistry) {
      super(text, typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, Event event2) {
      scriptValue.putRunnable("cancel", event2::cancel);
      scriptValue.putSupplier("isCancelled", event2::isCancelled);
      this.putEvent(scriptValue, (T)event2);
      return scriptValue;
   }
}
