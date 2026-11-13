package net.optifine.config;

import net.minecraft.enchantment.Enchantment;

public class ParserEnchantmentId implements IntGetter {
   @Override
   public int getInt(String text) {
      Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(text);
      return enchantmentByLocation == null ? Integer.MIN_VALUE : enchantmentByLocation.effectId;
   }
}
