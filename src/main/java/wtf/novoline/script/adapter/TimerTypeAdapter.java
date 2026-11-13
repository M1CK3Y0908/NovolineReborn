package wtf.novoline.script.adapter;

import net.minecraft.util.Timer;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class TimerTypeAdapter extends AbstractTypeAdapter<Timer> {
   public Timer apply(ScriptValue scriptValue, Timer timer) {
      timer.timerSpeed = scriptValue.getInt("timerSpeed");
      timer.renderPartialTicks = scriptValue.getFloat("renderPartialTicks");
      timer.elapsedPartialTicks = scriptValue.getFloat("elapsedPartialTicks");
      timer.elapsedTicks = scriptValue.getInt("elapsedTicks");
      return timer;
   }

   public ScriptValue put(ScriptValue scriptValue, Timer timer) {
      scriptValue.putRunnable("updateTimer", timer::updateTimer);
      scriptValue.put("timerSpeed", timer.timerSpeed);
      scriptValue.put("renderPartialTicks", timer.renderPartialTicks);
      scriptValue.put("elapsedPartialTicks", timer.elapsedPartialTicks);
      scriptValue.put("elapsedTicks", timer.elapsedTicks);
      return scriptValue;
   }

   public TimerTypeAdapter(TypeRegistry typeRegistry) {
      super("Timer", typeRegistry);
   }
}
