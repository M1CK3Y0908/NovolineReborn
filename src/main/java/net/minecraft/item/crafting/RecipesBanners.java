package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBanner.EnumBannerPattern;
import net.minecraft.world.World;

public class RecipesBanners {
   void addRecipes(CraftingManager craftingManager) {
      for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
         craftingManager.addRecipe(
            new ItemStack(Items.banner, 1, enumDyeColor.getDyeDamage()),
            "###",
            "###",
            " | ",
            '#',
            new ItemStack(Blocks.wool, 1, enumDyeColor.getMetadata()),
            '|',
            Items.stick
         );
      }

      craftingManager.addRecipe(new RecipesBanners.RecipeDuplicatePattern());
      craftingManager.addRecipe(new RecipesBanners.RecipeAddPattern());
   }

   static class RecipeAddPattern implements IRecipe {
      private EnumBannerPattern findMatchingPattern(InventoryCrafting inventoryCrafting) {
         for (EnumBannerPattern enumBannerPattern : EnumBannerPattern.values()) {
            if (enumBannerPattern.hasValidCrafting()) {
               boolean flag = true;
               if (enumBannerPattern.hasCraftingStack()) {
                  boolean flag2 = false;
                  boolean flag3 = false;

                  for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
                     ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
                     if (stackInSlot != null && stackInSlot.getItem() != Items.banner) {
                        if (stackInSlot.getItem() == Items.dye) {
                           if (flag3) {
                              flag = false;
                              break;
                           }

                           flag3 = true;
                        } else {
                           if (flag2 || !stackInSlot.isItemEqual(enumBannerPattern.getCraftingStack())) {
                              flag = false;
                              break;
                           }

                           flag2 = true;
                        }
                     }
                  }

                  if (!flag2) {
                     flag = false;
                  }
               } else if (inventoryCrafting.getSizeInventory() == enumBannerPattern.getCraftingLayers().length * enumBannerPattern.getCraftingLayers()[0].length()) {
                  int i2 = -1;

                  for (int i3 = 0; i3 < inventoryCrafting.getSizeInventory(); i3++) {
                     int i4 = i3 / 3;
                     int i5 = i3 % 3;
                     ItemStack stackInSlot2 = inventoryCrafting.getStackInSlot(i3);
                     if (stackInSlot2 != null && stackInSlot2.getItem() != Items.banner) {
                        if (stackInSlot2.getItem() != Items.dye) {
                           flag = false;
                           break;
                        }

                        if (i2 != -1 && i2 != stackInSlot2.getMetadata()) {
                           flag = false;
                           break;
                        }

                        if (enumBannerPattern.getCraftingLayers()[i4].charAt(i5) == ' ') {
                           flag = false;
                           break;
                        }

                        i2 = stackInSlot2.getMetadata();
                     } else if (enumBannerPattern.getCraftingLayers()[i4].charAt(i5) != ' ') {
                        flag = false;
                        break;
                     }
                  }
               } else {
                  flag = false;
               }

               if (flag) {
                  return enumBannerPattern;
               }
            }
         }

         return null;
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
      public ItemStack getCraftingResult(InventoryCrafting inv) {
         ItemStack itemStack = null;

         for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack itemStack2 = inv.getStackInSlot(index);
            if (itemStack2 != null && itemStack2.getItem() == Items.banner) {
               itemStack = itemStack2.copy();
               itemStack.stackSize = 1;
               break;
            }
         }

         EnumBannerPattern tileentitybanner$enumbannerpattern = this.findMatchingPattern(inv);
         if (tileentitybanner$enumbannerpattern != null) {
            int i = 0;

            for (int innerIndex = 0; innerIndex < inv.getSizeInventory(); innerIndex++) {
               ItemStack itemStack3 = inv.getStackInSlot(innerIndex);
               if (itemStack3 != null && itemStack3.getItem() == Items.dye) {
                  i = itemStack3.getMetadata();
                  break;
               }
            }

            NBTTagCompound nBTTagCompound = itemStack.getSubCompound("BlockEntityTag", true);
            NBTTagList nBTTagList;
            if (nBTTagCompound.hasKey("Patterns", 9)) {
               nBTTagList = nBTTagCompound.getTagList("Patterns", 10);
            } else {
               nBTTagList = new NBTTagList();
               nBTTagCompound.setTag("Patterns", nBTTagList);
            }

            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setString("Pattern", tileentitybanner$enumbannerpattern.getPatternID());
            nbtTagCompound.setInteger("Color", i);
            nBTTagList.appendTag(nbtTagCompound);
         }

         return itemStack;
      }

      private RecipeAddPattern() {
      }

      @Override
      public boolean matches(InventoryCrafting inv, World worldIn) {
         boolean conditionMet = false;

         for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack itemStack = inv.getStackInSlot(index);
            if (itemStack != null && itemStack.getItem() == Items.banner) {
               if (conditionMet) {
                  return false;
               }

               if (TileEntityBanner.getPatterns(itemStack) >= 6) {
                  return false;
               }

               conditionMet = true;
            }
         }

         return !conditionMet ? false : this.findMatchingPattern(inv) != null;
      }

      @Override
      public int getRecipeSize() {
         return 10;
      }

      @Override
      public ItemStack getRecipeOutput() {
         return null;
      }
   }

   static class RecipeDuplicatePattern implements IRecipe {
      private RecipeDuplicatePattern() {
      }

      @Override
      public ItemStack getCraftingResult(InventoryCrafting inv) {
         for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack itemStack = inv.getStackInSlot(index);
            if (itemStack != null && TileEntityBanner.getPatterns(itemStack) > 0) {
               ItemStack itemStack2 = itemStack.copy();
               itemStack2.stackSize = 1;
               return itemStack2;
            }
         }

         return null;
      }

      @Override
      public boolean matches(InventoryCrafting inv, World worldIn) {
         ItemStack itemStack4 = null;
         ItemStack itemStack5 = null;

         for (int index = 0; index < inv.getSizeInventory(); index++) {
            ItemStack itemStack3 = inv.getStackInSlot(index);
            if (itemStack3 != null) {
               if (itemStack3.getItem() != Items.banner) {
                  return false;
               }

               if (itemStack4 != null && itemStack5 != null) {
                  return false;
               }

               int baseColor = TileEntityBanner.getBaseColor(itemStack3);
               boolean flag = TileEntityBanner.getPatterns(itemStack3) > 0;
               if (itemStack4 != null) {
                  if (flag) {
                     return false;
                  }

                  if (baseColor != TileEntityBanner.getBaseColor(itemStack4)) {
                     return false;
                  }

                  itemStack5 = itemStack3;
               } else if (itemStack5 != null) {
                  if (!flag) {
                     return false;
                  }

                  if (baseColor != TileEntityBanner.getBaseColor(itemStack5)) {
                     return false;
                  }

                  itemStack4 = itemStack3;
               } else if (flag) {
                  itemStack4 = itemStack3;
               } else {
                  itemStack5 = itemStack3;
               }
            }
         }

         return itemStack4 != null && itemStack5 != null;
      }

      @Override
      public ItemStack[] getRemainingItems(InventoryCrafting inv) {
         ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

         for (int index = 0; index < aitemstack.length; index++) {
            ItemStack itemStack = inv.getStackInSlot(index);
            if (itemStack != null) {
               if (itemStack.getItem().hasContainerItem()) {
                  aitemstack[index] = new ItemStack(itemStack.getItem().getContainerItem());
               } else if (itemStack.hasTagCompound() && TileEntityBanner.getPatterns(itemStack) > 0) {
                  aitemstack[index] = itemStack.copy();
                  aitemstack[index].stackSize = 1;
               }
            }
         }

         return aitemstack;
      }

      @Override
      public int getRecipeSize() {
         return 2;
      }

      @Override
      public ItemStack getRecipeOutput() {
         return null;
      }
   }
}
