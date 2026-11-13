package wtf.novoline.script.adapter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptFunctionUtils;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.ScriptFunctionUtils.Component;

public class EntityTypeAdapter extends AbstractTypeAdapter<Entity> {
   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(EntityLivingBase.class, new EntityLivingBaseTypeAdapter(this.typeRegistry));
   }

   public ScriptValue put(ScriptValue scriptValue, Entity entity2) {
      Component component = Component.createComponent(this, scriptValue, entity2);
      scriptValue.put("prevPosX", entity2.prevPosX);
      scriptValue.put("prevPosY", entity2.prevPosY);
      scriptValue.put("prevPosZ", entity2.prevPosZ);
      scriptValue.put("posX", entity2.posX);
      scriptValue.put("posY", entity2.posY);
      scriptValue.put("posZ", entity2.posZ);
      scriptValue.put("motionX", entity2.motionX);
      scriptValue.put("motionY", entity2.motionY);
      scriptValue.put("motionZ", entity2.motionZ);
      scriptValue.put("rotationYaw", entity2.rotationYaw);
      scriptValue.put("rotationPitch", entity2.rotationPitch);
      scriptValue.put("prevRotationYaw", entity2.prevRotationYaw);
      scriptValue.put("prevRotationPitch", entity2.prevRotationPitch);
      scriptValue.put("onGround", entity2.onGround);
      scriptValue.put("isDead", entity2.isDead);
      scriptValue.put("fallDistance", entity2.fallDistance);
      scriptValue.put("ticksExisted", entity2.ticksExisted);
      scriptValue.put("hurtResistantTime", entity2.hurtResistantTime);
      scriptValue.put("lastTickPosX", entity2.lastTickPosX);
      scriptValue.put("lastTickPosY", entity2.lastTickPosY);
      scriptValue.put("lastTickPosZ", entity2.lastTickPosZ);
      scriptValue.put("isCollided", entity2.isCollided);
      scriptValue.put("isCollidedHorizontally", entity2.isCollidedHorizontally);
      scriptValue.put("isCollidedVertically", entity2.isCollidedVertically);
      scriptValue.putSupplier("getDisplayName", ScriptFunctionUtils.supplier(component, MappingUtils.mappingSupplier(IChatComponent.class, entity2::getDisplayName)));
      scriptValue.putSupplier("getName", ScriptFunctionUtils.supplier(component, entity2::getName));
      scriptValue.putSupplier("getEyeHeight", ScriptFunctionUtils.supplier(component, entity2::getEyeHeight));
      scriptValue.putFunction("getDistanceToEntity", ScriptFunctionUtils.function(component, MappingUtils.m2oMappingFunction(Entity.class, entity2::getDistanceToEntity)));
      scriptValue.putFunction(
         "getDistanceSqToCenter", ScriptFunctionUtils.function(component, MappingUtils.m2oMappingFunction(BlockPos.class, entity2::getDistanceSqToCenter))
      );
      scriptValue.putSupplier("isInWeb", ScriptFunctionUtils.supplier(component, entity2::isIsInWeb));
      scriptValue.putSupplier("isInWater", ScriptFunctionUtils.supplier(component, entity2::isInWater));
      scriptValue.putSupplier("isInLava", ScriptFunctionUtils.supplier(component, entity2::isInLava));
      scriptValue.putSupplier("isRiding", ScriptFunctionUtils.supplier(component, entity2::isRiding));
      scriptValue.putSupplier("isInvisible", ScriptFunctionUtils.supplier(component, entity2::isInvisible));
      scriptValue.putSupplier("isSprinting", ScriptFunctionUtils.supplier(component, entity2::isSprinting));
      scriptValue.putSupplier("isSneaking", ScriptFunctionUtils.supplier(component, entity2::isSneaking));
      return scriptValue;
   }

   public EntityTypeAdapter(TypeRegistry typeRegistry2) {
      super("Entity", typeRegistry2);
      this.addSubTypes(ScriptType.createScriptType("EntityLivingBase", EntityLivingBase.class));
   }

   public Entity apply(ScriptValue scriptValue, Entity entity2) {
      entity2.prevPosX = scriptValue.getBigDecimal("prevPosX").floatValue();
      entity2.prevPosY = scriptValue.getBigDecimal("prevPosY").floatValue();
      entity2.prevPosZ = scriptValue.getBigDecimal("prevPosZ").floatValue();
      entity2.posX = scriptValue.getBigDecimal("posX").floatValue();
      entity2.posY = scriptValue.getBigDecimal("posY").floatValue();
      entity2.posZ = scriptValue.getBigDecimal("posZ").floatValue();
      entity2.motionX = scriptValue.getBigDecimal("motionX").floatValue();
      entity2.motionY = scriptValue.getBigDecimal("motionY").floatValue();
      entity2.motionZ = scriptValue.getBigDecimal("motionZ").floatValue();
      entity2.rotationYaw = scriptValue.getBigDecimal("rotationYaw").floatValue();
      entity2.rotationPitch = scriptValue.getBigDecimal("rotationPitch").floatValue();
      entity2.prevRotationYaw = scriptValue.getBigDecimal("prevRotationYaw").floatValue();
      entity2.prevRotationPitch = scriptValue.getBigDecimal("prevRotationPitch").floatValue();
      entity2.onGround = scriptValue.checkString("onGround");
      entity2.isDead = scriptValue.checkString("isDead");
      entity2.isCollided = scriptValue.checkString("isCollided");
      entity2.isCollidedHorizontally = scriptValue.checkString("isCollidedHorizontally");
      entity2.isCollidedVertically = scriptValue.checkString("isCollidedVertically");
      entity2.ticksExisted = scriptValue.getBigDecimal("ticksExisted").intValue();
      entity2.fallDistance = scriptValue.getBigDecimal("fallDistance").intValue();
      entity2.hurtResistantTime = scriptValue.getBigDecimal("hurtResistantTime").intValue();
      return entity2;
   }
}
