package net.minecraft.entity.boss;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IBossDisplayData, IRangedAttackMob {
   private float[] headPitch = new float[2];
   private float[] headYaw = new float[2];
   private int blockBreakCounter;
   private float[] prevHeadPitch = new float[2];
   private float[] prevHeadYaw = new float[2];
   private int[] attackCooldown = new int[2];
   private int[] attackVariant = new int[2];
   private static final Predicate<Entity> attackEntitySelector = new Predicate<Entity>() {
      public boolean apply(Entity arg1) {
         return arg1 instanceof EntityLivingBase && ((EntityLivingBase)arg1).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
      }
   };

   @Override
   public void setInWeb() {
   }

   @Override
   protected void updateAITasks() {
      if (this.getInvulTime() > 0) {
         int i = this.getInvulTime() - 1;
         if (i <= 0) {
            this.worldObj
               .newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
            this.worldObj.playBroadcastSound(1013, new BlockPos(this), 0);
         }

         this.setInvulTime(i);
         if (this.ticksExisted % 10 == 0) {
            this.heal(10.0F);
         }
      } else {
         super.updateAITasks();

         for (int index = 1; index < 3; index++) {
            if (this.ticksExisted >= this.attackCooldown[index - 1]) {
               this.attackCooldown[index - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
               if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  int i2 = index - 1;
                  int i3 = this.attackVariant[index - 1];
                  this.attackVariant[i2] = this.attackVariant[index - 1] + 1;
                  if (i3 > 15) {
                     float f = 10.0F;
                     float f2 = 5.0F;
                     double xCoordinate = MathHelper.getRandomDoubleInRange(this.rand, this.posX - f, this.posX + f);
                     double yCoordinate = MathHelper.getRandomDoubleInRange(this.rand, this.posY - f2, this.posY + f2);
                     double zCoordinate = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - f, this.posZ + f);
                     this.launchWitherSkullToCoords(index + 1, xCoordinate, yCoordinate, zCoordinate, true);
                     this.attackVariant[index - 1] = 0;
                  }
               }

               int watchedTargetId = this.getWatchedTargetId(index);
               if (watchedTargetId > 0) {
                  Entity entity = this.worldObj.getEntityByID(watchedTargetId);
                  if (entity == null || !entity.isEntityAlive() || !(this.getDistanceSqToEntity(entity) <= 900.0) || !this.canEntityBeSeen(entity)) {
                     this.updateWatchedTargetId(index, 0);
                  } else if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage) {
                     this.updateWatchedTargetId(index, 0);
                  } else {
                     this.launchWitherSkullToEntity(index + 1, (EntityLivingBase)entity);
                     this.attackCooldown[index - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                     this.attackVariant[index - 1] = 0;
                  }
               } else {
                  List<EntityLivingBase> list = this.worldObj
                     .getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        this.getEntityBoundingBox().expand(20.0, 8.0, 20.0),
                        Predicates.and(attackEntitySelector, EntitySelectors.NOT_SPECTATING)
                     );

                  for (int index2 = 0; index2 < 10 && !list.isEmpty(); index2++) {
                     EntityLivingBase entityLivingBase = list.get(this.rand.nextInt(list.size()));
                     if (entityLivingBase != this && entityLivingBase.isEntityAlive() && this.canEntityBeSeen(entityLivingBase)) {
                        if (entityLivingBase instanceof EntityPlayer) {
                           if (!((EntityPlayer)entityLivingBase).capabilities.disableDamage) {
                              this.updateWatchedTargetId(index, entityLivingBase.getEntityId());
                           }
                        } else {
                           this.updateWatchedTargetId(index, entityLivingBase.getEntityId());
                        }
                        break;
                     }

                     list.remove(entityLivingBase);
                  }
               }
            }
         }

         if (this.getAttackTarget() != null) {
            this.updateWatchedTargetId(0, this.getAttackTarget().getEntityId());
         } else {
            this.updateWatchedTargetId(0, 0);
         }

         if (this.blockBreakCounter > 0) {
            this.blockBreakCounter--;
            if (this.blockBreakCounter == 0 && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
               int i4 = MathHelper.floor_double(this.posY);
               int i5 = MathHelper.floor_double(this.posX);
               int i6 = MathHelper.floor_double(this.posZ);
               boolean conditionMet = false;

               for (int index3 = -1; index3 <= 1; index3++) {
                  for (int index4 = -1; index4 <= 1; index4++) {
                     for (int innerIndex = 0; innerIndex <= 3; innerIndex++) {
                        int i7 = i5 + index3;
                        int i8 = i4 + innerIndex;
                        int i9 = i6 + index4;
                        BlockPos blockPos = new BlockPos(i7, i8, i9);
                        Block block = this.worldObj.getBlockState(blockPos).getBlock();
                        if (block.getMaterial() != Material.air && canDestroyBlock(block)) {
                           conditionMet = this.worldObj.destroyBlock(blockPos, true) || conditionMet;
                        }
                     }
                  }
               }

               if (conditionMet) {
                  this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, new BlockPos(this), 0);
               }
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }
      }
   }

   @Override
   public void onLivingUpdate() {
      this.motionY *= 0.6F;
      if (!this.worldObj.isRemote && this.getWatchedTargetId(0) > 0) {
         Entity entity = this.worldObj.getEntityByID(this.getWatchedTargetId(0));
         if (entity != null) {
            if (this.posY < entity.posY || !this.isArmored() && this.posY < entity.posY + 5.0) {
               if (this.motionY < 0.0) {
                  this.motionY = 0.0;
               }

               this.motionY = this.motionY + (0.5 - this.motionY) * 0.6F;
            }

            double xCoordinate = entity.posX - this.posX;
            double zCoordinate = entity.posZ - this.posZ;
            double d = xCoordinate * xCoordinate + zCoordinate * zCoordinate;
            if (d > 9.0) {
               double d2 = MathHelper.sqrt_double(d);
               this.motionX = this.motionX + (xCoordinate / d2 * 0.5 - this.motionX) * 0.6F;
               this.motionZ = this.motionZ + (zCoordinate / d2 * 0.5 - this.motionZ) * 0.6F;
            }
         }
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05F) {
         this.rotationYaw = (float)MathHelper.atan2(this.motionZ, this.motionX) * (180.0F / (float)Math.PI) - 90.0F;
      }

      super.onLivingUpdate();

      for (int index = 0; index < 2; index++) {
         this.prevHeadYaw[index] = this.headYaw[index];
         this.prevHeadPitch[index] = this.headPitch[index];
      }

      for (int innerIndex = 0; innerIndex < 2; innerIndex++) {
         int watchedTargetId = this.getWatchedTargetId(innerIndex + 1);
         Entity entity2 = null;
         if (watchedTargetId > 0) {
            entity2 = this.worldObj.getEntityByID(watchedTargetId);
         }

         if (entity2 != null) {
            double headX = this.getHeadX(innerIndex + 1);
            double headY = this.getHeadY(innerIndex + 1);
            double headZ = this.getHeadZ(innerIndex + 1);
            double xCoordinate2 = entity2.posX - headX;
            double yCoordinate = entity2.posY + entity2.getEyeHeight() - headY;
            double zCoordinate2 = entity2.posZ - headZ;
            double d3 = MathHelper.sqrt_double(xCoordinate2 * xCoordinate2 + zCoordinate2 * zCoordinate2);
            float f = (float)(MathHelper.atan2(zCoordinate2, xCoordinate2) * 180.0 / Math.PI) - 90.0F;
            float f2 = (float)(-(MathHelper.atan2(yCoordinate, d3) * 180.0 / Math.PI));
            this.headPitch[innerIndex] = this.interpolateAngle(this.headPitch[innerIndex], f2, 40.0F);
            this.headYaw[innerIndex] = this.interpolateAngle(this.headYaw[innerIndex], f, 10.0F);
         } else {
            this.headYaw[innerIndex] = this.interpolateAngle(this.headYaw[innerIndex], this.renderYawOffset, 10.0F);
         }
      }

      boolean isArmored = this.isArmored();

      for (int outerIndex = 0; outerIndex < 3; outerIndex++) {
         double headX2 = this.getHeadX(outerIndex);
         double headY2 = this.getHeadY(outerIndex);
         double headZ2 = this.getHeadZ(outerIndex);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.SMOKE_NORMAL,
               headX2 + this.rand.nextGaussian() * 0.3F,
               headY2 + this.rand.nextGaussian() * 0.3F,
               headZ2 + this.rand.nextGaussian() * 0.3F,
               0.0,
               0.0,
               0.0
            );
         if (isArmored && this.worldObj.rand.nextInt(4) == 0) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.SPELL_MOB,
                  headX2 + this.rand.nextGaussian() * 0.3F,
                  headY2 + this.rand.nextGaussian() * 0.3F,
                  headZ2 + this.rand.nextGaussian() * 0.3F,
                  0.7F,
                  0.7F,
                  0.5
               );
         }
      }

      if (this.getInvulTime() > 0) {
         for (int index2 = 0; index2 < 3; index2++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.SPELL_MOB,
                  this.posX + this.rand.nextGaussian() * 1.0,
                  this.posY + this.rand.nextFloat() * 3.3F,
                  this.posZ + this.rand.nextGaussian() * 1.0,
                  0.7F,
                  0.7F,
                  0.9F
               );
         }
      }
   }

   private void launchWitherSkullToEntity(int i, EntityLivingBase entityLivingBase2) {
      double posX2 = entityLivingBase2.posX;
      double d = entityLivingBase2.posY + entityLivingBase2.getEyeHeight() * 0.5;
      boolean flag = i == 0 && this.rand.nextFloat() < 0.001F;
      double posZ2 = entityLivingBase2.posZ;
      this.launchWitherSkullToCoords(i, posX2, d, posZ2, flag);
   }

   private double getHeadX(int headIndex) {
      if (headIndex <= 0) {
         return this.posX;
      }

      float f = (this.renderYawOffset + 180 * (headIndex - 1)) / 180.0F * (float) Math.PI;
      float f2 = MathHelper.cos(f);
      return this.posX + f2 * 1.3;
   }

   @Override
   public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
      this.launchWitherSkullToEntity(0, target);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setInvulTime(tagCompund.getInteger("Invul"));
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source == DamageSource.drown || source.getEntity() instanceof EntityWither) {
         return false;
      }

      if (this.getInvulTime() > 0 && source != DamageSource.outOfWorld) {
         return false;
      }

      if (this.isArmored()) {
         Entity entity = source.getSourceOfDamage();
         if (entity instanceof EntityArrow) {
            return false;
         }
      }

      Entity entity2 = source.getEntity();
      if (entity2 != null
         && !(entity2 instanceof EntityPlayer)
         && entity2 instanceof EntityLivingBase
         && ((EntityLivingBase)entity2).getCreatureAttribute() == this.getCreatureAttribute()) {
         return false;
      }

      if (this.blockBreakCounter <= 0) {
         this.blockBreakCounter = 20;
      }

      for (int index = 0; index < this.attackVariant.length; index++) {
         this.attackVariant[index] = this.attackVariant[index] + 3;
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Invul", this.getInvulTime());
   }

   @Override
   public int getTotalArmorValue() {
      return 4;
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(0));
      this.dataWatcher.addObject(19, new Integer(0));
      this.dataWatcher.addObject(20, new Integer(0));
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
   }

   public void updateWatchedTargetId(int targetOffset, int newId) {
      this.dataWatcher.updateObject(17 + targetOffset, newId);
   }

   private double getHeadY(int headIndex) {
      return headIndex <= 0 ? this.posY + 3.0 : this.posY + 2.2;
   }

   @Override
   protected String getDeathSound() {
      return "mob.wither.death";
   }

   private double getHeadZ(int headIndex) {
      if (headIndex <= 0) {
         return this.posZ;
      }

      float f = (this.renderYawOffset + 180 * (headIndex - 1)) / 180.0F * (float) Math.PI;
      float f2 = MathHelper.sin(f);
      return this.posZ + f2 * 1.3;
   }

   public static boolean canDestroyBlock(Block block2) {
      return block2 != Blocks.bedrock
         && block2 != Blocks.end_portal
         && block2 != Blocks.end_portal_frame
         && block2 != Blocks.command_block
         && block2 != Blocks.barrier;
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      EntityItem entityItem = this.getEntityItem(Items.nether_star);
      if (entityItem != null) {
         entityItem.setNoDespawn();
      }

      if (!this.worldObj.isRemote) {
         Iterator iterator2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(50.0, 100.0, 50.0)).iterator();

         while (iterator2.hasNext()) {
            ((EntityPlayer)iterator2.next()).triggerAchievement(AchievementList.killWither);
         }
      }
   }

   @Override
   protected String getHurtSound() {
      return "mob.wither.hurt";
   }

   public void setInvulTime(int i) {
      this.dataWatcher.updateObject(20, i);
   }

   private void launchWitherSkullToCoords(int headIndex, double x, double y, double z, boolean invulnerable) {
      this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, new BlockPos(this), 0);
      double headX = this.getHeadX(headIndex);
      double headY = this.getHeadY(headIndex);
      double headZ = this.getHeadZ(headIndex);
      double d = x - headX;
      double d2 = y - headY;
      double d3 = z - headZ;
      EntityWitherSkull entityWitherSkull = new EntityWitherSkull(this.worldObj, this, d, d2, d3);
      if (invulnerable) {
         entityWitherSkull.update();
      }

      entityWitherSkull.posY = headY;
      entityWitherSkull.posX = headX;
      entityWitherSkull.posZ = headZ;
      this.worldObj.spawnEntityInWorld(entityWitherSkull);
   }

   @Override
   public void addPotionEffect(PotionEffect potioneffectIn) {
   }

   private float interpolateAngle(float startAngle, float endAngle, float maxDelta) {
      float f = MathHelper.wrapAngleTo180_float(endAngle - startAngle);
      if (f > maxDelta) {
         f = maxDelta;
      }

      if (f < -maxDelta) {
         f = -maxDelta;
      }

      return startAngle + f;
   }

   public int getWatchedTargetId(int i) {
      return this.dataWatcher.getWatchableObjectInt(17 + i);
   }

   public int getInvulTime() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   @Override
   public void mountEntity(Entity entityIn) {
      this.ridingEntity = null;
   }

   @Override
   protected String getLivingSound() {
      return "mob.wither.idle";
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6F);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
   }

   public float getHeadYaw(int headIndex) {
      return this.headYaw[headIndex];
   }

   public void setWitherInvulnerability() {
      this.setInvulTime(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   public boolean isArmored() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   public float getHeadPitch(int headIndex) {
      return this.headPitch[headIndex];
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   protected void despawnEntity() {
      this.entityAge = 0;
   }

   public EntityWither(World worldIn) {
      super(worldIn);
      this.setHealth(this.getMaxHealth());
      this.setSize(0.9F, 3.5F);
      this.isImmuneToFire = true;
      ((PathNavigateGround)this.getNavigator()).update();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0, 40, 20.0F));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, attackEntitySelector));
      this.experienceValue = 50;
   }
}
