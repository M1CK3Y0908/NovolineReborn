package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe {
   private final int recipeWidth;
   private final int recipeHeight;
   private boolean copyIngredientNBT;
   private final ItemStack[] recipeItems;
   private final ItemStack recipeOutput;

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting inv) {
      ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

      for (int index = 0; index < aitemstack.length; index++) {
         ItemStack itemStack = inv.getStackInSlot(index);
         if (itemStack != null && itemStack.getItem().hasContainerItem()) {
            aitemstack[index] = new ItemStack(itemStack.getItem().getContainerItem());
         }
      }

      return aitemstack;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   private boolean checkMatch(InventoryCrafting inventoryCrafting, int i, int i2, boolean flag) {
      for (int index = 0; index < 3; index++) {
         for (int innerIndex = 0; innerIndex < 3; innerIndex++) {
            int i3 = index - i;
            int i4 = innerIndex - i2;
            ItemStack itemStack3 = null;
            if (i3 >= 0 && i4 >= 0 && i3 < this.recipeWidth && i4 < this.recipeHeight) {
               if (flag) {
                  itemStack3 = this.recipeItems[this.recipeWidth - i3 - 1 + i4 * this.recipeWidth];
               } else {
                  itemStack3 = this.recipeItems[i3 + i4 * this.recipeWidth];
               }
            }

            ItemStack itemStack2 = inventoryCrafting.getStackInRowAndColumn(index, innerIndex);
            if (itemStack2 != null || itemStack3 != null) {
               if (itemStack2 == null && itemStack3 != null || itemStack2 != null && itemStack3 == null) {
                  return false;
               }

               if (itemStack3.getItem() != itemStack2.getItem()) {
                  return false;
               }

               if (itemStack3.getMetadata() != 32767 && itemStack3.getMetadata() != itemStack2.getMetadata()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack itemStack = this.getRecipeOutput().copy();
      if (this.copyIngredientNBT) {
         for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack itemStack2 = inv.getStackInSlot(index);
            if (itemStack2 != null && itemStack2.hasTagCompound()) {
               itemStack.setTagCompound((NBTTagCompound)itemStack2.getTagCompound().copy());
            }
         }
      }

      return itemStack;
   }

   @Override
   public int getRecipeSize() {
      return this.recipeWidth * this.recipeHeight;
   }

   public ShapedRecipes(int width, int height, ItemStack[] itemStacks, ItemStack output) {
      this.recipeWidth = width;
      this.recipeHeight = height;
      this.recipeItems = itemStacks;
      this.recipeOutput = output;
   }

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      for (int index = 0; index <= 3 - this.recipeWidth; index++) {
         for (int innerIndex = 0; innerIndex <= 3 - this.recipeHeight; innerIndex++) {
            if (this.checkMatch(inv, index, innerIndex, true)) {
               return true;
            }

            if (this.checkMatch(inv, index, innerIndex, false)) {
               return true;
            }
         }
      }

      return false;
   }
}
