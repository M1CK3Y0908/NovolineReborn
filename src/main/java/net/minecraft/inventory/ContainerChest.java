package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
   private IInventory lowerChestInventory;
   private int numRows;

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < this.numRows * 9) {
            if (!this.mergeItemStack(itemStack2, this.numRows * 9, this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 0, this.numRows * 9, false)) {
            return null;
         }

         if (itemStack2.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemStack;
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }

   public ContainerChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player) {
      this.lowerChestInventory = chestInventory;
      this.numRows = chestInventory.getSizeInventory() / 9;
      chestInventory.openInventory(player);
      int i = (this.numRows - 4) * 18;

      for (int innerIndex = 0; innerIndex < this.numRows; innerIndex++) {
         for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
            this.addSlotToContainer(new Slot(chestInventory, nestedIndex + innerIndex * 9, 8 + nestedIndex * 18, 18 + innerIndex * 18));
         }
      }

      for (int outerIndex = 0; outerIndex < 3; outerIndex++) {
         for (int innerIndex2 = 0; innerIndex2 < 9; innerIndex2++) {
            this.addSlotToContainer(new Slot(playerInventory, innerIndex2 + outerIndex * 9 + 9, 8 + innerIndex2 * 18, 103 + outerIndex * 18 + i));
         }
      }

      for (int index = 0; index < 9; index++) {
         this.addSlotToContainer(new Slot(playerInventory, index, 8 + index * 18, 161 + i));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.lowerChestInventory.isUseableByPlayer(playerIn);
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.lowerChestInventory.closeInventory(playerIn);
   }
}
