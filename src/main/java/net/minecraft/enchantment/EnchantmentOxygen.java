package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentOxygen extends Enchantment {
   @Override
   public int getMaxLevel() {
      return 3;
   }

   public EnchantmentOxygen(ResourceLocation resourceLocation) {
      super(5, resourceLocation, 2, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("oxygen");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 * enchantmentLevel;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 30;
   }
}
