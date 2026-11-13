package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowFire extends Enchantment {
   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 20;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }

   public EnchantmentArrowFire(ResourceLocation resourceLocation) {
      super(50, resourceLocation, 2, EnumEnchantmentType.BOW);
      this.setName("arrowFire");
   }
}
