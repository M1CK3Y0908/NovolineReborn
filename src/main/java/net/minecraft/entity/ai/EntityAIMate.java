package net.minecraft.entity.ai;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityAIMate extends EntityAIBase {
   private EntityAnimal targetMate;
   private EntityAnimal theAnimal;
   World theWorld;
   int spawnBabyDelay;
   double moveSpeed;

   private void spawnBaby() {
      EntityAgeable entityAgeable = this.theAnimal.createChild(this.targetMate);
      if (entityAgeable != null) {
         EntityPlayer entityPlayer = this.theAnimal.getPlayerInLove();
         if (entityPlayer == null && this.targetMate.getPlayerInLove() != null) {
            entityPlayer = this.targetMate.getPlayerInLove();
         }

         if (entityPlayer != null) {
            entityPlayer.triggerAchievement(StatList.animalsBredStat);
            if (this.theAnimal instanceof EntityCow) {
               entityPlayer.triggerAchievement(AchievementList.breedCow);
            }
         }

         this.theAnimal.setGrowingAge(6000);
         this.targetMate.setGrowingAge(6000);
         this.theAnimal.resetInLove();
         this.targetMate.resetInLove();
         entityAgeable.setGrowingAge(-24000);
         entityAgeable.setLocationAndAngles(this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, 0.0F, 0.0F);
         this.theWorld.spawnEntityInWorld(entityAgeable);
         Random random = this.theAnimal.getRNG();

         for (int index = 0; index < 7; index++) {
            double d = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            double d3 = random.nextGaussian() * 0.02;
            double d4 = random.nextDouble() * this.theAnimal.width * 2.0 - this.theAnimal.width;
            double d5 = 0.5 + random.nextDouble() * this.theAnimal.height;
            double d6 = random.nextDouble() * this.theAnimal.width * 2.0 - this.theAnimal.width;
            this.theWorld
               .spawnParticle(
                  EnumParticleTypes.HEART,
                  this.theAnimal.posX + d4,
                  this.theAnimal.posY + d5,
                  this.theAnimal.posZ + d6,
                  d,
                  d2,
                  d3
               );
         }

         if (this.theWorld.getGameRules().getBoolean("doMobLoot")) {
            this.theWorld
               .spawnEntityInWorld(new EntityXPOrb(this.theWorld, this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, random.nextInt(7) + 1));
         }
      }
   }

   @Override
   public void resetTask() {
      this.targetMate = null;
      this.spawnBabyDelay = 0;
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theAnimal.isInLove()) {
         return false;
      }

      this.targetMate = this.getNearbyMate();
      return this.targetMate != null;
   }

   public EntityAIMate(EntityAnimal animal, double speedIn) {
      this.theAnimal = animal;
      this.theWorld = animal.worldObj;
      this.moveSpeed = speedIn;
      this.setMutexBits(3);
   }

   @Override
   public boolean continueExecuting() {
      return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
   }

   private EntityAnimal getNearbyMate() {
      float f = 8.0F;
      List entitiesWithinAABB = this.theWorld.getEntitiesWithinAABB(this.theAnimal.getClass(), this.theAnimal.getEntityBoundingBox().expand(f, f, f));
      double d = Double.MAX_VALUE;
      EntityAnimal entityAnimal = null;

      for (EntityAnimal entityAnimal2 : (Iterable<EntityAnimal>) entitiesWithinAABB) {
         if (this.theAnimal.canMateWith(entityAnimal2) && this.theAnimal.getDistanceSqToEntity(entityAnimal2) < d) {
            entityAnimal = entityAnimal2;
            d = this.theAnimal.getDistanceSqToEntity(entityAnimal2);
         }
      }

      return entityAnimal;
   }

   @Override
   public void updateTask() {
      this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, this.theAnimal.getVerticalFaceSpeed());
      this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
      this.spawnBabyDelay++;
      if (this.spawnBabyDelay >= 60 && this.theAnimal.getDistanceSqToEntity(this.targetMate) < 9.0) {
         this.spawnBaby();
      }
   }
}
