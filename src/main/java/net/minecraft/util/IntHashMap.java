package net.minecraft.util;

public class IntHashMap<V> {
   private transient IntHashMap.Entry<V>[] slots = new IntHashMap.Entry[16];
   private transient int count;
   private int threshold = 12;
   private final float growFactor = 0.75F;

   public V removeObject(int key) {
      IntHashMap.Entry<V> entry = this.removeEntry(key);
      return entry == null ? null : entry.valueEntry;
   }

   final IntHashMap.Entry<V> removeEntry(int key) {
      int slotIndex2 = getSlotIndex(computeHash(key), this.slots.length);
      IntHashMap.Entry entry2 = this.slots[slotIndex2];
      IntHashMap.Entry entry3 = entry2;

      while (entry3 != null) {
         IntHashMap.Entry nextEntry2 = entry3.nextEntry;
         if (entry3.hashEntry == key) {
            this.count--;
            if (entry2 == entry3) {
               this.slots[slotIndex2] = nextEntry2;
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

   private void grow(int newSize) {
      if (this.slots.length == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         IntHashMap.Entry[] entrys = new IntHashMap.Entry[newSize];
         this.copyTo(entrys);
         this.slots = entrys;
         this.threshold = (int)(newSize * 0.75F);
      }
   }

   private static int computeHash(int integer) {
      integer = integer ^ integer >>> 20 ^ integer >>> 12;
      return integer ^ integer >>> 7 ^ integer >>> 4;
   }

   public void addKey(int key, V value) {
      int hash2 = computeHash(key);
      int count = getSlotIndex(hash2, this.slots.length);

      for (IntHashMap.Entry<V> entry = this.slots[count]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == key) {
            entry.valueEntry = value;
            return;
         }
      }

      this.insert(hash2, key, value, count);
   }

   private static int getSlotIndex(int hash, int slotCount) {
      return hash & slotCount - 1;
   }

   public void clearMap() {
      IntHashMap.Entry<V>[] entry = this.slots;

      for (int index = 0; index < entry.length; index++) {
         entry[index] = null;
      }

      this.count = 0;
   }

   public boolean containsItem(int key) {
      return this.lookupEntry(key) != null;
   }

   private void insert(int hash, int key, V value, int slotIndex) {
      IntHashMap.Entry entry2 = this.slots[slotIndex];
      this.slots[slotIndex] = new IntHashMap.Entry<>(hash, key, value, entry2);
      if (this.count++ >= this.threshold) {
         this.grow(2 * this.slots.length);
      }
   }

   public V lookup(int key) {
      int hash2 = computeHash(key);

      for (IntHashMap.Entry<V> entry = this.slots[getSlotIndex(hash2, this.slots.length)]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == key) {
            return entry.valueEntry;
         }
      }

      return null;
   }

   final IntHashMap.Entry<V> lookupEntry(int key) {
      int hash2 = computeHash(key);

      for (IntHashMap.Entry<V> entry = this.slots[getSlotIndex(hash2, this.slots.length)]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == key) {
            return entry;
         }
      }

      return null;
   }

   private void copyTo(IntHashMap.Entry<V>[] newSlots) {
      IntHashMap.Entry<V>[] entry = this.slots;
      int count = newSlots.length;

      for (int innerIndex = 0; innerIndex < entry.length; innerIndex++) {
         IntHashMap.Entry entry2 = entry[innerIndex];
         if (entry2 != null) {
            entry[innerIndex] = null;

            while (true) {
               IntHashMap.Entry<V> entry4 = entry2.nextEntry;
               int slotIndex2 = getSlotIndex(entry2.slotHash, count);
               entry2.nextEntry = newSlots[slotIndex2];
               newSlots[slotIndex2] = entry2;
               entry2 = entry4;
               if (entry4 == null) {
                  break;
               }
            }
         }
      }
   }

   static class Entry<V> {
      V valueEntry;
      IntHashMap.Entry<V> nextEntry;
      final int hashEntry;
      final int slotHash;

      @Override
      public final String toString() {
         return this.getHash() + "=" + this.getValue();
      }

      @Override
      public final boolean equals(Object other) {
         if (!(other instanceof IntHashMap.Entry<?> entry)) {
            return false;
         } else {
            Object thisKey = this.getHash();
            Object otherKey = entry.getHash();
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

      @Override
      public final int hashCode() {
         return IntHashMap.computeHash(this.hashEntry);
      }

      Entry(int slotHashIn, int hashEntryIn, V valueEntryIn, IntHashMap.Entry<V> nextEntryIn) {
         this.valueEntry = valueEntryIn;
         this.nextEntry = nextEntryIn;
         this.hashEntry = hashEntryIn;
         this.slotHash = slotHashIn;
      }

      public final V getValue() {
         return this.valueEntry;
      }

      public final int getHash() {
         return this.hashEntry;
      }
   }
}
