package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public abstract class Container {
   public List<ItemStack> inventoryItemStacks = Lists.newArrayList();
   public List<Slot> inventorySlots = Lists.newArrayList();
   private int dragMode = -1;
   private short transactionID;
   private int dragEvent;
   public int windowId;
   private final Set<Slot> dragSlots = Sets.newHashSet();
   protected List<ICrafting> crafters = Lists.newArrayList();
   private Set<EntityPlayer> playerList = Sets.newHashSet();

   public boolean getCanCraft(EntityPlayer entityPlayer) {
      return !this.playerList.contains(entityPlayer);
   }

   public abstract boolean canInteractWith(EntityPlayer entityPlayer);

   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.detectAndSendChanges();
   }

   public Slot getSlotFromInventory(IInventory inv, int slotIn) {
      for (int index = 0; index < this.inventorySlots.size(); index++) {
         Slot slot = this.inventorySlots.get(index);
         if (slot.isHere(inv, slotIn)) {
            return slot;
         }
      }

      return null;
   }

   protected Slot addSlotToContainer(Slot slotIn) {
      slotIn.slotNumber = this.inventorySlots.size();
      this.inventorySlots.add(slotIn);
      this.inventoryItemStacks.add(null);
      return slotIn;
   }

   public static int getDragEvent(int i) {
      return i & 3;
   }

   public short getNextTransactionID(InventoryPlayer inventoryPlayer2) {
      this.transactionID++;
      return this.transactionID;
   }

   public List<ItemStack> getInventory() {
      List<ItemStack> list = Lists.newArrayList();

      for (int index = 0; index < this.inventorySlots.size(); index++) {
         list.add(this.inventorySlots.get(index).getStack());
      }

      return list;
   }

   public static boolean isValidDragMode(int dragModeIn, EntityPlayer player) {
      return dragModeIn == 0 ? true : (dragModeIn == 1 ? true : dragModeIn == 2 && player.capabilities.isCreativeMode);
   }

   public void setCanCraft(EntityPlayer entityPlayer, boolean flag) {
      if (flag) {
         this.playerList.remove(entityPlayer);
      } else {
         this.playerList.add(entityPlayer);
      }
   }

   public void onCraftGuiOpened(ICrafting listener) {
      if (this.crafters.contains(listener)) {
         throw new IllegalArgumentException("Listener already listening");
      }

      this.crafters.add(listener);
      listener.updateCraftingInventory(this, this.getInventory());
      this.detectAndSendChanges();
   }

   public Slot getSlot(int slotId) {
      return this.inventorySlots.get(slotId);
   }

   public static int extractDragMode(int i) {
      return i >> 2 & 3;
   }

   protected void handleInt(int i, int i2, EntityPlayer entityPlayer) {
      this.slotClick(i, i2, 1, entityPlayer);
   }

   public void putStackInSlot(int slotID, ItemStack stack) {
      this.getSlot(slotID).putStack(stack);
   }

   public void detectAndSendChanges() {
      for (int i = 0; i < this.inventorySlots.size(); i++) {
         ItemStack stack2 = this.inventorySlots.get(i).getStack();
         ItemStack itemStack = this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(itemStack, stack2)) {
            itemStack = stack2 == null ? null : stack2.copy();
            this.inventoryItemStacks.set(i, itemStack);

            for (int i2 = 0; i2 < this.crafters.size(); i2++) {
               this.crafters.get(i2).sendSlotContents(this, i, itemStack);
            }
         }
      }
   }

   public void updateProgressBar(int id, int data) {
   }

   public boolean enchantItem(EntityPlayer playerIn, int id) {
      return false;
   }

   public void removeCraftingFromCrafters(ICrafting listeners) {
      this.crafters.remove(listeners);
   }

   public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
      ItemStack itemStack = null;
      InventoryPlayer inventory2 = playerIn.inventory;
      if (mode == 5) {
         int dragEvent2 = this.dragEvent;
         this.dragEvent = getDragEvent(clickedButton);
         if ((dragEvent2 != 1 || this.dragEvent != 2) && dragEvent2 != this.dragEvent) {
            this.resetDrag();
         } else if (inventory2.getItemStack() == null) {
            this.resetDrag();
         } else if (this.dragEvent == 0) {
            this.dragMode = extractDragMode(clickedButton);
            if (isValidDragMode(this.dragMode, playerIn)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.resetDrag();
            }
         } else if (this.dragEvent == 1) {
            Slot slot2 = this.inventorySlots.get(slotId);
            if (slot2 != null
               && canAddItemToSlot(slot2, inventory2.getItemStack(), true)
               && slot2.isItemValid(inventory2.getItemStack())
               && inventory2.getItemStack().stackSize > this.dragSlots.size()
               && this.canDragIntoSlot(slot2)) {
               this.dragSlots.add(slot2);
            }
         } else if (this.dragEvent == 2) {
            if (!this.dragSlots.isEmpty()) {
               ItemStack itemStack2 = inventory2.getItemStack().copy();
               int stackSize2 = inventory2.getItemStack().stackSize;

               for (Slot slot3 : this.dragSlots) {
                  if (slot3 != null
                     && canAddItemToSlot(slot3, inventory2.getItemStack(), true)
                     && slot3.isItemValid(inventory2.getItemStack())
                     && inventory2.getItemStack().stackSize >= this.dragSlots.size()
                     && this.canDragIntoSlot(slot3)) {
                     ItemStack itemStack3 = itemStack2.copy();
                     int i = slot3.getHasStack() ? slot3.getStack().stackSize : 0;
                     computeStackSize(this.dragSlots, this.dragMode, itemStack3, i);
                     if (itemStack3.stackSize > itemStack3.getMaxStackSize()) {
                        itemStack3.stackSize = itemStack3.getMaxStackSize();
                     }

                     if (itemStack3.stackSize > slot3.getItemStackLimit(itemStack3)) {
                        itemStack3.stackSize = slot3.getItemStackLimit(itemStack3);
                     }

                     stackSize2 -= itemStack3.stackSize - i;
                     slot3.putStack(itemStack3);
                  }
               }

               itemStack2.stackSize = stackSize2;
               if (itemStack2.stackSize <= 0) {
                  itemStack2 = null;
               }

               inventory2.setItemStack(itemStack2);
            }

            this.resetDrag();
         } else {
            this.resetDrag();
         }
      } else if (this.dragEvent != 0) {
         this.resetDrag();
      } else if ((mode == 0 || mode == 1) && (clickedButton == 0 || clickedButton == 1)) {
         if (slotId == -999) {
            if (inventory2.getItemStack() != null) {
               if (clickedButton == 0) {
                  playerIn.getEntityItem3(inventory2.getItemStack());
                  inventory2.setItemStack((ItemStack)null);
               }

               if (clickedButton == 1) {
                  playerIn.getEntityItem3(inventory2.getItemStack().splitStack(1));
                  if (inventory2.getItemStack().stackSize == 0) {
                     inventory2.setItemStack((ItemStack)null);
                  }
               }
            }
         } else if (mode == 1) {
            if (slotId < 0) {
               return null;
            }

            Slot slot4 = this.inventorySlots.get(slotId);
            if (slot4 != null && slot4.canTakeStack(playerIn)) {
               ItemStack itemStack4 = this.transferStackInSlot(playerIn, slotId);
               if (itemStack4 != null) {
                  Item item2 = itemStack4.getItem();
                  itemStack = itemStack4.copy();
                  if (slot4.getStack() != null && slot4.getStack().getItem() == item2) {
                     this.handleInt(slotId, clickedButton, playerIn);
                  }
               }
            }
         } else {
            if (slotId < 0) {
               return null;
            }

            Slot slot5 = this.inventorySlots.get(slotId);
            if (slot5 != null) {
               ItemStack stack2 = slot5.getStack();
               ItemStack itemStack5 = inventory2.getItemStack();
               if (stack2 != null) {
                  itemStack = stack2.copy();
               }

               if (stack2 == null) {
                  if (itemStack5 != null && slot5.isItemValid(itemStack5)) {
                     int i2 = clickedButton == 0 ? itemStack5.stackSize : 1;
                     if (i2 > slot5.getItemStackLimit(itemStack5)) {
                        i2 = slot5.getItemStackLimit(itemStack5);
                     }

                     if (itemStack5.stackSize >= i2) {
                        slot5.putStack(itemStack5.splitStack(i2));
                     }

                     if (itemStack5.stackSize == 0) {
                        inventory2.setItemStack((ItemStack)null);
                     }
                  }
               } else if (slot5.canTakeStack(playerIn)) {
                  if (itemStack5 == null) {
                     int i3 = clickedButton == 0 ? stack2.stackSize : (stack2.stackSize + 1) / 2;
                     ItemStack itemStack6 = slot5.decrStackSize(i3);
                     inventory2.setItemStack(itemStack6);
                     if (stack2.stackSize == 0) {
                        slot5.putStack((ItemStack)null);
                     }

                     slot5.onPickupFromSlot(playerIn, inventory2.getItemStack());
                  } else if (slot5.isItemValid(itemStack5)) {
                     if (stack2.getItem() == itemStack5.getItem() && stack2.getMetadata() == itemStack5.getMetadata() && ItemStack.areItemStackTagsEqual(stack2, itemStack5)) {
                        int i4 = clickedButton == 0 ? itemStack5.stackSize : 1;
                        if (i4 > slot5.getItemStackLimit(itemStack5) - stack2.stackSize) {
                           i4 = slot5.getItemStackLimit(itemStack5) - stack2.stackSize;
                        }

                        if (i4 > itemStack5.getMaxStackSize() - stack2.stackSize) {
                           i4 = itemStack5.getMaxStackSize() - stack2.stackSize;
                        }

                        itemStack5.splitStack(i4);
                        if (itemStack5.stackSize == 0) {
                           inventory2.setItemStack((ItemStack)null);
                        }

                        stack2.stackSize += i4;
                     } else if (itemStack5.stackSize <= slot5.getItemStackLimit(itemStack5)) {
                        slot5.putStack(itemStack5);
                        inventory2.setItemStack(stack2);
                     }
                  } else if (stack2.getItem() == itemStack5.getItem()
                     && itemStack5.getMaxStackSize() > 1
                     && (!stack2.getHasSubtypes() || stack2.getMetadata() == itemStack5.getMetadata())
                     && ItemStack.areItemStackTagsEqual(stack2, itemStack5)) {
                     int stackSize3 = stack2.stackSize;
                     if (stackSize3 > 0 && stackSize3 + itemStack5.stackSize <= itemStack5.getMaxStackSize()) {
                        itemStack5.stackSize += stackSize3;
                        stack2 = slot5.decrStackSize(stackSize3);
                        if (stack2.stackSize == 0) {
                           slot5.putStack((ItemStack)null);
                        }

                        slot5.onPickupFromSlot(playerIn, inventory2.getItemStack());
                     }
                  }
               }

               slot5.onSlotChanged();
            }
         }
      } else if (mode == 2 && clickedButton >= 0 && clickedButton < 9) {
         Slot slot6 = this.inventorySlots.get(slotId);
         if (slot6.canTakeStack(playerIn)) {
            ItemStack stackInSlot = inventory2.getStackInSlot(clickedButton);
            boolean flag = stackInSlot == null || slot6.inventory == inventory2 && slot6.isItemValid(stackInSlot);
            int i5 = -1;
            if (!flag) {
               i5 = inventory2.getFirstEmptyStack();
               flag |= i5 > -1;
            }

            if (slot6.getHasStack() && flag) {
               ItemStack stack3 = slot6.getStack();
               inventory2.setInventorySlotContents(clickedButton, stack3.copy());
               if ((slot6.inventory != inventory2 || !slot6.isItemValid(stackInSlot)) && stackInSlot != null) {
                  if (i5 > -1) {
                     inventory2.addItemStackToInventory(stackInSlot);
                     slot6.decrStackSize(stack3.stackSize);
                     slot6.putStack((ItemStack)null);
                     slot6.onPickupFromSlot(playerIn, stack3);
                  }
               } else {
                  slot6.decrStackSize(stack3.stackSize);
                  slot6.putStack(stackInSlot);
                  slot6.onPickupFromSlot(playerIn, stack3);
               }
            } else if (!slot6.getHasStack() && stackInSlot != null && slot6.isItemValid(stackInSlot)) {
               inventory2.setInventorySlotContents(clickedButton, (ItemStack)null);
               slot6.putStack(stackInSlot);
            }
         }
      } else if (mode == 3 && playerIn.capabilities.isCreativeMode && inventory2.getItemStack() == null && slotId >= 0) {
         Slot slot7 = this.inventorySlots.get(slotId);
         if (slot7 != null && slot7.getHasStack()) {
            ItemStack itemStack7 = slot7.getStack().copy();
            itemStack7.stackSize = itemStack7.getMaxStackSize();
            inventory2.setItemStack(itemStack7);
         }
      } else if (mode == 4 && inventory2.getItemStack() == null && slotId >= 0) {
         Slot slot8 = this.inventorySlots.get(slotId);
         if (slot8 != null && slot8.getHasStack() && slot8.canTakeStack(playerIn)) {
            ItemStack itemStack8 = slot8.decrStackSize(clickedButton == 0 ? 1 : slot8.getStack().stackSize);
            slot8.onPickupFromSlot(playerIn, itemStack8);
            playerIn.getEntityItem3(itemStack8);
         }
      } else if (mode == 6 && slotId >= 0) {
         Slot slot9 = this.inventorySlots.get(slotId);
         ItemStack itemStack9 = inventory2.getItemStack();
         if (itemStack9 != null && (slot9 == null || !slot9.getHasStack() || !slot9.canTakeStack(playerIn))) {
            int i6 = clickedButton == 0 ? 0 : this.inventorySlots.size() - 1;
            int i7 = clickedButton == 0 ? 1 : -1;

            for (int i8 = 0; i8 < 2; i8++) {
               for (int i9 = i6; i9 >= 0 && i9 < this.inventorySlots.size() && itemStack9.stackSize < itemStack9.getMaxStackSize(); i9 += i7) {
                  Slot slot10 = this.inventorySlots.get(i9);
                  if (slot10.getHasStack()
                     && canAddItemToSlot(slot10, itemStack9, true)
                     && slot10.canTakeStack(playerIn)
                     && this.canMergeSlot(itemStack9, slot10)
                     && (i8 != 0 || slot10.getStack().stackSize != slot10.getStack().getMaxStackSize())) {
                     int i10 = Math.min(itemStack9.getMaxStackSize() - itemStack9.stackSize, slot10.getStack().stackSize);
                     ItemStack itemStack10 = slot10.decrStackSize(i10);
                     itemStack9.stackSize += i10;
                     if (itemStack10.stackSize <= 0) {
                        slot10.putStack((ItemStack)null);
                     }

                     slot10.onPickupFromSlot(playerIn, itemStack10);
                  }
               }
            }
         }

         this.detectAndSendChanges();
      }

      return itemStack;
   }

   protected void resetDrag() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   public static boolean canAddItemToSlot(Slot slotIn, ItemStack stack, boolean stackSizeMatters) {
      boolean conditionMet = slotIn == null || !slotIn.getHasStack();
      if (slotIn != null
         && slotIn.getHasStack()
         && stack != null
         && stack.isItemEqual(slotIn.getStack())
         && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack)) {
         int i = stackSizeMatters ? 0 : stack.stackSize;
         conditionMet |= slotIn.getStack().stackSize + i <= stack.getMaxStackSize();
      }

      return conditionMet;
   }

   public boolean canDragIntoSlot(Slot slot2) {
      return true;
   }

   public void putStacksInSlots(ItemStack[] itemStacks) {
      for (int index = 0; index < itemStacks.length; index++) {
         this.getSlot(index).putStack(itemStacks[index]);
      }
   }

   public static int calcRedstoneFromInventory(IInventory inv) {
      if (inv == null) {
         return 0;
      }

      int i = 0;
      float f = 0.0F;

      for (int i2 = 0; i2 < inv.getSizeInventory(); i2++) {
         ItemStack stackInSlot = inv.getStackInSlot(i2);
         if (stackInSlot != null) {
            f += (float)stackInSlot.stackSize / Math.min(inv.getInventoryStackLimit(), stackInSlot.getMaxStackSize());
            i++;
         }
      }

      f /= inv.getSizeInventory();
      return MathHelper.floor_float(f * 14.0F) + (i > 0 ? 1 : 0);
   }

   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return true;
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      InventoryPlayer inventoryPlayer = playerIn.inventory;
      if (inventoryPlayer.getItemStack() != null) {
         playerIn.getEntityItem3(inventoryPlayer.getItemStack());
         inventoryPlayer.setItemStack((ItemStack)null);
      }
   }

   public static int calcRedstone(TileEntity te) {
      return te instanceof IInventory ? calcRedstoneFromInventory((IInventory)te) : 0;
   }

   protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
      boolean flag = false;
      int startIndex2 = startIndex;
      if (reverseDirection) {
         startIndex2 = endIndex - 1;
      }

      if (stack.isStackable()) {
         while (stack.stackSize > 0 && (!reverseDirection && startIndex2 < endIndex || reverseDirection && startIndex2 >= startIndex)) {
            Slot slot2 = this.inventorySlots.get(startIndex2);
            ItemStack stack2 = slot2.getStack();
            if (stack2 != null
               && stack2.getItem() == stack.getItem()
               && (!stack.getHasSubtypes() || stack.getMetadata() == stack2.getMetadata())
               && ItemStack.areItemStackTagsEqual(stack, stack2)) {
               int i = stack2.stackSize + stack.stackSize;
               if (i <= stack.getMaxStackSize()) {
                  stack.stackSize = 0;
                  stack2.stackSize = i;
                  slot2.onSlotChanged();
                  flag = true;
               } else if (stack2.stackSize < stack.getMaxStackSize()) {
                  stack.stackSize = stack.stackSize - (stack.getMaxStackSize() - stack2.stackSize);
                  stack2.stackSize = stack.getMaxStackSize();
                  slot2.onSlotChanged();
                  flag = true;
               }
            }

            if (reverseDirection) {
               startIndex2--;
            } else {
               startIndex2++;
            }
         }
      }

      if (stack.stackSize > 0) {
         if (reverseDirection) {
            startIndex2 = endIndex - 1;
         } else {
            startIndex2 = startIndex;
         }

         while (!reverseDirection && startIndex2 < endIndex || reverseDirection && startIndex2 >= startIndex) {
            Slot slot3 = this.inventorySlots.get(startIndex2);
            ItemStack stack3 = slot3.getStack();
            if (stack3 == null) {
               slot3.putStack(stack.copy());
               slot3.onSlotChanged();
               stack.stackSize = 0;
               flag = true;
               break;
            }

            if (reverseDirection) {
               startIndex2--;
            } else {
               startIndex2++;
            }
         }
      }

      return flag;
   }

   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      Slot slot = this.inventorySlots.get(index);
      return slot != null ? slot.getStack() : null;
   }

   public static int getQuickcraftMask(int mouseButton, int dragMode) {
      return mouseButton & 3 | (dragMode & 3) << 2;
   }

   public static void computeStackSize(Set<Slot> set2, int i, ItemStack itemStack, int i2) {
      switch (i) {
         case 0:
            itemStack.stackSize = MathHelper.floor_float((float)itemStack.stackSize / set2.size());
            break;
         case 1:
            itemStack.stackSize = 1;
            break;
         case 2:
            itemStack.stackSize = itemStack.getItem().getItemStackLimit();
      }

      itemStack.stackSize += i2;
   }
}
