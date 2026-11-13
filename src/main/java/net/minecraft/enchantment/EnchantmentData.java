package net.minecraft.enchantment;


public class EnchantmentData extends net.minecraft.util.WeightedRandom.Item {
   public final Enchantment enchantmentobj;
   public final int enchantmentLevel;

   public EnchantmentData(Enchantment enchantmentObj, int enchLevel) {
      super(enchantmentObj.getWeight());
      this.enchantmentobj = enchantmentObj;
      this.enchantmentLevel = enchLevel;
   }
}
