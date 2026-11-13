package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {
   public IInventory tableInventory = new InventoryBasic("Enchant", true, 2) {
      @Override
      public void markDirty() {
         super.markDirty();
         ContainerEnchantment.this.onCraftMatrixChanged(this);
      }

      @Override
      public int getInventoryStackLimit() {
         return 64;
      }
   };
   private Random rand = new Random();
   public int[] enchantLevels = new int[3];
   public int[] enchantmentIds = new int[]{-1, -1, -1};
   private World worldPointer;
   private BlockPos position;
   public int xpSeed;

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.worldPointer.isRemote) {
         for (int index = 0; index < this.tableInventory.getSizeInventory(); index++) {
            ItemStack itemStack = this.tableInventory.removeStackFromSlot(index);
            if (itemStack != null) {
               playerIn.getEntityItem3(itemStack);
            }
         }
      }
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int index = 0; index < this.crafters.size(); index++) {
         ICrafting crafting = this.crafters.get(index);
         crafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
         crafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
         crafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
         crafting.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
         crafting.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
         crafting.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
         crafting.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
      }
   }

   private List<EnchantmentData> buildEnchantmentList(ItemStack stack, int slotIndex, int enchantLevel) {
      this.rand.setSeed(this.xpSeed + slotIndex);
      List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, stack, enchantLevel);
      if (stack.getItem() == Items.book && list != null && list.size() > 1) {
         list.remove(this.rand.nextInt(list.size()));
      }

      return list;
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      if (inventoryIn == this.tableInventory) {
         ItemStack itemStack = inventoryIn.getStackInSlot(0);
         if (itemStack != null && itemStack.isItemEnchantable()) {
            if (!this.worldPointer.isRemote) {
               int i = 0;

               for (int innerIndex = -1; innerIndex <= 1; innerIndex++) {
                  for (int nestedIndex = -1; nestedIndex <= 1; nestedIndex++) {
                     if ((innerIndex != 0 || nestedIndex != 0)
                        && this.worldPointer.isAirBlock(this.position.add(nestedIndex, 0, innerIndex))
                        && this.worldPointer.isAirBlock(this.position.add(nestedIndex, 1, innerIndex))) {
                        if (this.worldPointer.getBlockState(this.position.add(nestedIndex * 2, 0, innerIndex * 2)).getBlock() == Blocks.bookshelf) {
                           i++;
                        }

                        if (this.worldPointer.getBlockState(this.position.add(nestedIndex * 2, 1, innerIndex * 2)).getBlock() == Blocks.bookshelf) {
                           i++;
                        }

                        if (nestedIndex != 0 && innerIndex != 0) {
                           if (this.worldPointer.getBlockState(this.position.add(nestedIndex * 2, 0, innerIndex)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(nestedIndex * 2, 1, innerIndex)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(nestedIndex, 0, innerIndex * 2)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(nestedIndex, 1, innerIndex * 2)).getBlock() == Blocks.bookshelf) {
                              i++;
                           }
                        }
                     }
                  }
               }

               this.rand.setSeed(this.xpSeed);

               for (int index = 0; index < 3; index++) {
                  this.enchantLevels[index] = EnchantmentHelper.calcItemStackEnchantability(this.rand, index, i, itemStack);
                  this.enchantmentIds[index] = -1;
                  if (this.enchantLevels[index] < index + 1) {
                     this.enchantLevels[index] = 0;
                  }
               }

               for (int innerIndex2 = 0; innerIndex2 < 3; innerIndex2++) {
                  if (this.enchantLevels[innerIndex2] > 0) {
                     List<EnchantmentData> list = this.buildEnchantmentList(itemStack, innerIndex2, this.enchantLevels[innerIndex2]);
                     if (list != null && !list.isEmpty()) {
                        EnchantmentData enchantmentData = list.get(this.rand.nextInt(list.size()));
                        this.enchantmentIds[innerIndex2] = enchantmentData.enchantmentobj.effectId | enchantmentData.enchantmentLevel << 8;
                     }
                  }
               }

               this.detectAndSendChanges();
            }
         } else {
            for (int index2 = 0; index2 < 3; index2++) {
               this.enchantLevels[index2] = 0;
               this.enchantmentIds[index2] = -1;
            }
         }
      }
   }

   public int getLapisAmount() {
      ItemStack itemStack = this.tableInventory.getStackInSlot(1);
      return itemStack == null ? 0 : itemStack.stackSize;
   }

   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn, BlockPos pos) {
      this.worldPointer = worldIn;
      this.position = pos;
      this.xpSeed = playerInv.player.getXPSeed();
      this.addSlotToContainer(new Slot(this.tableInventory, 0, 15, 47) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return true;
         }

         @Override
         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlotToContainer(new Slot(this.tableInventory, 1, 35, 47) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == Items.dye && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.BLUE;
         }
      });

      for (int index = 0; index < 3; index++) {
         for (int innerIndex = 0; innerIndex < 9; innerIndex++) {
            this.addSlotToContainer(new Slot(playerInv, innerIndex + index * 9 + 9, 8 + innerIndex * 18, 84 + index * 18));
         }
      }

      for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
         this.addSlotToContainer(new Slot(playerInv, nestedIndex, 8 + nestedIndex * 18, 142));
      }
   }

   @Override
   public boolean enchantItem(EntityPlayer playerIn, int id) {
      ItemStack itemStack = this.tableInventory.getStackInSlot(0);
      ItemStack itemStack2 = this.tableInventory.getStackInSlot(1);
      int i = id + 1;
      if ((itemStack2 == null || itemStack2.stackSize < i) && !playerIn.capabilities.isCreativeMode) {
         return false;
      }

      if (this.enchantLevels[id] > 0
         && itemStack != null
         && (playerIn.experienceLevel >= i && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.capabilities.isCreativeMode)) {
         if (!this.worldPointer.isRemote) {
            List<EnchantmentData> list = this.buildEnchantmentList(itemStack, id, this.enchantLevels[id]);
            boolean flag = itemStack.getItem() == Items.book;
            if (list != null) {
               playerIn.removeExperienceLevel(i);
               if (flag) {
                  itemStack.setItem(Items.enchanted_book);
               }

               for (int innerIndex = 0; innerIndex < list.size(); innerIndex++) {
                  EnchantmentData enchantmentData = list.get(innerIndex);
                  if (flag) {
                     Items.enchanted_book.addEnchantment(itemStack, enchantmentData);
                  } else {
                     itemStack.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
                  }
               }

               if (!playerIn.capabilities.isCreativeMode) {
                  itemStack2.stackSize -= i;
                  if (itemStack2.stackSize <= 0) {
                     this.tableInventory.setInventorySlotContents(1, (ItemStack)null);
                  }
               }

               playerIn.triggerAchievement(StatList.itemEnchantedStat);
               this.tableInventory.markDirty();
               this.xpSeed = playerIn.getXPSeed();
               this.onCraftMatrixChanged(this.tableInventory);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn) {
      this(playerInv, worldIn, BlockPos.ORIGIN);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemStack2, 2, 38, true)) {
               return null;
            }
         } else if (index == 1) {
            if (!this.mergeItemStack(itemStack2, 2, 38, true)) {
               return null;
            }
         } else if (itemStack2.getItem() == Items.dye && EnumDyeColor.byDyeDamage(itemStack2.getMetadata()) == EnumDyeColor.BLUE) {
            if (!this.mergeItemStack(itemStack2, 1, 2, true)) {
               return null;
            }
         } else {
            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(itemStack2)) {
               return null;
            }

            if (itemStack2.hasTagCompound() && itemStack2.stackSize == 1) {
               this.inventorySlots.get(0).putStack(itemStack2.copy());
               itemStack2.stackSize = 0;
            } else if (itemStack2.stackSize >= 1) {
               this.inventorySlots.get(0).putStack(new ItemStack(itemStack2.getItem(), 1, itemStack2.getMetadata()));
               itemStack2.stackSize--;
            }
         }

         if (itemStack2.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if (itemStack2.stackSize == itemStack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(playerIn, itemStack2);
      }

      return itemStack;
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.worldPointer.getBlockState(this.position).getBlock() != Blocks.enchanting_table
         ? false
         : playerIn.getDistanceSq(this.position.getX() + 0.5, this.position.getY() + 0.5, this.position.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
      listener.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
      listener.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
      listener.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
      listener.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
      listener.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
      listener.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      if (id >= 0 && id <= 2) {
         this.enchantLevels[id] = data;
      } else if (id == 3) {
         this.xpSeed = data;
      } else if (id >= 4 && id <= 6) {
         this.enchantmentIds[id - 4] = data;
      } else {
         super.updateProgressBar(id, data);
      }
   }
}
