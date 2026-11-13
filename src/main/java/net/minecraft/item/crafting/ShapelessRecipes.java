package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
   private final ItemStack recipeOutput;
   private final List<ItemStack> recipeItems;

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      ArrayList arrayList = Lists.newArrayList(this.recipeItems);

      for (int i = 0; i < inv.getHeight(); i++) {
         for (int i2 = 0; i2 < inv.getWidth(); i2++) {
            ItemStack itemStack2 = inv.getStackInRowAndColumn(i2, i);
            if (itemStack2 != null) {
               boolean flag = false;

               for (ItemStack itemStack3 : (Iterable<ItemStack>) arrayList) {
                  if (itemStack2.getItem() == itemStack3.getItem() && (itemStack3.getMetadata() == 32767 || itemStack2.getMetadata() == itemStack3.getMetadata())) {
                     flag = true;
                     arrayList.remove(itemStack3);
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }
      }

      return arrayList.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.recipeOutput.copy();
   }

   public ShapelessRecipes(ItemStack output, List<ItemStack> inputList) {
      this.recipeOutput = output;
      this.recipeItems = inputList;
   }

   @Override
   public int getRecipeSize() {
      return this.recipeItems.size();
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
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
