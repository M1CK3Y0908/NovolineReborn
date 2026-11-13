package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

public class BlockState {
   private final Block block;
   private static final Joiner COMMA_JOINER = Joiner.on(", ");
   private static final Function<IProperty, String> GET_NAME_FUNC = new Function<IProperty, String>() {
      public String apply(IProperty arg1) {
         return arg1 == null ? "<NULL>" : arg1.getName();
      }
   };
   private final ImmutableList<IProperty> properties;
   private final ImmutableList<IBlockState> validStates;

   public ImmutableList<IBlockState> getValidStates() {
      return this.validStates;
   }

   public Block getBlock() {
      return this.block;
   }

   public BlockState(Block blockIn, IProperty... properties) {
      this.block = blockIn;
      Arrays.sort(properties, new Comparator<IProperty>() {
         public int compare(IProperty arg1, IProperty arg2) {
            return arg1.getName().compareTo(arg2.getName());
         }
      });
      this.properties = ImmutableList.copyOf(properties);
      LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
      ArrayList arrayList = Lists.newArrayList();

      for (List list2 : (Iterable<List>) Cartesian.createIterable3(this.getAllowedValues())) {
         Map map2 = MapPopulator.createMap2(this.properties, list2);
         BlockState.StateImplementation stateImplementation = new BlockState.StateImplementation(blockIn, ImmutableMap.copyOf(map2));
         linkedHashMap.put(map2, stateImplementation);
         arrayList.add(stateImplementation);
      }

      for (BlockState.StateImplementation stateImplementation2 : (Iterable<BlockState.StateImplementation>) arrayList) {
         stateImplementation2.buildPropertyValueTable(linkedHashMap);
      }

      this.validStates = ImmutableList.copyOf(arrayList);
   }

   public Collection<IProperty> getProperties() {
      return this.properties;
   }

   private List<Iterable<Comparable>> getAllowedValues() {
      List<Iterable<Comparable>> list = Lists.newArrayList();

      for (int index = 0; index < this.properties.size(); index++) {
         list.add(((IProperty)this.properties.get(index)).getAllowedValues());
      }

      return list;
   }

   public IBlockState getBaseState() {
      return (IBlockState)this.validStates.get(0);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
         .add("block", Block.blockRegistry.getNameForObject(this.block))
         .add("properties", Iterables.transform(this.properties, GET_NAME_FUNC))
         .toString();
   }

   static class StateImplementation extends BlockStateBase {
      private final Block block;
      private final ImmutableMap<IProperty, Comparable> properties;
      private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;

      public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map) {
         if (this.propertyValueTable != null) {
            throw new IllegalStateException();
         }

         HashBasedTable hashBasedTable = HashBasedTable.create();
         UnmodifiableIterator unmodifiableIterator = this.properties.keySet().iterator();

         while (unmodifiableIterator.hasNext()) {
            IProperty iProperty = (IProperty)unmodifiableIterator.next();

            for (Comparable comparable : (Iterable<Comparable>) iProperty.getAllowedValues()) {
               if (comparable != this.properties.get(iProperty)) {
                  hashBasedTable.put(iProperty, comparable, map.get(this.getPropertiesWithValue(iProperty, comparable)));
               }
            }
         }

         this.propertyValueTable = ImmutableTable.copyOf(hashBasedTable);
      }

      @Override
      public ImmutableMap<IProperty, Comparable> getProperties() {
         return this.properties;
      }

      @Override
      public boolean equals(Object arg1) {
         return this == arg1;
      }

      @Override
      public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
         if (!this.properties.containsKey(property)) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
         } else if (!property.getAllowedValues().contains(value)) {
            throw new IllegalArgumentException(
               "Cannot set property "
                  + property
                  + " to "
                  + value
                  + " on block "
                  + Block.blockRegistry.getNameForObject(this.block)
                  + ", it is not an allowed value"
            );
         } else {
            return this.properties.get(property) == value ? this : (IBlockState)this.propertyValueTable.get(property, value);
         }
      }

      private Map<IProperty, Comparable> getPropertiesWithValue(IProperty property, Comparable value) {
         Map<IProperty, Comparable> map = Maps.newHashMap(this.properties);
         map.put(property, value);
         return map;
      }

      @Override
      public Block getBlock() {
         return this.block;
      }

      @Override
      public Collection<IProperty> getPropertyNames() {
         return Collections.unmodifiableCollection(this.properties.keySet());
      }

      @Override
      public int hashCode() {
         return this.properties.hashCode();
      }

      private StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn) {
         this.block = blockIn;
         this.properties = propertiesIn;
      }

      @Override
      public <T extends Comparable<T>> T getValue(IProperty<T> property) {
         if (!this.properties.containsKey(property)) {
            throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
         } else {
            return property.getValueClass().cast(this.properties.get(property));
         }
      }
   }
}
