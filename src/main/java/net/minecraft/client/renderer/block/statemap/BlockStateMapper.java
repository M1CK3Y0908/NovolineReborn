package net.minecraft.client.renderer.block.statemap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class BlockStateMapper {
   private Map<Block, IStateMapper> blockStateMap = Maps.newIdentityHashMap();
   private Set<Block> setBuiltInBlocks = Sets.newIdentityHashSet();

   public void registerBlockStateMapper(Block block2, IStateMapper iStateMapper) {
      this.blockStateMap.put(block2, iStateMapper);
   }

   public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations() {
      IdentityHashMap identityHashMap = Maps.newIdentityHashMap();

      for (Block block2 : Block.blockRegistry) {
         if (!this.setBuiltInBlocks.contains(block2)) {
            identityHashMap.putAll(((IStateMapper)Objects.firstNonNull(this.blockStateMap.get(block2), new DefaultStateMapper())).putStateModelLocations(block2));
         }
      }

      return identityHashMap;
   }

   public void registerBuiltInBlocks(Block... blocks) {
      Collections.addAll(this.setBuiltInBlocks, blocks);
   }
}
