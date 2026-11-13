package wtf.novoline.script.adapter;

import net.minecraft.util.MovementInput;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class MovementInputTypeAdapter extends AbstractTypeAdapter<MovementInput> {
   public ScriptValue put(ScriptValue scriptValue, MovementInput movementInput) {
      scriptValue.put("moveStrafe", movementInput.moveStrafe);
      scriptValue.put("moveForward", movementInput.moveForward);
      scriptValue.put("jump", movementInput.jump);
      scriptValue.put("sneak", movementInput.sneak);
      return scriptValue;
   }

   public MovementInputTypeAdapter(TypeRegistry typeRegistry) {
      super("MovementInput", typeRegistry);
   }

   public MovementInput apply(ScriptValue scriptValue, MovementInput movementInput) {
      movementInput.moveStrafe = scriptValue.getFloat("moveStrafe");
      movementInput.moveForward = scriptValue.getFloat("moveForward");
      movementInput.jump = scriptValue.checkString("jump");
      movementInput.sneak = scriptValue.checkString("sneak");
      return movementInput;
   }
}
