package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.Team;
import net.minecraft.src.Config;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.optifine.reflect.Reflector;

public abstract class EntityLiving extends EntityLivingBase {
   private NBTTagCompound leashNBTTag;
   private boolean isLeashed;
   private ItemStack[] equipment = new ItemStack[5];
   protected float[] equipmentDropChances = new float[5];
   private boolean persistenceRequired;
   private UUID teamUuid = null;
   public int livingSoundTime;
   protected int experienceValue;
   private String teamUuidString = null;
   private EntityLivingBase attackTarget;
   protected final EntityAITasks tasks;
   protected final EntityAITasks targetTasks;
   private EntityLookHelper lookHelper;
   private boolean canPickUpLoot;
   protected EntityMoveHelper moveHelper;
   protected EntityJumpHelper jumpHelper;
   private EntityBodyHelper bodyHelper;
   protected PathNavigate navigator;
   private EntitySenses senses;
   private Entity leashedToEntity;

   protected void updateLeashedState() {
      if (this.leashNBTTag != null) {
         this.recreateLeash();
      }

      if (this.isLeashed) {
         if (!this.isEntityAlive()) {
            this.clearLeashed(true, true);
         }

         if (this.leashedToEntity == null || this.leashedToEntity.isDead) {
            this.clearLeashed(true, true);
         }
      }
   }

   @Override
   public void setAIMoveSpeed(float speedIn) {
      super.setAIMoveSpeed(speedIn);
      this.setMoveForward(speedIn);
   }

   public void playLivingSound() {
      String text = this.getLivingSound();
      if (text != null) {
         this.playSound(text, this.getSoundVolume(), this.getSoundPitch());
      }
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(15, (byte)0);
   }

   private boolean canSkipUpdate() {
      if (this.isChild()) {
         return false;
      }

      if (this.hurtTime > 0) {
         return false;
      }

      if (this.ticksExisted < 20) {
         return false;
      }

      World world = this.getEntityWorld();
      if (world == null) {
         return false;
      }

      if (world.playerEntities.size() != 1) {
         return false;
      }

      Entity entity = world.playerEntities.get(0);
      double xCoordinate = Math.max(Math.abs(this.posX - entity.posX) - 16.0, 0.0);
      double zCoordinate = Math.max(Math.abs(this.posZ - entity.posZ) - 16.0, 0.0);
      double d = xCoordinate * xCoordinate + zCoordinate * zCoordinate;
      return !this.isInRangeToRenderDist(d);
   }

   public void enablePersistence() {
      this.persistenceRequired = true;
   }

   protected boolean canDespawn() {
      return true;
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
      return cls != EntityGhast.class;
   }

   public EntityMoveHelper getMoveHelper() {
      return this.moveHelper;
   }

   protected void updateAITasks() {
   }

   public boolean isAIDisabled() {
      return this.dataWatcher.getWatchableObjectByte(15) != 0;
   }

   public void setLeashedToEntity(Entity entityIn, boolean sendAttachNotification) {
      this.isLeashed = true;
      this.leashedToEntity = entityIn;
      if (!this.worldObj.isRemote && sendAttachNotification && this.worldObj instanceof WorldServer) {
         ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
      }
   }

   private void onUpdateMinimal() {
      this.entityAge++;
      if (this instanceof EntityMob) {
         float brightness = this.getBrightness(1.0F);
         if (brightness > 0.5F) {
            this.entityAge += 2;
         }
      }

      this.despawnEntity();
   }

