package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFireAspect extends Enchantment {
   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }

   protected EnchantmentFireAspect(ResourceLocation resourceLocation) {
      super(20, resourceLocation, 2, EnumEnchantmentType.WEAPON);
      this.setName("fire");
   }
}
