package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntityLockable implements IInventory {
   protected String customName;
   private ItemStack[] stacks = new ItemStack[9];
   private static final Random RNG = new Random();

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.dispenser";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerDispenser(playerInventory, this);
   }

   @Override
   public void clear() {
      for (int index = 0; index < this.stacks.length; index++) {
         this.stacks[index] = null;
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.stacks.length; index++) {
         if (this.stacks[index] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)index);
            this.stacks[index].writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Items", nbtTagList);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.stacks[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   @Override
   public int getSizeInventory() {
      return 9;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.stacks[index] != null) {
         ItemStack itemStack = this.stacks[index];
         this.stacks[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public String getGuiID() {
      return "minecraft:dispenser";
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int addItemStack(ItemStack stack) {
      for (int index = 0; index < this.stacks.length; index++) {
         if (this.stacks[index] == null || this.stacks[index].getItem() == null) {
            this.setInventorySlotContents(index, stack);
            return index;
         }
      }

      return -1;
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.stacks[index] != null) {
         if (this.stacks[index].stackSize <= count) {
            ItemStack itemStack = this.stacks[index];
            this.stacks[index] = null;
            this.markDirty();
            return itemStack;
         }

         ItemStack itemStack2 = this.stacks[index].splitStack(count);
         if (this.stacks[index].stackSize == 0) {
            this.stacks[index] = null;
         }

         this.markDirty();
         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList nBTTagList = compound.getTagList("Items", 10);
      this.stacks = new ItemStack[this.getSizeInventory()];

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         int i = nBTTagCompound.getByte("Slot") & 255;
         if (i >= 0 && i < this.stacks.length) {
            this.stacks[i] = ItemStack.loadItemStackFromNBT(nBTTagCompound);
         }
      }

      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }
   }

   public void setCustomName(String customName) {
      this.customName = customName;
   }

   public int getDispenseSlot() {
      int i = -1;
      int i2 = 1;

      for (int nestedIndex = 0; nestedIndex < this.stacks.length; nestedIndex++) {
         if (this.stacks[nestedIndex] != null && RNG.nextInt(i2++) == 0) {
            i = nestedIndex;
         }
      }

      return i;
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.stacks[index];
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }
}
