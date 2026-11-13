package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer extends Container {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
   public IInventory craftResult = new InventoryCraftResult();
   public boolean isLocalWorld;
   private final EntityPlayer thePlayer;

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);

      for (int index = 0; index < 4; index++) {
         ItemStack itemStack = this.craftMatrix.removeStackFromSlot(index);
         if (itemStack != null) {
            playerIn.getEntityItem3(itemStack);
         }
      }

      this.craftResult.setInventorySlotContents(0, (ItemStack)null);
   }

   @Override
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
   }

   public ContainerPlayer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
      this.isLocalWorld = localWorld;
      this.thePlayer = player;
      this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));

      for (int i = 0; i < 2; i++) {
         for (int i2 = 0; i2 < 2; i2++) {
            this.addSlotToContainer(new Slot(this.craftMatrix, i2 + i * 2, 88 + i2 * 18, 26 + i * 18));
         }
      }

      for (int i3 = 0; i3 < 4; i3++) {
         final int i4 = i3;
         this.addSlotToContainer(
            new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - i3, 8, 8 + i3 * 18) {
               @Override
               public String getSlotTexture() {
                  return ItemArmor.EMPTY_SLOT_NAMES[i4];
               }

               @Override
               public int getSlotStackLimit() {
                  return 1;
               }

               @Override
               public boolean isItemValid(ItemStack stack) {
                  return stack == null
                     ? false
                     : (
                        stack.getItem() instanceof ItemArmor
                           ? ((ItemArmor)stack.getItem()).armorType == i4
                           : (stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull ? false : i4 == 0)
                     );
               }
            }
         );
      }

      for (int i5 = 0; i5 < 3; i5++) {
         for (int i6 = 0; i6 < 9; i6++) {
            this.addSlotToContainer(new Slot(playerInventory, i6 + (i5 + 1) * 9, 8 + i6 * 18, 84 + i5 * 18));
         }
      }

      for (int i7 = 0; i7 < 9; i7++) {
         this.addSlotToContainer(new Slot(playerInventory, i7, 8 + i7 * 18, 142));
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemStack2, 9, 45, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (index >= 1 && index < 5) {
            if (!this.mergeItemStack(itemStack2, 9, 45, false)) {
               return null;
            }
         } else if (index >= 5 && index < 9) {
            if (!this.mergeItemStack(itemStack2, 9, 45, false)) {
               return null;
            }
         } else if (itemStack.getItem() instanceof ItemArmor && !this.inventorySlots.get(5 + ((ItemArmor)itemStack.getItem()).armorType).getHasStack()) {
            int i = 5 + ((ItemArmor)itemStack.getItem()).armorType;
            if (!this.mergeItemStack(itemStack2, i, i + 1, false)) {
               return null;
            }
         } else if (index >= 9 && index < 36) {
            if (!this.mergeItemStack(itemStack2, 36, 45, false)) {
               return null;
            }
         } else if (index >= 36 && index < 45) {
            if (!this.mergeItemStack(itemStack2, 9, 36, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 9, 45, false)) {
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
