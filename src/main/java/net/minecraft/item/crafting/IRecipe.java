package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRecipe {
   ItemStack[] getRemainingItems(InventoryCrafting inventoryCrafting);

   ItemStack getCraftingResult(InventoryCrafting inventoryCrafting);

   int getRecipeSize();

   ItemStack getRecipeOutput();

   boolean matches(InventoryCrafting inventoryCrafting, World world2);
}
