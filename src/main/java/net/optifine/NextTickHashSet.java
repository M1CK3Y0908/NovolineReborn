package net.optifine;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;

public class NextTickHashSet extends TreeSet {
   private LongHashMap longHashMap = new LongHashMap();
   private int minX = Integer.MIN_VALUE;
   private int minZ = Integer.MIN_VALUE;
   private int maxX = Integer.MIN_VALUE;
   private static final int UNDEFINED = Integer.MIN_VALUE;
   private int maxZ = Integer.MIN_VALUE;

   @Override
   public Iterator iterator() {
      if (this.minX == Integer.MIN_VALUE) {
         return super.iterator();
      }

      if (this.size() <= 0) {
         return Iterators.emptyIterator();
      }

      int i = this.minX >> 4;
      int i2 = this.minZ >> 4;
      int i3 = this.maxX >> 4;
      int i4 = this.maxZ >> 4;
      ArrayList arrayList = new ArrayList();

      for (int index = i; index <= i3; index++) {
         for (int innerIndex = i2; innerIndex <= i4; innerIndex++) {
            Set set = this.getSubSet(index, innerIndex, false);
            if (set != null) {
               arrayList.add(set.iterator());
            }
         }
      }

      if (arrayList.size() <= 0) {
         return Iterators.emptyIterator();
      } else {
         return arrayList.size() == 1 ? (Iterator)arrayList.get(0) : Iterators.concat(arrayList.iterator());
      }
   }

   @Override
   public boolean contains(Object obj) {
      if (!(obj instanceof NextTickListEntry nextTickListEntry)) {
         return false;
      } else {
         Set set = this.getSubSet(nextTickListEntry, false);
         return set == null ? false : set.contains(nextTickListEntry);
      }
   }

   @Override
   public boolean add(Object obj) {
      if (!(obj instanceof NextTickListEntry nextTickListEntry2)) {
         return false;
      } else if (nextTickListEntry2 == null) {
         return false;
      } else {
         boolean flag = this.getSubSet(nextTickListEntry2, true).add(nextTickListEntry2);
         boolean flag2 = super.add(obj);
         if (flag != flag2) {
            throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag2);
         } else {
            return flag2;
         }
      }
   }

   public void clearIteratorLimits() {
      this.minX = Integer.MIN_VALUE;
      this.minZ = Integer.MIN_VALUE;
      this.maxX = Integer.MIN_VALUE;
      this.maxZ = Integer.MIN_VALUE;
   }

   public NextTickHashSet(Set oldSet) {
      for (Object tickEntry : oldSet) {
         this.add(tickEntry);
      }
   }

   @Override
   public boolean remove(Object obj) {
      if (!(obj instanceof NextTickListEntry nextTickListEntry)) {
         return false;
      } else {
         Set set = this.getSubSet(nextTickListEntry, false);
         if (set == null) {
            return false;
         } else {
            boolean conditionMet = set.remove(nextTickListEntry);
            boolean conditionMet2 = super.remove(nextTickListEntry);
            if (conditionMet != conditionMet2) {
               throw new IllegalStateException("Added: " + conditionMet + ", addedParent: " + conditionMet2);
            } else {
               return conditionMet2;
            }
         }
      }
   }

   private Set getSubSet(int cx, int cz, boolean autoCreate) {
      long l = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
      HashSet hashSet = (HashSet)this.longHashMap.getValueByKey(l);
      if (hashSet == null && autoCreate) {
         hashSet = new HashSet();
         this.longHashMap.add(l, hashSet);
      }

      return hashSet;
   }

   private Set getSubSet(NextTickListEntry entry, boolean autoCreate) {
      if (entry == null) {
         return null;
      }

      BlockPos blockPos = entry.position;
      int xCoordinate = blockPos.getX() >> 4;
      int zCoordinate = blockPos.getZ() >> 4;
      return this.getSubSet(xCoordinate, zCoordinate, autoCreate);
   }
}
