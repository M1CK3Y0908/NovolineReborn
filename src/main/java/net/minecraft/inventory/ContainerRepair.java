package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   private int materialCost;
   private IInventory outputSlot = new InventoryCraftResult();
   private IInventory inputSlots = new InventoryBasic("Repair", true, 2) {
      @Override
      public void markDirty() {
         super.markDirty();
         ContainerRepair.this.onCraftMatrixChanged(this);
      }
   };
   private BlockPos selfPosition;
   private static final Logger logger = LogManager.getLogger();
   private String repairedItemName;
   public int maximumCost;
   private World theWorld;
   private final EntityPlayer thePlayer;

   public void updateItemName(String newName) {
      this.repairedItemName = newName;
      if (this.getSlot(2).getHasStack()) {
         ItemStack itemStack = this.getSlot(2).getStack();
         if (StringUtils.isBlank(newName)) {
            itemStack.clearCustomName();
         } else {
            itemStack.setStackDisplayName(this.repairedItemName);
         }
      }

      this.updateRepairOutput();
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.theWorld.isRemote) {
         for (int index = 0; index < this.inputSlots.getSizeInventory(); index++) {
            ItemStack itemStack = this.inputSlots.removeStackFromSlot(index);
            if (itemStack != null) {
               playerIn.getEntityItem3(itemStack);
            }
         }
      }
   }

   public ContainerRepair(InventoryPlayer playerInventory, World worldIn, EntityPlayer player) {
      this(playerInventory, worldIn, BlockPos.ORIGIN, player);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      if (id == 0) {
         this.maximumCost = data;
      }
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 2) {
            if (!this.mergeItemStack(itemStack2, 3, 39, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (index != 0 && index != 1) {
            if (index >= 3 && index < 39 && !this.mergeItemStack(itemStack2, 0, 2, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 3, 39, false)) {
            return null;
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
      return this.theWorld.getBlockState(this.selfPosition).getBlock() != Blocks.anvil
         ? false
         : playerIn.getDistanceSq(this.selfPosition.getX() + 0.5, this.selfPosition.getY() + 0.5, this.selfPosition.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendProgressBarUpdate(this, 0, this.maximumCost);
   }

   public ContainerRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player) {
      this.selfPosition = blockPosIn;
      this.theWorld = worldIn;
      this.thePlayer = player;
      this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlotToContainer(
         new Slot(this.outputSlot, 2, 134, 47) {
            @Override
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
               if (!playerIn.capabilities.isCreativeMode) {
                  playerIn.addExperienceLevel(-ContainerRepair.this.maximumCost);
               }

               ContainerRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);
               if (ContainerRepair.this.materialCost > 0) {
                  ItemStack itemStack = ContainerRepair.this.inputSlots.getStackInSlot(1);
                  if (itemStack != null && itemStack.stackSize > ContainerRepair.this.materialCost) {
                     itemStack.stackSize = itemStack.stackSize - ContainerRepair.this.materialCost;
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, itemStack);
                  } else {
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                  }
               } else {
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
               }

               ContainerRepair.this.maximumCost = 0;
               IBlockState blockState = worldIn.getBlockState(blockPosIn);
               if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && blockState.getBlock() == Blocks.anvil && playerIn.getRNG().nextFloat() < 0.12F
                  )
                {
                  int value = blockState.getValue(BlockAnvil.DAMAGE);
                  if (++value > 2) {
                     worldIn.setBlockToAir(blockPosIn);
                     worldIn.playAuxSFX(1020, blockPosIn, 0);
                  } else {
                     worldIn.setBlockState(blockPosIn, blockState.withProperty(BlockAnvil.DAMAGE, value), 2);
                     worldIn.playAuxSFX(1021, blockPosIn, 0);
                  }
               } else if (!worldIn.isRemote) {
                  worldIn.playAuxSFX(1021, blockPosIn, 0);
               }
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
               return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerRepair.this.maximumCost)
                  && ContainerRepair.this.maximumCost > 0
                  && this.getHasStack();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
               return false;
            }
         }
      );

      for (int index = 0; index < 3; index++) {
         for (int innerIndex = 0; innerIndex < 9; innerIndex++) {
            this.addSlotToContainer(new Slot(playerInventory, innerIndex + index * 9 + 9, 8 + innerIndex * 18, 84 + index * 18));
         }
      }

      for (int nestedIndex = 0; nestedIndex < 9; nestedIndex++) {
         this.addSlotToContainer(new Slot(playerInventory, nestedIndex, 8 + nestedIndex * 18, 142));
      }
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      super.onCraftMatrixChanged(inventoryIn);
      if (inventoryIn == this.inputSlots) {
         this.updateRepairOutput();
      }
   }

   public void updateRepairOutput() {
      ItemStack stackInSlot = this.inputSlots.getStackInSlot(0);
      this.maximumCost = 1;
      int i = 0;
      byte b = 0;
      if (stackInSlot == null) {
         this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
         this.maximumCost = 0;
      } else {
         ItemStack itemStack3 = stackInSlot.copy();
         ItemStack stackInSlot2 = this.inputSlots.getStackInSlot(1);
         Map enchantments = EnchantmentHelper.getEnchantments(itemStack3);
         int i2 = 0 + stackInSlot.getRepairCost() + (stackInSlot2 == null ? 0 : stackInSlot2.getRepairCost());
         this.materialCost = 0;
         if (stackInSlot2 != null) {
            boolean flag = stackInSlot2.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(stackInSlot2).tagCount() > 0;
            if (itemStack3.isItemStackDamageable() && itemStack3.getItem().getIsRepairable(stackInSlot, stackInSlot2)) {
               int i3 = Math.min(itemStack3.getItemDamage(), itemStack3.getMaxDamage() / 4);
               if (i3 <= 0) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               int i4;
               for (i4 = 0; i3 > 0 && i4 < stackInSlot2.stackSize; i4++) {
                  int i5 = itemStack3.getItemDamage() - i3;
                  itemStack3.setItemDamage(i5);
                  i++;
                  i3 = Math.min(itemStack3.getItemDamage(), itemStack3.getMaxDamage() / 4);
               }

               this.materialCost = i4;
            } else {
               if (!flag && (itemStack3.getItem() != stackInSlot2.getItem() || !itemStack3.isItemStackDamageable())) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               if (itemStack3.isItemStackDamageable() && !flag) {
                  int i6 = stackInSlot.getMaxDamage() - stackInSlot.getItemDamage();
                  int i7 = stackInSlot2.getMaxDamage() - stackInSlot2.getItemDamage();
                  int i8 = i7 + itemStack3.getMaxDamage() * 12 / 100;
                  int i9 = i6 + i8;
                  int i10 = itemStack3.getMaxDamage() - i9;
                  if (i10 < 0) {
                     i10 = 0;
                  }

                  if (i10 < itemStack3.getMetadata()) {
                     itemStack3.setItemDamage(i10);
                     i += 2;
                  }
               }

               Map enchantments2 = EnchantmentHelper.getEnchantments(stackInSlot2);

               for (int i11 : (Iterable<Integer>) enchantments2.keySet()) {
                  Enchantment enchantmentById = Enchantment.getEnchantmentById(i11);
                  if (enchantmentById != null) {
                     int i12 = enchantments.containsKey(i11) ? (Integer)enchantments.get(i11) : 0;
                     int i13 = (Integer)enchantments2.get(i11);
                     int i14;
                     if (i12 == i13) {
                        i14 = ++i13;
                     } else {
                        i14 = Math.max(i13, i12);
                     }

                     i13 = i14;
                     boolean apply = enchantmentById.canApply(stackInSlot);
                     if (this.thePlayer.capabilities.isCreativeMode || stackInSlot.getItem() == Items.enchanted_book) {
                        apply = true;
                     }

                     for (int i15 : (Iterable<Integer>) enchantments.keySet()) {
                        if (i15 != i11 && !enchantmentById.canApplyTogether(Enchantment.getEnchantmentById(i15))) {
                           apply = false;
                           i++;
                        }
                     }

                     if (apply) {
                        if (i13 > enchantmentById.getMaxLevel()) {
                           i13 = enchantmentById.getMaxLevel();
                        }

                        enchantments.put(i11, i13);
                        int i16 = 0;
                        switch (enchantmentById.getWeight()) {
                           case 1:
                              i16 = 8;
                              break;
                           case 2:
                              i16 = 4;
                           case 3:
                           case 4:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           default:
                              break;
                           case 5:
                              i16 = 2;
                              break;
                           case 10:
                              i16 = 1;
                        }

                        if (flag) {
                           i16 = Math.max(1, i16 / 2);
                        }

                        i += i16 * i13;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.repairedItemName)) {
            if (stackInSlot.hasDisplayName()) {
               b = 1;
               i++;
               itemStack3.clearCustomName();
            }
         } else if (!this.repairedItemName.equals(stackInSlot.getDisplayName())) {
            b = 1;
            i++;
            itemStack3.setStackDisplayName(this.repairedItemName);
         }

         this.maximumCost = i2 + i;
         if (i <= 0) {
            itemStack3 = null;
         }

         if (b == i && b > 0 && this.maximumCost >= 40) {
            this.maximumCost = 39;
         }

         if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode) {
            itemStack3 = null;
         }

         if (itemStack3 != null) {
            int repairCost = itemStack3.getRepairCost();
            if (stackInSlot2 != null && repairCost < stackInSlot2.getRepairCost()) {
               repairCost = stackInSlot2.getRepairCost();
            }

            repairCost = repairCost * 2 + 1;
            itemStack3.setRepairCost(repairCost);
            EnchantmentHelper.setEnchantments(enchantments, itemStack3);
         }

         this.outputSlot.setInventorySlotContents(0, itemStack3);
         this.detectAndSendChanges();
      }
   }
}
