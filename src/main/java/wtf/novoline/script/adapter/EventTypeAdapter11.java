package wtf.novoline.script.adapter;

import wtf.novoline.event.GameEndEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter11 extends EventTypeAdapter<GameEndEvent> {
   public EventTypeAdapter11(TypeRegistry typeRegistry) {
      super("GameEndEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, GameEndEvent gameEndEvent) {
      scriptValue.put("gameType", gameEndEvent.gameType);
      scriptValue.put("win", gameEndEvent.win);
   }
}
