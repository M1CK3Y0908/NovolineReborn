package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe {
   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      int i = 0;
      ItemStack itemStack3 = null;

      for (int innerIndex = 0; innerIndex < inv.getSizeInventory(); innerIndex++) {
         ItemStack itemStack2 = inv.getStackInSlot(innerIndex);
         if (itemStack2 != null) {
            if (itemStack2.getItem() == Items.filled_map) {
               if (itemStack3 != null) {
                  return false;
               }

               itemStack3 = itemStack2;
            } else {
               if (itemStack2.getItem() != Items.map) {
                  return false;
               }

               i++;
            }
         }
      }

      return itemStack3 != null && i > 0;
   }

   @Override
   public int getRecipeSize() {
      return 9;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      int i = 0;
      ItemStack itemStack3 = null;

      for (int innerIndex = 0; innerIndex < inv.getSizeInventory(); innerIndex++) {
         ItemStack itemStack2 = inv.getStackInSlot(innerIndex);
         if (itemStack2 != null) {
            if (itemStack2.getItem() == Items.filled_map) {
               if (itemStack3 != null) {
                  return null;
               }

               itemStack3 = itemStack2;
            } else {
               if (itemStack2.getItem() != Items.map) {
                  return null;
               }

               i++;
            }
         }
      }

      if (itemStack3 != null && i >= 1) {
         ItemStack itemStack4 = new ItemStack(Items.filled_map, i + 1, itemStack3.getMetadata());
         if (itemStack3.hasDisplayName()) {
            itemStack4.setStackDisplayName(itemStack3.getDisplayName());
         }

         return itemStack4;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }

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
}
