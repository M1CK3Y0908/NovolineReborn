package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase {
   private int targetSearchStatus;
   protected final EntityCreature taskOwner;
   private int targetUnseenTicks;
   protected boolean shouldCheckSight;
   private boolean nearbyOnly;
   private int targetSearchDelay;

   @Override
   public void resetTask() {
      this.taskOwner.setAttackTarget((EntityLivingBase)null);
   }

   protected boolean checkEntityLivingBase(EntityLivingBase entityLivingBase2) {
      if (!checkEntityLiving(this.taskOwner, entityLivingBase2, this.shouldCheckSight)) {
         return false;
      }

      if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(entityLivingBase2))) {
         return false;
      }

      if (this.nearbyOnly) {
         if (--this.targetSearchDelay <= 0) {
            this.targetSearchStatus = 0;
         }

         if (this.targetSearchStatus == 0) {
            this.targetSearchStatus = this.canEasilyReach(entityLivingBase2) ? 1 : 2;
         }

         if (this.targetSearchStatus == 2) {
            return false;
         }
      }

      return true;
   }

   private boolean canEasilyReach(EntityLivingBase target) {
      this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
      PathEntity pathEntity = this.taskOwner.getNavigator().getPathToEntityLiving(target);
      if (pathEntity == null) {
         return false;
      }

      PathPoint pathPoint = pathEntity.getFinalPathPoint();
      if (pathPoint == null) {
         return false;
      }

      int i = pathPoint.xCoord - MathHelper.floor_double(target.posX);
      int i2 = pathPoint.zCoord - MathHelper.floor_double(target.posZ);
      return i * i + i2 * i2 <= 2.25;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase entityLivingBase = this.taskOwner.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      if (!entityLivingBase.isEntityAlive()) {
         return false;
      }

      Team team = this.taskOwner.getTeam();
      Team team2 = entityLivingBase.getTeam();
      if (team != null && team2 == team) {
         return false;
      }

      double targetDistance = this.getTargetDistance();
      if (this.taskOwner.getDistanceSqToEntity(entityLivingBase) > targetDistance * targetDistance) {
         return false;
      }

      if (this.shouldCheckSight) {
         if (this.taskOwner.getEntitySenses().canSee(entityLivingBase)) {
            this.targetUnseenTicks = 0;
         } else if (++this.targetUnseenTicks > 60) {
            return false;
         }
      }

      return !(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).capabilities.disableDamage;
   }

   public static boolean checkEntityLiving(EntityLiving entityLiving, EntityLivingBase entityLivingBase2, boolean flag) {
      if (entityLivingBase2 == null) {
         return false;
      }

      if (entityLivingBase2 == entityLiving) {
         return false;
      }

      if (!entityLivingBase2.isEntityAlive()) {
         return false;
      }

      if (!entityLiving.canAttackClass((Class<? extends EntityLivingBase>)entityLivingBase2.getClass())) {
         return false;
      }

      Team team3 = entityLiving.getTeam();
      Team team4 = entityLivingBase2.getTeam();
      if (team3 != null && team4 == team3) {
         return false;
      }

      if (entityLiving instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)entityLiving).getOwnerId())) {
         if (entityLivingBase2 instanceof IEntityOwnable && ((IEntityOwnable)entityLiving).getOwnerId().equals(((IEntityOwnable)entityLivingBase2).getOwnerId())) {
            return false;
         }

         if (entityLivingBase2 == ((IEntityOwnable)entityLiving).getOwner()) {
            return false;
         }
      } else if (entityLivingBase2 instanceof EntityPlayer && ((EntityPlayer)entityLivingBase2).capabilities.disableDamage) {
         return false;
      }

      return !flag || entityLiving.getEntitySenses().canSee(entityLivingBase2);
   }

   protected double getTargetDistance() {
      IAttributeInstance attributeInstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
      return attributeInstance == null ? 16.0 : attributeInstance.getAttributeValue();
   }

   public EntityAITarget(EntityCreature creature, boolean checkSight, boolean onlyNearby) {
      this.taskOwner = creature;
      this.shouldCheckSight = checkSight;
      this.nearbyOnly = onlyNearby;
   }

   public EntityAITarget(EntityCreature creature, boolean checkSight) {
      this(creature, checkSight, false);
   }

   @Override
   public void startExecuting() {
      this.targetSearchStatus = 0;
      this.targetSearchDelay = 0;
      this.targetUnseenTicks = 0;
   }
}
