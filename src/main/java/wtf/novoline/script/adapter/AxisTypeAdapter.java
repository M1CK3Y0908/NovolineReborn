package wtf.novoline.script.adapter;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class AxisTypeAdapter extends AbstractTypeAdapter<Axis> {
   private static final Map<Axis, Map<String, Object>> packedCache = new HashMap<>();

   public AxisTypeAdapter(TypeRegistry typeRegistry) {
      super("Axis", typeRegistry);
   }

   public Map<String, Object> pack(Axis axis2) {
      return packedCache.computeIfAbsent(axis2, axis -> super.pack(axis2));
   }

   public ScriptValue put(ScriptValue scriptValue, Axis axis2) {
      scriptValue.putFunction("apply", MappingUtils.m2oMappingFunction(EnumFacing.class, axis2::apply));
      scriptValue.putSupplier("getPlane", MappingUtils.mappingSupplier(Plane.class, axis2::getPlane));
      scriptValue.putSupplier("getName", axis2::getName);
      scriptValue.putSupplier("getName2", axis2::getName2);
      scriptValue.putSupplier("isHorizontal", axis2::isHorizontal);
      scriptValue.putSupplier("isVertical", axis2::isVertical);
      return scriptValue;
   }
}
