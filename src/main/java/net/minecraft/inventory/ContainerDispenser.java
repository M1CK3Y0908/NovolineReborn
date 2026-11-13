package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerDispenser extends Container {
   private IInventory dispenserInventory;

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.dispenserInventory.isUseableByPlayer(playerIn);
   }

   public ContainerDispenser(IInventory playerInventory, IInventory dispenserInventoryIn) {
      this.dispenserInventory = dispenserInventoryIn;

      for (int index = 0; index < 3; index++) {
         for (int innerIndex = 0; innerIndex < 3; innerIndex++) {
            this.addSlotToContainer(new Slot(dispenserInventoryIn, innerIndex + index * 3, 62 + innerIndex * 18, 17 + index * 18));
         }
      }

      for (int nestedIndex = 0; nestedIndex < 3; nestedIndex++) {
         for (int index2 = 0; index2 < 9; index2++) {
            this.addSlotToContainer(new Slot(playerInventory, index2 + nestedIndex * 9 + 9, 8 + index2 * 18, 84 + nestedIndex * 18));
         }
      }

      for (int outerIndex = 0; outerIndex < 9; outerIndex++) {
         this.addSlotToContainer(new Slot(playerInventory, outerIndex, 8 + outerIndex * 18, 142));
      }
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < 9) {
            if (!this.mergeItemStack(itemStack2, 9, 45, true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 0, 9, false)) {
            return null;
         }

         if (itemStack2.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if (itemStack2.stackSize == itemStack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(playerIn, itemStack2);
      }

      return itemStack;
   }
}
