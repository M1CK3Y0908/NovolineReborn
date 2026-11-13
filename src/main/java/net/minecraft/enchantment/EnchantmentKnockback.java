package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentKnockback extends Enchantment {
   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + 20 * (enchantmentLevel - 1);
   }

   protected EnchantmentKnockback(ResourceLocation resourceLocation) {
      super(19, resourceLocation, 5, EnumEnchantmentType.WEAPON);
      this.setName("knockback");
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
