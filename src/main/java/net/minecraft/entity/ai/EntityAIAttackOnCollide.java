package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {
   int attackTick;
   protected EntityCreature attacker;
   private int delayCounter;
   World worldObj;
   private double targetX;
   Class<? extends Entity> classTarget;
   private double targetY;
   private double targetZ;
   double speedTowardsTarget;
   PathEntity entityPathEntity;
   boolean longMemory;

   @Override
   public boolean continueExecuting() {
      EntityLivingBase entityLivingBase = this.attacker.getAttackTarget();
      return entityLivingBase == null
         ? false
         : (
            !entityLivingBase.isEntityAlive()
               ? false
               : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entityLivingBase)))
         );
   }

   public EntityAIAttackOnCollide(EntityCreature creature, Class<? extends Entity> targetClass, double speedIn, boolean useLongMemory) {
      this(creature, speedIn, useLongMemory);
      this.classTarget = targetClass;
   }

   @Override
   public void startExecuting() {
      this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
      this.delayCounter = 0;
   }

   public EntityAIAttackOnCollide(EntityCreature creature, double speedIn, boolean useLongMemory) {
      this.attacker = creature;
      this.worldObj = creature.worldObj;
      this.speedTowardsTarget = speedIn;
      this.longMemory = useLongMemory;
      this.setMutexBits(3);
   }

   protected double getAttackReachSq(EntityLivingBase attackTarget) {
      return this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width;
   }

   @Override
   public void resetTask() {
      this.attacker.getNavigator().clearPathEntity();
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entityLivingBase = this.attacker.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      if (!entityLivingBase.isEntityAlive()) {
         return false;
      }

      if (this.classTarget != null && !this.classTarget.isAssignableFrom(entityLivingBase.getClass())) {
         return false;
      }

      this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entityLivingBase);
      return this.entityPathEntity != null;
   }

   @Override
   public void updateTask() {
      EntityLivingBase entityLivingBase = this.attacker.getAttackTarget();
      this.attacker.getLookHelper().setLookPositionWithEntity(entityLivingBase, 30.0F, 30.0F);
      double xCoordinate = this.attacker.getDistanceSq(entityLivingBase.posX, entityLivingBase.getEntityBoundingBox().minY, entityLivingBase.posZ);
      double attackReachSq = this.getAttackReachSq(entityLivingBase);
      this.delayCounter--;
      if ((this.longMemory || this.attacker.getEntitySenses().canSee(entityLivingBase))
         && this.delayCounter <= 0
         && (
            this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
               || entityLivingBase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0
               || this.attacker.getRNG().nextFloat() < 0.05F
         )) {
         this.targetX = entityLivingBase.posX;
         this.targetY = entityLivingBase.getEntityBoundingBox().minY;
         this.targetZ = entityLivingBase.posZ;
         this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
         if (xCoordinate > 1024.0) {
            this.delayCounter += 10;
         } else if (xCoordinate > 256.0) {
            this.delayCounter += 5;
         }

         if (!this.attacker.getNavigator().tryMoveToEntityLiving(entityLivingBase, this.speedTowardsTarget)) {
            this.delayCounter += 15;
         }
      }

      this.attackTick = Math.max(this.attackTick - 1, 0);
      if (xCoordinate <= attackReachSq && this.attackTick <= 0) {
         this.attackTick = 20;
         if (this.attacker.getHeldItem() != null) {
            this.attacker.swingItemNoPacket();
         }

         this.attacker.attackEntityAsMob(entityLivingBase);
      }
   }
}
