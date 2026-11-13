package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import wtf.novoline.api.Contract2;

public class ObjectIntIdentityMap<T> implements Contract2<T> {
   private final IdentityHashMap<T, Integer> identityMap = new IdentityHashMap<>(512);
   private final List<T> objectList = Lists.newArrayList();

   public final T getByValue(int value) {
      return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
   }

   public int get(T key) {
      Integer integer = this.identityMap.get(key);
      return integer == null ? -1 : integer;
   }

   @Override
   public Iterator<T> iterator() {
      return Iterators.filter(this.objectList.iterator(), Predicates.notNull());
   }

   public void put(T key, int value) {
      this.identityMap.put(key, value);

      while (this.objectList.size() <= value) {
         this.objectList.add(null);
      }

      this.objectList.set(value, key);
   }
}
