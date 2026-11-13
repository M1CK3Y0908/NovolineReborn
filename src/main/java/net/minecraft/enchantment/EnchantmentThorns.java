package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EnchantmentThorns extends Enchantment {
   public EnchantmentThorns(ResourceLocation resourceLocation) {
      super(7, resourceLocation, 1, EnumEnchantmentType.ARMOR_TORSO);
      this.setName("thorns");
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.getItem() instanceof ItemArmor ? true : super.canApply(stack);
   }

   public static boolean shouldHit(int level, Random random) {
      return level <= 0 ? false : random.nextFloat() < 0.15F * level;
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   @Override
   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
      Random random = user.getRNG();
      ItemStack itemStack = EnchantmentHelper.getEnchantedItem(Enchantment.thorns, user);
      if (shouldHit(level, random)) {
         if (attacker != null) {
            attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), getDamage(level, random));
            attacker.playSound("damage.thorns", 0.5F, 1.0F);
         }

         if (itemStack != null) {
            itemStack.damageItem(3, user);
         }
      } else if (itemStack != null) {
         itemStack.damageItem(1, user);
      }
   }

   public static int getDamage(int level, Random random) {
      return level > 10 ? level - 10 : 1 + random.nextInt(4);
   }
}
