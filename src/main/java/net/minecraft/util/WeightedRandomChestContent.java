package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;

public class WeightedRandomChestContent extends WeightedRandom.Item {
   private ItemStack theItemId;
   private int minStackSize;
   private int maxStackSize;

   public static void generateChestContents(Random random, List<WeightedRandomChestContent> listIn, IInventory inv, int max) {
      for (int index = 0; index < max; index++) {
         WeightedRandomChestContent weightedRandomChestContent = WeightedRandom.getRandomItem(random, listIn);
         int i = weightedRandomChestContent.minStackSize
            + random.nextInt(weightedRandomChestContent.maxStackSize - weightedRandomChestContent.minStackSize + 1);
         if (weightedRandomChestContent.theItemId.getMaxStackSize() >= i) {
            ItemStack itemStack = weightedRandomChestContent.theItemId.copy();
            itemStack.stackSize = i;
            inv.setInventorySlotContents(random.nextInt(inv.getSizeInventory()), itemStack);
         } else {
            for (int nestedIndex = 0; nestedIndex < i; nestedIndex++) {
               ItemStack itemStack2 = weightedRandomChestContent.theItemId.copy();
               itemStack2.stackSize = 1;
               inv.setInventorySlotContents(random.nextInt(inv.getSizeInventory()), itemStack2);
            }
         }
      }
   }

   public WeightedRandomChestContent(ItemStack itemStack3, int i, int i2) {
      super(i2);
      this.theItemId = itemStack3;
      this.minStackSize = 1;
      this.maxStackSize = i;
   }

   public static void handleRandom(Random random2, List list2, TileEntityDispenser tileEntityDispenser) {
      for (int i = 0; i < 2; i++) {
         WeightedRandomChestContent weightedRandomChestContent2 = (WeightedRandomChestContent) WeightedRandom.getRandomItem(random2, list2);
         int i2 = weightedRandomChestContent2.minStackSize + random2.nextInt(weightedRandomChestContent2.maxStackSize - weightedRandomChestContent2.minStackSize + 1);
         if (weightedRandomChestContent2.theItemId.getMaxStackSize() >= i2) {
            ItemStack itemStack3 = weightedRandomChestContent2.theItemId.copy();
            itemStack3.stackSize = i2;
            tileEntityDispenser.setInventorySlotContents(random2.nextInt(tileEntityDispenser.getSizeInventory()), itemStack3);
         } else {
            for (int i3 = 0; i3 < i2; i3++) {
               ItemStack itemStack4 = weightedRandomChestContent2.theItemId.copy();
               itemStack4.stackSize = 1;
               tileEntityDispenser.setInventorySlotContents(random2.nextInt(tileEntityDispenser.getSizeInventory()), itemStack4);
            }
         }
      }
   }

   public WeightedRandomChestContent(Item item, int metadata, int minimumChance, int maximumChance, int itemWeightIn) {
      super(itemWeightIn);
      this.theItemId = new ItemStack(item, 1, metadata);
      this.minStackSize = minimumChance;
      this.maxStackSize = maximumChance;
   }

   public static List<WeightedRandomChestContent> copyWithAddition(List<WeightedRandomChestContent> contents, WeightedRandomChestContent... additionalContents) {
      List<WeightedRandomChestContent> list = Lists.newArrayList(contents);
      Collections.addAll(list, additionalContents);
      return list;
   }
}
