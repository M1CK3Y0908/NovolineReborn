package net.minecraft.block.properties;

import com.google.common.base.Objects;

public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T> {
   private final Class<T> valueClass;
   private final String name;

   @Override
   public int hashCode() {
      return 31 * this.valueClass.hashCode() + this.name.hashCode();
   }

   @Override
   public boolean equals(Object arg1) {
      if (this == arg1) {
         return true;
      } else if (arg1 != null && this.getClass() == arg1.getClass()) {
         PropertyHelper propertyHelper = (PropertyHelper)arg1;
         return this.valueClass.equals(propertyHelper.valueClass) && this.name.equals(propertyHelper.name);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).add("values", this.getAllowedValues()).toString();
   }

   protected PropertyHelper(String name, Class<T> valueClass) {
      this.valueClass = valueClass;
      this.name = name;
   }

   @Override
   public Class<T> getValueClass() {
      return this.valueClass;
   }

   @Override
   public String getName() {
      return this.name;
   }
}
