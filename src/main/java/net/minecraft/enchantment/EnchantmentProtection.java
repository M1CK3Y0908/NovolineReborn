package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class EnchantmentProtection extends Enchantment {
   private static final String[] protectionName = new String[]{"all", "fire", "fall", "explosion", "projectile"};
   public final int protectionType;
   private static final int[] baseEnchantability = new int[]{1, 10, 5, 5, 3};
   private static final int[] levelEnchantability = new int[]{11, 8, 6, 8, 6};
   private static final int[] thresholdEnchantability = new int[]{20, 12, 10, 12, 15};

   public static double getBlastDamageReduction(Entity entity, double damage) {
      int maxEnchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.blastProtection.effectId, entity.getInventory());
      if (maxEnchantmentLevel > 0) {
         damage -= MathHelper.floor_double(damage * (maxEnchantmentLevel * 0.15F));
      }

      return damage;
   }

   public static int getFireTimeForEntity(Entity entity, int fireTime) {
      int maxEnchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.fireProtection.effectId, entity.getInventory());
      if (maxEnchantmentLevel > 0) {
         fireTime -= MathHelper.floor_float((float)fireTime * maxEnchantmentLevel * 0.15F);
      }

      return fireTime;
   }

   @Override
   public String getName() {
      return "enchantment.protect." + protectionName[this.protectionType];
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return baseEnchantability[this.protectionType] + (enchantmentLevel - 1) * levelEnchantability[this.protectionType];
   }

   @Override
   public boolean canApplyTogether(Enchantment ench) {
      if (!(ench instanceof EnchantmentProtection enchantmentProtection)) {
         return super.canApplyTogether(ench);
      } else {
         return enchantmentProtection.protectionType == this.protectionType ? false : this.protectionType == 2 || enchantmentProtection.protectionType == 2;
      }
   }

   @Override
   public int getMaxLevel() {
      return 4;
   }

   @Override
   public int calcModifierDamage(int level, DamageSource source) {
      if (source.canHarmInCreative()) {
         return 0;
      }

      float f = (6 + level * level) / 3.0F;
      return this.protectionType == 0
         ? MathHelper.floor_float(f * 0.75F)
         : (
            this.protectionType == 1 && source.isFireDamage()
               ? MathHelper.floor_float(f * 1.25F)
               : (
                  this.protectionType == 2 && source == DamageSource.fall
                     ? MathHelper.floor_float(f * 2.5F)
                     : (
                        this.protectionType == 3 && source.isExplosion()
                           ? MathHelper.floor_float(f * 1.5F)
                           : (this.protectionType == 4 && source.isProjectile() ? MathHelper.floor_float(f * 1.5F) : 0)
                     )
               )
         );
   }

   public EnchantmentProtection(int i, ResourceLocation resourceLocation, int i2, int i3) {
      super(i, resourceLocation, i2, EnumEnchantmentType.ARMOR);
      this.protectionType = i3;
      if (i3 == 2) {
         this.type = EnumEnchantmentType.ARMOR_FEET;
      }
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + thresholdEnchantability[this.protectionType];
   }
}
