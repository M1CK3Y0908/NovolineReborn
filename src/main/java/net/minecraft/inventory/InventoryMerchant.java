package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {
   private MerchantRecipe currentRecipe;
   private ItemStack[] theInventory = new ItemStack[3];
   private int currentRecipeIndex;
   private final EntityPlayer thePlayer;
   private final IMerchant theMerchant;

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.theInventory[index] != null) {
         if (index == 2) {
            ItemStack itemStack = this.theInventory[index];
            this.theInventory[index] = null;
            return itemStack;
         }

         if (this.theInventory[index].stackSize <= count) {
            ItemStack itemStack2 = this.theInventory[index];
            this.theInventory[index] = null;
            if (this.inventoryResetNeededOnSlotChange(index)) {
               this.resetRecipeAndSlots();
            }

            return itemStack2;
         } else {
            ItemStack itemStack3 = this.theInventory[index].splitStack(count);
            if (this.theInventory[index].stackSize == 0) {
               this.theInventory[index] = null;
            }

            if (this.inventoryResetNeededOnSlotChange(index)) {
               this.resetRecipeAndSlots();
            }

            return itemStack3;
         }
      } else {
         return null;
      }
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.theMerchant.getCustomer() == player;
   }

   @Override
   public void clear() {
      for (int index = 0; index < this.theInventory.length; index++) {
         this.theInventory[index] = null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.theInventory[index] != null) {
         ItemStack itemStack = this.theInventory[index];
         this.theInventory[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public String getName() {
      return "mob.villager";
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   private boolean inventoryResetNeededOnSlotChange(int i) {
      return i == 0 || i == 1;
   }

   public void resetRecipeAndSlots() {
      this.currentRecipe = null;
      ItemStack itemStack = this.theInventory[0];
      ItemStack itemStack2 = this.theInventory[1];
      if (itemStack == null) {
         itemStack = itemStack2;
         itemStack2 = null;
      }

      if (itemStack == null) {
         this.setInventorySlotContents(2, (ItemStack)null);
      } else {
         MerchantRecipeList recipes = this.theMerchant.getRecipes(this.thePlayer);
         if (recipes != null) {
            MerchantRecipe recipeBeUsed = recipes.canRecipeBeUsed(itemStack, itemStack2, this.currentRecipeIndex);
            if (recipeBeUsed != null && !recipeBeUsed.isRecipeDisabled()) {
               this.currentRecipe = recipeBeUsed;
               this.setInventorySlotContents(2, recipeBeUsed.getItemToSell().copy());
            } else if (itemStack2 != null) {
               recipeBeUsed = recipes.canRecipeBeUsed(itemStack2, itemStack, this.currentRecipeIndex);
               if (recipeBeUsed != null && !recipeBeUsed.isRecipeDisabled()) {
                  this.currentRecipe = recipeBeUsed;
                  this.setInventorySlotContents(2, recipeBeUsed.getItemToSell().copy());
               } else {
                  this.setInventorySlotContents(2, (ItemStack)null);
               }
            } else {
               this.setInventorySlotContents(2, (ItemStack)null);
            }
         }
      }

      this.theMerchant.verifySellingItem(this.getStackInSlot(2));
   }

   public MerchantRecipe getCurrentRecipe() {
      return this.currentRecipe;
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.theInventory[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      if (this.inventoryResetNeededOnSlotChange(index)) {
         this.resetRecipeAndSlots();
      }
   }

   @Override
   public int getSizeInventory() {
      return this.theInventory.length;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
   }

   public void setCurrentRecipeIndex(int currentRecipeIndexIn) {
      this.currentRecipeIndex = currentRecipeIndexIn;
      this.resetRecipeAndSlots();
   }

   @Override
   public void markDirty() {
      this.resetRecipeAndSlots();
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.theInventory[index];
   }

   @Override
   public void setField(int id, int value) {
   }

   public InventoryMerchant(EntityPlayer thePlayerIn, IMerchant theMerchantIn) {
      this.thePlayer = thePlayerIn;
      this.theMerchant = theMerchantIn;
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
   }
}
