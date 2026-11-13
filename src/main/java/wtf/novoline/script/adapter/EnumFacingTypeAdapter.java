package wtf.novoline.script.adapter;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumFacing.Plane;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class EnumFacingTypeAdapter extends AbstractTypeAdapter<EnumFacing> {
   private static final Map<EnumFacing, Map<String, Object>> packedCache = new HashMap<>();

   public ScriptValue put(ScriptValue scriptValue, EnumFacing enumFacing2) {
      scriptValue.putSupplier("getIndex", enumFacing2::getIndex);
      scriptValue.putSupplier("getName", enumFacing2::getName);
      scriptValue.putSupplier("getHorizontalIndex", enumFacing2::getHorizontalIndex);
      scriptValue.putSupplier("getFrontOffsetX", enumFacing2::getFrontOffsetX);
      scriptValue.putSupplier("getFrontOffsetZ", enumFacing2::getFrontOffsetZ);
      scriptValue.putSupplier("getAxis", MappingUtils.mappingSupplier(Axis.class, enumFacing2::getAxis));
      scriptValue.putSupplier("getAxisDirection", MappingUtils.mappingSupplier(AxisDirection.class, enumFacing2::getAxisDirection));
      scriptValue.putSupplier("getOpposite", MappingUtils.mappingSupplier(EnumFacing.class, enumFacing2::getOpposite));
      scriptValue.putSupplier("rotateY", MappingUtils.mappingSupplier(EnumFacing.class, enumFacing2::rotateY));
      scriptValue.putSupplier("rotateYCCW", MappingUtils.mappingSupplier(EnumFacing.class, enumFacing2::rotateYCCW));
      scriptValue.putSupplier("getDirectionVec", MappingUtils.mappingSupplier(Vec3i.class, enumFacing2::getDirectionVec));
      scriptValue.putFunction("rotateAround", MappingUtils.m2mMappingFunction(Axis.class, EnumFacing.class, enumFacing2::rotateAround));
      return scriptValue;
   }

   public EnumFacingTypeAdapter(TypeRegistry typeRegistry) {
      super("EnumFacing", typeRegistry);
      typeRegistry.put(Axis.class, new AxisTypeAdapter(typeRegistry));
      typeRegistry.put(AxisDirection.class, new AxisDirectionTypeAdapter(typeRegistry));
      typeRegistry.put(Plane.class, new PlaneTypeAdapter(typeRegistry));
   }

   public Map<String, Object> pack(EnumFacing enumFacing2) {
      return packedCache.computeIfAbsent(enumFacing2, enumFacing -> super.pack(enumFacing2));
   }
}
