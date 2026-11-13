package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeFireworks implements IRecipe {
   private ItemStack recipeOutput;

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

   @Override
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.recipeOutput.copy();
   }

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      this.recipeOutput = null;
      int i = 0;
      int i2 = 0;
      int i3 = 0;
      int i4 = 0;
      int i5 = 0;
      int i6 = 0;

      for (int i7 = 0; i7 < inv.getSizeInventory(); i7++) {
         ItemStack stackInSlot = inv.getStackInSlot(i7);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() == Items.gunpowder) {
               i2++;
            } else if (stackInSlot.getItem() == Items.firework_charge) {
               i4++;
            } else if (stackInSlot.getItem() == Items.dye) {
               i3++;
            } else if (stackInSlot.getItem() == Items.paper) {
               i++;
            } else if (stackInSlot.getItem() == Items.glowstone_dust) {
               i5++;
            } else if (stackInSlot.getItem() == Items.diamond) {
               i5++;
            } else if (stackInSlot.getItem() == Items.fire_charge) {
               i6++;
            } else if (stackInSlot.getItem() == Items.feather) {
               i6++;
            } else if (stackInSlot.getItem() == Items.gold_nugget) {
               i6++;
            } else {
               if (stackInSlot.getItem() != Items.skull) {
                  return false;
               }

               i6++;
            }
         }
      }

      i5 = i5 + i3 + i6;
      if (i2 > 3 || i > 1) {
         return false;
      }

      if (i2 >= 1 && i == 1 && i5 == 0) {
         this.recipeOutput = new ItemStack(Items.fireworks);
         if (i4 > 0) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            NBTTagList nbtTagList = new NBTTagList();

            for (int i8 = 0; i8 < inv.getSizeInventory(); i8++) {
               ItemStack stackInSlot2 = inv.getStackInSlot(i8);
               if (stackInSlot2 != null && stackInSlot2.getItem() == Items.firework_charge && stackInSlot2.hasTagCompound() && stackInSlot2.getTagCompound().hasKey("Explosion", 10)) {
                  nbtTagList.appendTag(stackInSlot2.getTagCompound().getCompoundTag("Explosion"));
               }
            }

            nbtTagCompound2.setTag("Explosions", nbtTagList);
            nbtTagCompound2.setByte("Flight", (byte)i2);
            nbtTagCompound.setTag("Fireworks", nbtTagCompound2);
            this.recipeOutput.setTagCompound(nbtTagCompound);
         }

         return true;
      } else if (i2 == 1 && i == 0 && i4 == 0 && i3 > 0 && i6 <= 1) {
         this.recipeOutput = new ItemStack(Items.firework_charge);
         NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
         NBTTagCompound nbtTagCompound4 = new NBTTagCompound();
         byte b = 0;
         ArrayList arrayList = Lists.newArrayList();

         for (int i9 = 0; i9 < inv.getSizeInventory(); i9++) {
            ItemStack stackInSlot3 = inv.getStackInSlot(i9);
            if (stackInSlot3 != null) {
               if (stackInSlot3.getItem() == Items.dye) {
                  arrayList.add(ItemDye.dyeColors[stackInSlot3.getMetadata() & 15]);
               } else if (stackInSlot3.getItem() == Items.glowstone_dust) {
                  nbtTagCompound4.setBoolean("Flicker", true);
               } else if (stackInSlot3.getItem() == Items.diamond) {
                  nbtTagCompound4.setBoolean("Trail", true);
               } else if (stackInSlot3.getItem() == Items.fire_charge) {
                  b = 1;
               } else if (stackInSlot3.getItem() == Items.feather) {
                  b = 4;
               } else if (stackInSlot3.getItem() == Items.gold_nugget) {
                  b = 2;
               } else if (stackInSlot3.getItem() == Items.skull) {
                  b = 3;
               }
            }
         }

         int[] iArray = new int[arrayList.size()];

         for (int i10 = 0; i10 < iArray.length; i10++) {
            iArray[i10] = (Integer)arrayList.get(i10);
         }

         nbtTagCompound4.setIntArray("Colors", iArray);
         nbtTagCompound4.setByte("Type", b);
         nbtTagCompound3.setTag("Explosion", nbtTagCompound4);
         this.recipeOutput.setTagCompound(nbtTagCompound3);
         return true;
      } else if (i2 == 0 && i == 0 && i4 == 1 && i3 > 0 && i3 == i5) {
         ArrayList arrayList2 = Lists.newArrayList();

         for (int i11 = 0; i11 < inv.getSizeInventory(); i11++) {
            ItemStack stackInSlot4 = inv.getStackInSlot(i11);
            if (stackInSlot4 != null) {
               if (stackInSlot4.getItem() == Items.dye) {
                  arrayList2.add(ItemDye.dyeColors[stackInSlot4.getMetadata() & 15]);
               } else if (stackInSlot4.getItem() == Items.firework_charge) {
                  this.recipeOutput = stackInSlot4.copy();
                  this.recipeOutput.stackSize = 1;
               }
            }
         }

         int[] iArray2 = new int[arrayList2.size()];

         for (int i12 = 0; i12 < iArray2.length; i12++) {
            iArray2[i12] = (Integer)arrayList2.get(i12);
         }

         if (this.recipeOutput != null && this.recipeOutput.hasTagCompound()) {
            NBTTagCompound compoundTag = this.recipeOutput.getTagCompound().getCompoundTag("Explosion");
            if (compoundTag == null) {
               return false;
            }

            compoundTag.setIntArray("FadeColors", iArray2);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
