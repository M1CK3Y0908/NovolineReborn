package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface ISidedInventory extends IInventory {
   boolean canInsertItem(int i, ItemStack itemStack, EnumFacing enumFacing);

   int[] getSlotsForFace(EnumFacing enumFacing);

   boolean canExtractItem(int i, ItemStack itemStack, EnumFacing enumFacing);
}
