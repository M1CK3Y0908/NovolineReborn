package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot {
   private EntityPlayer thePlayer;
   private int amountCrafted;
   private final IMerchant theMerchant;
   private final InventoryMerchant theMerchantInventory;

   @Override
   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.amountCrafted = this.amountCrafted + Math.min(amount, this.getStack().stackSize);
      }

      return super.decrStackSize(amount);
   }

   public SlotMerchantResult(EntityPlayer player, IMerchant merchant, InventoryMerchant merchantInventory, int slotIndex, int xPosition, int yPosition) {
      super(merchantInventory, slotIndex, xPosition, yPosition);
      this.thePlayer = player;
      this.theMerchant = merchant;
      this.theMerchantInventory = merchantInventory;
   }

   @Override
   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   private boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem) {
      ItemStack itemStack = trade.getItemToBuy();
      ItemStack itemStack2 = trade.getSecondItemToBuy();
      if (firstItem != null && firstItem.getItem() == itemStack.getItem()) {
         if (itemStack2 != null && secondItem != null && itemStack2.getItem() == secondItem.getItem()) {
            firstItem.stackSize = firstItem.stackSize - itemStack.stackSize;
            secondItem.stackSize = secondItem.stackSize - itemStack2.stackSize;
            return true;
         }

         if (itemStack2 == null && secondItem == null) {
            firstItem.stackSize = firstItem.stackSize - itemStack.stackSize;
            return true;
         }
      }

      return false;
   }

   @Override
   public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      this.onCrafting(stack);
      MerchantRecipe merchantRecipe = this.theMerchantInventory.getCurrentRecipe();
      if (merchantRecipe != null) {
         ItemStack itemStack = this.theMerchantInventory.getStackInSlot(0);
         ItemStack itemStack2 = this.theMerchantInventory.getStackInSlot(1);
         if (this.doTrade(merchantRecipe, itemStack, itemStack2) || this.doTrade(merchantRecipe, itemStack2, itemStack)) {
            this.theMerchant.useRecipe(merchantRecipe);
            playerIn.triggerAchievement(StatList.timesTradedWithVillagerStat);
            if (itemStack != null && itemStack.stackSize <= 0) {
               itemStack = null;
            }

            if (itemStack2 != null && itemStack2.stackSize <= 0) {
               itemStack2 = null;
            }

            this.theMerchantInventory.setInventorySlotContents(0, itemStack);
            this.theMerchantInventory.setInventorySlotContents(1, itemStack2);
         }
      }
   }

   protected void onCrafting(ItemStack stack) {
      stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
      this.amountCrafted = 0;
   }

   @Override
   protected void onCrafting(ItemStack stack, int amount) {
      this.amountCrafted += amount;
      this.onCrafting(stack);
   }
}
