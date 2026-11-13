package net.minecraft.util;

public class LongHashMap<V> {
   private transient LongHashMap.Entry<V>[] hashArray = new LongHashMap.Entry[4096];
   private int capacity = 3072;
   private transient int numHashElements;
   private final float percentUseable = 0.75F;
   private transient volatile int modCount;
   private int mask = this.hashArray.length - 1;

   public V remove(long key) {
      LongHashMap.Entry<V> entry = this.removeKey(key);
      return entry == null ? null : entry.value;
   }

   public int getNumHashElements() {
      return this.numHashElements;
   }

   final LongHashMap.Entry<V> removeKey(long key) {
      int hashIndex2 = getHashIndex(getHashedKey(key), this.mask);
      LongHashMap.Entry entry2 = this.hashArray[hashIndex2];
      LongHashMap.Entry entry3 = entry2;

      while (entry3 != null) {
         LongHashMap.Entry nextEntry2 = entry3.nextEntry;
         if (entry3.key == key) {
            this.modCount++;
            this.numHashElements--;
            if (entry2 == entry3) {
               this.hashArray[hashIndex2] = nextEntry2;
            } else {
               entry2.nextEntry = nextEntry2;
            }

            return entry3;
         }

         entry2 = entry3;
         entry3 = nextEntry2;
      }

      return entry3;
   }

   final LongHashMap.Entry<V> getEntry(long key) {
      int hashedKey = getHashedKey(key);

      for (LongHashMap.Entry<V> entry = this.hashArray[getHashIndex(hashedKey, this.mask)]; entry != null; entry = entry.nextEntry) {
         if (entry.key == key) {
            return entry;
         }
      }

      return null;
   }

   private static int getHashedKey(long originalKey) {
      return (int)(originalKey ^ originalKey >>> 27);
   }

   public boolean containsItem(long key) {
      return this.getEntry(key) != null;
   }

   public V getValueByKey(long key) {
      int hashedKey = getHashedKey(key);

      for (LongHashMap.Entry<V> entry = this.hashArray[getHashIndex(hashedKey, this.mask)]; entry != null; entry = entry.nextEntry) {
         if (entry.key == key) {
            return entry.value;
         }
      }

      return null;
   }

   private void createKey(int hash, long key, V value, int hashIndex) {
      LongHashMap.Entry entry2 = this.hashArray[hashIndex];
      this.hashArray[hashIndex] = new LongHashMap.Entry<>(hash, key, value, entry2);
      if (this.numHashElements++ >= this.capacity) {
         this.resizeTable(2 * this.hashArray.length);
      }
   }

   public void add(long key, V value) {
      int hashedKey = getHashedKey(key);
      int hashIndex2 = getHashIndex(hashedKey, this.mask);

      for (LongHashMap.Entry<V> entry = this.hashArray[hashIndex2]; entry != null; entry = entry.nextEntry) {
         if (entry.key == key) {
            entry.value = value;
            return;
         }
      }

      this.modCount++;
      this.createKey(hashedKey, key, value, hashIndex2);
   }

   private void resizeTable(int newSize) {
      if (this.hashArray.length == 1073741824) {
         this.capacity = Integer.MAX_VALUE;
      } else {
         LongHashMap.Entry[] entrys = new LongHashMap.Entry[newSize];
         this.copyHashTableTo(entrys);
         this.hashArray = entrys;
         this.mask = this.hashArray.length - 1;
         float newSize2 = newSize;
         this.getClass();
         this.capacity = (int)(newSize2 * 0.75F);
      }
   }

   private void copyHashTableTo(LongHashMap.Entry<V>[] newHashArray) {
      LongHashMap.Entry<V>[] entry = this.hashArray;
      int count = newHashArray.length;

      for (int innerIndex = 0; innerIndex < entry.length; innerIndex++) {
         LongHashMap.Entry entry2 = entry[innerIndex];
         if (entry2 != null) {
            entry[innerIndex] = null;

            while (true) {
               LongHashMap.Entry<V> entry4 = entry2.nextEntry;
               int hashIndex2 = getHashIndex(entry2.hash, count - 1);
               entry2.nextEntry = newHashArray[hashIndex2];
               newHashArray[hashIndex2] = entry2;
               entry2 = entry4;
               if (entry4 == null) {
                  break;
               }
            }
         }
      }
   }

   private static int getHashIndex(int hash, int mask) {
      return hash & mask;
   }

   static class Entry<V> {
      V value;
      LongHashMap.Entry<V> nextEntry;
      final long key;
      final int hash;

      @Override
      public final int hashCode() {
         return LongHashMap.getHashedKey(this.key);
      }

      @Override
      public final String toString() {
         return this.getKey() + "=" + this.getValue();
      }

      Entry(int hashIn, long keyIn, V valueIn, LongHashMap.Entry<V> nextEntryIn) {
         this.value = valueIn;
         this.nextEntry = nextEntryIn;
         this.key = keyIn;
         this.hash = hashIn;
      }

      public final long getKey() {
         return this.key;
      }

      public final V getValue() {
         return this.value;
      }

      @Override
      public final boolean equals(Object other) {
         if (!(other instanceof LongHashMap.Entry<?> entry)) {
            return false;
         } else {
            Object thisKey = this.getKey();
            Object otherKey = entry.getKey();
            if (thisKey == otherKey || thisKey != null && thisKey.equals(otherKey)) {
               Object thisValue = this.getValue();
               Object otherValue = entry.getValue();
               if (thisValue == otherValue || thisValue != null && thisValue.equals(otherValue)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
