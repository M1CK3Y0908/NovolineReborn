package wtf.novoline.script.adapter;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class EntityPlayerSPTypeAdapter extends AbstractTypeAdapter<EntityPlayerSP> {
   public EntityPlayerSP apply(ScriptValue scriptValue, EntityPlayerSP entityPlayerSP) {
      entityPlayerSP.movementInput = (MovementInput)scriptValue.getUnpacked("movementInput", MovementInput.class);
      return entityPlayerSP;
   }

   public ScriptValue put(ScriptValue scriptValue, EntityPlayerSP entityPlayerSP) {
      scriptValue.putObject("movementInput", entityPlayerSP.movementInput, MovementInput.class);
      return scriptValue;
   }

   public EntityPlayerSPTypeAdapter(TypeRegistry typeRegistry) {
      super("EntityPlayerSP", typeRegistry);
   }
}
