package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeBookCloning implements IRecipe {
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
            if (itemStack2.getItem() == Items.written_book) {
               if (itemStack3 != null) {
                  return null;
               }

               itemStack3 = itemStack2;
            } else {
               if (itemStack2.getItem() != Items.writable_book) {
                  return null;
               }

               i++;
            }
         }
      }

      if (itemStack3 != null && i >= 1 && ItemEditableBook.getGeneration(itemStack3) < 2) {
         ItemStack itemStack4 = new ItemStack(Items.written_book, i);
         itemStack4.setTagCompound((NBTTagCompound)itemStack3.getTagCompound().copy());
         itemStack4.getTagCompound().setInteger("generation", ItemEditableBook.getGeneration(itemStack3) + 1);
         if (itemStack3.hasDisplayName()) {
            itemStack4.setStackDisplayName(itemStack3.getDisplayName());
         }

         return itemStack4;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting inv) {
      ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

      for (int index = 0; index < aitemstack.length; index++) {
         ItemStack itemStack = inv.getStackInSlot(index);
         if (itemStack != null && itemStack.getItem() instanceof ItemEditableBook) {
            aitemstack[index] = itemStack;
            break;
         }
      }

      return aitemstack;
   }

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      int i = 0;
      ItemStack itemStack3 = null;

      for (int innerIndex = 0; innerIndex < inv.getSizeInventory(); innerIndex++) {
         ItemStack itemStack2 = inv.getStackInSlot(innerIndex);
         if (itemStack2 != null) {
            if (itemStack2.getItem() == Items.written_book) {
               if (itemStack3 != null) {
                  return false;
               }

               itemStack3 = itemStack2;
            } else {
               if (itemStack2.getItem() != Items.writable_book) {
                  return false;
               }

               i++;
            }
         }
      }

      return itemStack3 != null && i > 0;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }
}
