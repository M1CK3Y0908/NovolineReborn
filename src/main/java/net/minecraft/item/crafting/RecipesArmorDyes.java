package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesArmorDyes implements IRecipe {
   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      ItemStack itemStack2 = null;
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() instanceof ItemArmor) {
               if (((ItemArmor)stackInSlot.getItem()).getArmorMaterial() != net.minecraft.item.ItemArmor.ArmorMaterial.LEATHER || itemStack2 != null) {
                  return false;
               }

               itemStack2 = stackInSlot;
            } else {
               if (stackInSlot.getItem() != Items.dye) {
                  return false;
               }

               arrayList.add(stackInSlot);
            }
         }
      }

      return itemStack2 != null && !arrayList.isEmpty();
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack itemStack2 = null;
      int[] iArray = new int[3];
      int i = 0;
      int i2 = 0;
      ItemArmor itemArmor = null;

      for (int i3 = 0; i3 < inv.getSizeInventory(); i3++) {
         ItemStack stackInSlot = inv.getStackInSlot(i3);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() instanceof ItemArmor) {
               itemArmor = (ItemArmor)stackInSlot.getItem();
               if (itemArmor.getArmorMaterial() != net.minecraft.item.ItemArmor.ArmorMaterial.LEATHER || itemStack2 != null) {
                  return null;
               }

               itemStack2 = stackInSlot.copy();
               itemStack2.stackSize = 1;
               if (itemArmor.hasColor(stackInSlot)) {
                  int color = itemArmor.getColor(itemStack2);
                  float f = (color >> 16 & 0xFF) / 255.0F;
                  float f2 = (color >> 8 & 0xFF) / 255.0F;
                  float f3 = (color & 0xFF) / 255.0F;
                  i = (int)(i + Math.max(f, Math.max(f2, f3)) * 255.0F);
                  iArray[0] = (int)(iArray[0] + f * 255.0F);
                  iArray[1] = (int)(iArray[1] + f2 * 255.0F);
                  iArray[2] = (int)(iArray[2] + f3 * 255.0F);
                  i2++;
               }
            } else {
               if (stackInSlot.getItem() != Items.dye) {
                  return null;
               }

               float[] dyeRgb = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(stackInSlot.getMetadata()));
               int i4 = (int)(dyeRgb[0] * 255.0F);
               int i5 = (int)(dyeRgb[1] * 255.0F);
               int i6 = (int)(dyeRgb[2] * 255.0F);
               i += Math.max(i4, Math.max(i5, i6));
               iArray[0] += i4;
               iArray[1] += i5;
               iArray[2] += i6;
               i2++;
            }
         }
      }

      if (itemArmor == null) {
         return null;
      }

      int i7 = iArray[0] / i2;
      int i8 = iArray[1] / i2;
      int i9 = iArray[2] / i2;
      float f4 = (float)i / i2;
      float f5 = Math.max(i7, Math.max(i8, i9));
      i7 = (int)(i7 * f4 / f5);
      i8 = (int)(i8 * f4 / f5);
      i9 = (int)(i9 * f4 / f5);
      int i10 = (i7 << 8) + i8;
      i10 = (i10 << 8) + i9;
      itemArmor.setColor(itemStack2, i10);
      return itemStack2;
   }

   @Override
   public int getRecipeSize() {
      return 10;
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
}
