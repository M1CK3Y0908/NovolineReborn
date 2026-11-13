package wtf.novoline.script.adapter;

import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.script.ScriptFunctionUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.ScriptFunctionUtils.Component;

public class PlayerMoveEventTypeAdapter extends EventTypeAdapter<PlayerMoveEvent> {
   public PlayerMoveEvent apply(ScriptValue scriptValue, PlayerMoveEvent playerMoveEvent) {
      playerMoveEvent.x = scriptValue.getFloat("x");
      playerMoveEvent.y = scriptValue.getFloat("y");
      playerMoveEvent.z = scriptValue.getFloat("z");
      return playerMoveEvent;
   }

   public PlayerMoveEventTypeAdapter(TypeRegistry typeRegistry) {
      super("PlayerMoveEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, PlayerMoveEvent playerMoveEvent) {
      Component component = Component.createComponent(this, scriptValue, playerMoveEvent);
      scriptValue.put("x", playerMoveEvent.x);
      scriptValue.put("y", playerMoveEvent.y);
      scriptValue.put("z", playerMoveEvent.z);
      scriptValue.putConsumer("setSpeed", ScriptFunctionUtils.consumer(component, playerMoveEvent::setSpeed));
      scriptValue.putConsumer(
         "setSpeedCustom",
         ScriptFunctionUtils.consumer(
            component,
            object -> {
               ScriptValue fromObject = ScriptValue.createFromObject(object);
               playerMoveEvent.setSpeedCustom(
                  fromObject.getDouble("moveSpeed"), fromObject.getFloat("yaw"), fromObject.getFloat("moveForward"), fromObject.getFloat("moveStrafe"), fromObject.getInt("angle")
               );
            }
         )
      );
   }
}
