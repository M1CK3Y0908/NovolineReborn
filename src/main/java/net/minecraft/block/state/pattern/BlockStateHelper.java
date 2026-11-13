package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper implements Predicate<IBlockState> {
   private final Map<IProperty, Predicate> propertyPredicates = Maps.newHashMap();
   private final BlockState blockstate;

   public static BlockStateHelper forBlock(Block blockIn) {
      return new BlockStateHelper(blockIn.getBlockState());
   }

   public <V extends Comparable<V>> BlockStateHelper where(IProperty<V> property, Predicate<? extends V> is) {
      if (!this.blockstate.getProperties().contains(property)) {
         throw new IllegalArgumentException(this.blockstate + " cannot support property " + property);
      }

      this.propertyPredicates.put(property, is);
      return this;
   }

   private BlockStateHelper(BlockState blockStateIn) {
      this.blockstate = blockStateIn;
   }

   public boolean apply(IBlockState arg1) {
      if (arg1 != null && arg1.getBlock().equals(this.blockstate.getBlock())) {
         for (Entry entry : this.propertyPredicates.entrySet()) {
            Comparable value = arg1.getValue((IProperty)entry.getKey());
            if (!((Predicate)entry.getValue()).apply(value)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
