package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob {
   private int explosionStrength = 1;

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(100.0);
   }

   @Override
   protected String getHurtSound() {
      return "mob.ghast.scream";
   }

   public boolean isAttacking() {
      return this.dataWatcher.getWatchableObjectByte(16) != 0;
   }

   @Override
   protected String getLivingSound() {
      return "mob.ghast.moan";
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("ExplosionPower", this.explosionStrength);
   }

   public void setAttacking(boolean attacking) {
      this.dataWatcher.updateObject(16, (byte)(attacking ? 1 : 0));
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   public EntityGhast(World worldIn) {
      super(worldIn);
      this.setSize(4.0F, 4.0F);
      this.isImmuneToFire = true;
      this.experienceValue = 5;
      this.moveHelper = new EntityGhast.GhastMoveHelper(this);
      this.tasks.addTask(5, new EntityGhast.AIRandomFly(this));
      this.tasks.addTask(7, new EntityGhast.AILookAround(this));
      this.tasks.addTask(7, new EntityGhast.AIFireballAttack(this));
      this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
   }

   @Override
   public float getEyeHeight() {
      return 2.6F;
   }

   @Override
   protected String getDeathSound() {
      return "mob.ghast.death";
   }

   public int getFireballStrength() {
      return this.explosionStrength;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer) {
         super.attackEntityFrom(source, 1000.0F);
         ((EntityPlayer)source.getEntity()).triggerAchievement(AchievementList.ghast);
         return true;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   @Override
   public boolean getCanSpawnHere() {
      return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(2) + this.rand.nextInt(1 + lootingModifier);

      for (int i2 = 0; i2 < i; i2++) {
         this.getEntityItem(Items.ghast_tear);
      }

      i = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingModifier);

      for (int i3 = 0; i3 < i; i3++) {
         this.getEntityItem(Items.gunpowder);
      }
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 1;
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("ExplosionPower", 99)) {
         this.explosionStrength = tagCompund.getInteger("ExplosionPower");
      }
   }

   @Override
   protected float getSoundVolume() {
      return 10.0F;
   }

   @Override
   protected Item getDropItem() {
      return Items.gunpowder;
   }

   static class AIFireballAttack extends EntityAIBase {
      public int attackTimer;
      private EntityGhast parentEntity;

      @Override
      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null;
      }

      @Override
      public void updateTask() {
         EntityLivingBase entityLivingBase = this.parentEntity.getAttackTarget();
         double d = 64.0;
         if (entityLivingBase.getDistanceSqToEntity(this.parentEntity) < d * d && this.parentEntity.canEntityBeSeen(entityLivingBase)) {
            World world = this.parentEntity.worldObj;
            this.attackTimer++;
            if (this.attackTimer == 10) {
               world.playAuxSFXAtEntity((EntityPlayer)null, 1007, new BlockPos(this.parentEntity), 0);
            }

            if (this.attackTimer == 20) {
               double d2 = 4.0;
               Vec3 vec3 = this.parentEntity.getLook(1.0F);
               double xCoordinate = entityLivingBase.posX - (this.parentEntity.posX + vec3.xCoord * d2);
               double yCoordinate = entityLivingBase.getEntityBoundingBox().minY
                  + entityLivingBase.height / 2.0F
                  - (0.5 + this.parentEntity.posY + this.parentEntity.height / 2.0F);
               double zCoordinate = entityLivingBase.posZ - (this.parentEntity.posZ + vec3.zCoord * d2);
               world.playAuxSFXAtEntity((EntityPlayer)null, 1008, new BlockPos(this.parentEntity), 0);
               EntityLargeFireball entityLargeFireball = new EntityLargeFireball(world, this.parentEntity, xCoordinate, yCoordinate, zCoordinate);
               entityLargeFireball.explosionPower = this.parentEntity.getFireballStrength();
               entityLargeFireball.posX = this.parentEntity.posX + vec3.xCoord * d2;
               entityLargeFireball.posY = this.parentEntity.posY + this.parentEntity.height / 2.0F + 0.5;
               entityLargeFireball.posZ = this.parentEntity.posZ + vec3.zCoord * d2;
               world.spawnEntityInWorld(entityLargeFireball);
               this.attackTimer = -40;
            }
         } else if (this.attackTimer > 0) {
            this.attackTimer--;
         }

         this.parentEntity.setAttacking(this.attackTimer > 10);
      }

      public AIFireballAttack(EntityGhast ghast) {
         this.parentEntity = ghast;
      }

      @Override
      public void resetTask() {
         this.parentEntity.setAttacking(false);
      }

      @Override
      public void startExecuting() {
         this.attackTimer = 0;
      }
   }

   static class AILookAround extends EntityAIBase {
      private EntityGhast parentEntity;

      @Override
      public boolean shouldExecute() {
         return true;
      }

      public AILookAround(EntityGhast ghast) {
         this.parentEntity = ghast;
         this.setMutexBits(2);
      }

      @Override
      public void updateTask() {
         if (this.parentEntity.getAttackTarget() == null) {
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ))
               * 180.0F
               / (float) Math.PI;
         } else {
            EntityLivingBase entityLivingBase = this.parentEntity.getAttackTarget();
            double d = 64.0;
            if (entityLivingBase.getDistanceSqToEntity(this.parentEntity) < d * d) {
               double xCoordinate = entityLivingBase.posX - this.parentEntity.posX;
               double zCoordinate = entityLivingBase.posZ - this.parentEntity.posZ;
               this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(xCoordinate, zCoordinate))
                  * 180.0F
                  / (float) Math.PI;
            }
         }
      }
   }

   static class AIRandomFly extends EntityAIBase {
      private EntityGhast parentEntity;

      @Override
      public void startExecuting() {
         Random random = this.parentEntity.getRNG();
         double xCoordinate = this.parentEntity.posX + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double yCoordinate = this.parentEntity.posY + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double zCoordinate = this.parentEntity.posZ + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         this.parentEntity.getMoveHelper().setMoveTo(xCoordinate, yCoordinate, zCoordinate, 1.0);
      }

      public AIRandomFly(EntityGhast ghast) {
         this.parentEntity = ghast;
         this.setMutexBits(1);
      }

      @Override
      public boolean continueExecuting() {
         return false;
      }

      @Override
      public boolean shouldExecute() {
         EntityMoveHelper entityMoveHelper = this.parentEntity.getMoveHelper();
         if (!entityMoveHelper.isUpdating()) {
            return true;
         }

         double xCoordinate = entityMoveHelper.getX() - this.parentEntity.posX;
         double yCoordinate = entityMoveHelper.getY() - this.parentEntity.posY;
         double zCoordinate = entityMoveHelper.getZ() - this.parentEntity.posZ;
         double d = xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate;
         return d < 1.0 || d > 3600.0;
      }
   }

   static class GhastMoveHelper extends EntityMoveHelper {
      private EntityGhast parentEntity;
      private int courseChangeCooldown;

      @Override
      public void onUpdateMoveHelper() {
         if (this.update) {
            double zCoordinate = this.posX - this.parentEntity.posX;
            double d = this.posY - this.parentEntity.posY;
            double d2 = this.posZ - this.parentEntity.posZ;
            double d3 = zCoordinate * zCoordinate + d * d + d2 * d2;
            if (this.courseChangeCooldown-- <= 0) {
               this.courseChangeCooldown = this.courseChangeCooldown + this.parentEntity.getRNG().nextInt(5) + 2;
               d3 = MathHelper.sqrt_double(d3);
               if (this.isNotColliding(this.posX, this.posY, this.posZ, d3)) {
                  this.parentEntity.motionX += zCoordinate / d3 * 0.1;
                  this.parentEntity.motionY += d / d3 * 0.1;
                  this.parentEntity.motionZ += d2 / d3 * 0.1;
               } else {
                  this.update = false;
               }
            }
         }
      }

      public GhastMoveHelper(EntityGhast ghast) {
         super(ghast);
         this.parentEntity = ghast;
      }

      private boolean isNotColliding(double x, double y, double z, double d) {
         double xCoordinate = (x - this.parentEntity.posX) / d;
         double yCoordinate = (y - this.parentEntity.posY) / d;
         double zCoordinate = (z - this.parentEntity.posZ) / d;
         AxisAlignedBB axisAlignedBB = this.parentEntity.getEntityBoundingBox();

         for (int index = 1; index < d; index++) {
            axisAlignedBB = axisAlignedBB.offset(xCoordinate, yCoordinate, zCoordinate);
            if (!this.parentEntity.worldObj.getCollidingBoundingBoxes(this.parentEntity, axisAlignedBB).isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }
}
