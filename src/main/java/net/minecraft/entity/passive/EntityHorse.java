package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic {
   private String texturePrefix;
   private static final Predicate<Entity> horseBreedingSelector = new Predicate<Entity>() {
      public boolean apply(Entity arg1) {
         return arg1 instanceof EntityHorse && ((EntityHorse)arg1).isBreeding();
      }
   };
   private static final IAttribute horseJumpStrength = new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7, 0.0, 2.0)
      .setDescription("Jump Strength")
      .getBaseAttribute();
   private int gallopTime;
   private static final String[] horseArmorTextures = new String[]{
      null,
      "textures/entity/horse/armor/horse_armor_iron.png",
      "textures/entity/horse/armor/horse_armor_gold.png",
      "textures/entity/horse/armor/horse_armor_diamond.png"
   };
   public int headTossCounter;
   private int jumpRearingCounter;
   private static final String[] HORSE_ARMOR_TEXTURES_ABBR = new String[]{"", "meo", "goo", "dio"};
   public int rearStepCounter;
   private static final int[] armorValues = new int[]{0, 5, 7, 11};
   private boolean rearBlocked;
   private float headLean;
   private float prevMouthOpenness;
   private static final String[] horseTextures = new String[]{
      "textures/entity/horse/horse_white.png",
      "textures/entity/horse/horse_creamy.png",
      "textures/entity/horse/horse_chestnut.png",
      "textures/entity/horse/horse_brown.png",
      "textures/entity/horse/horse_black.png",
      "textures/entity/horse/horse_gray.png",
      "textures/entity/horse/horse_darkbrown.png"
   };
   private float mouthOpenness;
   private static final String[] HORSE_TEXTURES_ABBR = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] horseMarkingTextures = new String[]{
      null,
      "textures/entity/horse/horse_markings_white.png",
      "textures/entity/horse/horse_markings_whitefield.png",
      "textures/entity/horse/horse_markings_whitedots.png",
      "textures/entity/horse/horse_markings_blackdots.png"
   };
   protected boolean horseJumping;
   private AnimalChest horseChest;
   private int eatingHaystackCounter;
   private String[] horseTexturesArray = new String[3];
   private float rearingAmount;
   private boolean validTexturePaths = false;
   private boolean hasReproduced;
   private float prevRearingAmount;
   private int openMouthCounter;
   protected float jumpPower;
   private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   protected int temper;
   private float prevHeadLean;

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setEatingHaystack(tagCompund.getBoolean("EatingHaystack"));
      this.setBreeding(tagCompund.getBoolean("Bred"));
      this.setChested(tagCompund.getBoolean("ChestedHorse"));
      this.setHasReproduced(tagCompund.getBoolean("HasReproduced"));
      this.setHorseType(tagCompund.getInteger("Type"));
      this.setHorseVariant(tagCompund.getInteger("Variant"));
      this.setTemper(tagCompund.getInteger("Temper"));
      this.setHorseTamed(tagCompund.getBoolean("Tame"));
      String message;
      if (tagCompund.hasKey("OwnerUUID", 8)) {
         message = tagCompund.getString("OwnerUUID");
      } else {
         String text = tagCompund.getString("Owner");
         message = PreYggdrasilConverter.getStringUUIDFromName(text);
      }

      if (message.length() > 0) {
         this.setOwnerId(message);
      }

      IAttributeInstance attributeInstance = this.getAttributeMap().getAttributeInstanceByName("Speed");
      if (attributeInstance != null) {
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(attributeInstance.getBaseValue() * 0.25);
      }

      if (this.isChested()) {
         NBTTagList nBTTagList = tagCompund.getTagList("Items", 10);
         this.initHorseChest();

         for (int index = 0; index < nBTTagList.tagCount(); index++) {
            NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
            int i = nBTTagCompound.getByte("Slot") & 255;
            if (i >= 2 && i < this.horseChest.getSizeInventory()) {
               this.horseChest.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(nBTTagCompound));
            }
         }
      }

      if (tagCompund.hasKey("ArmorItem", 10)) {
         ItemStack itemStack = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("ArmorItem"));
         if (itemStack != null && isArmorItem(itemStack.getItem())) {
            this.horseChest.setInventorySlotContents(1, itemStack);
         }
      }

      if (tagCompund.hasKey("SaddleItem", 10)) {
         ItemStack itemStack2 = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("SaddleItem"));
         if (itemStack2 != null && itemStack2.getItem() == Items.saddle) {
            this.horseChest.setInventorySlotContents(0, itemStack2);
         }
      } else if (tagCompund.getBoolean("Saddle")) {
         this.horseChest.setInventorySlotContents(0, new ItemStack(Items.saddle));
      }

      this.updateHorseSlots();
   }

   @Override
   protected void updateFallDamage(float f) {
      if (f > 6.0F && this.isEatingHaystack()) {
         this.setEatingHaystack(false);
      }
   }

   public void setHorseArmorStack(ItemStack itemStackIn) {
      this.dataWatcher.updateObject(22, this.getHorseArmorIndex(itemStackIn));
      this.resetTexturePrefix();
   }

   protected EntityHorse getClosestHorse(Entity entityIn, double distance) {
      double d = Double.MAX_VALUE;
      Entity entity2 = null;

      for (Entity entity3 : this.worldObj
         .getEntitiesInAABBexcluding(entityIn, entityIn.getEntityBoundingBox().addCoord(distance, distance, distance), horseBreedingSelector)) {
         double distanceSq = entity3.getDistanceSq(entityIn.posX, entityIn.posY, entityIn.posZ);
         if (distanceSq < d) {
            entity2 = entity3;
            d = distanceSq;
         }
      }

      return (EntityHorse)entity2;
   }

   private void playHorseMouthSound() {
      this.openHorseMouth();
      if (!this.isSilent()) {
         this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }
   }

   public void setTemper(int temperIn) {
      this.temper = temperIn;
   }

   public void setHorseJumping(boolean jumping) {
      this.horseJumping = jumping;
   }

   private void updateHorseSlots() {
      if (!this.worldObj.isRemote) {
         this.setHorseSaddled(this.horseChest.getStackInSlot(0) != null);
         if (this.isActive()) {
            this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
         }
      }
   }

   public void setHorseVariant(int variant) {
      this.dataWatcher.updateObject(20, variant);
      this.resetTexturePrefix();
   }

   public boolean isSterile() {
      return this.isUndead() || this.getHorseType() == 2;
   }

   @Override
   public EntityAgeable createChild(EntityAgeable ageable) {
      EntityHorse entityHorse2 = (EntityHorse)ageable;
      EntityHorse entityHorse3 = new EntityHorse(this.worldObj);
      int horseType2 = this.getHorseType();
      int horseType3 = entityHorse2.getHorseType();
      int i = 0;
      if (horseType2 == horseType3) {
         i = horseType2;
      } else if (horseType2 == 0 && horseType3 == 1 || horseType2 == 1 && horseType3 == 0) {
         i = 2;
      }

      if (i == 0) {
         int i2 = this.rand.nextInt(9);
         int i3;
         if (i2 < 4) {
            i3 = this.getHorseVariant() & 0xFF;
         } else if (i2 < 8) {
            i3 = entityHorse2.getHorseVariant() & 0xFF;
         } else {
            i3 = this.rand.nextInt(7);
         }

         int i4 = this.rand.nextInt(5);
         if (i4 < 2) {
            i3 |= this.getHorseVariant() & 0xFF00;
         } else if (i4 < 4) {
            i3 |= entityHorse2.getHorseVariant() & 0xFF00;
         } else {
            i3 |= this.rand.nextInt(5) << 8 & 0xFF00;
         }

         entityHorse3.setHorseVariant(i3);
      }

      entityHorse3.setHorseType(i);
      double d = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + ageable.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + this.getModifiedMaxHealth();
      entityHorse3.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(d / 3.0);
      double d2 = this.getEntityAttribute(horseJumpStrength).getBaseValue()
         + ageable.getEntityAttribute(horseJumpStrength).getBaseValue()
         + this.getModifiedJumpStrength();
      entityHorse3.getEntityAttribute(horseJumpStrength).setBaseValue(d2 / 3.0);
      double d3 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + ageable.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + this.getModifiedMovementSpeed();
      entityHorse3.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(d3 / 3.0);
      return entityHorse3;
   }

   public boolean isTame() {
      return this.getHorseWatchableBoolean(2);
   }

   @Override
   public void setScaleForAge(boolean flag) {
      if (flag) {
         this.setScale(this.getHorseSize());
      } else {
         this.setScale(1.0F);
      }
   }

   public void setHorseType(int type) {
      this.dataWatcher.updateObject(19, (byte)type);
      this.resetTexturePrefix();
   }

   private double getModifiedJumpStrength() {
      return 0.4F + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("EatingHaystack", this.isEatingHaystack());
      tagCompound.setBoolean("ChestedHorse", this.isChested());
      tagCompound.setBoolean("HasReproduced", this.getHasReproduced());
      tagCompound.setBoolean("Bred", this.isBreeding());
      tagCompound.setInteger("Type", this.getHorseType());
      tagCompound.setInteger("Variant", this.getHorseVariant());
      tagCompound.setInteger("Temper", this.getTemper());
      tagCompound.setBoolean("Tame", this.isTame());
      tagCompound.setString("OwnerUUID", this.getOwnerId());
      if (this.isChested()) {
         NBTTagList nbtTagList = new NBTTagList();

         for (int index = 2; index < this.horseChest.getSizeInventory(); index++) {
            ItemStack itemStack = this.horseChest.getStackInSlot(index);
            if (itemStack != null) {
               NBTTagCompound nbtTagCompound = new NBTTagCompound();
               nbtTagCompound.setByte("Slot", (byte)index);
               itemStack.writeToNBT(nbtTagCompound);
               nbtTagList.appendTag(nbtTagCompound);
            }
         }

         tagCompound.setTag("Items", nbtTagList);
      }

      if (this.horseChest.getStackInSlot(1) != null) {
         tagCompound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
      }

      if (this.horseChest.getStackInSlot(0) != null) {
         tagCompound.setTag("SaddleItem", this.horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
      }
   }

   public int increaseTemper(int i) {
      int i2 = MathHelper.clamp_int(this.getTemper() + i, 0, this.getMaxTemper());
      this.setTemper(i2);
      return i2;
   }

   private int getHorseArmorIndex(ItemStack itemStackIn) {
      if (itemStackIn == null) {
         return 0;
      }

      Item item = itemStackIn.getItem();
      return item == Items.iron_horse_armor ? 1 : (item == Items.golden_horse_armor ? 2 : (item == Items.diamond_horse_armor ? 3 : 0));
   }

   public String getOwnerId() {
      return this.dataWatcher.getWatchableObjectString(21);
   }

   public void setJumpPower(int jumpPowerIn) {
      if (this.isHorseSaddled()) {
         if (jumpPowerIn < 0) {
            jumpPowerIn = 0;
         } else {
            this.rearBlocked = true;
            this.makeHorseRear();
         }

         if (jumpPowerIn >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * jumpPowerIn / 90.0F;
         }
      }
   }

   @Override
   protected boolean isMovementBlocked() {
      return this.riddenByEntity != null && this.isHorseSaddled() ? true : this.isEatingHaystack() || this.isRearing();
   }

   public boolean isEatingHaystack() {
      return this.getHorseWatchableBoolean(32);
   }

   public float getRearingAmount(float f) {
      return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * f;
   }

   private void initHorseChest() {
      AnimalChest animalChest = this.horseChest;
      this.horseChest = new AnimalChest("HorseChest", this.getChestSize());
      this.horseChest.setCustomName(this.getName());
      if (animalChest != null) {
         animalChest.removeInventoryChangeListener(this);
         int i = Math.min(animalChest.getSizeInventory(), this.horseChest.getSizeInventory());

         for (int innerIndex = 0; innerIndex < i; innerIndex++) {
            ItemStack itemStack = animalChest.getStackInSlot(innerIndex);
            if (itemStack != null) {
               this.horseChest.setInventorySlotContents(innerIndex, itemStack.copy());
            }
         }
      }

      this.horseChest.addInventoryChangeListener(this);
      this.updateHorseSlots();
   }

   private void setHorseTexturePaths() {
      this.texturePrefix = "horse/";
      this.horseTexturesArray[0] = null;
      this.horseTexturesArray[1] = null;
      this.horseTexturesArray[2] = null;
      int horseType2 = this.getHorseType();
      int horseVariant2 = this.getHorseVariant();
      if (horseType2 == 0) {
         int i = horseVariant2 & 0xFF;
         int i2 = (horseVariant2 & 0xFF00) >> 8;
         if (i >= horseTextures.length) {
            this.validTexturePaths = false;
            return;
         }

         this.horseTexturesArray[0] = horseTextures[i];
         this.texturePrefix = this.texturePrefix + HORSE_TEXTURES_ABBR[i];
         if (i2 >= horseMarkingTextures.length) {
            this.validTexturePaths = false;
            return;
         }

         this.horseTexturesArray[1] = horseMarkingTextures[i2];
         this.texturePrefix = this.texturePrefix + HORSE_MARKING_TEXTURES_ABBR[i2];
      } else {
         this.horseTexturesArray[0] = "";
         this.texturePrefix = this.texturePrefix + "_" + horseType2 + "_";
      }

      int horseArmorIndexSynced = this.getHorseArmorIndexSynced();
      if (horseArmorIndexSynced >= horseArmorTextures.length) {
         this.validTexturePaths = false;
      } else {
         this.horseTexturesArray[2] = horseArmorTextures[horseArmorIndexSynced];
         this.texturePrefix = this.texturePrefix + HORSE_ARMOR_TEXTURES_ABBR[horseArmorIndexSynced];
         this.validTexturePaths = true;
      }
   }

   public void setEatingHaystack(boolean flag) {
      this.setEating(flag);
   }

   @Override
   public void onInventoryChanged(InventoryBasic inventoryBasic) {
      int horseArmorIndexSynced = this.getHorseArmorIndexSynced();
      boolean isHorseSaddled = this.isHorseSaddled();
      this.updateHorseSlots();
      if (this.ticksExisted > 20) {
         if (horseArmorIndexSynced == 0 && horseArmorIndexSynced != this.getHorseArmorIndexSynced()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         } else if (horseArmorIndexSynced != this.getHorseArmorIndexSynced()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         }

         if (!isHorseSaddled && this.isHorseSaddled()) {
            this.playSound("mob.horse.leather", 0.5F, 1.0F);
         }
      }
   }

   public void setHorseSaddled(boolean saddled) {
      this.setHorseWatchableBoolean(4, saddled);
   }

   @Override
   public void setEating(boolean eating) {
      this.setHorseWatchableBoolean(32, eating);
   }

   @Override
   protected String getLivingSound() {
      this.openHorseMouth();
      if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
         this.makeHorseRear();
      }

      int horseType2 = this.getHorseType();
      return horseType2 == 3
         ? "mob.horse.zombie.idle"
         : (horseType2 == 4 ? "mob.horse.skeleton.idle" : (horseType2 != 1 && horseType2 != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
   }

   public int getHorseType() {
      return this.dataWatcher.getWatchableObjectByte(19);
   }

   public int getHorseArmorIndexSynced() {
      return this.dataWatcher.getWatchableObjectInt(22);
   }

   public double getHorseJumpStrength() {
      return this.getEntityAttribute(horseJumpStrength).getAttributeValue();
   }

   @Override
   protected float getSoundVolume() {
      return 0.8F;
   }

   public boolean isRearing() {
      return this.getHorseWatchableBoolean(64);
   }

   @Override
   protected String getDeathSound() {
      this.openHorseMouth();
      int horseType2 = this.getHorseType();
      return horseType2 == 3
         ? "mob.horse.zombie.death"
         : (horseType2 == 4 ? "mob.horse.skeleton.death" : (horseType2 != 1 && horseType2 != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
   }

   private void dropItemsInChest(Entity entityIn, AnimalChest animalChestIn) {
      if (animalChestIn != null && !this.worldObj.isRemote) {
         for (int index = 0; index < animalChestIn.getSizeInventory(); index++) {
            ItemStack itemStack = animalChestIn.getStackInSlot(index);
            if (itemStack != null) {
               this.entityDropItem(itemStack, 0.0F);
            }
         }
      }
   }

   @Override
   public int getTotalArmorValue() {
      return armorValues[this.getHorseArmorIndexSynced()];
   }

   public void setRearing(boolean rearing) {
      if (rearing) {
         this.setEatingHaystack(false);
      }

      this.setHorseWatchableBoolean(64, rearing);
   }

   private void setHorseWatchableBoolean(int i, boolean flag) {
      int watchableObjectInt = this.dataWatcher.getWatchableObjectInt(16);
      if (flag) {
         this.dataWatcher.updateObject(16, watchableObjectInt | i);
      } else {
         this.dataWatcher.updateObject(16, watchableObjectInt & ~i);
      }
   }

   @Override
   public void onLivingUpdate() {
      if (this.rand.nextInt(200) == 0) {
         this.playHorseStepSound();
      }

      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (!this.isEatingHaystack()
            && this.riddenByEntity == null
            && this.rand.nextInt(300) == 0
            && this.worldObj
                  .getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ)))
                  .getBlock()
               == Blocks.grass) {
            this.setEatingHaystack(true);
         }

         if (this.isEatingHaystack() && ++this.eatingHaystackCounter > 50) {
            this.eatingHaystackCounter = 0;
            this.setEatingHaystack(false);
         }

         if (this.isBreeding() && !this.isAdultHorse() && !this.isEatingHaystack()) {
            EntityHorse entityHorse = this.getClosestHorse(this, 16.0);
            if (entityHorse != null && this.getDistanceSqToEntity(entityHorse) > 4.0) {
               this.navigator.getPathToEntityLiving(entityHorse);
            }
         }
      }
   }

   public String getHorseTexture() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.texturePrefix;
   }

   public float getHorseSize() {
      return 0.5F;
   }

   public boolean canCarryChest() {
      int horseType2 = this.getHorseType();
      return horseType2 == 2 || horseType2 == 1;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      Entity entity = source.getEntity();
      return this.riddenByEntity != null && this.riddenByEntity.equals(entity) ? false : super.attackEntityFrom(source, amount);
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(horseJumpStrength);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(53.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.225F);
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      if (distance > 1.0F) {
         this.playSound("mob.horse.land", 0.4F, 1.0F);
      }

      int i = MathHelper.ceiling_float_int((distance * 0.5F - 3.0F) * damageMultiplier);
      if (i > 0) {
         this.attackEntityFrom(DamageSource.fall, i);
         if (this.riddenByEntity != null) {
            this.riddenByEntity.attackEntityFrom(DamageSource.fall, i);
         }

         Block block = this.worldObj.getBlockState(new BlockPos(this.posX, this.posY - 0.2 - this.prevRotationYaw, this.posZ)).getBlock();
         if (block.getMaterial() != Material.air && !this.isSilent()) {
            net.minecraft.block.Block.SoundType block$soundtype = block.stepSound;
            this.worldObj.playSoundAtEntity(this, block$soundtype.getStepSound(), block$soundtype.getVolume() * 0.5F, block$soundtype.getFrequency() * 0.75F);
         }
      }
   }

   private int getChestSize() {
      int horseType2 = this.getHorseType();
      return this.isChested() && (horseType2 == 1 || horseType2 == 2) ? 17 : 2;
   }

   private void resetTexturePrefix() {
      this.texturePrefix = null;
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase && this.isHorseSaddled()) {
         this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
         this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
         strafe = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
         forward = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (forward <= 0.0F) {
            forward *= 0.25F;
            this.gallopTime = 0;
         }

         if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.rearBlocked) {
            strafe = 0.0F;
            forward = 0.0F;
         }

         if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
            this.motionY = this.getHorseJumpStrength() * this.jumpPower;
            if (this.isPotionActive(Potion.jump)) {
               this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
            }

            this.setHorseJumping(true);
            this.isAirBorne = true;
            if (forward > 0.0F) {
               float f = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
               float f2 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
               this.motionX = this.motionX + -0.4F * f * this.jumpPower;
               this.motionZ = this.motionZ + 0.4F * f2 * this.jumpPower;
               this.playSound("mob.horse.jump", 0.4F, 1.0F);
            }

            this.jumpPower = 0.0F;
         }

         this.stepHeight = 1.0F;
         this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
         if (!this.worldObj.isRemote) {
            this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            super.moveEntityWithHeading(strafe, forward);
         }

         if (this.onGround) {
            this.jumpPower = 0.0F;
            this.setHorseJumping(false);
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double xCoordinate = this.posX - this.prevPosX;
         double zCoordinate = this.posZ - this.prevPosZ;
         float f3 = MathHelper.sqrt_double(xCoordinate * xCoordinate + zCoordinate * zCoordinate) * 4.0F;
         if (f3 > 1.0F) {
            f3 = 1.0F;
         }

         this.limbSwingAmount = this.limbSwingAmount + (f3 - this.limbSwingAmount) * 0.4F;
         this.limbSwing = this.limbSwing + this.limbSwingAmount;
      } else {
         this.stepHeight = 0.5F;
         this.jumpMovementFactor = 0.02F;
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   @Override
   public int getTalkInterval() {
      return 400;
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (!this.worldObj.isRemote) {
         this.dropChestItems();
      }
   }

   public boolean isHorseJumping() {
      return this.horseJumping;
   }

   public boolean setTamedBy(EntityPlayer player) {
      this.setOwnerId(player.getUniqueID().toString());
      this.setHorseTamed(true);
      return true;
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 6;
   }

   @Override
   protected void playStepSound(BlockPos pos, Block blockIn) {
      net.minecraft.block.Block.SoundType block$soundtype = blockIn.stepSound;
      if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
         block$soundtype = Blocks.snow_layer.stepSound;
      }

      if (!blockIn.getMaterial().isLiquid()) {
         int horseType2 = this.getHorseType();
         if (this.riddenByEntity != null && horseType2 != 1 && horseType2 != 2) {
            this.gallopTime++;
            if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
               this.playSound("mob.horse.gallop", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());
               if (horseType2 == 0 && this.rand.nextInt(10) == 0) {
                  this.playSound("mob.horse.breathe", block$soundtype.getVolume() * 0.6F, block$soundtype.getFrequency());
               }
            } else if (this.gallopTime <= 5) {
               this.playSound("mob.horse.wood", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());
            }
         } else if (block$soundtype == Block.soundTypeWood) {
            this.playSound("mob.horse.wood", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());
         } else {
            this.playSound("mob.horse.soft", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());
         }
      }
   }

   public boolean isChested() {
      return this.getHorseWatchableBoolean(8);
   }

   public void dropChests() {
      if (!this.worldObj.isRemote && this.isChested()) {
         this.getEntityItem(Item.getItemFromBlock(Blocks.chest));
         this.setChested(false);
      }
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.spawn_egg) {
         return super.interact(player);
      }

      if (!this.isTame() && this.isUndead()) {
         return false;
      }

      if (this.isTame() && this.isAdultHorse() && player.isSneaking()) {
         this.openGUI(player);
         return true;
      }

      if (this.isActive2() && this.riddenByEntity != null) {
         return super.interact(player);
      }

      if (itemStack != null) {
         boolean conditionMet = false;
         if (this.isActive()) {
            int i = -1;
            if (itemStack.getItem() == Items.iron_horse_armor) {
               i = 1;
            } else if (itemStack.getItem() == Items.golden_horse_armor) {
               i = 2;
            } else if (itemStack.getItem() == Items.diamond_horse_armor) {
               i = 3;
            }

            if (i >= 0) {
               if (!this.isTame()) {
                  this.makeHorseRearWithSound();
                  return true;
               }

               this.openGUI(player);
               return true;
            }
         }

         if (!this.isUndead()) {
            float f = 0.0F;
            int i2 = 0;
            int i3 = 0;
            if (itemStack.getItem() == Items.wheat) {
               f = 2.0F;
               i2 = 20;
               i3 = 3;
            } else if (itemStack.getItem() == Items.sugar) {
               f = 1.0F;
               i2 = 30;
               i3 = 3;
            } else if (Block.getBlockFromItem(itemStack.getItem()) == Blocks.hay_block) {
               f = 20.0F;
               i2 = 180;
            } else if (itemStack.getItem() == Items.apple) {
               f = 3.0F;
               i2 = 60;
               i3 = 3;
            } else if (itemStack.getItem() == Items.golden_carrot) {
               f = 4.0F;
               i2 = 60;
               i3 = 5;
               if (this.isTame() && this.getGrowingAge() == 0) {
                  conditionMet = true;
                  this.setInLove(player);
               }
            } else if (itemStack.getItem() == Items.golden_apple) {
               f = 10.0F;
               i2 = 240;
               i3 = 10;
               if (this.isTame() && this.getGrowingAge() == 0) {
                  conditionMet = true;
                  this.setInLove(player);
               }
            }

            if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
               this.heal(f);
               conditionMet = true;
            }

            if (!this.isAdultHorse() && i2 > 0) {
               this.addGrowth(i2);
               conditionMet = true;
            }

            if (i3 > 0 && (conditionMet || !this.isTame()) && i3 < this.getMaxTemper()) {
               conditionMet = true;
               this.increaseTemper(i3);
            }

            if (conditionMet) {
               this.playHorseMouthSound();
            }
         }

         if (!this.isTame() && !conditionMet) {
            if (itemStack != null && itemStack.interactWithEntity(player, this)) {
               return true;
            }

            this.makeHorseRearWithSound();
            return true;
         }

         if (!conditionMet && this.canCarryChest() && !this.isChested() && itemStack.getItem() == Item.getItemFromBlock(Blocks.chest)) {
            this.setChested(true);
            this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            conditionMet = true;
            this.initHorseChest();
         }

         if (!conditionMet && this.isActive2() && !this.isHorseSaddled() && itemStack.getItem() == Items.saddle) {
            this.openGUI(player);
            return true;
         }

         if (conditionMet) {
            if (!player.capabilities.isCreativeMode && --itemStack.stackSize == 0) {
               player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
            }

            return true;
         }
      }

      if (!this.isActive2() || this.riddenByEntity != null) {
         return super.interact(player);
      }

      if (itemStack != null && itemStack.interactWithEntity(player, this)) {
         return true;
      }

      this.mountTo(player);
      return true;
   }

   public int getHorseVariant() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   public void setHasReproduced(boolean hasReproducedIn) {
      this.hasReproduced = hasReproducedIn;
   }

   @Override
   public void updateRiderPosition() {
      super.updateRiderPosition();
      if (this.prevRearingAmount > 0.0F) {
         float f = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
         float f2 = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
         float f3 = 0.7F * this.prevRearingAmount;
         float f4 = 0.15F * this.prevRearingAmount;
         this.riddenByEntity
            .setPosition(
               this.posX + f3 * f,
               this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset() + f4,
               this.posZ - f3 * f2
            );
         if (this.riddenByEntity instanceof EntityLivingBase) {
            ((EntityLivingBase)this.riddenByEntity).renderYawOffset = this.renderYawOffset;
         }
      }
   }

   public void setBreeding(boolean breeding) {
      this.setHorseWatchableBoolean(16, breeding);
   }

   public boolean isAdultHorse() {
      return !this.isChild();
   }

   public boolean isUndead() {
      int horseType2 = this.getHorseType();
      return horseType2 == 3 || horseType2 == 4;
   }

   protected String getAngrySoundName() {
      this.openHorseMouth();
      this.makeHorseRear();
      int horseType2 = this.getHorseType();
      return horseType2 != 3 && horseType2 != 4 ? (horseType2 != 1 && horseType2 != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.spawnHorseParticles(true);
      } else if (id == 6) {
         this.spawnHorseParticles(false);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public boolean canWearArmor() {
      return this.getHorseType() == 0 || this.getHorseArmorIndexSynced() > 0;
   }

   public static boolean isArmorItem(Item item2) {
      return item2 == Items.iron_horse_armor || item2 == Items.golden_horse_armor || item2 == Items.diamond_horse_armor;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.worldObj.isRemote && this.dataWatcher.hasObjectChanged()) {
         this.dataWatcher.resetObjectChangedFlag();
         this.resetTexturePrefix();
      }

      if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
         this.openMouthCounter = 0;
         this.setHorseWatchableBoolean(128, false);
      }

      if (!this.worldObj.isRemote && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
         this.jumpRearingCounter = 0;
         this.setRearing(false);
      }

      if (this.headTossCounter > 0 && ++this.headTossCounter > 8) {
         this.headTossCounter = 0;
      }

      if (this.rearStepCounter > 0) {
         this.rearStepCounter++;
         if (this.rearStepCounter > 300) {
            this.rearStepCounter = 0;
         }
      }

      this.prevHeadLean = this.headLean;
      if (this.isEatingHaystack()) {
         this.headLean = this.headLean + ((1.0F - this.headLean) * 0.4F + 0.05F);
         if (this.headLean > 1.0F) {
            this.headLean = 1.0F;
         }
      } else {
         this.headLean = this.headLean + ((0.0F - this.headLean) * 0.4F - 0.05F);
         if (this.headLean < 0.0F) {
            this.headLean = 0.0F;
         }
      }

      this.prevRearingAmount = this.rearingAmount;
      if (this.isRearing()) {
         this.prevHeadLean = this.headLean = 0.0F;
         this.rearingAmount = this.rearingAmount + ((1.0F - this.rearingAmount) * 0.4F + 0.05F);
         if (this.rearingAmount > 1.0F) {
            this.rearingAmount = 1.0F;
         }
      } else {
         this.rearBlocked = false;
         this.rearingAmount = this.rearingAmount + ((0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F);
         if (this.rearingAmount < 0.0F) {
            this.rearingAmount = 0.0F;
         }
      }

      this.prevMouthOpenness = this.mouthOpenness;
      if (this.getHorseWatchableBoolean(128)) {
         this.mouthOpenness = this.mouthOpenness + ((1.0F - this.mouthOpenness) * 0.7F + 0.05F);
         if (this.mouthOpenness > 1.0F) {
            this.mouthOpenness = 1.0F;
         }
      } else {
         this.mouthOpenness = this.mouthOpenness + ((0.0F - this.mouthOpenness) * 0.7F - 0.05F);
         if (this.mouthOpenness < 0.0F) {
            this.mouthOpenness = 0.0F;
         }
      }
   }

   private void mountTo(EntityPlayer player) {
      player.rotationYaw = this.rotationYaw;
      player.rotationPitch = this.rotationPitch;
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.worldObj.isRemote) {
         player.mountEntity(this);
      }
   }

   public boolean isActive() {
      return this.getHorseType() == 0;
   }

   private boolean canMate() {
      return this.riddenByEntity == null
         && this.ridingEntity == null
         && this.isTame()
         && this.isAdultHorse()
         && !this.isSterile()
         && this.getHealth() >= this.getMaxHealth()
         && this.isInLove();
   }

   @Override
   protected Item getDropItem() {
      boolean flag = this.rand.nextInt(4) == 0;
      int horseType2 = this.getHorseType();
      return horseType2 == 4 ? Items.bone : (horseType2 == 3 ? (flag ? null : Items.rotten_flesh) : Items.leather);
   }

   private boolean getHorseWatchableBoolean(int i) {
      return (this.dataWatcher.getWatchableObjectInt(16) & i) != 0;
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomNameTag();
      }

      switch (this.getHorseType()) {
         case 0:
         default:
            return StatCollector.translateToLocal("entity.horse.name");
         case 1:
            return StatCollector.translateToLocal("entity.donkey.name");
         case 2:
            return StatCollector.translateToLocal("entity.mule.name");
         case 3:
            return StatCollector.translateToLocal("entity.zombiehorse.name");
         case 4:
            return StatCollector.translateToLocal("entity.skeletonhorse.name");
      }
   }

   public boolean isBreeding() {
      return this.getHorseWatchableBoolean(16);
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }

   private float getModifiedMaxHealth() {
      return 15.0F + this.rand.nextInt(8) + this.rand.nextInt(9);
   }

   public void makeHorseRearWithSound() {
      this.makeHorseRear();
      String text = this.getAngrySoundName();
      if (text != null) {
         this.playSound(text, this.getSoundVolume(), this.getSoundPitch());
      }
   }

   public void setHorseTamed(boolean tamed) {
      this.setHorseWatchableBoolean(2, tamed);
   }

   @Override
   public boolean getCanSpawnHere() {
      this.prepareChunkForSpawn();
      return super.getCanSpawnHere();
   }

   public void dropChestItems() {
      this.dropItemsInChest(this, this.horseChest);
      this.dropChests();
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return false;
   }

   public int getMaxTemper() {
      return 100;
   }

   private void makeHorseRear() {
      if (!this.worldObj.isRemote) {
         this.jumpRearingCounter = 1;
         this.setRearing(true);
      }
   }

   @Override
   public boolean isOnLadder() {
      return false;
   }

   public void setOwnerId(String id) {
      this.dataWatcher.updateObject(21, id);
   }

   public EntityHorse(World worldIn) {
      super(worldIn);
      this.setSize(1.4F, 1.6F);
      this.isImmuneToFire = false;
      this.setChested(false);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.2));
      this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 0.7));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.initHorseChest();
   }

   @Override
   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal == this) {
         return false;
      } else if (otherAnimal.getClass() != this.getClass()) {
         return false;
      } else {
         EntityHorse entityHorse = (EntityHorse)otherAnimal;
         if (this.canMate() && entityHorse.canMate()) {
            int horseType2 = this.getHorseType();
            int horseType3 = entityHorse.getHorseType();
            return horseType2 == horseType3 || horseType2 == 0 && horseType3 == 1 || horseType2 == 1 && horseType3 == 0;
         } else {
            return false;
         }
      }
   }

   public int getTemper() {
      return this.temper;
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      int i = 0;
      int i2;
      if (livingdata instanceof EntityHorse.GroupData) {
         i2 = ((EntityHorse.GroupData)livingdata).horseType;
         i = ((EntityHorse.GroupData)livingdata).horseVariant & 0xFF | this.rand.nextInt(5) << 8;
      } else {
         if (this.rand.nextInt(10) == 0) {
            i2 = 1;
         } else {
            int i3 = this.rand.nextInt(7);
            int i4 = this.rand.nextInt(5);
            i2 = 0;
            i = i3 | i4 << 8;
         }

         livingdata = new EntityHorse.GroupData(i2, i);
      }

      this.setHorseType(i2);
      this.setHorseVariant(i);
      if (this.rand.nextInt(5) == 0) {
         this.setGrowingAge(-24000);
      }

      if (i2 != 4 && i2 != 3) {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getModifiedMaxHealth());
         if (i2 == 0) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getModifiedMovementSpeed());
         } else {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.175F);
         }
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0);
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
      }

      if (i2 != 2 && i2 != 1) {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(this.getModifiedJumpStrength());
      } else {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(0.5);
      }

      this.setHealth(this.getMaxHealth());
      return livingdata;
   }

   public void openGUI(EntityPlayer playerEntity) {
      if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == playerEntity) && this.isTame()) {
         this.horseChest.setCustomName(this.getName());
         playerEntity.displayGUIHorse(this, this.horseChest);
      }
   }

   public boolean isActive2() {
      return this.isAdultHorse();
   }

   public String[] getVariantTexturePaths() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.horseTexturesArray;
   }

   private void playHorseStepSound() {
      this.headTossCounter = 1;
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
      this.dataWatcher.addObject(19, (byte)0);
      this.dataWatcher.addObject(20, 0);
      this.dataWatcher.addObject(21, String.valueOf(""));
      this.dataWatcher.addObject(22, 0);
   }

   public float getMouthOpennessAngle(float f) {
      return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * f;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot == 499 && this.canCarryChest()) {
         if (itemStackIn == null && this.isChested()) {
            this.setChested(false);
            this.initHorseChest();
            return true;
         }

         if (itemStackIn != null && itemStackIn.getItem() == Item.getItemFromBlock(Blocks.chest) && !this.isChested()) {
            this.setChested(true);
            this.initHorseChest();
            return true;
         }
      }

      int i = inventorySlot - 400;
      if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory()) {
         if (i == 0 && itemStackIn != null && itemStackIn.getItem() != Items.saddle) {
            return false;
         }

         if (i == 1 && (itemStackIn != null && !isArmorItem(itemStackIn.getItem()) || !this.isActive())) {
            return false;
         }

         this.horseChest.setInventorySlotContents(i, itemStackIn);
         this.updateHorseSlots();
         return true;
      } else {
         int i2 = inventorySlot - 500 + 2;
         if (i2 >= 2 && i2 < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(i2, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean prepareChunkForSpawn() {
      int i = MathHelper.floor_double(this.posX);
      int i2 = MathHelper.floor_double(this.posZ);
      this.worldObj.getBiomeGenForCoords(new BlockPos(i, 0, i2));
      return true;
   }

   private void openHorseMouth() {
      if (!this.worldObj.isRemote) {
         this.openMouthCounter = 1;
         this.setHorseWatchableBoolean(128, true);
      }
   }

   @Override
   public boolean canBePushed() {
      return this.riddenByEntity == null;
   }

   public boolean hasValidTexturePaths() {
      return this.validTexturePaths;
   }

   @Override
   protected String getHurtSound() {
      this.openHorseMouth();
      if (this.rand.nextInt(3) == 0) {
         this.makeHorseRear();
      }

      int horseType2 = this.getHorseType();
      return horseType2 == 3
         ? "mob.horse.zombie.hit"
         : (horseType2 == 4 ? "mob.horse.skeleton.hit" : (horseType2 != 1 && horseType2 != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
   }

   public float getGrassEatingAmount(float f) {
      return this.prevHeadLean + (this.headLean - this.prevHeadLean) * f;
   }

   @Override
   public boolean allowLeashing() {
      return !this.isUndead() && super.allowLeashing();
   }

   protected void spawnHorseParticles(boolean flag) {
      EnumParticleTypes particleTypes = flag ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

      for (int index = 0; index < 7; index++) {
         double d = this.rand.nextGaussian() * 0.02;
         double d2 = this.rand.nextGaussian() * 0.02;
         double d3 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               particleTypes,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 0.5 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               d,
               d2,
               d3
            );
      }
   }

   public boolean isHorseSaddled() {
      return this.getHorseWatchableBoolean(4);
   }

   public void setChested(boolean chested) {
      this.setHorseWatchableBoolean(8, chested);
   }

   public boolean getHasReproduced() {
      return this.hasReproduced;
   }

   private double getModifiedMovementSpeed() {
      return (0.45F + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3) * 0.25;
   }

   public static class GroupData implements IEntityLivingData {
      public int horseType;
      public int horseVariant;

      public GroupData(int type, int variant) {
         this.horseType = type;
         this.horseVariant = variant;
      }
   }
}
