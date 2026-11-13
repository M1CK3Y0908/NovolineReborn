package wtf.novoline.script.adapter;

import net.minecraft.entity.player.PlayerCapabilities;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class PlayerCapabilitiesTypeAdapter extends AbstractTypeAdapter<PlayerCapabilities> {
   public PlayerCapabilitiesTypeAdapter(TypeRegistry typeRegistry) {
      super("PlayerCapabilities", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, PlayerCapabilities playerCapabilities) {
      scriptValue.put("allowEdit", playerCapabilities.allowEdit);
      scriptValue.put("allowFlying", playerCapabilities.allowFlying);
      scriptValue.put("flySpeed", playerCapabilities.flySpeed);
      scriptValue.put("walkSpeed", playerCapabilities.walkSpeed);
      scriptValue.put("disableDamage", playerCapabilities.disableDamage);
      scriptValue.put("isCreativeMode", playerCapabilities.isCreativeMode);
      scriptValue.put("isFlying", playerCapabilities.isFlying);
      return scriptValue;
   }

   public PlayerCapabilities apply(ScriptValue scriptValue, PlayerCapabilities playerCapabilities) {
      playerCapabilities.allowEdit = scriptValue.checkString("allowEdit");
      playerCapabilities.allowFlying = scriptValue.checkString("allowFlying");
      playerCapabilities.flySpeed = scriptValue.getFloat("flySpeed");
      playerCapabilities.walkSpeed = scriptValue.getFloat("walkSpeed");
      playerCapabilities.disableDamage = scriptValue.checkString("disableDamage");
      playerCapabilities.isCreativeMode = scriptValue.checkString("isCreativeMode");
      playerCapabilities.isFlying = scriptValue.checkString("isFlying");
      return playerCapabilities;
   }
}
