package wtf.novoline.util;

import java.lang.reflect.Array;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RingBuffer<T> {
   private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private final Class<? extends T> classValue;
   private int size;
   private int writeIndex;
   private final T[] objectArray;

   public Object[] toArray() {
      Object[] objects = (Object[])Array.newInstance(this.classValue, this.size);
      this.readWriteLock.readLock().lock();

      for (int i = 0; i < this.size; i++) {
         int i2 = (this.writeIndex - this.size + i) % this.getCapacity();
         if (i2 < 0) {
            i2 += this.getCapacity();
         }

         objects[i] = this.objectArray[i2];
      }

      this.readWriteLock.readLock().unlock();
      return objects;
   }

   public Object add(Object object) {
      this.readWriteLock.writeLock().lock();
      this.objectArray[this.writeIndex] = (T)object;
      this.writeIndex = (this.writeIndex + 1) % this.getCapacity();
      if (this.size < this.getCapacity()) {
         this.size++;
      }

      this.readWriteLock.writeLock().unlock();
      return object;
   }

   public RingBuffer(Class clazz) {
      this.classValue = clazz;
      this.objectArray = (T[])((Object[])Array.newInstance(clazz, 50));
   }

   public int getCapacity() {
      this.readWriteLock.readLock().lock();
      int length2 = this.objectArray.length;
      this.readWriteLock.readLock().unlock();
      return length2;
   }
}
