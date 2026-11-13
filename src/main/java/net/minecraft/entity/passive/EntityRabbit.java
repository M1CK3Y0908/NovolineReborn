package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityRabbit extends EntityAnimal {
   private int jumpMoveTime = 0;
   private int nextJumpMoveTime = 0;
   private boolean isJumping2 = false;
   private boolean wasOnGround = false;
   private int currentMoveTypeDuration = 0;
   private EntityRabbit.EnumMoveType moveType = EntityRabbit.EnumMoveType.HOP;
   private int carrotTicks = 0;
   private EntityPlayer ownerEntity = null;
   private EntityRabbit.AIAvoidEntity<EntityWolf> aiAvoidWolves;

   @Override
   protected void addRandomDrop() {
      this.entityDropItem(new ItemStack(Items.rabbit_foot, 1), 0.0F);
   }

   public void setMoveType(EntityRabbit.EnumMoveType type) {
      this.moveType = type;
   }

   @Override
   protected String getDeathSound() {
      return "mob.rabbit.death";
   }

   private boolean isCarrotEaten() {
      return this.carrotTicks == 0;
   }

   public EntityRabbit(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 0.7F);
      this.jumpHelper = new EntityRabbit.RabbitJumpHelper(this);
      this.moveHelper = new EntityRabbit.RabbitMoveHelper(this);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.navigator.setHeightRequirement(2.5F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityRabbit.AIPanic(this, 1.33));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Items.carrot, false));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Items.golden_carrot, false));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Item.getItemFromBlock(Blocks.yellow_flower), false));
      this.tasks.addTask(3, new EntityAIMate(this, 0.8));
      this.tasks.addTask(5, new EntityRabbit.AIRaidFarm(this));
      this.tasks.addTask(5, new EntityAIWander(this, 0.6));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
      this.aiAvoidWolves = new EntityRabbit.AIAvoidEntity<>(this, EntityWolf.class, 16.0F, 1.33, 1.33);
      this.tasks.addTask(4, this.aiAvoidWolves);
      this.setMovementSpeed(0.0);
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, (byte)0);
   }

   public float getJumpProgress(float partialTicks) {
      return this.nextJumpMoveTime == 0 ? 0.0F : (this.jumpMoveTime + partialTicks) / this.nextJumpMoveTime;
   }

   private boolean isRabbitBreedingItem(Item itemIn) {
      return itemIn == Items.carrot || itemIn == Items.golden_carrot || itemIn == Item.getItemFromBlock(Blocks.yellow_flower);
   }

   private void startJump() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setJumping(true);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return this.isEntityInvulnerable(source) ? false : super.attackEntityFrom(source, amount);
   }

   @Override
   protected float getJumpUpwardsMotion() {
      return this.moveHelper.isUpdating() && this.moveHelper.getY() > this.posY + 0.5 ? 0.5F : this.moveType.getJumpMotion();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("RabbitType", this.getRabbitType());
      tagCompound.setInteger("MoreCarrotTicks", this.carrotTicks);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.jumpMoveTime != this.nextJumpMoveTime) {
         if (this.jumpMoveTime == 0 && !this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, (byte)1);
         }

         this.jumpMoveTime++;
      } else if (this.nextJumpMoveTime != 0) {
         this.jumpMoveTime = 0;
         this.nextJumpMoveTime = 0;
      }
   }

   public void doMovementAction(EntityRabbit.EnumMoveType moveType) {
      this.setJumping(true, moveType);
      this.nextJumpMoveTime = moveType.getStepHeight();
      this.jumpMoveTime = 0;
   }

   @Override
   public void updateAITasks() {
      if (this.moveHelper.getSpeed() > 0.8) {
         this.setMoveType(EntityRabbit.EnumMoveType.SPRINT);
      } else if (this.moveType != EntityRabbit.EnumMoveType.ATTACK) {
         this.setMoveType(EntityRabbit.EnumMoveType.HOP);
      }

      if (this.currentMoveTypeDuration > 0) {
         this.currentMoveTypeDuration--;
      }

      if (this.carrotTicks > 0) {
         this.carrotTicks = this.carrotTicks - this.rand.nextInt(3);
         if (this.carrotTicks < 0) {
            this.carrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false, EntityRabbit.EnumMoveType.NONE);
            this.resetMoveTypeDuration();
         }

         if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0) {
            EntityLivingBase entityLivingBase = this.getAttackTarget();
            if (entityLivingBase != null && this.getDistanceSqToEntity(entityLivingBase) < 16.0) {
               this.calculateRotationYaw(entityLivingBase.posX, entityLivingBase.posZ);
               this.moveHelper.setMoveTo(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, this.moveHelper.getSpeed());
               this.doMovementAction(EntityRabbit.EnumMoveType.ATTACK);
               this.wasOnGround = true;
            }
         }

         EntityRabbit.RabbitJumpHelper entityrabbit$rabbitjumphelper = (EntityRabbit.RabbitJumpHelper)this.jumpHelper;
         if (!entityrabbit$rabbitjumphelper.getIsJumping()) {
            if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
               PathEntity pathEntity = this.navigator.getPath();
               Vec3 vec3 = new Vec3(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());
               if (pathEntity != null && pathEntity.getCurrentPathIndex() < pathEntity.getCurrentPathLength()) {
                  vec3 = pathEntity.getPosition(this);
               }

               this.calculateRotationYaw(vec3.xCoord, vec3.zCoord);
               this.doMovementAction(this.moveType);
            }
         } else if (!entityrabbit$rabbitjumphelper.isForcingJump()) {
            this.startJump();
         }
      }

      this.wasOnGround = this.onGround;
   }

   protected int getMoveTypeDuration() {
      return this.moveType.getDuration();
   }

   @Override
   protected String getHurtSound() {
      return "mob.rabbit.hurt";
   }

   protected String getJumpingSound() {
      return "mob.rabbit.hop";
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
   }

   private void updateMoveTypeDuration() {
      this.currentMoveTypeDuration = this.getMoveTypeDuration();
   }

   public boolean isCurrentlyJumping() {
      return this.isJumping2;
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      int i = this.rand.nextInt(6);
      boolean conditionMet = false;
      if (livingdata instanceof EntityRabbit.RabbitTypeData) {
         i = ((EntityRabbit.RabbitTypeData)livingdata).typeData;
         conditionMet = true;
      } else {
         livingdata = new EntityRabbit.RabbitTypeData(i);
      }

      this.setRabbitType(i);
      if (conditionMet) {
         this.setGrowingAge(-24000);
      }

      return livingdata;
   }

   @Override
   public void spawnRunningParticles() {
   }

   @Override
   public int getTotalArmorValue() {
      return this.getRabbitType() == 99 ? 8 : super.getTotalArmorValue();
   }

   protected void createEatingParticles() {
      this.worldObj
         .spawnParticle(
            EnumParticleTypes.BLOCK_DUST,
            this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
            this.posY + 0.5 + this.rand.nextFloat() * this.height,
            this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
            0.0,
            0.0,
            0.0,
            Block.getStateId(Blocks.carrots.getStateFromMeta(7))
         );
      this.carrotTicks = 100;
   }

   private void playMovementSound() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setJumping(false);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setRabbitType(tagCompund.getInteger("RabbitType"));
      this.carrotTicks = tagCompund.getInteger("MoreCarrotTicks");
   }

   public int getRabbitType() {
      return this.dataWatcher.getWatchableObjectByte(18);
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int i = this.rand.nextInt(2) + this.rand.nextInt(1 + lootingModifier);

      for (int i2 = 0; i2 < i; i2++) {
         this.getEntityItem(Items.rabbit_hide);
      }

      i = this.rand.nextInt(2);

      for (int i3 = 0; i3 < i; i3++) {
         if (this.isBurning()) {
            this.getEntityItem(Items.cooked_rabbit);
         } else {
            this.getEntityItem(Items.rabbit);
         }
      }
   }

   public void setJumping(boolean jump, EntityRabbit.EnumMoveType moveTypeIn) {
      super.setJumping(jump);
      if (!jump) {
         if (this.moveType == EntityRabbit.EnumMoveType.ATTACK) {
            this.moveType = EntityRabbit.EnumMoveType.HOP;
         }
      } else {
         this.setMovementSpeed(1.5 * moveTypeIn.getSpeed());
         this.playSound(this.getJumpingSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

      this.isJumping2 = jump;
   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigator().setSpeed(newSpeed);
      this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
   }

   public void setRabbitType(int rabbitTypeId) {
      if (rabbitTypeId == 99) {
         this.tasks.removeTask(this.aiAvoidWolves);
         this.tasks.addTask(4, new EntityRabbit.AIEvilAttack(this));
         this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityWolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomNameTag(StatCollector.translateToLocal("entity.KillerBunny.name"));
         }
      }

      this.dataWatcher.updateObject(18, (byte)rabbitTypeId);
   }

   private void calculateRotationYaw(double x, double z) {
      this.rotationYaw = (float)(MathHelper.atan2(z - this.posZ, x - this.posX) * 180.0 / Math.PI) - 90.0F;
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && this.isRabbitBreedingItem(stack.getItem());
   }

   @Override
   protected String getLivingSound() {
      return "mob.rabbit.idle";
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getRabbitType() == 99) {
         this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
      } else {
         return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
      }
   }

   public EntityRabbit createChild(EntityAgeable ageable) {
      EntityRabbit entityRabbit = new EntityRabbit(this.worldObj);
      if (ageable instanceof EntityRabbit) {
         entityRabbit.setRabbitType(this.rand.nextBoolean() ? this.getRabbitType() : ((EntityRabbit)ageable).getRabbitType());
      }

      return entityRabbit;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 1) {
         this.createRunningParticles();
         this.nextJumpMoveTime = 10;
         this.jumpMoveTime = 0;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   private void resetMoveTypeDuration() {
      this.updateMoveTypeDuration();
      this.playMovementSound();
   }

   static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      private EntityRabbit entityInstance;

      public AIAvoidEntity(EntityRabbit rabbit, Class<T> clazz, float f, double d, double d2) {
         super(rabbit, clazz, f, d, d2);
         this.entityInstance = rabbit;
      }

      @Override
      public void updateTask() {
         super.updateTask();
      }
   }

   static class AIEvilAttack extends EntityAIAttackOnCollide {
      @Override
      protected double getAttackReachSq(EntityLivingBase attackTarget) {
         return 4.0F + attackTarget.width;
      }

      public AIEvilAttack(EntityRabbit rabbit) {
         super(rabbit, EntityLivingBase.class, 1.4, true);
      }
   }

   static class AIPanic extends EntityAIPanic {
      private EntityRabbit theEntity;

      public AIPanic(EntityRabbit rabbit, double speedIn) {
         super(rabbit, speedIn);
         this.theEntity = rabbit;
      }

      @Override
      public void updateTask() {
         super.updateTask();
         this.theEntity.setMovementSpeed(this.speed);
      }
   }

   static class AIRaidFarm extends EntityAIMoveToBlock {
      private boolean shouldRaid = false;
      private boolean flag;
      private final EntityRabbit rabbit;

      @Override
      public boolean shouldExecute() {
         if (this.runDelay <= 0) {
            if (!this.rabbit.worldObj.getGameRules().getBoolean("mobGriefing")) {
               return false;
            }

            this.shouldRaid = false;
            this.flag = this.rabbit.isCarrotEaten();
         }

         return super.shouldExecute();
      }

      @Override
      public void startExecuting() {
         super.startExecuting();
      }

      @Override
      public void resetTask() {
         super.resetTask();
      }

      @Override
      protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
         if (worldIn.getBlockState(pos).getBlock() == Blocks.farmland) {
            pos = pos.up();
            IBlockState blockState2 = worldIn.getBlockState(pos);
            if (blockState2.getBlock() instanceof BlockCarrot && blockState2.getValue(BlockCarrot.AGE) == 7 && this.flag && !this.shouldRaid) {
               this.shouldRaid = true;
               return true;
            }
         }

         return false;
      }

      public AIRaidFarm(EntityRabbit rabbitIn) {
         super(rabbitIn, 0.7F, 16);
         this.rabbit = rabbitIn;
      }

      @Override
      public void updateTask() {
         super.updateTask();
         this.rabbit
            .getLookHelper()
            .setLookPosition(
               this.destinationBlock.getX() + 0.5,
               this.destinationBlock.getY() + 1,
               this.destinationBlock.getZ() + 0.5,
               10.0F,
               this.rabbit.getVerticalFaceSpeed()
            );
         if (this.getIsAboveDestination()) {
            World world = this.rabbit.worldObj;
            BlockPos blockPos = this.destinationBlock.up();
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (this.shouldRaid && block instanceof BlockCarrot && blockState.getValue(BlockCarrot.AGE) == 7) {
               world.setBlockState(blockPos, Blocks.air.getDefaultState(), 2);
               world.destroyBlock(blockPos, true);
               this.rabbit.createEatingParticles();
            }

            this.shouldRaid = false;
            this.runDelay = 10;
         }
      }

      @Override
      public boolean continueExecuting() {
         return this.shouldRaid && super.continueExecuting();
      }
   }

   enum EnumMoveType {
      NONE(0.0F, 0.0F, 30, 1),
      HOP(0.8F, 0.2F, 20, 10),
      STEP(1.0F, 0.45F, 14, 14),
      SPRINT(1.75F, 0.4F, 1, 8),
      ATTACK(2.0F, 0.7F, 7, 8);

      private final float speed;
      private final float jumpMotion;
      private final int duration;
      private final int stepHeight;

      public int getStepHeight() {
         return this.stepHeight;
      }

      EnumMoveType(float f, float f2, int i, int i2) {
         this.speed = f;
         this.jumpMotion = f2;
         this.duration = i;
         this.stepHeight = i2;
      }

      public float getSpeed() {
         return this.speed;
      }

      public int getDuration() {
         return this.duration;
      }

      public float getJumpMotion() {
         return this.jumpMotion;
      }
   }

   public class RabbitJumpHelper extends EntityJumpHelper {
      private boolean forceJump = false;
      private EntityRabbit theEntity;

      public RabbitJumpHelper(EntityRabbit rabbit) {
         super(rabbit);
         this.theEntity = rabbit;
      }

      public boolean getIsJumping() {
         return this.isJumping;
      }

      @Override
      public void doJump() {
         if (this.isJumping) {
            this.theEntity.doMovementAction(EntityRabbit.EnumMoveType.STEP);
            this.isJumping = false;
         }
      }

      public boolean isForcingJump() {
         return this.forceJump;
      }

      public void setJumping(boolean flag2) {
         this.forceJump = flag2;
      }
   }

   static class RabbitMoveHelper extends EntityMoveHelper {
      private EntityRabbit theEntity;

      public RabbitMoveHelper(EntityRabbit rabbit) {
         super(rabbit);
         this.theEntity = rabbit;
      }

      @Override
      public void onUpdateMoveHelper() {
         if (this.theEntity.onGround && !this.theEntity.isCurrentlyJumping()) {
            this.theEntity.setMovementSpeed(0.0);
         }

         super.onUpdateMoveHelper();
      }
   }

   public static class RabbitTypeData implements IEntityLivingData {
      public int typeData;

      public RabbitTypeData(int type) {
         this.typeData = type;
      }
   }
}
