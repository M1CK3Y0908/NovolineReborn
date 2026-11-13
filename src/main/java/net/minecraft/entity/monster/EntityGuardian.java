package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGuardian extends EntityMob {
   private EntityAIWander wander;
   private float clientSideTailAnimation;
   private float clientSideTailAnimationSpeed;
   private boolean clientSideTouchedGround;
   private float clientSideTailAnimationO;
   private float clientSideSpikesAnimation;
   private EntityLivingBase targetedEntity;
   private int clientSideAttackTime;
   private float clientSideSpikesAnimationO;

   public EntityLivingBase getTargetedEntity() {
      if (!this.hasTargetedEntity()) {
         return null;
      }

      if (this.worldObj.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity entity = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(17));
            if (entity instanceof EntityLivingBase) {
               this.targetedEntity = (EntityLivingBase)entity;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   public void setElder(boolean elder) {
      this.setSyncedFlag(4, elder);
      if (elder) {
         this.setSize(1.9975F, 1.9975F);
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
         this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0);
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(80.0);
         this.enablePersistence();
         this.wander.update();
      }
   }

   @Override
   protected PathNavigate getNewNavigator(World worldIn) {
      return new PathNavigateSwimmer(this, worldIn);
   }

   @Override
   protected void updateAITasks() {
      super.updateAITasks();
      if (this.isElder()) {
         if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
            Potion digSlowdown2 = Potion.digSlowdown;

            for (EntityPlayerMP entityPlayerMP : this.worldObj.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {
               public boolean apply(EntityPlayerMP arg1) {
                  return EntityGuardian.this.getDistanceSqToEntity(arg1) < 2500.0 && arg1.theItemInWorldManager.survivalOrAdventure();
               }
            })) {
               if (!entityPlayerMP.isPotionActive(digSlowdown2)
                  || entityPlayerMP.getActivePotionEffect(digSlowdown2).getAmplifier() < 2
                  || entityPlayerMP.getActivePotionEffect(digSlowdown2).getDuration() < 1200) {
                  entityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(10, 0.0F));
                  entityPlayerMP.addPotionEffect(new PotionEffect(digSlowdown2.id, 6000, 2));
               }
            }
         }

         if (!this.hasHome()) {
            this.setHomePosAndDistance(new BlockPos(this), 16);
         }
      }
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return this.worldObj.getBlockState(pos).getBlock().getMaterial() == Material.water
         ? 10.0F + this.worldObj.getLightBrightness(pos) - 0.5F
         : super.getBlockPathWeight(pos);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setElder(tagCompund.getBoolean("Elder"));
   }

   public boolean isMoving() {
      return this.isSyncedFlagSet(2);
   }

   public float getTailAnimation(float f) {
      return this.clientSideTailAnimationO + (this.clientSideTailAnimation - this.clientSideTailAnimationO) * f;
   }

   @Override
   protected boolean isValidLightLevel() {
      return true;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
   }

   public float getAttackAnimationScale(float f) {
      return (this.clientSideAttackTime + f) / this.getAttackDuration();
   }

   @Override
   protected void addRandomDrop() {
      ItemStack itemStack = WeightedRandom.getRandomItem(this.rand, EntityFishHook.getFishLoot()).getItemStack(this.rand);
      this.entityDropItem(itemStack, 1.0F);
   }

   public boolean hasTargetedEntity() {
      return this.dataWatcher.getWatchableObjectInt(17) != 0;
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(3) + this.rand.nextInt(lootingModifier + 1);
      if (i > 0) {
         this.entityDropItem(new ItemStack(Items.prismarine_shard, i, 0), 1.0F);
      }

      if (this.rand.nextInt(3 + lootingModifier) > 1) {
         this.entityDropItem(new ItemStack(Items.fish, 1, net.minecraft.item.ItemFishFood.FishType.COD.getMetadata()), 1.0F);
      } else if (this.rand.nextInt(3 + lootingModifier) > 1) {
         this.entityDropItem(new ItemStack(Items.prismarine_crystals, 1, 0), 1.0F);
      }

      if (wasRecentlyHit && this.isElder()) {
         this.entityDropItem(new ItemStack(Blocks.sponge, 1, 1), 1.0F);
      }
   }

   @Override
   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty();
   }

   private void setTargetedEntity(int entityId) {
      this.dataWatcher.updateObject(17, entityId);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("Elder", this.isElder());
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         this.clientSideTailAnimationO = this.clientSideTailAnimation;
         if (!this.isInWater()) {
            this.clientSideTailAnimationSpeed = 2.0F;
            if (this.motionY > 0.0 && this.clientSideTouchedGround && !this.isSilent()) {
               this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.guardian.flop", 1.0F, 1.0F, false);
            }

            this.clientSideTouchedGround = this.motionY < 0.0 && this.worldObj.isBlockNormalCube(new BlockPos(this).down(), false);
         } else if (this.isMoving()) {
            if (this.clientSideTailAnimationSpeed < 0.5F) {
               this.clientSideTailAnimationSpeed = 4.0F;
            } else {
               this.clientSideTailAnimationSpeed = this.clientSideTailAnimationSpeed + (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
            }
         } else {
            this.clientSideTailAnimationSpeed = this.clientSideTailAnimationSpeed + (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
         }

         this.clientSideTailAnimation = this.clientSideTailAnimation + this.clientSideTailAnimationSpeed;
         this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
         if (!this.isInWater()) {
            this.clientSideSpikesAnimation = this.rand.nextFloat();
         } else if (this.isMoving()) {
            this.clientSideSpikesAnimation = this.clientSideSpikesAnimation + (0.0F - this.clientSideSpikesAnimation) * 0.25F;
         } else {
            this.clientSideSpikesAnimation = this.clientSideSpikesAnimation + (1.0F - this.clientSideSpikesAnimation) * 0.06F;
         }

         if (this.isMoving() && this.isInWater()) {
            Vec3 look = this.getLook(0.0F);

            for (int i = 0; i < 2; i++) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.WATER_BUBBLE,
                     this.posX + (this.rand.nextDouble() - 0.5) * this.width - look.xCoord * 1.5,
                     this.posY + this.rand.nextDouble() * this.height - look.yCoord * 1.5,
                     this.posZ + (this.rand.nextDouble() - 0.5) * this.width - look.zCoord * 1.5,
                     0.0,
                     0.0,
                     0.0
                  );
            }
         }

         if (this.hasTargetedEntity()) {
            if (this.clientSideAttackTime < this.getAttackDuration()) {
               this.clientSideAttackTime++;
            }

            EntityLivingBase targetedEntity2 = this.getTargetedEntity();
            if (targetedEntity2 != null) {
               this.getLookHelper().setLookPositionWithEntity(targetedEntity2, 90.0F, 90.0F);
               this.getLookHelper().onUpdateLook();
               double attackAnimationScale = this.getAttackAnimationScale(0.0F);
               double d = targetedEntity2.posX - this.posX;
               double d2 = targetedEntity2.posY + targetedEntity2.height * 0.5F - (this.posY + this.getEyeHeight());
               double d3 = targetedEntity2.posZ - this.posZ;
               double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
               d /= d4;
               d2 /= d4;
               d3 /= d4;
               double d5 = this.rand.nextDouble();

               while (d5 < d4) {
                  d5 += 1.8 - attackAnimationScale + this.rand.nextDouble() * (1.7 - attackAnimationScale);
                  this.worldObj
                     .spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE,
                        this.posX + d * d5,
                        this.posY + d2 * d5 + this.getEyeHeight(),
                        this.posZ + d3 * d5,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }
      }

      if (this.inWater) {
         this.setAir(300);
      } else if (this.onGround) {
         this.motionY += 0.5;
         this.motionX = this.motionX + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.motionZ = this.motionZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F;
         this.rotationYaw = this.rand.nextFloat() * 360.0F;
         this.onGround = false;
         this.isAirBorne = true;
      }

      if (this.hasTargetedEntity()) {
         this.rotationYaw = this.rotationYawHead;
      }

      super.onLivingUpdate();
   }

   private void setMoving(boolean flag) {
      this.setSyncedFlag(2, flag);
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.isServerWorld()) {
         if (this.isInWater()) {
            this.moveFlying(strafe, forward, 0.1F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9F;
            this.motionY *= 0.9F;
            this.motionZ *= 0.9F;
            if (!this.isMoving() && this.getAttackTarget() == null) {
               this.motionY -= 0.005;
            }
         } else {
            super.moveEntityWithHeading(strafe, forward);
         }
      } else {
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
      this.dataWatcher.addObject(17, 0);
   }

   @Override
   public int getTalkInterval() {
      return 160;
   }

   private boolean isSyncedFlagSet(int flagId) {
      return (this.dataWatcher.getWatchableObjectInt(16) & flagId) != 0;
   }

   @Override
   protected String getLivingSound() {
      return !this.isInWater() ? "mob.guardian.land.idle" : (this.isElder() ? "mob.guardian.elder.idle" : "mob.guardian.idle");
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   @Override
   public void onDataWatcherUpdate(int dataID) {
      super.onDataWatcherUpdate(dataID);
      if (dataID == 16) {
         if (this.isElder() && this.width < 1.0F) {
            this.setSize(1.9975F, 1.9975F);
         }
      } else if (dataID == 17) {
         this.clientSideAttackTime = 0;
         this.targetedEntity = null;
      }
   }

   @Override
   public boolean getCanSpawnHere() {
      return (this.rand.nextInt(20) == 0 || !this.worldObj.canBlockSeeSky(new BlockPos(this))) && super.getCanSpawnHere();
   }

   @Override
   public int getVerticalFaceSpeed() {
      return 180;
   }

   public boolean isElder() {
      return this.isSyncedFlagSet(4);
   }

   @Override
   protected String getHurtSound() {
      return !this.isInWater() ? "mob.guardian.land.hit" : (this.isElder() ? "mob.guardian.elder.hit" : "mob.guardian.hit");
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.isMoving() && !source.isMagicDamage() && source.getSourceOfDamage() instanceof EntityLivingBase) {
         EntityLivingBase entityLivingBase = (EntityLivingBase)source.getSourceOfDamage();
         if (!source.isExplosion()) {
            entityLivingBase.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
            entityLivingBase.playSound("damage.thorns", 0.5F, 1.0F);
         }
      }

      this.wander.makeUpdate();
      return super.attackEntityFrom(source, amount);
   }

   public void setElder() {
      this.setElder(true);
      this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation = 1.0F;
   }

   public int getAttackDuration() {
      return this.isElder() ? 60 : 80;
   }

   private void setSyncedFlag(int flagId, boolean state) {
      int watchableObjectInt = this.dataWatcher.getWatchableObjectInt(16);
      if (state) {
         this.dataWatcher.updateObject(16, watchableObjectInt | flagId);
      } else {
         this.dataWatcher.updateObject(16, watchableObjectInt & ~flagId);
      }
   }

   @Override
   protected String getDeathSound() {
      return !this.isInWater() ? "mob.guardian.land.death" : (this.isElder() ? "mob.guardian.elder.death" : "mob.guardian.death");
   }

   public EntityGuardian(World worldIn) {
      super(worldIn);
      this.experienceValue = 10;
      this.setSize(0.85F, 0.85F);
      this.tasks.addTask(4, new EntityGuardian.AIGuardianAttack(this));
      EntityAIMoveTowardsRestriction entityAIMoveTowardsRestriction;
      this.tasks.addTask(5, entityAIMoveTowardsRestriction = new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(7, this.wander = new EntityAIWander(this, 1.0, 80));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityGuardian.class, 12.0F, 0.01F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.wander.setMutexBits(3);
      entityAIMoveTowardsRestriction.setMutexBits(3);
      this.targetTasks
         .addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, new EntityGuardian.GuardianTargetSelector(this)));
      this.moveHelper = new EntityGuardian.GuardianMoveHelper(this);
      this.clientSideTailAnimationO = this.clientSideTailAnimation = this.rand.nextFloat();
   }

   public float getSpikesAnimation(float f) {
      return this.clientSideSpikesAnimationO + (this.clientSideSpikesAnimation - this.clientSideSpikesAnimationO) * f;
   }

   static class AIGuardianAttack extends EntityAIBase {
      private EntityGuardian theEntity;
      private int tickCounter;

      @Override
      public boolean continueExecuting() {
         return super.continueExecuting() && (this.theEntity.isElder() || this.theEntity.getDistanceSqToEntity(this.theEntity.getAttackTarget()) > 9.0);
      }

      @Override
      public void updateTask() {
         EntityLivingBase entityLivingBase = this.theEntity.getAttackTarget();
         this.theEntity.getNavigator().clearPathEntity();
         this.theEntity.getLookHelper().setLookPositionWithEntity(entityLivingBase, 90.0F, 90.0F);
         if (!this.theEntity.canEntityBeSeen(entityLivingBase)) {
            this.theEntity.setAttackTarget((EntityLivingBase)null);
         } else {
            this.tickCounter++;
            if (this.tickCounter == 0) {
               this.theEntity.setTargetedEntity(this.theEntity.getAttackTarget().getEntityId());
               this.theEntity.worldObj.setEntityState(this.theEntity, (byte)21);
            } else if (this.tickCounter >= this.theEntity.getAttackDuration()) {
               float f = 1.0F;
               if (this.theEntity.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                  f += 2.0F;
               }

               if (this.theEntity.isElder()) {
                  f += 2.0F;
               }

               entityLivingBase.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.theEntity, this.theEntity), f);
               entityLivingBase.attackEntityFrom(
                  DamageSource.causeMobDamage(this.theEntity),
                  (float)this.theEntity.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()
               );
               this.theEntity.setAttackTarget((EntityLivingBase)null);
            } else if (this.tickCounter >= 60 && this.tickCounter % 20 == 0) {
            }

            super.updateTask();
         }
      }

      @Override
      public void resetTask() {
         this.theEntity.setTargetedEntity(0);
         this.theEntity.setAttackTarget((EntityLivingBase)null);
         this.theEntity.wander.makeUpdate();
      }

      @Override
      public void startExecuting() {
         this.tickCounter = -10;
         this.theEntity.getNavigator().clearPathEntity();
         this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getAttackTarget(), 90.0F, 90.0F);
         this.theEntity.isAirBorne = true;
      }

      @Override
      public boolean shouldExecute() {
         EntityLivingBase entityLivingBase = this.theEntity.getAttackTarget();
         return entityLivingBase != null && entityLivingBase.isEntityAlive();
      }

      public AIGuardianAttack(EntityGuardian guardian) {
         this.theEntity = guardian;
         this.setMutexBits(3);
      }
   }

   static class GuardianMoveHelper extends EntityMoveHelper {
      private EntityGuardian entityGuardian;

      @Override
      public void onUpdateMoveHelper() {
         if (this.update && !this.entityGuardian.getNavigator().noPath()) {
            double d = this.posX - this.entityGuardian.posX;
            double d2 = this.posY - this.entityGuardian.posY;
            double yCoordinate2 = this.posZ - this.entityGuardian.posZ;
            double d3 = d * d + d2 * d2 + yCoordinate2 * yCoordinate2;
            d3 = MathHelper.sqrt_double(d3);
            d2 /= d3;
            float f = (float)(MathHelper.atan2(yCoordinate2, d) * 180.0 / Math.PI) - 90.0F;
            this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, f, 30.0F);
            this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw;
            float f2 = (float)(this.speed * this.entityGuardian.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            this.entityGuardian.setAIMoveSpeed(this.entityGuardian.getAIMoveSpeed() + (f2 - this.entityGuardian.getAIMoveSpeed()) * 0.125F);
            double d4 = Math.sin((this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5) * 0.05;
            double d5 = Math.cos(this.entityGuardian.rotationYaw * (float) Math.PI / 180.0F);
            double d6 = Math.sin(this.entityGuardian.rotationYaw * (float) Math.PI / 180.0F);
            this.entityGuardian.motionX += d4 * d5;
            this.entityGuardian.motionZ += d4 * d6;
            d4 = Math.sin((this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75) * 0.05;
            this.entityGuardian.motionY += d4 * (d6 + d5) * 0.25;
            this.entityGuardian.motionY = this.entityGuardian.motionY + this.entityGuardian.getAIMoveSpeed() * d2 * 0.1;
            EntityLookHelper lookHelper = this.entityGuardian.getLookHelper();
            double d7 = this.entityGuardian.posX + d / d3 * 2.0;
            double d8 = this.entityGuardian.getEyeHeight() + this.entityGuardian.posY + d2 / d3 * 1.0;
            double d9 = this.entityGuardian.posZ + yCoordinate2 / d3 * 2.0;
            double lookPosX = lookHelper.getLookPosX();
            double lookPosY = lookHelper.getLookPosY();
            double lookPosZ = lookHelper.getLookPosZ();
            if (!lookHelper.getIsLooking()) {
               lookPosX = d7;
               lookPosY = d8;
               lookPosZ = d9;
            }

            this.entityGuardian
               .getLookHelper()
               .setLookPosition(lookPosX + (d7 - lookPosX) * 0.125, lookPosY + (d8 - lookPosY) * 0.125, lookPosZ + (d9 - lookPosZ) * 0.125, 10.0F, 40.0F);
            this.entityGuardian.setMoving(true);
         } else {
            this.entityGuardian.setAIMoveSpeed(0.0F);
            this.entityGuardian.setMoving(false);
         }
      }

      public GuardianMoveHelper(EntityGuardian guardian) {
         super(guardian);
         this.entityGuardian = guardian;
      }
   }

   static class GuardianTargetSelector implements Predicate<EntityLivingBase> {
      private EntityGuardian parentEntity;

      public GuardianTargetSelector(EntityGuardian guardian) {
         this.parentEntity = guardian;
      }

      public boolean apply(EntityLivingBase arg1) {
         return (arg1 instanceof EntityPlayer || arg1 instanceof EntitySquid) && arg1.getDistanceSqToEntity(this.parentEntity) > 9.0;
      }
   }
}
