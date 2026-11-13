package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.AchievementList;

public class SlotCrafting extends Slot {
   private int amountCrafted;
   private final EntityPlayer thePlayer;
   private final InventoryCrafting craftMatrix;

   @Override
   public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      this.onCrafting(stack);
      ItemStack[] aitemstack = CraftingManager.getInstance().getRemainingItems(this.craftMatrix, playerIn.worldObj);

      for (int index = 0; index < aitemstack.length; index++) {
         ItemStack itemStack = this.craftMatrix.getStackInSlot(index);
         ItemStack itemStack2 = aitemstack[index];
         if (itemStack != null) {
            this.craftMatrix.decrStackSize(index, 1);
         }

         if (itemStack2 != null) {
            if (this.craftMatrix.getStackInSlot(index) == null) {
               this.craftMatrix.setInventorySlotContents(index, itemStack2);
            } else if (!this.thePlayer.inventory.addItemStackToInventory(itemStack2)) {
               this.thePlayer.getEntityItem3(itemStack2);
            }
         }
      }
   }

   @Override
   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   @Override
   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.amountCrafted = this.amountCrafted + Math.min(amount, this.getStack().stackSize);
      }

      return super.decrStackSize(amount);
   }

   public SlotCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory iInventory, int slotIndex, int xPosition, int yPosition) {
      super(iInventory, slotIndex, xPosition, yPosition);
      this.thePlayer = player;
      this.craftMatrix = craftingInventory;
   }

   @Override
   protected void onCrafting(ItemStack stack, int amount) {
      this.amountCrafted += amount;
      this.onCrafting(stack);
   }

   protected void onCrafting(ItemStack stack) {
      if (this.amountCrafted > 0) {
         stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
      }

      this.amountCrafted = 0;
      if (stack.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
         this.thePlayer.triggerAchievement(AchievementList.buildWorkBench);
      }

      if (stack.getItem() instanceof ItemPickaxe) {
         this.thePlayer.triggerAchievement(AchievementList.buildPickaxe);
      }

      if (stack.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
         this.thePlayer.triggerAchievement(AchievementList.buildFurnace);
      }

      if (stack.getItem() instanceof ItemHoe) {
         this.thePlayer.triggerAchievement(AchievementList.buildHoe);
      }

      if (stack.getItem() == Items.bread) {
         this.thePlayer.triggerAchievement(AchievementList.makeBread);
      }

      if (stack.getItem() == Items.cake) {
         this.thePlayer.triggerAchievement(AchievementList.bakeCake);
      }

      if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe)stack.getItem()).getToolMaterial() != net.minecraft.item.Item.ToolMaterial.WOOD) {
         this.thePlayer.triggerAchievement(AchievementList.buildBetterPickaxe);
      }

      if (stack.getItem() instanceof ItemSword) {
         this.thePlayer.triggerAchievement(AchievementList.buildSword);
      }

      if (stack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
         this.thePlayer.triggerAchievement(AchievementList.enchantments);
      }

      if (stack.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
         this.thePlayer.triggerAchievement(AchievementList.bookcase);
      }

      if (stack.getItem() == Items.golden_apple && stack.getMetadata() == 1) {
         this.thePlayer.triggerAchievement(AchievementList.overpowered);
      }
   }
}
