package net.minecraft.block.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public interface IBlockState {
   <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> iProperty);

   Block getBlock();

   <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> iProperty, V value);

   <T extends Comparable<T>> T getValue(IProperty<T> iProperty);

   Collection<IProperty> getPropertyNames();

   ImmutableMap<IProperty, Comparable> getProperties();
}
