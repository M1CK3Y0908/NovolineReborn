package wtf.novoline.script.adapter;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class AxisAlignedBBTypeAdapter extends AbstractTypeAdapter<AxisAlignedBB> {
   public AxisAlignedBBTypeAdapter(TypeRegistry typeRegistry) {
      super("AxisAlignedBB", typeRegistry);
      typeRegistry.put(MovingObjectPosition.class, new MovingObjectPositionTypeAdapter(typeRegistry));
   }

   public ScriptValue put(ScriptValue scriptValue, AxisAlignedBB axisAlignedBB) {
      scriptValue.put("minX", axisAlignedBB.minX);
      scriptValue.put("minY", axisAlignedBB.minY);
      scriptValue.put("minZ", axisAlignedBB.minZ);
      scriptValue.put("maxX", axisAlignedBB.maxX);
      scriptValue.put("maxY", axisAlignedBB.maxY);
      scriptValue.put("maxZ", axisAlignedBB.maxZ);
      scriptValue.putFunction3("addCoord", MappingUtils.ooo2mMappingFunction(AxisAlignedBB.class, axisAlignedBB::addCoord));
      scriptValue.putFunction3("offset", MappingUtils.ooo2mMappingFunction(AxisAlignedBB.class, axisAlignedBB::offset));
      scriptValue.putFunction3("expand", MappingUtils.ooo2mMappingFunction(AxisAlignedBB.class, axisAlignedBB::expand));
      scriptValue.putFunction3("contract", MappingUtils.ooo2mMappingFunction(AxisAlignedBB.class, axisAlignedBB::contract));
      scriptValue.putBiFunction("calculateIntercept", MappingUtils.mm2mMappingFunction(Vec3.class, Vec3.class, MovingObjectPosition.class, axisAlignedBB::calculateIntercept));
      scriptValue.putBiFunction("calculateXOffset", MappingUtils.mo2oMappingFunction(AxisAlignedBB.class, axisAlignedBB::calculateXOffset));
      scriptValue.putBiFunction("calculateYOffset", MappingUtils.mo2oMappingFunction(AxisAlignedBB.class, axisAlignedBB::calculateYOffset));
      scriptValue.putBiFunction("calculateZOffset", MappingUtils.mo2oMappingFunction(AxisAlignedBB.class, axisAlignedBB::calculateZOffset));
      scriptValue.putFunction("intersectsWith", MappingUtils.m2oMappingFunction(AxisAlignedBB.class, axisAlignedBB::intersectsWith));
      scriptValue.putFunction("isVecInside", MappingUtils.m2oMappingFunction(Vec3.class, axisAlignedBB::isVecInside));
      scriptValue.putFunction("union", MappingUtils.m2mMappingFunction(AxisAlignedBB.class, AxisAlignedBB.class, axisAlignedBB::union));
      scriptValue.putSupplier("getAverageEdgeLength", axisAlignedBB::getAverageEdgeLength);
      scriptValue.putSupplier("hasNaN", axisAlignedBB::hasNaN);
      return scriptValue;
   }
}
