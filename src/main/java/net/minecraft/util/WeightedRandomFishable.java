package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom.Item {
   private boolean enchantable;
   private final ItemStack returnStack;
   private float maxDamagePercent;

   public WeightedRandomFishable(ItemStack returnStackIn, int itemWeightIn) {
      super(itemWeightIn);
      this.returnStack = returnStackIn;
   }

   public ItemStack getItemStack(Random random) {
      ItemStack itemStack = this.returnStack.copy();
      if (this.maxDamagePercent > 0.0F) {
         int i = (int)(this.maxDamagePercent * this.returnStack.getMaxDamage());
         int i2 = itemStack.getMaxDamage() - random.nextInt(random.nextInt(i) + 1);
         if (i2 > i) {
            i2 = i;
         }

         if (i2 < 1) {
            i2 = 1;
         }

         itemStack.setItemDamage(i2);
      }

      if (this.enchantable) {
         EnchantmentHelper.addRandomEnchantment(random, itemStack, 30);
      }

      return itemStack;
   }

   public WeightedRandomFishable setMaxDamagePercent(float maxDamagePercentIn) {
      this.maxDamagePercent = maxDamagePercentIn;
      return this;
   }

   public WeightedRandomFishable setEnchantable() {
      this.enchantable = true;
      return this;
   }
}
