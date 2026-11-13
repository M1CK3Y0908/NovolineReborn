package wtf.novoline.script.adapter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class IBlockStateTypeAdapter extends AbstractTypeAdapter<IBlockState> {
   public IBlockStateTypeAdapter(TypeRegistry typeRegistry) {
      super("IBlockState", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, IBlockState iBlockState) {
      scriptValue.putSupplier("getBlock", MappingUtils.mappingSupplier(Block.class, iBlockState::getBlock));
      return scriptValue;
   }
}
