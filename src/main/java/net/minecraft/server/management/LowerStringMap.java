package net.minecraft.server.management;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap<V> implements Map<String, V> {
   private final Map<String, V> internalMap = Maps.newLinkedHashMap();

   @Override
   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   @Override
   public Set<Entry<String, V>> entrySet() {
      return this.internalMap.entrySet();
   }

   @Override
   public boolean containsKey(Object arg1) {
      return this.internalMap.containsKey(arg1.toString().toLowerCase());
   }

   @Override
   public int size() {
      return this.internalMap.size();
   }

   @Override
   public Set<String> keySet() {
      return this.internalMap.keySet();
   }

   @Override
   public V remove(Object arg1) {
      return this.internalMap.remove(arg1.toString().toLowerCase());
   }

   @Override
   public boolean containsValue(Object arg1) {
      return this.internalMap.containsKey(arg1);
   }

   @Override
   public V get(Object arg1) {
      return this.internalMap.get(arg1.toString().toLowerCase());
   }

   @Override
   public void clear() {
      this.internalMap.clear();
   }

   public V put(String arg1, V arg2) {
      return this.internalMap.put(arg1.toLowerCase(), arg2);
   }

   @Override
   public Collection<V> values() {
      return this.internalMap.values();
   }

   @Override
   public void putAll(Map<? extends String, ? extends V> arg1) {
      for (Entry entry : arg1.entrySet()) {
         this.put((String)entry.getKey(), (V)entry.getValue());
      }
   }
}
