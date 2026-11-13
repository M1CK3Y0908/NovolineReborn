package wtf.novoline.script.adapter;

import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.event.RenderGameOverlayEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class EventTypeAdapter6 extends EventTypeAdapter<RenderGameOverlayEvent> {
   public EventTypeAdapter6() {
      super((String) null, (TypeRegistry) null);
   }

   protected void putEvent(ScriptValue scriptValue, RenderGameOverlayEvent renderGameOverlayEvent) {
      scriptValue.put("partialTicks", renderGameOverlayEvent.partialTicks);
      scriptValue.putObject("scaledResolution", renderGameOverlayEvent.scaledResolution, ScaledResolution.class);
   }
}
