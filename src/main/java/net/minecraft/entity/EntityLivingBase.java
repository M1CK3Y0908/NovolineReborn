package net.minecraft.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import wtf.novoline.Novoline;
import wtf.novoline.event.JumpEvent;
import wtf.novoline.module.ModuleManager;

public abstract class EntityLivingBase extends Entity {
   private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   protected float prevOnGroundSpeedFactor;
   protected float lastDamage;
   protected int recentlyHit;
   public float prevLimbSwingAmount;
   private final CombatTracker _combatTracker = new CombatTracker(this);
   protected float randomYawVelocity;
   protected int entityAge;
   protected double newRotationYaw;
   public float prevRotationYawHead;
   public int maxHurtTime;
   protected double newPosY;
   public float moveStrafing;
   public float moveForward;
   protected double newPosX;
   private BaseAttributeMap attributeMap;
   protected float movedDistance;
   public int swingProgressInt;
   public int hurtTime;
   private static final AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.3F, 2)
      .getAttributeModifier();
   public float limbSwingAmount;
   private final Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();
   protected EntityPlayer attackingPlayer;
   protected int newPosRotationIncrements;
   protected float unused180;
   private final ItemStack[] previousEquipment = new ItemStack[5];
   protected int scoreValue;
   public float limbSwing;
   private float absorptionAmount;
   public int maxHurtResistantTime = 20;
   public float jumpMovementFactor = 0.02F;
   public int jumpTicks;
   public float prevRenderYawOffset;
   protected float onGroundSpeedFactor;
   private EntityLivingBase lastAttacker;
   public boolean isSwingInProgress;
   public int arrowHitTimer;
   private boolean potionsNeedUpdate = true;
   public float attackedAtYaw;
   protected boolean isJumping;
   public float randomUnused1;
   public int deathTime;
   public float randomUnused2;
   private EntityLivingBase entityLivingToAttack;
   public float renderYawOffset;
   public float prevCameraPitch;
   protected float prevMovedDistance;
   private int lastAttackerTime;
   public float swingProgress;
   public float prevSwingProgress;
   private int revengeTimer;
   public float rotationYawHead;
   protected double newRotationPitch;
   public float cameraPitch;
   public float rotationPitchHead;
   private float landMovementFactor;
   public float prevRotationPitchHead;
   protected double newPosZ;
   protected boolean dead;

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   protected void addRandomDrop() {
   }

   public void onItemPickup(Entity entity2, int i) {
      if (!entity2.isDead && !this.worldObj.isRemote) {
         EntityTracker entityTracker = ((WorldServer)this.worldObj).getEntityTracker();
         if (entity2 instanceof EntityItem) {
            entityTracker.sendToAllTrackingEntity(entity2, new S0DPacketCollectItem(entity2.getEntityId(), this.getEntityId()));
         }

         if (entity2 instanceof EntityArrow) {
            entityTracker.sendToAllTrackingEntity(entity2, new S0DPacketCollectItem(entity2.getEntityId(), this.getEntityId()));
         }

         if (entity2 instanceof EntityXPOrb) {
            entityTracker.sendToAllTrackingEntity(entity2, new S0DPacketCollectItem(entity2.getEntityId(), this.getEntityId()));
         }
      }
   }

   protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
   }

   public boolean isOnSameTeam(EntityLivingBase otherEntity) {
      return this.isOnTeam(otherEntity.getTeam());
   }

   protected String getDeathSound() {
      return "game.neutral.die";
   }

   public void addPotionEffect(PotionEffect potioneffectIn) {
      if (this.isPotionApplicable(potioneffectIn)) {
         if (this.activePotionsMap.containsKey(potioneffectIn.getPotionID())) {
            this.activePotionsMap.get(potioneffectIn.getPotionID()).combine(potioneffectIn);
            this.onChangedPotionEffect(this.activePotionsMap.get(potioneffectIn.getPotionID()), true);
         } else {
            this.activePotionsMap.put(potioneffectIn.getPotionID(), potioneffectIn);
            this.onNewPotionEffect(potioneffectIn);
         }
      }
   }

   protected void updatePotionEffects() {
      Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();

      while (iterator.hasNext()) {
         Integer integer = iterator.next();
         PotionEffect potionEffect = this.activePotionsMap.get(integer);
         if (!potionEffect.onUpdate(this)) {
            if (!this.worldObj.isRemote) {
               iterator.remove();
               this.onFinishedPotionEffect(potionEffect);
            }
         } else if (potionEffect.getDuration() % 600 == 0) {
            this.onChangedPotionEffect(potionEffect, false);
         }
      }

      if (this.potionsNeedUpdate) {
         if (!this.worldObj.isRemote) {
            this.updatePotionMetadata();
         }

         this.potionsNeedUpdate = false;
      }

      int watchableObjectInt = this.dataWatcher.getWatchableObjectInt(7);
      boolean flag = this.dataWatcher.getWatchableObjectByte(8) > 0;
      if (watchableObjectInt > 0) {
         boolean conditionMet2;
         if (!this.isInvisible()) {
            conditionMet2 = this.rand.nextBoolean();
         } else {
            conditionMet2 = this.rand.nextInt(15) == 0;
         }

         if (flag) {
            conditionMet2 &= this.rand.nextInt(5) == 0;
         }

         if (conditionMet2 && watchableObjectInt > 0) {
            double d = (watchableObjectInt >> 16 & 0xFF) / 255.0;
            double d2 = (watchableObjectInt >> 8 & 0xFF) / 255.0;
            double d3 = (watchableObjectInt >> 0 & 0xFF) / 255.0;
            this.worldObj
               .spawnParticle(
                  flag ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB,
                  this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                  this.posY + this.rand.nextDouble() * this.height,
                  this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                  d,
                  d2,
                  d3
               );
         }
      }
   }

   @Override
   public void performHurtAnimation() {
      this.hurtTime = this.maxHurtTime = 10;
      this.attackedAtYaw = 0.0F;
   }

   protected void updateArmSwingProgress() {
      int armSwingAnimationEnd = this.getArmSwingAnimationEnd();
      if (this.isSwingInProgress) {
         this.swingProgressInt++;
         if (this.swingProgressInt >= armSwingAnimationEnd) {
            this.swingProgressInt = 0;
            this.isSwingInProgress = false;
         }
      } else {
         this.swingProgressInt = 0;
      }

      this.swingProgress = (float)this.swingProgressInt / armSwingAnimationEnd;
   }

   public void setLastAttacker(Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         this.lastAttacker = (EntityLivingBase)entityIn;
      } else {
         this.lastAttacker = null;
      }

      this.lastAttackerTime = this.ticksExisted;
   }

   @Override
   public abstract void setCurrentItemOrArmor(int i, ItemStack itemStack);

   public float getAbsorptionAmount() {
      return this.absorptionAmount;
   }

   public int getTotalArmorValue() {
      int i = 0;

      for (ItemStack itemStack : this.getInventory()) {
         if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
            int damageReduceAmount2 = ((ItemArmor)itemStack.getItem()).damageReduceAmount;
            i += damageReduceAmount2;
         }
      }

      return i;
   }

   @Override
   protected void entityInit() {
      this.dataWatcher.addObject(7, 0);
      this.dataWatcher.addObject(8, (byte)0);
      this.dataWatcher.addObject(9, (byte)0);
      this.dataWatcher.addObject(6, 1.0F);
   }

   public void sendEndCombat() {
   }

   protected int decreaseAirSupply(int i) {
      int respiration = EnchantmentHelper.getRespiration(this);
      return respiration > 0 && this.rand.nextInt(respiration + 1) > 0 ? i : i - 1;
   }

   public EntityLivingBase getAttackingEntity() {
      return this._combatTracker.getAttacker() != null
         ? this._combatTracker.getAttacker()
         : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null));
   }

   public boolean canEntityBeSeen(Entity entityIn) {
      return this.worldObj
            .rayTraceBlocks(
               new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ), new Vec3(entityIn.posX, entityIn.posY + entityIn.getEyeHeight(), entityIn.posZ)
            )
         == null;
   }

   private int getArmSwingAnimationEnd() {
      if (ModuleManager.animations.isEnabled()) {
         return (int)(6.0F + ModuleManager.animations.getSlowdown().getCurrentValue());
      } else {
         return this.isPotionActive(Potion.digSpeed)
            ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1
            : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
      }
   }

   public boolean isPotionActive(Potion potionIn) {
      return this.activePotionsMap.containsKey(potionIn.id);
   }

   @Override
   public abstract ItemStack[] getInventory();

   public boolean isPlayerSleeping() {
      return false;
   }

   public final float getMaxHealth() {
      return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
   }

   public void setRevengeTarget(EntityLivingBase livingBase) {
      this.entityLivingToAttack = livingBase;
      this.revengeTimer = this.ticksExisted;
   }

   @Override
   protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (!this.isInWater()) {
         this.handleWaterMovement();
      }

      if (!this.worldObj.isRemote && this.fallDistance > 3.0F && onGroundIn) {
         IBlockState blockState = this.worldObj.getBlockState(pos);
         Block block = blockState.getBlock();
         float f = MathHelper.ceiling_float_int(this.fallDistance - 3.0F);
         if (block.getMaterial() != Material.air) {
            double d = Math.min(0.2F + f / 15.0F, 10.0F);
            if (d > 2.5) {
               d = 2.5;
            }

            int i = (int)(150.0 * d);
            ((WorldServer)this.worldObj)
               .spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, i, 0.0, 0.0, 0.0, 0.15F, Block.getStateId(blockState));
         }
      }

      super.updateFallState(y, onGroundIn, blockIn, pos);
   }

   public boolean isServerWorld() {
      return !this.worldObj.isRemote;
   }

   @Override
   public float getRotationYawHead() {
      return this.rotationYawHead;
   }

   public final int getArrowCountInEntity() {
      return this.dataWatcher.getWatchableObjectByte(9);
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   @Override
   protected void setBeenAttacked() {
      this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
   }

   protected void handleJumpLava() {
      this.motionY += 0.04F;
   }

   protected void updateAITick() {
      this.motionY += 0.04F;
   }

   protected boolean isPlayer() {
      return false;
   }

   protected void onChangedPotionEffect(PotionEffect id, boolean flag) {
      this.potionsNeedUpdate = true;
      if (flag && !this.worldObj.isRemote) {
         Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
         Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }
   }

   @Override
   public void mountEntity(Entity entityIn) {
      if (this.ridingEntity != null && entityIn == null) {
         if (!this.worldObj.isRemote) {
            this.dismountEntity(this.ridingEntity);
         }

         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         super.mountEntity(entityIn);
      }
   }

   @Override
   public void handleDouble(double d, double d2, double d3, float f, float f2, boolean flag) {
      this.newPosX = d;
      this.newPosY = d2;
      this.newPosZ = d3;
      this.newRotationYaw = f;
      this.newRotationPitch = f2;
      this.newPosRotationIncrements = 3;
   }

   public EntityLivingBase(World worldIn) {
      super(worldIn);
      this.applyEntityAttributes();
      this.setHealth(this.getMaxHealth());
      this.preventEntitySpawning = true;
      this.randomUnused1 = (float)((Math.random() + 1.0) * 0.01F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.randomUnused2 = (float)Math.random() * 12398.0F;
      this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
      this.rotationYawHead = this.rotationYaw;
      this.rotationPitchHead = this.rotationPitch;
      this.stepHeight = 0.6F;
   }

   public void setHealth(float health) {
      this.dataWatcher.updateObject(6, MathHelper.clamp_float(health, 0.0F, this.getMaxHealth()));
   }

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return false;
   }

   @Override
   public void updateRidden() {
      super.updateRidden();
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      this.onGroundSpeedFactor = 0.0F;
      this.fallDistance = 0.0F;
   }

   public void sendEnterCombat() {
   }

   public void swingItemNoPacket() {
      if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
         this.swingProgressInt = -1;
         this.isSwingInProgress = true;
         if (this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
         }
      }
   }

   public int getRevengeTimer() {
      return this.revengeTimer;
   }

   public Team getTeam() {
      return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
   }

   protected void resetPotionEffectMetadata() {
      this.dataWatcher.updateObject(8, (byte)0);
      this.dataWatcher.updateObject(7, 0);
   }

   public int getLastAttackerTime() {
      return this.lastAttackerTime;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 2) {
         this.limbSwingAmount = 1.5F;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.hurtTime = this.maxHurtTime = 10;
         this.attackedAtYaw = 0.0F;
         if (this.getHurtSound() != null) {
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.attackEntityFrom(DamageSource.generic, 0.0F);
      } else if (id == 3) {
         if (this.getDeathSound() != null) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.setHealth(0.0F);
         this.onDeath(DamageSource.generic);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   protected void updatePotionMetadata() {
      if (this.activePotionsMap.isEmpty()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(false);
      } else {
         int potionLiquidColor = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
         this.dataWatcher.updateObject(8, (byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0));
         this.dataWatcher.updateObject(7, potionLiquidColor);
         this.setInvisible(this.isPotionActive(Potion.invisibility.id));
      }
   }

   public void removePotionEffect(int potionId) {
      PotionEffect potionEffect = this.activePotionsMap.remove(potionId);
      if (potionEffect != null) {
         this.onFinishedPotionEffect(potionEffect);
      }
   }

   protected void collideWithNearbyEntities() {
      List<Entity> list = this.worldObj
         .getEntitiesInAABBexcluding(
            this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
               public boolean apply(Entity arg1) {
                  return arg1.canBePushed();
               }
            })
         );
      if (!list.isEmpty()) {
         for (int index = 0; index < list.size(); index++) {
            Entity entity = list.get(index);
            this.collideWithEntity(entity);
         }
      }
   }

   public float getSwingProgress(float partialTickTime) {
      float f = this.swingProgress - this.prevSwingProgress;
      if (f < 0.0F) {
         f++;
      }

      return this.prevSwingProgress + f * partialTickTime;
   }

   public boolean isOnTeam(Team teamIn) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(teamIn) : false;
   }

   protected void onNewPotionEffect(PotionEffect id) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }
   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F;
   }

   protected void onDeathUpdate() {
      this.deathTime++;
      if (this.deathTime == 20) {
         if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")
            )
          {
            int experiencePoints = this.getExperiencePoints(this.attackingPlayer);

            while (experiencePoints > 0) {
               int xpSplit = EntityXPOrb.getXPSplit(experiencePoints);
               experiencePoints -= xpSplit;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xpSplit));
            }
         }

         this.setDead();

         for (int nestedIndex = 0; nestedIndex < 20; nestedIndex++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.EXPLOSION_NORMAL,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  d,
                  d2,
                  d3
               );
         }
      }
   }

   public IAttributeInstance getEntityAttribute(IAttribute attribute) {
      return this.getAttributeMap().getAttributeInstance(attribute);
   }

   @Override
   public void onKillCommand() {
      this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
   }

   protected String getFallSoundString(int damageValue) {
      return damageValue > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
   }

   public final void setArrowCountInEntity(int count) {
      this.dataWatcher.updateObject(9, (byte)count);
   }

   protected void updateEntityActionState() {
   }

   public BaseAttributeMap getAttributeMap() {
      if (this.attributeMap == null) {
         this.attributeMap = new ServersideAttributeMap();
      }

      return this.attributeMap;
   }

   public void renderBrokenItemStack(ItemStack stack) {
      this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

      for (int i = 0; i < 5; i++) {
         Vec3 vec3 = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         vec3 = vec3.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         vec3 = vec3.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         double d = -this.rand.nextFloat() * 0.6 - 0.3;
         Vec3 vec32 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, d, 0.6);
         vec32 = vec32.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         vec32 = vec32.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         vec32 = vec32.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.ITEM_CRACK,
               vec32.xCoord,
               vec32.yCoord,
               vec32.zCoord,
               vec3.xCoord,
               vec3.yCoord + 0.05,
               vec3.zCoord,
               Item.getIdFromItem(stack.getItem())
            );
      }
   }

   @Override
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      IAttributeInstance attributeInstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (attributeInstance.getModifier(sprintingSpeedBoostModifierUUID) != null) {
         attributeInstance.removeModifier(sprintingSpeedBoostModifier);
      }

      if (sprinting) {
         attributeInstance.applyModifier(sprintingSpeedBoostModifier);
      }
   }

   public abstract ItemStack getEquipmentInSlot(int i);

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEFINED;
   }

   public EntityLivingBase getAITarget() {
      return this.entityLivingToAttack;
   }

   public Collection<PotionEffect> getActivePotionEffects() {
      return this.activePotionsMap.values();
   }

   protected void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
         damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
         float damageAmount2 = damageAmount;
         damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAmount2 - damageAmount));
         if (damageAmount != 0.0F) {
            float health2 = this.getHealth();
            this.setHealth(health2 - damageAmount);
            this.getCombatTracker().trackDamage(damageSrc, health2, damageAmount);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
         }
      }
   }

   @Override
   public void setRenderYawOffset(float f) {
      this.renderYawOffset = f;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setFloat("HealF", this.getHealth());
      tagCompound.setShort("Health", (short)Math.ceil(this.getHealth()));
      tagCompound.setShort("HurtTime", (short)this.hurtTime);
      tagCompound.setInteger("HurtByTimestamp", this.revengeTimer);
      tagCompound.setShort("DeathTime", (short)this.deathTime);
      tagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

      for (ItemStack itemStack : this.getInventory()) {
         if (itemStack != null) {
            this.attributeMap.removeAttributeModifiers(itemStack.getAttributeModifiers());
         }
      }

      tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

      for (ItemStack itemStack2 : this.getInventory()) {
         if (itemStack2 != null) {
            this.attributeMap.applyAttributeModifiers(itemStack2.getAttributeModifiers());
         }
      }

      if (!this.activePotionsMap.isEmpty()) {
         NBTTagList nbtTagList = new NBTTagList();

         for (PotionEffect potionEffect2 : this.activePotionsMap.values()) {
            nbtTagList.appendTag(potionEffect2.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         tagCompound.setTag("ActiveEffects", nbtTagList);
      }
   }

   protected boolean canDropLoot() {
      return !this.isChild();
   }

   public void onDeath(DamageSource cause) {
      Entity entity = cause.getEntity();
      EntityLivingBase entityLivingBase = this.getAttackingEntity();
      if (this.scoreValue >= 0 && entityLivingBase != null) {
         entityLivingBase.addToPlayerScore(this, this.scoreValue);
      }

      if (entity != null) {
         entity.onKillEntity(this);
      }

      this.dead = true;
      this.getCombatTracker().reset();
      if (!this.worldObj.isRemote) {
         int i = 0;
         if (entity instanceof EntityPlayer) {
            i = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
         }

         if (this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
            this.dropFewItems(this.recentlyHit > 0, i);
            this.dropEquipment(this.recentlyHit > 0, i);
            if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + i * 0.01F) {
               this.addRandomDrop();
            }
         }
      }

      this.worldObj.setEntityState(this, (byte)3);
   }

   public float getAIMoveSpeed() {
      return this.landMovementFactor;
   }

   protected void onFinishedPotionEffect(PotionEffect effect) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[effect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), effect.getAmplifier());
      }
   }

   public void setJumping(boolean jumping) {
      this.isJumping = jumping;
   }

   @Override
   protected void kill() {
      this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      super.fall(distance, damageMultiplier);
      PotionEffect potionEffect = this.getActivePotionEffect(Potion.jump);
      float f = potionEffect != null ? potionEffect.getAmplifier() + 1 : 0.0F;
      int i = MathHelper.ceiling_float_int((distance - 3.0F - f) * damageMultiplier);
      if (i > 0) {
         this.playSound(this.getFallSoundString(i), 1.0F, 1.0F);
         this.attackEntityFrom(DamageSource.fall, i);
         int i2 = MathHelper.floor_double(this.posX);
         int i3 = MathHelper.floor_double(this.posY - 0.2F);
         int i4 = MathHelper.floor_double(this.posZ);
         Block block = this.worldObj.getBlockState(new BlockPos(i2, i3, i4)).getBlock();
         if (block.getMaterial() != Material.air) {
            net.minecraft.block.Block.SoundType block$soundtype = block.stepSound;
            this.playSound(block$soundtype.getStepSound(), block$soundtype.getVolume() * 0.5F, block$soundtype.getFrequency() * 0.75F);
         }
      }
   }

   protected float getSoundPitch() {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));
      if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
         SharedMonsterAttributes.setAttributeModifiers(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
      }

      if (tagCompund.hasKey("ActiveEffects", 9)) {
         NBTTagList tagList = tagCompund.getTagList("ActiveEffects", 10);

         for (int i = 0; i < tagList.tagCount(); i++) {
            PotionEffect customPotionEffectFromNBT = PotionEffect.readCustomPotionEffectFromNBT(tagList.getCompoundTagAt(i));
            if (customPotionEffectFromNBT != null) {
               this.activePotionsMap.put(customPotionEffectFromNBT.getPotionID(), customPotionEffectFromNBT);
            }
         }
      }

      if (tagCompund.hasKey("HealF", 99)) {
         this.setHealth(tagCompund.getFloat("HealF"));
      } else {
         NBTBase tag = tagCompund.getTag("Health");
         if (tag == null) {
            this.setHealth(this.getMaxHealth());
         } else if (tag.getId() == 5) {
            this.setHealth(((NBTTagFloat)tag).getFloat());
         } else if (tag.getId() == 2) {
            this.setHealth(((NBTTagShort)tag).getShort());
         }
      }

      this.hurtTime = tagCompund.getShort("HurtTime");
      this.deathTime = tagCompund.getShort("DeathTime");
      this.revengeTimer = tagCompund.getInteger("HurtByTimestamp");
   }

   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
   }

   public void clearActivePotions() {
      Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();

      while (iterator.hasNext()) {
         Integer integer = iterator.next();
         PotionEffect potionEffect = this.activePotionsMap.get(integer);
         if (!this.worldObj.isRemote) {
            iterator.remove();
            this.onFinishedPotionEffect(potionEffect);
         }
      }
   }

   public PotionEffect getActivePotionEffect(Potion potionIn) {
      return this.activePotionsMap.get(potionIn.id);
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         int arrowCountInEntity = this.getArrowCountInEntity();
         if (arrowCountInEntity > 0) {
            if (this.arrowHitTimer <= 0) {
               this.arrowHitTimer = 20 * (30 - arrowCountInEntity);
            }

            this.arrowHitTimer--;
            if (this.arrowHitTimer <= 0) {
               this.setArrowCountInEntity(arrowCountInEntity - 1);
            }
         }

         for (int i = 0; i < 5; i++) {
            ItemStack itemStack = this.previousEquipment[i];
            ItemStack equipmentInSlot = this.getEquipmentInSlot(i);
            if (!ItemStack.areItemStacksEqual(equipmentInSlot, itemStack)) {
               ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), i, equipmentInSlot));
               if (itemStack != null) {
                  this.attributeMap.removeAttributeModifiers(itemStack.getAttributeModifiers());
               }

               if (equipmentInSlot != null) {
                  this.attributeMap.applyAttributeModifiers(equipmentInSlot.getAttributeModifiers());
               }

               this.previousEquipment[i] = equipmentInSlot == null ? null : equipmentInSlot.copy();
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.getCombatTracker().reset();
         }
      }

      this.onLivingUpdate();
      double d = this.posX - this.prevPosX;
      double d2 = this.posZ - this.prevPosZ;
      float f = (float)(d * d + d2 * d2);
      float renderYawOffset2 = this.renderYawOffset;
      float f2 = 0.0F;
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      float f3 = 0.0F;
      if (f > 0.0025000002F) {
         f3 = 1.0F;
         f2 = (float)Math.sqrt(f) * 3.0F;
         renderYawOffset2 = (float)MathHelper.atan2(d2, d) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (this.swingProgress > 0.0F) {
         renderYawOffset2 = this.rotationYaw;
      }

      if (!this.onGround) {
         f3 = 0.0F;
      }

      this.onGroundSpeedFactor = this.onGroundSpeedFactor + (f3 - this.onGroundSpeedFactor) * 0.3F;
      this.worldObj.theProfiler.startSection("headTurn");
      f2 = this.updateDistance(renderYawOffset2, f2);
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("rangeChecks");

      while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
         this.prevRenderYawOffset -= 360.0F;
      }

      while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
         this.prevRenderYawOffset += 360.0F;
      }

      while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
         this.prevRotationPitch -= 360.0F;
      }

      while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while (this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
         this.prevRotationYawHead -= 360.0F;
      }

      while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
         this.prevRotationYawHead += 360.0F;
      }

      this.worldObj.theProfiler.endSection();
      this.movedDistance += f2;
   }

   protected void jump() {
      if (this == Minecraft.getMinecraft().thePlayer) {
         JumpEvent jumpEvent = new JumpEvent();
         Novoline.getNovoline().getBus().post(jumpEvent);
         if (jumpEvent.isCancelled()) {
            return;
         }
      }

      this.motionY = this.getJumpUpwardsMotion();
      if (this.isPotionActive(Potion.jump)) {
         this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
      }

      if (this.isSprinting()) {
         float f = this.rotationYaw * (float) (Math.PI / 180.0);
         this.motionX = this.motionX - MathHelper.sin(f) * 0.2F;
         this.motionZ = this.motionZ + MathHelper.cos(f) * 0.2F;
      }

      this.isAirBorne = true;
   }

   protected void damageArmor(float f) {
   }

   public boolean isEntityUndead() {
      return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   protected float updateDistance(float f, float f2) {
      float f3 = MathHelper.wrapAngleTo180_float(f - this.renderYawOffset);
      this.renderYawOffset += f3 * 0.3F;
      float f4 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
      boolean flag = f4 < -90.0F || f4 >= 90.0F;
      if (f4 < -75.0F) {
         f4 = -75.0F;
      }

      if (f4 >= 75.0F) {
         f4 = 75.0F;
      }

      this.renderYawOffset = this.rotationYaw - f4;
      if (f4 * f4 > 2500.0F) {
         this.renderYawOffset += f4 * 0.2F;
      }

      if (flag) {
         f2 *= -1.0F;
      }

      return f2;
   }

   protected float applyPotionDamageCalculations(DamageSource source, float damage) {
      if (source.isDamageAbsolute()) {
         return damage;
      }

      if (this.isPotionActive(Potion.resistance) && source != DamageSource.outOfWorld) {
         int i = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
         int i2 = 25 - i;
         float f = damage * i2;
         damage = f / 25.0F;
      }

      if (damage <= 0.0F) {
         return 0.0F;
      }

      int enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), source);
      if (enchantmentModifierDamage > 20) {
         enchantmentModifierDamage = 20;
      }

      if (enchantmentModifierDamage > 0 && enchantmentModifierDamage <= 20) {
         int i3 = 25 - enchantmentModifierDamage;
         float f2 = damage * i3;
         damage = f2 / 25.0F;
      }

      return damage;
   }

   public void onLivingUpdate() {
      if (this.jumpTicks > 0) {
         this.jumpTicks--;
      }

      if (this.newPosRotationIncrements > 0) {
         double d = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
         double d2 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
         double d3 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
         double d4 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
         this.rotationYaw = (float)(this.rotationYaw + d4 / this.newPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
         this.newPosRotationIncrements--;
         this.setPosition(d, d2, d3);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else if (!this.isServerWorld()) {
         this.motionX *= 0.98;
         this.motionY *= 0.98;
         this.motionZ *= 0.98;
      }

      if (Math.abs(this.motionX) < 0.005) {
         this.motionX = 0.0;
      }

      if (Math.abs(this.motionY) < 0.005) {
         this.motionY = 0.0;
      }

      if (Math.abs(this.motionZ) < 0.005) {
         this.motionZ = 0.0;
      }

      this.worldObj.theProfiler.startSection("ai");
      if (this.isMovementBlocked()) {
         this.isJumping = false;
         this.moveStrafing = 0.0F;
         this.moveForward = 0.0F;
         this.randomYawVelocity = 0.0F;
      } else if (this.isServerWorld()) {
         this.worldObj.theProfiler.startSection("newAi");
         this.updateEntityActionState();
         this.worldObj.theProfiler.endSection();
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("jump");
      if (this.isJumping) {
         if (this.isInWater()) {
            this.updateAITick();
         } else if (this.isInLava()) {
            this.handleJumpLava();
         } else if (this.onGround && this.jumpTicks == 0) {
            this.jump();
            this.jumpTicks = 10;
         }
      } else {
         this.jumpTicks = 0;
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("travel");
      this.moveStrafing *= 0.98F;
      this.moveForward *= 0.98F;
      this.randomYawVelocity *= 0.9F;
      this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("push");
      if (!this.worldObj.isRemote) {
         this.collideWithNearbyEntities();
      }

      this.worldObj.theProfiler.endSection();
   }

   @Override
   public boolean canBePushed() {
      return !this.isDead;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.setLastAttacker(entityIn);
      return false;
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
         int potionID = potioneffectIn.getPotionID();
         if (potionID == Potion.regeneration.id || potionID == Potion.poison.id) {
            return false;
         }
      }

      return true;
   }

   public CombatTracker getCombatTracker() {
      return this._combatTracker;
   }

   public void setAbsorptionAmount(float amount) {
      if (amount < 0.0F) {
         amount = 0.0F;
      }

      this.absorptionAmount = amount;
   }

   public void heal(float healAmount) {
      float health2 = this.getHealth();
      if (health2 > 0.0F) {
         this.setHealth(health2 + healAmount);
      }
   }

   public final float getHealth() {
      return this.dataWatcher.getWatchableObjectFloat(6);
   }

   protected float getJumpUpwardsMotion() {
      return 0.42F;
   }

   public abstract ItemStack getHeldItem();

   public boolean isOnLadder() {
      int i = MathHelper.floor_double(this.posX);
      int i2 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
      int i3 = MathHelper.floor_double(this.posZ);
      Block block = this.worldObj.getBlockState(new BlockPos(i, i2, i3)).getBlock();
      return (block == Blocks.ladder || block == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
   }

   @Override
   public void setRotationYawHead(float f) {
      this.rotationYawHead = f;
   }

   @Override
   public Vec3 getLookVec() {
      return this.getLook(1.0F);
   }

   @Override
   public void onEntityUpdate() {
      this.prevSwingProgress = this.swingProgress;
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("livingEntityBaseTick");
      boolean conditionMet = this instanceof EntityPlayer;
      if (this.isEntityAlive()) {
         if (this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.inWall, 1.0F);
         } else if (conditionMet && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox())) {
            double d = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();
            if (d < 0.0) {
               this.attackEntityFrom(DamageSource.inWall, Math.max(1, MathHelper.floor_double(-d * this.worldObj.getWorldBorder().getDamageAmount())));
            }
         }
      }

      if (this.isImmuneToFire() || this.worldObj.isRemote) {
         this.extinguish();
      }

      boolean flag = conditionMet && ((EntityPlayer)this).capabilities.disableDamage;
      if (this.isEntityAlive()) {
         if (this.isInsideOfMaterial(Material.water)) {
            if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !flag) {
               this.setAir(this.decreaseAirSupply(this.getAir()));
               if (this.getAir() == -20) {
                  this.setAir(0);

                  for (int index = 0; index < 8; index++) {
                     float f = this.rand.nextFloat() - this.rand.nextFloat();
                     float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                     float f3 = this.rand.nextFloat() - this.rand.nextFloat();
                     this.worldObj
                        .spawnParticle(
                           EnumParticleTypes.WATER_BUBBLE,
                           this.posX + f,
                           this.posY + f2,
                           this.posZ + f3,
                           this.motionX,
                           this.motionY,
                           this.motionZ
                        );
                  }

                  this.attackEntityFrom(DamageSource.drown, 2.0F);
               }
            }

            if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
               this.mountEntity((Entity)null);
            }
         } else {
            this.setAir(300);
         }
      }

      if (this.isEntityAlive() && this.isWet()) {
         this.extinguish();
      }

      this.prevCameraPitch = this.cameraPitch;
      if (this.hurtTime > 0) {
         this.hurtTime--;
      }

      if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
         this.hurtResistantTime--;
      }

      if (this.getHealth() <= 0.0F) {
         this.onDeathUpdate();
      }

      if (this.recentlyHit > 0) {
         this.recentlyHit--;
      } else {
         this.attackingPlayer = null;
      }

      if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
         this.lastAttacker = null;
      }

      if (this.entityLivingToAttack != null) {
         if (!this.entityLivingToAttack.isEntityAlive()) {
            this.setRevengeTarget((EntityLivingBase)null);
         } else if (this.ticksExisted - this.revengeTimer > 100) {
            this.setRevengeTarget((EntityLivingBase)null);
         }
      }

      this.updatePotionEffects();
      this.prevMovedDistance = this.movedDistance;
      this.prevRenderYawOffset = this.renderYawOffset;
      this.prevRotationYawHead = this.rotationYawHead;
      this.prevRotationPitchHead = this.rotationPitchHead;
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      this.worldObj.theProfiler.endSection();
   }

   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.isServerWorld()) {
         if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
            if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
               float f = 0.91F;
               if (this.onGround) {
                  f = this.worldObj
                        .getBlockState(
                           new BlockPos(
                              MathHelper.floor_double(this.posX),
                              MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                              MathHelper.floor_double(this.posZ)
                           )
                        )
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               float f2 = 0.16277136F / (f * f * f);
               float f3;
               if (this.onGround) {
                  f3 = this.getAIMoveSpeed() * f2;
               } else {
                  f3 = this.jumpMovementFactor;
               }

               this.moveFlying(strafe, forward, f3);
               f = 0.91F;
               if (this.onGround) {
                  f = this.worldObj
                        .getBlockState(
                           new BlockPos(
                              MathHelper.floor_double(this.posX),
                              MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                              MathHelper.floor_double(this.posZ)
                           )
                        )
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               if (this.isOnLadder()) {
                  float f4 = 0.15F;
                  this.motionX = MathHelper.clamp_double(this.motionX, -f4, f4);
                  this.motionZ = MathHelper.clamp_double(this.motionZ, -f4, f4);
                  this.fallDistance = 0.0F;
                  if (this.motionY < -0.15) {
                     this.motionY = -0.15;
                  }

                  boolean flag = this.isSneaking() && this instanceof EntityPlayer;
                  if (flag && this.motionY < 0.0) {
                     this.motionY = 0.0;
                  }
               }

               this.moveEntity(this.motionX, this.motionY, this.motionZ);
               if (this.isCollidedHorizontally && this.isOnLadder()) {
                  this.motionY = 0.2;
               }

               if (this.worldObj.isRemote
                  && (
                     !this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ))
                        || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded()
                  )) {
                  if (this.posY > 0.0) {
                     this.motionY = -0.1;
                  } else {
                     this.motionY = 0.0;
                  }
               } else {
                  this.motionY -= 0.08;
               }

               this.motionY *= 0.98F;
               this.motionX *= f;
               this.motionZ *= f;
            } else {
               double posY2 = this.posY;
               this.moveFlying(strafe, forward, 0.02F);
               this.moveEntity(this.motionX, this.motionY, this.motionZ);
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
               this.motionY -= 0.02;
               if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + posY2, this.motionZ)) {
                  this.motionY = 0.3F;
               }
            }
         } else {
            double posY3 = this.posY;
            float f5 = 0.8F;
            float f6 = 0.02F;
            float depthStriderModifier = EnchantmentHelper.getDepthStriderModifier(this);
            if (depthStriderModifier > 3.0F) {
               depthStriderModifier = 3.0F;
            }

            if (!this.onGround) {
               depthStriderModifier *= 0.5F;
            }

            if (depthStriderModifier > 0.0F) {
               f5 += (0.54600006F - f5) * depthStriderModifier / 3.0F;
               f6 += (this.getAIMoveSpeed() * 1.0F - f6) * depthStriderModifier / 3.0F;
            }

            this.moveFlying(strafe, forward, f6);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= f5;
            this.motionY *= 0.8F;
            this.motionZ *= f5;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + posY3, this.motionZ)) {
               this.motionY = 0.3F;
            }
         }
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d = this.posX - this.prevPosX;
      double d2 = this.posZ - this.prevPosZ;
      float f7 = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
      if (f7 > 1.0F) {
         f7 = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (f7 - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
   }

   public void removePotionEffectClient(int potionId) {
      this.activePotionsMap.remove(potionId);
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 0;
   }

   protected void collideWithEntity(Entity entityIn) {
      entityIn.applyEntityCollision(this);
   }

   public void dismountEntity(Entity entityIn) {
      double posX2 = entityIn.posX;
      double d = entityIn.getEntityBoundingBox().minY + entityIn.height;
      double posZ2 = entityIn.posZ;

      for (int i = -1; i <= 1; i++) {
         for (int i2 = -1; i2 < 1; i2++) {
            if (i != 0 || i2 != 0) {
               int i3 = (int)(this.posX + i);
               int i4 = (int)(this.posZ + i2);
               AxisAlignedBB axisAlignedBB = this.getEntityBoundingBox().offset(i, 1.0, i2);
               if (this.worldObj.getCollisionBoxes(axisAlignedBB).isEmpty()) {
                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(i3, (int)this.posY, i4))) {
                     this.setPositionAndUpdate(this.posX + i, this.posY + 1.0, this.posZ + i2);
                     return;
                  }

                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(i3, (int)this.posY - 1, i4))
                     || this.worldObj.getBlockState(new BlockPos(i3, (int)this.posY - 1, i4)).getBlock().getMaterial() == Material.water) {
                     posX2 = this.posX + i;
                     d = this.posY + 1.0;
                     posZ2 = this.posZ + i2;
                  }
               }
            }
         }
      }

      this.setPositionAndUpdate(posX2, d, posZ2);
   }

   protected void markPotionsDirty() {
      this.potionsNeedUpdate = true;
   }

   protected String getHurtSound() {
      return "game.neutral.hurt";
   }

   public abstract ItemStack getCurrentArmor(int i);

   @Override
   public Vec3 getLook(float partialTicks) {
      if (partialTicks == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
      }

      float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
      float f2 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
      return this.getVectorForRotation(f, f2);
   }

   protected void applyEntityAttributes() {
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
   }

   @Override
   public boolean isEntityAlive() {
      return !this.isDead && this.getHealth() > 0.0F;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (this.worldObj.isRemote) {
         return false;
      }

      this.entityAge = 0;
      if (this.getHealth() <= 0.0F) {
         return false;
      }

      if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
         return false;
      }

      if ((source == DamageSource.anvil || source == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
         this.getEquipmentInSlot(4).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
         amount *= 0.75F;
      }

      this.limbSwingAmount = 1.5F;
      boolean conditionMet = true;
      if (this.hurtResistantTime > this.maxHurtResistantTime / 2.0F) {
         if (amount <= this.lastDamage) {
            return false;
         }

         this.damageEntity(source, amount - this.lastDamage);
         this.lastDamage = amount;
         conditionMet = false;
      } else {
         this.lastDamage = amount;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.damageEntity(source, amount);
         this.hurtTime = this.maxHurtTime = 10;
      }

      this.attackedAtYaw = 0.0F;
      Entity entity = source.getEntity();
      if (entity != null) {
         if (entity instanceof EntityLivingBase) {
            this.setRevengeTarget((EntityLivingBase)entity);
         }

         if (entity instanceof EntityPlayer) {
            this.recentlyHit = 100;
            this.attackingPlayer = (EntityPlayer)entity;
         } else if (entity instanceof EntityWolf entityWolf && entityWolf.isTamed()) {
            this.recentlyHit = 100;
            this.attackingPlayer = null;
         }
      }

      if (conditionMet) {
         this.worldObj.setEntityState(this, (byte)2);
         if (source != DamageSource.drown) {
            this.setBeenAttacked();
         }

         if (entity != null) {
            double xCoordinate = entity.posX - this.posX;

            double d;
            for (d = entity.posZ - this.posZ;
               xCoordinate * xCoordinate + d * d < 1.0E-4;
               d = (Math.random() - Math.random()) * 0.01
            ) {
               xCoordinate = (Math.random() - Math.random()) * 0.01;
            }

            this.attackedAtYaw = (float)(MathHelper.atan2(d, xCoordinate) * 180.0 / Math.PI - this.rotationYaw);
            this.knockBack(entity, amount, xCoordinate, d);
         } else {
            this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
         }
      }

      if (this.getHealth() <= 0.0F) {
         String text = this.getDeathSound();
         if (conditionMet && text != null) {
            this.playSound(text, this.getSoundVolume(), this.getSoundPitch());
         }

         this.onDeath(source);
      } else {
         String text2 = this.getHurtSound();
         if (conditionMet && text2 != null) {
            this.playSound(text2, this.getSoundVolume(), this.getSoundPitch());
         }
      }

      return true;
   }

   public boolean isChild() {
      return false;
   }

   public Random getRNG() {
      return this.rand;
   }

   public int getAge() {
      return this.entityAge;
   }

   protected float applyArmorCalculations(DamageSource source, float damage) {
      if (!source.isUnblockable()) {
         int i = 25 - this.getTotalArmorValue();
         float f = damage * i;
         this.damageArmor(damage);
         damage = f / 25.0F;
      }

      return damage;
   }

   public EntityLivingBase getLastAttacker() {
      return this.lastAttacker;
   }

   public boolean isPotionActive(int potionId) {
      return this.activePotionsMap.containsKey(potionId);
   }

   public void knockBack(Entity entityIn, float f, double d, double d2) {
      if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
         this.isAirBorne = true;
         float f2 = MathHelper.sqrt_double(d * d + d2 * d2);
         float f3 = 0.4F;
         this.motionX /= 2.0;
         this.motionY /= 2.0;
         this.motionZ /= 2.0;
         this.motionX -= d / f2 * f3;
         this.motionY += f3;
         this.motionZ -= d2 / f2 * f3;
         if (this.motionY > 0.4F) {
            this.motionY = 0.4F;
         }
      }
   }

   public void setAIMoveSpeed(float speedIn) {
      this.landMovementFactor = speedIn;
   }
}
