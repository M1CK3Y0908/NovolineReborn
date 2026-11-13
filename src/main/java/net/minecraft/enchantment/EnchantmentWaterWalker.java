package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWalker extends Enchantment {
   public EnchantmentWaterWalker(ResourceLocation resourceLocation) {
      super(8, resourceLocation, 2, EnumEnchantmentType.ARMOR_FEET);
      this.setName("waterWalker");
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return enchantmentLevel * 10;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
