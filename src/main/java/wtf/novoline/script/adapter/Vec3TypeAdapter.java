package wtf.novoline.script.adapter;

import net.minecraft.util.Vec3;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class Vec3TypeAdapter extends AbstractTypeAdapter<Vec3> {
   public Vec3TypeAdapter(TypeRegistry typeRegistry) {
      super("Vec3", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, Vec3 vec3) {
      scriptValue.put("xCoord", vec3.xCoord);
      scriptValue.put("yCoord", vec3.yCoord);
      scriptValue.put("zCoord", vec3.zCoord);
      scriptValue.putFunction("add", MappingUtils.m2mMappingFunction(Vec3.class, Vec3.class, vec3::add));
      scriptValue.putFunction3("addVector", MappingUtils.ooo2mMappingFunction(Vec3.class, vec3::addVector));
      scriptValue.putFunction("subtract", MappingUtils.m2mMappingFunction(Vec3.class, Vec3.class, vec3::subtract));
      scriptValue.putFunction3("subtractVector", MappingUtils.<Double, Double, Double, Vec3>ooo2mMappingFunction(Vec3.class, vec3::subtract));
      scriptValue.putFunction("rotateYaw", MappingUtils.o2mMappingFunction(Vec3.class, vec3::rotateYaw));
      scriptValue.putFunction("rotatePitch", MappingUtils.o2mMappingFunction(Vec3.class, vec3::rotatePitch));
      scriptValue.putFunction("crossProduct", MappingUtils.m2mMappingFunction(Vec3.class, Vec3.class, vec3::crossProduct));
      scriptValue.putFunction("dotProduct", MappingUtils.m2oMappingFunction(Vec3.class, vec3::dotProduct));
      scriptValue.putFunction("distanceTo", MappingUtils.m2oMappingFunction(Vec3.class, vec3::distanceTo));
      scriptValue.putSupplier("lengthVector", vec3::lengthVector);
      scriptValue.putSupplier("normalize", MappingUtils.mappingSupplier(Vec3.class, vec3::normalize));
      return scriptValue;
   }
}
