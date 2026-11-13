package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.world.Explosion;

public class DamageSource {
   public static DamageSource inFire = new DamageSource("inFire").setFireDamage();
   public static DamageSource lightningBolt = new DamageSource("lightningBolt");
   public static DamageSource onFire = new DamageSource("onFire").setDamageBypassesArmor().setFireDamage();
   public static DamageSource lava = new DamageSource("lava").setFireDamage();
   public static DamageSource inWall = new DamageSource("inWall").setDamageBypassesArmor();
   private boolean magicDamage;
   private boolean isDamageAllowedInCreativeMode;
   public static DamageSource drown = new DamageSource("drown").setDamageBypassesArmor();
   public static DamageSource starve = new DamageSource("starve").setDamageBypassesArmor().setDamageIsAbsolute();
   public static DamageSource cactus = new DamageSource("cactus");
   public static DamageSource fall = new DamageSource("fall").setDamageBypassesArmor();
   public static DamageSource outOfWorld = new DamageSource("outOfWorld").setDamageBypassesArmor().setDamageAllowedInCreativeMode();
   private boolean isUnblockable;
   private boolean explosion;
   public static DamageSource generic = new DamageSource("generic").setDamageBypassesArmor();
   private boolean damageIsAbsolute;
   public static DamageSource magic = new DamageSource("magic").setDamageBypassesArmor().setMagicDamage();
   public static DamageSource wither = new DamageSource("wither").setDamageBypassesArmor();
   public static DamageSource anvil = new DamageSource("anvil");
   public static DamageSource fallingBlock = new DamageSource("fallingBlock");
   private boolean difficultyScaled;
   private float hungerDamage = 0.3F;
   private boolean fireDamage;
   public String damageType;
   private boolean projectile;

   public static DamageSource causeFireballDamage(EntityFireball fireball, Entity indirectEntityIn) {
      return indirectEntityIn == null
         ? new EntityDamageSourceIndirect("onFire", fireball, fireball).setFireDamage().setProjectile()
         : new EntityDamageSourceIndirect("fireball", fireball, indirectEntityIn).setFireDamage().setProjectile();
   }

   protected DamageSource setDamageAllowedInCreativeMode() {
      this.isDamageAllowedInCreativeMode = true;
      return this;
   }

   public static DamageSource causeArrowDamage(EntityArrow arrow, Entity indirectEntityIn) {
      return new EntityDamageSourceIndirect("arrow", arrow, indirectEntityIn).setProjectile();
   }

   public static DamageSource causeThornsDamage(Entity source) {
      return new EntityDamageSource("thorns", source).setIsThornsDamage().setMagicDamage();
   }

   public DamageSource setMagicDamage() {
      this.magicDamage = true;
      return this;
   }

   public boolean isMagicDamage() {
      return this.magicDamage;
   }

   protected DamageSource setDamageBypassesArmor() {
      this.isUnblockable = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public static DamageSource causeIndirectMagicDamage(Entity source, Entity indirectEntityIn) {
      return new EntityDamageSourceIndirect("indirectMagic", source, indirectEntityIn).setDamageBypassesArmor().setMagicDamage();
   }

   protected DamageSource setDamageIsAbsolute() {
      this.damageIsAbsolute = true;
      this.hungerDamage = 0.0F;
      return this;
   }

   public boolean isFireDamage() {
      return this.fireDamage;
   }

   public Entity getSourceOfDamage() {
      return this.getEntity();
   }

   protected DamageSource(String damageTypeIn) {
      this.damageType = damageTypeIn;
   }

   public static DamageSource causeThrownDamage(Entity source, Entity indirectEntityIn) {
      return new EntityDamageSourceIndirect("thrown", source, indirectEntityIn).setProjectile();
   }

   public boolean isUnblockable() {
      return this.isUnblockable;
   }

   public boolean canHarmInCreative() {
      return this.isDamageAllowedInCreativeMode;
   }

   public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      EntityLivingBase entityLivingBase = entityLivingBaseIn.getAttackingEntity();
      String text = "death.attack." + this.damageType;
      String text2 = text + ".player";
      return entityLivingBase != null && StatCollector.canTranslate(text2)
         ? new ChatComponentTranslation(text2, entityLivingBaseIn.getDisplayName(), entityLivingBase.getDisplayName())
         : new ChatComponentTranslation(text, entityLivingBaseIn.getDisplayName());
   }

   public boolean isDifficultyScaled() {
      return this.difficultyScaled;
   }

   protected DamageSource setFireDamage() {
      this.fireDamage = true;
      return this;
   }

   public boolean isDamageAbsolute() {
      return this.damageIsAbsolute;
   }

   public DamageSource setProjectile() {
      this.projectile = true;
      return this;
   }

   public boolean isCreativePlayer() {
      Entity entity = this.getEntity();
      return entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode;
   }

   public boolean isExplosion() {
      return this.explosion;
   }

   public DamageSource setExplosion() {
      this.explosion = true;
      return this;
   }

   public float getHungerDamage() {
      return this.hungerDamage;
   }

   public static DamageSource causeMobDamage(EntityLivingBase mob) {
      return new EntityDamageSource("mob", mob);
   }

   public DamageSource setDifficultyScaled() {
      this.difficultyScaled = true;
      return this;
   }

   public static DamageSource setExplosionSource(Explosion explosionIn) {
      return explosionIn != null && explosionIn.getExplosivePlacedBy() != null
         ? new EntityDamageSource("explosion.player", explosionIn.getExplosivePlacedBy()).setDifficultyScaled().setExplosion()
         : new DamageSource("explosion").setDifficultyScaled().setExplosion();
   }

   public boolean isProjectile() {
      return this.projectile;
   }

   public static DamageSource causePlayerDamage(EntityPlayer player) {
      return new EntityDamageSource("player", player);
   }

   public Entity getEntity() {
      return null;
   }

   public String getDamageType() {
      return this.damageType;
   }
}
