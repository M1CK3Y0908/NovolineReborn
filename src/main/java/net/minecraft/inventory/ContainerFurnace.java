package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerFurnace extends Container {
   private int cookTime;
   private final IInventory tileFurnace;
   private int furnaceBurnTime;
   private int totalCookTime;
   private int currentItemBurnTime;

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
         } else if (index != 1 && index != 0) {
            if (FurnaceRecipes.instance().getSmeltingResult(itemStack2) != null) {
               if (!this.mergeItemStack(itemStack2, 0, 1, false)) {
                  return null;
               }
            } else if (TileEntityFurnace.isItemFuel(itemStack2)) {
               if (!this.mergeItemStack(itemStack2, 1, 2, false)) {
                  return null;
               }
            } else if (index >= 3 && index < 30) {
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

   public ContainerFurnace(InventoryPlayer playerInventory, IInventory furnaceInventory) {
      this.tileFurnace = furnaceInventory;
      this.addSlotToContainer(new Slot(furnaceInventory, 0, 56, 17));
      this.addSlotToContainer(new SlotFurnaceFuel(furnaceInventory, 1, 56, 53));
      this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, furnaceInventory, 2, 116, 35));

      for (int index = 0; index < 3; index++) {
         for (int innerIndex = 0; innerIndex < 9; innerIndex++) {
            this.addSlotToContainer(new Slot(playerInventory, innerIndex + index * 9 + 9, 8 + innerIndex * 18, 84 + index * 18));
         }
      }

      for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
         this.addSlotToContainer(new Slot(playerInventory, nestedIndex, 8 + nestedIndex * 18, 142));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendAllWindowProperties(this, this.tileFurnace);
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileFurnace.isUseableByPlayer(playerIn);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int index = 0; index < this.crafters.size(); index++) {
         ICrafting crafting = this.crafters.get(index);
         if (this.cookTime != this.tileFurnace.getField(2)) {
            crafting.sendProgressBarUpdate(this, 2, this.tileFurnace.getField(2));
         }

         if (this.furnaceBurnTime != this.tileFurnace.getField(0)) {
            crafting.sendProgressBarUpdate(this, 0, this.tileFurnace.getField(0));
         }

         if (this.currentItemBurnTime != this.tileFurnace.getField(1)) {
            crafting.sendProgressBarUpdate(this, 1, this.tileFurnace.getField(1));
         }

         if (this.totalCookTime != this.tileFurnace.getField(3)) {
            crafting.sendProgressBarUpdate(this, 3, this.tileFurnace.getField(3));
         }
      }

      this.cookTime = this.tileFurnace.getField(2);
      this.furnaceBurnTime = this.tileFurnace.getField(0);
      this.currentItemBurnTime = this.tileFurnace.getField(1);
      this.totalCookTime = this.tileFurnace.getField(3);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      this.tileFurnace.setField(id, data);
   }
}
