package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

public abstract class BlockStateBase implements IBlockState {
   private int blockId = -1;
   private int blockStateId = -1;
   private static final Joiner COMMA_JOINER = Joiner.on(',');
   private static final Function<Entry<IProperty, Comparable>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty, Comparable>, String>() {
      public String apply(Entry<IProperty, Comparable> arg1) {
         if (arg1 == null) {
            return "<NULL>";
         }

         IProperty property = arg1.getKey();
         return property.getName() + "=" + property.getName(arg1.getValue());
      }
   };
   private int metadata = -1;
   private ResourceLocation blockLocation = null;

   public int getMetadata() {
      if (this.metadata < 0) {
         this.metadata = this.getBlock().getMetaFromState(this);
      }

      return this.metadata;
   }

   protected static <T> T cyclePropertyValue(Collection<T> values, T currentValue) {
      Iterator<T> iterator = values.iterator();

      while (iterator.hasNext()) {
         if (iterator.next().equals(currentValue)) {
            if (iterator.hasNext()) {
               return iterator.next();
            }

            return values.iterator().next();
         }
      }

      return iterator.next();
   }

   public ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> getPropertyValueTable() {
      return null;
   }

   public int getBlockId() {
      if (this.blockId < 0) {
         this.blockId = Block.getIdFromBlock(this.getBlock());
      }

      return this.blockId;
   }

   @Override
   public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
      return this.withProperty(property, cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));
      if (!this.getProperties().isEmpty()) {
         stringBuilder.append("[");
         COMMA_JOINER.appendTo(stringBuilder, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
         stringBuilder.append("]");
      }

      return stringBuilder.toString();
   }
}
