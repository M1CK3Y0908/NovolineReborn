package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class ContainerBrewingStand extends Container {
   private IInventory tileBrewingStand;
   private int brewTime;
   private final Slot theSlot;

   @Override
   public void updateProgressBar(int id, int data) {
      this.tileBrewingStand.setField(id, data);
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendAllWindowProperties(this, this.tileBrewingStand);
   }

   public ContainerBrewingStand(InventoryPlayer playerInventory, IInventory tileBrewingStandIn) {
      this.tileBrewingStand = tileBrewingStandIn;
      this.addSlotToContainer(new ContainerBrewingStand.Potion(playerInventory.player, tileBrewingStandIn, 0, 56, 46));
      this.addSlotToContainer(new ContainerBrewingStand.Potion(playerInventory.player, tileBrewingStandIn, 1, 79, 53));
      this.addSlotToContainer(new ContainerBrewingStand.Potion(playerInventory.player, tileBrewingStandIn, 2, 102, 46));
      this.theSlot = this.addSlotToContainer(new ContainerBrewingStand.Ingredient(tileBrewingStandIn, 3, 79, 17));

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
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int index = 0; index < this.crafters.size(); index++) {
         ICrafting crafting = this.crafters.get(index);
         if (this.brewTime != this.tileBrewingStand.getField(0)) {
            crafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getField(0));
         }
      }

      this.brewTime = this.tileBrewingStand.getField(0);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if ((index < 0 || index > 2) && index != 3) {
            if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(itemStack2)) {
               if (!this.mergeItemStack(itemStack2, 3, 4, false)) {
                  return null;
               }
            } else if (ContainerBrewingStand.Potion.canHoldPotion(itemStack)) {
               if (!this.mergeItemStack(itemStack2, 0, 3, false)) {
                  return null;
               }
            } else if (index >= 4 && index < 31) {
               if (!this.mergeItemStack(itemStack2, 31, 40, false)) {
                  return null;
               }
            } else if (index >= 31 && index < 40) {
               if (!this.mergeItemStack(itemStack2, 4, 31, false)) {
                  return null;
               }
            } else if (!this.mergeItemStack(itemStack2, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.mergeItemStack(itemStack2, 4, 40, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
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
      return this.tileBrewingStand.isUseableByPlayer(playerIn);
   }

   class Ingredient extends Slot {
      @Override
      public boolean isItemValid(ItemStack stack) {
         return stack != null ? stack.getItem().isPotionIngredient(stack) : false;
      }

      public Ingredient(IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
      }

      @Override
      public int getSlotStackLimit() {
         return 64;
      }
   }

   static class Potion extends Slot {
      private EntityPlayer player;

      @Override
      public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
         if (stack.getItem() == Items.potionitem && stack.getMetadata() > 0) {
            this.player.triggerAchievement(AchievementList.potion);
         }

         super.onPickupFromSlot(playerIn, stack);
      }

      public Potion(EntityPlayer playerIn, IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
         this.player = playerIn;
      }

      @Override
      public int getSlotStackLimit() {
         return 1;
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return canHoldPotion(stack);
      }

      public static boolean canHoldPotion(ItemStack stack) {
         return stack != null && (stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle);
      }
   }
}
