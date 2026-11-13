package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Slot {
   public final IInventory inventory;
   public int slotNumber;
   private final int slotIndex;
   public int xDisplayPosition;
   public int yDisplayPosition;

   public boolean isHere(IInventory inv, int slotIn) {
      return inv == this.inventory && slotIn == this.slotIndex;
   }

   public boolean canTakeStack(EntityPlayer playerIn) {
      return true;
   }

   public void putStack(ItemStack stack) {
      this.inventory.setInventorySlotContents(this.slotIndex, stack);
      this.onSlotChanged();
   }

   public boolean getHasStack() {
      return this.getStack() != null;
   }

   public boolean canBeHovered() {
      return true;
   }

   public String getSlotTexture() {
      return null;
   }

   public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      this.onSlotChanged();
   }

   public Slot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      this.inventory = inventoryIn;
      this.slotIndex = index;
      this.xDisplayPosition = xPosition;
      this.yDisplayPosition = yPosition;
   }

   public void onSlotChange(ItemStack itemStack, ItemStack itemStack2) {
      if (itemStack != null && itemStack2 != null && itemStack.getItem() == itemStack2.getItem()) {
         int i = itemStack2.stackSize - itemStack.stackSize;
         if (i > 0) {
            this.onCrafting(itemStack, i);
         }
      }
   }

   public int getItemStackLimit(ItemStack stack) {
      return this.getSlotStackLimit();
   }

   public int getSlotStackLimit() {
      return this.inventory.getInventoryStackLimit();
   }

   public void onSlotChanged() {
      this.inventory.markDirty();
   }

   protected void onCrafting(ItemStack stack, int amount) {
   }

   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   public ItemStack decrStackSize(int amount) {
      return this.inventory.decrStackSize(this.slotIndex, amount);
   }

   public boolean isItemValid(ItemStack stack) {
      return true;
   }
}
