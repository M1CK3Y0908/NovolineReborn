package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowKnockback extends Enchantment {
   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 25;
   }

   public EnchantmentArrowKnockback(ResourceLocation resourceLocation) {
      super(49, resourceLocation, 2, EnumEnchantmentType.BOW);
      this.setName("arrowKnockback");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 12 + (enchantmentLevel - 1) * 20;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
