package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearest extends EntityAIBase {
   private EntityLiving mob;
   private static final Logger LOGGER = LogManager.getLogger();
   private Class<? extends EntityLivingBase> targetClass;
   private EntityLivingBase target;
   private final Predicate<EntityLivingBase> targetPredicate;
   private final EntityAINearestAttackableTarget.Sorter targetSorter;

   protected double getFollowRange() {
      IAttributeInstance attributeInstance = this.mob.getEntityAttribute(SharedMonsterAttributes.followRange);
      return attributeInstance == null ? 16.0 : attributeInstance.getAttributeValue();
   }

   public EntityAIFindEntityNearest(EntityLiving mobIn, Class<? extends EntityLivingBase> targetClassIn) {
      this.mob = mobIn;
      this.targetClass = targetClassIn;
      if (mobIn instanceof EntityCreature) {
         LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.targetPredicate = new Predicate<EntityLivingBase>() {
         public boolean apply(EntityLivingBase candidate) {
            double followRange2 = EntityAIFindEntityNearest.this.getFollowRange();
            if (candidate.isSneaking()) {
               followRange2 *= 0.8F;
            }

            return candidate.isInvisible()
               ? false
               : (
                  candidate.getDistanceToEntity(EntityAIFindEntityNearest.this.mob) > followRange2
                     ? false
                     : EntityAITarget.checkEntityLiving(EntityAIFindEntityNearest.this.mob, candidate, true)
               );
         }
      };
      this.targetSorter = new EntityAINearestAttackableTarget.Sorter(mobIn);
   }

   @Override
   public boolean shouldExecute() {
      double followRange2 = this.getFollowRange();
      List<EntityLivingBase> list = this.mob
         .worldObj
         .getEntitiesWithinAABB(this.targetClass, this.mob.getEntityBoundingBox().expand(followRange2, 4.0, followRange2), this.targetPredicate);
      Collections.sort(list, this.targetSorter);
      if (list.isEmpty()) {
         return false;
      }

      this.target = list.get(0);
      return true;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase entityLivingBase = this.mob.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      if (!entityLivingBase.isEntityAlive()) {
         return false;
      }

      double followRange2 = this.getFollowRange();
      return this.mob.getDistanceSqToEntity(entityLivingBase) > followRange2 * followRange2
         ? false
         : !(entityLivingBase instanceof EntityPlayerMP) || !((EntityPlayerMP)entityLivingBase).theItemInWorldManager.isCreative();
   }

   @Override
   public void startExecuting() {
      this.mob.setAttackTarget(this.target);
      super.startExecuting();
   }

   @Override
   public void resetTask() {
      this.mob.setAttackTarget((EntityLivingBase)null);
      super.startExecuting();
   }
}
