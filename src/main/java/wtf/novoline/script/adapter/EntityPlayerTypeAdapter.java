package wtf.novoline.script.adapter;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptFunctionUtils;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.ScriptFunctionUtils.Component;

public final class EntityPlayerTypeAdapter extends AbstractTypeAdapter<EntityPlayer> {
   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(EntityPlayerSP.class, new EntityPlayerSPTypeAdapter(this.typeRegistry));
   }

   public EntityPlayerTypeAdapter(TypeRegistry typeRegistry2) {
      super("EntityPlayer", typeRegistry2);
      this.addSubTypes(ScriptType.createScriptType("EntityPlayerSP", EntityPlayerSP.class));
   }

   public EntityPlayer apply(ScriptValue scriptValue, EntityPlayer entityPlayer) {
      entityPlayer.capabilities = (PlayerCapabilities)scriptValue.getUnpacked("capabilities", PlayerCapabilities.class);
      return entityPlayer;
   }

   public ScriptValue put(ScriptValue scriptValue, EntityPlayer entityPlayer) {
      Component component = Component.createComponent(this, scriptValue, entityPlayer);
      scriptValue.putObject("capabilities", entityPlayer.capabilities, PlayerCapabilities.class);
      scriptValue.putSupplier("isUsingItem", ScriptFunctionUtils.supplier(component, entityPlayer::isUsingItem));
      scriptValue.putSupplier("getItemInUseDuration", ScriptFunctionUtils.supplier(component, entityPlayer::getItemInUseDuration));
      scriptValue.putRunnable("stopUsingItem", ScriptFunctionUtils.runnable(component, entityPlayer::stopUsingItem));
      scriptValue.putRunnable("clearItemInUse", ScriptFunctionUtils.runnable(component, entityPlayer::clearItemInUse));
      scriptValue.putSupplier("isBlocking", ScriptFunctionUtils.supplier(component, entityPlayer::isBlocking));
      scriptValue.putConsumer("dropOneItem", ScriptFunctionUtils.consumer(component, object -> entityPlayer.dropOneItem((Boolean)object)));
      scriptValue.putFunction("getToolDigEfficiency", ScriptFunctionUtils.function(component, MappingUtils.m2oMappingFunction(Block.class, entityPlayer::getToolDigEfficiency)));
      scriptValue.putFunction("canHarvestBlock", ScriptFunctionUtils.function(component, MappingUtils.m2oMappingFunction(Block.class, entityPlayer::canHarvestBlock)));
      scriptValue.putFunction("canAttackPlayer", ScriptFunctionUtils.function(component, MappingUtils.m2oMappingFunction(EntityPlayer.class, entityPlayer::canAttackPlayer)));
      scriptValue.putSupplier("getTotalArmorValue", ScriptFunctionUtils.supplier(component, entityPlayer::getTotalArmorValue));
      scriptValue.putSupplier(
         "getCurrentEquippedItem", ScriptFunctionUtils.supplier(component, MappingUtils.mappingSupplier(ItemStack.class, entityPlayer::getCurrentEquippedItem))
      );
      scriptValue.putRunnable("jump", ScriptFunctionUtils.runnable(component, entityPlayer::jump));
      return scriptValue;
   }
}
