package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowInfinite extends Enchantment {
   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 20;
   }

   public EnchantmentArrowInfinite(ResourceLocation resourceLocation) {
      super(51, resourceLocation, 1, EnumEnchantmentType.BOW);
      this.setName("arrowInfinite");
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }
}
