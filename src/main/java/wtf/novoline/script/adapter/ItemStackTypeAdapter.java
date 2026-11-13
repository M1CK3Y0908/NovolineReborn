package wtf.novoline.script.adapter;

import net.minecraft.item.ItemStack;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class ItemStackTypeAdapter extends AbstractTypeAdapter<ItemStack> {
   public ScriptValue put(ScriptValue scriptValue, ItemStack itemStack) {
      scriptValue.putSupplier("getDisplayName", itemStack::getDisplayName);
      scriptValue.putSupplier("getUnlocalizedName", itemStack::getUnlocalizedName);
      return scriptValue;
   }

   public ItemStackTypeAdapter(TypeRegistry typeRegistry) {
      super("ItemStack", typeRegistry);
   }
}
