package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnaceOutput extends Slot {
   private int amountCrafted;
   private EntityPlayer thePlayer;

   @Override
   protected void onCrafting(ItemStack stack, int amount) {
      this.amountCrafted += amount;
      this.onCrafting(stack);
   }

   public SlotFurnaceOutput(EntityPlayer player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
      super(inventoryIn, slotIndex, xPosition, yPosition);
      this.thePlayer = player;
   }

   @Override
   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.amountCrafted = this.amountCrafted + Math.min(amount, this.getStack().stackSize);
      }

      return super.decrStackSize(amount);
   }

   protected void onCrafting(ItemStack stack) {
      stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
      if (!this.thePlayer.worldObj.isRemote) {
         int amountCrafted2 = this.amountCrafted;
         float smeltingExperience = FurnaceRecipes.instance().getSmeltingExperience(stack);
         if (smeltingExperience == 0.0F) {
            amountCrafted2 = 0;
         } else if (smeltingExperience < 1.0F) {
            int i = MathHelper.floor_float(amountCrafted2 * smeltingExperience);
            if (i < MathHelper.ceiling_float_int(amountCrafted2 * smeltingExperience) && Math.random() < amountCrafted2 * smeltingExperience - i) {
               i++;
            }

            amountCrafted2 = i;
         }

         while (amountCrafted2 > 0) {
            int xpSplit = EntityXPOrb.getXPSplit(amountCrafted2);
            amountCrafted2 -= xpSplit;
            this.thePlayer
               .worldObj
               .spawnEntityInWorld(
                  new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5, this.thePlayer.posZ + 0.5, xpSplit)
               );
         }
      }

      this.amountCrafted = 0;
      if (stack.getItem() == Items.iron_ingot) {
         this.thePlayer.triggerAchievement(AchievementList.acquireIron);
      }

      if (stack.getItem() == Items.cooked_fish) {
         this.thePlayer.triggerAchievement(AchievementList.cookFish);
      }
   }

   @Override
   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   @Override
   public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      this.onCrafting(stack);
      super.onPickupFromSlot(playerIn, stack);
   }
}
