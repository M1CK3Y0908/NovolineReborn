package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cartesian {
   public static Iterable createIterable(Class clazz, Iterable iterable) {
      return new Cartesian.IterableImpl(clazz, (Iterable[])createArray(Iterable.class, iterable));
   }

   private static <T> T[] createArray2(Class<? super T> clazz, int i) {
      return (T[])((Object[])Array.newInstance(clazz, i));
   }

   public static Iterable createIterable3(Iterable iterable) {
      return createIterable2(createIterable(Object.class, iterable));
   }

   private static <T> Iterable<List<T>> createIterable2(Iterable<Object[]> iterable) {
      return Iterables.transform(iterable, new Cartesian.FunctionImpl());
   }

   private static Object[] createArray(Class clazz, Iterable iterable) {
      ArrayList arrayList = Lists.newArrayList();

      for (Object object : iterable) {
         arrayList.add(object);
      }

      return arrayList.toArray(createArray2(clazz, arrayList.size()));
   }

   static class FunctionImpl<T> implements Function<Object[], List<T>> {
      public List<T> apply(Object[] objects) {
         return Arrays.asList((T[])objects);
      }

      private FunctionImpl() {
      }
   }

   static class IterableImpl<T> implements Iterable<T[]> {
      private final Class<T> classValue;
      private final Iterable<? extends T>[] iterableArray;

      private IterableImpl(Class<T> clazz, Iterable<? extends T>[] iterables) {
         this.classValue = clazz;
         this.iterableArray = iterables;
      }

      @Override
      public Iterator<T[]> iterator() {
         return (Iterator<T[]>)(this.iterableArray.length <= 0
            ? Collections.singletonList((T)Cartesian.createArray2(this.classValue, 0)).iterator()
            : new Cartesian.UnmodifiableIteratorChild(this.classValue, this.iterableArray));
      }
   }

   static class UnmodifiableIteratorChild<T> extends UnmodifiableIterator<T[]> {
      private int intValue = -2;
      private final Iterable<? extends T>[] iterableArray;
      private final Iterator<? extends T>[] iteratorArray;
      private final T[] objectArray;

      public T[] next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         }

         while (this.intValue < this.iteratorArray.length) {
            this.objectArray[this.intValue] = (T)this.iteratorArray[this.intValue].next();
            this.intValue++;
         }

         return (T[])this.objectArray.clone();
      }

      private UnmodifiableIteratorChild(Class clazz, Iterable[] iterables) {
         this.iterableArray = iterables;
         this.iteratorArray = Cartesian.createArray2(Iterator.class, this.iterableArray.length);

         for (int i = 0; i < this.iterableArray.length; i++) {
            this.iteratorArray[i] = iterables[i].iterator();
         }

         this.objectArray = (T[])Cartesian.createArray2(clazz, this.iteratorArray.length);
      }

      public boolean hasNext() {
         if (this.intValue == -2) {
            this.intValue = 0;
            Iterator[] iteratorArray2 = this.iteratorArray;
            int length2 = iteratorArray2.length;

            for (int i = 0; i < length2; i++) {
               if (!iteratorArray2[i].hasNext()) {
                  this.update();
                  break;
               }
            }

            return true;
         } else {
            if (this.intValue >= this.iteratorArray.length) {
               for (this.intValue = this.iteratorArray.length - 1; this.intValue >= 0; this.intValue--) {
                  Iterator iterator2 = this.iteratorArray[this.intValue];
                  if (iterator2.hasNext()) {
                     break;
                  }

                  if (this.intValue == 0) {
                     this.update();
                     break;
                  }

                  iterator2 = this.iterableArray[this.intValue].iterator();
                  this.iteratorArray[this.intValue] = iterator2;
                  if (!iterator2.hasNext()) {
                     this.update();
                     break;
                  }
               }
            }

            return this.intValue >= 0;
         }
      }

      private void update() {
         this.intValue = -1;
         Arrays.fill(this.iteratorArray, null);
         Arrays.fill(this.objectArray, null);
      }
   }
}
