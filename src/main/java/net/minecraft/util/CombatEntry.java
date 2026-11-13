package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;

public class CombatEntry {
   private final DamageSource damageSrc;
   private final int time;
   private final float damage;
   private final float health;
   private final String fallSuffix;
   private final float fallDistance;

   public boolean isLivingDamageSrc() {
      return this.damageSrc.getEntity() instanceof EntityLivingBase;
   }

   public String getFallSuffix() {
      return this.fallSuffix;
   }

   public IChatComponent getDamageSrcDisplayName() {
      return this.getDamageSrc().getEntity() == null ? null : this.getDamageSrc().getEntity().getDisplayName();
   }

   public DamageSource getDamageSrc() {
      return this.damageSrc;
   }

   public float getDamageAmount() {
      return this.damageSrc == DamageSource.outOfWorld ? Float.MAX_VALUE : this.fallDistance;
   }

   public CombatEntry(DamageSource damageSrcIn, int timeIn, float healthAmount, float damageAmount, String fallSuffixIn, float fallDistanceIn) {
      this.damageSrc = damageSrcIn;
      this.time = timeIn;
      this.damage = damageAmount;
      this.health = healthAmount;
      this.fallSuffix = fallSuffixIn;
      this.fallDistance = fallDistanceIn;
   }

   public float getDamage() {
      return this.damage;
   }
}
