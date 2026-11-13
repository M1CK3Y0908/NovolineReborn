package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget {
   protected EntityLivingBase targetEntity;
   protected final Class<T> targetClass;
   private final int targetChance;
   protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
   protected Predicate<? super T> targetEntitySelector;

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.targetEntity);
      super.startExecuting();
   }

   public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
      this(creature, classTarget, 10, checkSight, false, (Predicate<? super T>)null);
   }

   public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
      this(creature, classTarget, checkSight, false);
   }

   public EntityAINearestAttackableTarget(
      EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector
   ) {
      super(creature, checkSight, onlyNearby);
      this.targetClass = classTarget;
      this.targetChance = chance;
      this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(creature);
      this.setMutexBits(1);
      this.targetEntitySelector = new Predicate<T>() {
         public boolean apply(T arg1) {
            if (targetSelector != null && !targetSelector.apply(arg1)) {
               return false;
            }

            if (arg1 instanceof EntityPlayer) {
               double targetDistance = EntityAINearestAttackableTarget.this.getTargetDistance();
               if (arg1.isSneaking()) {
                  targetDistance *= 0.8F;
               }

               if (arg1.isInvisible()) {
                  float armorVisibility = ((EntityPlayer)arg1).getArmorVisibility();
                  if (armorVisibility < 0.1F) {
                     armorVisibility = 0.1F;
                  }

                  targetDistance *= 0.7F * armorVisibility;
               }

               if (arg1.getDistanceToEntity(EntityAINearestAttackableTarget.this.taskOwner) > targetDistance) {
                  return false;
               }
            }

            return EntityAINearestAttackableTarget.this.checkEntityLivingBase(arg1);
         }
      };
   }

   @Override
   public boolean shouldExecute() {
      if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      }

      double targetDistance = this.getTargetDistance();
      List<T> list = this.taskOwner
         .worldObj
         .getEntitiesWithinAABB(
            this.targetClass,
            this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0, targetDistance),
            Predicates.and(this.targetEntitySelector, EntitySelectors.NOT_SPECTATING)
         );
      Collections.sort(list, this.theNearestAttackableTargetSorter);
      if (list.isEmpty()) {
         return false;
      }

      this.targetEntity = list.get(0);
      return true;
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity theEntity;

      public int compare(Entity arg1, Entity arg2) {
         double distanceSqToEntity = this.theEntity.getDistanceSqToEntity(arg1);
         double distanceSqToEntity2 = this.theEntity.getDistanceSqToEntity(arg2);
         return distanceSqToEntity < distanceSqToEntity2 ? -1 : (distanceSqToEntity > distanceSqToEntity2 ? 1 : 0);
      }

      public Sorter(Entity theEntityIn) {
         this.theEntity = theEntityIn;
      }
   }
}
