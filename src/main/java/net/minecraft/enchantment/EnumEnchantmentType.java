package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {
   ALL,
   ARMOR,
   ARMOR_FEET,
   ARMOR_LEGS,
   ARMOR_TORSO,
   ARMOR_HEAD,
   WEAPON,
   DIGGER,
   FISHING_ROD,
   BREAKABLE,
   BOW;

   public boolean canEnchantItem(Item item2) {
      if (this == ALL) {
         return true;
      }

      if (this == BREAKABLE && item2.isDamageable()) {
         return true;
      }

      if (item2 instanceof ItemArmor) {
         if (this == ARMOR) {
            return true;
         }

         ItemArmor itemArmor = (ItemArmor)item2;
         return itemArmor.armorType == 0
            ? this == ARMOR_HEAD
            : (
               itemArmor.armorType == 2
                  ? this == ARMOR_LEGS
                  : (itemArmor.armorType == 1 ? this == ARMOR_TORSO : (itemArmor.armorType == 3 ? this == ARMOR_FEET : false))
            );
      } else {
         return item2 instanceof ItemSword
            ? this == WEAPON
            : (
               item2 instanceof ItemTool
                  ? this == DIGGER
                  : (item2 instanceof ItemBow ? this == BOW : (item2 instanceof ItemFishingRod ? this == FISHING_ROD : false))
            );
      }
   }
}
