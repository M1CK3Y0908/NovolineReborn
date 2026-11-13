package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class NpcMerchant implements IMerchant {
   private EntityPlayer customer;
   private IChatComponent displayName;
   private MerchantRecipeList recipeList;
   private InventoryMerchant theMerchantInventory;

   @Override
   public MerchantRecipeList getRecipes(EntityPlayer player) {
      return this.recipeList;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.displayName != null ? this.displayName : new ChatComponentTranslation("entity.Villager.name");
   }

   @Override
   public void setRecipes(MerchantRecipeList recipeList) {
      this.recipeList = recipeList;
   }

   public NpcMerchant(EntityPlayer customerIn, IChatComponent displayNameIn) {
      this.customer = customerIn;
      this.displayName = displayNameIn;
      this.theMerchantInventory = new InventoryMerchant(customerIn, this);
   }

   @Override
   public void verifySellingItem(ItemStack stack) {
   }

   @Override
   public void useRecipe(MerchantRecipe recipe) {
      recipe.incrementToolUses();
   }

   @Override
   public EntityPlayer getCustomer() {
      return this.customer;
   }

   @Override
   public void setCustomer(EntityPlayer customer) {
   }
}
