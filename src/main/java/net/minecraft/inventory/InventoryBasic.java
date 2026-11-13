package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory {
   private String inventoryTitle;
   private List<IInvBasic> changeListeners;
   private boolean hasCustomName;
   private int slotsCount;
   private ItemStack[] inventoryContents;

   @Override
   public void setField(int id, int value) {
   }

   public InventoryBasic(String title, boolean customName, int slotCount) {
      this.inventoryTitle = title;
      this.hasCustomName = customName;
      this.slotsCount = slotCount;
      this.inventoryContents = new ItemStack[slotCount];
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventoryContents[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   public InventoryBasic(IChatComponent title, int slotCount) {
      this(title.getUnformattedText(), true, slotCount);
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public String getName() {
      return this.inventoryTitle;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   public void addInventoryChangeListener(IInvBasic listener) {
      if (this.changeListeners == null) {
         this.changeListeners = Lists.newArrayList();
      }

      this.changeListeners.add(listener);
   }

   @Override
   public void clear() {
      for (int index = 0; index < this.inventoryContents.length; index++) {
         this.inventoryContents[index] = null;
      }
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return true;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public int getSizeInventory() {
      return this.slotsCount;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.inventoryContents[index] != null) {
         ItemStack itemStack3 = this.inventoryContents[index];
         this.inventoryContents[index] = null;
         return itemStack3;
      } else {
         return null;
      }
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public boolean hasCustomName() {
      return this.hasCustomName;
   }

   public void setCustomName(String inventoryTitleIn) {
      this.hasCustomName = true;
      this.inventoryTitle = inventoryTitleIn;
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public void removeInventoryChangeListener(IInvBasic listener) {
      this.changeListeners.remove(listener);
   }

   @Override
   public void markDirty() {
      if (this.changeListeners != null) {
         for (int index = 0; index < this.changeListeners.size(); index++) {
            this.changeListeners.get(index).onInventoryChanged(this);
         }
      }
   }

   public ItemStack addItem(ItemStack stack) {
      ItemStack itemStack = stack.copy();

      for (int index = 0; index < this.slotsCount; index++) {
         ItemStack itemStack2 = this.getStackInSlot(index);
         if (itemStack2 == null) {
            this.setInventorySlotContents(index, itemStack);
            this.markDirty();
            return null;
         }

         if (ItemStack.areItemsEqual(itemStack2, itemStack)) {
            int i = Math.min(this.getInventoryStackLimit(), itemStack2.getMaxStackSize());
            int i2 = Math.min(itemStack.stackSize, i - itemStack2.stackSize);
            if (i2 > 0) {
               itemStack2.stackSize += i2;
               itemStack.stackSize -= i2;
               if (itemStack.stackSize <= 0) {
                  this.markDirty();
                  return null;
               }
            }
         }
      }

      if (itemStack.stackSize != stack.stackSize) {
         this.markDirty();
      }

      return itemStack;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.inventoryContents[index] != null) {
         if (this.inventoryContents[index].stackSize <= count) {
            ItemStack itemStack3 = this.inventoryContents[index];
            this.inventoryContents[index] = null;
            this.markDirty();
            return itemStack3;
         }

         ItemStack itemStack2 = this.inventoryContents[index].splitStack(count);
         if (this.inventoryContents[index].stackSize == 0) {
            this.inventoryContents[index] = null;
         }

         this.markDirty();
         return itemStack2;
      } else {
         return null;
      }
   }
}
