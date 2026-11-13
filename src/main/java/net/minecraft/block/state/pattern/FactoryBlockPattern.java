package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.state.BlockWorldState;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
   private final List<String[]> depth = Lists.newArrayList();
   private static final Joiner COMMA_JOIN = Joiner.on(",");
   private int rowWidth;
   private int aisleHeight;
   private final Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();

   private void checkMissingPredicates() {
      ArrayList arrayList = Lists.newArrayList();

      for (Entry entry : this.symbolMap.entrySet()) {
         if (entry.getValue() == null) {
            arrayList.add((Character)entry.getKey());
         }
      }

      if (!arrayList.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(arrayList) + " are missing");
      }
   }

   public BlockPattern build() {
      return new BlockPattern(this.makePredicateArray());
   }

   public FactoryBlockPattern where(char symbol, Predicate<BlockWorldState> blockMatcher) {
      this.symbolMap.put(symbol, blockMatcher);
      return this;
   }

   private FactoryBlockPattern() {
      this.symbolMap.put(' ', Predicates.alwaysTrue());
   }

   private Predicate<BlockWorldState>[][][] makePredicateArray() {
      this.checkMissingPredicates();
      Predicate[][][] predicatesArrays = (Predicate[][][])Array.newInstance(Predicate.class, this.depth.size(), this.aisleHeight, this.rowWidth);

      for (int index = 0; index < this.depth.size(); index++) {
         for (int innerIndex = 0; innerIndex < this.aisleHeight; innerIndex++) {
            for (int nestedIndex = 0; nestedIndex < this.rowWidth; nestedIndex++) {
               predicatesArrays[index][innerIndex][nestedIndex] = this.symbolMap.get(this.depth.get(index)[innerIndex].charAt(nestedIndex));
            }
         }
      }

      return predicatesArrays;
   }

   public static FactoryBlockPattern start() {
      return new FactoryBlockPattern();
   }

   public FactoryBlockPattern aisle(String... aisle) {
      if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
         if (this.depth.isEmpty()) {
            this.aisleHeight = aisle.length;
            this.rowWidth = aisle[0].length();
         }

         if (aisle.length != this.aisleHeight) {
            throw new IllegalArgumentException(
               "Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")"
            );
         }

         for (String text : aisle) {
            if (text.length() != this.rowWidth) {
               throw new IllegalArgumentException(
                  "Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + text.length() + ")"
               );
            }

            for (char c : text.toCharArray()) {
               if (!this.symbolMap.containsKey(c)) {
                  this.symbolMap.put(c, null);
               }
            }
         }

         this.depth.add(aisle);
         return this;
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }
}
