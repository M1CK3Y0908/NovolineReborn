package wtf.novoline.script.adapter;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptFunctionUtils;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.ScriptFunctionUtils.Component;

public final class EntityLivingBaseTypeAdapter extends AbstractTypeAdapter<EntityLivingBase> {
   public EntityLivingBaseTypeAdapter(TypeRegistry typeRegistry2) {
      super("EntityLivingBase", typeRegistry2);
      this.addSubTypes(ScriptType.createScriptType("EntityPlayer", EntityPlayer.class));
   }

   public EntityLivingBase apply(ScriptValue scriptValue, EntityLivingBase entityLivingBase) {
      entityLivingBase.rotationYawHead = scriptValue.getBigDecimal("rotationYawHead").floatValue();
      entityLivingBase.prevRotationYawHead = scriptValue.getBigDecimal("prevRotationYawHead").floatValue();
      entityLivingBase.rotationPitchHead = scriptValue.getBigDecimal("rotationPitchHead").floatValue();
      entityLivingBase.prevRotationYawHead = scriptValue.getBigDecimal("prevRotationYawHead").floatValue();
      entityLivingBase.moveStrafing = scriptValue.getBigDecimal("moveStrafing").floatValue();
      entityLivingBase.moveForward = scriptValue.getBigDecimal("moveForward").floatValue();
      entityLivingBase.jumpMovementFactor = scriptValue.getBigDecimal("jumpMovementFactor").floatValue();
      entityLivingBase.hurtTime = scriptValue.getBigDecimal("hurtTime").intValue();
      return entityLivingBase;
   }

   public ScriptValue put(ScriptValue scriptValue, EntityLivingBase entityLivingBase) {
      Component component = Component.createComponent(this, scriptValue, entityLivingBase);
      scriptValue.put("rotationYawHead", entityLivingBase.rotationYawHead);
      scriptValue.put("prevRotationYawHead", entityLivingBase.prevRotationYawHead);
      scriptValue.put("rotationPitchHead", entityLivingBase.rotationPitchHead);
      scriptValue.put("prevRotationPitchHead", entityLivingBase.prevRotationPitchHead);
      scriptValue.put("hurtTime", entityLivingBase.hurtTime);
      scriptValue.put("moveStrafing", entityLivingBase.moveStrafing);
      scriptValue.put("moveForward", entityLivingBase.moveForward);
      scriptValue.put("jumpMovementFactor", entityLivingBase.jumpMovementFactor);
      scriptValue.putSupplier("getHealth", ScriptFunctionUtils.supplier(component, entityLivingBase::getHealth));
      scriptValue.putSupplier("getMaxHealth", ScriptFunctionUtils.supplier(component, entityLivingBase::getMaxHealth));
      scriptValue.putSupplier("getHeldItem", ScriptFunctionUtils.supplier(component, MappingUtils.mappingSupplier(ItemStack.class, entityLivingBase::getHeldItem)));
      scriptValue.putSupplier("getInventory", ScriptFunctionUtils.supplier(component, MappingUtils.mappingArraySupplier(ItemStack.class, entityLivingBase::getInventory)));
      scriptValue.putFunction("isOnSameTeam", ScriptFunctionUtils.function(component, MappingUtils.m2oMappingFunction(EntityLivingBase.class, entityLivingBase::isOnSameTeam)));
      scriptValue.putSupplier("getTotalArmorValue", ScriptFunctionUtils.supplier(component, entityLivingBase::getTotalArmorValue));
      scriptValue.putSupplier(
         "getEntityBoundingBox", ScriptFunctionUtils.supplier(component, MappingUtils.mappingSupplier(AxisAlignedBB.class, entityLivingBase::getEntityBoundingBox))
      );
      return scriptValue;
   }

   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(EntityPlayer.class, new EntityPlayerTypeAdapter(this.typeRegistry));
   }
}
