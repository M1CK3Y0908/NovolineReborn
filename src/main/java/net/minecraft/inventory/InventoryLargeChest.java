package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class InventoryLargeChest implements ILockableContainer {
   private String name;
   private ILockableContainer upperChest;
   private ILockableContainer lowerChest;

   @Override
   public String getName() {
      return this.upperChest.hasCustomName() ? this.upperChest.getName() : (this.lowerChest.hasCustomName() ? this.lowerChest.getName() : this.name);
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public String getGuiID() {
      return this.upperChest.getGuiID();
   }

   @Override
   public int getSizeInventory() {
      return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.upperChest.isUseableByPlayer(player) && this.lowerChest.isUseableByPlayer(player);
   }

   @Override
   public LockCode getLockCode() {
      return this.upperChest.getLockCode();
   }

   @Override
   public void clear() {
      this.upperChest.clear();
      this.lowerChest.clear();
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
      this.upperChest.closeInventory(entityPlayer);
      this.lowerChest.closeInventory(entityPlayer);
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      return index >= this.upperChest.getSizeInventory()
         ? this.lowerChest.decrStackSize(index - this.upperChest.getSizeInventory(), count)
         : this.upperChest.decrStackSize(index, count);
   }

   @Override
   public boolean hasCustomName() {
      return this.upperChest.hasCustomName() || this.lowerChest.hasCustomName();
   }

   @Override
   public void markDirty() {
      this.upperChest.markDirty();
      this.lowerChest.markDirty();
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index >= this.upperChest.getSizeInventory()) {
         this.lowerChest.setInventorySlotContents(index - this.upperChest.getSizeInventory(), stack);
      } else {
         this.upperChest.setInventorySlotContents(index, stack);
      }
   }

   public boolean isPartOfLargeChest(IInventory inventoryIn) {
      return this.upperChest == inventoryIn || this.lowerChest == inventoryIn;
   }

   @Override
   public ItemStack getStackInSlot(int i) {
      return i >= this.upperChest.getSizeInventory()
         ? this.lowerChest.getStackInSlot(i - this.upperChest.getSizeInventory())
         : this.upperChest.getStackInSlot(i);
   }

   @Override
   public boolean isLocked() {
      return this.upperChest.isLocked() || this.lowerChest.isLocked();
   }

   public InventoryLargeChest(String nameIn, ILockableContainer upperChestIn, ILockableContainer lowerChestIn) {
      this.name = nameIn;
      if (upperChestIn == null) {
         upperChestIn = lowerChestIn;
      }

      if (lowerChestIn == null) {
         lowerChestIn = upperChestIn;
      }

      this.upperChest = upperChestIn;
      this.lowerChest = lowerChestIn;
      if (upperChestIn.isLocked()) {
         lowerChestIn.setLockCode(upperChestIn.getLockCode());
      } else if (lowerChestIn.isLocked()) {
         upperChestIn.setLockCode(lowerChestIn.getLockCode());
      }
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public ItemStack removeStackFromSlot(int i) {
      return i >= this.upperChest.getSizeInventory()
         ? this.lowerChest.removeStackFromSlot(i - this.upperChest.getSizeInventory())
         : this.upperChest.removeStackFromSlot(i);
   }

   @Override
   public void setLockCode(LockCode code) {
      this.upperChest.setLockCode(code);
      this.lowerChest.setLockCode(code);
   }

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
      this.upperChest.openInventory(entityPlayer);
      this.lowerChest.openInventory(entityPlayer);
   }

   @Override
   public int getInventoryStackLimit() {
      return this.upperChest.getInventoryStackLimit();
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerChest(playerInventory, this, playerIn);
   }
}
