package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.player.AutoTool;

public class InventoryPlayer implements IInventory {
   private ItemStack itemStack;
   public boolean inventoryChanged;
   public ItemStack[] mainInventory = new ItemStack[36];
   public ItemStack[] armorInventory = new ItemStack[4];
   public int currentItem;
   public EntityPlayer player;

   public void readFromNBT(NBTTagList nbtTagListIn) {
      this.mainInventory = new ItemStack[36];
      this.armorInventory = new ItemStack[4];

      for (int index = 0; index < nbtTagListIn.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nbtTagListIn.getCompoundTagAt(index);
         int i = nBTTagCompound.getByte("Slot") & 255;
         ItemStack itemStack = ItemStack.loadItemStackFromNBT(nBTTagCompound);
         if (itemStack != null) {
            if (i >= 0 && i < this.mainInventory.length) {
               this.mainInventory[i] = itemStack;
            }

            if (i >= 100 && i < this.armorInventory.length + 100) {
               this.armorInventory[i - 100] = itemStack;
            }
         }
      }
   }

   public boolean hasItem(Item itemIn) {
      return this.getInventorySlotContainItem(itemIn) >= 0;
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getFirstEmptyStack() {
      for (int index = 0; index < this.mainInventory.length; index++) {
         if (this.mainInventory[index] == null) {
            return index;
         }
      }

      return -1;
   }

   public void changeCurrentItem(int direction) {
      if (direction > 0) {
         direction = 1;
      }

      if (direction < 0) {
         direction = -1;
      }

      this.currentItem -= direction;

      while (this.currentItem < 0) {
         this.currentItem += 9;
      }

      while (this.currentItem >= 9) {
         this.currentItem -= 9;
      }
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0;
   }

   public void setItemStack(ItemStack itemStackIn) {
      this.itemStack = itemStackIn;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      ItemStack[] mainInventory2 = this.mainInventory;
      if (index >= mainInventory2.length) {
         index -= mainInventory2.length;
         mainInventory2 = this.armorInventory;
      }

      return mainInventory2[index];
   }

   private int storeItemStack(ItemStack itemStackIn) {
      for (int index = 0; index < this.mainInventory.length; index++) {
         if (this.mainInventory[index] != null
            && this.mainInventory[index].getItem() == itemStackIn.getItem()
            && this.mainInventory[index].isStackable()
            && this.mainInventory[index].stackSize < this.mainInventory[index].getMaxStackSize()
            && this.mainInventory[index].stackSize < this.getInventoryStackLimit()
            && (!this.mainInventory[index].getHasSubtypes() || this.mainInventory[index].getMetadata() == itemStackIn.getMetadata())
            && ItemStack.areItemStackTagsEqual(this.mainInventory[index], itemStackIn)) {
            return index;
         }
      }

      return -1;
   }

   public void setCurrentItem(Item itemIn, int metadataIn, boolean isMetaSpecific, boolean flag) {
      ItemStack currentItem2 = this.getCurrentItem();
      int i = isMetaSpecific ? this.getInventorySlotContainItemAndDamage(itemIn, metadataIn) : this.getInventorySlotContainItem(itemIn);
      if (i >= 0 && i < 9) {
         this.currentItem = i;
      } else if (flag && itemIn != null) {
         int firstEmptyStack = this.getFirstEmptyStack();
         if (firstEmptyStack >= 0 && firstEmptyStack < 9) {
            this.currentItem = firstEmptyStack;
         }

         if (currentItem2 == null
            || !currentItem2.isItemEnchantable()
            || this.getInventorySlotContainItemAndDamage(currentItem2.getItem(), currentItem2.getItemDamage()) != this.currentItem) {
            int inventorySlotContainItemAndDamage = this.getInventorySlotContainItemAndDamage(itemIn, metadataIn);
            int i2;
            if (inventorySlotContainItemAndDamage >= 0) {
               i2 = this.mainInventory[inventorySlotContainItemAndDamage].stackSize;
               this.mainInventory[inventorySlotContainItemAndDamage] = this.mainInventory[this.currentItem];
            } else {
               i2 = 1;
            }

            this.mainInventory[this.currentItem] = new ItemStack(itemIn, i2, metadataIn);
         }
      }
   }

   public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
      for (int index = 0; index < this.mainInventory.length; index++) {
         if (this.mainInventory[index] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)index);
            this.mainInventory[index].writeToNBT(nbtTagCompound);
            nbtTagListIn.appendTag(nbtTagCompound);
         }
      }

      for (int innerIndex = 0; innerIndex < this.armorInventory.length; innerIndex++) {
         if (this.armorInventory[innerIndex] != null) {
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            nbtTagCompound2.setByte("Slot", (byte)(innerIndex + 100));
            this.armorInventory[innerIndex].writeToNBT(nbtTagCompound2);
            nbtTagListIn.appendTag(nbtTagCompound2);
         }
      }

      return nbtTagListIn;
   }

   private int getInventorySlotContainItemAndDamage(Item itemIn, int metadataIn) {
      for (int index = 0; index < this.mainInventory.length; index++) {
         if (this.mainInventory[index] != null && this.mainInventory[index].getItem() == itemIn && this.mainInventory[index].getMetadata() == metadataIn) {
            return index;
         }
      }

      return -1;
   }

   public boolean canHeldItemHarvest(Block blockIn) {
      AutoTool autoTool2 = ModuleManager.autoTool;
      if (blockIn.getMaterial().isToolNotRequired()) {
         return true;
      }

      int currentItem2 = this.currentItem;
      if (autoTool2.isEnabled() && autoTool2.toolSlot != -1) {
         this.currentItem = autoTool2.toolSlot;
      }

      ItemStack stackInSlot = this.getStackInSlot(this.currentItem);
      boolean flag = stackInSlot != null && stackInSlot.canHarvestBlock(blockIn);
      this.currentItem = currentItem2;
      return flag;
   }

   private int storePartialItemStack(ItemStack itemStackIn) {
      Item item2 = itemStackIn.getItem();
      int stackSize2 = itemStackIn.stackSize;
      int i = this.storeItemStack(itemStackIn);
      if (i < 0) {
         i = this.getFirstEmptyStack();
      }

      if (i < 0) {
         return stackSize2;
      }

      if (this.mainInventory[i] == null) {
         this.mainInventory[i] = new ItemStack(item2, 0, itemStackIn.getMetadata());
         if (itemStackIn.hasTagCompound()) {
            this.mainInventory[i].setTagCompound((NBTTagCompound)itemStackIn.getTagCompound().copy());
         }
      }

      int i2 = stackSize2;
      if (stackSize2 > this.mainInventory[i].getMaxStackSize() - this.mainInventory[i].stackSize) {
         i2 = this.mainInventory[i].getMaxStackSize() - this.mainInventory[i].stackSize;
      }

      if (i2 > this.getInventoryStackLimit() - this.mainInventory[i].stackSize) {
         i2 = this.getInventoryStackLimit() - this.mainInventory[i].stackSize;
      }

      if (i2 == 0) {
         return stackSize2;
      }

      stackSize2 -= i2;
      this.mainInventory[i].stackSize += i2;
      this.mainInventory[i].animationsToGo = 5;
      return stackSize2;
   }

   public InventoryPlayer(EntityPlayer playerIn) {
      this.player = playerIn;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   public ItemStack armorItemInSlot(int slotIn) {
      return this.armorInventory[slotIn];
   }

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
   }

   public ItemStack getCurrentItem() {
      return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void clear() {
      for (int index = 0; index < this.mainInventory.length; index++) {
         this.mainInventory[index] = null;
      }

      for (int innerIndex = 0; innerIndex < this.armorInventory.length; innerIndex++) {
         this.armorInventory[innerIndex] = null;
      }
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      ItemStack[] mainInventory2 = this.mainInventory;
      if (index >= mainInventory2.length) {
         index -= mainInventory2.length;
         mainInventory2 = this.armorInventory;
      }

      mainInventory2[index] = stack;
   }

   public float getStrVsBlock(Block blockIn) {
      float f = 1.0F;
      if (this.mainInventory[this.currentItem] != null) {
         f *= this.mainInventory[this.currentItem].getStrVsBlock(blockIn);
      }

      return f;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      ItemStack[] aitemstack = this.mainInventory;
      if (index >= this.mainInventory.length) {
         aitemstack = this.armorInventory;
         index -= this.mainInventory.length;
      }

      if (aitemstack[index] != null) {
         ItemStack itemStack2 = aitemstack[index];
         aitemstack[index] = null;
         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      ItemStack[] mainInventory2 = this.mainInventory;
      if (index >= this.mainInventory.length) {
         mainInventory2 = this.armorInventory;
         index -= this.mainInventory.length;
      }

      if (mainInventory2[index] != null) {
         if (mainInventory2[index].stackSize <= count) {
            ItemStack itemStack2 = mainInventory2[index];
            mainInventory2[index] = null;
            return itemStack2;
         }

         ItemStack itemStack3 = mainInventory2[index].splitStack(count);
         if (mainInventory2[index].stackSize == 0) {
            mainInventory2[index] = null;
         }

         return itemStack3;
      } else {
         return null;
      }
   }

   public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT) {
      int i = 0;

      for (int innerIndex = 0; innerIndex < this.mainInventory.length; innerIndex++) {
         ItemStack itemStack2 = this.mainInventory[innerIndex];
         if (itemStack2 != null
            && (itemIn == null || itemStack2.getItem() == itemIn)
            && (metadataIn <= -1 || itemStack2.getMetadata() == metadataIn)
            && (itemNBT == null || NBTUtil.areNBTEquals(itemNBT, itemStack2.getTagCompound(), true))) {
            int i2 = removeCount <= 0 ? itemStack2.stackSize : Math.min(removeCount - i, itemStack2.stackSize);
            i += i2;
            if (removeCount != 0) {
               this.mainInventory[innerIndex].stackSize -= i2;
               if (this.mainInventory[innerIndex].stackSize == 0) {
                  this.mainInventory[innerIndex] = null;
               }

               if (removeCount > 0 && i >= removeCount) {
                  return i;
               }
            }
         }
      }

      for (int outerIndex = 0; outerIndex < this.armorInventory.length; outerIndex++) {
         ItemStack itemStack3 = this.armorInventory[outerIndex];
         if (itemStack3 != null
            && (itemIn == null || itemStack3.getItem() == itemIn)
            && (metadataIn <= -1 || itemStack3.getMetadata() == metadataIn)
            && (itemNBT == null || NBTUtil.areNBTEquals(itemNBT, itemStack3.getTagCompound(), false))) {
            int i3 = removeCount <= 0 ? itemStack3.stackSize : Math.min(removeCount - i, itemStack3.stackSize);
            i += i3;
            if (removeCount != 0) {
               this.armorInventory[outerIndex].stackSize -= i3;
               if (this.armorInventory[outerIndex].stackSize == 0) {
                  this.armorInventory[outerIndex] = null;
               }

               if (removeCount > 0 && i >= removeCount) {
                  return i;
               }
            }
         }
      }

      if (this.itemStack != null) {
         if (itemIn != null && this.itemStack.getItem() != itemIn) {
            return i;
         }

         if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
            return i;
         }

         if (itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, this.itemStack.getTagCompound(), false)) {
            return i;
         }

         int i4 = removeCount <= 0 ? this.itemStack.stackSize : Math.min(removeCount - i, this.itemStack.stackSize);
         i += i4;
         if (removeCount != 0) {
            this.itemStack.stackSize -= i4;
            if (this.itemStack.stackSize == 0) {
               this.itemStack = null;
            }

            if (removeCount > 0 && i >= removeCount) {
               return i;
            }
         }
      }

      return i;
   }

   public boolean hasItemStack(ItemStack itemStackIn) {
      for (int index = 0; index < this.armorInventory.length; index++) {
         if (this.armorInventory[index] != null && this.armorInventory[index].isItemEqual(itemStackIn)) {
            return true;
         }
      }

      for (int innerIndex = 0; innerIndex < this.mainInventory.length; innerIndex++) {
         if (this.mainInventory[innerIndex] != null && this.mainInventory[innerIndex].isItemEqual(itemStackIn)) {
            return true;
         }
      }

      return false;
   }

   public int getTotalArmorValue() {
      int i = 0;

      for (int innerIndex = 0; innerIndex < this.armorInventory.length; innerIndex++) {
         if (this.armorInventory[innerIndex] != null && this.armorInventory[innerIndex].getItem() instanceof ItemArmor) {
            int damageReduceAmount2 = ((ItemArmor)this.armorInventory[innerIndex].getItem()).damageReduceAmount;
            i += damageReduceAmount2;
         }
      }

      return i;
   }

   private int getInventorySlotContainItem(Item itemIn) {
      for (int index = 0; index < this.mainInventory.length; index++) {
         if (this.mainInventory[index] != null && this.mainInventory[index].getItem() == itemIn) {
            return index;
         }
      }

      return -1;
   }

   public void damageArmor(float damage) {
      damage /= 4.0F;
      if (damage < 1.0F) {
         damage = 1.0F;
      }

      for (int index = 0; index < this.armorInventory.length; index++) {
         if (this.armorInventory[index] != null && this.armorInventory[index].getItem() instanceof ItemArmor) {
            this.armorInventory[index].damageItem((int)damage, this.player);
            if (this.armorInventory[index].stackSize == 0) {
               this.armorInventory[index] = null;
            }
         }
      }
   }

   @Override
   public void markDirty() {
      this.inventoryChanged = true;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public void decrementAnimations() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            ItemStack itemStack2 = this.mainInventory[i];
            boolean flag = this.currentItem == i;
            itemStack2.handleWorld(this.player.worldObj, this.player, flag);
         }
      }
   }

   public void dropAllItems() {
      for (int index = 0; index < this.mainInventory.length; index++) {
         if (this.mainInventory[index] != null) {
            this.player.dropItem(this.mainInventory[index], true, false);
            this.mainInventory[index] = null;
         }
      }

      for (int innerIndex = 0; innerIndex < this.armorInventory.length; innerIndex++) {
         if (this.armorInventory[innerIndex] != null) {
            this.player.dropItem(this.armorInventory[innerIndex], true, false);
            this.armorInventory[innerIndex] = null;
         }
      }
   }

   public static int getHotbarSize() {
      return 9;
   }

   @Override
   public int getSizeInventory() {
      return this.mainInventory.length + 4;
   }

   public boolean consumeInventoryItem(Item itemIn) {
      int inventorySlotContainItem = this.getInventorySlotContainItem(itemIn);
      if (inventorySlotContainItem < 0) {
         return false;
      }

      if (--this.mainInventory[inventorySlotContainItem].stackSize <= 0) {
         this.mainInventory[inventorySlotContainItem] = null;
      }

      return true;
   }

   public void copyInventory(InventoryPlayer playerInventory) {
      for (int index = 0; index < this.mainInventory.length; index++) {
         this.mainInventory[index] = ItemStack.copyItemStack(playerInventory.mainInventory[index]);
      }

      for (int innerIndex = 0; innerIndex < this.armorInventory.length; innerIndex++) {
         this.armorInventory[innerIndex] = ItemStack.copyItemStack(playerInventory.armorInventory[innerIndex]);
      }

      this.currentItem = playerInventory.currentItem;
   }

   public boolean addItemStackToInventory(final ItemStack itemStackIn) {
      if (itemStackIn != null && itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
         try {
            if (itemStackIn.isItemDamaged()) {
               int firstEmptyStack = this.getFirstEmptyStack();
               if (firstEmptyStack >= 0) {
                  this.mainInventory[firstEmptyStack] = ItemStack.copyItemStack(itemStackIn);
                  this.mainInventory[firstEmptyStack].animationsToGo = 5;
                  itemStackIn.stackSize = 0;
                  return true;
               } else if (this.player.capabilities.isCreativeMode) {
                  itemStackIn.stackSize = 0;
                  return true;
               } else {
                  return false;
               }
            } else {
               int i;
               do {
                  i = itemStackIn.stackSize;
                  itemStackIn.stackSize = this.storePartialItemStack(itemStackIn);
               } while (itemStackIn.stackSize > 0 && itemStackIn.stackSize < i);

               if (itemStackIn.stackSize == i && this.player.capabilities.isCreativeMode) {
                  itemStackIn.stackSize = 0;
                  return true;
               } else {
                  return itemStackIn.stackSize < i;
               }
            }
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Item being added");
            crashReportCategory.addCrashSection("Item ID", Item.getIdFromItem(itemStackIn.getItem()));
            crashReportCategory.addCrashSection("Item data", itemStackIn.getMetadata());
            crashReportCategory.addCrashSectionCallable("Item name", new Callable<String>() {
               public String call() throws Exception {
                  return itemStackIn.getDisplayName();
               }
            });
            throw new ReportedException(crashReport);
         }
      } else {
         return false;
      }
   }

   @Override
   public String getName() {
      return "container.inventory";
   }
}
