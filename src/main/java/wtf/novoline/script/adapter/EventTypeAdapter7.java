package wtf.novoline.script.adapter;

import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EventTypeAdapter7 extends EventTypeAdapter<PreUpdateEvent> {
   public PreUpdateEvent apply(ScriptValue scriptValue, PreUpdateEvent preUpdateEvent) {
      preUpdateEvent.posX = scriptValue.getFloat("posX");
      preUpdateEvent.posY = scriptValue.getFloat("posY");
      preUpdateEvent.posZ = scriptValue.getFloat("posZ");
      preUpdateEvent.motionX = scriptValue.getFloat("motionX");
      preUpdateEvent.motionY = scriptValue.getFloat("motionY");
      preUpdateEvent.motionZ = scriptValue.getFloat("motionZ");
      preUpdateEvent.rotationYaw = scriptValue.getFloat("rotationYaw");
      preUpdateEvent.rotationPitch = scriptValue.getFloat("rotationPitch");
      preUpdateEvent.onGround = scriptValue.checkString("onGround");
      return preUpdateEvent;
   }

   protected void putEvent(ScriptValue scriptValue, PreUpdateEvent preUpdateEvent) {
      scriptValue.put("posX", preUpdateEvent.posX);
      scriptValue.put("posY", preUpdateEvent.posY);
      scriptValue.put("posZ", preUpdateEvent.posZ);
      scriptValue.put("motionX", preUpdateEvent.motionX);
      scriptValue.put("motionY", preUpdateEvent.motionY);
      scriptValue.put("motionZ", preUpdateEvent.motionZ);
      scriptValue.put("rotationYaw", preUpdateEvent.rotationYaw);
      scriptValue.put("rotationPitch", preUpdateEvent.rotationPitch);
      scriptValue.put("onGround", preUpdateEvent.onGround);
   }

   public EventTypeAdapter7(TypeRegistry typeRegistry) {
      super("PreUpdateEvent", typeRegistry);
   }
}
