package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {
   private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier attackingSpeedBoostModifier = new AttributeModifier(
         attackingSpeedBoostModifierUUID, "Attacking speed boost", 0.15F, 0
      )
      .getAttributeModifier();
   private static final Set<Block> carriableBlocks = Sets.newIdentityHashSet();
   private boolean isAggressive;

   @Override
   public float getEyeHeight() {
      return 2.55F;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         for (int index = 0; index < 2; index++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.PORTAL,
                  this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                  this.posY + this.rand.nextDouble() * this.height - 0.25,
                  this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                  (this.rand.nextDouble() - 0.5) * 2.0,
                  -this.rand.nextDouble(),
                  (this.rand.nextDouble() - 0.5) * 2.0
               );
         }
      }

      this.isJumping = false;
      super.onLivingUpdate();
   }

   public EntityEnderman(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 2.9F);
      this.stepHeight = 1.0F;
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0, false));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(10, new EntityEnderman.AIPlaceBlock(this));
      this.tasks.addTask(11, new EntityEnderman.AITakeBlock(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityEnderman.AIFindPlayer(this));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityEndermite.class, 10, true, false, new Predicate<EntityEndermite>() {
         public boolean apply(EntityEndermite arg1) {
            return arg1.isSpawnedByPlayer();
         }
      }));
   }

   @Override
   protected String getHurtSound() {
      return "mob.endermen.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.endermen.death";
   }

   public IBlockState getHeldBlockState() {
      return Block.getStateById(this.dataWatcher.getWatchableObjectShort(16) & 65535);
   }

   public void setHeldBlockState(IBlockState state) {
      this.dataWatcher.updateObject(16, (short)(Block.getStateId(state) & 65535));
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      IBlockState iBlockState;
      if (tagCompund.hasKey("carried", 8)) {
         iBlockState = Block.getBlockFromName(tagCompund.getString("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
      } else {
         iBlockState = Block.getBlockById(tagCompund.getShort("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
      }

      this.setHeldBlockState(iBlockState);
   }

   static {
      carriableBlocks.add(Blocks.grass);
      carriableBlocks.add(Blocks.dirt);
      carriableBlocks.add(Blocks.sand);
      carriableBlocks.add(Blocks.gravel);
      carriableBlocks.add(Blocks.yellow_flower);
      carriableBlocks.add(Blocks.red_flower);
      carriableBlocks.add(Blocks.brown_mushroom);
      carriableBlocks.add(Blocks.red_mushroom);
      carriableBlocks.add(Blocks.tnt);
      carriableBlocks.add(Blocks.cactus);
      carriableBlocks.add(Blocks.clay);
      carriableBlocks.add(Blocks.pumpkin);
      carriableBlocks.add(Blocks.melon_block);
      carriableBlocks.add(Blocks.mycelium);
   }

   protected boolean teleportRandomly() {
      double xCoordinate = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
      double yCoordinate = this.posY + (this.rand.nextInt(64) - 32);
      double zCoordinate = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
      return this.teleportTo(xCoordinate, yCoordinate, zCoordinate);
   }

   protected boolean teleportTo(double x, double y, double z) {
      double posX2 = this.posX;
      double posY2 = this.posY;
      double posZ2 = this.posZ;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      boolean flag = false;
      BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
      if (this.worldObj.isBlockLoaded(blockPos)) {
         boolean flag2 = false;

         while (!flag2 && blockPos.getY() > 0) {
            BlockPos blockPos2 = blockPos.down();
            Block block3 = this.worldObj.getBlockState(blockPos2).getBlock();
            if (block3.getMaterial().blocksMovement()) {
               flag2 = true;
            } else {
               this.posY--;
               blockPos = blockPos2;
            }
         }

         if (flag2) {
            super.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            if (this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox())
               )
             {
               flag = true;
            }
         }
      }

      if (!flag) {
         this.setPosition(posX2, posY2, posZ2);
         return false;
      }

      for (int i = 0; i < 128; i++) {
         double d = i / (128 - 1.0);
         float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
         float f3 = (this.rand.nextFloat() - 0.5F) * 0.2F;
         double d2 = posX2 + (this.posX - posX2) * d + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
         double d3 = posY2 + (this.posY - posY2) * d + this.rand.nextDouble() * this.height;
         double d4 = posZ2 + (this.posZ - posZ2) * d + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
         this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, d2, d3, d4, f, f2, f3);
      }

      this.worldObj.playSoundEffect(posX2, posY2, posZ2, "mob.endermen.portal", 1.0F, 1.0F);
      this.playSound("mob.endermen.portal", 1.0F, 1.0F);
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source.getEntity() == null || !(source.getEntity() instanceof EntityEndermite)) {
         if (!this.worldObj.isRemote) {
            this.setScreaming(true);
         }

         if (source instanceof EntityDamageSource && source.getEntity() instanceof EntityPlayer) {
            if (source.getEntity() instanceof EntityPlayerMP && ((EntityPlayerMP)source.getEntity()).theItemInWorldManager.isCreative()) {
               this.setScreaming(false);
            } else {
               this.isAggressive = true;
            }
         }

         if (source instanceof EntityDamageSourceIndirect) {
            this.isAggressive = false;

            for (int index = 0; index < 64; index++) {
               if (this.teleportRandomly()) {
                  return true;
               }
            }

            return false;
         }
      }

      boolean conditionMet = super.attackEntityFrom(source, amount);
      if (source.isUnblockable() && this.rand.nextInt(10) != 0) {
         this.teleportRandomly();
      }

      return conditionMet;
   }

   @Override
   protected String getLivingSound() {
      return this.isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Short((short)0));
      this.dataWatcher.addObject(17, new Byte((byte)0));
      this.dataWatcher.addObject(18, new Byte((byte)0));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      IBlockState blockState = this.getHeldBlockState();
      tagCompound.setShort("carried", (short)Block.getIdFromBlock(blockState.getBlock()));
      tagCompound.setShort("carriedData", (short)blockState.getBlock().getMetaFromState(blockState));
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item item = this.getDropItem();
      if (item != null) {
         int i = this.rand.nextInt(2 + lootingModifier);

         for (int innerIndex = 0; innerIndex < i; innerIndex++) {
            this.getEntityItem(item);
         }
      }
   }

   @Override
   protected void updateAITasks() {
      if (this.isWet()) {
         this.attackEntityFrom(DamageSource.drown, 1.0F);
      }

      if (this.isScreaming() && !this.isAggressive && this.rand.nextInt(100) == 0) {
         this.setScreaming(false);
      }

      if (this.worldObj.isDaytime()) {
         float brightness = this.getBrightness(1.0F);
         if (brightness > 0.5F && this.worldObj.canSeeSky(new BlockPos(this)) && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
            this.setAttackTarget((EntityLivingBase)null);
            this.setScreaming(false);
            this.isAggressive = false;
            this.teleportRandomly();
         }
      }

      super.updateAITasks();
   }

   @Override
   protected Item getDropItem() {
      return Items.ender_pearl;
   }

   protected boolean teleportToEntity(Entity entity2) {
      Vec3 vec3 = new Vec3(
         this.posX - entity2.posX, this.getEntityBoundingBox().minY + this.height / 2.0F - entity2.posY + entity2.getEyeHeight(), this.posZ - entity2.posZ
      );
      vec3 = vec3.normalize();
      double d = 16.0;
      double d2 = this.posX + (this.rand.nextDouble() - 0.5) * 8.0 - vec3.xCoord * d;
      double d3 = this.posY + (this.rand.nextInt(16) - 8) - vec3.yCoord * d;
      double d4 = this.posZ + (this.rand.nextDouble() - 0.5) * 8.0 - vec3.zCoord * d;
      return this.teleportTo(d2, d3, d4);
   }

   public void setScreaming(boolean screaming) {
      this.dataWatcher.updateObject(18, (byte)(screaming ? 1 : 0));
   }

   public boolean isScreaming() {
      return this.dataWatcher.getWatchableObjectByte(18) > 0;
   }

   private boolean shouldAttackPlayer(EntityPlayer player) {
      ItemStack itemStack = player.inventory.armorInventory[3];
      if (itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         return false;
      }

      Vec3 vec3 = player.getLook(1.0F).normalize();
      Vec3 vec32 = new Vec3(
         this.posX - player.posX, this.getEntityBoundingBox().minY + this.height / 2.0F - (player.posY + player.getEyeHeight()), this.posZ - player.posZ
      );
      double d = vec32.lengthVector();
      vec32 = vec32.normalize();
      double d2 = vec3.dotProduct(vec32);
      return d2 > 1.0 - 0.025 / d ? player.canEntityBeSeen(this) : false;
   }

   static class AIFindPlayer extends EntityAINearestAttackableTarget {
      private EntityEnderman enderman;
      private int teleportDelay;
      private int attackDelay;
      private EntityPlayer player;

      @Override
      public void startExecuting() {
         this.attackDelay = 5;
         this.teleportDelay = 0;
      }

      @Override
      public boolean shouldExecute() {
         double targetDistance = this.getTargetDistance();
         List<EntityPlayer> list = this.taskOwner
            .worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0, targetDistance), this.targetEntitySelector);
         Collections.sort(list, this.theNearestAttackableTargetSorter);
         if (list.isEmpty()) {
            return false;
         }

         this.player = list.get(0);
         return true;
      }

      @Override
      public void resetTask() {
         this.player = null;
         this.enderman.setScreaming(false);
         this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(EntityEnderman.attackingSpeedBoostModifier);
         super.resetTask();
      }

      @Override
      public void updateTask() {
         if (this.player != null) {
            if (--this.attackDelay <= 0) {
               this.targetEntity = this.player;
               this.player = null;
               super.startExecuting();
               this.enderman.playSound("mob.endermen.stare", 1.0F, 1.0F);
               this.enderman.setScreaming(true);
               this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(EntityEnderman.attackingSpeedBoostModifier);
            }
         } else {
            if (this.targetEntity != null) {
               if (this.targetEntity instanceof EntityPlayer && this.enderman.shouldAttackPlayer((EntityPlayer)this.targetEntity)) {
                  if (this.targetEntity.getDistanceSqToEntity(this.enderman) < 16.0) {
                     this.enderman.teleportRandomly();
                  }

                  this.teleportDelay = 0;
               } else if (this.targetEntity.getDistanceSqToEntity(this.enderman) > 256.0
                  && this.teleportDelay++ >= 30
                  && this.enderman.teleportToEntity(this.targetEntity)) {
                  this.teleportDelay = 0;
               }
            }

            super.updateTask();
         }
      }

      public AIFindPlayer(EntityEnderman endermanIn) {
         super(endermanIn, EntityPlayer.class, true);
         this.enderman = endermanIn;
      }

      @Override
      public boolean continueExecuting() {
         if (this.player != null) {
            if (!this.enderman.shouldAttackPlayer(this.player)) {
               return false;
            }

            this.enderman.isAggressive = true;
            this.enderman.faceEntity(this.player, 10.0F, 10.0F);
            return true;
         } else {
            return super.continueExecuting();
         }
      }
   }

   static class AIPlaceBlock extends EntityAIBase {
      private EntityEnderman enderman;

      public AIPlaceBlock(EntityEnderman endermanIn) {
         this.enderman = endermanIn;
      }

      @Override
      public boolean shouldExecute() {
         return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing")
            ? false
            : (this.enderman.getHeldBlockState().getBlock().getMaterial() == Material.air ? false : this.enderman.getRNG().nextInt(2000) == 0);
      }

      @Override
      public void updateTask() {
         Random random = this.enderman.getRNG();
         World world = this.enderman.worldObj;
         int i = MathHelper.floor_double(this.enderman.posX - 1.0 + random.nextDouble() * 2.0);
         int i2 = MathHelper.floor_double(this.enderman.posY + random.nextDouble() * 2.0);
         int i3 = MathHelper.floor_double(this.enderman.posZ - 1.0 + random.nextDouble() * 2.0);
         BlockPos blockPos = new BlockPos(i, i2, i3);
         Block block = world.getBlockState(blockPos).getBlock();
         Block block2 = world.getBlockState(blockPos.down()).getBlock();
         if (this.canPlaceHeldBlock(world, blockPos, this.enderman.getHeldBlockState().getBlock(), block, block2)) {
            world.setBlockState(blockPos, this.enderman.getHeldBlockState(), 3);
            this.enderman.setHeldBlockState(Blocks.air.getDefaultState());
         }
      }

      private boolean canPlaceHeldBlock(World worldIn, BlockPos pos, Block carriedBlock, Block block, Block blockBelow) {
         return !carriedBlock.canPlaceBlockAt(worldIn, pos)
            ? false
            : (block.getMaterial() != Material.air ? false : (blockBelow.getMaterial() == Material.air ? false : blockBelow.isFullCube()));
      }
   }

   static class AITakeBlock extends EntityAIBase {
      private EntityEnderman enderman;

      @Override
      public boolean shouldExecute() {
         return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing")
            ? false
            : (this.enderman.getHeldBlockState().getBlock().getMaterial() != Material.air ? false : this.enderman.getRNG().nextInt(20) == 0);
      }

      public AITakeBlock(EntityEnderman endermanIn) {
         this.enderman = endermanIn;
      }

      @Override
      public void updateTask() {
         Random random = this.enderman.getRNG();
         World world = this.enderman.worldObj;
         int i = MathHelper.floor_double(this.enderman.posX - 2.0 + random.nextDouble() * 4.0);
         int i2 = MathHelper.floor_double(this.enderman.posY + random.nextDouble() * 3.0);
         int i3 = MathHelper.floor_double(this.enderman.posZ - 2.0 + random.nextDouble() * 4.0);
         BlockPos blockPos = new BlockPos(i, i2, i3);
         IBlockState blockState = world.getBlockState(blockPos);
         Block block = blockState.getBlock();
         if (EntityEnderman.carriableBlocks.contains(block)) {
            this.enderman.setHeldBlockState(blockState);
            world.setBlockState(blockPos, Blocks.air.getDefaultState());
         }
      }
   }
}
