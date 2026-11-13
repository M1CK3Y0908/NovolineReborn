package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CombatTracker {
   private int combatStartTime;
   private final List<CombatEntry> combatEntries = Lists.newArrayList();
   private int lastDamageTime;
   private boolean inCombat;
   private final EntityLivingBase fighter;
   private int combatEndTime;
   private boolean takingDamage;
   private String fallSuffix;

   public int getCombatDuration() {
      return this.inCombat ? this.fighter.ticksExisted - this.combatStartTime : this.combatEndTime - this.combatStartTime;
   }

   private String getFallSuffix(CombatEntry combatEntry) {
      return combatEntry.getFallSuffix() == null ? "generic" : combatEntry.getFallSuffix();
   }

   public CombatTracker(EntityLivingBase fighterIn) {
      this.fighter = fighterIn;
   }

   public EntityLivingBase getFighter() {
      return this.fighter;
   }

   public void updateFallSuffix() {
      this.resetFallSuffix();
      if (this.fighter.isOnLadder()) {
         Block block = this.fighter
            .worldObj
            .getBlockState(new BlockPos(this.fighter.posX, this.fighter.getEntityBoundingBox().minY, this.fighter.posZ))
            .getBlock();
         if (block == Blocks.ladder) {
            this.fallSuffix = "ladder";
         } else if (block == Blocks.vine) {
            this.fallSuffix = "vines";
         }
      } else if (this.fighter.isInWater()) {
         this.fallSuffix = "water";
      }
   }

   private CombatEntry getBestCombatEntry() {
      CombatEntry combatEntry2 = null;
      CombatEntry combatEntry3 = null;
      float f = 0.0F;

      for (int i = 0; i < this.combatEntries.size(); i++) {
         CombatEntry combatEntry4 = this.combatEntries.get(i);
         CombatEntry combatEntry5 = i > 0 ? this.combatEntries.get(i - 1) : null;
         if ((combatEntry4.getDamageSrc() == DamageSource.fall || combatEntry4.getDamageSrc() == DamageSource.outOfWorld)
            && combatEntry4.getDamageAmount() > 0.0F
            && (combatEntry2 == null || combatEntry4.getDamageAmount() > f)) {
            if (i > 0) {
               combatEntry2 = combatEntry5;
            } else {
               combatEntry2 = combatEntry4;
            }

            f = combatEntry4.getDamageAmount();
         }

         if (combatEntry4.getFallSuffix() != null && (combatEntry3 == null || combatEntry4.getDamage() > 0)) {
            combatEntry3 = combatEntry4;
         }
      }

      return f > 5.0F && combatEntry2 != null ? combatEntry2 : null;
   }

   public void reset() {
      int i = this.inCombat ? 300 : 100;
      if (this.takingDamage && (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.lastDamageTime > i)) {
         boolean conditionMet = this.inCombat;
         this.takingDamage = false;
         this.inCombat = false;
         this.combatEndTime = this.fighter.ticksExisted;
         if (conditionMet) {
            this.fighter.sendEndCombat();
         }

         this.combatEntries.clear();
      }
   }

   public EntityLivingBase getAttacker() {
      EntityLivingBase entityLivingBase = null;
      EntityPlayer entityPlayer = null;
      float f = 0.0F;
      float f2 = 0.0F;

      for (CombatEntry combatEntry2 : this.combatEntries) {
         if (combatEntry2.getDamageSrc().getEntity() instanceof EntityPlayer && (entityPlayer == null || combatEntry2.getDamage() > f2)) {
            f2 = combatEntry2.getDamage();
            entityPlayer = (EntityPlayer)combatEntry2.getDamageSrc().getEntity();
         }

         if (combatEntry2.getDamageSrc().getEntity() instanceof EntityLivingBase && (entityLivingBase == null || combatEntry2.getDamage() > f)) {
            f = combatEntry2.getDamage();
            entityLivingBase = (EntityLivingBase)combatEntry2.getDamageSrc().getEntity();
         }
      }

      return entityPlayer != null && f2 >= f / 3.0F ? entityPlayer : entityLivingBase;
   }

   public IChatComponent getDeathMessage() {
      if (this.combatEntries.size() == 0) {
         return new ChatComponentTranslation("death.attack.generic", this.fighter.getDisplayName());
      }

      CombatEntry bestCombatEntry = this.getBestCombatEntry();
      CombatEntry combatEntry2 = this.combatEntries.get(this.combatEntries.size() - 1);
      IChatComponent damageSrcDisplayName = combatEntry2.getDamageSrcDisplayName();
      Entity entity2 = combatEntry2.getDamageSrc().getEntity();
      IChatComponent iChatComponent;
      if (bestCombatEntry != null && combatEntry2.getDamageSrc() == DamageSource.fall) {
         IChatComponent damageSrcDisplayName2 = bestCombatEntry.getDamageSrcDisplayName();
         if (bestCombatEntry.getDamageSrc() == DamageSource.fall || bestCombatEntry.getDamageSrc() == DamageSource.outOfWorld) {
            iChatComponent = new ChatComponentTranslation("death.fell.accident." + this.getFallSuffix(bestCombatEntry), this.fighter.getDisplayName());
         } else if (damageSrcDisplayName2 != null && (damageSrcDisplayName == null || !damageSrcDisplayName2.equals(damageSrcDisplayName))) {
            Entity entity3 = bestCombatEntry.getDamageSrc().getEntity();
            ItemStack itemStack = entity3 instanceof EntityLivingBase ? ((EntityLivingBase)entity3).getHeldItem() : null;
            if (itemStack != null && itemStack.hasDisplayName()) {
               iChatComponent = new ChatComponentTranslation("death.fell.assist.item", this.fighter.getDisplayName(), damageSrcDisplayName2, itemStack.getChatComponent());
            } else {
               iChatComponent = new ChatComponentTranslation("death.fell.assist", this.fighter.getDisplayName(), damageSrcDisplayName2);
            }
         } else if (damageSrcDisplayName != null) {
            ItemStack itemStack2 = entity2 instanceof EntityLivingBase ? ((EntityLivingBase)entity2).getHeldItem() : null;
            if (itemStack2 != null && itemStack2.hasDisplayName()) {
               iChatComponent = new ChatComponentTranslation("death.fell.finish.item", this.fighter.getDisplayName(), damageSrcDisplayName, itemStack2.getChatComponent());
            } else {
               iChatComponent = new ChatComponentTranslation("death.fell.finish", this.fighter.getDisplayName(), damageSrcDisplayName);
            }
         } else {
            iChatComponent = new ChatComponentTranslation("death.fell.killer", this.fighter.getDisplayName());
         }
      } else {
         iChatComponent = combatEntry2.getDamageSrc().getDeathMessage(this.fighter);
      }

      return iChatComponent;
   }

   public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount) {
      this.reset();
      this.updateFallSuffix();
      CombatEntry combatEntry2 = new CombatEntry(damageSrc, this.fighter.ticksExisted, healthIn, damageAmount, this.fallSuffix, this.fighter.fallDistance);
      this.combatEntries.add(combatEntry2);
      this.lastDamageTime = this.fighter.ticksExisted;
      this.takingDamage = true;
      if (combatEntry2.isLivingDamageSrc() && !this.inCombat && this.fighter.isEntityAlive()) {
         this.inCombat = true;
         this.combatStartTime = this.fighter.ticksExisted;
         this.combatEndTime = this.combatStartTime;
         this.fighter.sendEnterCombat();
      }
   }

   private void resetFallSuffix() {
      this.fallSuffix = null;
   }
}
