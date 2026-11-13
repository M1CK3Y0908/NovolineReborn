package wtf.novoline.script.adapter;

import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter10 extends EventTypeAdapter<RenderScreenEvent> {
   public EventTypeAdapter10(TypeRegistry typeRegistry) {
      super("RenderScreenEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, RenderScreenEvent renderScreenEvent) {
      scriptValue.put("partialTicks", renderScreenEvent.partialTicks);
      scriptValue.putObject("scaledResolution", renderScreenEvent.scaledResolution, ScaledResolution.class);
   }
}
