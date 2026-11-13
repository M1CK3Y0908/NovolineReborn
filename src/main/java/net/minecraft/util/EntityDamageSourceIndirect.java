package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {
   private Entity indirectEntity;

   @Override
   public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      IChatComponent chatComponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack itemStack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
      String text = "death.attack." + this.damageType;
      String text2 = text + ".item";
      return itemStack != null && itemStack.hasDisplayName() && StatCollector.canTranslate(text2)
         ? new ChatComponentTranslation(text2, entityLivingBaseIn.getDisplayName(), chatComponent, itemStack.getChatComponent())
         : new ChatComponentTranslation(text, entityLivingBaseIn.getDisplayName(), chatComponent);
   }

   @Override
   public Entity getSourceOfDamage() {
      return this.damageSourceEntity;
   }

   public EntityDamageSourceIndirect(String damageTypeIn, Entity source, Entity indirectEntityIn) {
      super(damageTypeIn, source);
      this.indirectEntity = indirectEntityIn;
   }

   @Override
   public Entity getEntity() {
      return this.indirectEntity;
   }
}
