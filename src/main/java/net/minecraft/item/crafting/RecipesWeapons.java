package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesWeapons {
   private String[][] recipePatterns = new String[][]{{"X", "X", "#"}};
   private Object[][] recipeItems = new Object[][]{
      {Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot},
      {Items.wooden_sword, Items.stone_sword, Items.iron_sword, Items.diamond_sword, Items.golden_sword}
   };

   public void addRecipes(CraftingManager craftingManager) {
      for (int index = 0; index < this.recipeItems[0].length; index++) {
         Object object = this.recipeItems[0][index];

         for (int innerIndex = 0; innerIndex < this.recipeItems.length - 1; innerIndex++) {
            Item item = (Item)this.recipeItems[innerIndex + 1][index];
            craftingManager.addRecipe(new ItemStack(item), this.recipePatterns[innerIndex], '#', Items.stick, 'X', object);
         }
      }

      craftingManager.addRecipe(new ItemStack(Items.bow, 1), " #X", "# X", " #X", 'X', Items.string, '#', Items.stick);
      craftingManager.addRecipe(new ItemStack(Items.arrow, 4), "X", "#", "Y", 'Y', Items.feather, 'X', Items.flint, '#', Items.stick);
   }
}
