package wtf.novoline.script.adapter;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class PlaneTypeAdapter extends AbstractTypeAdapter<Plane> {
   private static final Map<Plane, Map<String, Object>> packedCache = new HashMap<>();

   public PlaneTypeAdapter(TypeRegistry typeRegistry) {
      super("Plane", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, Plane plane2) {
      scriptValue.putSupplier("name", plane2::name);
      scriptValue.putSupplier("facings", MappingUtils.mappingArraySupplier(EnumFacing.class, plane2::facings));
      scriptValue.putFunction("equals", MappingUtils.m2oMappingFunction(Plane.class, plane2::equals));
      scriptValue.putFunction("apply", MappingUtils.m2oMappingFunction(EnumFacing.class, plane2::apply));
      return scriptValue;
   }

   public Map<String, Object> pack(Plane plane2) {
      return packedCache.computeIfAbsent(plane2, plane -> super.pack(plane2));
   }
}
