package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V> {
   private V defaultValue;
   private final K defaultValueKey;

   @Override
   public V getObjectById(int id) {
      V v = super.getObjectById(id);
      return v == null ? this.defaultValue : v;
   }

   @Override
   public void register(int id, K key, V value) {
      if (this.defaultValueKey.equals(key)) {
         this.defaultValue = value;
      }

      super.register(id, key, value);
   }

   public RegistryNamespacedDefaultedByKey(K defaultValueKeyIn) {
      this.defaultValueKey = defaultValueKeyIn;
   }

   @Override
   public V getObject(K name) {
      V v = super.getObject(name);
      return v == null ? this.defaultValue : v;
   }

   public void validateKey() {
      Validate.notNull(this.defaultValueKey);
   }
}
