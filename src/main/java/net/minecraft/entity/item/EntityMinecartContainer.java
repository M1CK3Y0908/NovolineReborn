package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
   private ItemStack[] minecartContainerItems = new ItemStack[36];
   private boolean dropContentsWhenDead = true;

   @Override
   protected void applyDrag() {
      int i = 15 - Container.calcRedstoneFromInventory(this);
      float f = 0.98F + i * 0.001F;
      this.motionX *= f;
      this.motionY *= 0.0;
      this.motionZ *= f;
   }

   public EntityMinecartContainer(World worldIn) {
      super(worldIn);
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (!this.worldObj.isRemote) {
         playerIn.displayGUIChest(this);
      }

      return true;
   }

   @Override
   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.isDead ? false : player.getDistanceSqToEntity(this) <= 64.0;
   }

   @Override
   public void travelToDimension(int dimensionId) {
      this.dropContentsWhenDead = false;
      super.travelToDimension(dimensionId);
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.minecartContainerItems.length; index++) {
         if (this.minecartContainerItems[index] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)index);
            this.minecartContainerItems[index].writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      tagCompound.setTag("Items", nbtTagList);
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.minecartContainerItems[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.minecartContainerItems[index] != null) {
         ItemStack itemStack = this.minecartContainerItems[index];
         this.minecartContainerItems[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.minecartContainerItems[index];
   }

   @Override
   public void clear() {
      for (int index = 0; index < this.minecartContainerItems.length; index++) {
         this.minecartContainerItems[index] = null;
      }
   }

   public EntityMinecartContainer(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : "container.minecart";
   }

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.minecartContainerItems[index] != null) {
         if (this.minecartContainerItems[index].stackSize <= count) {
            ItemStack itemStack = this.minecartContainerItems[index];
            this.minecartContainerItems[index] = null;
            return itemStack;
         }

         ItemStack itemStack2 = this.minecartContainerItems[index].splitStack(count);
         if (this.minecartContainerItems[index].stackSize == 0) {
            this.minecartContainerItems[index] = null;
         }

         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public boolean isLocked() {
      return false;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public void setDead() {
      if (this.dropContentsWhenDead) {
         InventoryHelper.dropInventoryItems(this.worldObj, this, this);
      }

      super.setDead();
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      NBTTagList nBTTagList = tagCompund.getTagList("Items", 10);
      this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         int i = nBTTagCompound.getByte("Slot") & 255;
         if (i >= 0 && i < this.minecartContainerItems.length) {
            this.minecartContainerItems[i] = ItemStack.loadItemStackFromNBT(nBTTagCompound);
         }
      }
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         InventoryHelper.dropInventoryItems(this.worldObj, this, this);
      }
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   @Override
   public void markDirty() {
   }

   @Override
   public void setLockCode(LockCode code) {
   }
}
