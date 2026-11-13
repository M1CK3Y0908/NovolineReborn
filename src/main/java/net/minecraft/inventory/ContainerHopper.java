package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {
   private final IInventory hopperInventory;

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < this.hopperInventory.getSizeInventory()) {
            if (!this.mergeItemStack(itemStack2, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 0, this.hopperInventory.getSizeInventory(), false)) {
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

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.hopperInventory.closeInventory(playerIn);
   }

   public ContainerHopper(InventoryPlayer playerInventory, IInventory hopperInventoryIn, EntityPlayer player) {
      this.hopperInventory = hopperInventoryIn;
      hopperInventoryIn.openInventory(player);

      for (int i = 0; i < hopperInventoryIn.getSizeInventory(); i++) {
         this.addSlotToContainer(new Slot(hopperInventoryIn, i, 44 + i * 18, 20));
      }

      for (int i2 = 0; i2 < 3; i2++) {
         for (int i3 = 0; i3 < 9; i3++) {
            this.addSlotToContainer(new Slot(playerInventory, i3 + i2 * 9 + 9, 8 + i3 * 18, i2 * 18 + 51));
         }
      }

      for (int i4 = 0; i4 < 9; i4++) {
         this.addSlotToContainer(new Slot(playerInventory, i4, 8 + i4 * 18, 109));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.hopperInventory.isUseableByPlayer(playerIn);
   }
}
