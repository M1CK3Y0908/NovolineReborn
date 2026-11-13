package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeRepairItem implements IRecipe {
   @Override
   public int getRecipeSize() {
      return 4;
   }

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting inv) {
      ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

      for (int index = 0; index < aitemstack.length; index++) {
         ItemStack itemStack = inv.getStackInSlot(index);
         if (itemStack != null && itemStack.getItem().hasContainerItem()) {
            aitemstack[index] = new ItemStack(itemStack.getItem().getContainerItem());
         }
      }

      return aitemstack;
   }

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      List<ItemStack> list = Lists.newArrayList();

      for (int index = 0; index < inv.getSizeInventory(); index++) {
         ItemStack itemStack = inv.getStackInSlot(index);
         if (itemStack != null) {
            list.add(itemStack);
            if (list.size() > 1) {
               ItemStack itemStack2 = list.get(0);
               if (itemStack.getItem() != itemStack2.getItem() || itemStack2.stackSize != 1 || itemStack.stackSize != 1 || !itemStack2.getItem().isDamageable()
                  )
                {
                  return false;
               }
            }
         }
      }

      return list.size() == 2;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      List<ItemStack> list = Lists.newArrayList();

      for (int index = 0; index < inv.getSizeInventory(); index++) {
         ItemStack itemStack = inv.getStackInSlot(index);
         if (itemStack != null) {
            list.add(itemStack);
            if (list.size() > 1) {
               ItemStack itemStack2 = list.get(0);
               if (itemStack.getItem() != itemStack2.getItem() || itemStack2.stackSize != 1 || itemStack.stackSize != 1 || !itemStack2.getItem().isDamageable()
                  )
                {
                  return null;
               }
            }
         }
      }

      if (list.size() == 2) {
         ItemStack itemStack3 = list.get(0);
         ItemStack itemStack4 = list.get(1);
         if (itemStack3.getItem() == itemStack4.getItem() && itemStack3.stackSize == 1 && itemStack4.stackSize == 1 && itemStack3.getItem().isDamageable()) {
            Item item = itemStack3.getItem();
            int i = item.getMaxDamage() - itemStack3.getItemDamage();
            int i2 = item.getMaxDamage() - itemStack4.getItemDamage();
            int i3 = i + i2 + item.getMaxDamage() * 5 / 100;
            int i4 = item.getMaxDamage() - i3;
            if (i4 < 0) {
               i4 = 0;
            }

            return new ItemStack(itemStack3.getItem(), 1, i4);
         }
      }

      return null;
   }
}
