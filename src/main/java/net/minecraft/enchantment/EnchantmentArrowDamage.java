package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowDamage extends Enchantment {
   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + (enchantmentLevel - 1) * 10;
   }

   public EnchantmentArrowDamage(ResourceLocation resourceLocation) {
      super(48, resourceLocation, 10, EnumEnchantmentType.BOW);
      this.setName("arrowDamage");
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }
}
