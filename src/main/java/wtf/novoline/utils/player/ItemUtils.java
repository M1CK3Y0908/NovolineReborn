package wtf.novoline.utils.player;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ItemUtils {
   public static int getArmorScore(ItemStack itemStack) {
      ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
      return 0
         + itemArmor.damageReduceAmount
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack) * 4
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, itemStack);
   }

   public static int getMaterialScore(String text) {
      switch (text) {
         case "EMERALD":
         case "DIAMOND":
            return 3;
         case "IRON":
            return 2;
         case "STONE":
            return 1;
         case "GOLD":
         case "WOOD":
            return 0;
         default:
            return 0;
      }
   }

   public static int getToolScore(ItemStack itemStack) {
      Item item2 = itemStack.getItem();
      int i = 0;
      if (item2 instanceof ItemSword itemSword) {
         i = 0 + getMaterialScore(itemSword.getToolMaterialName());
         i = (int)(i + itemSword.attackDamage);
      } else if (item2 instanceof ItemTool itemTool) {
         i = 0 + getMaterialScore(itemTool.getToolMaterialName());
      } else if (item2 instanceof ItemHoe itemHoe) {
         i = 0 + getMaterialScore(itemHoe.getMaterialName());
      }

      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 4;
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 2;
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.smite.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack);
      return i + EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack);
   }
}
