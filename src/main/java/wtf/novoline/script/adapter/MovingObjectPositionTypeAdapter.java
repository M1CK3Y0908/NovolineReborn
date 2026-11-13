package wtf.novoline.script.adapter;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class MovingObjectPositionTypeAdapter extends AbstractTypeAdapter<MovingObjectPosition> {
   public ScriptValue put(ScriptValue scriptValue2, MovingObjectPosition movingObjectPosition) {
      scriptValue2.putObject("entityHit", movingObjectPosition.entityHit, Entity.class);
      scriptValue2.putObject("hitVec", movingObjectPosition.hitVec, Vec3.class);
      scriptValue2.putObject("sideHit", movingObjectPosition.sideHit, EnumFacing.class);
      scriptValue2.putSupplier("getBlockPos", MappingUtils.mappingSupplier(BlockPos.class, movingObjectPosition::getBlockPos));
      scriptValue2.putEnum("typeOfHit", movingObjectPosition.typeOfHit);
      return scriptValue2;
   }

   public MovingObjectPositionTypeAdapter(TypeRegistry typeRegistry) {
      super("MovingObjectPosition", typeRegistry);
   }

   public MovingObjectPosition apply(ScriptValue scriptValue2, MovingObjectPosition movingObjectPosition) {
      scriptValue2.getOptionalScriptValue("entityHit").ifPresent((scriptValue) -> movingObjectPosition.entityHit = (Entity)((ScriptValue) scriptValue).getJavaObject());
      scriptValue2.getOptionalScriptValue("hitVec").ifPresent((scriptValue) -> movingObjectPosition.hitVec = (Vec3)((ScriptValue) scriptValue).getJavaObject());
      scriptValue2.getOptionalScriptValue("sideHit").ifPresent((scriptValue) -> movingObjectPosition.sideHit = (EnumFacing)((ScriptValue) scriptValue).getJavaObject());
      return movingObjectPosition;
   }
}
