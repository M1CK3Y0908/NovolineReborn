package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
   private IMerchant theMerchant;
   private final World theWorld;
   private InventoryMerchant merchantInventory;

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.theMerchant.getCustomer() == playerIn;
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.merchantInventory.resetRecipeAndSlots();
      super.onCraftMatrixChanged(inventoryIn);
   }

   public void setCurrentRecipeIndex(int currentRecipeIndex) {
      this.merchantInventory.setCurrentRecipeIndex(currentRecipeIndex);
   }

   public ContainerMerchant(InventoryPlayer playerInventory, IMerchant merchant, World worldIn) {
      this.theMerchant = merchant;
      this.theWorld = worldIn;
      this.merchantInventory = new InventoryMerchant(playerInventory.player, merchant);
      this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
      this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
      this.addSlotToContainer(new SlotMerchantResult(playerInventory.player, merchant, this.merchantInventory, 2, 120, 53));

      for (int index = 0; index < 3; index++) {
         for (int innerIndex = 0; innerIndex < 9; innerIndex++) {
            this.addSlotToContainer(new Slot(playerInventory, innerIndex + index * 9 + 9, 8 + innerIndex * 18, 84 + index * 18));
         }
      }

      for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
         this.addSlotToContainer(new Slot(playerInventory, nestedIndex, 8 + nestedIndex * 18, 142));
      }
   }

   public InventoryMerchant getMerchantInventory() {
      return this.merchantInventory;
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.theMerchant.setCustomer((EntityPlayer)null);
      super.onContainerClosed(playerIn);
      if (!this.theWorld.isRemote) {
         ItemStack itemStack3 = this.merchantInventory.removeStackFromSlot(0);
         if (itemStack3 != null) {
            playerIn.getEntityItem3(itemStack3);
         }

         itemStack3 = this.merchantInventory.removeStackFromSlot(1);
         if (itemStack3 != null) {
            playerIn.getEntityItem3(itemStack3);
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
         if (index == 2) {
            if (!this.mergeItemStack(itemStack2, 3, 39, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (index != 0 && index != 1) {
            if (index >= 3 && index < 30) {
               if (!this.mergeItemStack(itemStack2, 30, 39, false)) {
                  return null;
               }
            } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemStack2, 3, 30, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 3, 39, false)) {
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
   public void updateProgressBar(int id, int data) {
   }
}
