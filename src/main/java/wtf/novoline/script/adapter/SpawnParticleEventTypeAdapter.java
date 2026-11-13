package wtf.novoline.script.adapter;

import wtf.novoline.event.SpawnParticleEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class SpawnParticleEventTypeAdapter extends EventTypeAdapter<SpawnParticleEvent> {
   public SpawnParticleEventTypeAdapter() {
      super((String) null, (TypeRegistry) null);
   }

   public SpawnParticleEvent apply(ScriptValue scriptValue, SpawnParticleEvent spawnParticleEvent) {
      spawnParticleEvent.id = scriptValue.getInt("id");
      return spawnParticleEvent;
   }

   protected void putEvent(ScriptValue scriptValue, SpawnParticleEvent spawnParticleEvent) {
      scriptValue.put("id", spawnParticleEvent.id);
   }
}
