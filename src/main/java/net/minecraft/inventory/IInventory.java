package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorldNameable;

public interface IInventory extends IWorldNameable {
   int getInventoryStackLimit();

   void setField(int i, int i2);

   void closeInventory(EntityPlayer entityPlayer);

   void markDirty();

   ItemStack decrStackSize(int i, int i2);

   ItemStack removeStackFromSlot(int i);

   boolean isUseableByPlayer(EntityPlayer entityPlayer);

   void openInventory(EntityPlayer entityPlayer);

   int getField(int i);

   int getSizeInventory();

   int getFieldCount();

   ItemStack getStackInSlot(int i);

   void setInventorySlotContents(int i, ItemStack itemStack);

   boolean isItemValidForSlot(int i, ItemStack itemStack);

   void clear();
}
