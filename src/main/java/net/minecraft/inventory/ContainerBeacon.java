package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container {
   private IInventory tileBeacon;
   private final ContainerBeacon.BeaconSlot beaconSlot;

   @Override
   public void updateProgressBar(int id, int data) {
      this.tileBeacon.setField(id, data);
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendAllWindowProperties(this, this.tileBeacon);
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (playerIn != null && !playerIn.worldObj.isRemote) {
         ItemStack itemStack = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());
         if (itemStack != null) {
            playerIn.getEntityItem3(itemStack);
         }
      }
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemStack2, 1, 37, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(itemStack2) && itemStack2.stackSize == 1) {
            if (!this.mergeItemStack(itemStack2, 0, 1, false)) {
               return null;
            }
         } else if (index >= 1 && index < 28) {
            if (!this.mergeItemStack(itemStack2, 28, 37, false)) {
               return null;
            }
         } else if (index >= 28 && index < 37) {
            if (!this.mergeItemStack(itemStack2, 1, 28, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 1, 37, false)) {
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

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileBeacon.isUseableByPlayer(playerIn);
   }

   public IInventory getBeaconInventory() {
      return this.tileBeacon;
   }

   public ContainerBeacon(IInventory playerInventory, IInventory tileBeaconIn) {
      this.tileBeacon = tileBeaconIn;
      this.addSlotToContainer(this.beaconSlot = new ContainerBeacon.BeaconSlot(tileBeaconIn, 0, 136, 110));

      for (int i = 0; i < 3; i++) {
         for (int i2 = 0; i2 < 9; i2++) {
            this.addSlotToContainer(new Slot(playerInventory, i2 + i * 9 + 9, 36 + i2 * 18, 137 + i * 18));
         }
      }

      for (int i3 = 0; i3 < 9; i3++) {
         this.addSlotToContainer(new Slot(playerInventory, i3, 36 + i3 * 18, 195));
      }
   }

   class BeaconSlot extends Slot {
      @Override
      public int getSlotStackLimit() {
         return 1;
      }

      public BeaconSlot(IInventory iInventory, int i, int i2, int i3) {
         super(iInventory, i, i2, i3);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return stack == null
            ? false
            : stack.getItem() == Items.emerald
               || stack.getItem() == Items.diamond
               || stack.getItem() == Items.gold_ingot
               || stack.getItem() == Items.iron_ingot;
      }
   }
}
