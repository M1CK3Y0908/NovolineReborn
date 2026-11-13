package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotations;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityArmorStand extends EntityLivingBase {
   private final ItemStack[] contents = new ItemStack[5];
   private static final Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private long punchCooldown;
   private static final Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private Rotations headRotation = DEFAULT_HEAD_ROTATION;
   private boolean canInteract;
   private static final Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
   private Rotations bodyRotation = DEFAULT_BODY_ROTATION;
   private Rotations leftArmRotation = DEFAULT_LEFTARM_ROTATION;
   private Rotations rightArmRotation;
   private static final Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
   private Rotations leftLegRotation;
   private Rotations rightLegRotation;
   private boolean wasMarker;
   private static final Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
   private int disabledSlots;
   private static final Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);

   @Override
   public boolean canBeCollidedWith() {
      return super.canBeCollidedWith() && !this.hasMarker();
   }

   public Rotations getHeadRotation() {
      return this.headRotation;
   }

   public boolean getShowArms() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 4) != 0;
   }

   public EntityArmorStand(World worldIn) {
      super(worldIn);
      this.rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
      this.leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
      this.rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;
      this.setSilent(true);
      this.noClip = this.hasNoGravity();
      this.setSize(0.5F, 1.975F);
   }

   private void swapItem(EntityPlayer player, int slot) {
      ItemStack itemStack = this.contents[slot];
      if ((itemStack == null || (this.disabledSlots & 1 << slot + 8) == 0) && (itemStack != null || (this.disabledSlots & 1 << slot + 16) == 0)) {
         int currentItem2 = player.inventory.currentItem;
         ItemStack itemStack2 = player.inventory.getStackInSlot(currentItem2);
         if (player.capabilities.isCreativeMode && (itemStack == null || itemStack.getItem() == Item.getItemFromBlock(Blocks.air)) && itemStack2 != null) {
            ItemStack itemStack3 = itemStack2.copy();
            itemStack3.stackSize = 1;
            this.setCurrentItemOrArmor(slot, itemStack3);
         } else if (itemStack2 == null || itemStack2.stackSize <= 1) {
            this.setCurrentItemOrArmor(slot, itemStack2);
            player.inventory.setInventorySlotContents(currentItem2, itemStack);
         } else if (itemStack == null) {
            ItemStack itemStack4 = itemStack2.copy();
            itemStack4.stackSize = 1;
            this.setCurrentItemOrArmor(slot, itemStack4);
            itemStack2.stackSize--;
         }
      }
   }

   private void setMarkerSize(boolean visible) {
      double xCoordinate = this.posX;
      double yCoordinate = this.posY;
      double zCoordinate = this.posZ;
      if (visible) {
         this.setSize(0.5F, 1.975F);
      } else {
         this.setSize(0.0F, 0.0F);
      }

      this.setPosition(xCoordinate, yCoordinate, zCoordinate);
   }

   @Override
   public void setInvisible(boolean invisible) {
      this.canInteract = invisible;
      super.setInvisible(invisible);
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.worldObj.isRemote) {
         return false;
      }

      if (DamageSource.outOfWorld.equals(source)) {
         this.setDead();
         return false;
      }

      if (this.isEntityInvulnerable(source) || this.canInteract || this.hasMarker()) {
         return false;
      }

      if (source.isExplosion()) {
         this.dropContents();
         this.setDead();
         return false;
      }

      if (DamageSource.inFire.equals(source)) {
         if (!this.isBurning()) {
            this.setFire(5);
         } else {
            this.damageArmorStand(0.15F);
         }

         return false;
      } else {
         if (DamageSource.onFire.equals(source) && this.getHealth() > 0.5F) {
            this.damageArmorStand(4.0F);
            return false;
         }

         boolean flag = "arrow".equals(source.getDamageType());
         if (!"player".equals(source.getDamageType()) && !flag) {
            return false;
         }

         if (source.getSourceOfDamage() instanceof EntityArrow) {
            source.getSourceOfDamage().setDead();
         }

         if (source.getEntity() instanceof EntityPlayer && !((EntityPlayer)source.getEntity()).capabilities.allowEdit) {
            return false;
         }

         if (source.isCreativePlayer()) {
            this.playParticles();
            this.setDead();
            return false;
         }

         long totalWorldTime = this.worldObj.getTotalWorldTime();
         if (totalWorldTime - this.punchCooldown > 5L && !flag) {
            this.punchCooldown = totalWorldTime;
         } else {
            this.dropBlock();
            this.playParticles();
            this.setDead();
         }

         return false;
      }
   }

   @Override
   protected void updatePotionMetadata() {
      this.setInvisible(this.canInteract);
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.contents[slotIn + 1];
   }

   @Override
   protected void collideWithNearbyEntities() {
      List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
      if (list != null && !list.isEmpty()) {
         for (int index = 0; index < list.size(); index++) {
            Entity entity = list.get(index);
            if (entity instanceof EntityMinecart
               && ((EntityMinecart)entity).getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
               && this.getDistanceSqToEntity(entity) <= 0.2) {
               entity.applyEntityCollision(this);
            }
         }
      }
   }

   public void setHeadRotation(Rotations rotations2) {
      this.headRotation = rotations2;
      this.dataWatcher.updateObject(11, rotations2);
   }

   @Override
   protected void collideWithEntity(Entity entityIn) {
   }

   public Rotations getRightLegRotation() {
      return this.rightLegRotation;
   }

   public Rotations getRightArmRotation() {
      return this.rightArmRotation;
   }

   @Override
   protected float updateDistance(float f, float f2) {
      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
      return 0.0F;
   }

   private void dropContents() {
      for (int index = 0; index < this.contents.length; index++) {
         if (this.contents[index] != null && this.contents[index].stackSize > 0) {
            if (this.contents[index] != null) {
               Block.spawnAsEntity(this.worldObj, new BlockPos(this).up(), this.contents[index]);
            }

            this.contents[index] = null;
         }
      }
   }

   @Override
   public boolean isImmuneToExplosions() {
      return this.isInvisible();
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      Rotations rotations = this.dataWatcher.getWatchableObjectRotations(11);
      if (!this.headRotation.equals(rotations)) {
         this.setHeadRotation(rotations);
      }

      Rotations rotations6 = this.dataWatcher.getWatchableObjectRotations(12);
      if (!this.bodyRotation.equals(rotations6)) {
         this.setBodyRotation(rotations6);
      }

      Rotations rotations7 = this.dataWatcher.getWatchableObjectRotations(13);
      if (!this.leftArmRotation.equals(rotations7)) {
         this.setLeftArmRotation(rotations7);
      }

      Rotations rotations8 = this.dataWatcher.getWatchableObjectRotations(14);
      if (!this.rightArmRotation.equals(rotations8)) {
         this.setRightArmRotation(rotations8);
      }

      Rotations rotations9 = this.dataWatcher.getWatchableObjectRotations(15);
      if (!this.leftLegRotation.equals(rotations9)) {
         this.setLeftLegRotation(rotations9);
      }

      Rotations rotations10 = this.dataWatcher.getWatchableObjectRotations(16);
      if (!this.rightLegRotation.equals(rotations10)) {
         this.setRightLegRotation(rotations10);
      }

      boolean hasMarker = this.hasMarker();
      if (!this.wasMarker && hasMarker) {
         this.setMarkerSize(false);
      } else {
         if (!this.wasMarker || hasMarker) {
            return;
         }

         this.setMarkerSize(true);
      }

      this.wasMarker = hasMarker;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.contents.length; index++) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         if (this.contents[index] != null) {
            this.contents[index].writeToNBT(nbtTagCompound);
         }

         nbtTagList.appendTag(nbtTagCompound);
      }

      tagCompound.setTag("Equipment", nbtTagList);
      if (this.getAlwaysRenderNameTag() && (this.getCustomNameTag() == null || this.getCustomNameTag().length() == 0)) {
         tagCompound.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
      }

      tagCompound.setBoolean("Invisible", this.isInvisible());
      tagCompound.setBoolean("Small", this.isSmall());
      tagCompound.setBoolean("ShowArms", this.getShowArms());
      tagCompound.setInteger("DisabledSlots", this.disabledSlots);
      tagCompound.setBoolean("NoGravity", this.hasNoGravity());
      tagCompound.setBoolean("NoBasePlate", this.hasNoBasePlate());
      if (this.hasMarker()) {
         tagCompound.setBoolean("Marker", this.hasMarker());
      }

      tagCompound.setTag("Pose", this.readPoseFromNBT());
   }

   private void setNoBasePlate(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 8);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -9);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(10, (byte)0);
      this.dataWatcher.addObject(11, DEFAULT_HEAD_ROTATION);
      this.dataWatcher.addObject(12, DEFAULT_BODY_ROTATION);
      this.dataWatcher.addObject(13, DEFAULT_LEFTARM_ROTATION);
      this.dataWatcher.addObject(14, DEFAULT_RIGHTARM_ROTATION);
      this.dataWatcher.addObject(15, DEFAULT_LEFTLEG_ROTATION);
      this.dataWatcher.addObject(16, DEFAULT_RIGHTLEG_ROTATION);
   }

   public boolean isSmall() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 1) != 0;
   }

   private void setShowArms(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 4);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -5);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   public Rotations getBodyRotation() {
      return this.bodyRotation;
   }

   @Override
   public boolean isChild() {
      return this.isSmall();
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (!this.hasNoGravity()) {
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   public Rotations getLeftArmRotation() {
      return this.leftArmRotation;
   }

   @Override
   public boolean interactAt(EntityPlayer player, Vec3 targetVec3) {
      if (this.hasMarker()) {
         return false;
      }

      if (!this.worldObj.isRemote && !player.isSpectator()) {
         int i = 0;
         ItemStack currentEquippedItem = player.getCurrentEquippedItem();
         boolean flag = currentEquippedItem != null;
         if (flag && currentEquippedItem.getItem() instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor)currentEquippedItem.getItem();
            if (itemArmor.armorType == 3) {
               i = 1;
            } else if (itemArmor.armorType == 2) {
               i = 2;
            } else if (itemArmor.armorType == 1) {
               i = 3;
            } else if (itemArmor.armorType == 0) {
               i = 4;
            }
         }

         if (flag && (currentEquippedItem.getItem() == Items.skull || currentEquippedItem.getItem() == Item.getItemFromBlock(Blocks.pumpkin))) {
            i = 4;
         }

         int i2 = 0;
         boolean small = this.isSmall();
         double d = small ? targetVec3.yCoord * 2.0 : targetVec3.yCoord;
         if (d >= 0.1 && d < 0.1 + (small ? 0.8 : 0.45) && this.contents[1] != null) {
            i2 = 1;
         } else if (d >= 0.9 + (small ? 0.3 : 0.0) && d < 0.9 + (small ? 1.0 : 0.7) && this.contents[3] != null) {
            i2 = 3;
         } else if (d >= 0.4 && d < 0.4 + (small ? 1.0 : 0.8) && this.contents[2] != null) {
            i2 = 2;
         } else if (d >= 1.6 && this.contents[4] != null) {
            i2 = 4;
         }

         boolean flag2 = this.contents[i2] != null;
         if ((this.disabledSlots & 1 << i2) != 0 || (this.disabledSlots & 1 << i) != 0) {
            i2 = i;
            if ((this.disabledSlots & 1 << i) != 0) {
               if ((this.disabledSlots & 1) != 0) {
                  return true;
               }

               i2 = 0;
            }
         }

         if (flag && i == 0 && !this.getShowArms()) {
            return true;
         }

         if (flag) {
            this.swapItem(player, i);
         } else if (flag2) {
            this.swapItem(player, i2);
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.contents[slotIn] = stack;
   }

   private void setNoGravity(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 2);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -3);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   private void dropBlock() {
      Block.spawnAsEntity(this.worldObj, new BlockPos(this), new ItemStack(Items.armor_stand));
      this.dropContents();
   }

   private void damageArmorStand(float damage) {
      float health = this.getHealth();
      health -= damage;
      if (health <= 0.5F) {
         this.dropContents();
         this.setDead();
      } else {
         this.setHealth(health);
      }
   }

   private NBTTagCompound readPoseFromNBT() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
         nbtTagCompound.setTag("Head", this.headRotation.writeToNBT());
      }

      if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
         nbtTagCompound.setTag("Body", this.bodyRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation)) {
         nbtTagCompound.setTag("LeftArm", this.leftArmRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation)) {
         nbtTagCompound.setTag("RightArm", this.rightArmRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation)) {
         nbtTagCompound.setTag("LeftLeg", this.leftLegRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation)) {
         nbtTagCompound.setTag("RightLeg", this.rightLegRotation.writeToNBT());
      }

      return nbtTagCompound;
   }

   public EntityArmorStand(World worldIn, double posX, double posY, double posZ) {
      this(worldIn);
      this.setPosition(posX, posY, posZ);
   }

   private void setSmall(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 1);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -2);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   @Override
   public void onKillCommand() {
      this.setDead();
   }

   public boolean hasNoGravity() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 2) != 0;
   }

   public void setRightLegRotation(Rotations rotations2) {
      this.rightLegRotation = rotations2;
      this.dataWatcher.updateObject(16, rotations2);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("Equipment", 9)) {
         NBTTagList nBTTagList = tagCompund.getTagList("Equipment", 10);

         for (int index = 0; index < this.contents.length; index++) {
            this.contents[index] = ItemStack.loadItemStackFromNBT(nBTTagList.getCompoundTagAt(index));
         }
      }

      this.setInvisible(tagCompund.getBoolean("Invisible"));
      this.setSmall(tagCompund.getBoolean("Small"));
      this.setShowArms(tagCompund.getBoolean("ShowArms"));
      this.disabledSlots = tagCompund.getInteger("DisabledSlots");
      this.setNoGravity(tagCompund.getBoolean("NoGravity"));
      this.setNoBasePlate(tagCompund.getBoolean("NoBasePlate"));
      this.setMarker(tagCompund.getBoolean("Marker"));
      this.wasMarker = !this.hasMarker();
      this.noClip = this.hasNoGravity();
      NBTTagCompound nBTTagCompound = tagCompund.getCompoundTag("Pose");
      this.writePoseToNBT(nBTTagCompound);
   }

   private void writePoseToNBT(NBTTagCompound tagCompound) {
      NBTTagList nBTTagList = tagCompound.getTagList("Head", 5);
      if (nBTTagList.tagCount() > 0) {
         this.setHeadRotation(new Rotations(nBTTagList));
      } else {
         this.setHeadRotation(DEFAULT_HEAD_ROTATION);
      }

      NBTTagList nBTTagList2 = tagCompound.getTagList("Body", 5);
      if (nBTTagList2.tagCount() > 0) {
         this.setBodyRotation(new Rotations(nBTTagList2));
      } else {
         this.setBodyRotation(DEFAULT_BODY_ROTATION);
      }

      NBTTagList nBTTagList3 = tagCompound.getTagList("LeftArm", 5);
      if (nBTTagList3.tagCount() > 0) {
         this.setLeftArmRotation(new Rotations(nBTTagList3));
      } else {
         this.setLeftArmRotation(DEFAULT_LEFTARM_ROTATION);
      }

      NBTTagList nBTTagList4 = tagCompound.getTagList("RightArm", 5);
      if (nBTTagList4.tagCount() > 0) {
         this.setRightArmRotation(new Rotations(nBTTagList4));
      } else {
         this.setRightArmRotation(DEFAULT_RIGHTARM_ROTATION);
      }

      NBTTagList nBTTagList5 = tagCompound.getTagList("LeftLeg", 5);
      if (nBTTagList5.tagCount() > 0) {
         this.setLeftLegRotation(new Rotations(nBTTagList5));
      } else {
         this.setLeftLegRotation(DEFAULT_LEFTLEG_ROTATION);
      }

      NBTTagList nBTTagList6 = tagCompound.getTagList("RightLeg", 5);
      if (nBTTagList6.tagCount() > 0) {
         this.setRightLegRotation(new Rotations(nBTTagList6));
      } else {
         this.setRightLegRotation(DEFAULT_RIGHTLEG_ROTATION);
      }
   }

   @Override
   public ItemStack getHeldItem() {
      return this.contents[0];
   }

   @Override
   public float getEyeHeight() {
      return this.isChild() ? this.height * 0.5F : this.height * 0.9F;
   }

   public void setBodyRotation(Rotations rotations2) {
      this.bodyRotation = rotations2;
      this.dataWatcher.updateObject(12, rotations2);
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      int i;
      if (inventorySlot == 99) {
         i = 0;
      } else {
         i = inventorySlot - 100 + 1;
         if (i < 0 || i >= this.contents.length) {
            return false;
         }
      }

      if (itemStackIn == null || EntityLiving.getArmorPosition(itemStackIn) == i || i == 4 && itemStackIn.getItem() instanceof ItemBlock) {
         this.setCurrentItemOrArmor(i, itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   public void setLeftArmRotation(Rotations rotations2) {
      this.leftArmRotation = rotations2;
      this.dataWatcher.updateObject(13, rotations2);
   }

   public void setRightArmRotation(Rotations rotations2) {
      this.rightArmRotation = rotations2;
      this.dataWatcher.updateObject(14, rotations2);
   }

   public Rotations getLeftLegRotation() {
      return this.leftLegRotation;
   }

   @Override
   public boolean isServerWorld() {
      return super.isServerWorld() && !this.hasNoGravity();
   }

   private void playParticles() {
      if (this.worldObj instanceof WorldServer) {
         ((WorldServer)this.worldObj)
            .spawnParticle(
               EnumParticleTypes.BLOCK_DUST,
               this.posX,
               this.posY + this.height / 1.5,
               this.posZ,
               10,
               this.width / 4.0F,
               this.height / 4.0F,
               this.width / 4.0F,
               0.05,
               Block.getStateId(Blocks.planks.getDefaultState())
            );
      }
   }

   @Override
   public ItemStack[] getInventory() {
      return this.contents;
   }

   private void setMarker(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 16);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -17);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   public void setLeftLegRotation(Rotations rotations2) {
      this.leftLegRotation = rotations2;
      this.dataWatcher.updateObject(15, rotations2);
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d) || d == 0.0) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   @Override
   public ItemStack getEquipmentInSlot(int slotIn) {
      return this.contents[slotIn];
   }

   public boolean hasNoBasePlate() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 8) != 0;
   }

   public boolean hasMarker() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 16) != 0;
   }
}
