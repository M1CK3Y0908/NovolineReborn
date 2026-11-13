package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
   protected EntityAISit aiSit = new EntityAISit(this);

   public EntityLivingBase getOwner() {
      try {
         UUID uuid = UUID.fromString(this.getOwnerId());
         return uuid == null ? null : this.worldObj.getPlayerEntityByUUID(uuid);
      } catch (IllegalArgumentException illegalArgumentException) {
         return null;
      }
   }

   public void setSitting(boolean sitting) {
      byte b0 = this.dataWatcher.getWatchableObjectByte(16);
      if (sitting) {
         this.dataWatcher.updateObject(16, (byte)(b0 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b0 & -2));
      }
   }

   @Override
   public boolean isOnSameTeam(EntityLivingBase otherEntity) {
      if (this.isTamed()) {
         EntityLivingBase entityLivingBase = this.getOwner();
         if (otherEntity == entityLivingBase) {
            return true;
         }

         if (entityLivingBase != null) {
            return entityLivingBase.isOnSameTeam(otherEntity);
         }
      }

      return super.isOnSameTeam(otherEntity);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      String text;
      if (tagCompund.hasKey("OwnerUUID", 8)) {
         text = tagCompund.getString("OwnerUUID");
      } else {
         text = PreYggdrasilConverter.getStringUUIDFromName(tagCompund.getString("Owner"));
      }

      if (text.length() > 0) {
         this.setOwnerId(text);
         this.setTamed(true);
      }

      this.aiSit.setSitting(tagCompund.getBoolean("Sitting"));
      this.setSitting(tagCompund.getBoolean("Sitting"));
   }

   public boolean isOwner(EntityLivingBase entityIn) {
      return entityIn == this.getOwner();
   }

   public boolean isTamed() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      if (this.getOwnerId() == null) {
         tagCompound.setString("OwnerUUID", "");
      } else {
         tagCompound.setString("OwnerUUID", this.getOwnerId());
      }

      tagCompound.setBoolean("Sitting", this.isSitting());
   }

   public EntityTameable(World worldIn) {
      super(worldIn);
      this.setupTamedAI();
   }

   protected void setupTamedAI() {
   }

   protected void playTameEffect(boolean play) {
      EnumParticleTypes particleTypes = EnumParticleTypes.HEART;
      if (!play) {
         particleTypes = EnumParticleTypes.SMOKE_NORMAL;
      }

      for (int index = 0; index < 7; index++) {
         double d = this.rand.nextGaussian() * 0.02;
         double d2 = this.rand.nextGaussian() * 0.02;
         double d3 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               particleTypes,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 0.5 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               d,
               d2,
               d3
            );
      }
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
      this.dataWatcher.addObject(17, "");
   }

   @Override
   public Team getTeam() {
      if (this.isTamed()) {
         EntityLivingBase entityLivingBase = this.getOwner();
         if (entityLivingBase != null) {
            return entityLivingBase.getTeam();
         }
      }

      return super.getTeam();
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.playTameEffect(true);
      } else if (id == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public void setTamed(boolean tamed) {
      byte b0 = this.dataWatcher.getWatchableObjectByte(16);
      if (tamed) {
         this.dataWatcher.updateObject(16, (byte)(b0 | 4));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b0 & -5));
      }

      this.setupTamedAI();
   }

   public EntityAISit getAISit() {
      return this.aiSit;
   }

   public boolean shouldAttackEntity(EntityLivingBase entityLivingBase2, EntityLivingBase entityLivingBase3) {
      return true;
   }

   @Override
   public String getOwnerId() {
      return this.dataWatcher.getWatchableObjectString(17);
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (!this.worldObj.isRemote
         && this.worldObj.getGameRules().getBoolean("showDeathMessages")
         && this.hasCustomName()
         && this.getOwner() instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.getOwner()).addChatMessage(this.getCombatTracker().getDeathMessage());
      }

      super.onDeath(cause);
   }

   public boolean isSitting() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setOwnerId(String ownerUuid) {
      this.dataWatcher.updateObject(17, ownerUuid);
   }
}
