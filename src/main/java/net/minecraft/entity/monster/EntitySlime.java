package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class EntitySlime extends EntityLiving implements IMob {
   public float squishAmount;
   private boolean wasOnGround;
   public float squishFactor;
   public float prevSquishFactor;

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   @Override
   public boolean getCanSpawnHere() {
      BlockPos blockPos = new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ));
      Chunk chunkFromBlockCoords = this.worldObj.getChunkFromBlockCoords(blockPos);
      if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT && this.rand.nextInt(4) != 1) {
         return false;
      }

      if (this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL) {
         if (this.worldObj.getBiomeGenForCoords(blockPos) == BiomeGenBase.swampland
            && this.posY > 50.0
            && this.posY < 70.0
            && this.rand.nextFloat() < 0.5F
            && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor()
            && this.worldObj.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8)) {
            return super.getCanSpawnHere();
         }

         if (this.rand.nextInt(10) == 0 && chunkFromBlockCoords.getRandom().nextInt(10) == 0 && this.posY < 40.0) {
            return super.getCanSpawnHere();
         }
      }

      return false;
   }

   protected String getJumpSound() {
      return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
   }

   @Override
   public void setDead() {
      int slimeSize = this.getSlimeSize();
      if (!this.worldObj.isRemote && slimeSize > 1 && this.getHealth() <= 0.0F) {
         int i = 2 + this.rand.nextInt(3);

         for (int nestedIndex = 0; nestedIndex < i; nestedIndex++) {
            float f = (nestedIndex % 2 - 0.5F) * slimeSize / 4.0F;
            float f2 = (nestedIndex / 2 - 0.5F) * slimeSize / 4.0F;
            EntitySlime entitySlime = this.createInstance();
            if (this.hasCustomName()) {
               entitySlime.setCustomNameTag(this.getCustomNameTag());
            }

            if (this.isNoDespawnRequired()) {
               entitySlime.enablePersistence();
            }

            entitySlime.setSlimeSize(slimeSize / 2);
            entitySlime.setLocationAndAngles(this.posX + f, this.posY + 0.5, this.posZ + f2, this.rand.nextFloat() * 360.0F, 0.0F);
            this.worldObj.spawnEntityInWorld(entitySlime);
         }
      }

      super.setDead();
   }

   protected boolean makesSoundOnJump() {
      return this.getSlimeSize() > 0;
   }

   @Override
   protected String getDeathSound() {
      return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
   }

   @Override
   public int getVerticalFaceSpeed() {
      return 0;
   }

   public EntitySlime(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntitySlime.SlimeMoveHelper(this);
      this.tasks.addTask(1, new EntitySlime.AISlimeFloat(this));
      this.tasks.addTask(2, new EntitySlime.AISlimeAttack(this));
      this.tasks.addTask(3, new EntitySlime.AISlimeFaceRandom(this));
      this.tasks.addTask(5, new EntitySlime.AISlimeHop(this));
      this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
      this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
   }

   @Override
   public float getEyeHeight() {
      return 0.625F * this.height;
   }

   @Override
   public void onUpdate() {
      if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0) {
         this.isDead = true;
      }

      this.squishFactor = this.squishFactor + (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      super.onUpdate();
      if (this.onGround && !this.wasOnGround) {
         int slimeSize = this.getSlimeSize();

         for (int innerIndex = 0; innerIndex < slimeSize * 8; innerIndex++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f2 = this.rand.nextFloat() * 0.5F + 0.5F;
            float f3 = MathHelper.sin(f) * slimeSize * 0.5F * f2;
            float f4 = MathHelper.cos(f) * slimeSize * 0.5F * f2;
            World world = this.worldObj;
            EnumParticleTypes particleTypes = this.getParticleType();
            double xCoordinate = this.posX + f3;
            double zCoordinate = this.posZ + f4;
            world.spawnParticle(particleTypes, xCoordinate, this.getEntityBoundingBox().minY, zCoordinate, 0.0, 0.0, 0.0);
         }

         if (this.makesSoundOnLand()) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         }

         this.squishAmount = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.squishAmount = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.alterSquishAmount();
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.SLIME;
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)1);
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F * this.getSlimeSize();
   }

   protected int getAttackStrength() {
      return this.getSlimeSize();
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      int integer = tagCompund.getInteger("Size");
      if (integer < 0) {
         integer = 0;
      }

      this.setSlimeSize(integer + 1);
      this.wasOnGround = tagCompund.getBoolean("wasOnGround");
   }

   @Override
   public void applyEntityCollision(Entity entityIn) {
      super.applyEntityCollision(entityIn);
      if (entityIn instanceof EntityIronGolem && this.canDamagePlayer()) {
         this.attackEntity((EntityLivingBase)entityIn);
      }
   }

   @Override
   protected Item getDropItem() {
      return this.getSlimeSize() == 1 ? Items.slime_ball : null;
   }

   @Override
   protected String getHurtSound() {
      return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
   }

   protected EntitySlime createInstance() {
      return new EntitySlime(this.worldObj);
   }

   public int getSlimeSize() {
      return this.dataWatcher.getWatchableObjectByte(16);
   }

   protected void attackEntity(EntityLivingBase entityLivingBase) {
      int slimeSize = this.getSlimeSize();
      if (this.canEntityBeSeen(entityLivingBase)
         && this.getDistanceSqToEntity(entityLivingBase) < 0.6 * slimeSize * 0.6 * slimeSize
         && entityLivingBase.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength())) {
         this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.applyEnchantments(this, entityLivingBase);
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      int i = this.rand.nextInt(3);
      if (i < 2 && this.rand.nextFloat() < 0.5F * difficulty.getClampedAdditionalDifficulty()) {
         i++;
      }

      int i2 = 1 << i;
      this.setSlimeSize(i2);
      return super.onInitialSpawn(difficulty, livingdata);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Size", this.getSlimeSize() - 1);
      tagCompound.setBoolean("wasOnGround", this.wasOnGround);
   }

   @Override
   protected void jump() {
      this.motionY = 0.42F;
      this.isAirBorne = true;
   }

   @Override
   public void onDataWatcherUpdate(int dataID) {
      if (dataID == 16) {
         int slimeSize = this.getSlimeSize();
         this.setSize(0.51000005F * slimeSize, 0.51000005F * slimeSize);
         this.rotationYaw = this.rotationYawHead;
         this.renderYawOffset = this.rotationYawHead;
         if (this.isInWater() && this.rand.nextInt(20) == 0) {
            this.resetHeight();
         }
      }

      super.onDataWatcherUpdate(dataID);
   }

   protected boolean makesSoundOnLand() {
      return this.getSlimeSize() > 2;
   }

   protected boolean canDamagePlayer() {
      return this.getSlimeSize() > 1;
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (this.canDamagePlayer()) {
         this.attackEntity(entityIn);
      }
   }

   protected void setSlimeSize(int size) {
      this.dataWatcher.updateObject(16, (byte)size);
      this.setSize(0.51000005F * size, 0.51000005F * size);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(size * size);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F + 0.1F * size);
      this.setHealth(this.getMaxHealth());
      this.experienceValue = size;
   }

   protected int getJumpDelay() {
      return this.rand.nextInt(20) + 10;
   }

   static class AISlimeAttack extends EntityAIBase {
      private int deathTime;
      private EntitySlime slime;

      @Override
      public void updateTask() {
         this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setTargetYaw(this.slime.rotationYaw, this.slime.canDamagePlayer());
      }

      @Override
      public boolean shouldExecute() {
         EntityLivingBase attackTarget = this.slime.getAttackTarget();
         return attackTarget == null ? false : (!attackTarget.isEntityAlive() ? false : !(attackTarget instanceof EntityPlayer) || !((EntityPlayer)attackTarget).capabilities.disableDamage);
      }

      @Override
      public boolean continueExecuting() {
         EntityLivingBase attackTarget = this.slime.getAttackTarget();
         return attackTarget == null
            ? false
            : (!attackTarget.isEntityAlive() ? false : (attackTarget instanceof EntityPlayer && ((EntityPlayer)attackTarget).capabilities.disableDamage ? false : --this.deathTime > 0));
      }

      @Override
      public void startExecuting() {
         this.deathTime = 300;
         super.startExecuting();
      }

      public AISlimeAttack(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(2);
      }
   }

   static class AISlimeFaceRandom extends EntityAIBase {
      private float randomYaw;
      private EntitySlime slime;
      private int directionChangeTime;

      public AISlimeFaceRandom(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(2);
      }

      @Override
      public boolean shouldExecute() {
         return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava());
      }

      @Override
      public void updateTask() {
         if (--this.directionChangeTime <= 0) {
            this.directionChangeTime = 40 + this.slime.getRNG().nextInt(60);
            this.randomYaw = this.slime.getRNG().nextInt(360);
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setTargetYaw(this.randomYaw, false);
      }
   }

   static class AISlimeFloat extends EntityAIBase {
      private EntitySlime slime;

      public AISlimeFloat(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(5);
         ((PathNavigateGround)slimeIn.getNavigator()).update();
      }

      @Override
      public boolean shouldExecute() {
         return this.slime.isInWater() || this.slime.isInLava();
      }

      @Override
      public void updateTask() {
         if (this.slime.getRNG().nextFloat() < 0.8F) {
            this.slime.getJumpHelper().setJumping();
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.2);
      }
   }

   static class AISlimeHop extends EntityAIBase {
      private EntitySlime slime;

      public AISlimeHop(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(5);
      }

      @Override
      public void updateTask() {
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0);
      }

      @Override
      public boolean shouldExecute() {
         return true;
      }
   }

   static class SlimeMoveHelper extends EntityMoveHelper {
      private float slimeTargetYaw;
      private int jumpDelay;
      private EntitySlime slime;
      private boolean shouldJump;

      public void setSpeed(double speedIn) {
         this.speed = speedIn;
         this.update = true;
      }

      @Override
      public void onUpdateMoveHelper() {
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.slimeTargetYaw, 30.0F);
         this.entity.rotationYawHead = this.entity.rotationYaw;
         this.entity.renderYawOffset = this.entity.rotationYaw;
         if (!this.update) {
            this.entity.setMoveForward(0.0F);
         } else {
            this.update = false;
            if (this.entity.onGround) {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.shouldJump) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpHelper().setJumping();
                  if (this.slime.makesSoundOnJump()) {
                     this.slime
                        .playSound(
                           this.slime.getJumpSound(),
                           this.slime.getSoundVolume(),
                           ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F
                        );
                  }
               } else {
                  this.slime.moveStrafing = this.slime.moveForward = 0.0F;
                  this.entity.setAIMoveSpeed(0.0F);
               }
            } else {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
            }
         }
      }

      public void setTargetYaw(float f, boolean flag) {
         this.slimeTargetYaw = f;
         this.shouldJump = flag;
      }

      public SlimeMoveHelper(EntitySlime slimeIn) {
         super(slimeIn);
         this.slime = slimeIn;
      }
   }
}
