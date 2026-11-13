package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      playerIn.displayGUIBook(itemStackIn);
      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }

   public static boolean isNBTValid(NBTTagCompound nbt) {
      if (nbt == null) {
         return false;
      }

      if (!nbt.hasKey("pages", 9)) {
         return false;
      }

      NBTTagList nBTTagList = nbt.getTagList("pages", 8);

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         String text = nBTTagList.getStringTagAt(index);
         if (text == null) {
            return false;
         }

         if (text.length() > 32767) {
            return false;
         }
      }

      return true;
   }

   public ItemWritableBook() {
      this.setMaxStackSize(1);
   }
}
