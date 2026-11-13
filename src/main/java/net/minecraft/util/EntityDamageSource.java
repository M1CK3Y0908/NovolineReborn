package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {
   private boolean isThornsDamage = false;
   protected Entity damageSourceEntity;

   public boolean getIsThornsDamage() {
      return this.isThornsDamage;
   }

   @Override
   public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      ItemStack itemStack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
      String text = "death.attack." + this.damageType;
      String text2 = text + ".item";
      return itemStack != null && itemStack.hasDisplayName() && StatCollector.canTranslate(text2)
         ? new ChatComponentTranslation(text2, entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName(), itemStack.getChatComponent())
         : new ChatComponentTranslation(text, entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName());
   }

   @Override
   public boolean isDifficultyScaled() {
      return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
   }

   public EntityDamageSource(String damageTypeIn, Entity damageSourceEntityIn) {
      super(damageTypeIn);
      this.damageSourceEntity = damageSourceEntityIn;
   }

   @Override
   public Entity getEntity() {
      return this.damageSourceEntity;
   }

   public EntityDamageSource setIsThornsDamage() {
      this.isThornsDamage = true;
      return this;
   }
}
