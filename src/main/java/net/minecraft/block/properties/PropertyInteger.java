package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;

public class PropertyInteger extends PropertyHelper<Integer> {
   private final ImmutableSet<Integer> allowedValues;

   @Override
   public int hashCode() {
      int i = super.hashCode();
      return 31 * i + this.allowedValues.hashCode();
   }

   @Override
   public boolean equals(Object arg1) {
      if (this == arg1) {
         return true;
      }

      if (arg1 == null || this.getClass() != arg1.getClass()) {
         return false;
      }

      if (!super.equals(arg1)) {
         return false;
      }

      PropertyInteger propertyInteger = (PropertyInteger)arg1;
      return this.allowedValues.equals(propertyInteger.allowedValues);
   }

   protected PropertyInteger(String name, int min, int max) {
      super(name, Integer.class);
      if (min < 0) {
         throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
      }

      if (max <= min) {
         throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
      }

      Set<Integer> set = Sets.newHashSet();

      for (int index = min; index <= max; index++) {
         set.add(index);
      }

      this.allowedValues = ImmutableSet.copyOf(set);
   }

   @Override
   public Collection<Integer> getAllowedValues() {
      return this.allowedValues;
   }

   public String getName(Integer value) {
      return value.toString();
   }

   public static PropertyInteger create(String name, int min, int max) {
      return new PropertyInteger(name, min, max);
   }
}
