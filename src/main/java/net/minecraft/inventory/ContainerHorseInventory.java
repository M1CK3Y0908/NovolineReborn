package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {
   private IInventory horseInventory;
   private EntityHorse theHorse;

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.horseInventory.closeInventory(playerIn);
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.horseInventory.isUseableByPlayer(playerIn) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(playerIn) < 8.0F;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < this.horseInventory.getSizeInventory()) {
            if (!this.mergeItemStack(itemStack2, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (this.getSlot(1).isItemValid(itemStack2) && !this.getSlot(1).getHasStack()) {
            if (!this.mergeItemStack(itemStack2, 1, 2, false)) {
               return null;
            }
         } else if (this.getSlot(0).isItemValid(itemStack2)) {
            if (!this.mergeItemStack(itemStack2, 0, 1, false)) {
               return null;
            }
         } else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemStack2, 2, this.horseInventory.getSizeInventory(), false)) {
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

   public ContainerHorseInventory(IInventory playerInventory, IInventory horseInventoryIn, final EntityHorse horse, EntityPlayer player) {
      this.horseInventory = horseInventoryIn;
      this.theHorse = horse;
      horseInventoryIn.openInventory(player);
      this.addSlotToContainer(new Slot(horseInventoryIn, 0, 8, 18) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && stack.getItem() == Items.saddle && !this.getHasStack();
         }
      });
      this.addSlotToContainer(new Slot(horseInventoryIn, 1, 8, 36) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && horse.isActive() && EntityHorse.isArmorItem(stack.getItem());
         }

         @Override
         public boolean canBeHovered() {
            return horse.isActive();
         }
      });
      if (horse.isChested()) {
         for (int i = 0; i < 3; i++) {
            for (int i2 = 0; i2 < 5; i2++) {
               this.addSlotToContainer(new Slot(horseInventoryIn, 2 + i2 + i * 5, 80 + i2 * 18, 18 + i * 18));
            }
         }
      }

      for (int i3 = 0; i3 < 3; i3++) {
         for (int i4 = 0; i4 < 9; i4++) {
            this.addSlotToContainer(new Slot(playerInventory, i4 + i3 * 9 + 9, 8 + i4 * 18, 102 + i3 * 18 + -18));
         }
      }

      for (int i5 = 0; i5 < 9; i5++) {
         this.addSlotToContainer(new Slot(playerInventory, i5, 8 + i5 * 18, 142));
      }
   }
}
