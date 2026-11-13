package wtf.novoline.script.adapter;

import wtf.novoline.event.GameStartEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter2 extends EventTypeAdapter<GameStartEvent> {
   protected void putEvent(ScriptValue scriptValue, GameStartEvent gameStartEvent) {
      scriptValue.putEnum("gamemode", gameStartEvent.hypixelGameType);
   }

   public EventTypeAdapter2(TypeRegistry typeRegistry) {
      super("GameStartEvent", typeRegistry);
   }
}
