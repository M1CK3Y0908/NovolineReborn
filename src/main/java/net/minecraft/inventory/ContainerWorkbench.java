package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
   public IInventory craftResult = new InventoryCraftResult();
   private World worldObj;
   private BlockPos pos;

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.worldObj.getBlockState(this.pos).getBlock() != Blocks.crafting_table
         ? false
         : playerIn.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
   }

   public ContainerWorkbench(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
      this.worldObj = worldIn;
      this.pos = posIn;
      this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

      for (int index = 0; index < 3; index++) {
         for (int innerIndex = 0; innerIndex < 3; innerIndex++) {
            this.addSlotToContainer(new Slot(this.craftMatrix, innerIndex + index * 3, 30 + innerIndex * 18, 17 + index * 18));
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

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   @Override
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.worldObj.isRemote) {
         for (int index = 0; index < 9; index++) {
            ItemStack itemStack = this.craftMatrix.removeStackFromSlot(index);
            if (itemStack != null) {
               playerIn.getEntityItem3(itemStack);
            }
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
            if (!this.mergeItemStack(itemStack2, 10, 46, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (index >= 10 && index < 37) {
            if (!this.mergeItemStack(itemStack2, 37, 46, false)) {
               return null;
            }
         } else if (index >= 37 && index < 46) {
            if (!this.mergeItemStack(itemStack2, 10, 37, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 10, 46, false)) {
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
