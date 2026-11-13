package wtf.novoline.script.adapter;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.EnumFacing.AxisDirection;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class AxisDirectionTypeAdapter extends AbstractTypeAdapter<AxisDirection> {
   private static final Map<AxisDirection, Map<String, Object>> packedCache = new HashMap<>();

   public AxisDirectionTypeAdapter(TypeRegistry typeRegistry) {
      super("AxisDirection", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, AxisDirection axisDirection2) {
      scriptValue.putSupplier("getOffset", axisDirection2::getOffset);
      scriptValue.putSupplier("name", axisDirection2::name);
      return scriptValue;
   }

   public Map<String, Object> pack(AxisDirection axisDirection2) {
      return packedCache.computeIfAbsent(axisDirection2, axisDirection -> super.pack(axisDirection2));
   }
}
