package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
   private final V defaultObject;

   @Override
   public V getObject(K name) {
      V v = super.getObject(name);
      return v == null ? this.defaultObject : v;
   }

   public RegistryDefaulted(V defaultObjectIn) {
      this.defaultObject = defaultObjectIn;
   }
}
