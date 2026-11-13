package wtf.novoline.scripting;

import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import wtf.novoline.scripting.ScriptEventHandler.Helper;

public class ScriptEventHandlerList extends ArrayList<ScriptEventHandler> {
   public List<ScriptEventHandler> findExactByScript(String text, Helper helper) {
      return this.stream()
         .filter(scriptEventHandler -> scriptEventHandler.getScript().getInternalId().equals(text))
         .filter(scriptEventHandler -> scriptEventHandler.getEventId().equals(helper))
         .toList();
   }

   public List<ScriptEventHandler> findAllEnabledByEvent(Helper helper) {
      return this.stream().filter(scriptEventHandler -> scriptEventHandler.getModule().isEnabled()).filter(scriptEventHandler -> scriptEventHandler.getEventId().equals(helper)).toList();
   }

   public List<ScriptEventHandler> sortByPriority(List<ScriptEventHandler> list) {
      return Ordering.from(Comparator.comparingInt(ScriptEventHandler::getPriority)).sortedCopy(list);
   }
}
