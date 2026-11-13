package wtf.novoline.script.adapter;

import net.minecraft.block.Block;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class BlockTypeAdapter extends AbstractTypeAdapter<Block> {
   public ScriptValue put(ScriptValue scriptValue, Block block2) {
      scriptValue.putSupplier("getLocalizedName", block2::getLocalizedName);
      scriptValue.putSupplier("getUnlocalizedName", block2::getUnlocalizedName);
      return scriptValue;
   }

   public BlockTypeAdapter(TypeRegistry typeRegistry) {
      super("Block", typeRegistry);
   }
}
