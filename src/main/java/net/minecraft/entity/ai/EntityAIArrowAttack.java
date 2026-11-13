package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.MathHelper;

public class EntityAIArrowAttack extends EntityAIBase {
   private int rangedAttackTime = -1;
   private final IRangedAttackMob rangedAttackEntityHost;
   private final EntityLiving entityHost;
   private int seeTime;
   private double entityMoveSpeed;
   private int minAttackTime;
   private int maxRangedAttackTime;
   private float attackRadius;
   private EntityLivingBase attackTarget;
   private float maxAttackDistance;

   public EntityAIArrowAttack(IRangedAttackMob attacker, double movespeed, int i, float f) {
      this(attacker, movespeed, i, i, f);
   }

   @Override
   public boolean continueExecuting() {
      return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
   }

   @Override
   public void updateTask() {
      double xCoordinate = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
      boolean conditionMet = this.entityHost.getEntitySenses().canSee(this.attackTarget);
      if (conditionMet) {
         this.seeTime++;
      } else {
         this.seeTime = 0;
      }

      if (xCoordinate <= this.maxAttackDistance && this.seeTime >= 20) {
         this.entityHost.getNavigator().clearPathEntity();
      } else {
         this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
      }

      this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
      if (--this.rangedAttackTime == 0) {
         if (xCoordinate > this.maxAttackDistance || !conditionMet) {
            return;
         }

         float f = MathHelper.sqrt_double(xCoordinate) / this.attackRadius;
         float f2 = MathHelper.clamp_float(f, 0.1F, 1.0F);
         this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, f2);
         this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.minAttackTime) + this.minAttackTime);
      } else if (this.rangedAttackTime < 0) {
         float f3 = MathHelper.sqrt_double(xCoordinate) / this.attackRadius;
         this.rangedAttackTime = MathHelper.floor_float(f3 * (this.maxRangedAttackTime - this.minAttackTime) + this.minAttackTime);
      }
   }

   @Override
   public void resetTask() {
      this.attackTarget = null;
      this.seeTime = 0;
      this.rangedAttackTime = -1;
   }

   public EntityAIArrowAttack(IRangedAttackMob attacker, double movespeed, int i, int maxAttackTime, float maxAttackDistanceIn) {
      if (!(attacker instanceof EntityLivingBase)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      }

      this.rangedAttackEntityHost = attacker;
      this.entityHost = (EntityLiving)attacker;
      this.entityMoveSpeed = movespeed;
      this.minAttackTime = i;
      this.maxRangedAttackTime = maxAttackTime;
      this.attackRadius = maxAttackDistanceIn;
      this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entityLivingBase = this.entityHost.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      this.attackTarget = entityLivingBase;
      return true;
   }
}
