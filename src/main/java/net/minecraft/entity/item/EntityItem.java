package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem extends Entity {
   private int delayBeforeCanPickup;
   private int health = 5;
   private int age;
   private String owner;
   public float hoverStart = (float)(Math.random() * Math.PI * 2.0);
   private static final Logger logger = LogManager.getLogger();
   private String thrower;

   private boolean combineItems(EntityItem other) {
      if (other == this) {
         return false;
      }

      if (other.isEntityAlive() && this.isEntityAlive()) {
         ItemStack itemStack = this.getEntityItem();
         ItemStack itemStack2 = other.getEntityItem();
         if (this.delayBeforeCanPickup == 32767 || other.delayBeforeCanPickup == 32767) {
            return false;
         }

         if (this.age != -32768 && other.age != -32768) {
            if (itemStack2.getItem() != itemStack.getItem()) {
               return false;
            }

            if (itemStack2.hasTagCompound() ^ itemStack.hasTagCompound()) {
               return false;
            }

            if (itemStack2.hasTagCompound() && !itemStack2.getTagCompound().equals(itemStack.getTagCompound())) {
               return false;
            }

            if (itemStack2.getItem() == null) {
               return false;
            }

            if (itemStack2.getItem().getHasSubtypes() && itemStack2.getMetadata() != itemStack.getMetadata()) {
               return false;
            }

            if (itemStack2.stackSize < itemStack.stackSize) {
               return other.combineItems(this);
            }

            if (itemStack2.stackSize + itemStack.stackSize > itemStack2.getMaxStackSize()) {
               return false;
            }

            itemStack2.stackSize = itemStack2.stackSize + itemStack.stackSize;
            other.delayBeforeCanPickup = Math.max(other.delayBeforeCanPickup, this.delayBeforeCanPickup);
            other.age = Math.min(other.age, this.age);
            other.setEntityItemStack(itemStack2);
            this.setDead();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public String getThrower() {
      return this.thrower;
   }

   @Override
   public boolean handleWaterMovement() {
      if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.water, this)) {
         if (!this.inWater && !this.firstUpdate) {
            this.resetHeight();
         }

         this.inWater = true;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   public void setDefaultPickupDelay() {
      this.delayBeforeCanPickup = 10;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("Health", (byte)this.health);
      tagCompound.setShort("Age", (short)this.age);
      tagCompound.setShort("PickupDelay", (short)this.delayBeforeCanPickup);
      if (this.getThrower() != null) {
         tagCompound.setString("Thrower", this.thrower);
      }

      if (this.getOwner() != null) {
         tagCompound.setString("Owner", this.owner);
      }

      if (this.getEntityItem() != null) {
         tagCompound.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
      }
   }

   public ItemStack getEntityItem() {
      ItemStack itemStack = this.getDataWatcher().getWatchableObjectItemStack(10);
      if (itemStack == null) {
         if (this.worldObj != null) {
            logger.error("Item entity " + this.getEntityId() + " has no item?!");
         }

         return new ItemStack(Blocks.stone);
      } else {
         return itemStack;
      }
   }

   public boolean cannotPickup() {
      return this.delayBeforeCanPickup > 0;
   }

   public void setAgeToCreativeDespawnTime() {
      this.age = 4800;
   }

   public void setInfinitePickupDelay() {
      this.delayBeforeCanPickup = 32767;
   }

   public void update() {
      this.delayBeforeCanPickup = 40;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   @Override
   protected void entityInit() {
      this.getDataWatcher().handleInt(10);
   }

   public void setNoPickupDelay() {
      this.delayBeforeCanPickup = 0;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : StatCollector.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
   }

   @Override
   public void onUpdate() {
      if (this.getEntityItem() == null) {
         this.setDead();
      } else {
         super.onUpdate();
         if (this.delayBeforeCanPickup > 0 && this.delayBeforeCanPickup != 32767) {
            this.delayBeforeCanPickup--;
         }

         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.motionY -= 0.04F;
         this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         if ((int)this.prevPosX != (int)this.posX
            || (int)this.prevPosY != (int)this.posY
            || (int)this.prevPosZ != (int)this.posZ
            || this.ticksExisted % 25 == 0) {
            if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava) {
               this.motionY = 0.2F;
               this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
               this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
               this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (!this.worldObj.isRemote) {
               this.searchForOtherItemsNearby();
            }
         }

         float f = 0.98F;
         if (this.onGround) {
            f = this.worldObj
                  .getBlockState(
                     new BlockPos(
                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                     )
                  )
                  .getBlock()
                  .slipperiness
               * 0.98F;
         }

         this.motionX *= f;
         this.motionY *= 0.98F;
         this.motionZ *= f;
         if (this.onGround) {
            this.motionY *= -0.5;
         }

         if (this.age != -32768) {
            this.age++;
         }

         this.handleWaterMovement();
         if (!this.worldObj.isRemote && this.age >= 6000) {
            this.setDead();
         }
      }
   }

   private void searchForOtherItemsNearby() {
      for (EntityItem entityItem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.5, 0.0, 0.5))) {
         this.combineItems(entityItem);
      }
   }

   public int getAge() {
      return this.age;
   }

   public EntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setEntityItemStack(stack);
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.worldObj.isRemote) {
         ItemStack itemStack = this.getEntityItem();
         int stackSize2 = itemStack.stackSize;
         if (this.delayBeforeCanPickup == 0
            && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(entityIn.getName()))
            && entityIn.inventory.addItemStackToInventory(itemStack)) {
            if (itemStack.getItem() == Item.getItemFromBlock(Blocks.log)) {
               entityIn.triggerAchievement(AchievementList.mineWood);
            }

            if (itemStack.getItem() == Item.getItemFromBlock(Blocks.log2)) {
               entityIn.triggerAchievement(AchievementList.mineWood);
            }

            if (itemStack.getItem() == Items.leather) {
               entityIn.triggerAchievement(AchievementList.killCow);
            }

            if (itemStack.getItem() == Items.diamond) {
               entityIn.triggerAchievement(AchievementList.diamonds);
            }

            if (itemStack.getItem() == Items.blaze_rod) {
               entityIn.triggerAchievement(AchievementList.blazeRod);
            }

            if (itemStack.getItem() == Items.diamond && this.getThrower() != null) {
               EntityPlayer entityPlayer = this.worldObj.getPlayerEntityByName(this.getThrower());
               if (entityPlayer != null && entityPlayer != entityIn) {
                  entityPlayer.triggerAchievement(AchievementList.diamondsToYou);
               }
            }

            if (!this.isSilent()) {
               this.worldObj.playSoundAtEntity(entityIn, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            entityIn.onItemPickup(this, stackSize2);
            if (itemStack.stackSize <= 0) {
               this.setDead();
            }
         }
      }
   }

   public void makeFakeItem() {
      this.setInfinitePickupDelay();
      this.age = 5999;
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   public String getOwner() {
      return this.owner;
   }

   public EntityItem(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.setEntityItemStack(new ItemStack(Blocks.air, 0));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.health = tagCompund.getShort("Health") & 255;
      this.age = tagCompund.getShort("Age");
      if (tagCompund.hasKey("PickupDelay")) {
         this.delayBeforeCanPickup = tagCompund.getShort("PickupDelay");
      }

      if (tagCompund.hasKey("Owner")) {
         this.owner = tagCompund.getString("Owner");
      }

      if (tagCompund.hasKey("Thrower")) {
         this.thrower = tagCompund.getString("Thrower");
      }

      NBTTagCompound nBTTagCompound = tagCompund.getCompoundTag("Item");
      this.setEntityItemStack(ItemStack.loadItemStackFromNBT(nBTTagCompound));
      if (this.getEntityItem() == null) {
         this.setDead();
      }
   }

   @Override
   protected void setTimeout() {
      this.attackEntityFrom(DamageSource.inFire, 1);
   }

   public void setThrower(String thrower) {
      this.thrower = thrower;
   }

   public EntityItem(World worldIn, double x, double y, double z) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.setPosition(x, y, z);
      this.rotationYaw = (float)(Math.random() * 360.0);
      this.motionX = (float)(Math.random() * 0.2F - 0.1F);
      this.motionY = 0.2F;
      this.motionZ = (float)(Math.random() * 0.2F - 0.1F);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (this.getEntityItem() != null && this.getEntityItem().getItem() == Items.nether_star && source.isExplosion()) {
         return false;
      }

      this.setBeenAttacked();
      this.health = (int)(this.health - amount);
      if (this.health <= 0) {
         this.setDead();
      }

      return false;
   }

   public void setEntityItemStack(ItemStack stack) {
      this.getDataWatcher().updateObject(10, stack);
      this.getDataWatcher().setObjectWatched(10);
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public void travelToDimension(int dimensionId) {
      super.travelToDimension(dimensionId);
      if (!this.worldObj.isRemote) {
         this.searchForOtherItemsNearby();
      }
   }
}
