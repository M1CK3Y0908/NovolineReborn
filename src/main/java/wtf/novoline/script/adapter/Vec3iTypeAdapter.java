package wtf.novoline.script.adapter;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class Vec3iTypeAdapter extends AbstractTypeAdapter<Vec3i> {
   public ScriptValue put(ScriptValue scriptValue, Vec3i vec3i) {
      scriptValue.putSupplier("getX", vec3i::getX);
      scriptValue.putSupplier("getY", vec3i::getY);
      scriptValue.putSupplier("getZ", vec3i::getZ);
      scriptValue.putFunction("crossProduct", MappingUtils.m2mMappingFunction(Vec3i.class, Vec3i.class, vec3i::crossProduct));
      scriptValue.putFunction("compareTo", MappingUtils.m2oMappingFunction(Vec3i.class, vec3i::compareTo));
      scriptValue.putFunction("distanceSq", MappingUtils.m2oMappingFunction(Vec3i.class, vec3i::distanceSq));
      return scriptValue;
   }

   public Vec3iTypeAdapter(TypeRegistry typeRegistry) {
      super("Vec3i", typeRegistry);
      this.addSubTypes(ScriptType.createScriptType("BlockPos", BlockPos.class));
   }
}
