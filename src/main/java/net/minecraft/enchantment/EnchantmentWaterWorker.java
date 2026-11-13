package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(ResourceLocation resourceLocation) {
      super(6, resourceLocation, 2, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("waterWorker");
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 40;
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 1;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }
}
