package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.optifine.util.IteratorCache;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
   private static final Set<Class<?>> KNOWN_CLASS_LOOKUPS = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final Map<Class<?>, List<T>> map = Maps.newHashMap();
   private final Set<Class<?>> knownKeys = Sets.newIdentityHashSet();
   private final List<T> values = Lists.newArrayList();
   private final Class<T> baseClass;
   public boolean empty;

   public ClassInheritanceMultiMap(Class<T> baseClassIn) {
      this.baseClass = baseClassIn;
      this.knownKeys.add(baseClassIn);
      this.map.put(baseClassIn, this.values);

      for (Class clazz2 : KNOWN_CLASS_LOOKUPS) {
         this.createLookup(clazz2);
      }

      this.empty = this.values.size() == 0;
   }

   protected void createLookup(Class<?> clazz) {
      KNOWN_CLASS_LOOKUPS.add(clazz);
      int count = this.values.size();

      for (int innerIndex = 0; innerIndex < count; innerIndex++) {
         T t = this.values.get(innerIndex);
         if (clazz.isAssignableFrom(t.getClass())) {
            this.addForClass(t, clazz);
         }
      }

      this.knownKeys.add(clazz);
   }

   @Override
   public int size() {
      return this.values.size();
   }

   @Override
   public boolean isEmpty() {
      return this.empty;
   }

   @Override
   public boolean remove(Object value) {
      Object value2 = value;
      boolean flag = false;

      for (Class clazz2 : this.knownKeys) {
         if (clazz2.isAssignableFrom(value2.getClass())) {
            List list2 = this.map.get(clazz2);
            if (list2 != null && list2.remove(value2)) {
               flag = true;
            }
         }
      }

      this.empty = this.values.size() == 0;
      return flag;
   }

   public <S> Iterable<S> getByClass(final Class<S> clazz) {
      return new Iterable<S>() {
         @Override
         public Iterator<S> iterator() {
            List list2 = ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.initializeClassLookup(clazz));
            return list2 == null ? Iterators.emptyIterator() : Iterators.filter(list2.iterator(), clazz);
         }
      };
   }

   @Override
   public boolean add(T value) {
      for (Class clazz2 : this.knownKeys) {
         if (clazz2.isAssignableFrom(value.getClass())) {
            this.addForClass(value, clazz2);
         }
      }

      this.empty = this.values.size() == 0;
      return true;
   }

   private void addForClass(T value, Class<?> parentClass) {
      List<T> list = this.map.get(parentClass);
      if (list == null) {
         this.map.put(parentClass, Lists.newArrayList(value));
      } else {
         list.add(value);
      }

      this.empty = this.values.size() == 0;
   }

   @Override
   public boolean contains(Object value) {
      return Iterators.contains(this.getByClass(value.getClass()).iterator(), value);
   }

   @Override
   public Iterator<T> iterator() {
      return (Iterator<T>)(this.values.isEmpty() ? Iterators.emptyIterator() : IteratorCache.getReadOnly(this.values));
   }

   protected Class<?> initializeClassLookup(Class<?> clazz) {
      if (this.baseClass.isAssignableFrom(clazz)) {
         if (!this.knownKeys.contains(clazz)) {
            this.createLookup(clazz);
         }

         return clazz;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + clazz);
      }
   }
}
