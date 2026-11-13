package wtf.novoline.script.adapter;

import wtf.novoline.event.GamemodeUpdateEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class GamemodeUpdateEventTypeAdapter extends EventTypeAdapter<GamemodeUpdateEvent> {
   public GamemodeUpdateEventTypeAdapter(TypeRegistry typeRegistry) {
      super("GamemodeUpdateEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, GamemodeUpdateEvent gamemodeUpdateEvent) {
      scriptValue.put("newGamemode", gamemodeUpdateEvent.hypixelGameType);
   }
}
