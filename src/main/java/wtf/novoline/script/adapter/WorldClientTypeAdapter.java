package wtf.novoline.script.adapter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class WorldClientTypeAdapter extends AbstractTypeAdapter<WorldClient> {
   public ScriptValue put(ScriptValue scriptValue, WorldClient worldClient) {
      scriptValue.putFunction("getBlockState", MappingUtils.m2mMappingFunction(BlockPos.class, IBlockState.class, worldClient::getBlockState));
      scriptValue.putSupplier("getLoadedEntityList", MappingUtils.mappingListSupplier(Entity.class, worldClient::getLoadedEntityList));
      return scriptValue;
   }

   public WorldClientTypeAdapter(TypeRegistry typeRegistry) {
      super("WorldClient", typeRegistry);
   }
}
