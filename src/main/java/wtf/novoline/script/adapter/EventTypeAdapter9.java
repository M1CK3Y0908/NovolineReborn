package wtf.novoline.script.adapter;

import net.minecraft.entity.player.EntityPlayer;
import wtf.novoline.event.PlayerKillEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter9 extends EventTypeAdapter<PlayerKillEvent> {
   public EventTypeAdapter9(TypeRegistry typeRegistry) {
      super("PlayerKillEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, PlayerKillEvent playerKillEvent) {
      scriptValue.putObject("entity", playerKillEvent.entityPlayer, EntityPlayer.class);
   }
}