   protected void despawnEntity() {
      Object object = Reflector.createObject(Reflector.reflectorField9);
      Object object2 = Reflector.createObject(Reflector.reflectorField7);
      if (this.persistenceRequired) {
         this.entityAge = 0;
      } else {
         Object object3;
         if ((this.entityAge & 31) != 31 || (object3 = Reflector.createObject2(Reflector.reflectorMethod37, this)) == object) {
            EntityPlayer closestPlayerToEntity = this.worldObj.getClosestPlayerToEntity(this, -1.0);
            if (closestPlayerToEntity != null) {
               double d = closestPlayerToEntity.posX - this.posX;
               double d2 = closestPlayerToEntity.posY - this.posY;
               double d3 = closestPlayerToEntity.posZ - this.posZ;
               double d4 = d * d + d2 * d2 + d3 * d3;
               if (this.canDespawn() && d4 > 16384.0) {
                  this.setDead();
               }

               if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d4 > 1024.0 && this.canDespawn()) {
                  this.setDead();
               } else if (d4 < 1024.0) {
                  this.entityAge = 0;
               }
            }
         } else if (object3 == object2) {
            this.entityAge = 0;
         } else {
            this.setDead();
         }
      }
   }

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      this.attackTarget = entitylivingbaseIn;
      Reflector.handleReflectorMethod(Reflector.reflectorMethod50, this, entitylivingbaseIn);
   }

   protected String getLivingSound() {
      return null;
   }

   @Override
   public Team getTeam() {
      UUID uUID = this.getUniqueID();
      if (this.teamUuid != uUID) {
         this.teamUuid = uUID;
         this.teamUuidString = uUID.toString();
      }

      return this.worldObj.getScoreboard().getPlayersTeam(this.teamUuidString);
   }

   public void setMoveForward(float f) {
      this.moveForward = f;
   }

   public EntityLookHelper getLookHelper() {
      return this.lookHelper;
   }

   @Override
   public final boolean interactFirst(EntityPlayer playerIn) {
      if (this.getLeashed() && this.getLeashedToEntity() == playerIn) {
         this.clearLeashed(true, !playerIn.capabilities.isCreativeMode);
         return true;
      }

      ItemStack itemStack = playerIn.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.lead && this.allowLeashing()) {
         if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed()) {
            this.setLeashedToEntity(playerIn, true);
            itemStack.stackSize--;
            return true;
         }

         if (((EntityTameable)this).isOwner(playerIn)) {
            this.setLeashedToEntity(playerIn, true);
            itemStack.stackSize--;
            return true;
         }
      }

      return this.interact(playerIn) ? true : super.interactFirst(playerIn);
   }

   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   @Override
   protected final void updateEntityActionState() {
      this.entityAge++;
      this.worldObj.theProfiler.startSection("checkDespawn");
      this.despawnEntity();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("sensing");
      this.senses.clearSensingCache();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("targetSelector");
      this.targetTasks.onUpdateTasks();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("goalSelector");
      this.tasks.onUpdateTasks();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("navigation");
      this.navigator.onUpdateNavigation();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("mob tick");
      this.updateAITasks();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("controls");
      this.worldObj.theProfiler.startSection("move");
      this.moveHelper.onUpdateMoveHelper();
      this.worldObj.theProfiler.endStartSection("look");
      this.lookHelper.onUpdateLook();
      this.worldObj.theProfiler.endStartSection("jump");
      this.jumpHelper.doJump();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.endSection();
   }

   @Override
   public void onUpdate() {
      if (Config.isSmoothWorld() && this.canSkipUpdate()) {
         this.onUpdateMinimal();
      } else {
         super.onUpdate();
         if (!this.worldObj.isRemote) {
            this.updateLeashedState();
         }
      }
   }

   public EntitySenses getEntitySenses() {
      return this.senses;
   }

   @Override
   public ItemStack getEquipmentInSlot(int slotIn) {
      return this.equipment[slotIn];
   }

   protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
      ItemStack itemStack = itemEntity.getEntityItem();
      int armorPosition = getArmorPosition(itemStack);
      if (armorPosition > -1) {
         boolean conditionMet = true;
         ItemStack itemStack2 = this.getEquipmentInSlot(armorPosition);
         if (itemStack2 != null) {
            if (armorPosition == 0) {
               if (itemStack.getItem() instanceof ItemSword && !(itemStack2.getItem() instanceof ItemSword)) {
                  conditionMet = true;
               } else if (itemStack.getItem() instanceof ItemSword && itemStack2.getItem() instanceof ItemSword) {
                  ItemSword itemSword = (ItemSword)itemStack.getItem();
                  ItemSword itemSword2 = (ItemSword)itemStack2.getItem();
                  if (itemSword.getDamageVsEntity() != itemSword2.getDamageVsEntity()) {
                     conditionMet = itemSword.getDamageVsEntity() > itemSword2.getDamageVsEntity();
                  } else {
                     conditionMet = itemStack.getMetadata() > itemStack2.getMetadata() || itemStack.hasTagCompound() && !itemStack2.hasTagCompound();
                  }
               } else if (itemStack.getItem() instanceof ItemBow && itemStack2.getItem() instanceof ItemBow) {
                  conditionMet = itemStack.hasTagCompound() && !itemStack2.hasTagCompound();
               } else {
                  conditionMet = false;
               }
            } else if (itemStack.getItem() instanceof ItemArmor && !(itemStack2.getItem() instanceof ItemArmor)) {
               conditionMet = true;
            } else if (itemStack.getItem() instanceof ItemArmor && itemStack2.getItem() instanceof ItemArmor) {
               ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
               ItemArmor itemArmor2 = (ItemArmor)itemStack2.getItem();
               if (itemArmor.damageReduceAmount != itemArmor2.damageReduceAmount) {
                  conditionMet = itemArmor.damageReduceAmount > itemArmor2.damageReduceAmount;
               } else {
                  conditionMet = itemStack.getMetadata() > itemStack2.getMetadata() || itemStack.hasTagCompound() && !itemStack2.hasTagCompound();
               }
            } else {
               conditionMet = false;
            }
         }

         if (conditionMet && this.canEquipItem(itemStack)) {
            if (itemStack2 != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[armorPosition]) {
               this.entityDropItem(itemStack2, 0.0F);
            }

            if (itemStack.getItem() == Items.diamond && itemEntity.getThrower() != null) {
               EntityPlayer entityPlayer = this.worldObj.getPlayerEntityByName(itemEntity.getThrower());
               if (entityPlayer != null) {
                  entityPlayer.triggerAchievement(AchievementList.diamondsToYou);
               }
            }

            this.setCurrentItemOrArmor(armorPosition, itemStack);
            this.equipmentDropChances[armorPosition] = 2.0F;
            this.persistenceRequired = true;
            this.onItemPickup(itemEntity, 1);
            itemEntity.setDead();
         }
      }
   }

   protected boolean interact(EntityPlayer player) {
      return false;
   }

   public void setEquipmentDropChance(int slotIn, float chance) {
      this.equipmentDropChances[slotIn] = chance;
   }

   protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
      float clampedAdditionalDifficulty = difficulty.getClampedAdditionalDifficulty();
      if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * clampedAdditionalDifficulty) {
         EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + clampedAdditionalDifficulty * this.rand.nextInt(18)));
      }

      for (int index = 0; index < 4; index++) {
         ItemStack itemStack = this.getCurrentArmor(index);
         if (itemStack != null && this.rand.nextFloat() < 0.5F * clampedAdditionalDifficulty) {
            EnchantmentHelper.addRandomEnchantment(this.rand, itemStack, (int)(5.0F + clampedAdditionalDifficulty * this.rand.nextInt(18)));
         }
      }
   }

   private void recreateLeash() {
      if (this.isLeashed && this.leashNBTTag != null) {
         if (this.leashNBTTag.hasKey("UUIDMost", 4) && this.leashNBTTag.hasKey("UUIDLeast", 4)) {
            UUID uuid = new UUID(this.leashNBTTag.getLong("UUIDMost"), this.leashNBTTag.getLong("UUIDLeast"));

            for (EntityLivingBase entityLivingBase : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(10.0, 10.0, 10.0))) {
               if (entityLivingBase.getUniqueID().equals(uuid)) {
                  this.leashedToEntity = entityLivingBase;
                  break;
               }
            }
         } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
            BlockPos blockPos2 = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
            EntityLeashKnot knotForPosition = EntityLeashKnot.getKnotForPosition(this.worldObj, blockPos2);
            if (knotForPosition == null) {
               knotForPosition = EntityLeashKnot.createKnot(this.worldObj, blockPos2);
            }

            this.leashedToEntity = knotForPosition;
         } else {
            this.clearLeashed(false, true);
         }
      }

      this.leashNBTTag = null;
   }

   public EntityJumpHelper getJumpHelper() {
      return this.jumpHelper;
   }

   protected boolean canEquipItem(ItemStack stack) {
      return true;
   }

   public void eatGrassBonus() {
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item item = this.getDropItem();
      if (item != null) {
         int i = this.rand.nextInt(3);
         if (lootingModifier > 0) {
            i += this.rand.nextInt(lootingModifier + 1);
         }

         for (int innerIndex = 0; innerIndex < i; innerIndex++) {
            this.getEntityItem(item);
         }
      }
   }

   public EntityLivingBase getAttackTarget() {
      return this.attackTarget;
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      this.getEntityAttribute(SharedMonsterAttributes.followRange)
         .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
      return livingdata;
   }

   public int getMaxSpawnedInChunk() {
      return 4;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      if (this.rand.nextFloat() < 0.15F * difficulty.getClampedAdditionalDifficulty()) {
         int i = this.rand.nextInt(2);
         float f = this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;
         if (this.rand.nextFloat() < 0.095F) {
            i++;
         }

         if (this.rand.nextFloat() < 0.095F) {
            i++;
         }

         if (this.rand.nextFloat() < 0.095F) {
            i++;
         }

         for (int innerIndex = 3; innerIndex >= 0; innerIndex--) {
            ItemStack itemStack = this.getCurrentArmor(innerIndex);
            if (innerIndex < 3 && this.rand.nextFloat() < f) {
               break;
            }

            if (itemStack == null) {
               Item item = getArmorItemForSlot(innerIndex + 1, i);
               if (item != null) {
                  this.setCurrentItemOrArmor(innerIndex + 1, new ItemStack(item));
               }
            }
         }
      }
   }

   public float getRenderSizeModifier() {
      return 1.0F;
   }

   @Override
   protected float updateDistance(float f, float f2) {
      this.bodyHelper.updateRenderAngles();
      return f2;
   }

   @Override
   public ItemStack getHeldItem() {
      return this.equipment[0];
   }

   @Override
   public ItemStack[] getInventory() {
      return this.equipment;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 20) {
         this.spawnExplosionParticle();
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public static Item getArmorItemForSlot(int armorSlot, int itemTier) {
      switch (armorSlot) {
         case 4:
            if (itemTier == 0) {
               return Items.leather_helmet;
            } else if (itemTier == 1) {
               return Items.golden_helmet;
            } else if (itemTier == 2) {
               return Items.chainmail_helmet;
            } else if (itemTier == 3) {
               return Items.iron_helmet;
            } else if (itemTier == 4) {
               return Items.diamond_helmet;
            }
         case 3:
            if (itemTier == 0) {
               return Items.leather_chestplate;
            } else if (itemTier == 1) {
               return Items.golden_chestplate;
            } else if (itemTier == 2) {
               return Items.chainmail_chestplate;
            } else if (itemTier == 3) {
               return Items.iron_chestplate;
            } else if (itemTier == 4) {
               return Items.diamond_chestplate;
            }
         case 2:
            if (itemTier == 0) {
               return Items.leather_leggings;
            } else if (itemTier == 1) {
               return Items.golden_leggings;
            } else if (itemTier == 2) {
               return Items.chainmail_leggings;
            } else if (itemTier == 3) {
               return Items.iron_leggings;
            } else if (itemTier == 4) {
               return Items.diamond_leggings;
            }
         case 1:
            if (itemTier == 0) {
               return Items.leather_boots;
            } else if (itemTier == 1) {
               return Items.golden_boots;
            } else if (itemTier == 2) {
               return Items.chainmail_boots;
            } else if (itemTier == 3) {
               return Items.iron_boots;
            } else if (itemTier == 4) {
               return Items.diamond_boots;
            }
         default:
            return null;
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
      tagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.equipment.length; index++) {
         NBTTagCompound innerIndex = new NBTTagCompound();
         if (this.equipment[index] != null) {
            this.equipment[index].writeToNBT(innerIndex);
         }

         nbtTagList.appendTag(innerIndex);
      }

      tagCompound.setTag("Equipment", nbtTagList);
      NBTTagList nbtTagList2 = new NBTTagList();

      for (int innerIndex = 0; innerIndex < this.equipmentDropChances.length; innerIndex++) {
         nbtTagList2.appendTag(new NBTTagFloat(this.equipmentDropChances[innerIndex]));
      }

      tagCompound.setTag("DropChances", nbtTagList2);
      tagCompound.setBoolean("Leashed", this.isLeashed);
      if (this.leashedToEntity != null) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         if (this.leashedToEntity instanceof EntityLivingBase) {
            nbtTagCompound.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
            nbtTagCompound.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
         } else if (this.leashedToEntity instanceof EntityHanging) {
            BlockPos blockPos = ((EntityHanging)this.leashedToEntity).getHangingPosition();
            nbtTagCompound.setInteger("X", blockPos.getX());
            nbtTagCompound.setInteger("Y", blockPos.getY());
            nbtTagCompound.setInteger("Z", blockPos.getZ());
         }

         tagCompound.setTag("Leash", nbtTagCompound);
      }

      if (this.isAIDisabled()) {
         tagCompound.setBoolean("NoAI", this.isAIDisabled());
      }
   }

   public static int getArmorPosition(ItemStack stack) {
      if (stack.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || stack.getItem() == Items.skull) {
         return 4;
      }

      if (stack.getItem() instanceof ItemArmor) {
         switch (((ItemArmor)stack.getItem()).armorType) {
            case 0:
               return 4;
            case 1:
               return 3;
            case 2:
               return 2;
            case 3:
               return 1;
         }
      }

      return 0;
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(tagCompund.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = tagCompund.getBoolean("PersistenceRequired");
      if (tagCompund.hasKey("Equipment", 9)) {
         NBTTagList nBTTagList = tagCompund.getTagList("Equipment", 10);

         for (int index = 0; index < this.equipment.length; index++) {
            this.equipment[index] = ItemStack.loadItemStackFromNBT(nBTTagList.getCompoundTagAt(index));
         }
      }

      if (tagCompund.hasKey("DropChances", 9)) {
         NBTTagList nBTTagList2 = tagCompund.getTagList("DropChances", 5);

         for (int innerIndex = 0; innerIndex < nBTTagList2.tagCount(); innerIndex++) {
            this.equipmentDropChances[innerIndex] = nBTTagList2.getFloatAt(innerIndex);
         }
      }

      this.isLeashed = tagCompund.getBoolean("Leashed");
      if (this.isLeashed && tagCompund.hasKey("Leash", 10)) {
         this.leashNBTTag = tagCompund.getCompoundTag("Leash");
      }

      this.setNoAI(tagCompund.getBoolean("NoAI"));
   }

   public boolean canBeSteered() {
      return false;
   }

   public void faceEntity(Entity entityIn, float f, float f2) {
      double d = entityIn.posX - this.posX;
      double d2 = entityIn.posZ - this.posZ;
      double d3;
      if (entityIn instanceof EntityLivingBase entityLivingBase) {
         d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (this.posY + this.getEyeHeight());
      } else {
         d3 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0 - (this.posY + this.getEyeHeight());
      }

      double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
      float f3 = (float)(MathHelper.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
      float f4 = (float)(-(MathHelper.atan2(d3, d4) * 180.0 / Math.PI));
      this.rotationPitch = this.updateRotation(this.rotationPitch, f4, f2);
      this.rotationYaw = this.updateRotation(this.rotationYaw, f3, f);
   }

   @Override
   public void onEntityUpdate() {
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("mobBaseTick");
      if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
         this.livingSoundTime = -this.getTalkInterval();
         this.playLivingSound();
      }

      this.worldObj.theProfiler.endSection();
   }

   public void setNoAI(boolean disable) {
      this.dataWatcher.updateObject(15, (byte)(disable ? 1 : 0));
   }

   protected PathNavigate getNewNavigator(World worldIn) {
      return new PathNavigateGround(this, worldIn);
   }

   public Entity getLeashedToEntity() {
      return this.leashedToEntity;
   }

   protected Item getDropItem() {
      return null;
   }

   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
   }

   @Override
   public int getMaxFallHeight() {
      if (this.getAttackTarget() == null) {
         return 3;
      }

      int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
      i -= (3 - this.worldObj.getDifficulty().getDifficultyId()) * 4;
      if (i < 0) {
         i = 0;
      }

      return i + 3;
   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void spawnExplosionParticle() {
      if (this.worldObj.isRemote) {
         for (int index = 0; index < 20; index++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            double d4 = 10.0;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.EXPLOSION_NORMAL,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width - d * d4,
                  this.posY + this.rand.nextFloat() * this.height - d2 * d4,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width - d3 * d4,
                  d,
                  d2,
                  d3
               );
         }
      } else {
         this.worldObj.setEntityState(this, (byte)20);
      }
   }

   @Override
   public boolean isServerWorld() {
      return super.isServerWorld() && !this.isAIDisabled();
   }

   public boolean getCanSpawnHere() {
      return true;
   }

   public EntityLiving(World worldIn) {
      super(worldIn);
      this.tasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
      this.targetTasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
      this.lookHelper = new EntityLookHelper(this);
      this.moveHelper = new EntityMoveHelper(this);
      this.jumpHelper = new EntityJumpHelper(this);
      this.bodyHelper = new EntityBodyHelper(this);
      this.navigator = this.getNewNavigator(worldIn);
      this.senses = new EntitySenses(this);

      for (int index = 0; index < this.equipmentDropChances.length; index++) {
         this.equipmentDropChances[index] = 0.085F;
      }
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.worldObj.theProfiler.startSection("looting");
      if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
         for (EntityItem entityItem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0, 0.0, 1.0))) {
            if (!entityItem.isDead && entityItem.getEntityItem() != null && !entityItem.cannotPickup()) {
               this.updateEquipmentIfNeeded(entityItem);
            }
         }
      }

      this.worldObj.theProfiler.endSection();
   }

   public int getTalkInterval() {
      return 80;
   }

   @Override
   protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
      for (int index = 0; index < this.getInventory().length; index++) {
         ItemStack itemStack = this.getEquipmentInSlot(index);
         boolean flag = this.equipmentDropChances[index] > 1.0F;
         if (itemStack != null && (wasRecentlyHit || flag) && this.rand.nextFloat() - lootingModifier * 0.01F < this.equipmentDropChances[index]) {
            if (!flag && itemStack.isItemStackDamageable()) {
               int i = Math.max(itemStack.getMaxDamage() - 25, 1);
               int i2 = itemStack.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(i) + 1);
               if (i2 > i) {
                  i2 = i;
               }

               if (i2 < 1) {
                  i2 = 1;
               }

               itemStack.setItemDamage(i2);
            }

            this.entityDropItem(itemStack, 0.0F);
         }
      }
   }

   @Override
   protected int getExperiencePoints(EntityPlayer player) {
      if (this.experienceValue > 0) {
         int experienceValue2 = this.experienceValue;
         ItemStack[] aitemstack = this.getInventory();

         for (int innerIndex = 0; innerIndex < aitemstack.length; innerIndex++) {
            if (aitemstack[innerIndex] != null && this.equipmentDropChances[innerIndex] <= 1.0F) {
               experienceValue2 += 1 + this.rand.nextInt(3);
            }
         }

         return experienceValue2;
      } else {
         return this.experienceValue;
      }
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.equipment[slotIn] = stack;
   }

   public boolean allowLeashing() {
      return !this.getLeashed() && !(this instanceof IMob);
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.equipment[slotIn + 1];
   }

   public PathNavigate getNavigator() {
      return this.navigator;
   }

   public boolean getLeashed() {
      return this.isLeashed;
   }

   public void setCanPickUpLoot(boolean canPickup) {
      this.canPickUpLoot = canPickup;
   }

   public void clearLeashed(boolean sendPacket, boolean dropLead) {
      if (this.isLeashed) {
         this.isLeashed = false;
         this.leashedToEntity = null;
         if (!this.worldObj.isRemote && dropLead) {
            this.getEntityItem(Items.lead);
         }

         if (!this.worldObj.isRemote && sendPacket && this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, (Entity)null));
         }
      }
   }

   private float updateRotation(float f, float f2, float f3) {
      float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
      if (f4 > f3) {
         f4 = f3;
      }

      if (f4 < -f3) {
         f4 = -f3;
      }

      return f + f4;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      int i;
      if (inventorySlot == 99) {
         i = 0;
      } else {
         i = inventorySlot - 100 + 1;
         if (i < 0 || i >= this.equipment.length) {
            return false;
         }
      }

      if (itemStackIn != null && getArmorPosition(itemStackIn) != i && (i != 4 || !(itemStackIn.getItem() instanceof ItemBlock))) {
         return false;
      }

      this.setCurrentItemOrArmor(i, itemStackIn);
      return true;
   }

   public int getVerticalFaceSpeed() {
      return 40;
   }

   public enum SpawnPlacementType {
      ON_GROUND,
      IN_AIR,
      IN_WATER;
   }
}
