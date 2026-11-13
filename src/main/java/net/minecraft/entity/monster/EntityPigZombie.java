package net.minecraft.entity.monster;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityPigZombie extends EntityZombie {
   private int angerLevel;
   private int randomSoundDelay;
   private static final UUID ATTACK_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private UUID angerTargetUUID;
   private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = new AttributeModifier(
         ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05, 0
      )
      .getAttributeModifier();

   @Override
   protected String getHurtSound() {
      return "mob.zombiepig.zpighurt";
   }

   @Override
   protected void applyEntityAI() {
      this.targetTasks.addTask(1, new EntityPigZombie.AIHurtByAggressor(this));
      this.targetTasks.addTask(2, new EntityPigZombie.AITargetAggressor(this));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.angerLevel = tagCompund.getShort("Anger");
      String text = tagCompund.getString("HurtBy");
      if (text.length() > 0) {
         this.angerTargetUUID = UUID.fromString(text);
         EntityPlayer entityPlayer = this.worldObj.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(entityPlayer);
         if (entityPlayer != null) {
            this.attackingPlayer = entityPlayer;
            this.recentlyHit = this.getRevengeTimer();
         }
      }
   }

   private void becomeAngryAt(Entity entity2) {
      this.angerLevel = 400 + this.rand.nextInt(400);
      this.randomSoundDelay = this.rand.nextInt(40);
      if (entity2 instanceof EntityLivingBase) {
         this.setRevengeTarget((EntityLivingBase)entity2);
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      Entity entity = source.getEntity();
      if (entity instanceof EntityPlayer) {
         this.becomeAngryAt(entity);
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(reinforcementChance).setBaseValue(0.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0);
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setShort("Anger", (short)this.angerLevel);
      if (this.angerTargetUUID != null) {
         tagCompound.setString("HurtBy", this.angerTargetUUID.toString());
      } else {
         tagCompound.setString("HurtBy", "");
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      super.onInitialSpawn(difficulty, livingdata);
      this.setVillager(false);
      return livingdata;
   }

   @Override
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      this.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(2 + lootingModifier);

      for (int i2 = 0; i2 < i; i2++) {
         this.getEntityItem(Items.rotten_flesh);
      }

      i = this.rand.nextInt(2 + lootingModifier);

      for (int i3 = 0; i3 < i; i3++) {
         this.getEntityItem(Items.gold_nugget);
      }
   }

   public EntityPigZombie(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = true;
   }

   @Override
   protected void addRandomDrop() {
      this.getEntityItem(Items.gold_ingot);
   }

   @Override
   protected void updateAITasks() {
      IAttributeInstance attributeInstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (this.isAngry()) {
         if (!this.isChild() && !attributeInstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
            attributeInstance.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
         }

         this.angerLevel--;
      } else if (attributeInstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
         attributeInstance.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
      }

      if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
         this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.angerLevel > 0 && this.angerTargetUUID != null && this.getAITarget() == null) {
         EntityPlayer entityPlayer = this.worldObj.getPlayerEntityByUUID(this.angerTargetUUID);
         this.setRevengeTarget(entityPlayer);
         this.attackingPlayer = entityPlayer;
         this.recentlyHit = this.getRevengeTimer();
      }

      super.updateAITasks();
   }

   @Override
   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
   }

   @Override
   public void setRevengeTarget(EntityLivingBase livingBase) {
      super.setRevengeTarget(livingBase);
      if (livingBase != null) {
         this.angerTargetUUID = livingBase.getUniqueID();
      }
   }

   @Override
   protected String getDeathSound() {
      return "mob.zombiepig.zpigdeath";
   }

   public boolean isAngry() {
      return this.angerLevel > 0;
   }

   @Override
   public boolean interact(EntityPlayer player) {
      return false;
   }

   @Override
   protected String getLivingSound() {
      return "mob.zombiepig.zpig";
   }

   static class AIHurtByAggressor extends EntityAIHurtByTarget {
      public AIHurtByAggressor(EntityPigZombie entityPigZombie) {
         super(entityPigZombie, true);
      }

      @Override
      protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
         super.setEntityAttackTarget(creatureIn, entityLivingBaseIn);
         if (creatureIn instanceof EntityPigZombie) {
            ((EntityPigZombie)creatureIn).becomeAngryAt(entityLivingBaseIn);
         }
      }
   }

   static class AITargetAggressor extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AITargetAggressor(EntityPigZombie entityPigZombie) {
         super(entityPigZombie, EntityPlayer.class, true);
      }

      @Override
      public boolean shouldExecute() {
         return ((EntityPigZombie)this.taskOwner).isAngry() && super.shouldExecute();
      }
   }
}
