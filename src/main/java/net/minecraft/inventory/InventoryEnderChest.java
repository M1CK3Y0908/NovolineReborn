package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest extends InventoryBasic {
   private TileEntityEnderChest associatedChest;

   @Override
   public void openInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.openChest();
      }

      super.openInventory(player);
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUseableByPlayer(player);
   }

   public void setChestTileEntity(TileEntityEnderChest chestTileEntity) {
      this.associatedChest = chestTileEntity;
   }

   @Override
   public void closeInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.closeChest();
      }

      super.closeInventory(player);
      this.associatedChest = null;
   }

   public void loadInventoryFromNBT(NBTTagList nbtTagList) {
      for (int index = 0; index < this.getSizeInventory(); index++) {
         this.setInventorySlotContents(index, (ItemStack)null);
      }

      for (int nestedIndex = 0; nestedIndex < nbtTagList.tagCount(); nestedIndex++) {
         NBTTagCompound nBTTagCompound = nbtTagList.getCompoundTagAt(nestedIndex);
         int i = nBTTagCompound.getByte("Slot") & 255;
         if (i >= 0 && i < this.getSizeInventory()) {
            this.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(nBTTagCompound));
         }
      }
   }

   public InventoryEnderChest() {
      super("container.enderchest", false, 27);
   }

   public NBTTagList saveInventoryToNBT() {
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.getSizeInventory(); index++) {
         ItemStack itemStack = this.getStackInSlot(index);
         if (itemStack != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)index);
            itemStack.writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      return nbtTagList;
   }
}
