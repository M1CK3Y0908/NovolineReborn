package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface ICrafting {
   void updateCraftingInventory(Container container, List<ItemStack> list);

   void sendAllWindowProperties(Container container, IInventory iInventory);

   void sendProgressBarUpdate(Container container, int i, int i2);

   void sendSlotContents(Container container, int i, ItemStack itemStack);
}
