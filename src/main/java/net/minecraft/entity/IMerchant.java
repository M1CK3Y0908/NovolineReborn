package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public interface IMerchant {
   EntityPlayer getCustomer();

   MerchantRecipeList getRecipes(EntityPlayer entityPlayer);

   IChatComponent getDisplayName();

   void useRecipe(MerchantRecipe merchantRecipe);

   void setRecipes(MerchantRecipeList merchantRecipeList);

   void verifySellingItem(ItemStack itemStack);

   void setCustomer(EntityPlayer entityPlayer);
}
